package org.ly.applauncher.app.loop;

import org.lyj.commons.Delegates;
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
    private Delegates.Callback<String> _callback_out;
    private Delegates.Callback<String> _callback_error;

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
            this.handleError(_process);
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

    public Executable output(final Delegates.Callback<String> callback) {
        _callback_out = callback;
        return this;
    }

    public Executable error(final Delegates.Callback<String> callback) {
        _callback_error = callback;
        return this;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void handleOutput(final Process p) {
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                if (null != _callback_out) {
                    _callback_out.handle(line);
                }
            }
        } catch (Throwable ignored) {

        }
    }

    private void handleError(final Process p) {
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                if (null != _callback_error) {
                    _callback_error.handle(line);
                } else if (null != _callback_out) {
                    _callback_out.handle(line);

                }
            }
        } catch (Throwable ignored) {

        }
    }

}
