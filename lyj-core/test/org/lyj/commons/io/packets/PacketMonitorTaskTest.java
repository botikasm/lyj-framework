package org.lyj.commons.io.packets;

import org.junit.Before;
import org.junit.Test;
import org.lyj.TestInitializer;

import static org.junit.Assert.*;

public class PacketMonitorTaskTest {


    @Before
    public void setUp() throws Exception {
        TestInitializer.init();
    }

    @Test
    public void open() throws Exception {

        final PacketMonitorTask task = new PacketMonitorTask(10); // 10 seconds interval

        task.root("./packets");

        task.callback((file, files)->{
            System.out.println(file);
            System.out.println(files);
        });

        task.open();

        while (task.isRunning()){
            Thread.sleep(1000);
        }

        
    }

}