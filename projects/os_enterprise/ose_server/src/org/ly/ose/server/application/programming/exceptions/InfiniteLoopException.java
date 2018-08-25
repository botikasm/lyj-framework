package org.ly.ose.server.application.programming.exceptions;

public class InfiniteLoopException extends Exception{

    public InfiniteLoopException(){
        super("More than 100 recursion is too much, Suspect for an infinite loop");
    }

}
