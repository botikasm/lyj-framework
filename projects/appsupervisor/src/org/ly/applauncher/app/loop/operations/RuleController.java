package org.ly.applauncher.app.loop.operations;

import org.json.JSONArray;
import org.ly.applauncher.app.model.Rule;
import org.ly.applauncher.deploy.config.ConfigHelper;
import org.lyj.commons.network.URLUtils;
import org.lyj.commons.util.ByteUtils;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.StringUtils;

import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class RuleController {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String TYPE_MEMORY = "memory"; // check memory free
    public static final String TYPE_CLOCK = "clock";  // check date time
    public static final String TYPE_PING = "ping";    // check a ping response timeout

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

        return "";
    }

    private String validateClockRule(final Rule rule) {

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
