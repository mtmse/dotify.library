package org.daisy.dotify.formatter.impl.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.function.Predicate;

import org.daisy.dotify.api.formatter.BlockProperties;
import org.daisy.dotify.api.formatter.DynamicContent;
import org.daisy.dotify.api.formatter.Leader;
import org.daisy.dotify.api.formatter.Marker;
import org.daisy.dotify.api.formatter.NumeralStyle;
import org.daisy.dotify.api.formatter.SpanProperties;
import org.daisy.dotify.api.formatter.TableOfContents;
import org.daisy.dotify.api.formatter.TextProperties;
import org.daisy.dotify.formatter.impl.common.FormatterCoreContext;
import org.daisy.dotify.formatter.impl.row.RowDataProperties;


/**
 * Provides table of contents entries to be used when building a Table of Contents
 * @author Joel Håkansson
 */
public class TableOfContentsImpl extends FormatterCoreImpl implements TableOfContents  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2198713822437968076L;
	/* remember all the ref-id attributes in order to verify that they are unique */
	private final Set<String> refIds;
	/* every toc-entry maps exactly to one block in the resulting sequence of blocks */
	private final Map<Block,String> refIdForBlock;
	/* mapping from block in the resulting sequence of blocks to the toc-block element that it came from */
	private final Map<Block,Object> tocBlockForBlock;
	/* parent-child relationships of toc-block elements */
	private final Map<Object,Object> parentTocBlockForTocBlock;
	/* current stack of ancestor toc-block elements */
	private final Stack<Object> currentAncestorTocBlocks;
	/* whether we are currently inside a toc-entry */
	private boolean inTocEntry = false;

	public TableOfContentsImpl(FormatterCoreContext fc) {
		super(fc);
		this.refIds = new HashSet<>();
		this.refIdForBlock = new IdentityHashMap<>();
		this.tocBlockForBlock = new IdentityHashMap<>();
		this.parentTocBlockForTocBlock = new LinkedHashMap<>();
		this.currentAncestorTocBlocks = new Stack<>();
	}

	@Override
	public void startBlock(BlockProperties p, String blockId) {
		Object tocBlock = new Object();
		if (!currentAncestorTocBlocks.isEmpty()) {
			parentTocBlockForTocBlock.put(tocBlock, currentAncestorTocBlocks.peek());
		}
		currentAncestorTocBlocks.push(tocBlock);
		super.startBlock(p, blockId);
	}

	@Override
	public void endBlock() {
		super.endBlock();
		currentAncestorTocBlocks.pop();
	}

	@Override
	public Block newBlock(String blockId, RowDataProperties rdp) {
		Block b = super.newBlock(blockId, rdp);
		if (!currentAncestorTocBlocks.isEmpty()) {
			tocBlockForBlock.put(b, currentAncestorTocBlocks.peek());
		}
		return b;
	}

	@Override
	public void startEntry(String refId) {
		if (inTocEntry) {
			throw new RuntimeException("toc-entry may not be nested");
		}
		inTocEntry = true;
		if (!refIds.add(refId)) {
			throw new RuntimeException("ref-id is not unique: " + refId);
		}
		if (refIdForBlock.put(getCurrentBlock(), refId) != null) {
			// note that this is not strictly forbidden by OBFL, but it simplifies the implementation
			throw new RuntimeException("No two toc-entry's may be contained in the same block");
		}
	}
	
	@Override
	public void endEntry() {
		inTocEntry = false;
	}

	/**
	 * Filter out the toc-entry with a ref-id that does not satisfy the predicate. This is used to
	 * create the volume range toc. toc-block that have all their descendant toc-entry filtered out
	 * are also omitted.
	 */
	/*
	 * Note that, because this is implemented by filtering a fixed sequence of blocks, and because
	 * of the way the sequence of blocks is constructed, we are potentially throwing away borders
	 * and margins that should be kept. That said, the previous implementation did not handle
	 * borders and margins correctly either, so fixing this issue can be seen as an optimization.
	 */
	public Collection<Block> filter(Predicate<String> filter) {
		List<Block> filtered = new ArrayList<>();
		Set<Object> tocBlocksWithDescendantTocEntry = new HashSet<>();
		for (Block b : this) {
			if (refIdForBlock.containsKey(b)) {
				if (!filter.test(refIdForBlock.get(b))) {
					continue;
				}
				if (tocBlockForBlock.containsKey(b)) {
					Object tocBlock = tocBlockForBlock.get(b);
					tocBlocksWithDescendantTocEntry.add(tocBlock);
					while (parentTocBlockForTocBlock.containsKey(tocBlock)) {
						tocBlock = parentTocBlockForTocBlock.get(tocBlock);
						tocBlocksWithDescendantTocEntry.add(tocBlock);
					}
				}
			}
			filtered.add(b);
		}
		Iterator<Block> i = filtered.iterator();
		while (i.hasNext()) {
			Block b = i.next();
			if (refIdForBlock.containsKey(b)) {
				continue;
			}
			if (tocBlockForBlock.containsKey(b)) { // this should always be true
				if (tocBlocksWithDescendantTocEntry.contains(tocBlockForBlock.get(b))) {
					continue;
				}
			}
			i.remove();
		}
		return filtered;
	}

	private void assertInTocEntry() {
		if (!inTocEntry) {
			throw new RuntimeException("Inline content only allowed within toc-entry");
		}
	}

	@Override
	public void insertMarker(Marker marker) {
		assertInTocEntry();
		super.insertMarker(marker);
	}

	@Override
	public void insertAnchor(String ref) {
		assertInTocEntry();
		super.insertAnchor(ref);
	}

	@Override
	public void insertLeader(Leader leader) {
		assertInTocEntry();
		super.insertLeader(leader);
	}

	@Override
	public void addChars(CharSequence chars, TextProperties props) {
		assertInTocEntry();
		super.addChars(chars, props);
	}

	@Override
	public void startStyle(String style) {
		assertInTocEntry();
		super.startStyle(style);
	}

	@Override
	public void endStyle() {
		assertInTocEntry();
		super.endStyle();
	}

	@Override
	public void startSpan(SpanProperties props) {
		assertInTocEntry();
		super.startSpan(props);
	}

	@Override
	public void endSpan() {
		assertInTocEntry();
		super.endSpan();
	}

	@Override
	public void newLine() {
		assertInTocEntry();
		super.newLine();
	}

	@Override
	public void insertReference(String identifier, NumeralStyle numeralStyle) {
		assertInTocEntry();
		super.insertReference(identifier, numeralStyle);
	}

	@Override
	public void insertEvaluate(DynamicContent exp, TextProperties t) {
		assertInTocEntry();
		super.insertEvaluate(exp, t);
	}
}
