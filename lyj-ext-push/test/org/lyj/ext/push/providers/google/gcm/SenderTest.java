package org.lyj.ext.push.providers.google.gcm;

import org.junit.Test;
import org.lyj.ext.push.IConstants;

/**
 * Created by angelogeminiani on 18/03/16.
 */
public class SenderTest {

    @Test
    public void send() throws Exception {
        Result result = null;

        String GOOGLE_SERVER_KEY = IConstants.GOOGLE_SERVER_KEY;
        String MESSAGE_KEY = "message";

        String regId = IConstants.DEVICE_ID_2; // GCM RedgId of Android device to send push notification
        String userMessage = "Hello from test";


        Sender sender = new Sender(GOOGLE_SERVER_KEY);
        Message message = new Message.Builder()
                .timeToLive(30).delayWhileIdle(true)
                .contentAvailable(true)
                .addData(MESSAGE_KEY, userMessage)
                .build();

        System.out.println("regId: " + regId);

        result = sender.send(message, regId, 1);

        System.out.println(result);


    }
}