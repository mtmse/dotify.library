package org.daisy.dotify.translator.impl.liblouis;


import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * TODO: write java doc.
 */
public class LiblouisBrailleFilterTest {
    /*
    @Test
    public void testToTypeForm_01() {
        TextAttribute ta = new DefaultTextAttribute.Builder()
                .add(5)
                .add(
                    new DefaultTextAttribute.Builder("italic")
                    .add(2)
                    .add(new DefaultTextAttribute.Builder("bold").build(2))
                    .build(4))
                .add(new DefaultTextAttribute.Builder("bold").build(5))
                .build(14);
        Map<String, Integer> dict = new HashMap<>();
        dict.put("italic", 1);
        dict.put("underline", 2);
        dict.put("bold", 4);
        short[] expecteds = new short[] {0,0,0,0,0,1,1,5,5,4,4,4,4,4};
        short[] actuals = LiblouisBrailleFilter.toTypeForm(ta, dict);
        assertArrayEquals(expecteds, actuals);
    }

    @Test
    public void testToTypeForm_02() {
        TextAttribute ta = new DefaultTextAttribute.Builder()
                .add(5)
                .add(new DefaultTextAttribute.Builder("em").add(4).build(4))
                .add(5)
                .build(14);
        Map<String, Integer> dict = new HashMap<>();
        dict.put("em", 1);
        short[] expecteds = new short[] {0,0,0,0,0,1,1,1,1,0,0,0,0,0};
        short[] actuals = LiblouisBrailleFilter.toTypeForm(ta, dict);
        assertArrayEquals(expecteds, actuals);
    }

    @Test
    public void testToTypeForm_03() {
        TextAttribute ta = new DefaultTextAttribute.Builder("strong")
                .add(5)
                .add(new DefaultTextAttribute.Builder("em").add(4).build(4))
                .add(5)
                .build(14);
        Map<String, Integer> dict = new HashMap<>();
        dict.put("em", 1);
        dict.put("strong", 2);
        short[] expecteds = new short[] {2,2,2,2,2,3,3,3,3,2,2,2,2,2};
        short[] actuals = LiblouisBrailleFilter.toTypeForm(ta, dict);
        assertArrayEquals(expecteds, actuals);
    }*/

    @Test
    public void testToLiblouisSpecification_01() {
        String input = "hyphenate";
        String hyph = "hy\u00adphen\u00adate";
        LiblouisTranslatable hp = LiblouisBrailleFilter.toLiblouisSpecification(hyph, input);
        assertEquals(input, hp.getText());
        assertArrayEquals(new int[]{0, 1, 0, 0, 0, 1, 0, 0}, hp.getInterCharAtts());
    }

    @Test
    public void testToLiblouisSpecification_02() {
        String input = "- - -"; //002d, 0020, 002d, 0020, 002d
        String hyph = "-\u200b -\u200b -"; //002d, 200b, 0020, 002d, 200b, 0020, 002d
        LiblouisTranslatable hp = LiblouisBrailleFilter.toLiblouisSpecification(hyph, input);
        assertEquals(input, hp.getText());
        assertArrayEquals(new int[]{2, 0, 2, 0}, hp.getInterCharAtts());
    }

    @Test
    public void testToBrailleFilterString() {
        String input = "hyphenate";
        String hyph = "hy\u00adphen\u00adate";
        String res = LiblouisBrailleFilter.toBrailleFilterString(input, input,
            new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8}, new int[]{0, 1, 0, 0, 0, 1, 0, 0}, 0
        );
        assertEquals(hyph, res);
    }

    @Test
    public void testToBrailleFilterStringAppendsTrailingSoftHyphen() {
        // A soft hyphen at the very end of the hyphenated source cannot be encoded in
        // interCharAttr (which only covers positions between two cells) \u2014 it must be
        // carried through as a trailing attribute and appended after the last cell.
        String input = "ab";
        String res = LiblouisBrailleFilter.toBrailleFilterString(input, input,
            new int[]{0, 1}, new int[]{0}, 1  // 1 == LIBLOUIS_SOFT_HYPEN
        );
        assertEquals("ab\u00ad", res);
    }

    @Test
    public void testToLiblouisSpecificationCapturesTrailingSoftHyphen() {
        // hyphStr ends with a soft hyphen after the last input character; that break
        // candidate must surface as the LiblouisTranslatable's trailingAtt rather than
        // being silently dropped.
        String input = "ab";
        String hyph = "ab\u00ad";
        LiblouisTranslatable hp = LiblouisBrailleFilter.toLiblouisSpecification(hyph, input);
        assertEquals(input, hp.getText());
        assertArrayEquals(new int[]{0}, hp.getInterCharAtts());
        assertEquals(1, hp.getTrailingAtt());  // 1 == LIBLOUIS_SOFT_HYPEN
    }

    @Test
    public void testToLiblouisSpecificationHandlesRepeatedCharsWithSoftHyphensBetween() {
        // Regression test for an algorithm bug that previously dropped soft hyphens
        // between two identical input characters. The earlier "look-ahead" form
        // exited immediately at i=0 because cpHyph[j] already matched cpInput[i+1]
        // (both being '\u2824' = \u2824), so it never observed the '\u00ad' in between.
        //
        // This shape appears for OBFL block content that contains pre-rendered
        // braille cells with embedded soft hyphens, e.g.
        //   <block>\u2824\u00ad\u2824\u00ad\u2824</block>
        String input = "\u2824\u2824\u2824";          // \u2824\u2824\u2824
        String hyph  = "\u2824\u00ad\u2824\u00ad\u2824"; // \u2824\u00ad\u2824\u00ad\u2824
        LiblouisTranslatable hp = LiblouisBrailleFilter.toLiblouisSpecification(hyph, input);
        assertEquals(input, hp.getText());
        assertArrayEquals(new int[]{1, 1}, hp.getInterCharAtts()); // both = LIBLOUIS_SOFT_HYPEN
        assertEquals(0, hp.getTrailingAtt());
    }


    @Test
    public void testToBrailleFilterStringShouldNotCrash() {
        LiblouisBrailleFilter.toLiblouisSpecification("", "");
    }
}
