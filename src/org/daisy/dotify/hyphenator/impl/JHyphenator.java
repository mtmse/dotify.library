package org.daisy.dotify.hyphenator.impl;

import ch.sbs.jhyphen.Hyphenator;
import org.daisy.dotify.api.hyphenator.HyphenatorConfigurationException;

import java.io.File;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

class JHyphenator extends AbstractHyphenator {
    private final static char SHY = '\u00AD';
    private final static char ZWSP = '\u200B';
    private Hyphenator instance;

	JHyphenator(String locale) throws HyphenatorConfigurationException {
	    try {
            ResourceBundle p = PropertyResourceBundle.getBundle("org/daisy/dotify/hyphenator/impl/JHyphenator");
            instance = new Hyphenator(new File("/usr/share/hyphen/", p.getString(locale)));
        } catch (Exception e) {
	        throw new JHyphenatorConfigurationException(e);
        }
	}

	@Override
	public String hyphenate(String phrase) {
		return ensureStartAndEnd(instance.hyphenate(phrase, SHY, ZWSP));
	}

    protected String ensureStartAndEnd(String hyphenate) {
	    StringBuilder sb = new StringBuilder();
	    boolean first = true;
	    for (String s : hyphenate.split(" ")) {
	        if (!first) sb.append(" ");
	        sb.append(handleWord(s));
	        first = false;
        }
	    return sb.toString();
    }

    private String handleWord(String s) {

	    if(s.length() <= getBeginLimit()) return s.replaceAll("" + SHY, "");
	    String start = s.substring(0, getBeginLimit());
	    String rest = s.substring(getBeginLimit());
	    s = start.replaceAll("" + SHY, "") + rest;

        if(s.length() <= getEndLimit()) return s.replaceAll("" + SHY, "");
        int endIndex = s.length() - getEndLimit() - 1;
        rest = s.substring(0, endIndex);
        String end = s.substring(endIndex);

	    return rest + end.replaceAll("" + SHY, "");
    }

}
