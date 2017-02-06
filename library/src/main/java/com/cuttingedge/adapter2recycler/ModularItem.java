package com.cuttingedge.adapter2recycler;

/**
 * Created by Robbe Sneyders
 *
 * Item to extend for use of this adapter.
 */
public interface ModularItem {

    /**
     * Set to true if item should be handled as Header.
     * Is automatically removed when all subitems are removed.
     */
    boolean isHeader();

}
