package com.cuttingedge.adapter2recycler.Adapter;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;

import com.cuttingedge.adapter2recycler.Helpers.AdapterModuleManager;
import com.cuttingedge.adapter2recycler.ModularItem;
import com.cuttingedge.adapter2recycler.Modules.AdapterModule;
import com.cuttingedge.adapter2recycler.Modules.DragAndDropPlugin;
import com.cuttingedge.adapter2recycler.Modules.ItemClickPlugin;
import com.cuttingedge.adapter2recycler.Modules.ItemLongClickPlugin;
import com.cuttingedge.adapter2recycler.Modules.SwipePlugin;

import java.util.Collections;
import java.util.List;

/**
 * Created by Robbe Sneyders
 *
 * Adapter with all modular logic.
 */
@SuppressWarnings("unused")
public class ModularAdapter<VH extends ViewHolder, I extends ModularItem> extends AnimatedAdapter<VH> {

    private List<I> list;

    // Last removed item saved so removal can be undone
    private I pendingRemovalItem;
    private I pendingRemovalHeader;
    private int pendingRemovalPosition;
    private int pendingRemovalSwipeDir;

    private AdapterModuleManager<AdapterModule<VH , I>, VH, I> adapterModuleManager;


    /**
     * This adapter is built via a builder class.
     *
     * @param builder builder used to construct this adapter.
     */
    ModularAdapter(ModularAdapterBuilder<I> builder) {
        super(builder.recyclerView);
        this.list = builder.list;

        this.rightBackground = builder.rightBackground;
        this.rightMark = builder.rightMark;
        this.leftBackground = builder.leftBackground;
        this.leftMark = builder.leftMark;

        adapterModuleManager = new AdapterModuleManager<>();

        recyclerView.setAdapter(this);
    }


    /**
     * Get list in current state
     */
    public List<I> getList() {
        return list;
    }


    /**
     * Methods for adding and removing single items
     */
    public void addItem(I item) {
        list.add(item);
    }

    public void addItem(int index, I item) {
        list.add(index, item);
    }

    public void removeItem(I item) {
        list.remove(item);
    }

    public void removeItem(int index) {
        list.remove(index);
    }


    /**
     * Methods for adding and removing multiple items
     */
    public void addItems(List<I> items) {
        list.addAll(items);
    }

    public void addItems(int index, List<I> items) {
        list.addAll(index, items);
    }

    public void removeItems(List<I> items) {
        list.removeAll(items);
    }


    /**
     * Swap whole list at once
     * @param newList new list
     */
    public void swap(List<I> newList){
        if (list != null) {
            list.clear();
            list.addAll(newList);
        }
        else {
            list = newList;
        }
        notifyDataSetChanged();
    }


    /**
     * Add an AdapterModule tot the AdapterModuleManager for easy future reference.
     *
     * @param module AdapterModule to be added.
     */
    public <M extends AdapterModule<VH, I>> void addAdapterModule(M module) {
        adapterModuleManager.addAdapterModule(module);
    }


