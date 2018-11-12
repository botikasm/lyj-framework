package org.lyj.commons.util;

import org.json.JSONArray;
import org.lyj.commons.util.json.JsonItem;

import java.util.LinkedList;
import java.util.List;

/**
 * Handle a sequence of numbers from min to max and return a random value peeking
 * from sequence.
 * Uses three different modes:
 * 0 = sequential (ordered from start to end. stops on last repeating always last)
 * 1 = loop (ordered from start to end. restart from first)
 * 2 = rnd (random from start to end. restart from first)
 */
public class Randomizer
        extends JsonItem {

    public static final int MODE_SEQUENTIAL = 0;
    public static final int MODE_LOOP = 1;
    public static final int MODE_RND = 2;

    public Randomizer(final int mode,
                      final int min,
                      final int max) {
        super();
        this.init(mode, min, max);
    }

    public Randomizer(final Object value) {
        super(value);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void init(final int mode,
                     final int min,
                     final int max) {
        this.mode(mode);
        this.initSeries(min, max);
        this.initRemain();
        this.used(new JSONArray());
    }

    public void clear() {
        this.initRemain(); // remain all
        this.used(new JSONArray()); // no used
    }

    public int getMode() {
        return this.mode();
    }

    public int getLength() {
        return this.series().length();
    }

    public int next() {
        final int mode = this.mode();

        final int response;
        if (mode == MODE_SEQUENTIAL) {
            // stop on last
            response = this.nextSequence();
        } else if (mode == MODE_LOOP) {
            // loop
            response = this.nextLoop();
        } else if (mode == MODE_RND) {
            // rnd, but avoid duplicates
            response = this.nextRnd();
        } else {
            // rnd, but avoid duplicates
            response = this.nextRnd();
        }

        return response;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void initSeries(final int min, final int max) {
        final JSONArray series = new JSONArray();
        for (int i = min; i < max + 1; i++) {
            series.put(i);
        }
        this.series(series);
    }

    private void initRemain() {
        final JSONArray series = this.series();
        final JSONArray remain = new JSONArray();
        CollectionUtils.forEach(series, (item) -> {
            try {
                final int value = ConversionUtils.toInteger(item);
                remain.put(value);
            } catch (Throwable ignored) {
            }
        });
        this.remain(remain);
    }

    private int nextLoop() {
        if (this.remain().length() == 0) {
            this.clear();
        }

        int response = 0;
        final List<Integer> remain = this.toList(this.remain());
        final int index = 0;
        response = remain.remove(index);
        this.used().put(response);
        // save remain
        this.remain(remain);

        return response;
    }

    private int nextSequence() {
        int response = 0;
        final List<Integer> remain = this.toList(this.remain());
        final int index = 0;
        if (remain.size() == 1) {
            response = remain.get(index);
        } else {
            response = remain.remove(index);
            this.used().put(response);
            // save remain
            this.remain(remain);
        }

        return response;
    }

    private int nextRnd() {
        if (this.remain().length() == 0) {
            this.clear();
        }

        int response = 0;
        final List<Integer> remain = this.toList(this.remain());
        final int index = (int) RandomUtils.rnd(0, remain.size() - 1);
        response = remain.remove(index);
        this.used().put(response);
        // save remain
        this.remain(remain);

        return response;
    }

    private void mode(final int value) {
        super.put("mode", value);
    }

    private int mode() {
        return super.getInt("mode", MODE_RND);
    }

    private List<Integer> toList(final JSONArray jarray) {
        final List<Integer> response = new LinkedList<>();
        CollectionUtils.forEach(jarray, (value) -> {
            response.add(ConversionUtils.toInteger(value));
        });
        return response;
    }

    private void series(final JSONArray value) {
        super.put("series", value);
    }

    private JSONArray series() {
        if (!super.has("series")) {
            super.put("series", new JSONArray());
        }
        return super.getJSONArray("series");
    }

    private void used(final JSONArray value) {
        super.put("used", value);
    }

    private JSONArray used() {
        if (!super.has("used")) {
            super.put("used", new JSONArray());
        }
        return super.getJSONArray("used");
    }

    private void remain(final JSONArray value) {
        super.put("remain", value);
    }

    private JSONArray remain() {
        if (!super.has("remain")) {
            super.put("remain", new JSONArray());
        }
        return super.getJSONArray("remain");
    }

    private void remain(final List<Integer> values) {
        final JSONArray remain = new JSONArray();
        for (final int value : values) {
            remain.put(value);
        }
        this.remain(remain);
    }

}
