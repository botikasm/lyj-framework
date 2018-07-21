package org.ly.ose.client.gui.deploy;

import org.lyj.Lyj;
import org.lyj.commons.io.repository.deploy.FileDeployer;
import org.lyj.commons.logging.AbstractLogEmitter;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Centralized deploy controller.
 */
public class DeployController
        extends AbstractLogEmitter {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Collection<FileDeployer> _deployers;


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private DeployController() {
        _deployers = new LinkedList<>();
        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void deploy() {
        if (!Lyj.isTestUnitMode()) {
            for (final FileDeployer deployer : _deployers) {
                deployer.deploy();
            }

            // deploy webapp
            try {
                //WebDeployController.instance().deploy();
            } catch (Throwable t) {
                super.error("deploy#webapp", t);
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        //_deployers.add(new HtdocsDeployer(Lyj.isSilent()));
        // deployers.add(new SslDeployer(Lyj.isSilent()));


    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static DeployController __instance;

    public static synchronized DeployController instance() {
        if (null == __instance) {
            __instance = new DeployController();
        }
        return __instance;
    }


}
