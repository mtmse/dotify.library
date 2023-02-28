package org.daisy.dotify.hyphenator.impl;

import org.daisy.dotify.api.hyphenator.HyphenatorConfigurationException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is a simple implementation of a hyphenator using Hunspell hyphenator format for storing the patterns.
 */
public class SimpleHyphenator extends AbstractHyphenator {

    /*
    This map will keep the mapping between words/word parts without any numbers so we can find them quickly.
     */
    private final Map<String, short[]> hyphenWords = new HashMap<>();

    /*
    We want to keep track of the longest word in our hyphenWords map so we don't look for words that can't
    be there.
     */
    private int maxWordLen = 0;

    /*
    Left and right hyphenation min is same as previous implementations. It will ensure that we don't hyphenate
    a word to early or to late inside the word.
     */
    private int leftHyphenMin = 1;
    private int rightHyphenMin = 1;

    /*
    Lastly we have a hyphenation cache so we don't hyphenate a word more than once.
     */
    private final Map<String, String> hyphCache = new HashMap<>();

    /**
     * The constructor loads the dictionary with all the words package in a hunspell / LaTex similar format.
     *
     * @param locale  Locale of the language to load. We have specified all the available languages and their
     *                dictionary files within SimpleHyphenator.properties.
     *
     * @throws HyphenatorConfigurationException If we aren't able to read the bundle or find / load the dictionary
     *                this will throw a configuration exception.
     */
    SimpleHyphenator(String locale) throws HyphenatorConfigurationException {
        try {
            ResourceBundle p = PropertyResourceBundle.getBundle("org/daisy/dotify/hyphenator/impl/SimpleHyphenator");
            String patternPath = "resource-files/dict/" + p.getString(locale);

            InputStream language = SimpleHyphenator.getResource(patternPath).openStream();
            InputStreamReader sr = new InputStreamReader(language, StandardCharsets.UTF_8);

            loadTable(sr);
        } catch (Exception e) {
            throw new SimpleHyphenatorConfigurationException(e);
        }
    }

    /**
     * The load table function reads the dictionary into hyphenWords. First we ignore all comments and then
     * we look for the left min hyphen and right min hyphen so we could use that configuration from the header.
     * Currently we ignore the encoding and compound words part of the configuration. All files expects to be
     * in a UTF-8 encoding and until we get a dictinary where compound words are used we will not use that
     * configuration.
     *
     * @param isr   The stream reader used to read dictionary file.
     *
     * @throws IOException  If we can't find or read the file the method will throw an exception.
     */
    public void loadTable(InputStreamReader isr) throws IOException {
        BufferedReader br = new BufferedReader(isr);
        String line;
        while ((line = br.readLine()) != null) {
            if (line.contains("%")) {
                line = line.substring(0, line.indexOf("%"));
            }
            if (
                line.isEmpty() ||
                line.trim().equals("") ||
                line.toUpperCase().contains("COMPOUNDLEFTHYPHENMIN") ||
                line.toUpperCase().contains("COMPOUNDRIGHTHYPHENMIN") ||
                line.toUpperCase().contains("ISO8859-1") ||
                line.toUpperCase().contains("UTF-8")
            ) {
                continue;
            }
            if (line.toUpperCase().startsWith("LEFTHYPHENMIN")) {
                leftHyphenMin = Integer.parseInt(line.split(" ")[1]);
                continue;
            }
            if (line.toUpperCase().startsWith("RIGHTHYPHENMIN")) {
                rightHyphenMin = Integer.parseInt(line.split(" ")[1]);
                continue;
            }

            String simpleWord = simpleWord(line);
            maxWordLen = Math.max(maxWordLen, simpleWord.length());
            hyphenWords.put(simpleWord, shortMask(line, simpleWord.length()));
        }
    }

    /**
     * This function looks through a word and finds all the word pieces and looks up their accompanying mask.
     * It will loop through the word length (wLen) from 1 to the length of the word. And then loop through all
     * available positions within the word available of that length. Fetch the mask pieces and put them into a
     * list.
     *
     * @param word      Word to hyphenate.
     *
     * @return A list of mask pieces that later could be merged into a full mask used for hyphenation.
     */
    public List<MaskPiece> findMaskPieces(String word) {
        List<MaskPiece> allPieces = new ArrayList<>();

        if (hyphenWords.containsKey(word)) {
            allPieces.add(new MaskPiece(0, hyphenWords.get(word)));
        }
        for (int wLen = 1; wLen < Math.min(word.length(), maxWordLen); wLen++) {
            for (int pos = 0; pos < word.length() - wLen + 1; pos++) {
                String wordPiece = word.substring(pos, pos + wLen);
                if (hyphenWords.containsKey(wordPiece)) {
                    allPieces.add(new MaskPiece(pos, hyphenWords.get(wordPiece)));
                }
            }
        }
        return allPieces;
    }

    /*
    This pattern is used to find a word. It says that a word is a one or more uppercase or lowercase letters
    after each other. In cases where we have "Don't" we will hyphenate "Don" and "t" separately.
     */
    private final Pattern wordPattern = Pattern.compile("([\\p{javaUpperCase}\\p{javaLowerCase}]+)");

