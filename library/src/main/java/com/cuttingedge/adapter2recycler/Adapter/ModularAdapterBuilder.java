package com.cuttingedge.adapter2recycler.Adapter;

import java.util.List;

import com.cuttingedge.adapter2recycler.ModularItem;

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
    @NonNull
    @Size(min = 0)
    List<I> list;
    @Nullable
    ItemTouchHelper.Callback touchHelperCallback;
    @Nullable
    ItemTouchHelper itemTouchHelper;

    /**
     * Construct builder
     *
     * @param recyclerView to which adapter needs to be added.
     * @param list list of items to display. These items need to extend ModularItem.
     */
    public ModularAdapterBuilder(@NonNull RecyclerView recyclerView, @NonNull @Size(min = 0) List<I> list) {
        this.recyclerView = recyclerView;
        this.list = list;
    }

    public ModularAdapterBuilder(@NonNull RecyclerView recyclerView,
                                 @NonNull List<I> list,
                                 @Nullable ItemTouchHelperBaseCallback touchHelperCallback) {
        this(recyclerView, list);
        this.touchHelperCallback = touchHelperCallback;
    }

    public ModularAdapterBuilder(@NonNull RecyclerView recyclerView,
                                 @NonNull List<I> list,
                                 @NonNull ItemTouchHelperBaseCallback touchHelperCallback,
                                 @NonNull ItemTouchHelper itemTouchHelper) {
        this(recyclerView, list);
        this.touchHelperCallback = touchHelperCallback;
        this.itemTouchHelper = itemTouchHelper;
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
