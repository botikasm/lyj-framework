package org.lyj.ext.db.arango.query;

import org.junit.Test;

/**
 * Created by angelogeminiani on 03/07/17.
 */
public class ArnQueryTest {


    @Test
    public void FOR() throws Exception {
        final ArnQuery query = new ArnQuery();
        query.FOR("users")
                .FOR("accounts")
                .FILTER("t1.name=@name")
                .LET("sumAge", "t1.age + t2.age")
                .RETURN("{" +
                        "sumAge:sumAge" +
                        "}");

        System.out.println(query.toString());

    }

}