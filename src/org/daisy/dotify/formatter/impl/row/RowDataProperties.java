package org.daisy.dotify.formatter.impl.row;

import org.daisy.dotify.api.formatter.Condition;
import org.daisy.dotify.api.formatter.FormattingTypes;
import org.daisy.dotify.api.formatter.FormattingTypes.Alignment;
import org.daisy.dotify.formatter.impl.row.Margin.Type;

import java.util.Collections;

/**
 * Provides properties for a block. {@link RowDataProperties} are
 * immutable.
 *
 * @author Joel Håkansson
 */
public final class RowDataProperties {
    private int blockIndent = 0;
    private int blockIndentParent = 0;
    private int textIndent = 0;
    private int firstLineIndent = 0;
    private int rightTextIndent = 0;
    private int outerSpaceBefore = 0;
    private int outerSpaceAfter = 0;
    private int innerSpaceBefore = 0;
    private int innerSpaceAfter = 0;
    private int orphans = 0;
    private int widows = 0;
    private Alignment align = Alignment.LEFT;
    private Float rowSpacing = null;
    private BlockMargin margins = new BlockMargin(
            new Margin(Type.LEFT, Collections.emptyList()),
            new Margin(Type.RIGHT, Collections.emptyList()),
            // Space character doesn't matter, because the margin is empty
            ' ');

    private SingleLineDecoration leadingDecoration = null;
    private SingleLineDecoration trailingDecoration = null;
    private String underlineStyle = null;

    private ListItem listProps = null;
    private Object externalReference = null;
    private Condition displayWhen = null;

    public RowDataProperties() {}

    RowImpl configureNewEmptyRowBuilder(MarginProperties left, MarginProperties right) {
        RowImpl row = new RowImpl("");
        row.setLeftMargin(left);
        row.setRightMargin(right);
        row.setAlignment(getAlignment());
        row.setRowSpacing(getRowSpacing());
        row.addExternalReference(getExternalReference());
        row.setAdjustedForMargin(true);
        return row;
    }

    public int getBlockIndent() {
        return blockIndent;
    }

    public int getBlockIndentParent() {
        return blockIndentParent;
    }

    public int getTextIndent() {
        return textIndent;
    }

    public int getFirstLineIndent() {
        return firstLineIndent;
    }

    public int getRightTextIndent() {
        return rightTextIndent;
    }

    public FormattingTypes.Alignment getAlignment() {
        return align;
    }

    public Float getRowSpacing() {
        return rowSpacing;
    }

    public BlockMargin getMargins() {
        return margins;
    }

    public boolean isList() {
        return listProps != null;
    }

    ListItem getListItem() {
        return listProps;
    }

    public Object getExternalReference() {
        return externalReference;
    }

    public int getOuterSpaceBefore() {
        return outerSpaceBefore;
    }

    public int getOuterSpaceAfter() {
        return outerSpaceAfter;
    }

    public int getInnerSpaceBefore() {
        return innerSpaceBefore;
    }

    public int getInnerSpaceAfter() {
        return innerSpaceAfter;
    }

    public SingleLineDecoration getLeadingDecoration() {
        return leadingDecoration;
    }

    public SingleLineDecoration getTrailingDecoration() {
        return trailingDecoration;
    }

    public int getOrphans() {
        return orphans;
    }

    public int getWidows() {
        return widows;
    }

    public String getUnderlineStyle() {
        return underlineStyle;
    }

