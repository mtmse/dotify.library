package org.daisy.dotify.translator.impl.liblouis;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.daisy.dotify.api.hyphenator.HyphenatorFactoryMaker;
import org.daisy.dotify.api.hyphenator.HyphenatorFactoryMakerService;
import org.daisy.dotify.api.translator.BrailleTranslator;
import org.daisy.dotify.api.translator.BrailleTranslatorFactoryService;
import org.daisy.dotify.api.translator.BrailleTranslatorResult;
import org.daisy.dotify.api.translator.DefaultTextAttribute;
import org.daisy.dotify.api.translator.TextAttribute;
import org.daisy.dotify.api.translator.Translatable;
import org.daisy.dotify.api.translator.TranslationException;
import org.daisy.dotify.api.translator.TranslatorConfigurationException;
import org.daisy.dotify.api.translator.TranslatorType;
import org.daisy.dotify.translator.impl.sv.SwedishBrailleTranslatorFactoryService;
import org.junit.Test;

/**
 * Side-by-side comparison harness for emphasis (em/strong) + punctuation
 * placement. Runs the same TextAttribute tree through both the legacy
 * SwedishBrailleTranslator and the new liblouis-based translator, and prints
 * both outputs so we can compare against the MTM spec §3.4.
 *
 * This test does not assert any particular output — its purpose is to surface
 * what each code path produces today, given a matrix of styled-span shapes
 * with varying punctuation positions. Once we have data we'll decide which
 * outputs are spec-correct and turn the matrix into assertions.
 */
public class EmphasisPunctuationMatrixTest {

    /** Where to dump the comparison matrix (system property override available). */
    private static final Path REPORT_PATH = Paths.get(
        System.getProperty("emphasis.matrix.report", "/tmp/em-strong-matrix.txt"));

    private final BrailleTranslator legacy;
    private final BrailleTranslator liblouis;
    private PrintWriter report;

    public EmphasisPunctuationMatrixTest() throws TranslatorConfigurationException {
        String locale = "sv-SE";
        String mode = TranslatorType.UNCONTRACTED.toString();
        HyphenatorFactoryMakerService hyphenatorService = HyphenatorFactoryMaker.newInstance();

        SwedishBrailleTranslatorFactoryService legacySvc = new SwedishBrailleTranslatorFactoryService();
        legacySvc.setHyphenator(hyphenatorService);
        this.legacy = legacySvc.newFactory().newTranslator(locale, mode);

        LiblouisBrailleTranslatorFactoryService liblouisSvc = new LiblouisBrailleTranslatorFactoryService();
        liblouisSvc.setHyphenator(hyphenatorService);
        this.liblouis = liblouisSvc.newFactory().newTranslator(locale, mode);
    }

    @Test
    public void printMatrix() throws TranslationException, IOException {
        report = new PrintWriter(Files.newBufferedWriter(REPORT_PATH, StandardCharsets.UTF_8));
        try {
            line("");
            line("=== em/strong + punctuation placement matrix ===");
            line("(legacy = sv_SE-pas2 + SwedishMarkerProcessorFactory;"
                + " liblouis = sv-mtm-g0.utb staged)");
            line("");
            runAll();
            line("");
            report.flush();
        } finally {
            report.close();
        }
        System.out.println("Matrix written to " + REPORT_PATH);
    }

