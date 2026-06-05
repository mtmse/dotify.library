package org.daisy.dotify.hyphenator.impl;

import org.daisy.dotify.api.hyphenator.HyphenatorConfigurationException;
import org.junit.Assume;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * TODO: write java doc.
 */
public class JHypenatorTest {

    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

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
    public void testHyphenate() throws HyphenatorConfigurationException {
        Assume.assumeTrue(new File("/usr/share/hyphen/hyph_sv_SE.dic").exists());

        JHyphenator jHyphenator = new JHyphenator("sv");

        assertEquals("in", jHyphenator.hyphenate("in"));
        assertEquals("test", jHyphenator.hyphenate("test"));
        assertEquals("tes\u00ADtar", jHyphenator.hyphenate("testar"));
        assertEquals(
                "tes\u00ADtar i do\u00ADti\u00ADfy",
                jHyphenator.hyphenate("testar i dotify")
        );

        assertEquals("  No\u00ADte   36  ", jHyphenator.hyphenate("  Note   36  "));

    }
}
