package org.daisy.dotify.formatter.impl.obfl;

import org.daisy.dotify.api.formatter.FormatterCore;
import org.daisy.dotify.api.formatter.FormatterException;
import org.daisy.dotify.api.formatter.RenderingScenario;
import org.daisy.dotify.api.formatter.TextProperties;
import org.daisy.dotify.api.obfl.Expression;
import org.w3c.dom.Node;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * TODO: Write java doc.
 */
public class XSLTRenderingScenario implements RenderingScenario {
    private final ObflParserImpl parser;
    private final Transformer t;
    private final Node node;
    private final TextProperties tp;
    private final Expression ev;
    private final String exp;

    public XSLTRenderingScenario(
        ObflParserImpl parser,
        Transformer t,
        Node node,
        TextProperties tp,
        Expression ev,
        String exp
    ) {
        this.parser = parser;
        this.t = t;
        this.node = node;
        this.tp = tp;
        this.ev = ev;
        this.exp = exp;
    }

    @Override
    public void renderScenario(FormatterCore formatter) throws FormatterException {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            t.transform(new DOMSource(node), new StreamResult(os));

            // The StAX parser handles most malformed-XML cases; multiple-root-element
            // output is checked explicitly after the main loop (see below).
            XMLInputFactory factory = parser.getFactoryManager().getXmlInputFactory();
            XMLEventIterator input = new XMLEventReaderAdapter(
                factory.createXMLEventReader(new ByteArrayInputStream(os.toByteArray()))
            );
            XMLEvent event;
            while (input.hasNext()) {
                event = input.nextEvent();
                if (ObflParserImpl.equalsStart(event, ObflQName.XML_PROCESSOR_RESULT)) {
                    // ok
                } else if (event.isCharacters()) {
                    formatter.addChars(event.asCharacters().getData(), tp);
                } else if (ObflParserImpl.equalsStart(event, ObflQName.BLOCK)) {
                    parser.parseBlock(event, input, formatter, tp, false);
                } else if (ObflParserImpl.equalsStart(event, ObflQName.TABLE)) {
                    parser.parseTable(event, input, formatter, tp, false);
                } else if (parser.processAsBlockContents(formatter, event, input, tp, false, false)) {
                    //done
                } else if (ObflParserImpl.equalsEnd(event, ObflQName.XML_PROCESSOR_RESULT)) {
                    break;
                } else {
                    ObflParserImpl.report(event);
                }
            }
            // Verify the transform produced exactly one root element. A valid XML document
            // may not have multiple root elements; some StAX parsers do not enforce this,
            // so we check by draining any trailing events after the first root element ends.
            while (input.hasNext()) {
                XMLEvent trailing = input.nextEvent();
                if (trailing.isStartElement()) {
                    throw new RuntimeException(
                        "XSLT result must have exactly one root element; found a second <"
                        + trailing.asStartElement().getName().getLocalPart() + ">"
                    );
                }
            }
        } catch (Exception e) {
            throw new FormatterException(e);
        }
    }

    @Override
    public double calculateCost(Map<String, Double> variables) {
        for (String key : variables.keySet()) {
            ev.setVariable(key, ((Double) variables.get(key)));
        }
        Double ret = Double.parseDouble(ev.evaluate(exp).toString());
        return ret;
    }
}
