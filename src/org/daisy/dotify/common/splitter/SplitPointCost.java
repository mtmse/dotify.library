package org.daisy.dotify.common.splitter;

/**
 * Provides a cost function for a split point.
 *
 * @param <T> the type of split point unit
 * @author Joel Håkansson
 */
@FunctionalInterface
public interface SplitPointCost<T extends SplitPointUnit> {

    /**
     * Returns the cost of breaking after the unit with the specified index.
     *
     * @param units the units
     * @param index the index of the breakpoint unit
     * @param limit the maximum length to consider
     * @return returns the cost
     */
    public double getCost(SplitPointDataSource<T, ?> units, int index, int limit);

}
