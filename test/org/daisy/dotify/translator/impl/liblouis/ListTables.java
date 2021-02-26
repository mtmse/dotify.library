package org.daisy.dotify.translator.impl.liblouis;

import org.liblouis.CompilationException;
import org.liblouis.Louis;
import org.liblouis.Translator;

@SuppressWarnings("javadoc")
public class ListTables {

	public static void main(String[] args) {
		Louis.listTables().stream()
			.map(TblInfo::new)
			.filter(v->"literary".equals(v.type)&&!"sv".equals(v.locale))
			.sorted((o1, o2)-> o1.locale.compareTo(o2.locale))
			.forEach(tii->{
				String path = null;
				try {
					path = tii.table.getTranslator().getTable();
				} catch (CompilationException e2) {
					System.out.println("Comp failed");
				}
				if (path!=null) {
					try {
						new Translator(path);
						String prefix ="specs.put(";
						String postfix = ", \"" +path+"\");";
						String eightDot = tii.eightDot?".dotsPerCell(DotsPerCell.EIGHT)":"";
						if ("full".equalsIgnoreCase(tii.contraction)) {
							System.out.println(
								String.format("%snew TranslatorSpecification(\"%s\", TranslatorMode.Builder.withType(TranslatorType.CONTRACTED)%s.build())%s", prefix, tii.locale, eightDot, postfix)
							);
						} else if ("no".equalsIgnoreCase(tii.contraction)) {
							System.out.println(
								String.format("%snew TranslatorSpecification(\"%s\", TranslatorMode.Builder.withType(TranslatorType.UNCONTRACTED)%s.build())%s", prefix, tii.locale, eightDot, postfix)
							);
						}
						if (tii.grade!=null) {
							try {
								double grade = Double.parseDouble(tii.grade);
								System.out.println(String.format("%snew TranslatorSpecification(\"%s\", TranslatorMode.Builder.withGrade(%s)%s.build())%s", prefix, tii.locale, grade, eightDot, postfix));
							} catch (NumberFormatException e) {
								//Do nothing
							}
						}
						
					} catch (CompilationException e1) {
						System.out.println("Failed to read from table path: " + path);
					}
				}
			});
	}

}
