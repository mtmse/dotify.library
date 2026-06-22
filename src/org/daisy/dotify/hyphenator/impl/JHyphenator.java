package org.daisy.dotify.hyphenator.impl;

import ch.sbs.jhyphen.Hyphen;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import org.daisy.dotify.api.hyphenator.HyphenatorConfigurationException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class JHyphenator extends AbstractHyphenator {
    private static ByteBuffer wordHyphens = ByteBuffer.allocate(50);
    private final Pointer dictionary;

    /*
     Left and right hyphenation min is same as previous implementations. It will ensure that we don't hyphenate
     a word to early or to late inside the word.
     */
    private int leftHyphenMin = 1;
    private int rightHyphenMin = 1;

    private final Map<String, String> hyphCache = new HashMap<>();

    JHyphenator(String locale) throws HyphenatorConfigurationException {
        try {
            ResourceBundle p = PropertyResourceBundle.getBundle("org/daisy/dotify/hyphenator/impl/JHyphenator");
            File dictionaryFile = new File("/usr/share/hyphen/", p.getString(locale));

            BufferedReader br = new BufferedReader(new FileReader(dictionaryFile));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.toUpperCase().startsWith("LEFTHYPHENMIN")) {
                    leftHyphenMin = Integer.parseInt(line.split(" ")[1]);
                    continue;
                }
                if (line.toUpperCase().startsWith("RIGHTHYPHENMIN")) {
                    rightHyphenMin = Integer.parseInt(line.split(" ")[1]);
                    continue;
                }
            }

            dictionary = Hyphen.getLibrary().hnj_hyphen_load(dictionaryFile.getAbsolutePath());

        } catch (Exception e) {
            throw new JHyphenatorConfigurationException(e);
        }
    }

    private final Pattern wordPattern = Pattern.compile("([\\p{javaUpperCase}\\p{javaLowerCase}]+)");


    private byte[] hyphenate_inner(String word) {
        word = word.toLowerCase();
        word = "." + word + ".";
        byte[] wordBytes = StandardCharsets.UTF_8.encode(word).array();
        int wordSize = wordBytes.length;
        if (wordSize > wordHyphens.capacity()) {
            wordHyphens = ByteBuffer.allocate(wordSize * 2);
        }
        PointerByReference repPointer = new PointerByReference(Pointer.NULL);
        PointerByReference posPointer = new PointerByReference(Pointer.NULL);
        PointerByReference cutPointer = new PointerByReference(Pointer.NULL);
        Hyphen.getLibrary().hnj_hyphen_hyphenate2(dictionary, wordBytes, wordSize, wordHyphens, null,
                repPointer, posPointer, cutPointer);
        return wordHyphens.array();
    }

    public String hyphenate(String phrase) {
        StringBuilder sb = new StringBuilder();
        int pos = 0;
        Matcher m = wordPattern.matcher(phrase);
        while (m.find()) {
            sb.append(phrase.substring(pos, m.start()));
            if (!hyphCache.containsKey(m.group(1))) {
                hyphCache.put(m.group(1), addHyphens(m.group(1), hyphenate_inner(m.group(1))));
            }
            sb.append(hyphCache.get(m.group(1)));
            pos = m.end();
        }
        sb.append(phrase.substring(pos));
        return sb.toString();
    }

    private String addHyphens(String word, byte[] mask) {
        if (word.length() < leftHyphenMin + rightHyphenMin) {
            return word;
        }

        for (int i = 0; i < leftHyphenMin; i++) {
            mask[i] = 0;
        }
        for (int i = 0; i < rightHyphenMin; i++) {
            mask[(word.length() + 1) - (i + 1)] = 0;
        }

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
}
