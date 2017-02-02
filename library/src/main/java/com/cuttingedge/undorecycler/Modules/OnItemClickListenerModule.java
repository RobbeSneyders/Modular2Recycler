package com.cuttingedge.undorecycler.Modules;

import com.cuttingedge.undorecycler.ModularItem;

/**
 * Created by Robbe Sneyders
 *
 * Interface to implement in AdapterModule if ItemClicks are wanted
 */
public interface OnItemClickListenerModule<Item extends ModularItem> {

    /**
     * Called when item of corresponding adapterModule was clicked.
     *
     * @param item clicked item
     */
     void onItemClicked(Item item);
}
