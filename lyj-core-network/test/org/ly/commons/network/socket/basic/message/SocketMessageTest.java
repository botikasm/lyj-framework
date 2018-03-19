package org.ly.commons.network.socket.basic.message;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SocketMessageTest {

    @Test
    public void socketMessageParse() {

        final String TEXT = "ABv";

        final SocketMessage message = new SocketMessage();
        message.body(TEXT);
        final byte[] bytes = message.bytes();

        final SocketMessage clone = new SocketMessage(bytes);
        final String body = new String(clone.body());

        assertEquals(body, TEXT);

        final byte[] message_encoded = message.bytes();
        final SocketMessageReader reader = new SocketMessageReader();
        int count = 0;
        while (!reader.isComplete()) {
            try {
                reader.write(bytes[count]);
            }catch(Throwable t){
                t.printStackTrace();
                System.out.println(reader.toString());
                break;
            }
            count++;
        }

        System.out.println(reader.message().toString());
    }
}