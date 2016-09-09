package org.lyj.ext.script;

/**
 * Created by angelogeminiani on 07/09/16.
 */
public class SimpleObject {

    public String sayHello() {
        return "Hello from " + this.getClass().getName();
    }

}
