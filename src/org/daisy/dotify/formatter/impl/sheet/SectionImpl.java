package org.daisy.dotify.formatter.impl.sheet;

import org.daisy.dotify.api.formatter.Page;
import org.daisy.dotify.api.formatter.Section;
import org.daisy.dotify.api.writer.SectionProperties;

import java.util.ArrayList;
import java.util.List;

class SectionImpl implements Section {
    private final List<Page> pages;
    private final SectionProperties props;

    public SectionImpl(SectionProperties props) {
        this.pages = new ArrayList<>();
        this.props = props;
    }

    void addPage(Page p) {
        pages.add(p);
    }

    @Override
    public SectionProperties getSectionProperties() {
        return props;
    }

    @Override
    public List<? extends Page> getPages() {
        return pages;
    }

}
