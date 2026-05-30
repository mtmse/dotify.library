package org.daisy.dotify.translator.impl.liblouis;

import org.daisy.dotify.api.hyphenator.HyphenatorConfigurationException;
import org.daisy.dotify.api.hyphenator.HyphenatorFactoryMakerService;
import org.daisy.dotify.api.hyphenator.HyphenatorInterface;
import org.daisy.dotify.api.translator.BrailleFilter;
import org.daisy.dotify.api.translator.ResolvableText;
import org.daisy.dotify.api.translator.TextAttribute;
import org.daisy.dotify.api.translator.Translatable;
import org.daisy.dotify.api.translator.TranslatableWithContext;
import org.daisy.dotify.api.translator.TranslationException;
import org.daisy.dotify.api.translator.TranslatorSpecification;
import org.daisy.dotify.translator.DefaultMarkerProcessor;
import org.liblouis.CompilationException;
import org.liblouis.DisplayException;
import org.liblouis.DisplayTable.Fallback;
import org.liblouis.DisplayTable.UnicodeBrailleDisplayTable;
import org.liblouis.TranslationResult;
import org.liblouis.Translator;
import org.liblouis.Typeform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class LiblouisBrailleFilter implements BrailleFilter {
    private static final Logger LOGGER = Logger.getLogger(LiblouisBrailleFilter.class.getCanonicalName());
    private static final int SOFT_HYPHEN = 0x00ad;
    private static final int ZERO_WIDTH_SPACE = 0x200b;
    private static final int LIBLOUIS_NO_BREAKPOINT = 0;
    private static final int LIBLOUIS_SOFT_HYPEN = 1;
    private static final int LIBLOUIS_ZERO_WIDTH_SPACE = 2;
    private final String loc;
    private final HyphenatorFactoryMakerService hyphenatorFactoryMaker;
    private final Map<String, HyphenatorInterface> hyphenators;
    private final Translator table;
    private final Map<String, Typeform> typeformMap;
    private final LiblouisMarkerProcessor mp;

    LiblouisBrailleFilter(
        TranslatorSpecification ts,
        LiblouisMarkerProcessor mp,
        HyphenatorFactoryMakerService hyphenatorFactoryMaker
    ) {
        this.loc = ts.getLocale();
        this.hyphenatorFactoryMaker = hyphenatorFactoryMaker;
        this.hyphenators = new HashMap<>();
        try {
            this.table = new Translator(LiblouisSpecifications.getMap().get(ts));
        } catch (CompilationException e) {
            throw new IllegalArgumentException(e);
        }
        this.typeformMap = table.getSupportedTypeforms().stream()
                .collect(Collectors.toMap(x -> x.getName(), x -> x));
        addTypeformAlias("italic", "em");
        addTypeformAlias("bold", "strong");
        this.mp = mp;
    }

    private void addTypeformAlias(String name, String alias) {
        if (typeformMap.containsKey(name) && !typeformMap.containsKey(alias)) {
            Typeform t = typeformMap.get(name);
            typeformMap.put(alias, t);
        }
    }

    @Override
    public String filter(Translatable specification) throws TranslationException {
        if (specification.getText().isEmpty()) {
            return "";
        }
        String locale = specification.getLocale();
        if (locale == null) {
            locale = loc;
        }

        // Attributes should be processed here. To do that, the text must be split up into
        // the attribute parts, processed and then reassembled with updated attributes.
        // The updated attributes should then be used when creating the type form below.
        // See also https://github.com/brailleapps/dotify.translator.impl/issues/4
        // Something like this:
        //      String[] t = splitByAttribute(specification.getText(), specification.getAttributes());
        //      AttributeWithContext atts = toAttributeWithContext(specification.getAttributes(), t);
        //      List<String> x = Arrays.asList(mp.getMarkerProcessor()
        //          .processAttributesRetain(specification.getAttributes(), t));
        //      TextAttribute ta = DefaultMarkerProcessor.toTextAttribute(atts, x);
        //      String text = x.stream().collect(Collectors.toList());

        String text = specification.getText();

        if (!specification.shouldMarkCapitalLetters()) {
            //TODO: toLowerCase may not always do what we want here,
            //it depends on the lower case algorithm and the rules
            //of the braille for that language
            text = text.toLowerCase(Locale.ROOT);
        }

        if (specification.isHyphenating()) {
            HyphenatorInterface h = hyphenators.get(locale);
            if (h == null) {
                try {
                    h = hyphenatorFactoryMaker.newHyphenator(locale);
                } catch (HyphenatorConfigurationException e) {
                    throw new LiblouisBrailleFilterException(e);
                }
                hyphenators.put(locale, h);
            }
            text = h.hyphenate(text);
        }

        // Soft hyphens that appear in the source text must reach Liblouis as
        // inter-character break candidates, not as translatable characters. They are
        // turned into LIBLOUIS_SOFT_HYPEN flags by toLiblouisSpecification when they
        // are present in hyphStr but not in inputStr, so strip them out of inputStr
        // here. keptInputPositions records the surviving positions so the per-character
        // typeForm array can be rebuilt to match the stripped input length.
        String originalInput = specification.getText();
        int[] keptInputPositions = positionsWithoutSoftHyphen(originalInput);
        String inputForLiblouis = stripSoftHyphens(originalInput);

        // Only style attributes from Liblouis itself are processed here
        LiblouisTranslatable louisSpec = toLiblouisSpecification(text, inputForLiblouis);
        TextAttribute ta = specification.getAttributes();
        Typeform[] typeForm;
        if (ta == null) {
            typeForm = new Typeform[louisSpec.getCharAtts().length];
        } else {
            Typeform[] fullTypeForm = toTypeForm(ta, typeformMap);
            typeForm = new Typeform[keptInputPositions.length];
            for (int i = 0; i < keptInputPositions.length; i++) {
                typeForm[i] = fullTypeForm[keptInputPositions[i]];
            }
        }

        try {
            return toBrailleFilterString(
                louisSpec.getText(),
                table.translate(
                    louisSpec.getText(),
                    typeForm,
                    louisSpec.getCharAtts(),
                    louisSpec.getInterCharAtts(),
                    new UnicodeBrailleDisplayTable(Fallback.MASK)
                ),
                louisSpec.getTrailingAtt()
            );
        } catch (org.liblouis.TranslationException | DisplayException e) {
            throw new LiblouisBrailleFilterException(e);
        }
    }

    @Override
    public String filter(TranslatableWithContext specification) throws TranslationException {
        if (specification.getTextToTranslate().isEmpty()) {
            return "";
        }

        Stream<String> inStream = specification.getTextToTranslate().stream().map(v -> v.resolve());
        List<String> texts;

        if (mp != null && specification.getAttributes().isPresent()) {
            Stream<String> preceding = specification.getPrecedingText().stream().map(v -> v.resolve());
            Stream<String> following = specification.getFollowingText().stream().map(v -> v.peek());
            List<String> textsI = Stream.concat(
                Stream.concat(preceding, inStream), following
            ).collect(Collectors.toList());
            String[] out = mp.getMarkerProcessor().processAttributesRetain(specification.getAttributes().get(), textsI);
            int start = specification.getPrecedingText().size();
            int end = start + specification.getTextToTranslate().size();
            texts = Arrays.asList(out).subList(start, end);
        } else {
            texts = inStream.collect(Collectors.toList());
        }

        Processor p = new Processor(specification.getTextToTranslate().get(0));
        int i = 0;
        for (ResolvableText t : specification.getTextToTranslate()) {
            p.process(texts.get(i), t);
            i++;
        }
        p.flush();

        // See the soft-hyphen handling in filter(Translatable) for the rationale.
        // strIn (the text going to Liblouis) is stripped of source soft hyphens;
        // keptInputPositions maps back to the original (un-stripped) position so the
        // per-character typeForm can be extracted from typeForm2.
        String strInRaw = p.textB.toString();
        int[] keptInputPositions = positionsWithoutSoftHyphen(strInRaw);
        String strIn = stripSoftHyphens(strInRaw);
        String strHyph = p.hyphB.toString();

        LiblouisTranslatable louisSpec = toLiblouisSpecification(strHyph, strIn);

        Typeform[] typeForm;

        if (specification.getAttributes().isPresent()) {
            List<String> preceding = specification.getPrecedingText().stream().map(
                v -> v.resolve()).collect(Collectors.toList()
            );
            List<String> following = specification.getFollowingText().stream().map(
                v -> v.peek()).collect(Collectors.toList()
            );
            List<String> textsI = Stream.concat(
                Stream.concat(preceding.stream(), p.parts.stream()), following.stream()
            ).collect(Collectors.toList());
            TextAttribute ta = DefaultMarkerProcessor.toTextAttribute(specification.getAttributes().get(), textsI);
            Typeform[] typeForm2 = toTypeForm(ta, typeformMap);
            int start = preceding.stream().mapToInt(v -> v.length()).sum();
            typeForm = new Typeform[keptInputPositions.length];
            for (int k = 0; k < keptInputPositions.length; k++) {
                typeForm[k] = typeForm2[start + keptInputPositions[k]];
            }
        } else {
            typeForm = new Typeform[louisSpec.getCharAtts().length];
        }

        try {
            return toBrailleFilterString(
                louisSpec.getText(),
                table.translate(
                    louisSpec.getText(),
                    typeForm,
                    louisSpec.getCharAtts(),
                    louisSpec.getInterCharAtts(),
                    new UnicodeBrailleDisplayTable(Fallback.MASK)
                ),
                louisSpec.getTrailingAtt()
            );
        } catch (org.liblouis.TranslationException | DisplayException e) {
            throw new LiblouisBrailleFilterException(e);
        }
    }

    private class Processor {

        private Processor(ResolvableText props) {
            processorLocale = props.getLocale();
            hyphenate = props.shouldHyphenate();
            markCapitals = props.shouldMarkCapitalLetters();
        }

        Optional<String> processorLocale;
        boolean hyphenate;
        boolean markCapitals;

        List<String> parts = new ArrayList<>();
        StringBuilder textB = new StringBuilder();
        StringBuilder hyphB = new StringBuilder();
        StringBuilder toTranslate = new StringBuilder();

        private void process(String s, ResolvableText props) {
            if (
                !props.getLocale().equals(processorLocale) ||
                props.shouldHyphenate() != hyphenate ||
                props.shouldMarkCapitalLetters() != markCapitals
            ) {
                //Flush
                flush();
                // Set
                processorLocale = props.getLocale();
                hyphenate = props.shouldHyphenate();
                markCapitals = props.shouldMarkCapitalLetters();
                toTranslate = new StringBuilder();
            }
            parts.add(s);
            toTranslate.append(s);
        }

        private void flush() {
            if (toTranslate.length() == 0) {
                return;
            }
            String text = toTranslate.toString();
            if (!markCapitals) {
                //TODO: toLowerCase may not always do what we want here,
                //it depends on the lower case algorithm and the rules
                //of the braille for that language
                text = text.toLowerCase(Locale.ROOT);
            }
            String hyphText = text;
            if (hyphenate) {
                String locale = processorLocale.orElse(loc);
                HyphenatorInterface hx = hyphenators.get(locale);
                if (hx == null) {
                    try {
                        hx = hyphenatorFactoryMaker.newHyphenator(locale);
                    } catch (HyphenatorConfigurationException e) {
                        if (LOGGER.isLoggable(Level.WARNING)) {
                            LOGGER.log(Level.WARNING, String.format("Failed to create hyphenator for %s", locale), e);
                        }
                    }
                    hyphenators.put(locale, hx);
                }
                hyphText = hx.hyphenate(text);
            }

            textB.append(text);
            hyphB.append(hyphText);
        }

    }

    /** The Unicode soft hyphen code point (U+00AD). Kept as an escape — the literal
     *  character is invisible and easily lost to editors or copy-paste pipelines. */
    private static final char SOFT_HYPHEN_CHAR = '\u00ad';
    private static final String SOFT_HYPHEN_STRING = "\u00ad";

    /** Returns the input with all U+00AD (soft hyphen) code points removed. */
    static String stripSoftHyphens(String s) {
        if (s.indexOf(SOFT_HYPHEN_CHAR) < 0) {
            return s;
        }
        return s.replace(SOFT_HYPHEN_STRING, "");
    }

    /**
     * Returns the indexes of {@code s} whose character is not a soft hyphen.
     * Parallel to {@link #stripSoftHyphens(String)}: the i-th entry of the returned
     * array is the original-string position of the i-th character of the stripped string.
     * Used to re-align per-character data (e.g. type forms) when soft hyphens are
     * stripped before being sent to Liblouis.
     */
    static int[] positionsWithoutSoftHyphen(String s) {
        int n = s.length();
        int[] kept = new int[n];
        int k = 0;
        for (int i = 0; i < n; i++) {
            if (s.charAt(i) != SOFT_HYPHEN_CHAR) {
                kept[k++] = i;
            }
        }
        if (k == n) {
            return kept;
        }
        int[] trimmed = new int[k];
        System.arraycopy(kept, 0, trimmed, 0, k);
        return trimmed;
    }

    /**
     * Maps a translatable and the corresponding hyphenated string to a set of data that can be
     * used with Liblouis. The hyphenated string is used to set the intercharacter attributes.
     *
     * <p>The algorithm walks {@code cpHyph} and {@code cpInput} in lockstep:
     * <ul>
     *   <li>For each input character, advance past it in {@code cpHyph} (the two
     *       must match — otherwise the inputs are inconsistent).</li>
     *   <li>Then scan forward through any soft-hyphen or zero-width-space characters
     *       in {@code cpHyph} until the next non-break-attribute character. The
     *       highest-priority flag encountered becomes either the inter-character
     *       attribute between input[i] and input[i+1], or — for the last input
     *       character — the trailing attribute after the last output cell.</li>
     * </ul>
     * Earlier versions of this routine used a "look-ahead" form (advance until
     * {@code cpHyph[j] == cpInput[i+1]}), which silently failed when two adjacent
     * input characters were identical (e.g. already-braille content like
     * "{@code ⠤­⠤­⠤}", where input is "{@code ⠤⠤⠤}"): the look-ahead exited
     * immediately because {@code cpHyph[j]} already matched {@code cpInput[i+1]},
     * dropping the intervening soft hyphen.
     *
     * @param hyphStr  the hyphenated string
     * @param inputStr the input string
     * @return hyphenation information
     */
    static LiblouisTranslatable toLiblouisSpecification(String hyphStr, String inputStr) {
        if (hyphStr.length() < inputStr.length()) {
            throw new IllegalArgumentException("The hyphenated string cannot be shorter than the input string");
        }

        if (inputStr.isEmpty()) {
            return new LiblouisTranslatable(inputStr, new int[0], new int[0]);
        }

        int[] cpHyph = hyphStr.codePoints().toArray();
        int[] cpInput = inputStr.codePoints().toArray();
        int[] interCharAttr = new int[cpInput.length - 1];
        int[] charAtts = new int[cpInput.length];
        int trailingAtt = LIBLOUIS_NO_BREAKPOINT;

        int j = 0;
        for (int i = 0; i < cpInput.length; i++) {
            charAtts[i] = i;

            // cpHyph[j] should match cpInput[i] — advance past it.
            if (j >= cpHyph.length || cpHyph[j] != cpInput[i]) {
                throw new RuntimeException(
                    "Mismatch at input position " + i + ", hyph position " + j
                    + ": '" + hyphStr + "':'" + inputStr + "'"
                );
            }
            j++;

            // Scan any break-attribute chars between input[i] and input[i+1] (or
            // after the last input char) and record the strongest flag seen.
            //
            // The look-ahead check `cpInput[i+1] == cpHyph[j]` is necessary because
            // inputStr is allowed to contain SOFT_HYPHEN and ZERO_WIDTH_SPACE
            // characters as regular content (e.g. when the caller has explicit
            // soft hyphens in source text that match positions already accounted
            // for in inputStr). When cpHyph[j] is a break-attr character that
            // also appears at the next position in cpInput, it is NOT an extra
            // break-attr inserted by the hyphenator — it is the next input
            // character itself, which we will pick up on the next outer iteration.
            int flag = LIBLOUIS_NO_BREAKPOINT;
            while (j < cpHyph.length) {
                if (i + 1 < cpInput.length && cpInput[i + 1] == cpHyph[j]) {
                    break;
                }
                if (cpHyph[j] == SOFT_HYPHEN) {
                    flag = LIBLOUIS_SOFT_HYPEN;
                    j++;
                } else if (cpHyph[j] == ZERO_WIDTH_SPACE && flag != LIBLOUIS_SOFT_HYPEN) {
                    flag = LIBLOUIS_ZERO_WIDTH_SPACE;
                    j++;
                } else {
                    break;
                }
            }
            if (i < cpInput.length - 1) {
                interCharAttr[i] = flag;
            } else {
                // After the last input char — any remaining break attributes in
                // cpHyph become the trailing attribute.
                trailingAtt = flag;
            }
        }
        return new LiblouisTranslatable(inputStr, charAtts, interCharAttr, trailingAtt);
    }

    /**
     * Converts a text attribute to its "type form" equivalent.
     *
     * @param attr the text attribute
     * @param map  the text attribute name to type form value map
     * @return returns an array with the corresponding values
     */
    static Typeform[] toTypeForm(TextAttribute attr, Map<String, Typeform> map) {
        Typeform[] ret = new Typeform[attr.getWidth()];
        Typeform typeForm = Typeform.PLAIN_TEXT;
        if (attr.getDictionaryIdentifier() != null) {
            typeForm = Optional.ofNullable(map.get(attr.getDictionaryIdentifier())).orElse(typeForm);
        }

        if (attr.hasChildren()) {
            int offset = 0;
            for (TextAttribute t : attr) {
                Typeform[] v = toTypeForm(t, map);
                //Note: v.length == t.getWidth()
                for (int i = 0; i < v.length; i++) {
                    ret[i + offset] = typeForm.add(v[i]);
                }
                offset += t.getWidth();
            }
        } else {
            for (int i = 0; i < ret.length; i++) {
                ret[i] = typeForm;
            }
        }
        return ret;
    }

    private static String toBrailleFilterString(String input, TranslationResult res, int trailingAtt) {
        return toBrailleFilterString(
            input, res.getBraille(), res.getCharacterAttributes(), res.getInterCharacterAttributes(), trailingAtt
        );
    }

    /**
     * Modifies a string from Liblouis into a string that is compatible with {@link BrailleFilter}
     * by adding hyphenation characters (soft hyphen and zero width space).
     *
     * @param str           the Liblouis string
     * @param interCharAttr the inter char attributes.
     * @param trailingAtt   the break-attribute flag for the position after the last output cell.
     *                      Used when the source hyphenated string ended with a soft hyphen or
     *                      zero-width space, which is not representable in {@code interCharAttr}
     *                      because the latter only covers positions <em>between</em> two cells.
     * @return a string
     */
    static String toBrailleFilterString(String input, String str, int[] charAtts, int[] interCharAttr, int trailingAtt) {
        StringBuilder sb = new StringBuilder();
        int[] inputCodePoints = input.codePoints().toArray();
        int[] codePoints = str.codePoints().toArray();
        int prvInputIndex = -1;
        int inputIndex, inputCP;
        for (int outputIndex = 0; outputIndex < codePoints.length; outputIndex++) {
            inputIndex = charAtts[outputIndex];
            inputCP = inputCodePoints[inputIndex];
            // The following is needed because some tables in Liblouis translate spaces into braille cells, e.g. Danish.
            // The BrailleFilter contract requires spaces to be preserved.
            if (Character.isWhitespace(inputCP)) {
                // If the input index for the output index is the same as the previous
                // input index, then this output character belongs to the same input character.
                // If so, the character has already been processed, and should not be added to the
                // output again.
                if (prvInputIndex != inputIndex) {
                    sb.appendCodePoint(' ');
                }
                prvInputIndex = inputIndex;
            } else {
                prvInputIndex = -1;
                sb.appendCodePoint(codePoints[outputIndex]);
            }
            if (outputIndex < interCharAttr.length) {
                switch (interCharAttr[outputIndex]) {
                    case LIBLOUIS_NO_BREAKPOINT:
                        break;
                    case LIBLOUIS_SOFT_HYPEN:
                        sb.append('\u00ad');
                        break;
                    case LIBLOUIS_ZERO_WIDTH_SPACE:
                        sb.append('\u200b');
                        break;
                    default:
                }
            }
        }
        // Trailing break candidate that fell off the end of interCharAttr \u2014 see Javadoc.
        switch (trailingAtt) {
            case LIBLOUIS_SOFT_HYPEN:
                sb.append('\u00ad');
                break;
            case LIBLOUIS_ZERO_WIDTH_SPACE:
                sb.append('\u200b');
                break;
            default:
        }
        return stripStraySpaceAfterEmphasizedWord(sb.toString());
    }

    // Cells liblouis can emit as terminal punctuation in Swedish braille.
    // Used to identify "<emph-word-prefix><word>\u2800<punct>" sequences
    // where the \u2800 is a spurious word-boundary marker injected by
    // liblouis at the end of an emphasized word.
    private static final String SWEDISH_TERMINAL_PUNCT_CELLS = "\u2804\u2802\u2806\u2812\u2816\u2826\u2830\u2834\u283e";

    // Italic single-word emphasis: prefix \u2820\u2804 (dots 6, dots 3 \u2014 see
    // `begemphword italic 6-3` in sv-mtm-g0.utb), then non-space word cells,
    // then a spurious \u2800, then a terminal-punctuation cell. Drop the
    // \u2800. The non-greedy [^\u2800]+? avoids consuming a legitimate space.
    private static final Pattern STRAY_SPACE_AFTER_ITALIC_WORD = Pattern.compile(
        "(\u2820\u2804[^\u2800]+?)\u2800([" + SWEDISH_TERMINAL_PUNCT_CELLS + "])");

    // Bold single-word emphasis: prefix \u2828 (dots 46 \u2014 see `begemphword
    // bold 46` in sv-mtm-g0.utb). \u2828 also appears as the *first* cell of
    // the two-cell multi-word bold start \u2828\u2828, but the multi-word
    // form's trailing space (if any) is followed by a letter, not punct,
    // so the regex naturally won't match there.
    private static final Pattern STRAY_SPACE_AFTER_BOLD_WORD = Pattern.compile(
        "(\u2828[^\u2800]+?)\u2800([" + SWEDISH_TERMINAL_PUNCT_CELLS + "])");

    /**
     * Removes the spurious word-boundary space cell that liblouis injects
     * after an emphasized single word when that word is followed immediately
     * by terminal punctuation in the source \u2014 e.g. {@code <em>ord</em>.}
     * would otherwise emit {@code \u2820\u2804\u2815\u2817\u2819\u2800\u2804} (with a {@code \u2800} between
     * "ord" and the period). The MTM spec (\u00a73.4.1) and the legacy
     * SwedishMarkerProcessor both expect {@code \u2820\u2804\u2815\u2817\u2819\u2804} (no space).
     *
     * Triggered after we dropped {@code noemphchars italic \s.,:;!?} from
     * the table because that directive \u2014 while suppressing this artifact \u2014
     * also caused liblouis to glue trailing punctuation into the last word
     * of multi-word emphasis phrases, mispositioning the phrase-end marker
     * (the "r\u00f6ra" pattern). See BREAKING_CHANGES.md item 6.
     */
    private static String stripStraySpaceAfterEmphasizedWord(String s) {
        // Bail out cheaply when neither pattern can possibly match.
        if (s.indexOf('\u2800') < 0) {
            return s;
        }
        s = STRAY_SPACE_AFTER_ITALIC_WORD.matcher(s).replaceAll("$1$2");
        s = STRAY_SPACE_AFTER_BOLD_WORD.matcher(s).replaceAll("$1$2");
        return s;
    }

}
