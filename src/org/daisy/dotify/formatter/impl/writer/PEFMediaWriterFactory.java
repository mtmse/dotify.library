package org.daisy.dotify.formatter.impl.writer;

import org.daisy.dotify.api.writer.PagedMediaWriter;
import org.daisy.dotify.api.writer.PagedMediaWriterConfigurationException;
import org.daisy.dotify.api.writer.PagedMediaWriterFactory;

class PEFMediaWriterFactory implements PagedMediaWriterFactory {

    public PEFMediaWriterFactory() {
        super();
    }

    @Override
    public PagedMediaWriter newPagedMediaWriter()
            throws PagedMediaWriterConfigurationException {
        return new PEFMediaWriter();
    }

    @Override
    public Object getFeature(String key) {
        return null;
    }

    @Override
    public void setFeature(String key, Object value)
            throws PagedMediaWriterConfigurationException {
        throw new PEFMediaWriterConfigurationException("Unknown feature: " + key);
    }

    private class PEFMediaWriterConfigurationException extends PagedMediaWriterConfigurationException {

        /**
         *
         */
        private static final long serialVersionUID = -2673985749596696888L;

        public PEFMediaWriterConfigurationException(String message) {
            super(message);
        }

    }

}
