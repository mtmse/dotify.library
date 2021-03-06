package org.daisy.dotify.hyphenator.impl;

import org.daisy.dotify.api.hyphenator.HyphenatorConfigurationException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

class LatexHyphenatorCore {


    private static LatexHyphenatorCore instance;

    private final Map<String, HyphenationConfig> map;
    private final Logger logger;
    private final LatexRulesLocator locator;

    private LatexHyphenatorCore() {
        logger = Logger.getLogger(this.getClass().getCanonicalName());
        map = new HashMap<>();
        locator = new LatexRulesLocator();
    }

    static synchronized LatexHyphenatorCore getInstance() {
        if (instance == null) {
            instance = new LatexHyphenatorCore();
        }
        return instance;
    }

    boolean supportsLocale(String locale) {
        return locator.supportsLocale(locale);
    }

    private HyphenationConfig getConfiguration(String locale) {
        Properties props = locator.getProperties(locale);
        if (props != null) {
            return new HyphenationConfig(props);
        }
        return null;
    }

    HyphenationConfig getHyphenator(String locale) throws HyphenatorConfigurationException {
        HyphenationConfig hyph = map.get(locale);
        if (hyph != null) {
            return hyph;
        } else {
            HyphenationConfig props = getConfiguration(locale);
            if (props == null) {
                throw new LatexHyphenatorConfigurationException("Locale not supported: " + locale.toString());
            } else {
                logger.fine("Loading hyphenation for " + locale + " with properties " + props);
                map.put(locale, props);
                return props;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public Collection<String> listLocales() {
        return (Collection<String>) locator.listLocales();
    }

}