    /**
     * Forward onCreateViewHolder to the appropriate AdapterModule.
     */
    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return adapterModuleManager.getAdapterModule(viewType).onCreateViewHolder(parent);
    }


    /**
     * Forward onBindViewHolder to the appropriate AdapterModule.
     */
    @Override
    public void onBindViewHolder(VH viewHolder, int position) {
        adapterModuleManager.getAdapterModule(viewHolder.getItemViewType()).onBindViewHolder(viewHolder, list.get(position));
    }


    /**
     * Set On(Long)ClickListener on ViewHolder.itemView when ViewHolder gets attached.
     * Forward onViewAttachedToWindow to the appropriate AdapterModule.
     */
    @Override
    public void onViewAttachedToWindow(VH viewHolder) {
        viewHolder.itemView.setOnClickListener(onClickListener);
        viewHolder.itemView.setOnLongClickListener(onLongClickListener);
        adapterModuleManager.getAdapterModule(viewHolder.getItemViewType()).onViewAttachedToWindow(viewHolder);
    }


    /**
     * Remove On(Long)ClickListener from ViewHolder.itemView when ViewHolder gets detached.
     * Forward onViewDetachedFromWindow to the appropriate AdapterModule
     */
    @Override
    public void onViewDetachedFromWindow(VH viewHolder) {
        viewHolder.itemView.setOnClickListener(null);
        viewHolder.itemView.setOnLongClickListener(null);
        adapterModuleManager.getAdapterModule(viewHolder.getItemViewType()).onViewDetachedFromWindow(viewHolder);
    }


    /**
     * Forward onViewRecycled to the appropriate AdapterModule
     */
    @Override
    public void onViewRecycled(VH viewHolder) {
        adapterModuleManager.getAdapterModule(viewHolder.getItemViewType()).onViewRecycled(viewHolder);
    }


    /**
     * Returns the item count in this adapter.
     *
     * @return item count.
     */
    @Override
    public int getItemCount() {
        return list.size();
    }


    /**
     * Returns type of item at specified position.
     *
     * @param position position of item.
     * @return type of item.
     */
    @Override
    public int getItemViewType(int position) {
        return adapterModuleManager.getViewType(list.get(position));
    }


    /**
     * Get enabled swipe directions for the item at the given position.
     *
     * @param position position of the touched item in the adapter
     * @return enabled swipe directions: ItemTouchHelper.DIRECTION
     */
    @Override
    protected int getItemSwipeDirs(int position) {
        AdapterModule<VH, I> module = adapterModuleManager.getAdapterModule(getItemViewType(position));
        if (module instanceof SwipePlugin)
            return ((SwipePlugin) module).getSwipeDirs();
        return 0;
    }


    /**
     * Get enabled drag directions for the item at the given position.
     *
     * @param position position of the touched item in the adapter
     * @return enabled drag directions: ItemTouchHelper.DIRECTION
     */
    @Override
    protected int getDragDirs(int position) {
        AdapterModule<VH, I> module = adapterModuleManager.getAdapterModule(getItemViewType(position));
        if (module instanceof DragAndDropPlugin)
            return ((DragAndDropPlugin) module).getDragDirs();
        return 0;
    }


    /**
     * Called while view is being dragged.
     *
     * @param fromPosition current position of viewHolder in adapter.
     * @param toPosition position of viewHolder which place will be taken.
     * @return true if swapped.
     */
    @Override
    protected boolean onDrag(int fromPosition, int toPosition) {
        AdapterModule<VH, I> module = adapterModuleManager.getAdapterModule(getItemViewType(fromPosition));
        DragAndDropPlugin dragNdrop = (DragAndDropPlugin) module;
        boolean stayInSection = dragNdrop.keepDragInSection();

        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                if (stayInSection && list.get(i+1).isHeader())
                    return false;

                Collections.swap(list, i, i + 1);
            }
        }
        else {
            for (int i = fromPosition; i > toPosition; i--) {
                if (stayInSection && list.get(i-1).isHeader())
                    return false;

                Collections.swap(list, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }


    /**
     * Called when item status should be pending removal.
     * The item gets cached in the adapter and removed from the database.
     *
     * @param position Position of item to be removed.
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void onSwiped(int position, int swipeDir) {
        AdapterModule<VH, I> module = adapterModuleManager.getAdapterModule(getItemViewType(position));

        pendingRemovalItem = list.get(position);
        pendingRemovalPosition = position;
        pendingRemovalSwipeDir = swipeDir;

        if (position != 0 && list.get(position - 1).isHeader() &&
                (position == list.size() - 1 || list.get(position + 1).isHeader())) {
            pendingRemovalHeader = list.get(position - 1);
            list.remove(position);
            list.remove(position - 1);
            notifyItemRangeRemoved(position -1, 2);
        } else {
            pendingRemovalHeader = null;
            list.remove(position);
            notifyItemRemoved(position);
        }

        SwipePlugin<I> swipeListener = (SwipePlugin<I>) module;
        boolean undoEnabled = (swipeListener.getUndoDirs() & swipeDir) == swipeDir;
        showSnackbar(swipeListener.onSwiped(pendingRemovalItem, swipeDir), undoEnabled);
    }


    /**
     * Called when user presses undo.
     * Add the item back to the itemList on it's old location and notify for changes.
     */
    @SuppressWarnings("unchecked")
    private void undo() {
        lastWasSwiped = false;

        if (pendingRemovalHeader != null) {
            list.add(pendingRemovalPosition - 1, pendingRemovalHeader);
            list.add(pendingRemovalPosition, pendingRemovalItem);
            notifyItemRangeInserted(pendingRemovalPosition -1, 2);
        }
        else {
            list.add(pendingRemovalPosition, pendingRemovalItem);
            notifyItemInserted(pendingRemovalPosition);
        }

        AdapterModule<VH, I> module = adapterModuleManager.getAdapterModule(adapterModuleManager.getViewType(pendingRemovalItem));
        SwipePlugin<I> swipeListener = (SwipePlugin<I>) module;

        swipeListener.onUndo(pendingRemovalItem, pendingRemovalSwipeDir);
    }


    /**
     * Show snackbar with undo button.
     */
    private void showSnackbar(String message, boolean undoEnabled) {
        final Snackbar snackbar = Snackbar.make(recyclerView,
                message, Snackbar.LENGTH_LONG);

        if (undoEnabled) {
            snackbar.setAction("Undo", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    undo();
                    snackbar.dismiss();
                }
            });
        }
        snackbar.show();
    }


    /**
     * OnClickListener to forward clicks to ItemClickPlugin
     */
    @SuppressWarnings("unchecked")
    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            ViewHolder viewHolder = recyclerView.getChildViewHolder(v);
            AdapterModule<VH, I> module = adapterModuleManager.getAdapterModule(viewHolder.getItemViewType());
            if (module instanceof ItemClickPlugin)
                ((ItemClickPlugin<I>) module).onItemClicked(list.get(viewHolder.getAdapterPosition()));
        }
    };


    /**
     * OnLongClickListener to forward clicks to ItemLongClickPlugin
     */
    @SuppressWarnings("unchecked")
    private OnLongClickListener onLongClickListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            ViewHolder viewHolder = recyclerView.getChildViewHolder(v);
            AdapterModule module = adapterModuleManager.getAdapterModule(viewHolder.getItemViewType());
            return module instanceof ItemClickPlugin &&
                    ((ItemLongClickPlugin<I>) module).onItemLongClicked(list.get(viewHolder.getAdapterPosition()));
        }
    };
}
