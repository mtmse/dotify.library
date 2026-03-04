package org.daisy.dotify.api.formatter;

import java.util.Map;

/**
 * Provides a rendering scenario.
 *
 * @author Joel Håkansson
 */
public interface RenderingScenario {

    /**
     * Renders the scenario into the supplied formatter.
     *
     * @param formatter the formatter to render into
     * @throws FormatterException if rendering fails. If this exception is thrown,
     *                            the supplied formatter may be in an unknown state.
     */
    public void renderScenario(FormatterCore formatter) throws FormatterException;

    /**
     * Calculates the cost of this scenario with the supplied
     * variables (which might, for example, include the total height
     * of the rendered result).
     *
     * <p>The {@code variables} map is <em>ephemeral</em>: it is valid only for
     * the duration of this call. Implementations must read all required values
     * from the map before returning and must not retain a reference to the map
     * or pass it to any code that outlives this call. The map contents are
     * undefined after this method returns.</p>
     *
     * @param variables the variables to use when calculating the cost
     * @return returns the cost for this scenario
     */
    public double calculateCost(Map<String, Double> variables);
}
