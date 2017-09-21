package org.daisy.braille.impl.embosser;

import static org.junit.Assert.*;

import org.junit.Test;
@SuppressWarnings("javadoc")
public class EightToSixDotMapperTest {

	@Test
	public void testMapper() {
		EightToSixDotMapper ced = new EightToSixDotMapper(16);
		ced.write("⠀⠀⠀⠁⠀⠂⠀⠃⠀⠄⠀⠅⠀⠆⠀⠇");ced.newLine(1);
		ced.write("⠀⠈⠀⠉⠀⠊⠀⠋⠀⠌⠀⠍⠀⠎⠀⠏");ced.newLine(1);
		ced.write("⠀⠐⠀⠑⠀⠒⠀⠓⠀⠔⠀⠕⠀⠖⠀⠗");ced.newLine(1);
		ced.write("⠀⠘⠀⠙⠀⠚⠀⠛⠀⠜⠀⠝⠀⠞⠀⠟");ced.newLine(1);
		ced.write("⠀⠠⠀⠡⠀⠢⠀⠣⠀⠤⠀⠥⠀⠦⠀⠧");ced.newLine(1);
		ced.write("⠀⠨⠀⠩⠀⠪⠀⠫⠀⠬⠀⠭⠀⠮⠀⠯");ced.newLine(1);
		ced.write("⠀⠰⠀⠱⠀⠲⠀⠳⠀⠴⠀⠵⠀⠶⠀⠷");ced.newLine(1);
		ced.write("⠀⠸⠀⠹⠀⠺⠀⠻⠀⠼⠀⠽⠀⠾⠀⠿");
		ced.flush();

		assertEquals(ced.readLine(), "⠀⠀⠀⠁⠀⠂⠀⠃⠀⠄⠀⠅⠀⠆⠀⠇");
		assertEquals(ced.readLine(), "⠀⠠⠀⠤⠀⠠⠀⠤⠀⠠⠀⠤⠀⠠⠀⠤");
		assertEquals(ced.readLine(), "⠀⠀⠀⠀⠀⠁⠀⠁⠀⠂⠀⠂⠀⠃⠀⠃");
		assertEquals(ced.readLine(), "⠀⠠⠀⠢⠀⠤⠀⠦⠀⠠⠀⠢⠀⠤⠀⠦");
		assertEquals(ced.readLine(), "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠁⠀⠁⠀⠁⠀⠁");
		assertEquals(ced.readLine(), "⠀⠘⠀⠙⠀⠚⠀⠛⠀⠜⠀⠝⠀⠞⠀⠟");
		assertEquals(ced.readLine(), "⠀⠀⠀⠄⠀⠀⠀⠄⠀⠀⠀⠄⠀⠀⠀⠄");
		assertEquals(ced.readLine(), "⠀⠐⠀⠐⠀⠑⠀⠑⠀⠒⠀⠒⠀⠓⠀⠓");
		assertEquals(ced.readLine(), "⠀⠐⠀⠒⠀⠔⠀⠖⠀⠐⠀⠒⠀⠔⠀⠖");
		assertEquals(ced.readLine(), "⠀⠈⠀⠈⠀⠈⠀⠈⠀⠉⠀⠉⠀⠉⠀⠉");
		assertEquals(ced.readLine(), "⠀⠰⠀⠱⠀⠲⠀⠳⠀⠴⠀⠵⠀⠶⠀⠷");
		assertEquals(ced.readLine(), "⠀⠠⠀⠤⠀⠠⠀⠤⠀⠠⠀⠤⠀⠠⠀⠤");
		assertEquals(ced.readLine(), "⠀⠘⠀⠘⠀⠙⠀⠙⠀⠚⠀⠚⠀⠛⠀⠛");

		ced.write("⠀⡀⠀⡁⠀⡂⠀⡃⠀⡄⠀⡅⠀⡆⠀⡇");ced.newLine(1);
		ced.write("⠀⡈⠀⡉⠀⡊⠀⡋⠀⡌⠀⡍⠀⡎⠀⡏");ced.newLine(1);
		ced.write("⠀⡐⠀⡑⠀⡒⠀⡓⠀⡔⠀⡕⠀⡖⠀⡗");ced.newLine(1);
		ced.write("⠀⡘⠀⡙⠀⡚⠀⡛⠀⡜⠀⡝⠀⡞⠀⡟");ced.newLine(1);
		ced.write("⠀⡠⠀⡡⠀⡢⠀⡣⠀⡤⠀⡥⠀⡦⠀⡧");ced.newLine(1);
		ced.write("⠀⡨⠀⡩⠀⡪⠀⡫⠀⡬⠀⡭⠀⡮⠀⡯");ced.newLine(1);
		ced.write("⠀⡰⠀⡱⠀⡲⠀⡳⠀⡴⠀⡵⠀⡶⠀⡷");ced.newLine(1);
		ced.write("⠀⡸⠀⡹⠀⡺⠀⡻⠀⡼⠀⡽⠀⡾⠀⡿");
		ced.flush();

		assertEquals(ced.readLine(), "⠀⠀⠀⠁⠀⠂⠀⠃⠀⠄⠀⠅⠀⠆⠀⠇");
		assertEquals(ced.readLine(), "⠀⠡⠀⠥⠀⠡⠀⠥⠀⠡⠀⠥⠀⠡⠀⠥");
		assertEquals(ced.readLine(), "⠀⠄⠀⠄⠀⠅⠀⠅⠀⠆⠀⠆⠀⠇⠀⠇");
		assertEquals(ced.readLine(), "⠀⠠⠀⠢⠀⠤⠀⠦⠀⠠⠀⠢⠀⠤⠀⠦");
		assertEquals(ced.readLine(), "⠀⠂⠀⠂⠀⠂⠀⠂⠀⠃⠀⠃⠀⠃⠀⠃");
		assertEquals(ced.readLine(), "⠀⠘⠀⠙⠀⠚⠀⠛⠀⠜⠀⠝⠀⠞⠀⠟");
		assertEquals(ced.readLine(), "⠀⠁⠀⠅⠀⠁⠀⠅⠀⠁⠀⠅⠀⠁⠀⠅");
		assertEquals(ced.readLine(), "⠀⠔⠀⠔⠀⠕⠀⠕⠀⠖⠀⠖⠀⠗⠀⠗");
		assertEquals(ced.readLine(), "⠀⠐⠀⠒⠀⠔⠀⠖⠀⠐⠀⠒⠀⠔⠀⠖");
		assertEquals(ced.readLine(), "⠀⠊⠀⠊⠀⠊⠀⠊⠀⠋⠀⠋⠀⠋⠀⠋");
		assertEquals(ced.readLine(), "⠀⠰⠀⠱⠀⠲⠀⠳⠀⠴⠀⠵⠀⠶⠀⠷");
		assertEquals(ced.readLine(), "⠀⠡⠀⠥⠀⠡⠀⠥⠀⠡⠀⠥⠀⠡⠀⠥");
		assertEquals(ced.readLine(), "⠀⠜⠀⠜⠀⠝⠀⠝⠀⠞⠀⠞⠀⠟⠀⠟");

		ced.write("⠀⢀⠀⢁⠀⢂⠀⢃⠀⢄⠀⢅⠀⢆⠀⢇");ced.newLine(1);
		ced.write("⠀⢈⠀⢉⠀⢊⠀⢋⠀⢌⠀⢍⠀⢎⠀⢏");ced.newLine(1);
		ced.write("⠀⢐⠀⢑⠀⢒⠀⢓⠀⢔⠀⢕⠀⢖⠀⢗");ced.newLine(1);
		ced.write("⠀⢘⠀⢙⠀⢚⠀⢛⠀⢜⠀⢝⠀⢞⠀⢟");ced.newLine(1);
		ced.write("⠀⢠⠀⢡⠀⢢⠀⢣⠀⢤⠀⢥⠀⢦⠀⢧");ced.newLine(1);
		ced.write("⠀⢨⠀⢩⠀⢪⠀⢫⠀⢬⠀⢭⠀⢮⠀⢯");ced.newLine(1);
		ced.write("⠀⢰⠀⢱⠀⢲⠀⢳⠀⢴⠀⢵⠀⢶⠀⢷");ced.newLine(1);
		ced.write("⠀⢸⠀⢹⠀⢺⠀⢻⠀⢼⠀⢽⠀⢾⠀⢿");
		ced.flush();

		assertEquals(ced.readLine(), "⠀⠀⠀⠁⠀⠂⠀⠃⠀⠄⠀⠅⠀⠆⠀⠇");
		assertEquals(ced.readLine(), "⠀⠨⠀⠬⠀⠨⠀⠬⠀⠨⠀⠬⠀⠨⠀⠬");
		assertEquals(ced.readLine(), "⠀⠠⠀⠠⠀⠡⠀⠡⠀⠢⠀⠢⠀⠣⠀⠣");
		assertEquals(ced.readLine(), "⠀⠠⠀⠢⠀⠤⠀⠦⠀⠠⠀⠢⠀⠤⠀⠦");
		assertEquals(ced.readLine(), "⠀⠐⠀⠐⠀⠐⠀⠐⠀⠑⠀⠑⠀⠑⠀⠑");
		assertEquals(ced.readLine(), "⠀⠘⠀⠙⠀⠚⠀⠛⠀⠜⠀⠝⠀⠞⠀⠟");
		assertEquals(ced.readLine(), "⠀⠈⠀⠌⠀⠈⠀⠌⠀⠈⠀⠌⠀⠈⠀⠌");
		assertEquals(ced.readLine(), "⠀⠰⠀⠰⠀⠱⠀⠱⠀⠲⠀⠲⠀⠳⠀⠳");
		assertEquals(ced.readLine(), "⠀⠐⠀⠒⠀⠔⠀⠖⠀⠐⠀⠒⠀⠔⠀⠖");
		assertEquals(ced.readLine(), "⠀⠘⠀⠘⠀⠘⠀⠘⠀⠙⠀⠙⠀⠙⠀⠙");
		assertEquals(ced.readLine(), "⠀⠰⠀⠱⠀⠲⠀⠳⠀⠴⠀⠵⠀⠶⠀⠷");
		assertEquals(ced.readLine(), "⠀⠨⠀⠬⠀⠨⠀⠬⠀⠨⠀⠬⠀⠨⠀⠬");
		assertEquals(ced.readLine(), "⠀⠸⠀⠸⠀⠹⠀⠹⠀⠺⠀⠺⠀⠻⠀⠻");

		ced.write("⠀⣀⠀⣁⠀⣂⠀⣃⠀⣄⠀⣅⠀⣆⠀⣇");ced.newLine(1);
		ced.write("⠀⣈⠀⣉⠀⣊⠀⣋⠀⣌⠀⣍⠀⣎⠀⣏");ced.newLine(1);
		ced.write("⠀⣐⠀⣑⠀⣒⠀⣓⠀⣔⠀⣕⠀⣖⠀⣗");ced.newLine(1);
		ced.write("⠀⣘⠀⣙⠀⣚⠀⣛⠀⣜⠀⣝⠀⣞⠀⣟");ced.newLine(1);
		ced.write("⠀⣠⠀⣡⠀⣢⠀⣣⠀⣤⠀⣥⠀⣦⠀⣧");ced.newLine(1);
		ced.write("⠀⣨⠀⣩⠀⣪⠀⣫⠀⣬⠀⣭⠀⣮⠀⣯");ced.newLine(1);
		ced.write("⠀⣰⠀⣱⠀⣲⠀⣳⠀⣴⠀⣵⠀⣶⠀⣷");ced.newLine(1);
		ced.write("⠀⣸⠀⣹⠀⣺⠀⣻⠀⣼⠀⣽⠀⣾⠀⣿");
		ced.flush();

		assertEquals(ced.readLine(), "⠀⠀⠀⠁⠀⠂⠀⠃⠀⠄⠀⠅⠀⠆⠀⠇");
		assertEquals(ced.readLine(), "⠀⠩⠀⠭⠀⠩⠀⠭⠀⠩⠀⠭⠀⠩⠀⠭");
		assertEquals(ced.readLine(), "⠀⠤⠀⠤⠀⠥⠀⠥⠀⠦⠀⠦⠀⠧⠀⠧");
		assertEquals(ced.readLine(), "⠀⠠⠀⠢⠀⠤⠀⠦⠀⠠⠀⠢⠀⠤⠀⠦");
		assertEquals(ced.readLine(), "⠀⠒⠀⠒⠀⠒⠀⠒⠀⠓⠀⠓⠀⠓⠀⠓");
		assertEquals(ced.readLine(), "⠀⠘⠀⠙⠀⠚⠀⠛⠀⠜⠀⠝⠀⠞⠀⠟");
		assertEquals(ced.readLine(), "⠀⠉⠀⠍⠀⠉⠀⠍⠀⠉⠀⠍⠀⠉⠀⠍");
		assertEquals(ced.readLine(), "⠀⠴⠀⠴⠀⠵⠀⠵⠀⠶⠀⠶⠀⠷⠀⠷");
		assertEquals(ced.readLine(), "⠀⠐⠀⠒⠀⠔⠀⠖⠀⠐⠀⠒⠀⠔⠀⠖");
		assertEquals(ced.readLine(), "⠀⠚⠀⠚⠀⠚⠀⠚⠀⠛⠀⠛⠀⠛⠀⠛");
		assertEquals(ced.readLine(), "⠀⠰⠀⠱⠀⠲⠀⠳⠀⠴⠀⠵⠀⠶⠀⠷");
		assertEquals(ced.readLine(), "⠀⠩⠀⠭⠀⠩⠀⠭⠀⠩⠀⠭⠀⠩⠀⠭");
		assertEquals(ced.readLine(), "⠀⠼⠀⠼⠀⠽⠀⠽⠀⠾⠀⠾⠀⠿⠀⠿");

	}

