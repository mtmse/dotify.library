package org.daisy.dotify.hyphenator.impl;

import org.daisy.dotify.api.hyphenator.HyphenatorConfigurationException;

class SimpleHyphenatorConfigurationException extends
        HyphenatorConfigurationException {

    /**
     *
     */
    private static final long serialVersionUID = 2975917855861633456L;

    SimpleHyphenatorConfigurationException() {
        super();
    }

    SimpleHyphenatorConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    SimpleHyphenatorConfigurationException(String message) {
        super(message);
    }

    SimpleHyphenatorConfigurationException(Throwable cause) {
        super(cause);
    }
}
