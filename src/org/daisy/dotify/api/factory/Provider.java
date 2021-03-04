package org.daisy.dotify.api.factory;

import java.util.Collection;

/**
 * Provides an interface for a collection of Factories.
 *
 * @param <T> the type of Factories handled by this Provider
 * @author Joel Håkansson
 */
public interface Provider<T extends FactoryProperties> {

    /**
     * Lists all Factories.
     *
     * @return returns a collection of Factories
     */
    public Collection<T> list();

}
