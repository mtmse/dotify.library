package org.daisy.dotify.formatter.impl.row;

import org.daisy.dotify.api.formatter.FormattingTypes.Alignment;
import org.daisy.dotify.api.formatter.Marker;
import org.daisy.dotify.api.writer.Row;
import org.daisy.dotify.formatter.impl.segment.MarkerSegment;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a single row of text. {@link RowImpl}s are immutable.
 *
 * @author Joel Håkansson
 */
public final class RowImpl implements Row {
    private String chars;
    private List<Marker> markers = new ArrayList<>();
    private List<String> anchors = new ArrayList<>();
    private List<String> identifiers = new ArrayList<>();
    private MarginProperties leftMargin = MarginProperties.EMPTY_MARGIN;
    private MarginProperties rightMargin = MarginProperties.EMPTY_MARGIN;
    private Alignment alignment = Alignment.LEFT;
    private Float rowSpacing = null;
    private boolean adjustedForMargin = false;
    private boolean allowsBreakAfter = true;
    private int leaderSpace = 0;
    private Object externalReference;
    private boolean invisible = false;

    /**
     * Create a new Row.
     *
     * @param chars the characters on this row
     */
    public RowImpl(String chars) {
        this(chars, new MarginProperties(), new MarginProperties());
    }

    public RowImpl(RowImpl r) {
        this.chars = r.chars;
        this.markers = new ArrayList<>(r.markers);
        this.anchors = new ArrayList<>(r.anchors);
        this.identifiers = new ArrayList<>(r.identifiers);
        this.leftMargin = r.leftMargin;
        this.rightMargin = r.rightMargin;
        this.alignment = r.alignment;
        this.rowSpacing = r.rowSpacing;
        this.adjustedForMargin = r.adjustedForMargin;
        this.allowsBreakAfter = r.allowsBreakAfter;
        this.leaderSpace = r.leaderSpace;
        this.externalReference = r.externalReference;
        this.invisible = r.invisible;
    }

    public RowImpl(String chars, MarginProperties leftMargin, MarginProperties rightMargin) {
        this.chars = chars;
        this.markers = new ArrayList<>();
        this.anchors = new ArrayList<>();
        this.identifiers = new ArrayList<>();
        this.leftMargin = leftMargin;
        this.rightMargin = rightMargin;
        this.alignment = Alignment.LEFT;
        this.rowSpacing = null;
        this.adjustedForMargin = false;
        this.allowsBreakAfter = true;
        this.leaderSpace = 0;
        this.externalReference = null;
        this.invisible = false;
    }
    /**
     * Create a new empty Row.
     */
    public RowImpl() {
        this("");
    }

    /**
     * Get the characters on this row.
     *
     * @return returns the characters on the row
     */
    @Override
    public String getChars() {
        return chars;
    }

    public int getLeaderSpace() {
        return leaderSpace;
    }

    public int getWidth() {
        return chars.length() + leftMargin.getContent().length() + rightMargin.getContent().length();
    }

    /**
     * Get all markers on this Row.
     *
     * @return returns the markers
     */
    public List<Marker> getMarkers() {
        return markers;
    }

