package org.daisy.dotify.api.translator;


/**
 * Provides an interface for braille translation and hyphenation for a particular
 * locale. The locale is determined when the translator is instantiated
 * by the factory.
 *
 * @author Joel Håkansson
 */
public interface BrailleTranslator {

    /**
     * Translates a text into braille using the supplied specification.
     *
     * @param specification the specification
     * @return a translator result
     * @throws TranslationException     if the locale is not supported by the implementation
     * @throws IllegalArgumentException if the sum of all attributes length is not equal to the
     *                                  length of the text
     */
    public BrailleTranslatorResult translate(Translatable specification) throws TranslationException;

    /**
     * Translates a text into braille using the supplied specification.
     *
     * @param specification the specification
     * @return a translator result
     * @throws TranslationException     if the locale is not supported by the implementation
     * @throws IllegalArgumentException if the sum of all attributes length is not equal to the
     *                                  length of the text
     */
    public BrailleTranslatorResult translate(TranslatableWithContext specification) throws TranslationException;

    /**
     * Gets the translator mode of this translator.
     *
     * @return returns the translator mode
     */
    public String getTranslatorMode();

}
