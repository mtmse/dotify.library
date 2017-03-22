package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.daisy.dotify.api.formatter.Marker;
import org.daisy.dotify.common.text.StringTools;

abstract class AbstractBlockContentManager {
	//Immutable
	protected final int flowWidth;
	private final MarginProperties leftParent;
	private final MarginProperties rightParent;
	protected final MarginProperties leftMargin;
	protected final MarginProperties rightMargin;
	private final List<RowImpl> collapsiblePreContentRows;
	private final List<RowImpl> innerPreContentRows;
	private final List<RowImpl> postContentRows;
	private final List<RowImpl> skippablePostContentRows;
	private final int minWidth;
	
	//Mutable
	protected final RowDataProperties rdp;
	protected final FormatterContext fcontext;
	protected final ArrayList<Marker> groupMarkers;
	protected final ArrayList<String> groupAnchors;
	
	AbstractBlockContentManager(int flowWidth, RowDataProperties rdp, FormatterContext fcontext) {
		this(flowWidth, rdp, fcontext, null);
	}
	
	AbstractBlockContentManager(int flowWidth, RowDataProperties rdp, FormatterContext fcontext, Integer minWidth) {
		this.flowWidth = flowWidth;
		this.leftParent = rdp.getLeftMargin().buildMarginParent(fcontext.getSpaceCharacter());
		this.rightParent = rdp.getRightMargin().buildMarginParent(fcontext.getSpaceCharacter());
		this.leftMargin = rdp.getLeftMargin().buildMargin(fcontext.getSpaceCharacter());
		this.rightMargin = rdp.getRightMargin().buildMargin(fcontext.getSpaceCharacter());
		this.fcontext = fcontext;
		this.rdp = rdp;
		this.groupMarkers = new ArrayList<>();
		this.groupAnchors = new ArrayList<>();
		this.collapsiblePreContentRows = makeCollapsiblePreContentRows(rdp, leftParent, rightParent);	
		this.innerPreContentRows = makeInnerPreContentRows();
		this.minWidth = minWidth==null ? flowWidth-leftMargin.getContent().length()-rightMargin.getContent().length() : minWidth;

		List<RowImpl> postContentRowsBuilder = new ArrayList<>();
		List<RowImpl> skippablePostContentRowsBuilder = new ArrayList<>();
		MarginProperties margin = new MarginProperties(leftMargin.getContent()+StringTools.fill(fcontext.getSpaceCharacter(), rdp.getTextIndent()), leftMargin.isSpaceOnly());
		if (rdp.getTrailingDecoration()==null) {
			if (leftMargin.isSpaceOnly() && rightMargin.isSpaceOnly()) {
				for (int i=0; i<rdp.getInnerSpaceAfter(); i++) {
					skippablePostContentRowsBuilder.add(createAndConfigureEmptyNewRow(margin));
				}
			} else {
				for (int i=0; i<rdp.getInnerSpaceAfter(); i++) {
					postContentRowsBuilder.add(createAndConfigureEmptyNewRow(margin));
				}
			}
		} else {
			for (int i=0; i<rdp.getInnerSpaceAfter(); i++) {
				postContentRowsBuilder.add(createAndConfigureEmptyNewRow(margin));
			}
			postContentRowsBuilder.add(makeDecorationRow(flowWidth, rdp.getTrailingDecoration(), leftParent, rightParent));
		}
		
		if (leftParent.isSpaceOnly() && rightParent.isSpaceOnly()) {
			for (int i=0; i<rdp.getOuterSpaceAfter();i++) {
				skippablePostContentRowsBuilder.add(createAndConfigureNewEmptyRow(leftParent, rightParent));
			}
		} else {
			for (int i=0; i<rdp.getOuterSpaceAfter();i++) {
				postContentRowsBuilder.add(createAndConfigureNewEmptyRow(leftParent, rightParent));
			}
		}
		this.postContentRows = Collections.unmodifiableList(postContentRowsBuilder);
		this.skippablePostContentRows = Collections.unmodifiableList(skippablePostContentRowsBuilder);
	}
	
	abstract int getForceBreakCount();

	abstract int getRowCount();

	abstract RowImpl get(int index);

	/**
	 * Returns true if this manager supports rows with variable maximum
	 * width, false otherwise.
	 * @return true if variable maximum width is supported, false otherwise
	 */
	abstract boolean supportsVariableWidth();

