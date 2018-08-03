package org.lyj.commons.nlp.controllers.domain;

import java.util.LinkedList;

/**
 *
 */
public class DomainMatchList
        extends LinkedList<DomainMatch> {

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public DomainMatchList() {

    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------


    public void sort() {
        super.sort((o1, o2) -> {
            if (o1.rating() < o2.rating()) {
                return 1;
            } else if (o1.rating() > o2.rating()) {
                return -1;
            }
            return 0;
        });
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


}
