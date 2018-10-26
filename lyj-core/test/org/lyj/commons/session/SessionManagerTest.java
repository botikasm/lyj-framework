package org.lyj.commons.session;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by angelogeminiani on 07/09/16.
 */
public class SessionManagerTest {


    @Test
    public void get() throws Exception {

        Session session = SessionManager.instance().idleTimeOut(2000).get();
        assertTrue(!session.expired());

        System.out.println(session);

        Thread.sleep(2000);

        assertTrue(session.expired());

        session.put("key", "value");

        assertTrue(session.expired());

        Thread.sleep(500);

        assertFalse(SessionManager.instance().contains(session.id()));

        session = SessionManager.instance().idleTimeOut(2000).get();
        System.out.println(session);

        Thread.sleep(1000);

        session.put("key", "value");

        assertFalse(session.expired());

        Thread.sleep(1500);

        assertFalse(session.expired());

        Thread.sleep(500);

        assertTrue(session.expired());

        System.out.println("SESSIONS: " + SessionManager.instance().size());

        Thread.sleep(1000);

        System.out.println("SESSIONS: " + SessionManager.instance().size());
    }

}