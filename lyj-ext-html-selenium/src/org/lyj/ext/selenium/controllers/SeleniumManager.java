package org.lyj.ext.selenium.controllers;

import org.lyj.commons.util.FileUtils;
import org.lyj.commons.util.PathUtils;
import org.lyj.ext.selenium.controllers.proxy.ProxyController;
import org.lyj.ext.selenium.controllers.routines.RoutineController;
import org.lyj.ext.selenium.deploy.DriverDeployer;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class SeleniumManager {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String ROOT = "./selenium";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _root;

    private final ProxyController _proxies;
    private final RoutineController _scripts;

    private boolean _is_open;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private SeleniumManager() {
        _root = PathUtils.getAbsolutePath(ROOT);
        _is_open = false;

        _proxies = new ProxyController(this);
        _scripts = new RoutineController(this);

        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String root() {
        return _root;
    }

    public void open() {
        if (!_is_open) {

            _proxies.open();
            _scripts.open();

            // test connection before get one
            _proxies.testConnection(true);

            _is_open = true;
        }
    }

    public void close() {
        if (_is_open) {

            _proxies.close();
            _scripts.close();

            _is_open = false;
        }
    }

    public boolean isOpen() {
        return _is_open;
    }

    public ProxyController proxies() {
        return _proxies;
    }

    public RoutineController scripts() {
        return _scripts;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        // creates root
        FileUtils.tryMkdirs(_root);

        this.deploy();
    }

    private void deploy() {
        final String tmp_deploy = PathUtils.concat(_root, "tmp");
        final DriverDeployer driver_deployer = new DriverDeployer(tmp_deploy, true);
        driver_deployer.deploy();


    }

    private void test() {
        WebDriver driver = new ChromeDriver();
        driver.get("http://www.botika.it");

        driver.close();
    }

    // ------------------------------------------------------------------------
    //                      S I N G L E T O N
    // ------------------------------------------------------------------------

    private static SeleniumManager __instance;

    public static SeleniumManager instance() {
        if (null == __instance) {
            __instance = new SeleniumManager();
        }
        return __instance;
    }

}
