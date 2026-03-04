package org.daisy.dotify.formatter.impl.page;

import org.daisy.dotify.api.formatter.FormattingTypes.BreakBefore;
import org.daisy.dotify.formatter.impl.core.Block;

import java.util.ArrayList;
import java.util.List;

/**
 * Sequence of {@link Block}s starting at a hard page break (<code>break-before="page"</code> or
 * <code>break-before="sheet"</code>) or a block with absolute positioning
 * (<code>vertical-position</code>).
 */
class RowGroupSequence {
    private final List<Block> blocks;
    private final BreakBefore breakBefore;
    private VerticalSpacing vSpacing;
    private List<RowGroup> group;
    // Running sum of (int) rg.getUnitSize() for all row groups in the group list.
    // Maintained incrementally by addToGroup() and recomputed by setGroup().
    private int groupUnitSizeSum;

    public RowGroupSequence(BreakBefore breakBefore, VerticalSpacing vSpacing) {
        this(breakBefore, vSpacing, new ArrayList<>(), new ArrayList<>());
    }

    public RowGroupSequence(
        BreakBefore breakBefore,
        VerticalSpacing vSpacing,
        List<Block> blocks,
        List<RowGroup> group
    ) {
        this.blocks = blocks;
        this.group = group;
        this.vSpacing = vSpacing;
        this.breakBefore = breakBefore;
        int sum = 0;
        if (group != null) {
            for (RowGroup rg : group) {
                sum += (int) rg.getUnitSize();
            }
        }
        this.groupUnitSizeSum = sum;
    }

    /**
     * Creates a deep copy of the supplied instance.
     *
     * @param template the instance to copy
     */
    RowGroupSequence(RowGroupSequence template) {
        this(template, template.vSpacing, 0, true);
    }

    RowGroupSequence(RowGroupSequence template, int offset) {
        this(template, template.vSpacing, offset, false);
    }

    private RowGroupSequence(RowGroupSequence template, VerticalSpacing vs, int offset, boolean deepMode) {
        this.blocks = deepMode ? new ArrayList<>(template.blocks) : template.blocks;
        if (deepMode) {
            this.group = new ArrayList<>();
            for (RowGroup rg : template.group) {
                group.add(new RowGroup(rg));
            }
            // All row groups are copied with identical unit sizes, so the sum is unchanged.
            this.groupUnitSizeSum = template.groupUnitSizeSum;
        } else {
            if (template.group == null) {
                this.group = null;
                this.groupUnitSizeSum = 0;
            } else if (template.group.size() > offset) {
                this.group = new ArrayList<>(
                        offset > 0 ? template.group.subList(offset, template.group.size()) : template.group);
                // Recompute the sum for the retained sublist. This loop runs in O(n - offset),
                // the same cost as the subList copy above, so it introduces no additional asymptotic work.
                int sum = 0;
                for (RowGroup rg : this.group) {
                    sum += (int) rg.getUnitSize();
                }
                this.groupUnitSizeSum = sum;
            } else {
                this.group = new ArrayList<>();
                this.groupUnitSizeSum = 0;
            }
        }
        this.vSpacing = vs;
        this.breakBefore = template.breakBefore;
    }

    List<RowGroup> getGroup() {
        return group;
    }

    void setGroup(List<RowGroup> value) {
        this.group = value;
        int sum = 0;
        if (value != null) {
            for (RowGroup rg : value) {
                sum += (int) rg.getUnitSize();
            }
        }
        this.groupUnitSizeSum = sum;
    }

    /**
     * Appends a row group to the group list and updates the running unit-size sum.
     * Prefer this method over {@link #getGroup()}.add() to keep the sum consistent.
     *
     * @param rg the row group to append
     */
    void addToGroup(RowGroup rg) {
        if (group == null) {
            group = new ArrayList<>();
        }
        group.add(rg);
        groupUnitSizeSum += (int) rg.getUnitSize();
    }

    /**
     * Returns the sum of <code>(int) rg.getUnitSize()</code> for every row group currently
     * in the group list. Equivalent to iterating the list and summing, but O(1).
     *
     * @return the accumulated unit-size sum
     */
    int getGroupUnitSizeSum() {
        return groupUnitSizeSum;
    }

    List<Block> getBlocks() {
        return blocks;
    }

    VerticalSpacing getVerticalSpacing() {
        return vSpacing;
    }

    void setVerticalSpacing(VerticalSpacing vs) {
        this.vSpacing = vs;
    }

    BreakBefore getBreakBefore() {
        return breakBefore;
    }

}
