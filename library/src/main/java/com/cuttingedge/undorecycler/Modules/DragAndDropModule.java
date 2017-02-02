package com.cuttingedge.undorecycler.Modules;

/**
 * Created by Robbe Sneyders on 2/02/2017.
 *
 * Interface to implement in AdapterModule if corresponding items should be draggable.
 */
public interface DragAndDropModule {

    /**
     * @return true if item should not be draggable across headers.
     */
    boolean getStayInSection();
}
