package com.cuttingedge.adapter2recycler;

/**
 * Created by Robbe Sneyders
 *
 * Item to extend for use of this adapter.
 */
public abstract class ModularItem {

    /**
     * Set to true if item should be handled as Header.
     * Is automatically removed when all subitems are removed.
     */
    public boolean isHeader;

}
