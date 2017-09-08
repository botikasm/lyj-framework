package org.ly.applauncher.app.loop;

import org.lyj.commons.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Executable {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String[] _cmd;

    private Process _process;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Executable(final String cmd) {
        _cmd = new String[]{cmd};
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String cmd() {
        return StringUtils.toString(_cmd);
    }

    public Executable run() throws IOException {
        if (_cmd.length > 0) {
            final ProcessBuilder ps = new ProcessBuilder(_cmd);
            _process = ps.start();
            this.handleOutput(_process);
        }
        return this;
    }

    public Executable interrupt() {
        if (null != _process) {
            _process.destroy();
            _process = null;
        }
        return this;
    }

    public boolean isAlive() {
        if (null != _process) {
            return _process.isAlive();
        }
        return false;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void handleOutput(final Process p) {
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()))){
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Throwable ignored) {

        }
    }

}
