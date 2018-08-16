package org.daisy.dotify.formatter.test;

import java.io.IOException;

import org.daisy.dotify.api.engine.LayoutEngineException;
import org.daisy.dotify.api.writer.PagedMediaWriterConfigurationException;
import org.junit.Test;
@SuppressWarnings("javadoc")
public class VerticalPositionTest extends AbstractFormatterEngineTest {

	@Test
	public void testVerticalPosition() throws LayoutEngineException, IOException, PagedMediaWriterConfigurationException {
		testPEF("resource-files/vertical-position-input.obfl", "resource-files/vertical-position-expected.pef", false);
	}
	
	@Test
	public void testVerticalPositionWithBorders() throws LayoutEngineException, IOException, PagedMediaWriterConfigurationException {
		testPEF("resource-files/vertical-position-borders-input.obfl", "resource-files/vertical-position-borders-expected.pef", false);
	}
	
	@Test
	public void testVerticalPositionBefore() throws LayoutEngineException, IOException, PagedMediaWriterConfigurationException {
		testPEF("resource-files/vertical-position-before-input.obfl", "resource-files/vertical-position-before-expected.pef", false);
	}
	
	@Test
	public void testVerticalPositionDLS() throws LayoutEngineException, IOException, PagedMediaWriterConfigurationException {
		testPEF("resource-files/vertical-position-dls-input.obfl", "resource-files/vertical-position-dls-expected.pef", false);
	}

	@Test
	public void testVerticalPositionWithHeader() throws LayoutEngineException, IOException, PagedMediaWriterConfigurationException {
		testPEF("resource-files/vertical-position-header-input.obfl", "resource-files/vertical-position-header-expected.pef", false);
	}
}
