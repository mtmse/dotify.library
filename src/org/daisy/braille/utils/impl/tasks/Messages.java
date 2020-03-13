package org.daisy.braille.utils.impl.tasks;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

enum Messages {
    TITLE_BRF_TO_PEF("title-brf-to-pef"),
    LABEL_TABLE("label-table"),
    LABEL_IDENTIFIER("label-identifier"),
    LABEL_DATE("label-date"),
    LABEL_AUTHOR("label-author"),
    LABEL_TITLE("label-title"),
    LABEL_LANGUAGE("label-language"),
    LABEL_DUPLEX("label-duplex"),
    BOOLEAN_ON("boolean-on"),
    BOOLEAN_OFF("boolean-off");

    private static final String BUNDLE_NAME = Messages.class.getPackage().getName() + ".messages"; //$NON-NLS-1$
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault());
    private final String key;

    private Messages(String key) {
        this.key = key;
    }

    private static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }

    /**
     * Localizes the message without any variables.
     *
     * @return returns the localized message
     */
    String localize() {
        return getString(key);
    }

    /**
     * Localizes the message with the specified variables.
     *
     * @param variables the variables to insert into the localized message
     * @return returns the localized message
     */
    String localize(Object... variables) {
        return MessageFormat.format(localize(), variables);
    }
}
