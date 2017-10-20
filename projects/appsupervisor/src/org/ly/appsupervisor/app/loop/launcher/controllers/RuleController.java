package org.ly.appsupervisor.app.loop.launcher.controllers;

import org.json.JSONArray;
import org.ly.appsupervisor.app.IConstants;
import org.ly.appsupervisor.app.model.Rule;
import org.ly.appsupervisor.deploy.config.ConfigHelper;
import org.lyj.commons.network.URLUtils;
import org.lyj.commons.util.*;

import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class RuleController {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String TYPE_MEMORY = IConstants.TYPE_MEMORY; // check memory free
    private static final String TYPE_CLOCK = IConstants.TYPE_CLOCK;  // check date time
    private static final String TYPE_PING = IConstants.TYPE_PING;   // check a ping response timeout
    private static final String TYPE_NULL = IConstants.TYPE_NULL;

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

    private final List<Rule> _rules;


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private RuleController() {
        _rules = new LinkedList<>();


        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    /**
     * Check all rules for an action to perform
     */
    public Set<String> check() {
        final Set<String> actions = new HashSet<>();
        for (final Rule rule : _rules) {
            final String action_name = this.validateRule(rule);
            if (StringUtils.hasText(action_name)) {
                actions.add(action_name);
            }
        }

        return actions;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        // init rules
        final JSONArray rules = ConfigHelper.instance().rules();
        CollectionUtils.forEach(rules, (item) -> {
            final Rule rule = new Rule(item);
            if (rule.enabled()) {
                _rules.add(rule);
            }
        });

    }

    /**
     * Validate an enabled rule and return action to execute.
     * If rule is not enabled, nothing is checked.
     *
     * @param rule Rule to validate
     * @return Action name or empty string
     */
    private String validateRule(final Rule rule) {
        if (rule.enabled()) {
            final String type = rule.type();
            if (TYPE_MEMORY.equalsIgnoreCase(type)) {
                return this.validateMemoryRule(rule);
            } else if (TYPE_CLOCK.equalsIgnoreCase(type)) {
                return this.validateClockRule(rule);
            } else if (TYPE_PING.equalsIgnoreCase(type)) {
                return this.validatePingRule(rule);
            }
        }
        return "";
    }

    private String validateMemoryRule(final Rule rule) {
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

    private String validateClockRule(final Rule rule) {
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
    private String validatePingRule(final Rule rule) {
        final String host = rule.host();
        final int timeout = rule.timeout();

        if (StringUtils.hasText(host) && timeout > 0) {
            // perform GET
            try (final InputStream is = URLUtils.getInputStream(host, timeout)) {
                final byte[] bytes = ByteUtils.getBytes(is);
                // got response and return action name
                return "";
            } catch (Throwable t) {
                // timeout
                return rule.action();
            }
        }

        return "";
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
