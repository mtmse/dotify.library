package org.daisy.dotify.hyphenator.impl;

import org.daisy.dotify.api.hyphenator.HyphenatorConfigurationException;
import org.daisy.dotify.api.hyphenator.HyphenatorFactory;
import org.daisy.dotify.api.hyphenator.HyphenatorInterface;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides a hyphenator factory that uses jhyphen.
 */
public class SimpleHyphenatorFactory implements HyphenatorFactory {
    private static Map<String, SimpleHyphenator> hyphenatorMap = new HashMap<>();

    @Override
    public HyphenatorInterface newHyphenator(String locale) throws HyphenatorConfigurationException {
        if (!hyphenatorMap.containsKey(locale)) {
            hyphenatorMap.put(locale, new SimpleHyphenator(locale));
        }
        return hyphenatorMap.get(locale);
    }

    @Override
    public Object getFeature(String key) {
        return null;
    }

    @Override
    public void setFeature(String key, Object value) throws HyphenatorConfigurationException {
        throw new SimpleHyphenatorConfigurationException();
    }

}
