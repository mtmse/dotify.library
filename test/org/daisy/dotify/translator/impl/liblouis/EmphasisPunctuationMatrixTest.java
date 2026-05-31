package org.daisy.dotify.translator.impl.liblouis;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.daisy.dotify.api.hyphenator.HyphenatorFactoryMaker;
import org.daisy.dotify.api.hyphenator.HyphenatorFactoryMakerService;
import org.daisy.dotify.api.translator.BrailleTranslator;
import org.daisy.dotify.api.translator.BrailleTranslatorResult;
import org.daisy.dotify.api.translator.DefaultTextAttribute;
import org.daisy.dotify.api.translator.TextAttribute;
import org.daisy.dotify.api.translator.Translatable;
import org.daisy.dotify.api.translator.TranslationException;
import org.daisy.dotify.api.translator.TranslatorConfigurationException;
import org.daisy.dotify.api.translator.TranslatorType;
import org.daisy.dotify.translator.impl.sv.SwedishBrailleTranslatorFactoryService;
import org.junit.Test;

import static org.junit.Assert.fail;

/**
 * Regression coverage for emphasis (em/strong) + punctuation placement, the
 * design captured in BREAKING_CHANGES.md item 6 ("option C"). Each row
 * asserts the liblouis output against the spec-correct (or, where the spec
 * is silent, the documented trade-off) value, so a future table change or
 * Java change that breaks the placement surfaces here immediately.
 *
 * The legacy {@code SwedishBrailleTranslator} is also run for every case and
 * its output is written next to the liblouis output in the side-by-side
 * report file (see {@link #REPORT_PATH}). Those legacy values are <em>not</em>
 * asserted, but the liblouis path now matches the legacy placement for the
 * comma/period-INSIDE cases: when the punctuation carries the emphasis type
 * form, the end-marker ⠱ follows it (recovered from the attribute-derived
 * emphasis mask), preserving the source-markup boundary as the spec intends.
 *
 * Spec references are MTM <em>Svenska skrivregler för punktskrift</em>
 * (2009 / 2024 reissue), §3.4 <em>Tecken för stilsorter</em>.
 */
public class EmphasisPunctuationMatrixTest {

    /** Where to dump the side-by-side comparison (system property override available). */
    private static final Path REPORT_PATH = Paths.get(
        System.getProperty("emphasis.matrix.report", "/tmp/em-strong-matrix.txt"));

    private final BrailleTranslator legacy;
    private final BrailleTranslator liblouis;
    private PrintWriter report;
    private final List<String> failures = new ArrayList<>();

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
    public void emphasisPunctuationMatrix() throws TranslationException, IOException {
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

        if (!failures.isEmpty()) {
            StringBuilder msg = new StringBuilder(
                failures.size() + " liblouis output(s) did not match expectations:");
            for (String f : failures) {
                msg.append("\n  - ").append(f);
            }
            msg.append("\nSee ").append(REPORT_PATH).append(" for the full side-by-side report.");
            fail(msg.toString());
        }
    }

