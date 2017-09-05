/*
 * Braille Utils (C) 2010-2011 Daisy Consortium 
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.daisy.braille.pef;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.daisy.braille.api.factory.AbstractFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.thaiopensource.util.PropertyMap;
import com.thaiopensource.util.PropertyMapBuilder;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.validate.ValidationDriver;

/**
 * Validates PEF-documents against the official Relax NG schema. Optionally performs additional
 * checks, see the different modes. 
 * @author Joel Håkansson
 */
public class PEFValidator extends AbstractFactory implements org.daisy.braille.api.validator.Validator {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5109400956885804582L;
	/**
	 * Key for getFeature/setFeature,
	 * corresponding value should be a {@link Mode} value
	 */
	public static final String FEATURE_MODE = "validator mode";
	/**
	 * Defines the modes available to the validator.
	 */
	public enum Mode {
		/**
		 * Light mode validation only validates the document against the Relax NG schema
		 */
		LIGHT_MODE("resource-files/pef-2008-1-light.rng", false), 
		/**
		 * In addition to schema validation, performs other tests required by the PEF-specification.
		 */
		FULL_MODE("resource-files/pef-2008-1-full.rng", true);
		private final String schemaPath;
		private final boolean hasSchematron;
		Mode(String schemaPath, boolean hasSchematron) {
			this.schemaPath = schemaPath;
			this.hasSchematron = hasSchematron;
		}
	}
	private static final Logger logger = Logger.getLogger(PEFValidator.class.getCanonicalName());
	private ByteArrayOutputStream report;
	private Mode mode;

	/**
	 * Creates a new PEFValidator
	 */
	public PEFValidator() {
		this(PEFValidator.class.getCanonicalName());
	}

	PEFValidator(String id) {
		super("PEF Validator", "A validator for PEF 1.0 files.", id);
		this.report = null;
		this.mode = Mode.FULL_MODE;
	}

	@Override
	public boolean validate(URL input) {
		return validate(input, mode);
	}

	private boolean validate(URL input, Mode modeLocal) {
		report = new ByteArrayOutputStream();

		try (PrintStream ps = new PrintStream(report, false, "utf-8")) {
			TestError errorHandler = new TestError(ps);
			boolean ok;
			ok = runValidation(input, this.getClass().getResource(modeLocal.schemaPath), errorHandler);
			if (modeLocal.hasSchematron) {
				try {
					File schematron = transformSchematron(this.getClass().getResource(modeLocal.schemaPath));
					ok &= runValidation(input, schematron.toURI().toURL(), errorHandler);
				} catch (Exception e) {
					logger.log(Level.WARNING, "Validation failed.", e);
					ok = false;
				}
			}
			return ok;
		} catch (UnsupportedEncodingException e1) {
			//With utf-8 this should never happen, but if the value above is changed for some reason, it might, so we'll log a descriptive error message.
			logger.log(Level.WARNING, "Unsupported encoding.", e1);
			return false;
		}
	}

	private boolean runValidation(URL url, URL schema, TestError errorHandler) {
		PropertyMapBuilder propertyBuilder = new PropertyMapBuilder();

		propertyBuilder.put(ValidateProperty.ERROR_HANDLER, errorHandler);
		PropertyMap map = propertyBuilder.toPropertyMap();
		ValidationDriver vd = new ValidationDriver(map);
		try {
			vd.loadSchema(new InputSource(schema.openStream()));
			return vd.validate(new InputSource(url.openStream()));
		} catch (SAXException | IOException e) {
			logger.log(Level.WARNING, "Valdation failed.", e);
		}
		return false;
	}

	private File transformSchematron(URL schema) throws IOException, SAXException, TransformerException {

		InputSource schSource;
		File schematronSchema = File.createTempFile("schematron", ".tmp");
		schematronSchema.deleteOnExit();

		// Use XSLT to strip out Schematron rules
		Source xml = new StreamSource(schema.toString());

		Source xslt = new StreamSource(this.getClass().getResourceAsStream("resource-files/RNG2Schtrn.xsl"));
		TransformerFactory factory = TransformerFactory.newInstance();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine(this.getClass() + " is using transformer factory: " + factory.getClass().getName());
		}
		try {
			factory.setAttribute("http://saxon.sf.net/feature/version-warning", Boolean.FALSE);
		} catch (IllegalArgumentException iae) {
			logger.log(Level.FINE, "Failed to set saxon feature warning flag.", iae);
		}
		Transformer transformer = factory.newTransformer(xslt);

		transformer.transform(xml, new StreamResult(schematronSchema.toURI().toString()));
		schSource = new InputSource(schematronSchema.toURI().toString());
		schSource.setSystemId(schematronSchema.toURI().toString());

		return schematronSchema;
	}

	static class TestError implements ErrorHandler {
		private boolean hasErrors = false;
		private final PrintStream printStream;

		TestError(PrintStream writer) {
			this.printStream = writer;
		}

		@Override
		public void warning(SAXParseException exception) throws SAXException {
			buildErrorMessage("Warning", exception);
		}

		@Override
		public void error(SAXParseException exception) throws SAXException {
			hasErrors = true;
			buildErrorMessage("Error", exception);
		}

		@Override
		public void fatalError(SAXParseException exception) throws SAXException {
			hasErrors = true;
			buildErrorMessage("Fatal error", exception);
		}

		public boolean hasErrors() {
			return hasErrors;
		}

		private void buildErrorMessage(String type, SAXParseException e) {
			int line = e.getLineNumber();
			int column = e.getColumnNumber();
			printStream.print(type);
			if (line > -1 || column > -1) {
				printStream.print(" at");
				if (line > -1) {
					printStream.print(" line " + line);
				}
				if (line > -1 && column > -1) {
					printStream.print(",");
				}
				if (column > -1) {
					printStream.print(" column " + column);
				}
			}
			printStream.println(": " + e.getMessage());
		}
	}

	@Override
	public InputStream getReportStream() {
		if (report==null) {
			return null;
		}
		return new ByteArrayInputStream(report.toByteArray());	
	}

	@Override
	public Object getFeature(String key) {
		if (FEATURE_MODE.equals(key)) {
			return mode;
		} else {
			throw new IllegalArgumentException("Unknown feature: '" + key +"'");
		}
	}

	@Override
	public Object getProperty(String key) {
		return null;
	}

	@Override
	public void setFeature(String key, Object value) {
		if (FEATURE_MODE.equals(key)) {
			try {
				mode = (Mode)value;
			} catch (ClassCastException e) {
				throw new IllegalArgumentException("Unsupported value for " + FEATURE_MODE + " '" + value + "'", e);
			}
		} else {
			throw new IllegalArgumentException("Unknown feature: '" + key +"'");
		}

	}

}
