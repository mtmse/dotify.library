package org.daisy.braille.utils.pef;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.daisy.braille.utils.api.validator.ValidatorFactory;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class PEFValidatorTest {

	@Test
	public void testFactory_01() {
		ValidatorFactory vf = ValidatorFactory.newInstance();
		assertEquals(2, vf.list().size());
	}

	@Test
	public void testFactory_02() {
		ValidatorFactory vf = ValidatorFactory.newInstance();
		assertNotNull(vf.newValidator("application/x-pef+xml"));
	}

	@Test
	public void testValidationValid() throws IOException {
		URL input = this.getClass().getResource("resource-files/PEFBookTestInput.pef");
		PEFValidator v = new PEFValidator();
		boolean ret = v.validate(input);

		InputStreamReader is = new InputStreamReader(v.getReportStream(), "utf-8");
		int r;
		int c = 0;
		while ((r = is.read()) > -1) {
			c++;
			System.out.print((char) r);
		}
		assertEquals(0, c);
		assertTrue(ret);
	}

	@Test
	public void testValidationNotValid() throws IOException {
		URL input = this.getClass().getResource("resource-files/PEFBookTestInputNotValid.pef");
		PEFValidator v = new PEFValidator();
		assertTrue(!v.validate(input));

		InputStreamReader is = new InputStreamReader(v.getReportStream(), "utf-8");
		int r;
		while ((r = is.read()) > -1) {
			System.out.print((char) r);
		}
	}

	public static void main(String[] args) throws IOException {
		URL input = PEFValidatorTest.class.getResource("resource-files/PEFBookTestInput.pef");
		PEFValidator v = new PEFValidator();
		boolean ret = v.validate(input);

		InputStream is = v.getReportStream();
		int r;
		while ((r = is.read()) > -1) {
			System.out.print((char) r);
		}
		System.out.println(ret);
	}
}
