package org.daisy.dotify.impl.translator;

import org.daisy.dotify.api.translator.TextBorderFactory;
import org.daisy.dotify.api.translator.TextBorderFactoryService;

import aQute.bnd.annotation.component.Component;

/**
 * Provides a braille text border factory service.
 * @author Joel Håkansson
 */
@Component
public class BrailleTextBorderFactoryService implements
		TextBorderFactoryService {

	@Override
	public TextBorderFactory newFactory() {
		return new BrailleTextBorderFactory();
	}

	@Override
	public void setCreatedWithSPI() {
	}

}
