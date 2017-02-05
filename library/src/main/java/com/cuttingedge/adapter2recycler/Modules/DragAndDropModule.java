package com.cuttingedge.adapter2recycler.Modules;

/**
 * Created by Robbe Sneyders on 2/02/2017.
 *
 * Interface to implement in AdapterModule if corresponding items should be draggable.
 */
public interface DragAndDropModule {

    /**
     * @return true if item should not be draggable across headers.
     */
    boolean keepDragInSection();


    /**
     * @return directions in which item can be dragged
     */
    int getDragDirs();
}
