package org.ly.appsupervisor.app.loop.launcher.controllers;

import org.ly.appsupervisor.app.IConstants;
import org.ly.appsupervisor.app.model.ModelLauncher;
import org.ly.appsupervisor.app.model.ModelRule;
import org.ly.appsupervisor.deploy.config.ConfigHelper;
import org.lyj.commons.network.http.client.HttpClient;
import org.lyj.commons.util.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RuleController {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------
    private static final Map<String, ModelLauncher> LAUNCHERS = ConfigHelper.instance().launchers();

    private static final String TYPE_MEMORY = IConstants.TYPE_MEMORY; // check memory free
    private static final String TYPE_CLOCK = IConstants.TYPE_CLOCK;  // check date time
    private static final String TYPE_PING = IConstants.TYPE_PING;   // check a ping response timeout
    private static final String TYPE_NULL = IConstants.TYPE_NULL;
    private static final String TYPE_HTTP = IConstants.TYPE_HTTP;

    private static final String MU_DATE = IConstants.MU_DATE; // check datetime
    private static final String MU_TIME = IConstants.MU_TIME; // check time
    private static final String MU_DATETIME = IConstants.MU_DATETIME; // check datetime
    private static final String MU_MB = IConstants.MU_MB;  // megabyte
    private static final String MU_GB = IConstants.MU_GB;  // gigabyte

    private static final String PATTERN_DATE = IConstants.PATTERN_DATE;
    private static final String PATTERN_DATE_TIME = IConstants.PATTERN_DATE_TIME;
    private static final String PATTERN_TIME = IConstants.PATTERN_TIME;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private RuleController() {
        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    /**
     * Check all rules for an action to perform
     */
    public Map<String, Set<String>> check() {
        final Map<String, Set<String>> response = new HashMap<>();
        LAUNCHERS.forEach((uid, launcher) -> {
            // is laucher enabled?
            if (launcher.enabled()) {
                launcher.rules().forEach((rule) -> {
                    final String action_name = this.validateRule(uid, rule);
                    if (StringUtils.hasText(action_name)) {
                        if (!response.containsKey(uid)) {
                            response.put(uid, new HashSet<>());
                        }
                        response.get(uid).add(action_name);
                    }
                });
            }
        });
        return response;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {

    }

    /**
     * Validate an enabled rule and return action to execute.
     * If rule is not enabled, nothing is checked.
     *
     * @param rule Rule to validate
     * @return Action name or empty string
     */
    private String validateRule(final String uid,
                                final ModelRule rule) {
        if (rule.enabled()) {
            final String type = rule.type();
            if (TYPE_MEMORY.equalsIgnoreCase(type)) {
                return this.validateMemoryRule(rule);
            } else if (TYPE_CLOCK.equalsIgnoreCase(type)) {
                return this.validateClockRule(rule);
            } else if (TYPE_PING.equalsIgnoreCase(type)) {
                return this.validatePingRule(rule);
            } else if (TYPE_HTTP.equalsIgnoreCase(type)) {
                return this.validateHttpRule(rule);
            } else if (TYPE_NULL.equalsIgnoreCase(type)) {
                return this.validateNullRule(uid, rule);
            }
        }
        return "";
    }

    private String validateMemoryRule(final ModelRule rule) {
        try {
            final String mu = rule.mu();
            final String lower_than = rule.lowerThan();
            final String greater_than = rule.greaterThan();
            if (StringUtils.hasText(mu)) {
                final long free_memory = this.freeMemory(mu);
                if (StringUtils.hasText(lower_than)) {
                    final int check_value = ConversionUtils.toInteger(lower_than, -1);
                    if (free_memory <= check_value) {
                        return rule.action();
                    }
                } else if (StringUtils.hasText(greater_than)) {
                    final int check_value = ConversionUtils.toInteger(greater_than, -1);
                    if (free_memory >= check_value) {
                        return rule.action();
                    }
                }
            }
        } catch (Throwable ignored) {

        }
        return "";
    }

    private String validateClockRule(final ModelRule rule) {
        try {
            final String mu = rule.mu();
            final String lower_than = rule.lowerThan();
            final String greater_than = rule.greaterThan();
            if (StringUtils.hasText(mu)) {
                if (StringUtils.hasText(lower_than)) {
                    // now < 10,30
                    if (!this.isExpired(lower_than, mu)) {
                        return rule.action();
                    }
                } else if (StringUtils.hasText(greater_than)) {
                    // now > 10,30
                    if (this.isExpired(greater_than, mu)) {
                        return rule.action();
                    }
                }
            }
        } catch (Throwable ignored) {

        }
        return "";
    }

    /**
     * Returns action name if request timeout
     */
    private String validatePingRule(final ModelRule rule) {
        final String host = rule.host();
        final int timeout = rule.timeout();

        if (StringUtils.hasText(host) && timeout > 0) {
            // perform GET
            try {
                final String response = HttpClient.get(host, null, timeout * 1000);
                return "";
            } catch (Throwable t) {
                // timeout
                return rule.action();
            }
        }

        return "";
    }

    private String validateHttpRule(final ModelRule rule) {
        final String host = rule.host();
        final String raw_expression = rule.expression();
        final int timeout = rule.timeout();

        if (StringUtils.hasText(host) && timeout > 0 && StringUtils.hasText(raw_expression)) {
            // perform GET
            try {
                final RuleExpression expression = new RuleExpression(raw_expression);
                final String response = HttpClient.get(host, null, timeout * 1000);
                return expression.validate(response) ? rule.action() : "";
            } catch (Throwable t) {
                // timeout
                return rule.action();
            }
        }

        return "";
    }

    private String validateNullRule(final String uid, final ModelRule rule) {
        // is application running?
        return ActionController.instance().isExecuting(uid) ? "" : rule.action();
    }

    private long freeMemory(final String mu) {
        // garbage collector
        SystemUtils.gc();
        // get total free memory
        //final long total_memory = SystemUtils.getTotalMemory();
        //final long max_memory = SystemUtils.getMaxMemory();
        final long free_memory = SystemUtils.getFreeMemory();
        if (mu.equalsIgnoreCase(MU_MB)) {
            return (long) ConversionUtils.bytesToMbyte(free_memory);
        } else if (mu.equalsIgnoreCase(MU_GB)) {
            return (long) ConversionUtils.bytesToMbyte(free_memory) / 1000;
        } else {
            return free_memory;
        }
    }

    private boolean isExpired(final String value, final String mu) throws Exception {
        if (mu.equalsIgnoreCase(MU_DATE)) {
            final DateWrapper dw = new DateWrapper(value, PATTERN_DATE);
            return DateUtils.isExpiredDate(dw, DateUtils.now());
        } else if (mu.equalsIgnoreCase(MU_DATETIME)) {
            final DateWrapper dw = new DateWrapper(value, PATTERN_DATE_TIME);
            return DateUtils.isExpiredDate(dw, DateUtils.now()) && DateUtils.isExpiredTime(dw, DateUtils.now());
        } else if (mu.equalsIgnoreCase(MU_TIME)) {
            final DateWrapper dw = new DateWrapper(value, PATTERN_TIME);
            return DateUtils.isExpiredTime(dw, DateUtils.now());
        }
        throw new Exception("Unsupported 'Clock' Measure Unit: " + mu);
    }
    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static RuleController __instance;

    public static RuleController instance() {
        if (null == __instance) {
            __instance = new RuleController();
        }
        return __instance;
    }


}
