package org.daisy.dotify.hyphenator.impl;

import org.daisy.dotify.api.hyphenator.HyphenatorConfigurationException;
import org.daisy.dotify.api.hyphenator.HyphenatorInterface;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JHypenatorTest {

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    @Test
    public void testBeginEndHandling() throws HyphenatorConfigurationException {
        JHyphenator jHyphenator = new JHyphenator("sv");

        assertEquals("test\u00ADar", jHyphenator.ensureStartAndEnd("t\u00ADest\u00ADa\u00ADr"));
        assertEquals("test", jHyphenator.ensureStartAndEnd("t\u00ADest"));
        assertEquals("in", jHyphenator.ensureStartAndEnd("i\u00ADn"));
        assertEquals(
            "testar i do\u00ADtify",
            jHyphenator.ensureStartAndEnd("test\u00ADar i do\u00ADtif\u00ADy")
        );
    }

    @Test
    public void testHyphenate() throws HyphenatorConfigurationException {
        JHyphenator jHyphenator = new JHyphenator("sv");

        //System.out.println(bytesToHex(jHyphenator.hyphenate("testar").getBytes()));

        assertEquals("tes\u00ADtar", jHyphenator.hyphenate("testar"));
        assertEquals("test", jHyphenator.hyphenate("test"));
        assertEquals("in", jHyphenator.hyphenate("in"));
        assertEquals(
                "tes\u00ADtar i do\u00ADtify",
                jHyphenator.hyphenate("testar i dotify")
        );
    }
}
