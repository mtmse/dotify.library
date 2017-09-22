package org.daisy.braille.impl.embosser;

import static org.junit.Assert.*;

import org.junit.Test;
@SuppressWarnings("javadoc")
public class DotMapperTest {

	@Test
	public void testMapper() {
		DotMapper ced = new DotMapper(16);
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
		DotMapper ced = new DotMapper(16, DotMapperConfiguration.builder().cellHeight(4).build());
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
	public void testForDotGraphic() {
		DotMapper mapper = new DotMapper(8, DotMapperConfiguration.builder()
				.baseCharacter('@')
				.cellHeight(4)
				.cellWidth(1)
				.map(new int[]{1,2,4,8})
				.build());
		mapper.write("⠈⠚⠬⠾⣈⣚⣬⣾");
		mapper.flush();
		assertEquals("@ABCDEFGHIJKLMNO", mapper.getFirstRow());
	}

	@Test
	public void testTrimTrailing() {
		DotMapper mapper1 = new DotMapper(8, DotMapperConfiguration.builder()
				.baseCharacter('@')
				.cellHeight(4)
				.cellWidth(1)
				.map(new int[]{1,2,4,8})
				.build());
		assertEquals("When the width of the mapper is 1 then the trimmed string should be an even "
				+ "number of characters.", "ABC@", mapper1.trimTrailing("ABC@@@"));
		DotMapper mapper2 = new DotMapper(8, DotMapperConfiguration.builder()
				.baseCharacter('@')
				.cellHeight(4)
				.cellWidth(2)
				.map(new int[]{1,2,4,8})
				.build());
		assertEquals("When the width of the mapper is 2, each character is a braille cell.", "ABC", mapper2.trimTrailing("ABC@@@"));
	}

}
