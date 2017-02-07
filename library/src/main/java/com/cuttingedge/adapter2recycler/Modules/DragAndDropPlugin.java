package com.cuttingedge.adapter2recycler.Modules;

import com.cuttingedge.adapter2recycler.ModularItem;

import java.util.List;

/**
 * Created by Robbe Sneyders on 2/02/2017.
 *
 * Interface to implement in AdapterModule if corresponding items should be draggable.
 */
public interface DragAndDropPlugin<I extends ModularItem> {

    /**
     * @return true if item should not be draggable across headers.
     */
    boolean keepDragInSection();


    /**
     * @return directions in which item can be dragged
     */
    int getDragDirs();


    /**
     * Called when item is dragged and dropped.
     *
     * Might not be the best parameters. If you would like other parameters passed into this method,
     * please open an issue on the github page https://github.com/RobbeSneyders/Modular2Recycler.
     *
     * @param item moved item
     * @param adapterList list after item has been moved. Might also contain other classes implementing
     *                    ModularItem than the one handled by the called module.
     */
    void onMoved(I item, List<ModularItem> adapterList);
}
