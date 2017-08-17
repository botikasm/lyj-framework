package org.lyj.commons.util.calendar;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lyj.commons.util.*;
import org.lyj.commons.util.converters.MapConverter;

import java.text.DateFormatSymbols;
import java.util.Locale;
import java.util.Map;

/**
 * Daily Time Slot wrapper.
 * i.e. "sun 8.30 12.30" (sunday from 8.30am to 12.30am)
 */
public class DaySlots {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String FLD_DAYOFWEEK = "day_of_week";
    private static final String FLD_SLOTS = "slots";
    private static final String FLD_FROM = "from";
    private static final String FLD_TO = "to";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Locale _locale;
    private final JSONObject _item;
    private final String[] _short_week_days;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public DaySlots(final String lang) {
        this(LocaleUtils.getLocaleByLang(lang));
    }

    public DaySlots(final Locale locale) {
        this(locale, null);
    }

    public DaySlots(final String lang,
                    final String json) {
        this(LocaleUtils.getLocaleByLang(lang), json);
    }

    public DaySlots(final Locale locale,
                    final String json) {
        _locale = locale;
        _item = new JSONObject(StringUtils.isJSONObject(json) ? json : "{}");
        final DateFormatSymbols symbols = new DateFormatSymbols(_locale);
        _short_week_days = symbols.getShortWeekdays();

        this.init();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        final int index = this.dayOfWeek();
        if (index > -1) {
            sb.append(_short_week_days[index]);
            final JSONArray slots = this.slots();
            CollectionUtils.forEach(slots, (slot) -> {
                final JsonItem jslot = new JsonItem(slot);
                sb.append(" ");

                sb.append(FormatUtils.formatDouble(jslot.getDouble(FLD_FROM), "#.00"));
                sb.append(" ");
                sb.append(FormatUtils.formatDouble(jslot.getDouble(FLD_TO), "#.00"));
            });
        }
        return sb.toString();
    }

    // ------------------------------------------------------------------------
    //                     p u b l i c
    // ------------------------------------------------------------------------

    public JSONObject json() {
        return _item;
    }

    public Map<String, Object> map() {
        return MapConverter.toMap(this.json());
    }

    public boolean isValid() {
        return this.dayOfWeek() > 0 && this.slots().length() > 0;
    }

    public String[] shortWeekDays() {
        return _short_week_days;
    }

    public DaySlots clear() {
        this.init();
        return this;
    }

    public DaySlots parse(final String text) {
        this.clear();
        if (StringUtils.isJSONObject(text)) {
            this.parseJSON(text);
        } else {
            this.parseText(text);
        }
        return this;
    }

    public int dayOfWeek() {
        return _item.optInt(FLD_DAYOFWEEK, -1);
    }

    public DaySlots dayOfWeek(final int value) {
        _item.put(FLD_DAYOFWEEK, value);
        return this;
    }

    public DaySlots dayOfWeek(final String value) {
        _item.put(FLD_DAYOFWEEK, this.indexOf(value));
        return this;
    }

    /**
     * Returns array of time slots.
     * i.e. "{from:8.30, to:10.30}"
     *
     * @return
     */
    public JSONArray slots() {
        if (!_item.has(FLD_SLOTS)) {
            _item.put(FLD_SLOTS, new JSONArray());
        }
        return _item.getJSONArray(FLD_SLOTS);
    }

    // ------------------------------------------------------------------------
    //                      c o m p a r e
    // ------------------------------------------------------------------------

    public boolean equals(final DaySlots other) {
        return other.json().toString().equalsIgnoreCase(this.json().toString());
    }

    /**
     * Return:
     * -2 = not compatible slots
     * -1 = other slot is lower and is contained from current slot (inside)
     * 0 = equals
     * 1 = other slot is grater and contains current slot (outside)
     *
     * @param other Daily slot to match with current slot
     * @return Compatibility index.
     */
    public int match(final DaySlots other) {
        if (other.dayOfWeek() == this.dayOfWeek()) {
            return this.matchSlots(other.slots());
        }
        return -2;
    }

    /**
     * Return true if current slots are contained into other slots
     *
     * @param other Other Daily Slot
     * @return true id all slots of curretn DaylySlot are contained into other's slots
     */
    public boolean insideOf(final DaySlots other) {
        return this.match(other) == 1;
    }

    /**
     * Return true if current slots contains other slots
     *
     * @param other Other Daily Slot
     * @return true id all slots of curretn DaylySlot contains other's slots
     */
    public boolean outsideOf(final DaySlots other) {
        return this.match(other) == -1;
    }

    /**
     * Returns array of matching slots
     */
    public JSONArray selectInside(final DaySlots other) {
        final JSONArray response = new JSONArray();
        if (other.dayOfWeek() == this.dayOfWeek()) {
            this.selectMatching(this.slots(), other.slots(), 1, response);
        }
        return response;
    }

