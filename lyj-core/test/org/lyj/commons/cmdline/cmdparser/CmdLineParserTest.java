package org.lyj.commons.cmdline.cmdparser;

import org.junit.Test;
import org.lyj.commons.util.StringUtils;

import java.util.Vector;

/**
 * Test the parser
 */
public class CmdLineParserTest {

    private static final String[] CMD1 = new String[]{"-c", "value1", "--test", "value2 with space", "remaining"};

    @Test
    public void parse() throws Exception {
        final CmdLineParser parser = new CmdLineParser();

        CmdLineParser.Option optC = parser.addStringOption('c', "command");
        CmdLineParser.Option optT = parser.addStringOption('t', "test");

        parser.parse(CMD1);

        String c = (String)parser.getOptionValue(optC);
        String t = (String)parser.getOptionValue(optT);

        System.out.println("-c: " + c);
        System.out.println("-t: " + t);

        String[] args = parser.getRemainingArgs();
        System.out.println("ARGS: " + StringUtils.toString(args));
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------


}