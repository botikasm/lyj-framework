package org.lyj.commons.network.socket.server.helpers;

import org.lyj.commons.network.socket.messages.rest.RESTMessage;

/**
 *
 */
public class SampleRESTMessage extends RESTMessage {

     public SampleRESTMessage(){
         super.setMethod(RESTMessage.METHOD_GET);
         super.setPath("/test/all");
     }

}
