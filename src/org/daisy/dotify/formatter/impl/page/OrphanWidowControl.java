package org.daisy.dotify.formatter.impl.page;

/**
 * Provides an orphan/widow control utility.
 *
 * @author Joel Håkansson
 */
class OrphanWidowControl {
    private final int orphans, widows, size;
    private int rowCount = 0;

    /**
     * Creates a new instance.
     *
     * @param orphans the minimum number of block-opening lines that may appear by
     *                themselves at the bottom of a page.
     * @param widows  the minimum number of block-ending lines that may fall at the
     *                beginning of the following page.
     * @param size    the expected total number of rows in the block
     */
    OrphanWidowControl(int orphans, int widows, int size) {
        this.orphans = orphans;
        this.widows = widows;
        this.size = size;
    }

    /**
     * Specify that a new row was added to the block
     *
     * @return the current number of rows in the block
     */
    int increaseRowCount() {
        return ++rowCount;
    }

    /**
     * Returns true if a break is allowed after the current row.
     *
     * @return true if a break is allowed after the current row, false otherwise
     */
    boolean allowsBreakAfter() {
        if (rowCount >= size) {
            return true;
        } else {
            return rowCount >= orphans && widows <= size - rowCount;
        }
    }
}
