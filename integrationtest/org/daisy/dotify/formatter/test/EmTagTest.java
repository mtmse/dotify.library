package org.daisy.dotify.formatter.test;

import org.daisy.dotify.api.engine.LayoutEngineException;
import org.daisy.dotify.api.writer.PagedMediaWriterConfigurationException;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

/**
 * Testing the emphasis tag and how it works together with punctuation, we should not add an extra space
 * before punctuations.
 */
public class EmTagTest extends AbstractFormatterEngineTest {

    @Test
    public void testEmStyleTagAtTheEndOfASentence() throws
            LayoutEngineException,
            IOException,
            PagedMediaWriterConfigurationException {
        testPEF(
                "resource-files/em-punctuation-input.obfl",
                "resource-files/em-punctuation-expected.pef",
                false
        );
    }

    /**
     * A whole sentence is wrapped in an em tag, with the final period inside the span.
     * The phrase wraps across rows; the end-marker must follow the period
     * (…⠞⠕⠗⠊⠑⠗⠄⠱), matching the legacy translator and MTM §3.4.2.
     */
    @Test
    public void testEmStyleTagSpanningSentenceWithPeriodInside() throws
            LayoutEngineException,
            IOException,
            PagedMediaWriterConfigurationException {
        testPEF(
                "resource-files/em-punctuation-inside-input.obfl",
                "resource-files/em-punctuation-inside-expected.pef",
                false
        );
    }
}
