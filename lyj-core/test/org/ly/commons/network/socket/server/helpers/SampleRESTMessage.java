package org.ly.commons.network.socket.server.helpers;

import org.ly.commons.network.socket.messages.rest.RESTMessage;

/**
 *
 */
public class SampleRESTMessage extends RESTMessage {

     public SampleRESTMessage(){
         super.setMethod(RESTMessage.METHOD_GET);
         super.setPath("/test/all");
     }

}
