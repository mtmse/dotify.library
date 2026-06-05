package org.daisy.dotify.hyphenator.impl;

import org.daisy.dotify.api.hyphenator.HyphenatorFactory;
import org.daisy.dotify.api.hyphenator.HyphenatorFactoryService;
import org.osgi.service.component.annotations.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Provides a hyphenator factory service for latex hyphenation rules.
 *
 * @author Joel Håkansson
 */
@Component
public class SimpleHyphenatorFactoryService implements HyphenatorFactoryService {
    private final List<String> locales;

    /**
     * Creates a new instance.
     */
    public SimpleHyphenatorFactoryService() {
        locales = new ArrayList<>();

        ResourceBundle p = PropertyResourceBundle.getBundle("org/daisy/dotify/hyphenator/impl/SimpleHyphenator");
        Enumeration<String> keyEnum = p.getKeys();
        while (keyEnum.hasMoreElements()) {
            String key = keyEnum.nextElement();
            String patternPath = "resource-files/dict/" + p.getString(key);
            if (SimpleHyphenator.resourceExist(patternPath)) {
                locales.add(key);
            }
        }
    }

    @Override
    public boolean supportsLocale(String locale) {
        return locales.contains(locale);
    }

    @Override
    public HyphenatorFactory newFactory() {
        return new SimpleHyphenatorFactory();
    }

    @Override
    public Collection<String> listLocales() {
        return locales;
    }

}
