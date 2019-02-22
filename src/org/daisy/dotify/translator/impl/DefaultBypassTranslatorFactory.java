package org.daisy.dotify.translator.impl;

import org.daisy.dotify.api.hyphenator.HyphenatorFactoryMakerService;
import org.daisy.dotify.api.translator.BrailleTranslator;
import org.daisy.dotify.api.translator.BrailleTranslatorFactory;
import org.daisy.dotify.api.translator.TranslatorConfigurationException;
import org.daisy.dotify.api.translator.TranslatorType;
import org.daisy.dotify.common.text.IdentityFilter;
import org.daisy.dotify.translator.DefaultBrailleFilter;
import org.daisy.dotify.translator.SimpleBrailleTranslator;

class DefaultBypassTranslatorFactory implements BrailleTranslatorFactory {
	private final HyphenatorFactoryMakerService hyphenatorService;

	DefaultBypassTranslatorFactory(HyphenatorFactoryMakerService hyphenatorService) {
		this.hyphenatorService = hyphenatorService;
	}

	@Override
	public BrailleTranslator newTranslator(String locale, String mode) throws TranslatorConfigurationException {
		if (hyphenatorService == null) {
			throw new DefaultBypassTranslatorConfigurationException("HyphenatorFactoryMakerService not set.");
		} else if (mode.equals(TranslatorType.BYPASS.toString())) {
			return new SimpleBrailleTranslator(
					new DefaultBrailleFilter(new IdentityFilter(), locale, hyphenatorService),
					mode);
		}
		throw new DefaultBypassTranslatorConfigurationException("Factory does not support " + locale + "/" + mode);
	}
	
	private class DefaultBypassTranslatorConfigurationException extends TranslatorConfigurationException {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7094151522287723445L;

		private DefaultBypassTranslatorConfigurationException(String message) {
			super(message);
		}
	}

}
