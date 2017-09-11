package org.ly.applauncher.app.loop;

import org.junit.Test;
import org.lyj.commons.async.Async;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

public class ExecutableTest {

    private static final String CMD1 = "/Users/angelogeminiani/conversacon_bin/run_server_debug.sh";
    private static final String CMD2 = "java -agentlib:jdwp=transport=dt_socket,server=y,address=8000,suspend=n -jar /Users/angelogeminiani/conversacon_bin/conversacon_server.jar -w USERHOME/conversacon_server -d true";
    
    @Test
    public void run() throws Exception {
        // final Executable exec = new Executable(CMD2).run();
        // exec.interrupt();

        Process process = Runtime.getRuntime().exec(CMD2);

        Async.invoke((args) -> {
            try {
                String line = null;
                final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    // Ignore line, or do something with it
                    System.out.println(line);
                }
            } catch (Throwable t) {

            }
        });

        int pid = 0;
        if (process.getClass().getName().equals("java.lang.UNIXProcess")) {
            /* get the PID on unix/linux systems */
            try {
                Field f = process.getClass().getDeclaredField("pid");
                f.setAccessible(true);
                pid = f.getInt(process);
            } catch (Throwable e) {
            }
        }

        System.out.println(pid);

        Thread.sleep(10000);

        process.destroy();
        process.destroyForcibly();
        process.waitFor();

        //Runtime.getRuntime().exec("kill -9 " + pid);

        //Thread.sleep(10000);

    }

}