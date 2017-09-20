package com_indexbraille;

import org.daisy.braille.utils.api.factory.FactoryProperties;
import org.junit.Test;
import static org.junit.Assert.*;

public class IndexV4EmbosserTest {

	@Test
	public void testTableFilter() {
		assertTrue(IndexV4Embosser.tableFilter.accept(new FactoryProperties(){

			@Override
			public String getIdentifier() {
				return IndexV4Embosser.TABLE6DOT;
			}

			@Override
			public String getDisplayName() {
				return null;
			}

			@Override
			public String getDescription() {
				return null;
			}
		}));
		assertFalse(IndexV4Embosser.tableFilter.accept(new FactoryProperties() {
			
			@Override
			public String getIdentifier() {
				return "no-match";
			}
			
			@Override
			public String getDisplayName() {
				return null;
			}
			
			@Override
			public String getDescription() {
				return null;
			}
		}));
	}

}
