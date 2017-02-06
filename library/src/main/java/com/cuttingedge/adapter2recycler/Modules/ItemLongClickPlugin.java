package com.cuttingedge.adapter2recycler.Modules;

import com.cuttingedge.adapter2recycler.ModularItem;

/**
 * Created by Robbe Sneyders
 *
 * Interface to implement in AdapterModule if ItemLongClicks are wanted
 */

public interface ItemLongClickPlugin<I extends ModularItem> {

    /**
     * Called when item of corresponding adapterModule was long clicked.
     *
     * @param item long clicked item
     */
    boolean onItemLongClicked(I item);
}