    public Condition getDisplayWhen() {
        return displayWhen;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((align == null) ? 0 : align.hashCode());
        result = prime * result + blockIndent;
        result = prime * result + blockIndentParent;
        result = prime * result + firstLineIndent;
        result = prime * result + innerSpaceAfter;
        result = prime * result + innerSpaceBefore;
        result = prime * result + ((leadingDecoration == null) ? 0 : leadingDecoration.hashCode());
        result = prime * result + ((listProps == null) ? 0 : listProps.hashCode());
        result = prime * result + ((margins == null) ? 0 : margins.hashCode());
        result = prime * result + orphans;
        result = prime * result + outerSpaceAfter;
        result = prime * result + outerSpaceBefore;
        result = prime * result + rightTextIndent;
        result = prime * result + ((rowSpacing == null) ? 0 : rowSpacing.hashCode());
        result = prime * result + textIndent;
        result = prime * result + ((trailingDecoration == null) ? 0 : trailingDecoration.hashCode());
        result = prime * result + ((underlineStyle == null) ? 0 : underlineStyle.hashCode());
        result = prime * result + widows;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RowDataProperties other = (RowDataProperties) obj;
        if (align != other.align) {
            return false;
        }
        if (blockIndent != other.blockIndent) {
            return false;
        }
        if (blockIndentParent != other.blockIndentParent) {
            return false;
        }
        if (firstLineIndent != other.firstLineIndent) {
            return false;
        }
        if (innerSpaceAfter != other.innerSpaceAfter) {
            return false;
        }
        if (innerSpaceBefore != other.innerSpaceBefore) {
            return false;
        }
        if (leadingDecoration == null) {
            if (other.leadingDecoration != null) {
                return false;
            }
        } else if (!leadingDecoration.equals(other.leadingDecoration)) {
            return false;
        }
        if (listProps == null) {
            if (other.listProps != null) {
                return false;
            }
        } else if (!listProps.equals(other.listProps)) {
            return false;
        }
        if (margins == null) {
            if (other.margins != null) {
                return false;
            }
        } else if (!margins.equals(other.margins)) {
            return false;
        }
        if (orphans != other.orphans) {
            return false;
        }
        if (outerSpaceAfter != other.outerSpaceAfter) {
            return false;
        }
        if (outerSpaceBefore != other.outerSpaceBefore) {
            return false;
        }
        if (rightTextIndent != other.rightTextIndent) {
            return false;
        }
        if (rowSpacing == null) {
            if (other.rowSpacing != null) {
                return false;
            }
        } else if (!rowSpacing.equals(other.rowSpacing)) {
            return false;
        }
        if (textIndent != other.textIndent) {
            return false;
        }
        if (trailingDecoration == null) {
            if (other.trailingDecoration != null) {
                return false;
            }
        } else if (!trailingDecoration.equals(other.trailingDecoration)) {
            return false;
        }
        if (underlineStyle == null) {
            if (other.underlineStyle != null) {
                return false;
            }
        } else if (!underlineStyle.equals(other.underlineStyle)) {
            return false;
        }
        if (widows != other.widows) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "RowDataProperties [blockIndent=" + blockIndent + ", blockIndentParent=" + blockIndentParent
                + ", margins=" + margins + ", listProps=" + listProps + ", textIndent=" + textIndent
                + ", firstLineIndent=" + firstLineIndent + ", rightTextIndent=" + rightTextIndent + ", align=" + align
                + ", rowSpacing=" + rowSpacing + ", outerSpaceBefore=" + outerSpaceBefore + ", outerSpaceAfter="
                + outerSpaceAfter + ", innerSpaceBefore=" + innerSpaceBefore + ", innerSpaceAfter=" + innerSpaceAfter
                + ", orphans=" + orphans + ", widows=" + widows + ", leadingDecoration=" + leadingDecoration
                + ", trailingDecoration=" + trailingDecoration + ", underlineStyle=" + underlineStyle + "]";
    }

    public void setTextIndent(int textIndent) {
        this.textIndent = textIndent;
    }

    public void setFirstLineIndent(int firstLineIndent) {
        this.firstLineIndent = firstLineIndent;
    }

    public void setRightTextIndent(int rightTextIndent) {
        this.rightTextIndent = rightTextIndent;
    }

    public void setAlign(Alignment alignment) {
        this.align = alignment;
    }

    public void setRowSpacing(Float rowSpacing) {
        this.rowSpacing = rowSpacing;
    }

    public void setOrphans(int orphans) {
        this.orphans = orphans;
    }

    public void setWidows(int widows) {
        this.widows = widows;
    }

    public void setBlockIndent(int blockIndent) {
        this.blockIndent = blockIndent;
    }

    public void setBlockIndentParent(Integer peek) {
        this.blockIndentParent = peek;
    }

    public void setMargins(BlockMargin blockMargin) {
        this.margins = blockMargin;
    }

    public void setOuterSpaceBefore(int topSpacing) {
        this.outerSpaceBefore = topSpacing;
    }

    public void setUnderlineStyle(String underlineStyle) {
        this.underlineStyle = underlineStyle;
    }

    public void setDisplayWhen(Condition displayWhen) {
        this.displayWhen = displayWhen;
    }

    public void setInnerSpaceBefore(int topSpacing) {
        this.innerSpaceBefore = topSpacing;
    }

    public void setLeadingDecoration(SingleLineDecoration singleLineDecoration) {
        this.leadingDecoration = singleLineDecoration;
    }

    public void setTrailingDecoration(SingleLineDecoration singleLineDecoration) {
        this.trailingDecoration = singleLineDecoration;
    }

    public void setInnerSpaceAfter(int bottomSpacing) {
        this.innerSpaceAfter = bottomSpacing;
    }

    public void setOuterSpaceAfter(int i) {
        this.outerSpaceAfter = i;
    }

    public void setListProperties(ListItem listItem) {
        this.listProps = listItem;
    }
}
