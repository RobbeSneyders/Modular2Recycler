package com.cuttingedge.adapter2recycler.Adapter;

import com.cuttingedge.adapter2recycler.ModularItem;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

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
