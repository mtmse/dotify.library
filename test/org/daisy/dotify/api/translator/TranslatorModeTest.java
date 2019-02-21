package org.daisy.dotify.api.translator;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.daisy.dotify.api.translator.TranslatorMode.DotsPerCell;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class TranslatorModeTest {
	
	@Test
	public void test_01() {
		TranslatorMode m = TranslatorMode.withGrade(1);
		
		assertEquals("grade:1", m.getIdentifier());
		assertEquals(TranslatorType.CONTRACTED, m.getType().get());
		assertEquals(1, m.getContractionGrade().get().doubleValue(), 0);
		assertEquals(Optional.empty(), m.getDotsPerCell());
	}
	
	@Test
	public void test_02() {
		TranslatorMode m = TranslatorMode.withGrade(1.5);
		
		assertEquals("grade:1.5", m.getIdentifier());
		assertEquals(TranslatorType.CONTRACTED, m.getType().get());
		assertEquals(1.5, m.getContractionGrade().get().doubleValue(), 0);
		assertEquals(Optional.empty(), m.getDotsPerCell());
	}
	
	@Test
	public void test_03() {
		String input = "uncontracted";
		TranslatorMode m = TranslatorMode.parse(input);

		assertEquals(input, m.getIdentifier());
		assertEquals(TranslatorType.UNCONTRACTED, m.getType().get());
		assertEquals(Optional.empty(), m.getContractionGrade());
		assertEquals(Optional.empty(), m.getDotsPerCell());
	}
	
	@Test
	public void test_04() {
		String input = "grade:1";
		TranslatorMode m = TranslatorMode.parse(input);

		assertEquals(input, m.getIdentifier());
		assertEquals(TranslatorType.CONTRACTED, m.getType().get());
		assertEquals(1, m.getContractionGrade().get().doubleValue(), 0);
		assertEquals(Optional.empty(), m.getDotsPerCell());
	}
	
	@Test
	public void test_05() {
		String input = "uncontracted/8-dot";
		TranslatorMode m = TranslatorMode.parse(input);
		
		assertEquals(input, m.getIdentifier());
		assertEquals(TranslatorType.UNCONTRACTED, m.getType().get());
		assertEquals(Optional.empty(), m.getContractionGrade());
		assertEquals(DotsPerCell.EIGHT, m.getDotsPerCell().get());
	}

}
