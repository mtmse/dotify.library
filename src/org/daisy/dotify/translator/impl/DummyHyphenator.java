package org.daisy.dotify.translator.impl;

public class DummyHyphenator implements org.daisy.dotify.api.hyphenator.HyphenatorInterface {
    @Override
    public String hyphenate(String phrase) {
        return phrase;
    }

    @Override
    public int getBeginLimit() {
        return 0;
    }

    @Override
    public void setBeginLimit(int beginLimit) {
    }

    @Override
    public int getEndLimit() {
        return 0;
    }

    @Override
    public void setEndLimit(int endLimit) {
    }
}
