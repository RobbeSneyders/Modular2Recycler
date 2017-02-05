package com.cuttingedge.adapter2recycler.Adapter;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.cuttingedge.adapter2recycler.R;

/**
 * Created by Robbe Sneyders
 *
 * Base adapter class with all code for the animation of views.
 */
@SuppressWarnings("WeakerAccess")
abstract class AnimatedAdapter<VH extends ViewHolder> extends Adapter<VH> {

    protected Drawable leftBackground;
    protected Drawable rightBackground;
    protected Drawable leftMark;
    protected Drawable rightMark;
    private Drawable background;

    protected boolean lastWasSwiped;

    protected RecyclerView recyclerView;


    /**
     * Constructor.
     *
     * @param recyclerView recyclerView to which this adapter is connected.
     */
    public AnimatedAdapter(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        setUpItemTouchHelper();
    }


    /**
     * Get the directions in which the touched item can be swiped.
     *
     * @param position position of the touched item in the adapter.
     * @return enabled directions: flags ItemTouchHelper.LEFT or RIGHT.
     */
    protected abstract int getItemSwipeDirs(int position);


    /**
     * Is drag and drop enabled for the touched item?
     *
     * @param position position of the touched item in the adapter.
     * @return true if enabled, false otherwise.
     */
    protected abstract int getDragDirs(int position);


    /**
     * Called while viewHolder is dragged.
     *
     * @param fromPosition current position of viewHolder in adapter.
     * @param toPosition position of viewHolder which place will be taken.
     * @return True if the viewHolder has been moved to target position in adapter.
     */
    protected abstract boolean onDrag(int fromPosition, int toPosition);


    /**
     * Called when item is swiped away
     * @param position position of item in adapter
     * @param swipeDir direction of swipe
     */
    protected abstract void onSwiped(int position, int swipeDir);


    /**
     * Sets up the ItemTouchHelper and attaches it to the RecyclerView.
     */
    private void setUpItemTouchHelper() {
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
        setUpAnimationDecoratorHelper(recyclerView);
    }


    /**
     * Callback used in ItemTouchHelper to draw a background behind the swiped item.
     */
    private ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, 0) {

        // we want to cache these and not allocate anything repeatedly in the onChildDraw method
        int xMarkMargin;
        boolean initiated;


        /**
         * Margin is loaded from resources during first initialization.
         */
        private void init() {
            xMarkMargin = (int) recyclerView.getResources().getDimension(R.dimen.ic_clear_margin);
            initiated = true;
        }


        /**
         * Return direction flags for which movement (swipe and drag) is enabled for current ViewHolder.
         *
         * @param recyclerView current RecyclerView.
         * @param viewHolder current ViewHolder.
         * @return flags.
         */
        @Override
        public int getMovementFlags(RecyclerView recyclerView, ViewHolder viewHolder) {
            int dragFlags = AnimatedAdapter.this.getDragDirs(viewHolder.getAdapterPosition());
            int swipeFlags = getItemSwipeDirs(viewHolder.getAdapterPosition());
            return makeMovementFlags(dragFlags, swipeFlags);
        }


        /**
         * Called when viewHolder was swiped out of RecyclerView.
         *
         * @param viewHolder swiped ViewHolder.
         * @param swipeDir swipe direction.
         */
        @Override
        public void onSwiped(ViewHolder viewHolder, int swipeDir) {
            View v = viewHolder.itemView;

            if (swipeDir == ItemTouchHelper.LEFT)
                background = rightBackground;
            else if (swipeDir == ItemTouchHelper.RIGHT)
                background = leftBackground;

            background.setBounds(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());

            lastWasSwiped = true;
            AnimatedAdapter.this.onSwiped(viewHolder.getAdapterPosition(), swipeDir);
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
        public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder target) {
            lastWasSwiped = false;
            return onDrag(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        }


        /**
         * Called by ItemTouchHelper on RecyclerView's onDraw callback.
         */
        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            if (actionState != ItemTouchHelper.ACTION_STATE_SWIPE) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                return;
            }

            View itemView = viewHolder.itemView;

            // not sure why, but this method get's called for viewholder that are already swiped away
            if (viewHolder.getAdapterPosition() == -1)
                return;

            if (!initiated)
                init();

            if (dX < 0) { // Swiped left
                // draw background
                rightBackground.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getRight(), itemView.getBottom());
                rightBackground.draw(c);

                // draw x mark
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = rightMark.getIntrinsicWidth();
                int intrinsicHeight = rightMark.getIntrinsicHeight();

                int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                int xMarkRight = itemView.getRight() - xMarkMargin;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                int xMarkBottom = xMarkTop + intrinsicHeight;

                rightMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);
                rightMark.draw(c);
            } else if (dX > 0) { // Swiped right
                // draw background
                leftBackground.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getRight(), itemView.getBottom());
                leftBackground.draw(c);

                // draw x mark
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = leftMark.getIntrinsicWidth();
                int intrinsicHeight = leftMark.getIntrinsicHeight();

                int xMarkLeft = itemView.getLeft() + xMarkMargin;
                int xMarkRight = itemView.getLeft() + xMarkMargin + intrinsicWidth;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                int xMarkBottom = xMarkTop + intrinsicHeight;

                leftMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);
                leftMark.draw(c);
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };


    /**
     * Set up ItemDecorator that draws background while the items are animating to their new place
     * after an item is removed.
     */
    private void setUpAnimationDecoratorHelper(RecyclerView recycler) {

        recycler.addItemDecoration(new RecyclerView.ItemDecoration() {

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                // only if animation is in progress
                if (lastWasSwiped && parent.getItemAnimator().isRunning()) {
                    background.draw(c);
                }
                super.onDraw(c, parent, state);
            }
        });
    }
}
