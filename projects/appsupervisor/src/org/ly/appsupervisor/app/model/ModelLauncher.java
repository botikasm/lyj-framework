package org.ly.appsupervisor.app.model;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.json.JsonItem;

import java.util.*;

public class ModelLauncher extends JsonItem {

    // ------------------------------------------------------------------------
    //                      c o n s
    // ------------------------------------------------------------------------

    private static final String FLD_UID = "uid";
    private static final String FLD_EXEC = "exec";
    private static final String FLD_RULES = "rules";
    private static final String FLD_ACTIONS = "actions";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    final Map<String, ModelAction> _actions;
    final Collection<ModelRule> _rules;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public ModelLauncher() {
        super();
        _actions = new HashMap<>();
        _rules = new LinkedList<>();
        this.init();
    }

    public ModelLauncher(final Object item) {
        super(item);
        _actions = new HashMap<>();
        _rules = new LinkedList<>();
        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String uid() {
        return super.getString(FLD_UID);
    }

    public String exec() {
        return super.getString(FLD_EXEC);
    }

    public Collection<ModelRule> rules() {
        return _rules;
    }

    public Map<String, ModelAction> actions() {
        return _actions;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        // rules
        final JSONArray rules = super.getJSONArray(FLD_RULES);
        CollectionUtils.forEach(rules, (item) -> {
            _rules.add(new ModelRule(item));
        });

        // actions
        final JSONObject actions = super.getJSONObject(FLD_ACTIONS);
        final Set<String> names = actions.keySet();
        for (final String name : names) {
            _actions.put(name, new ModelAction(actions.get(name)));
        }
    }


}