    /**
     * This function will find all words to hyphenate and also save results to a hyphenation cache.
     *
     * @param phrase the phrase to hyphenate
     *
     * @return  Hyphenated phrase.
     */
    public String hyphenate(String phrase) {
        StringBuilder sb = new StringBuilder();
        int pos = 0;
        Matcher m = wordPattern.matcher(phrase);
        while (m.find()) {
            sb.append(phrase.substring(pos, m.start()));
            if (!hyphCache.containsKey(m.group(1))) {
                hyphCache.put(m.group(1), hyphenateWord(m.group(1)));
            }
            sb.append(hyphCache.get(m.group(1)));
            pos = m.end();
        }
        sb.append(phrase.substring(pos));
        return sb.toString();
    }

    /**
     * This function hyphenates a word. It will find the mask pieces and merge them together to a full mask.
     * We will reset the beginning and end of the mask dependent on the leftHyphenMin and rightHyphenMin.
     * Lastly we will append all allowed hyphens to the result word.
     *
     * @param word  Word to hyphenate.
     *
     * @return  Hyphenated word.
     */
    private String hyphenateWord(String word) {
        if (word.length() < leftHyphenMin + rightHyphenMin) {
            return word;
        }

        word = "." + word + ".";
        short[] fullmask = new short[word.length() + 1];
        for (MaskPiece mp : findMaskPieces(word.toLowerCase())) {
            mp.merge(fullmask);
        }

        // We add 1 to each loop as the word has a dot and the beginning and end.
        for (int i = 0; i < leftHyphenMin + 1; i++) {
            fullmask[i] = 0;
        }
        for (int i = 0; i < rightHyphenMin + 1; i++) {
            fullmask[fullmask.length - (i + 1)] = 0;
        }

        String hyphenedWord = addHyphens(word, fullmask);

        // We will remove the dots at the beginning and end of the word before returning.
        return hyphenedWord.substring(1, hyphenedWord.length() - 1);
    }

    /**
     * This function will add soft-hyphens to the word depending on the mask. Where ever the
     * number in the mask is odd we will add a soft-hyphen. And where it's even we will not.
     *
     * @param word  Word to hyphenate.
     * @param mask  Mask used for hyphenation.
     *
     * @return  Hyphenated word.
     */
    private String addHyphens(String word, short[] mask) {
        String newWord = "";
        int i = 0;
        for (String ch : word.split("")) {
            if (mask[i] % 2 == 1) {
                newWord += "\u00AD";
            }
            newWord += ch;
            i++;
        }
        return newWord;
    }

    /**
     * This function will extract the short mask from a encoded dictionary word.
     * If we find a number between characters we will add that number to the mask and if there is no number
     * the mask number will continue to be zeroes.
     *
     * @param word  The word that we want to extract number from.
     * @param len   The length of the simple word without any numbers. We use this to create a mask that is
     *              one number longer.
     *
     * @return      The mask used for hyphenation, the dictionary word without characters.
     */
    private short[] shortMask(String word, int len) {
        short[] mask = new short[len + 1];
        int i = 0;
        for (String ch : word.split("")) {
            if (ch.matches("[0-9]")) {
                mask[i] = Short.parseShort(ch);
                i--;
            }
            i++;
        }
        return mask;
    }

    /**
     * This function returns a simple word where we have removed all the numbers. This will be used
     * for lookup the mask in the hyphenWords map.
     *
     * @param word  Encoded word with numbers.
     *
     * @return  Word without number encoding.
     */
    private String simpleWord(String word) {
        return word.replaceAll("[0-9]", "");
    }

    /**
     * This class is a simple helper class used for the mask. It will store the mask piece and the
     * position within the word where we found that piece.
     */
    private class MaskPiece {
        private final int position;
        private final short[] mask;

        /**
         * Storing a mask piece with position and looked up mask.
         *
         * @param position  Position within the word where we found a hit on this mask.
         * @param mask  The mask we want to use for hyphenation.
         */
        private MaskPiece(int position, short[] mask) {
            this.position = position;
            this.mask = mask;
        }

        /**
         * This function will merge this specific piece with a full mask. If this piece have a higher number
         * than a specific position in the full mask then we will replace that number with the number from the
         * mask piece. We want to end up with a full mask with the highest number in each position of all the
         * mask pieces.
         *
         * @param fullmask  The complete mask we later will use for adding the soft-hyphens into the word.
         */
        private void merge(short[] fullmask) {
            int len = Math.min(fullmask.length - position, mask.length);
            for (int i = 0; i < len; i++) {
                fullmask[position + i] = fullmask[position + i] > mask[i] ? fullmask[position + i] : mask[i];
            }
        }
    }

    public static URL getResource(String path) throws FileNotFoundException {
        URL url;
        url = SimpleHyphenator.class.getResource(path);
        if (null == url) {
            String qualifiedPath = SimpleHyphenator.class.getPackage().getName().replace('.', '/') + "/";
            url = SimpleHyphenator.class.getClassLoader().getResource(qualifiedPath + path);
        }
        if (url == null) {
            throw new FileNotFoundException(
                    "Cannot find resource path '" + path + "' relative to " + SimpleHyphenator.class.getCanonicalName()
            );
        }
        return url;
    }

    public static boolean resourceExist(String path) {
        URL url;
        url = SimpleHyphenator.class.getResource(path);
        if (null == url) {
            String qualifiedPath = SimpleHyphenator.class.getPackage().getName().replace('.', '/') + "/";
            url = SimpleHyphenator.class.getClassLoader().getResource(qualifiedPath + path);
        }
        return url != null;
    }
}
