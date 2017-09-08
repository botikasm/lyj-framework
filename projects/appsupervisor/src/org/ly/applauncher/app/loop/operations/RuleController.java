package org.ly.applauncher.app.loop.operations;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ly.applauncher.app.model.Action;
import org.ly.applauncher.app.model.Rule;
import org.ly.applauncher.deploy.config.ConfigHelper;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.StringUtils;

import java.util.*;

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
    private final Map<String, Action> _actions;


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private RuleController() {
        _rules = new LinkedList<>();
        _actions = new HashMap<>();

        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    /**
     * Check all rules for an action to perform
     */
    public void check() {
        for (final Rule rule : _rules) {
            final String action_name = this.validateRule(rule);
            if (StringUtils.hasText(action_name) && _actions.containsKey(action_name)) {
                ActionController.instance().run(_actions.get(action_name));
            }
        }
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

        // init actions
        final JSONObject actions = ConfigHelper.instance().actions();
        final Set<String> keys = actions.keySet();
        for (final String key : keys) {
            final Action action = new Action(actions.optJSONObject(key));
            if (action.commands().length > 0) {
                _actions.put(key, action);
            }
        }

    }

    /**
     * Validate a rule and return action to execute.
     *
     * @param rule Rule to validate
     * @return Action name or empty string
     */
    private String validateRule(final Rule rule) {
        final String type = rule.type();
        if(TYPE_MEMORY.equalsIgnoreCase(type)){

        } else if(TYPE_CLOCK.equalsIgnoreCase(type)) {

        } else if(TYPE_PING.equalsIgnoreCase(type)) {

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
