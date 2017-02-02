package com.cuttingedge.undorecycler.Modules;

import com.cuttingedge.undorecycler.ModularItem;

/**
 * Created by Robbe Sneyders
 *
 * Interface to implement in AdapterModule if ItemLongClicks are wanted
 */

public interface OnItemLongClickListenerModule<Item extends ModularItem> {

    /**
     * Called when item of corresponding adapterModule was long clicked.
     *
     * @param item long clicked item
     */
    boolean onItemLongClicked(Item item);
}
