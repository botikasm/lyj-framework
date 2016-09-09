package org.lyj.commons.session;

/**
 *
 */
public interface ISessionMonitor {

    void notifySessionExpired(final String session_id);

}
