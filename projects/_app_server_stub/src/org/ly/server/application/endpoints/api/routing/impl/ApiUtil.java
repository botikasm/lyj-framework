package org.ly.server.application.endpoints.api.routing.impl;


import org.json.JSONObject;
import org.ly.server.IConstants;
import org.ly.server.application.endpoints.api.ApiHelper;
import org.lyj.commons.cryptograph.MD5;
import org.lyj.commons.logging.LoggingRepository;
import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.commons.util.json.JsonItem;
import org.lyj.ext.netty.server.web.HttpServerContext;

import java.io.File;
import java.util.Map;
import java.util.Set;

public class ApiUtil {

    public static void ping(final HttpServerContext context) {
        context.writeJson("true");
    }

    public static void version(final HttpServerContext context) {
        final JSONObject item = new JSONObject();
        item.put("version", IConstants.APP_VERSION);
        item.put("name", IConstants.APP_NAME);
        context.writeJson(item);
    }

    public static void md5(final HttpServerContext context) {
        final String token = ApiHelper.getParamToken(context);

        ApiHelper.auth(token, (err, valid) -> {
            if (null == err) {
                final String value = context.getParam("value");
                if (StringUtils.hasText(value)) {
                    try {
                        final String md5 = MD5.encode(value);

                        ApiHelper.writeJSON(context, md5);
                    } catch (Throwable t) {
                        ApiHelper.writeError(context, t, "login");
                    }

                } else {
                    ApiHelper.writeErroMissingParams(context, "value");
                }
            } else {
                ApiHelper.writeError(context, err);
            }
        });
    }

    /**
     * https://localhost:4000/api/util/log/botbuilder_system_jdgdiken8d6773jd/lyj.log
     * https://localhost:4000/api/util/log/botbuilder_system_jdgdiken8d6773jd/mail_listener.log
     * https://localhost:4000/api/util/log/botbuilder_system_jdgdiken8d6773jd/bot.log
     * https://localhost:4000/api/util/log/botbuilder_system_jdgdiken8d6773jd/installer.log
     * <p>
     * https://api.conversacon.com:4000/api/util/log/botbuilder_system_jdgdiken8d6773jd/lyj.log
     */
    public static void log(final HttpServerContext context) {
        final String token = ApiHelper.getParamToken(context);

        ApiHelper.auth(token, (err, valid) -> {
            if (null == err) {
                try {
                    final String name = ApiHelper.getParam(context, "name");

                    final String text = readLog(name);

                    ApiHelper.writeHTML(context, text);
                } catch (Throwable t) {
                    ApiHelper.writeError(context, t, "login");
                }
            } else {
                ApiHelper.writeError(context, err);
            }
        });
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static String readLog(final String name) {
        final StringBuilder response = new StringBuilder();
        final Map<String, String> loggers = LoggingRepository.getInstance().pathMap();
        if (StringUtils.hasText(name)) {
            try {
                final Set<String> keys = loggers.keySet();
                for (final String key : keys) {
                    final String value = loggers.get(key);
                    final String logger_name = PathUtils.getFilename(value, true);
                    if (logger_name.equalsIgnoreCase(name)) {
                        final String file_name = PathUtils.getAbsolutePath(value);
                        if (FileUtils.exists(file_name)) {
                            response.append(FileUtils.readFileToString(new File(file_name)));
                            break;
                        }
                    }
                }
            } catch (Throwable t) {
                response.append(t.toString());
            }
        }

        if (!StringUtils.hasText(response.toString())) {
            response.append(new JsonItem(loggers).toString()
                    .replaceAll("\\{", "{\n")
                    .replaceAll("}", "\n}")
                    .replaceAll(",", ",\n"));
        }

        return response.toString()
                .replaceAll("\n", "<br>")
                .replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
    }

}
