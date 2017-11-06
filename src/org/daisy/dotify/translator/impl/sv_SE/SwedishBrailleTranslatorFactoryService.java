package org.daisy.dotify.translator.impl.sv_SE;

import java.util.ArrayList;
import java.util.Collection;

import org.daisy.dotify.api.hyphenator.HyphenatorFactoryMakerService;
import org.daisy.dotify.api.translator.BrailleTranslatorFactory;
import org.daisy.dotify.api.translator.BrailleTranslatorFactoryService;
import org.daisy.dotify.api.translator.TranslatorSpecification;
import org.daisy.dotify.translator.impl.SPIHelper;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

/**
 * Provides a Swedish braille translator factory service.
 * @author Joel Håkansson
 *
 */
@Component
public class SwedishBrailleTranslatorFactoryService implements
		BrailleTranslatorFactoryService {

	private HyphenatorFactoryMakerService hyphenator = null;
	private final ArrayList<TranslatorSpecification> specs;

	/**
	 * Creates a new Swedish braille translator factory service.
	 */
	public SwedishBrailleTranslatorFactoryService() {
		this.specs = new ArrayList<>();
		specs.add(new TranslatorSpecification("sv-SE", BrailleTranslatorFactory.MODE_UNCONTRACTED));
		specs.add(new TranslatorSpecification("sv-SE", SwedishBrailleTranslatorFactory.PRE_TRANSLATED));
	}
	
	@Override
	public boolean supportsSpecification(String locale, String mode) {
		TranslatorSpecification target = new TranslatorSpecification(locale, mode);
		for (TranslatorSpecification spec : specs) {
			if (target.equals(spec)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public BrailleTranslatorFactory newFactory() {
		return new SwedishBrailleTranslatorFactory(hyphenator);
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
		return specs;
	}

	@Override
	public void setCreatedWithSPI() {
		setHyphenator(SPIHelper.getHyphenatorFactoryMakerService());
	}

}
