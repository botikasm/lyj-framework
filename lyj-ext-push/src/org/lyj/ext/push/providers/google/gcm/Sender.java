/*
 *  Copyright Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Original Source from:
 * https://github.com/google/gcm
 *
 */
package org.lyj.ext.push.providers.google.gcm;


import org.json.JSONArray;
import org.json.JSONObject;
import org.lyj.commons.lang.CharEncoding;
import org.lyj.commons.logging.Level;
import org.lyj.commons.logging.Logger;
import org.lyj.commons.logging.util.LoggingUtils;
import org.lyj.commons.util.json.JsonWrapper;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * Helper class to send messages to the GCM service using an API Key.
 */
public class Sender
        implements IConstants {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String UTF8 = CharEncoding.UTF_8;

    /**
     * Initial delay before first retry, without jitter.
     */
    private static final int BACKOFF_INITIAL_DELAY = 1000;
    /**
     * Maximum delay before a retry.
     */
    private static final int MAX_BACKOFF_DELAY = 1024000;


    private static final Logger logger = LoggingUtils.getLogger(Sender.class);

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Random _random = new Random();
    private final String _key;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    /**
     * Default constructor.
     *
     * @param key API _key obtained through the Google API Console.
     */
    public Sender(String key) {
        this._key = nonNull(key);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    /**
     * Sends a message to one device, retrying in case of unavailability.
     * <p>
     * <p>
     * <strong>Note: </strong> this method uses exponential back-off to retry in
     * case of service unavailability and hence could block the calling thread
     * for many seconds.
     *
     * @param message message to be sent, including the device's registration id.
     * @param to      registration token, notification _key, or topic where the message will be sent.
     * @param retries number of retries in case of service unavailability errors.
     * @return result of the request (see its javadoc for more details).
     * @throws IllegalArgumentException if to is {@literal null}.
     * @throws InvalidRequestException  if GCM didn't returned a 200 or 5xx status.
     * @throws IOException              if message could not be sent.
     */
    public Result send(final Message message, final String to, int retries)
            throws IOException {
        int attempt = 0;
        Result result;
        int backoff = BACKOFF_INITIAL_DELAY;
        boolean tryAgain;
        do {
            attempt++;
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Attempt #" + attempt + " to send message " +
                        message + " to regIds " + to);
            }
            result = sendNoRetry(message, to);
            tryAgain = result == null && attempt <= retries;
            if (tryAgain) {
                int sleepTime = backoff / 2 + _random.nextInt(backoff);
                sleep(sleepTime);
                if (2 * backoff < MAX_BACKOFF_DELAY) {
                    backoff *= 2;
                }
            }
        } while (tryAgain);
        if (result == null) {
            throw new IOException("Could not send message after " + attempt +
                    " attempts");
        }
        return result;
    }

    /**
     * Sends a message without retrying in case of service unavailability. See
     * {@link #send(Message, String, int)} for more info.
     *
     * @return result of the post, or {@literal null} if the GCM service was
     * unavailable or any network exception caused the request to fail,
     * or if the response contains more than one result.
     * @throws InvalidRequestException  if GCM didn't returned a 200 status.
     * @throws IllegalArgumentException if to is {@literal null}.
     */
    public Result sendNoRetry(final Message message,
                              final String to) throws IOException {
        nonNull(to);
        Map<Object, Object> jsonRequest = new HashMap<Object, Object>();
        messageToMap(message, jsonRequest);
        jsonRequest.put(JSON_TO, to);
        String responseBody = makeGcmHttpRequest(jsonRequest);
        if (responseBody == null) {
            return null;
        }
        //JSONParser parser = new JSONParser();
        JSONObject jsonResponse;
        try {
            jsonResponse = new JSONObject(responseBody);
            Result.Builder resultBuilder = new Result.Builder();
            if (jsonResponse.has("results")) {
                // Handle response from message sent to specific device.
                JSONArray jsonResults = jsonResponse.getJSONArray("results");
                if (jsonResults.length() == 1) {
                    JSONObject jsonResult = (JSONObject) jsonResults.get(0);
                    String messageId = (String) jsonResult.opt(JSON_MESSAGE_ID);
                    String canonicalRegId = (String) jsonResult.opt(TOKEN_CANONICAL_REG_ID);
                    String error = (String) jsonResult.opt(JSON_ERROR);
                    resultBuilder.messageId(messageId)
                            .canonicalRegistrationId(canonicalRegId)
                            .errorCode(error);
                } else {
                    logger.log(Level.WARNING, "Found null or " + jsonResults.length() +
                            " results, expected one");
                    return null;
                }
            } else if (to.startsWith(TOPIC_PREFIX)) {
                if (jsonResponse.has(JSON_MESSAGE_ID)) {
                    // message_id is expected when this is the response from a topic message.
                    Long messageId = (Long) jsonResponse.opt(JSON_MESSAGE_ID);
                    resultBuilder.messageId(messageId.toString());
                } else if (jsonResponse.has(JSON_ERROR)) {
                    String error = (String) jsonResponse.opt(JSON_ERROR);
                    resultBuilder.errorCode(error);
                } else {
                    logger.log(Level.WARNING, "Expected " + JSON_MESSAGE_ID + " or " + JSON_ERROR +
                            " found: " + responseBody);
                    return null;
                }
            } else if (jsonResponse.has(JSON_SUCCESS) && jsonResponse.has(JSON_FAILURE)) {
                // success and failure are expected when response is from group message.
                int success = JsonWrapper.getInt(jsonResponse, JSON_SUCCESS); //getNumber(jsonResponse, JSON_SUCCESS).intValue();
                int failure = JsonWrapper.getInt(jsonResponse, JSON_FAILURE); //getNumber(jsonResponse, JSON_FAILURE).intValue();
                List<String> failedIds = null;
                if (jsonResponse.has("failed_registration_ids")) {
                    JSONArray jFailedIds = jsonResponse.getJSONArray("failed_registration_ids");
                    failedIds = new ArrayList<String>();
                    for (int i = 0; i < jFailedIds.length(); i++) {
                        failedIds.add((String) jFailedIds.get(i));
                    }
                }
                resultBuilder.success(success).failure(failure)
                        .failedRegistrationIds(failedIds);
            } else {
                logger.warning("Unrecognized response: " + responseBody);
                throw newIoException(responseBody, new Exception("Unrecognized response."));
            }
            return resultBuilder.build();
        } catch (Exception e) {
            throw newIoException(responseBody, e);
        }
    }

    /**
     * Sends a message to many devices, retrying in case of unavailability.
     * <p>
     * <p>
     * <strong>Note: </strong> this method uses exponential back-off to retry in
     * case of service unavailability and hence could block the calling thread
     * for many seconds.
     *
     * @param message message to be sent.
     * @param regIds  registration id of the devices that will receive
     *                the message.
     * @param retries number of retries in case of service unavailability errors.
     * @return combined result of all requests made.
     * @throws IllegalArgumentException if registrationIds is {@literal null} or
     *                                  empty.
     * @throws InvalidRequestException  if GCM didn't returned a 200 or 503 status.
     * @throws IOException              if message could not be sent.
     */
    public MulticastResult send(Message message, List<String> regIds, int retries)
            throws IOException {
        int attempt = 0;
        MulticastResult multicastResult;
        int backoff = BACKOFF_INITIAL_DELAY;
        // Map of results by registration id, it will be updated after each attempt
        // to send the messages
        Map<String, Result> results = new HashMap<String, Result>();
        List<String> unsentRegIds = new ArrayList<String>(regIds);
        boolean tryAgain;
        List<Long> multicastIds = new ArrayList<Long>();
        do {
            multicastResult = null;
            attempt++;
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Attempt #" + attempt + " to send message " +
                        message + " to regIds " + unsentRegIds);
            }
            try {
                multicastResult = sendNoRetry(message, unsentRegIds);
            } catch (IOException e) {
                // no need for WARNING since exception might be already logged
                logger.log(Level.FINEST, "IOException on attempt " + attempt, e);
            }
            if (multicastResult != null) {
                long multicastId = multicastResult.getMulticastId();
                logger.fine("multicast_id on attempt # " + attempt + ": " +
                        multicastId);
                multicastIds.add(multicastId);
                unsentRegIds = updateStatus(unsentRegIds, results, multicastResult);
                tryAgain = !unsentRegIds.isEmpty() && attempt <= retries;
            } else {
                tryAgain = attempt <= retries;
            }
            if (tryAgain) {
                int sleepTime = backoff / 2 + _random.nextInt(backoff);
                sleep(sleepTime);
                if (2 * backoff < MAX_BACKOFF_DELAY) {
                    backoff *= 2;
                }
            }
        } while (tryAgain);
        if (multicastIds.isEmpty()) {
            // all JSON posts failed due to GCM unavailability
            throw new IOException("Could not post JSON requests to GCM after "
                    + attempt + " attempts");
        }
        // calculate summary
        int success = 0, failure = 0, canonicalIds = 0;
        for (Result result : results.values()) {
            if (result.getMessageId() != null) {
                success++;
                if (result.getCanonicalRegistrationId() != null) {
                    canonicalIds++;
                }
            } else {
                failure++;
            }
        }
        // build a new object with the overall result
        long multicastId = multicastIds.remove(0);
        MulticastResult.Builder builder = new MulticastResult.Builder(success,
                failure, canonicalIds, multicastId).retryMulticastIds(multicastIds);
        // add results, in the same order as the input
        for (String regId : regIds) {
            Result result = results.get(regId);
            builder.addResult(result);
        }
        return builder.build();
    }

    /**
     * Updates the status of the messages sent to devices and the list of devices
     * that should be retried.
     *
     * @param unsentRegIds    list of devices that are still pending an update.
     * @param allResults      map of status that will be updated.
     * @param multicastResult result of the last multicast sent.
     * @return updated version of devices that should be retried.
     */
    private List<String> updateStatus(List<String> unsentRegIds,
                                      Map<String, Result> allResults, MulticastResult multicastResult) {
        List<Result> results = multicastResult.getResults();
        if (results.size() != unsentRegIds.size()) {
            // should never happen, unless there is a flaw in the algorithm
            throw new RuntimeException("Internal error: sizes do not match. " +
                    "currentResults: " + results + "; unsentRegIds: " + unsentRegIds);
        }
        List<String> newUnsentRegIds = new ArrayList<String>();
        for (int i = 0; i < unsentRegIds.size(); i++) {
            String regId = unsentRegIds.get(i);
            Result result = results.get(i);
            allResults.put(regId, result);
            String error = result.getErrorCodeName();
            if (error != null && (error.equals(IConstants.ERROR_UNAVAILABLE)
                    || error.equals(IConstants.ERROR_INTERNAL_SERVER_ERROR))) {
                newUnsentRegIds.add(regId);
            }
        }
        return newUnsentRegIds;
    }

    /**
     * Sends a message without retrying in case of service unavailability. See
     * {@link #send(Message, List, int)} for more info.
     *
     * @return multicast results if the message was sent successfully,
     * {@literal null} if it failed but could be retried.
     * @throws IllegalArgumentException if registrationIds is {@literal null} or
     *                                  empty.
     * @throws InvalidRequestException  if GCM didn't returned a 200 status.
     * @throws IOException              if there was a JSON parsing error
     */
    public MulticastResult sendNoRetry(Message message,
                                       List<String> registrationIds) throws IOException {
        if (nonNull(registrationIds).isEmpty()) {
            throw new IllegalArgumentException("registrationIds cannot be empty");
        }
        final Map<Object, Object> jsonRequest = new HashMap<Object, Object>();
        messageToMap(message, jsonRequest);
        jsonRequest.put(JSON_REGISTRATION_IDS, registrationIds);
        String responseBody = makeGcmHttpRequest(jsonRequest);
        if (responseBody == null) {
            return null;
        }
        JSONObject jsonResponse;
        try {
            jsonResponse = new JSONObject(responseBody);
            int success = JsonWrapper.getInt(jsonResponse, JSON_SUCCESS);
            int failure = JsonWrapper.getInt(jsonResponse, JSON_FAILURE); //getNumber(jsonResponse, JSON_FAILURE).intValue();
            int canonicalIds = JsonWrapper.getInt(jsonResponse, JSON_CANONICAL_IDS); //getNumber(jsonResponse, JSON_CANONICAL_IDS).intValue();
            long multicastId = JsonWrapper.getInt(jsonResponse, JSON_MULTICAST_ID); //getNumber(jsonResponse, JSON_MULTICAST_ID).longValue();
            MulticastResult.Builder builder = new MulticastResult.Builder(success,
                    failure, canonicalIds, multicastId);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> results =
                    (List<Map<String, Object>>) jsonResponse.get(JSON_RESULTS);
            if (results != null) {
                for (Map<String, Object> jsonResult : results) {
                    String messageId = (String) jsonResult.get(JSON_MESSAGE_ID);
                    String canonicalRegId =
                            (String) jsonResult.get(TOKEN_CANONICAL_REG_ID);
                    String error = (String) jsonResult.get(JSON_ERROR);
                    Result result = new Result.Builder()
                            .messageId(messageId)
                            .canonicalRegistrationId(canonicalRegId)
                            .errorCode(error)
                            .build();
                    builder.addResult(result);
                }
            }
            return builder.build();
        } catch (Exception e) {
            throw newIoException(responseBody, e);
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private String makeGcmHttpRequest(final Map<Object, Object> jsonRequest) throws InvalidRequestException {
        String requestBody = new JSONObject(jsonRequest).toString();
        logger.finest("JSON request: " + requestBody);
        HttpURLConnection conn;
        int status;
        try {
            conn = post(GCM_SEND_ENDPOINT, "application/json", requestBody);
            status = conn.getResponseCode();
        } catch (IOException e) {
            logger.log(Level.FINE, "IOException posting to GCM", e);
            return null;
        }
        String responseBody;
        if (status != 200) {
            try {
                responseBody = getAndClose(conn.getErrorStream());
                logger.finest("JSON error response: " + responseBody);
            } catch (IOException e) {
                // ignore the exception since it will thrown an InvalidRequestException
                // anyways
                responseBody = "N/A";
                logger.log(Level.FINE, "Exception reading response: ", e);
            }
            throw new InvalidRequestException(status, responseBody);
        }
        try {
            responseBody = getAndClose(conn.getInputStream());
        } catch (IOException e) {
            logger.log(Level.WARNING, "IOException reading response", e);
            return null;
        }
        logger.finest("JSON response: " + responseBody);
        return responseBody;
    }

    /**
     * Populate Map with message.
     *
     * @param message    Message used to populate Map.
     * @param mapRequest Map populated by Message.
     */
    private void messageToMap(Message message, Map<Object, Object> mapRequest) {
        if (message == null || mapRequest == null) {
            return;
        }
        setJsonField(mapRequest, PARAM_PRIORITY, message.getPriority());
        setJsonField(mapRequest, PARAM_CONTENT_AVAILABLE, message.getContentAvailable());
        setJsonField(mapRequest, PARAM_TIME_TO_LIVE, message.getTimeToLive());
        setJsonField(mapRequest, PARAM_COLLAPSE_KEY, message.getCollapseKey());
        setJsonField(mapRequest, PARAM_RESTRICTED_PACKAGE_NAME, message.getRestrictedPackageName());
        setJsonField(mapRequest, PARAM_DELAY_WHILE_IDLE, message.isDelayWhileIdle());
        setJsonField(mapRequest, PARAM_DRY_RUN, message.isDryRun());
        Map<String, String> payload = message.getData();
        if (!payload.isEmpty()) {
            mapRequest.put(JSON_PAYLOAD, payload);
        }
        if (message.getNotification() != null) {
            Notification notification = message.getNotification();
            Map<Object, Object> nMap = new HashMap<Object, Object>();
            if (notification.getBadge() != null) {
                setJsonField(nMap, JSON_NOTIFICATION_BADGE, notification.getBadge().toString());
            }
            setJsonField(nMap, JSON_NOTIFICATION_BODY, notification.getBody());
            setJsonField(nMap, JSON_NOTIFICATION_BODY_LOC_ARGS, notification.getBodyLocArgs());
            setJsonField(nMap, JSON_NOTIFICATION_BODY_LOC_KEY, notification.getBodyLocKey());
            setJsonField(nMap, JSON_NOTIFICATION_CLICK_ACTION, notification.getClickAction());
            setJsonField(nMap, JSON_NOTIFICATION_COLOR, notification.getColor());
            setJsonField(nMap, JSON_NOTIFICATION_ICON, notification.getIcon());
            setJsonField(nMap, JSON_NOTIFICATION_SOUND, notification.getSound());
            setJsonField(nMap, JSON_NOTIFICATION_TAG, notification.getTag());
            setJsonField(nMap, JSON_NOTIFICATION_TITLE, notification.getTitle());
            setJsonField(nMap, JSON_NOTIFICATION_TITLE_LOC_ARGS, notification.getTitleLocArgs());
            setJsonField(nMap, JSON_NOTIFICATION_TITLE_LOC_KEY, notification.getTitleLocKey());
            mapRequest.put(JSON_NOTIFICATION, nMap);
        }
    }

    private IOException newIoException(String responseBody, Exception e) {
        // log exception, as IOException constructor that takes a message and cause
        // is only available on Java 6
        String msg = "Error parsing JSON response (" + responseBody + ")";
        logger.log(Level.WARNING, msg, e);
        return new IOException(msg + ":" + e);
    }

    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                // ignore error
                logger.log(Level.FINEST, "IOException closing stream", e);
            }
        }
    }

    /**
     * Sets a JSON field, but only if the value is not {@literal null}.
     */
    private void setJsonField(Map<Object, Object> json, String field,
                              Object value) {
        if (value != null) {
            json.put(field, value);
        }
    }

    private Number getNumber(Map<?, ?> json, String field) {
        Object value = json.get(field);
        if (value == null) {
            throw new CustomParserException("Missing field: " + field);
        }
        if (!(value instanceof Number)) {
            throw new CustomParserException("Field " + field +
                    " does not contain a number: " + value);
        }
        return (Number) value;
    }

    class CustomParserException extends RuntimeException {
        CustomParserException(String message) {
            super(message);
        }
    }

    /**
     * Make an HTTP post to a given URL.
     *
     * @return HTTP response.
     */
    protected HttpURLConnection post(String url, String body)
            throws IOException {
        return post(url, "application/x-www-form-urlencoded;charset=UTF-8", body);
    }

    /**
     * Makes an HTTP POST request to a given endpoint.
     * <p>
     * <p>
     * <strong>Note: </strong> the returned connected should not be disconnected,
     * otherwise it would kill persistent connections made using Keep-Alive.
     *
     * @param url         endpoint to post the request.
     * @param contentType type of request.
     * @param body        body of the request.
     * @return the underlying connection.
     * @throws IOException propagated from underlying methods.
     */
    protected HttpURLConnection post(String url, String contentType, String body)
            throws IOException {
        if (url == null || contentType == null || body == null) {
            throw new IllegalArgumentException("arguments cannot be null");
        }
        if (!url.startsWith("https://")) {
            logger.warning("URL does not use https: " + url);
        }
        logger.fine("Sending POST to " + url);
        logger.finest("POST body: " + body);
        byte[] bytes = body.getBytes(UTF8);
        HttpURLConnection conn = getConnection(url);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setFixedLengthStreamingMode(bytes.length);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", contentType);
        conn.setRequestProperty("Authorization", "key=" + _key);
        OutputStream out = conn.getOutputStream();
        try {
            out.write(bytes);
        } finally {
            close(out);
        }
        return conn;
    }

    /**
     * Creates a map with just one _key-value pair.
     */
    protected static final Map<String, String> newKeyValues(String key,
                                                            String value) {
        Map<String, String> keyValues = new HashMap<String, String>(1);
        keyValues.put(nonNull(key), nonNull(value));
        return keyValues;
    }

    /**
     * Creates a {@link StringBuilder} to be used as the body of an HTTP POST.
     *
     * @param name  initial parameter for the POST.
     * @param value initial value for that parameter.
     * @return StringBuilder to be used an HTTP POST body.
     */
    protected static StringBuilder newBody(String name, String value) {
        return new StringBuilder(nonNull(name)).append('=').append(nonNull(value));
    }

    /**
     * Adds a new parameter to the HTTP POST body.
     *
     * @param body  HTTP POST body.
     * @param name  parameter's name.
     * @param value parameter's value.
     */
    protected static void addParameter(StringBuilder body, String name,
                                       String value) {
        nonNull(body).append('&')
                .append(nonNull(name)).append('=').append(nonNull(value));
    }

    /**
     * Gets an {@link HttpURLConnection} given an URL.
     */
    private HttpURLConnection getConnection(String url) throws IOException {
        return (HttpURLConnection) new URL(url).openConnection();
    }

    /**
     * Convenience method to convert an InputStream to a String.
     * <p>
     * If the stream ends in a newline character, it will be stripped.
     * <p>
     * If the stream is {@literal null}, returns an empty string.
     */
    private static String getString(InputStream stream) throws IOException {
        if (stream == null) {
            return "";
        }
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(stream));
        StringBuilder content = new StringBuilder();
        String newLine;
        do {
            newLine = reader.readLine();
            if (newLine != null) {
                content.append(newLine).append('\n');
            }
        } while (newLine != null);
        if (content.length() > 0) {
            // strip last newline
            content.setLength(content.length() - 1);
        }
        return content.toString();
    }

    private static String getAndClose(InputStream stream) throws IOException {
        try {
            return getString(stream);
        } finally {
            if (stream != null) {
                close(stream);
            }
        }
    }

    static <T> T nonNull(T argument) {
        if (argument == null) {
            throw new IllegalArgumentException("argument cannot be null");
        }
        return argument;
    }

    void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
