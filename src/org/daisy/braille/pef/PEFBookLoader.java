package org.daisy.braille.pef;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

/**
 * Provides a loader for PEFBooks that keeps serialized PEFBooks that can be loaded 
 * quickly when the source is unchanged.
 *  
 * @author Joel Håkansson
 *
 */
public class PEFBookLoader {
	private static final Logger logger = Logger.getLogger(PEFBookLoader.class.getCanonicalName());
	private final File dir;

	public PEFBookLoader() {
		this(new File(System.getProperty("java.io.tmpdir")));
	}

	public PEFBookLoader(File dir) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Storing loaded PEF-files in " + dir);
		}
		this.dir = dir;
	}

	public PEFBook load(File f) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		File serial = new File(dir, f.getName()+"-"+f.hashCode()+".v3meta");
		PEFBook book;
		if (serial.exists() && serial.lastModified()>f.lastModified()) {
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(serial))) {
				book = (PEFBook)ois.readObject();
			} catch (Exception e) {
				logger.log(Level.WARNING, "Failed to deserialize: " + serial, e);
				book = PEFBook.load(f.toURI());
				if (!serial.delete()) {
					serial.deleteOnExit();
				}
			}
		} else {
			book = PEFBook.load(f.toURI());
			try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(serial))) {
				oos.writeObject(book);
			}
		}
		return book;
	}
}