    private void runAll() throws TranslationException {

        // ===== single-word em =====

        // <em>ord</em>.  — spec §3.4.1: ⠠⠄ prefix, no end marker, period
        // immediately after the word with no spurious gap.
        run("single-word em, period OUTSIDE", "ord.",
            new DefaultTextAttribute.Builder()
                .add(new DefaultTextAttribute.Builder("em").build(3))
                .add(1)
                .build(4),
            "⠠⠄⠕⠗⠙⠄");

        // <em>ord.</em>  — degenerate: single-word emphasis whose extent
        // happens to cover the period. Liblouis can't tell this case apart
        // from the previous row in cell-stream terms, and both paths emit
        // the same output.
        run("single-word em, period INSIDE", "ord.",
            new DefaultTextAttribute.Builder()
                .add(new DefaultTextAttribute.Builder("em").build(4))
                .build(4),
            "⠠⠄⠕⠗⠙⠄");

        // ===== multi-word em =====

        // <em>två ord</em>,  — spec §3.4.2: phrase wrapped in ⠠⠤ … ⠱;
        // pass2 swaps the comma past the ⠱ to put the end-marker before
        // the punctuation (matches legacy and spec example p. 36).
        run("multi-word em, comma OUTSIDE", "två ord,",
            new DefaultTextAttribute.Builder()
                .add(new DefaultTextAttribute.Builder("em").build(7))
                .add(1)
                .build(8),
            "⠠⠤⠞⠧⠡⠀⠕⠗⠙⠱⠂");

        // <em>två ord,</em>  — comma INSIDE the em span. Because the comma
        // carries the em type form, the end-marker ⠱ goes AFTER it,
        // preserving the markup boundary as the legacy translator and the
        // spec (§3.4.2) do. The emphasis mask, derived from the attribute
        // tree, lets the liblouis pipeline recover this distinction.
        run("multi-word em, comma INSIDE", "två ord,",
            new DefaultTextAttribute.Builder()
                .add(new DefaultTextAttribute.Builder("em").build(8))
                .build(8),
            "⠠⠤⠞⠧⠡⠀⠕⠗⠙⠂⠱");

        // <em>en längre fras med flera ord</em>,  — the "röra"
        // regression pattern reported by the external test suite.
        String longPhrase = "en längre fras med flera ord,";
        run("long em phrase, comma OUTSIDE (röra-pattern)", longPhrase,
            new DefaultTextAttribute.Builder()
                .add(new DefaultTextAttribute.Builder("em").build(28))
                .add(1)
                .build(29),
            "⠠⠤⠑⠝⠀⠇⠜⠝⠛⠗⠑⠀⠋⠗⠁⠎⠀⠍⠑⠙⠀⠋⠇⠑⠗⠁⠀⠕⠗⠙⠱⠂");

        // <em>en längre fras med flera ord,</em>  — comma INSIDE the span;
        // end-marker goes after the comma, same as the multi-word case above.
        run("long em phrase, comma INSIDE", longPhrase,
            new DefaultTextAttribute.Builder()
                .add(new DefaultTextAttribute.Builder("em").build(29))
                .build(29),
            "⠠⠤⠑⠝⠀⠇⠜⠝⠛⠗⠑⠀⠋⠗⠁⠎⠀⠍⠑⠙⠀⠋⠇⠑⠗⠁⠀⠕⠗⠙⠂⠱");

        // <em>Till Sara Mondani, … historier.</em>  — the real-world
        // regression: the whole sentence, including the trailing period, is
        // inside the em span. The period is emphasized, so the end-marker ⠱
        // follows it (…⠄⠱). The internal comma after "Mondani" is followed by
        // a space, not the end-marker, so it is unaffected.
        String sentence = "Till Sara Mondani, som alltid kunde nysta fram historier.";
        run("whole-sentence em, period INSIDE", sentence,
            new DefaultTextAttribute.Builder()
                .add(new DefaultTextAttribute.Builder("em").build(sentence.length()))
                .build(sentence.length()),
            "⠠⠤⠠⠞⠊⠇⠇⠀⠠⠎⠁⠗⠁⠀⠠⠍⠕⠝⠙⠁⠝⠊⠂⠀⠎⠕⠍⠀⠁⠇⠇⠞⠊⠙⠀⠅⠥⠝⠙⠑⠀⠝⠽⠎⠞⠁⠀⠋⠗⠁⠍⠀⠓⠊⠎⠞⠕⠗⠊⠑⠗⠄⠱");

        // Same sentence, but the trailing period is OUTSIDE the em span. The
        // period is not emphasized, so the end-marker stays before it (…⠱⠄) —
        // unchanged from the previous behaviour.
        String sentenceBody = "Till Sara Mondani, som alltid kunde nysta fram historier";
        run("whole-sentence em, period OUTSIDE", sentenceBody + ".",
            new DefaultTextAttribute.Builder()
                .add(new DefaultTextAttribute.Builder("em").build(sentenceBody.length()))
                .add(1)
                .build(sentenceBody.length() + 1),
            "⠠⠤⠠⠞⠊⠇⠇⠀⠠⠎⠁⠗⠁⠀⠠⠍⠕⠝⠙⠁⠝⠊⠂⠀⠎⠕⠍⠀⠁⠇⠇⠞⠊⠙⠀⠅⠥⠝⠙⠑⠀⠝⠽⠎⠞⠁⠀⠋⠗⠁⠍⠀⠓⠊⠎⠞⠕⠗⠊⠑⠗⠱⠄");

        // <em>ab cû,</em>  — multi-word em ending in a literal 'û' before an
        // in-span comma. 'û' renders as ⠈⠥ (not ⠱), so it is never mistaken
        // for the inserted end-marker; the marker still lands after the
        // emphasized comma.
        run("multi-word em ending in û, comma INSIDE", "ab cû,",
            new DefaultTextAttribute.Builder()
                .add(new DefaultTextAttribute.Builder("em").build(6))
                .build(6),
            "⠠⠤⠁⠃⠀⠉⠈⠥⠂⠱");

        // ===== single-word strong =====

        // <strong>fet</strong>.  — spec §3.4.1: ⠨ prefix, no end marker.
        run("single-word strong, period OUTSIDE", "fet.",
            new DefaultTextAttribute.Builder()
                .add(new DefaultTextAttribute.Builder("strong").build(3))
                .add(1)
                .build(4),
            "⠨⠋⠑⠞⠄");

        // ===== multi-word strong =====

        // <strong>två feta</strong>,  — phrase wrapped in ⠨⠨ … ⠱; pass2
        // swaps the comma past the ⠱.
        run("multi-word strong, comma OUTSIDE", "två feta,",
            new DefaultTextAttribute.Builder()
                .add(new DefaultTextAttribute.Builder("strong").build(8))
                .add(1)
                .build(9),
            "⠨⠨⠞⠧⠡⠀⠋⠑⠞⠁⠱⠂");

        // <strong>två feta,</strong>  — comma INSIDE the strong span; the
        // end-marker goes after the comma, same as the em comma-INSIDE cases.
        run("multi-word strong, comma INSIDE", "två feta,",
            new DefaultTextAttribute.Builder()
                .add(new DefaultTextAttribute.Builder("strong").build(9))
                .build(9),
            "⠨⠨⠞⠧⠡⠀⠋⠑⠞⠁⠂⠱");

        // ===== em embedded in surrounding text =====

        // Han sa <em>nej</em>.  — single-word em mid-sentence; the
        // following period must not pick up a spurious ⠀ before it.
        // "Han sa " (7) + "nej" (3) + "." (1) = 11
        run("em mid-sentence, period after non-em text", "Han sa nej.",
            new DefaultTextAttribute.Builder()
                .add(7)
                .add(new DefaultTextAttribute.Builder("em").build(3))
                .add(1)
                .build(11),
            "⠠⠓⠁⠝⠀⠎⠁⠀⠠⠄⠝⠑⠚⠄");

        // <em>ord</em> mer text  — single-word em followed by a real
        // space and more text. The single legitimate space between
        // "ord" and "mer" must survive (regression guard against an
        // over-eager strip).
        run("em followed by plain text, no punct", "ord mer text",
            new DefaultTextAttribute.Builder()
                .add(new DefaultTextAttribute.Builder("em").build(3))
                .add(9)
                .build(12),
            "⠠⠄⠕⠗⠙⠀⠍⠑⠗⠀⠞⠑⠭⠞");

        // ===== caps controls =====
        //
        // No TextAttribute styling on these — caps comes from letter case
        // alone. Spec §3.2.2 / §3.2.3 prescribes a ⠱ end-marker between
        // the all-caps run and a following lowercase / suffix tail
        // (e.g. ⠠⠠ISBN⠱-centralen). The current liblouis path emits
        // ⠠⠠⠊⠎⠃⠝⠤⠉⠑⠝⠞⠗⠁⠇⠑⠝ instead — that is, with no end-marker
        // before the hyphen-suffix. The legacy CapitalizationMarkers
        // does emit the marker (matching the spec). This is a known
        // pre-existing gap, tracked separately; we assert the current
        // behaviour so the test serves as a tripwire if it changes.

        run("caps phrase, hyphen-suffix (ISBN-centralen)",
            "ISBN-centralen", null,
            "⠠⠠⠊⠎⠃⠝⠤⠉⠑⠝⠞⠗⠁⠇⠑⠝");

        run("caps phrase, colon-suffix (SACO:s)",
            "SACO:s", null,
            "⠠⠠⠎⠁⠉⠕⠒⠎");

        run("caps phrase, period-suffix (IKEA.)",
            "IKEA.", null,
            "⠠⠠⠊⠅⠑⠁⠄");
    }

