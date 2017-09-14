package org.daisy.braille.pef;

import java.io.InputStream;

/**
 * Provides an interface for an input stream maker.
 * @author Joel Håkansson
 *
 */
public interface NormalizationResource {

	/**
	 * Creates a new input stream for this resource.
	 * @return returns a new input stream
	 */
	public InputStream getNormalizationResourceAsStream();
}