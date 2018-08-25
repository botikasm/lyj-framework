package org.ly.ose.server.application.programming.exceptions;

public class ImproperUseException extends Exception{

    public ImproperUseException(){
        super("You are not authorized to call this method.");
    }

}
