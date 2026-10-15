package org.daisy.dotify.formatter.impl.core;

import org.daisy.dotify.api.formatter.BlockProperties;
import org.daisy.dotify.formatter.impl.search.BlockAddress;

class AncestorContext {
    private final BlockProperties blockProperties;
    private final Integer volumeKeepPriority;
    private int listIterator;
    private BlockAddress keepWithPreviousSheetsScope;

    AncestorContext(BlockProperties props, Integer volumeKeep) {
        this.blockProperties = props;
        this.volumeKeepPriority = volumeKeep;
        this.listIterator = 0;
        this.keepWithPreviousSheetsScope = null;
    }

    public BlockProperties getBlockProperties() {
        return blockProperties;
    }

    public Integer getVolumeKeepPriority() {
        return volumeKeepPriority;
    }

    public int nextListNumber() {
        listIterator++;
        return listIterator;
    }

    public void setListNumber(int value) {
        listIterator = value;
    }

    public BlockAddress getKeepWithPreviousSheetsScope() {
        return keepWithPreviousSheetsScope;
    }

    public void setKeepWithPreviousSheetsScope(BlockAddress value) {
        keepWithPreviousSheetsScope = value;
    }
}
