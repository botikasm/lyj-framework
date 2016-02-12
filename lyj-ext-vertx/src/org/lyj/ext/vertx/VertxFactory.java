package org.lyj.ext.vertx;

import io.vertx.core.Vertx;

/**
 * ----- ONLY FOR TEST UNIT -------
 * Utility to creates a singleton Vertx instance.
 * You should not use this to get a Vertx instance, but Vertx native deploy.
 *
 * This class is only for test unit.
 *
 */
public class VertxFactory {

    private static Vertx __vertx;

    public static Vertx vertx(){
        if(null==__vertx){
            __vertx = Vertx.vertx();
        }
        return __vertx;
    }

}