    /**
     * Translate {@code text} through both translators, write the side-by-side
     * result to the report, and assert the liblouis output matches
     * {@code expectedLiblouis}. Failures are accumulated and surfaced as a
     * single assertion at the end of the test so every row in the matrix is
     * still executed and reported even when an earlier row fails.
     */
    private void run(String label, String text, TextAttribute atts, String expectedLiblouis)
        throws TranslationException {
        String legacyOut = translate(legacy, text, atts);
        String liblouisOut = translate(liblouis, text, atts);

        String marker;
        if (!liblouisOut.equals(expectedLiblouis)) {
            marker = "FAIL";
            failures.add(label + ": expected <" + expectedLiblouis + "> but was <" + liblouisOut + ">");
        } else if (!legacyOut.equals(liblouisOut)) {
            marker = "diff";
        } else {
            marker = "    ";
        }
        line(String.format("%-50s  %s", label, marker));
        line(String.format("  input    | %s", text));
        line(String.format("  legacy   | %s", legacyOut));
        line(String.format("  liblouis | %s", liblouisOut));
        if (!liblouisOut.equals(expectedLiblouis)) {
            line(String.format("  expected | %s   <-- FAIL", expectedLiblouis));
        }
        line("");
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

    private void line(String s) {
        report.println(s);
    }
}
