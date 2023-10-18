package org.daisy.dotify.hyphenator.impl;

import org.daisy.dotify.api.hyphenator.HyphenatorInterface;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * This class tests the JHyphenator against the same phrases we use for the new SimpleHyphenator.
 */
public class SimpleHyphenatorSETestJHyphen {
    private HyphenatorInterface hyphenator;

    @Before
    public void before() {
        try {
            hyphenator = new JHyphenatorFactory().newHyphenator("sv");
        } catch (Exception ignored) {
        }
    }

    private String hyph(String hyph) {
        return hyphenator.hyphenate(hyph);
    }

    public void testComplicatedWords() {
        Assume.assumeTrue(new File("/usr/share/hyphen/hyph_sv_SE.dic").exists());

        assertEquals("Im\u00ADper\u00ADti\u00ADnent", hyph("Impertinent"));
        assertEquals("He\u00ADge\u00ADmo\u00ADni", hyph("Hegemoni"));
        assertEquals("Ver\u00ADse\u00ADrad", hyph("Verserad"));
        assertEquals("Kom\u00ADpi\u00ADle\u00ADra", hyph("Kompilera"));
        assertEquals("Ci\u00ADne\u00ADma\u00ADtek", hyph("Cinematek"));
        assertEquals("Re\u00ADne\u00ADgat", hyph("Renegat"));
        assertEquals("Ka\u00ADres\u00ADse\u00ADra", hyph("Karessera"));
        assertEquals("Ex\u00ADtem\u00ADpo\u00ADre\u00ADra", hyph("Extemporera"));
        assertEquals("Trans\u00ADver\u00ADsal", hyph("Transversal"));
        assertEquals("Epis\u00ADte\u00ADmo\u00ADlo\u00ADgi", hyph("Epistemologi"));

        assertEquals("Apo\u00ADlo\u00ADgi", hyph("Apologi"));
        assertEquals("Ju\u00ADve\u00ADnil", hyph("Juvenil"));
        assertEquals("Fleg\u00ADma\u00ADtisk", hyph("Flegmatisk"));
        assertEquals("Pre\u00ADci\u00ADös", hyph("Preciös"));
        assertEquals("Ano\u00ADma\u00ADli", hyph("Anomali"));
        assertEquals("Ely\u00ADse\u00ADisk", hyph("Elyseisk"));
        assertEquals("Per\u00ADplex", hyph("Perplex"));
        assertEquals("Tru\u00ADism", hyph("Truism"));
        assertEquals("Pa\u00ADne\u00ADgy\u00ADrik", hyph("Panegyrik"));
        assertEquals("Fa\u00ADti\u00ADge\u00ADra", hyph("Fatigera"));

        assertEquals("Am\u00ADsa\u00ADga", hyph("Amsaga"));
        assertEquals("Bla\u00ADma\u00ADge", hyph("Blamage"));
        assertEquals("Pa\u00ADter\u00ADna\u00ADlism", hyph("Paternalism"));
        assertEquals("No\u00ADta\u00ADbi\u00ADli\u00ADtet", hyph("Notabilitet"));
        assertEquals("Afa\u00ADsi", hyph("Afasi"));
        assertEquals("Ma\u00ADro\u00ADde\u00ADra", hyph("Marodera"));
        assertEquals("Kom\u00ADmu\u00ADni\u00ADké", hyph("Kommuniké"));
        assertEquals("Ar\u00ADri\u00ADvist", hyph("Arrivist"));
        assertEquals("Tran\u00ADskri\u00ADbe\u00ADra", hyph("Transkribera"));
        assertEquals("Bond\u00ADfång\u00ADa\u00ADre", hyph("Bondfångare"));

        assertEquals("egna", hyph("egna"));

        assertEquals(
            "Hans fan\u00ADtas\u00ADtis\u00ADka för\u00ADmå\u00ADga att ex\u00ADtem\u00ADpo\u00ADre\u00ADra " +
                    "är som bort\u00ADblåst.",
            hyph("Hans fantastiska förmåga att extemporera är som bortblåst.")
        );
    }

