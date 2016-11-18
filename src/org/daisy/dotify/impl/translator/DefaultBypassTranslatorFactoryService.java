package org.daisy.dotify.impl.translator;

import java.util.ArrayList;
import java.util.Collection;

import org.daisy.dotify.api.hyphenator.HyphenatorFactoryMakerService;
import org.daisy.dotify.api.translator.BrailleTranslatorFactory;
import org.daisy.dotify.api.translator.BrailleTranslatorFactoryService;
import org.daisy.dotify.api.translator.TranslatorSpecification;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

/**
 * Provides a pass through translator factory service.
 * @author Joel Håkansson
 *
 */
@Component
public class DefaultBypassTranslatorFactoryService implements
		BrailleTranslatorFactoryService {

	private HyphenatorFactoryMakerService hyphenator = null;

	@Override
	public boolean supportsSpecification(String locale, String mode) {
		return mode.equals(BrailleTranslatorFactory.MODE_BYPASS);
	}

	@Override
	public BrailleTranslatorFactory newFactory() {
		return new DefaultBypassTranslatorFactory(hyphenator);
	}

	/**
	 * Sets the hyphenator factory maker service.
	 * @param hyphenator the hyphenator factory maker service.
	 */
	@Reference
	public void setHyphenator(HyphenatorFactoryMakerService hyphenator) {
		this.hyphenator = hyphenator;
	}

	/**
	 * Unsets the hyphenator factory maker service.
	 * @param hyphenator the instance to unset.
	 */
	public void unsetHyphenator(HyphenatorFactoryMakerService hyphenator) {
		this.hyphenator = null;
	}

	@Override
	public Collection<TranslatorSpecification> listSpecifications() {
		ArrayList<TranslatorSpecification> ret = new ArrayList<>();
		for (String loc : hyphenator.listLocales()) {
			ret.add(new TranslatorSpecification(loc, BrailleTranslatorFactory.MODE_BYPASS));
		}
		return ret;
	}

	@Override
	public void setCreatedWithSPI() {
		setHyphenator(SPIHelper.getHyphenatorFactoryMakerService());
	}

}
