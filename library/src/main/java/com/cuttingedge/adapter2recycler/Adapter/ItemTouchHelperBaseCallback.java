package com.cuttingedge.adapter2recycler.Adapter;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

public abstract class ItemTouchHelperBaseCallback extends ItemTouchHelper.SimpleCallback {

    private AnimatedAdapter adapter;

    public ItemTouchHelperBaseCallback() {
        super(0, 0);
    }

    public final void setAdapter(AnimatedAdapter adapter) {
        this.adapter = adapter;
    }

    /**
     * Return direction flags for which movement (swipe and drag) is enabled for current ViewHolder.
     *
     * @param recyclerView current RecyclerView.
     * @param viewHolder current ViewHolder.
     * @return flags.
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int position = viewHolder.getAdapterPosition();
        if (position == RecyclerView.NO_POSITION) {
            return super.getMovementFlags(recyclerView, viewHolder);
        } else {
            return makeMovementFlags(adapter.getDragDirs(position), adapter.getItemSwipeDirs(position));
        }
    }

    /**
     * Called when viewHolder was swiped out of RecyclerView.
     *
     * @param viewHolder swiped ViewHolder.
     * @param swipeDir swipe direction.
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
        adapter.onSwiped(viewHolder.getAdapterPosition(), swipeDir);
    }

    /**
     * Called during drag of ViewHolder.
     *
     * @param recyclerView current RecyclerView.
     * @param viewHolder dragged ViewHolder.
     * @param target ViewHolder which place will be taken.
     * @return true if positions are swapped.
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return adapter.onDrag(viewHolder.getAdapterPosition(), target.getAdapterPosition());
    }
}
