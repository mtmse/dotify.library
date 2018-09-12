package org.daisy.braille.utils.api.paper;

import java.io.IOException;
import java.util.Collection;

/**
 * Provides a custom paper collection that lets a user
 * add and remove papers. The collection is stored as
 * a file in the users home directory.
 * @deprecated use {@link PaperCatalogService}
 */
@Deprecated
public class CustomPaperCollection {
	private static CustomPaperCollection collection;

	private CustomPaperCollection() {
	}

	/**
	 * Gets the instance.
	 * @return returns the instance
	 */
	@Deprecated
	public synchronized static CustomPaperCollection getInstance() {
		if (collection==null) {
			collection = new CustomPaperCollection();
		}
		return collection;
	}

	/**
	 * Lists the papers in the collection.
	 * @return returns a collection of papers
	 */
	@Deprecated
	public synchronized Collection<Paper> list() {
		return UserPapersCollection.getInstance().list();
	}

	/**
	 * Adds a new sheet paper to the collection.
	 * @param name the name
	 * @param desc the description
	 * @param width the width
	 * @param height the height
	 * @return returns the new sheet paper
	 * @throws IOException if an I/O error occurs
	 */
	@Deprecated
	public synchronized SheetPaper addNewSheetPaper(String name, String desc, Length width, Length height) throws IOException {
		return UserPapersCollection.getInstance().addNewSheetPaper(name, desc, width, height);
	}

	/**
	 * Adds a new tractor paper to the collection.
	 * @param name the name
	 * @param desc the description
	 * @param across the length across the feed
	 * @param along the length along the feed
	 * @return returns the new tractor paper
	 * @throws IOException if an I/O error occurs
	 */
	@Deprecated
	public synchronized TractorPaper addNewTractorPaper(String name, String desc, Length across, Length along) throws IOException {
		return UserPapersCollection.getInstance().addNewTractorPaper(name, desc, across, along);
	}

	/**
	 * Adds a new roll paper to the collection.
	 * @param name the name
	 * @param desc the description
	 * @param across the length across the feed
	 * @return returns the new roll paper
	 * @throws IOException if an I/O error occurs
	 */
	@Deprecated
	public synchronized RollPaper addNewRollPaper(String name, String desc, Length across) throws IOException  {
		return UserPapersCollection.getInstance().addNewRollPaper(name, desc, across);
	}

	/**
	 * Removes the specified paper from the collection.
	 * @param p the paper to remove
	 * @throws IOException if an I/O error occurs
	 */
	@Deprecated
	public synchronized void remove(Paper p) throws IOException {
		UserPapersCollection.getInstance().remove(p);
	}

}