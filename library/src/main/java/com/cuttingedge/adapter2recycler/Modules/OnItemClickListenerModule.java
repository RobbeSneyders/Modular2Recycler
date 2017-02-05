package com.cuttingedge.adapter2recycler.Modules;

import com.cuttingedge.adapter2recycler.ModularItem;

/**
 * Created by Robbe Sneyders
 *
 * Interface to implement in AdapterModule if ItemClicks are wanted
 */
public interface OnItemClickListenerModule<I extends ModularItem> {

    /**
     * Called when item of corresponding adapterModule was clicked.
     *
     * @param item clicked item
     */
     void onItemClicked(I item);
}
