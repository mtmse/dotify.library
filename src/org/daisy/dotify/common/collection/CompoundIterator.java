package org.daisy.dotify.common.collection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Provides an iterator for a collection of iterables.
 *
 * @param <T> the type of iterator
 * @author Joel Håkansson
 */
public class CompoundIterator<T> implements Iterator<T> {
    List<Iterator<T>> iterators;

    /**
     * Creates a new compound iterator.
     *
     * @param iterables the iterables to use in this iterator
     */
    public CompoundIterator(Iterable<? extends Iterable<T>> iterables) {
        iterators = new ArrayList<>();
        for (Iterable<T> e : iterables) {
            iterators.add(e.iterator());
        }
    }

    @Override
    public boolean hasNext() {
        for (Iterator<T> e : iterators) {
            if (e.hasNext()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public T next() {
        for (Iterator<T> e : iterators) {
            if (e.hasNext()) {
                return e.next();
            }
        }
        throw new NoSuchElementException();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();

    }
}
