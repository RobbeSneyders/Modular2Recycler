package com.cuttingedge.undorecycler.Adapter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;

import com.cuttingedge.undorecycler.ModularItem;
import com.cuttingedge.undorecycler.R;

import java.util.List;

/**
 * Created by Robbe Sneyders
 *
 * Builder class for ModularAdapter
 */
public class ModularAdapterBuilder<I extends ModularItem> {

    RecyclerView recyclerView;
    List<I> list;

    Drawable rightBackground;
    Drawable rightMark;
    Drawable leftBackground;
    Drawable leftMark;


    /**
     * Construct builder
     *
     * @param recyclerView to which adapter needs to be added.
     * @param list list of items to display. These items need to extend ModularItem.
     */
    public ModularAdapterBuilder(RecyclerView recyclerView, List<I> list) {
        this.recyclerView = recyclerView;
        this.list = list;

        // default values;
        rightBackground = new ColorDrawable(Color.RED);
        rightMark = ResourcesCompat.getDrawable(recyclerView.getContext().getResources(), R.drawable.ic_delete_white_24dp, null);
        leftBackground = new ColorDrawable(Color.GREEN);
        leftMark = ResourcesCompat.getDrawable(recyclerView.getContext().getResources(), R.drawable.ic_archive_white_24dp, null);
    }


    /**
     * Set attributes for swiping to the left.
     *
     * @param color color drawn behind view
     * @param icon icon drawn behind view
     * @return builder
     */
    public ModularAdapterBuilder setSwipeLeft(int color, Drawable icon) {
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
    public ModularAdapterBuilder setSwipeRight(int color, Drawable icon) {
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
