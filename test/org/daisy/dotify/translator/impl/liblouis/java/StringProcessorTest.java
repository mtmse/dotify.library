package org.daisy.dotify.translator.impl.liblouis.java;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
@SuppressWarnings("javadoc")
public class StringProcessorTest {

	@Test
	public void test() {
		assertEquals("\\\f\n\r \t\0x0B\0x1B\r\r", StringProcessor.unescape("\\\\\\f\\n\\r\\s\\t\\v\\e\\x000D\\x000d"));
	}
}
