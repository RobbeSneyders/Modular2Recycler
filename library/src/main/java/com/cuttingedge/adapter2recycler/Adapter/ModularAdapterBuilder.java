package com.cuttingedge.adapter2recycler.Adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.cuttingedge.adapter2recycler.ModularItem;

import java.util.List;

/**
 * Created by Robbe Sneyders
 *
 * Builder class for ModularAdapter
 */
public class ModularAdapterBuilder<I extends ModularItem> {

    @NonNull
    RecyclerView recyclerView;
    @Size(min = 0)
    List<I> list;
    @Nullable
    ItemTouchHelper.Callback touchHelperCallback;

    /**
     * Construct builder
     *
     * @param recyclerView to which adapter needs to be added.
     * @param list list of items to display. These items need to extend ModularItem.
     */
    public ModularAdapterBuilder(@NonNull RecyclerView recyclerView, @Size(min = 0) List<I> list) {
        this.recyclerView = recyclerView;
        this.list = list;
    }

    public ModularAdapterBuilder(@NonNull RecyclerView recyclerView, List<I> list, @Nullable ItemTouchHelperBaseCallback touchHelperCallback) {
        this(recyclerView, list);
        this.touchHelperCallback = touchHelperCallback;
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
