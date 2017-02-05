package com.cuttingedge.adapter2recycler.Adapter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;

import com.cuttingedge.adapter2recycler.ModularItem;

import java.util.List;

/**
 * Created by Robbe Sneyders
 *
 * Builder class for ModularAdapter
 */
public class ModularAdapterBuilder<I extends ModularItem> {

    RecyclerView recyclerView;
    List<I> list;

    Drawable rightBackground = new ColorDrawable(Color.TRANSPARENT);
    Drawable rightMark = new ColorDrawable(Color.TRANSPARENT);
    Drawable leftBackground = new ColorDrawable(Color.TRANSPARENT);
    Drawable leftMark = new ColorDrawable(Color.TRANSPARENT);


    /**
     * Construct builder
     *
     * @param recyclerView to which adapter needs to be added.
     * @param list list of items to display. These items need to extend ModularItem.
     */
    public ModularAdapterBuilder(RecyclerView recyclerView, List<I> list) {
        this.recyclerView = recyclerView;
        this.list = list;
    }


    /**
     * Set attributes for swiping to the left.
     *
     * @param color color drawn behind view
     * @param icon icon drawn behind view
     * @return builder
     */
    public ModularAdapterBuilder<I> setSwipeLeft(int color, Drawable icon) {
        rightBackground = new ColorDrawable(color);
        rightMark = icon;
        return this;
    }


    /**
     * Set attributes for swiping to the right.
     *
     * @param color color drawn behind view
     * @param icon icon drawn behind view
     * @return builder
     */
    public ModularAdapterBuilder<I> setSwipeRight(int color, Drawable icon) {
        leftBackground = new ColorDrawable(color);
        leftMark = icon;
        return this;
    }


    /**
     * Build the ModularAdapter.
     *
     * @return built ModularAdapter.
     */
    public <VH extends ViewHolder> ModularAdapter<VH, I> build() {
        return new ModularAdapter<>(this);
    }
}
