package org.lyj.ext.selenium.controllers.routines.controller;

import org.lyj.commons.util.StringUtils;
import org.lyj.ext.selenium.IConstants;
import org.lyj.ext.selenium.controllers.SeleniumManager;
import org.lyj.ext.selenium.controllers.proxy.model.ModelProxy;
import org.lyj.ext.selenium.controllers.routines.model.ModelPackage;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

public class DriverBuilder {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String DRIVER_BASE = IConstants.DRIVER_BASE;
    private static final String DRIVER_CHROME = IConstants.DRIVER_CHROME;
    private static final String DRIVER_FIREFOX = IConstants.DRIVER_FIREFOX;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final ModelPackage _info;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private DriverBuilder(final ModelPackage info) {
        _info = info;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public WebDriver build() throws Exception {
        // capabilities
        final DesiredCapabilities cap = this.getCapabilities();

        final String driver = _info.browser();
        if (DRIVER_CHROME.equalsIgnoreCase(driver)) {

            return null != cap ? new ChromeDriver(cap) : new ChromeDriver();
        } else if (DRIVER_FIREFOX.equalsIgnoreCase(driver)) {

            return null != cap ? new FirefoxDriver(new FirefoxOptions(cap)) : new FirefoxDriver();
        } else {
            return null != cap ? new FirefoxDriver(new FirefoxOptions(cap)) : new FirefoxDriver();
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private DesiredCapabilities getCapabilities() {
        final DesiredCapabilities cap = new DesiredCapabilities();

        // proxy
        final Proxy proxy = this.getProxy();
        if (null != proxy) {
            cap.setCapability(CapabilityType.PROXY, proxy);
        }


        return cap;
    }

    private Proxy getProxy() {
        Proxy response = null;
        final ModelPackage.Proxy pinfo = _info.proxy();
        if (pinfo.enabled()) {

            if (!StringUtils.hasText(pinfo.ip()) || pinfo.port() < 0) {
                // get random proxy (connection tested)
                final ModelProxy model = SeleniumManager.instance().proxies().getOneRndByProtocol(pinfo.protocol());
                pinfo.ip(model.ip());
                pinfo.port(model.port());
            }

            final String protocol = pinfo.protocol(); // http, ssl, socks4, socks5
            final String proxy_text = pinfo.ip() + ":" + pinfo.port();
            final String driver = _info.browser();

            if (driver.equalsIgnoreCase(DRIVER_CHROME)
                    || driver.equalsIgnoreCase(DRIVER_FIREFOX)
                    || driver.equalsIgnoreCase(DRIVER_BASE)) {

                response = new Proxy();
                response.setProxyType(Proxy.ProxyType.MANUAL);

                if ("http".equalsIgnoreCase(protocol)) {
                    response.setHttpProxy(proxy_text);
                } else if ("ssl".equalsIgnoreCase(protocol)) {
                    response.setSslProxy(proxy_text);
                } else if ("socks4".equalsIgnoreCase(protocol)) {
                    response.setSocksProxy(proxy_text);
                    response.setSocksVersion(4);
                } else if ("socks5".equalsIgnoreCase(protocol)) {
                    response.setSocksProxy(proxy_text);
                    response.setSocksVersion(5);
                } else {
                    response.setSocksProxy(proxy_text);
                    response.setSocksVersion(5);
                }
            }
        }
        return response;
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static WebDriver build(final ModelPackage info) throws Exception {
        final DriverBuilder builder = new DriverBuilder(info);
        return builder.build();
    }

}