    public boolean hasMarkerWithName(String name) {
        for (Marker m : markers) {
            if (m.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get all anchors on this Row.
     *
     * @return returns an ArrayList of anchors
     */
    public List<String> getAnchors() {
        return anchors;
    }

    /**
     * Get all identifiers on this Row.
     *
     * @return returns an ArrayList of identifiers
     */
    public List<String> getIdentifiers() {
        return identifiers;
    }

    /**
     * Get the left margin value for the Row, in characters.
     *
     * @return returns the left margin
     */
    public MarginProperties getLeftMargin() {
        return leftMargin;
    }

    public MarginProperties getRightMargin() {
        return rightMargin;
    }

    /**
     * Gets the alignment value for the row.
     *
     * @return returns the alignment
     */
    public Alignment getAlignment() {
        return alignment;
    }

    @Override
    public Float getRowSpacing() {
        return rowSpacing;
    }

    public boolean shouldAdjustForMargin() {
        return adjustedForMargin;
    }

    public boolean allowsBreakAfter() {
        return allowsBreakAfter;
    }

    public boolean isInvisible() {
        return invisible;
    }

    /**
     * Get the current external reference information tagged for this row.
     *
     * @return  Object of the external reference.
     */
    public Object getExternalReference() {
        return externalReference;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (adjustedForMargin ? 1231 : 1237);
        result = prime * result + ((alignment == null) ? 0 : alignment.hashCode());
        result = prime * result + (allowsBreakAfter ? 1231 : 1237);
        result = prime * result + ((anchors == null) ? 0 : anchors.hashCode());
        result = prime * result + ((chars == null) ? 0 : chars.hashCode());
        result = prime * result + leaderSpace;
        result = prime * result + ((leftMargin == null) ? 0 : leftMargin.hashCode());
        result = prime * result + ((markers == null) ? 0 : markers.hashCode());
        result = prime * result + ((rightMargin == null) ? 0 : rightMargin.hashCode());
        result = prime * result + ((rowSpacing == null) ? 0 : rowSpacing.hashCode());
        result = prime * result + ((identifiers == null) ? 0 : identifiers.hashCode());
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
        RowImpl other = (RowImpl) obj;
        if (adjustedForMargin != other.adjustedForMargin) {
            return false;
        }
        if (alignment != other.alignment) {
            return false;
        }
        if (allowsBreakAfter != other.allowsBreakAfter) {
            return false;
        }
        if (anchors == null) {
            if (other.anchors != null) {
                return false;
            }
        } else if (!anchors.equals(other.anchors)) {
            return false;
        }
        if (chars == null) {
            if (other.chars != null) {
                return false;
            }
        } else if (!chars.equals(other.chars)) {
            return false;
        }
        if (leaderSpace != other.leaderSpace) {
            return false;
        }
        if (leftMargin == null) {
            if (other.leftMargin != null) {
                return false;
            }
        } else if (!leftMargin.equals(other.leftMargin)) {
            return false;
        }
        if (markers == null) {
            if (other.markers != null) {
                return false;
            }
        } else if (!markers.equals(other.markers)) {
            return false;
        }
        if (rightMargin == null) {
            if (other.rightMargin != null) {
                return false;
            }
        } else if (!rightMargin.equals(other.rightMargin)) {
            return false;
        }
        if (rowSpacing == null) {
            if (other.rowSpacing != null) {
                return false;
            }
        } else if (!rowSpacing.equals(other.rowSpacing)) {
            return false;
        }
        if (identifiers == null) {
            if (other.identifiers != null) {
                return false;
            }
        } else if (!identifiers.equals(other.identifiers)) {
            return false;
        }
        return true;
    }

    public void setLeftMargin(MarginProperties left) {
        this.leftMargin = left;
    }

    public void setRightMargin(MarginProperties right) {
        this.rightMargin = right;
    }

    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }

    public void setRowSpacing(Float rowSpacing) {
        this.rowSpacing = rowSpacing;
    }

    public void addExternalReference(Object externalReference) {
        this.externalReference = externalReference;
    }

    public void setAdjustedForMargin(boolean b) {
        this.adjustedForMargin = b;
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }

    public void addAnchors(List<String> anchors) {
        this.anchors.addAll(anchors);
    }

    public void addAnchors(int index, List<String> groupAnchors) {
        this.anchors.addAll(index, groupAnchors);
    }

    public void addMarkers(List<Marker> markersList) {
        this.markers.addAll(markersList);
    }

    public void addMarkers(int index, List<Marker> groupMarkers) {
        this.markers.addAll(index, groupMarkers);
    }

    public void addIdentifiers(List<String> identifiers) {
        this.identifiers.addAll(identifiers);
    }

    public void addIdentifiers(int index, List<String> groupIdentifiers) {
        this.identifiers.addAll(index, groupIdentifiers);
    }

    public void setAllowsBreakAfter(boolean allowsBreakAfter) {
        this.allowsBreakAfter = allowsBreakAfter;
    }

    public void addMarker(MarkerSegment marker) {
        this.markers.add(marker);
    }

    public void addAnchor(String referenceID) {
        this.anchors.add(referenceID);
    }

    public void addIdentifier(String name) {
        this.identifiers.add(name);
    }

    public void setChars(String s) {
        this.chars = s;
    }

    public void setLeaderSpace(int i) {
        this.leaderSpace = i;
    }
}
