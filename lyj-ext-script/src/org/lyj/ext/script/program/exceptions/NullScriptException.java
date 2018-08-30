package org.lyj.ext.script.program.exceptions;

import org.lyj.ext.script.program.engines.AbstractEngine;

public class NullScriptException extends Exception{

    public NullScriptException(final AbstractEngine engine){
         super("Cannot run a program with no script or files to execute!");
    }

}