    /**
     * Returns array of matching slots
     */
    public JSONArray selectOuside(final DaySlots other) {
        final JSONArray response = new JSONArray();
        if (other.dayOfWeek() == this.dayOfWeek()) {
            this.selectMatching(this.slots(), other.slots(), -1, response);
        }
        return response;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        _item.put(FLD_DAYOFWEEK, -1);
        _item.put(FLD_SLOTS, new JSONArray());


    }

    private int indexOf(final String day_of_week) {
        if (StringUtils.hasText(day_of_week)) {
            int i = 0;
            for (final String day : _short_week_days) {
                if (StringUtils.hasText(day) && day_of_week.toLowerCase().startsWith(day.toLowerCase())) {
                    return i;
                }
                i++;
            }
        }
        return -1;
    }

    private void parseText(final String text) {
        final String[] tokens = StringUtils.split(text, new String[]{" ", "-"});
        if (tokens.length > 1) {
            final int index = this.indexOf(tokens[0]); // first is the day of week
            if (index > -1) {
                // set day of week
                this.dayOfWeek(index);

                // parse slots step of 2
                int count = 0;
                for (int i = 1; i < tokens.length; i++) {
                    // odd
                    if (i % 2 != 0) {
                        count++;
                        final String from = CollectionUtils.get(tokens, i);
                        final String to = CollectionUtils.get(tokens, i + 1);
                        this.parseSlot(from, to, count);
                    }
                }
            }
        }
    }

    private void parseJSON(final String json) {
        final JsonItem item = new JsonItem(json);
        if (item.has(FLD_DAYOFWEEK)) {
            this.dayOfWeek(item.getInt(FLD_DAYOFWEEK));
        }
        if (item.has(FLD_SLOTS)) {
            _item.put(FLD_SLOTS, item.getJSONArray(FLD_SLOTS));
        }
    }

    private void parseSlot(final String from, final String to, final int count) {
        final double dfrom = ConversionUtils.toDouble(from, 2);
        final double dto = ConversionUtils.toDouble(StringUtils.hasText(to) ? to : "24", 2);

        final JSONObject slot = new JSONObject();
        slot.put(FLD_FROM, dfrom);
        slot.put(FLD_TO, dto);

        this.slots().put(slot);
    }

    /**
     * Return:
     * -1 = current contains other_slots - other slot is lower and is contained from current slot (inside)
     * 0 = equals
     * 1 =  other_slots contains current - other slot is grater and contains current slot (outside)
     */
    private int matchSlots(final JSONArray other_slots) {
        final JSONArray slots = this.slots();
        if (slots.length() == other_slots.length()) {
            if (slots.toString().equalsIgnoreCase(other_slots.toString())) {
                return 0; // EQUALS
            }

            final JSONObject[] aslots = JsonWrapper.toArrayOfJSONObject(slots);
            final JSONObject[] aother_slots = JsonWrapper.toArrayOfJSONObject(other_slots);
            int check = -2;
            for (int i = 0; i < aslots.length; i++) {
                final JSONObject slot1 = aslots[i];
                final JSONObject slot2 = aother_slots[i];
                if (i == 0) {
                    check = this.matchSlot(slot1, slot2);
                } else {
                    if (check != this.matchSlot(slot1, slot2)) {
                        return -2;
                    }
                }
            }
            return check;
        }
        return -2; // does not match at all
    }

    /**
     * Returns:
     * -2 = no match
     * -1 = slot1 contains slot2
     * 0 = equals
     * 1 = slot2 contains slot1
     */
    private int matchSlot(final JSONObject slot1,
                          final JSONObject slot2) {
        if (slot1.toString().equalsIgnoreCase(slot2.toString())) {
            return 0; // EQUALS
        }
        final double from1 = slot1.optDouble(FLD_FROM);
        final double to1 = slot1.optDouble(FLD_TO);
        final double from2 = slot2.optDouble(FLD_FROM);
        final double to2 = slot2.optDouble(FLD_TO);

        if (from1 >= from2 && to1 <= to2) {
            return 1; // slot 1 INSIDE slot 2: slot2 contains slot1
        }
        if (from1 <= from2 && to1 >= to2) {
            return -1; // slot 1 OUTSIDE slot 2: slot1 contains slot2
        }

        return -2;
    }

    private void selectMatching(final JSONArray slots,
                                final JSONArray other_slots,
                                final int matching_criteria,
                                final JSONArray matching) {
        final JSONObject[] aslots = JsonWrapper.toArrayOfJSONObject(slots);
        final JSONObject[] aother_slots = JsonWrapper.toArrayOfJSONObject(other_slots);

        final int len = slots.length();
        for (int i = 0; i < len; i++) {
            final JSONObject slot1 = aslots[i];
            for (final JSONObject slot2 : aother_slots) {
                if (this.matchSlot(slot1, slot2) == matching_criteria) {
                    matching.put(new JSONObject(slot1.toString())); // add clone
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static DaySlots create(final Locale locale) {
        return new DaySlots(locale);
    }

    public static DaySlots create(final String lang) {
        return new DaySlots(lang);
    }

}
