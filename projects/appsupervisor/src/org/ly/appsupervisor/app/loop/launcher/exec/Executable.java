package org.ly.appsupervisor.app.loop.launcher.exec;

import org.lyj.commons.Delegates;
import org.lyj.commons.async.Async;
import org.lyj.commons.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

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
        this(StringUtils.split(cmd, " "));
    }

    public Executable(final String[] cmd) {
        _cmd = cmd;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String cmd() {
        return StringUtils.toString(_cmd);
    }

    public Executable run() throws Exception {
        try {
            if (_cmd.length > 0) {
                final ProcessBuilder ps = new ProcessBuilder(_cmd);
                _process = ps.start();
                this.handleOutput(_process);
                this.handleError(_process);
            }
        } catch (Exception e) {
            throw e;
        }
        return this;
    }

    public Executable interrupt() {
        if (null != _process) {
            _process.destroy();
            try {
                if (!_process.waitFor(10, TimeUnit.SECONDS)) {
                    _process.destroyForcibly();
                }
            } catch (Throwable ignored) {
            }
            _process = null;
        }
        return this;
    }

    public boolean isAlive() {
        return null != _process && _process.isAlive();
    }

    public Executable output(final Delegates.Callback<String> callback) {
        _callback_out = callback;
        return this;
    }

    public Executable error(final Delegates.Callback<String> callback) {
        _callback_error = callback;
        return this;
    }

    /**
     * Return PID for linux system
     *
     * @return PID
     */
    public int pid() {
        if (null != _process) {
            return this.pid(_process);
        }
        return 0;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void handleOutput(final Process p) {
        Async.invoke((args) -> {
            try (final BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = in.readLine()) != null) {
                    if (null != _callback_out) {
                        _callback_out.handle(line);
                    }
                }
            } catch (Throwable ignored) {

            }
        });
    }

    private void handleError(final Process p) {
        Async.invoke((args) -> {
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
        });
    }

    private int pid(final Process p) {
        if (p.getClass().getName().equals("java.lang.UNIXProcess")) {
            /* get the PID on unix/linux systems */
            try {
                final Field f = p.getClass().getDeclaredField("pid");
                f.setAccessible(true);
                return f.getInt(p);
            } catch (Throwable e) {
            }
        } else if (p.getClass().getName().equals("java.lang.Win32Process") ||
                p.getClass().getName().equals("java.lang.ProcessImpl")) {
                /* determine the pid on windows plattforms */
            try {
                final Field f = p.getClass().getDeclaredField("handle");
                f.setAccessible(true);
                long handl = f.getLong(p);

                //Kernel32 kernel = Kernel32.INSTANCE;
                //W32API.HANDLE handle = new W32API.HANDLE();
                //handle.setPointer(Pointer.createConstant(handl));
                //pid = kernel.GetProcessId(handle);

                return 0;
            } catch (Throwable e) {
            }
        }

        return 0;
    }

}