	private static List<RowImpl> makeCollapsiblePreContentRows(RowDataProperties rdp, MarginProperties leftParent, MarginProperties rightParent) {
		List<RowImpl> ret = new ArrayList<>();
		for (int i=0; i<rdp.getOuterSpaceBefore();i++) {
			RowImpl row = new RowImpl.Builder("").leftMargin(leftParent).rightMargin(rightParent)
					.rowSpacing(rdp.getRowSpacing())
					.adjustedForMargin(true)
					.build();
			ret.add(row);
		}
		return Collections.unmodifiableList(ret);
	}
	
	private List<RowImpl> makeInnerPreContentRows() {
		ArrayList<RowImpl> ret = new ArrayList<>();
		if (rdp.getLeadingDecoration()!=null) {
			ret.add(makeDecorationRow(flowWidth, rdp.getLeadingDecoration(), leftParent, rightParent));
		}
		for (int i=0; i<rdp.getInnerSpaceBefore(); i++) {
			MarginProperties margin = new MarginProperties(leftMargin.getContent()+StringTools.fill(fcontext.getSpaceCharacter(), rdp.getTextIndent()), leftMargin.isSpaceOnly());
			ret.add(createAndConfigureEmptyNewRow(margin));
		}
		return Collections.unmodifiableList(ret);
	}
	
	protected RowImpl makeDecorationRow(int flowWidth, SingleLineDecoration d, MarginProperties leftParent, MarginProperties rightParent) {
		int w = flowWidth - rightParent.getContent().length() - leftParent.getContent().length();
		int aw = w-d.getLeftCorner().length()-d.getRightCorner().length();
		RowImpl row = new RowImpl.Builder(d.getLeftCorner() + StringTools.fill(d.getLinePattern(), aw) + d.getRightCorner())
				.leftMargin(leftParent)
				.rightMargin(rightParent)
				.alignment(rdp.getAlignment())
				.rowSpacing(rdp.getRowSpacing())
				.adjustedForMargin(true)
				.build();
		return row;
	}
	
	protected RowImpl createAndConfigureEmptyNewRow(MarginProperties left) {
		return createAndConfigureEmptyNewRowBuilder(left).build();
	}

	protected RowImpl.Builder createAndConfigureEmptyNewRowBuilder(MarginProperties left) {
		return createAndConfigureNewEmptyRowBuilder(left, rightMargin);
	}

	protected RowImpl createAndConfigureNewEmptyRow(MarginProperties left, MarginProperties right) {
		return createAndConfigureNewEmptyRowBuilder(left, right).build();
	}

	protected RowImpl.Builder createAndConfigureNewEmptyRowBuilder(MarginProperties left, MarginProperties right) {
		return new RowImpl.Builder("").leftMargin(left).rightMargin(right)
				.alignment(rdp.getAlignment())
				.rowSpacing(rdp.getRowSpacing())
				.adjustedForMargin(true);
	}

	MarginProperties getLeftMarginParent() {
		return leftParent;
	}

	MarginProperties getRightMarginParent() {
		return rightParent;
	}

	List<RowImpl> getCollapsiblePreContentRows() {
		return collapsiblePreContentRows;
	}
	
	boolean hasCollapsiblePreContentRows() {
		return !collapsiblePreContentRows.isEmpty();
	}

	List<RowImpl> getInnerPreContentRows() {
		return innerPreContentRows;
	}
	
	boolean hasInnerPreContentRows() {
		return !innerPreContentRows.isEmpty();
	}

	List<RowImpl> getPostContentRows() {
		return postContentRows;
	}
	
	boolean hasPostContentRows() {
		return !postContentRows.isEmpty();
	}
	
	List<RowImpl> getSkippablePostContentRows() {
		return skippablePostContentRows;
	}
	
	boolean hasSkippablePostContentRows() {
		return !skippablePostContentRows.isEmpty();
	}
	
	/**
	 * Gets the minimum width available for content (excluding margins)
	 * @return returns the available width, in characters
	 */
	int getMinimumAvailableWidth() {
		return minWidth;
	}

	/**
	 * Get markers that are not attached to a row, i.e. markers that proceeds any text contents
	 * @return returns markers that proceeds this FlowGroups text contents
	 */
	ArrayList<Marker> getGroupMarkers() {
		return groupMarkers;
	}
	
	ArrayList<String> getGroupAnchors() {
		return groupAnchors;
	}
	
}
