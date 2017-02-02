package com.cuttingedge.undorecycler.Adapter;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;

import com.cuttingedge.undorecycler.Modules.AdapterModule;
import com.cuttingedge.undorecycler.AdapterModuleManager;
import com.cuttingedge.undorecycler.ModularItem;
import com.cuttingedge.undorecycler.Modules.OnItemClickListenerModule;
import com.cuttingedge.undorecycler.Modules.OnItemLongClickListenerModule;
import com.cuttingedge.undorecycler.Modules.OnSwipeListenerModule;

import java.util.List;

/**
 * Created by Robbe Sneyders
 *
 * Adapter with all modular logic.
 */
public class ModularAdapter<VH extends ViewHolder, Item extends ModularItem> extends AnimatedAdapter {

    private List<Item> list;

    // Last removed item saved so removal can be undone
    private Item pendingRemovalItem;
    private Item pendingRemovalHeader;
    private int pendingRemovalPosition;
    private int pendingRemovalSwipeDir;

    private AdapterModuleManager adapterModuleManager;


    /**
     * This adapter is built via a builder class.
     *
     * @param builder builder used to construct this adapter.
     */
    ModularAdapter(ModularAdapterBuilder builder) {
        super(builder.recyclerView);
        this.list = builder.list;

        this.rightBackground = builder.rightBackground;
        this.rightMark = builder.rightMark;
        this.leftBackground = builder.leftBackground;
        this.leftMark = builder.leftMark;

        adapterModuleManager = new AdapterModuleManager();

        recyclerView.setAdapter(this);
    }


    /**
     * Methods for adding and removing single items
     */
    public void addItem(Item item) {
        list.add(item);
    }

    public void addItem(int index, Item item) {
        list.add(index, item);
    }

    public void removeItem(Item item) {
        list.remove(item);
    }

    public void removeItem(int index) {
        list.remove(index);
    }


    /**
     * Swap whole list at once
     * @param newList new list
     */
    public void swap(List<Item> newList){
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
    public void addAdapterModule(AdapterModule module) {
        adapterModuleManager.addAdapterModule(module);
    }


    /**
     * Forward onCreateViewHolder to the appropriate AdapterModule.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return adapterModuleManager.getAdapterModule(viewType).onCreateViewHolder(parent);
    }


    /**
     * Forward onBindViewHolder to the appropriate AdapterModule.
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        adapterModuleManager.getAdapterModule(viewHolder.getItemViewType()).onBindViewHolder(viewHolder, list.get(position));
    }


    /**
     * Set On(Long)ClickListener on ViewHolder.itemView when ViewHolder gets attached.
     * Forward onViewAttachedToWindow to the appropriate AdapterModule.
     */
    @Override
    public void onViewAttachedToWindow(ViewHolder viewHolder) {
        viewHolder.itemView.setOnClickListener(onClickListener);
        viewHolder.itemView.setOnLongClickListener(onLongClickListener);
        adapterModuleManager.getAdapterModule(viewHolder.getItemViewType()).onViewAttachedToWindow(viewHolder);
    }


    /**
     * Remove On(Long)ClickListener from ViewHolder.itemView when ViewHolder gets detached.
     * Forward onViewDetachedFromWindow to the appropriate AdapterModule
     */
    @Override
    public void onViewDetachedFromWindow(ViewHolder viewHolder) {
        viewHolder.itemView.setOnClickListener(null);
        viewHolder.itemView.setOnLongClickListener(null);
        adapterModuleManager.getAdapterModule(viewHolder.getItemViewType()).onViewDetachedFromWindow(viewHolder);
    }


    /**
     * Forward onViewRecycled to the appropriate AdapterModule
     */
    @Override
    public void onViewRecycled(ViewHolder viewHolder) {
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
        AdapterModule delegate = adapterModuleManager.getAdapterModule(getItemViewType(position));
        if (delegate instanceof OnSwipeListenerModule)
            return ((OnSwipeListenerModule) delegate).getSwipeDirs();
        return 0;
    }


    /**
     * Called when item status should be pending removal.
     * The item gets cached in the adapter and removed from the database.
     *
     * @param position Position of item to be removed.
     */
    @Override
    protected void pendingRemoval(int position, int swipeDir) {
        AdapterModule delegate = adapterModuleManager.getAdapterModule(getItemViewType(position));

        lastWasUndo = false;
        pendingRemovalItem = list.get(position);
        pendingRemovalPosition = position;
        pendingRemovalSwipeDir = swipeDir;

        if (position != 0 && list.get(position - 1).isHeader &&
                (position == list.size() - 1 || list.get(position + 1).isHeader)) {
            pendingRemovalHeader = list.get(position - 1);
            list.remove(position);
            list.remove(position - 1);
            notifyItemRangeRemoved(position -1, 2);
        } else {
            pendingRemovalHeader = null;
            list.remove(position);
            notifyItemRemoved(position);
        }

        OnSwipeListenerModule swipeListener = (OnSwipeListenerModule) delegate;
        boolean undoEnabled = (swipeListener.getUndoDirs() & swipeDir) == swipeDir;
        showSnackbar(swipeListener.onSwiped(pendingRemovalItem, swipeDir), undoEnabled);
    }


    /**
     * Called when user presses undo.
     * Add the item back to the itemList on it's old location and notify for changes.
     */
    private void undo() {
        lastWasUndo = true;

        if (pendingRemovalHeader != null) {
            list.add(pendingRemovalPosition - 1, pendingRemovalHeader);
            list.add(pendingRemovalPosition, pendingRemovalItem);
            notifyItemRangeInserted(pendingRemovalPosition -1, 2);
        }
        else {
            list.add(pendingRemovalPosition, pendingRemovalItem);
            notifyItemInserted(pendingRemovalPosition);
        }

        AdapterModule delegate = adapterModuleManager.getAdapterModule(adapterModuleManager.getViewType(pendingRemovalItem));
        OnSwipeListenerModule swipeListener = (OnSwipeListenerModule) delegate;

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
     * OnClickListener to forward clicks to OnItemClickListenerModule
     */
    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            ViewHolder viewHolder = recyclerView.getChildViewHolder(v);
            AdapterModule delegate = adapterModuleManager.getAdapterModule(viewHolder.getItemViewType());
            if (delegate instanceof OnItemClickListenerModule)
                ((OnItemClickListenerModule) delegate).onItemClicked(list.get(viewHolder.getAdapterPosition()));
        }
    };


    /**
     * OnLongClickListener to forward clicks to OnItemLongClickListenerModule
     */
    OnLongClickListener onLongClickListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            ViewHolder viewHolder = recyclerView.getChildViewHolder(v);
            AdapterModule delegate = adapterModuleManager.getAdapterModule(viewHolder.getItemViewType());
            if (delegate instanceof OnItemClickListenerModule)
                return ((OnItemLongClickListenerModule) delegate).onItemLongClicked(list.get(viewHolder.getAdapterPosition()));
            return false;
        }
    };
}
