package org.daisy.braille.utils.pef;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.daisy.braille.utils.api.table.TableCatalogService;

/**
 * Provides a facade for text converter.
 * @author Joel Håkansson
 * @deprecated use {@link TextHandler}
 */
@Deprecated
public class TextConverterFacade {
	/**
	 * Defines a date format (yyyy-MM-dd)
	 */
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	/**
	 * Key for parseTextFile setting,
	 * corresponding settings value should contain the title of the publication
	 */
	public static final String KEY_TITLE = "title";
	/**
	 * Key for parseTextFile setting,
	 * corresponding settings value should contain the author of the publication
	 */
	public static final String KEY_AUTHOR = "author";
	/**
	 * Key for parseTextFile setting,
	 * corresponding settings value should contain the identifier for the publication 
	 */
	public static final String KEY_IDENTIFIER = "identifier";
	/**
	 * Key for parseTextFile setting,
	 * corresponding settings value should match the table to use
	 */
	public static final String KEY_MODE = "mode";
	/**
	 * Key for parseTextFile setting,
	 * corresponding settings value should contain the language of the publication
	 */
	public static final String KEY_LANGUAGE = "language";
	/**
	 * Key for parseTextFile setting,
	 * corresponding settings value should be "true" for duplex or "false" for simplex
	 */
	public static final String KEY_DUPLEX = "duplex";
	/**
	 * Key for parseTextFile setting,
	 * corresponding settings value should be a string containing a valid date on the form yyyy-MM-dd
	 */
	public static final String KEY_DATE = "date";

	private final TableCatalogService factory;

	/**
	 * Creates a new text converter facade with the specified
	 * table catalog.
	 * @param factory the table catalog
	 */
	public TextConverterFacade(TableCatalogService factory) {
		this.factory = factory;
	}

	/**
	 * Parses a text file and outputs a PEF-file based on the contents of the file
	 * @param input input text file
	 * @param output output PEF-file
	 * @param settings settings
	 * @throws IOException if IO fails
	 */
	public void parseTextFile(File input, File output, Map<String, String> settings) throws IOException {
		new TextHandler.Builder(input, output, factory)
			.options(settings)
			.build()
			.parse();
	}	
}
