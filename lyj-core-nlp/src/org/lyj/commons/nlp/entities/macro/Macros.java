package org.lyj.commons.nlp.entities.macro;

import org.lyj.commons.nlp.entities.macro.simple.*;
import org.lyj.commons.nlp.entities.macro.withparams.*;
import org.lyj.commons.util.ClassLoaderUtils;
import org.lyj.commons.util.CollectionUtils;
import org.lyj.commons.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class Macros {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String PREFIX_MACRO = "#"; // macro command prefix

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Map<String, Class<? extends AbstractEntityMacro>> _classes;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private Macros() {
        _classes = new HashMap<>();
        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void register(final String key,
                         final Class<? extends AbstractEntityMacro> aclass) {
        _classes.put(key, aclass);
    }

    public AbstractEntityMacro get(final String name) {
        try {
            final String raw_name = name.startsWith(PREFIX_MACRO) ? name.substring(1) : name;
            final String[] tokens = StringUtils.split(raw_name, PREFIX_MACRO); // params are separated with #
            final String key = tokens[0]; // macro name is first
            if (_classes.containsKey(key)) {
                final String[] args = CollectionUtils.subArray(tokens, 1, tokens.length - 1);
                final Class<? extends AbstractEntityMacro> aclass = _classes.get(key);
                return (AbstractEntityMacro) ((args.length > 0)
                        ? ClassLoaderUtils.newInstance(aclass, new Object[]{args})
                        : ClassLoaderUtils.newInstance(aclass));
            }
        } catch (Throwable ignored) {
            // ignored
        }
        return null;
    }

    public String[] parse(final String name,
                          final String lang,
                          final int start_index,
                          final String[] phrase) {
        final AbstractEntityMacro macro = this.get(name);
        if (null != macro) {
            return macro.parse(lang, start_index, phrase);
        }
        return new String[0];
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {

        //-- REGISTER MACRO --//
        this.register(EntityMacroName.NAME, EntityMacroName.class);
        this.register(EntityMacroEmail.NAME, EntityMacroEmail.class);
        this.register(EntityMacroNumber.NAME, EntityMacroNumber.class);
        this.register(EntityMacroInteger.NAME, EntityMacroInteger.class);
        this.register(EntityMacroPhone.NAME, EntityMacroPhone.class);
        this.register(EntityMacroVat.NAME, EntityMacroVat.class);
        this.register(EntityMacroSocialSecurityNumber.NAME, EntityMacroSocialSecurityNumber.class);

        // require parameters in contructor
        this.register(EntityMacroStartsWith.NAME, EntityMacroStartsWith.class);
        this.register(EntityMacroStartsWithExp.NAME, EntityMacroStartsWithExp.class);
        this.register(EntityMacroEndsWith.NAME, EntityMacroEndsWith.class);
        this.register(EntityMacroContains.NAME, EntityMacroContains.class);
        this.register(EntityMacroIntegerLen.NAME, EntityMacroIntegerLen.class);
        this.register(EntityMacroIntegerLenG.NAME, EntityMacroIntegerLenG.class);
        this.register(EntityMacroIntegerLenL.NAME, EntityMacroIntegerLenL.class);
        this.register(EntityMacroLen.NAME, EntityMacroLen.class);
        this.register(EntityMacroLenG.NAME, EntityMacroLenG.class);
        this.register(EntityMacroLenL.NAME, EntityMacroLenL.class);
        this.register(EntityMacroExp.NAME, EntityMacroExp.class);
        this.register(EntityMacroDate.NAME, EntityMacroDate.class);
    }

    // ------------------------------------------------------------------------
    //                      S I N G L E T O N
    // ------------------------------------------------------------------------


    private static Macros __instance;

    public static synchronized Macros instance() {
        if (null == __instance) {
            __instance = new Macros();
        }
        return __instance;
    }

}
