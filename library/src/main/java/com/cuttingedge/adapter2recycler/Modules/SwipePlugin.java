package com.cuttingedge.adapter2recycler.Modules;

import com.cuttingedge.adapter2recycler.ModularItem;

/**
 * Created by Robbe Sneyders
 *
 * Interface to implement in AdapterModule if corresponding items should be swipeable.
 */
public interface SwipePlugin<I extends ModularItem> {

    /**
     * @return directions in which item should be swipeable.
     */
    int getSwipeDirs();


    /**
     * @return directions in which swipe action should be undoable.
     */
    int getUndoDirs();


    /**
     * Called when item was swiped out of list. Go ahead and update the item in your database.
     * A reference is kept in the adapter and provided when removal needs to be undone.
     *
     * @param item swiped item
     * @param swipeDir direction of swipe
     * @return text to show in snackbar after swipe
     */
    String onSwiped(I item, int swipeDir);


    /**
     * Called when action is undone.
     *
     * @param item item for which action was undone.
     * @param swipeDir direction of undone action.
     */
    void onUndo(I item, int swipeDir);

}
