package org.daisy.dotify.text.impl;

import org.daisy.dotify.api.text.Integer2Text;
import org.daisy.dotify.api.text.Integer2TextConfigurationException;
import org.daisy.dotify.api.text.Integer2TextFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class Integer2TextFactoryImpl implements Integer2TextFactory {
    /**
     * Use of lower case is allowed here. The enum name is used for populating the map and list below,
     * so the name should be equal to the language code for the implementation with '-' replaced by '_'.
     * '_' is subsequently replaced by '-' in the map and list.
     */
    private enum Implementation {
        //sv(SvInt2TextLocalization.class),
        //sv_SE(SvInt2TextLocalization.class),
        fi(FiInt2TextLocalization.class),
        fi_FI(FiInt2TextLocalization.class),
        no(NoInt2TextLocalization.class),
        no_NO(NoInt2TextLocalization.class),
        nb(NoInt2TextLocalization.class),
        nb_NO(NoInt2TextLocalization.class),
        nn(NoInt2TextLocalization.class),
        nn_NO(NoInt2TextLocalization.class);
        final Class<? extends Integer2Text> clazz;

        Implementation(Class<? extends Integer2Text> clazz) {
            this.clazz = clazz;
        }
    }

    static final Map<String, Class<? extends Integer2Text>> LOCALES;
    static final List<String> DISPLAY_NAMES;

    static {
        List<String> localeNames = new ArrayList<>();
        Map<String, Class<? extends Integer2Text>> locales = new ConcurrentHashMap<>();
        for (Implementation impl : Implementation.values()) {
            //Only use lower case keys
            String name = impl.name().replace('_', '-');
            locales.put(name.toLowerCase(Locale.ENGLISH), impl.clazz);
            localeNames.add(name);
        }
        LOCALES = Collections.unmodifiableMap(locales);
        DISPLAY_NAMES = Collections.unmodifiableList(localeNames);
    }

    @Override
    public Integer2Text newInteger2Text(String locale) throws Integer2TextConfigurationException {
        try {
            Class<? extends Integer2Text> c = LOCALES.get(locale.toLowerCase(Locale.ENGLISH));
            if (c == null) {
                throw new Integer2TextConfigurationExceptionImpl("Locale not supported.");
            }
            return c.newInstance();
        } catch (InstantiationException e) {
            throw new Integer2TextConfigurationExceptionImpl(e);
        } catch (IllegalAccessException e) {
            throw new Integer2TextConfigurationExceptionImpl(e);
        }
    }

    @Override
    public Object getFeature(String key) {
        return null;
    }

    @Override
    public void setFeature(String key, Object value) throws Integer2TextConfigurationException {
        throw new Integer2TextConfigurationExceptionImpl();
    }

    private class Integer2TextConfigurationExceptionImpl extends Integer2TextConfigurationException {

        /**
         *
         */
        private static final long serialVersionUID = -1129385990516203885L;

        private Integer2TextConfigurationExceptionImpl() {
            super();
        }

        private Integer2TextConfigurationExceptionImpl(String message) {
            super(message);
        }

        private Integer2TextConfigurationExceptionImpl(Throwable cause) {
            super(cause);
        }


    }

}