	@Test
	public void testIdentityMapper() {
		EightToSixDotMapper ced = new EightToSixDotMapper.Builder(16).cellHeight(4).build();
		ced.write("⠀⠀⠀⠁⠀⠂⠀⠃⠀⠄⠀⠅⠀⠆⠀⠇");ced.newLine(0);
		ced.write("⠀⠈⠀⠉⠀⠊⠀⠋⠀⠌⠀⠍⠀⠎⠀⠏");ced.newLine(0);
		ced.write("⠀⠐⠀⠑⠀⠒⠀⠓⠀⠔⠀⠕⠀⠖⠀⠗");ced.newLine(0);
		ced.write("⠀⠘⠀⠙⠀⠚⠀⠛⠀⠜⠀⠝⠀⠞⠀⠟");ced.newLine(0);
		ced.write("⠀⠠⠀⠡⠀⠢⠀⠣⠀⠤⠀⠥⠀⠦⠀⠧");ced.newLine(0);
		ced.write("⠀⠨⠀⠩⠀⠪⠀⠫⠀⠬⠀⠭⠀⠮⠀⠯");ced.newLine(0);
		ced.write("⠀⠰⠀⠱⠀⠲⠀⠳⠀⠴⠀⠵⠀⠶⠀⠷");ced.newLine(0);
		ced.write("⠀⠸⠀⠹⠀⠺⠀⠻⠀⠼⠀⠽⠀⠾⠀⠿");ced.newLine(0);
		ced.write("⠀⣀⠀⣁⠀⣂⠀⣃⠀⣄⠀⣅⠀⣆⠀⣇");ced.newLine(0);
		ced.write("⠀⣈⠀⣉⠀⣊⠀⣋⠀⣌⠀⣍⠀⣎⠀⣏");ced.newLine(0);
		ced.write("⠀⣐⠀⣑⠀⣒⠀⣓⠀⣔⠀⣕⠀⣖⠀⣗");ced.newLine(0);
		ced.write("⠀⣘⠀⣙⠀⣚⠀⣛⠀⣜⠀⣝⠀⣞⠀⣟");ced.newLine(0);
		ced.write("⠀⣠⠀⣡⠀⣢⠀⣣⠀⣤⠀⣥⠀⣦⠀⣧");ced.newLine(0);
		ced.write("⠀⣨⠀⣩⠀⣪⠀⣫⠀⣬⠀⣭⠀⣮⠀⣯");ced.newLine(0);
		ced.write("⠀⣰⠀⣱⠀⣲⠀⣳⠀⣴⠀⣵⠀⣶⠀⣷");ced.newLine(0);
		ced.write("⠀⣸⠀⣹⠀⣺⠀⣻⠀⣼⠀⣽⠀⣾⠀⣿");
		ced.flush();
		assertEquals(ced.readLine(), "⠀⠀⠀⠁⠀⠂⠀⠃⠀⠄⠀⠅⠀⠆⠀⠇");
		assertEquals(ced.readLine(), "⠀⠈⠀⠉⠀⠊⠀⠋⠀⠌⠀⠍⠀⠎⠀⠏");
		assertEquals(ced.readLine(), "⠀⠐⠀⠑⠀⠒⠀⠓⠀⠔⠀⠕⠀⠖⠀⠗");
		assertEquals(ced.readLine(), "⠀⠘⠀⠙⠀⠚⠀⠛⠀⠜⠀⠝⠀⠞⠀⠟");
		assertEquals(ced.readLine(), "⠀⠠⠀⠡⠀⠢⠀⠣⠀⠤⠀⠥⠀⠦⠀⠧");
		assertEquals(ced.readLine(), "⠀⠨⠀⠩⠀⠪⠀⠫⠀⠬⠀⠭⠀⠮⠀⠯");
		assertEquals(ced.readLine(), "⠀⠰⠀⠱⠀⠲⠀⠳⠀⠴⠀⠵⠀⠶⠀⠷");
		assertEquals(ced.readLine(), "⠀⠸⠀⠹⠀⠺⠀⠻⠀⠼⠀⠽⠀⠾⠀⠿");
		assertEquals(ced.readLine(), "⠀⣀⠀⣁⠀⣂⠀⣃⠀⣄⠀⣅⠀⣆⠀⣇");
		assertEquals(ced.readLine(), "⠀⣈⠀⣉⠀⣊⠀⣋⠀⣌⠀⣍⠀⣎⠀⣏");
		assertEquals(ced.readLine(), "⠀⣐⠀⣑⠀⣒⠀⣓⠀⣔⠀⣕⠀⣖⠀⣗");
		assertEquals(ced.readLine(), "⠀⣘⠀⣙⠀⣚⠀⣛⠀⣜⠀⣝⠀⣞⠀⣟");
		assertEquals(ced.readLine(), "⠀⣠⠀⣡⠀⣢⠀⣣⠀⣤⠀⣥⠀⣦⠀⣧");
		assertEquals(ced.readLine(), "⠀⣨⠀⣩⠀⣪⠀⣫⠀⣬⠀⣭⠀⣮⠀⣯");
		assertEquals(ced.readLine(), "⠀⣰⠀⣱⠀⣲⠀⣳⠀⣴⠀⣵⠀⣶⠀⣷");
		assertEquals(ced.readLine(), "⠀⣸⠀⣹⠀⣺⠀⣻⠀⣼⠀⣽⠀⣾⠀⣿");
	}

	@Test
	public void testCheckBitmap() {
		EightToSixDotMapper.checkBitMap(EightToSixDotMapper.UNICODE_BIT_MAP);
		// if we're here, the test was successful
		assertTrue(true);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testCheckBitmapNonUnique() {
		EightToSixDotMapper.checkBitMap(new int[]{2, 2});
	}

	@Test(expected=IllegalArgumentException.class)
	public void testCheckBitmapPowerOfTwo() {
		EightToSixDotMapper.checkBitMap(new int[]{3});
	}
	
	@Test
	public void testForDotGraphic() {
		EightToSixDotMapper mapper = new EightToSixDotMapper.Builder(8)
				.baseCharacter('@')
				.cellHeight(4)
				.cellWidth(1)
				.bitMap(new int[]{1,2,4,8})
				.build();
		mapper.write("⠈⠚⠬⠾⣈⣚⣬⣾");
		mapper.flush();
		assertEquals("@ABCDEFGHIJKLMNO", mapper.getFirstRow());
	}

}