    @Test
    public void testCLITestCase() {
        Assume.assumeTrue(new File("/usr/share/hyphen/hyph_sv_SE.dic").exists());

        assertEquals("av\u00ADstav\u00ADnings\u00ADreg\u00ADler", hyph("avstavningsregler"));
        assertEquals(
            "⠠⠤den här bo\u00ADken är en rö\u00ADra⠱, sä\u00ADger någ\u00ADra " +
            "(de otå\u00ADli\u00ADgas\u00ADte):",
            hyph("⠠⠤den här boken är en röra⠱, säger några (de otåligaste):")
        );
        assertEquals(
            "⠠⠤istäl\u00ADlet för att be\u00ADrät\u00ADta en his\u00ADto\u00ADria or\u00ADdent\u00ADligt, " +
            "som man ska, kom\u00ADmer han med med la\u00ADby\u00ADrin\u00ADter och trå\u00ADdar och " +
            "ari\u00ADad\u00ADnor⠱ (som in\u00ADte är ari\u00ADad\u00ADnor)",
            hyph(
                "⠠⠤istället för att berätta en historia ordentligt, som man ska, kommer han med med labyrinter och " +
                "trådar och ariadnor⠱ (som inte är ariadnor)"
            )
        );
        assertEquals(
            "⠠⠤och man för\u00ADstår in\u00ADte vad sjut\u00ADton han hål\u00ADler på med⠱. jag " +
            "för\u00ADsva\u00ADrar mig (jag mås\u00ADte för\u00ADsva\u00ADra min his\u00ADto\u00ADria) och " +
            "sä\u00ADger: ⠠⠤ja\u00ADmen, det mås\u00ADte in\u00ADte all\u00ADtid va\u00ADra så lätt att lä\u00ADsa " +
            "en bok.",
            hyph("⠠⠤och man förstår inte vad sjutton han håller på med⠱. jag försvarar mig (jag måste försvara min " +
                    "historia) och säger: ⠠⠤jamen, det måste inte alltid vara så lätt att läsa en bok.")
        );
        assertEquals(
            "ibland mås\u00ADte man hit\u00ADta trå\u00ADden ock\u00ADså för att kom\u00ADma ut ur " +
            "la\u00ADby\u00ADrin\u00ADten⠱. ut\u00ADan att av\u00ADslö\u00ADja att det där med ari\u00ADad\u00ADne, " +
            "vil\u00ADket ni snart kom\u00ADmer att få se, hör till be\u00ADrät\u00ADtel\u00ADsen.",
            hyph(
                "ibland måste man hitta tråden också för att komma ut ur labyrinten⠱. utan att avslöja att det där " +
                "med ariadne, vilket ni snart kommer att få se, hör till berättelsen."
            )
        );

        assertEquals(
            "finns många fler: vi har hu\u00ADgo be\u00ADren\u00ADste\u00ADin, vi har te\u00ADre\u00ADsa díaz – " +
            "te\u00ADre – och vi har ro\u00ADsa epi\u00ADo\u00ADnez, som har ett namn som lå\u00ADter som en " +
            "pi\u00ADon.",
            hyph(
                "finns många fler: vi har hugo berenstein, vi har teresa díaz – tere – och vi har rosa epionez, " +
                "som har ett namn som låter som en pion."
            )
        );

        assertEquals(
            "Brit\u00ADtis\u00ADke pre\u00ADmi\u00ADär\u00ADmi\u00ADnis\u00ADtern Win\u00ADston " +
            "Chur\u00ADchill om Rom\u00ADmel i en un\u00ADder\u00ADhus\u00ADde\u00ADbatt, " +
            "ja\u00ADnu\u00ADa\u00ADri 1942",
            hyph("Brittiske premiärministern Winston Churchill om Rommel i en underhusdebatt, januari 1942")
        );
    }
}
