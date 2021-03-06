package org.daisy.dotify.api.text;


/**
 * Provides an integer2text.
 *
 * @author Joel Håkansson
 */
public interface Integer2Text {

    /**
     * Converts the integer to text.
     *
     * @param value the integer value
     * @return the integer as text
     * @throws IntegerOutOfRange If value is out of range of the implementations
     *                           capabilities.
     */
    public String intToText(int value) throws IntegerOutOfRange;

}