    private void runAll() throws TranslationException {

        // <em>ord</em>.   — single-word em, period OUTSIDE
        run("single-word em, period OUTSIDE", "ord.",
            new DefaultTextAttribute.Builder()
                .add(new DefaultTextAttribute.Builder("em").build(3))
                .add(1)
                .build(4));

        // <em>ord.</em>   — single-word em, period INSIDE
        run("single-word em, period INSIDE", "ord.",
            new DefaultTextAttribute.Builder()
                .add(new DefaultTextAttribute.Builder("em").build(4))
                .build(4));

        // <em>två ord</em>,   — multi-word em, comma OUTSIDE
        run("multi-word em, comma OUTSIDE", "två ord,",
            new DefaultTextAttribute.Builder()
                .add(new DefaultTextAttribute.Builder("em").build(7))
                .add(1)
                .build(8));

        // <em>två ord,</em>   — multi-word em, comma INSIDE
        run("multi-word em, comma INSIDE", "två ord,",
            new DefaultTextAttribute.Builder()
                .add(new DefaultTextAttribute.Builder("em").build(8))
                .build(8));

        // <em>en längre fras med flera ord</em>,
        // The "röra" pattern from the regression suite: long em phrase with
        // trailing punctuation outside the em.
        String longPhrase = "en längre fras med flera ord,";
        run("long em phrase, comma OUTSIDE (röra-pattern)", longPhrase,
            new DefaultTextAttribute.Builder()
                .add(new DefaultTextAttribute.Builder("em").build(28))
                .add(1)
                .build(29));

        // <em>en längre fras med flera ord,</em>
        run("long em phrase, comma INSIDE", longPhrase,
            new DefaultTextAttribute.Builder()
                .add(new DefaultTextAttribute.Builder("em").build(29))
                .build(29));

        // <strong>fet</strong>.
        run("single-word strong, period OUTSIDE", "fet.",
            new DefaultTextAttribute.Builder()
                .add(new DefaultTextAttribute.Builder("strong").build(3))
                .add(1)
                .build(4));

        // <strong>två feta</strong>,
        run("multi-word strong, comma OUTSIDE", "två feta,",
            new DefaultTextAttribute.Builder()
                .add(new DefaultTextAttribute.Builder("strong").build(8))
                .add(1)
                .build(9));

        // <strong>två feta,</strong>
        run("multi-word strong, comma INSIDE", "två feta,",
            new DefaultTextAttribute.Builder()
                .add(new DefaultTextAttribute.Builder("strong").build(9))
                .build(9));

        // Han sa <em>nej</em>.   — em mid-sentence, period not adjacent
        // "Han sa " (7) + "nej" (3) + "." (1) = 11
        run("em mid-sentence, period after non-em text", "Han sa nej.",
            new DefaultTextAttribute.Builder()
                .add(7)
                .add(new DefaultTextAttribute.Builder("em").build(3))
                .add(1)
                .build(11));

        // <em>ord</em> mer text   — multi-word em, no trailing punctuation
        run("em followed by plain text, no punct", "ord mer text",
            new DefaultTextAttribute.Builder()
                .add(new DefaultTextAttribute.Builder("em").build(3))
                .add(9)
                .build(12));

        // Caps controls (no TextAttribute styling — caps comes from letter
        // case alone). Spec §3.2.2 / §3.2.3 prescribes ⠱ end-marker BEFORE
        // the trailing punctuation in these patterns.
        run("caps phrase, hyphen-suffix (ISBN-centralen)", "ISBN-centralen", null);
        run("caps phrase, colon-suffix (SACO:s)", "SACO:s", null);
        run("caps phrase, period-suffix (IKEA.)", "IKEA.", null);
    }

    /**
     * Translate {@code text} through both translators and write the results
     * to the report. If {@code atts} is null, no text-attribute styling is
     * applied (useful for caps controls where the styling comes from letter
     * case alone).
     */
    private void run(String label, String text, TextAttribute atts) throws TranslationException {
        String legacyOut = translate(legacy, text, atts);
        String liblouisOut = translate(liblouis, text, atts);
        String agreement = legacyOut.equals(liblouisOut) ? "    " : "DIFF";
        line(String.format("%-50s  %s", label, agreement));
        line(String.format("  input    | %s", text));
        line(String.format("  legacy   | %s", legacyOut));
        line(String.format("  liblouis | %s", liblouisOut));
        line("");
    }

    private void line(String s) {
        report.println(s);
    }

    private static String translate(BrailleTranslator t, String text, TextAttribute atts)
        throws TranslationException {
        Translatable.Builder b = Translatable.text(text);
        if (atts != null) {
            b = b.attributes(atts);
        }
        BrailleTranslatorResult r = t.translate(b.build());
        return r.getTranslatedRemainder();
    }
}
