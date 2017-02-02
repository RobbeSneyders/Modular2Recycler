package com.cuttingedge.undorecycler.Adapter;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.*;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.cuttingedge.undorecycler.R;

/**
 * Created by Robbe Sneyders
 *
 * Base adapter class with all code about animation
 */
@SuppressWarnings("WeakerAccess")
abstract class AnimatedAdapter<VH extends ViewHolder> extends Adapter<VH> {

    private int headerHeight;
    private int itemHeight;

    protected Drawable leftBackground;
    protected Drawable rightBackground;
    protected Drawable leftMark;
    protected Drawable rightMark;
    private Drawable background;

    protected boolean lastWasUndo;

    protected RecyclerView recyclerView;

    public AnimatedAdapter(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        setUpItemTouchHelper();
    }

    /**
     * Get the directions in which the touched item can be swiped
     *
     * @param position position of the touched item in the adapter
     * @return enabled directions: flags ItemTouchHelper.DIRECTION
     */
    protected abstract int getItemSwipeDirs(int position);

    /**
     * Called when item is swiped away
     * @param position position of item in adapter
     * @param swipeDir direction of swipe
     */
    protected abstract void pendingRemoval(int position, int swipeDir);

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

        private void init() {
            xMarkMargin = (int) recyclerView.getResources().getDimension(R.dimen.ic_clear_margin);
            initiated = true;
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            // TODO: Drag & Drop
            return false;
        }


        @Override
        public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return getItemSwipeDirs(viewHolder.getAdapterPosition());
        }


        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            headerHeight = 0;
            itemHeight = viewHolder.itemView.getHeight();

            if (swipeDir == ItemTouchHelper.LEFT) {
                background = rightBackground;
            } else if (swipeDir == ItemTouchHelper.RIGHT) {
                background = leftBackground;
            }
            pendingRemoval(viewHolder.getAdapterPosition(), swipeDir);
        }


        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            View itemView = viewHolder.itemView;

            // not sure why, but this method get's called for viewholder that are already swiped away
            if (viewHolder.getAdapterPosition() == -1)
                return;

            if (!initiated)
                init();

            if (dX < 0) { // Swiped left
                // draw background
                rightBackground.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                rightBackground.draw(c);

                // draw x mark
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = rightMark.getIntrinsicWidth();
                int intrinsicHeight = rightMark.getIntrinsicHeight();

                int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                int xMarkRight = itemView.getRight() - xMarkMargin;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight)/2;
                int xMarkBottom = xMarkTop + intrinsicHeight;

                rightMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);
                rightMark.draw(c);
            }
            else if (dX > 0){ // Swiped right
                // draw background
                leftBackground.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + (int) dX, itemView.getBottom());
                leftBackground.draw(c);

                // draw x mark
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = leftMark.getIntrinsicWidth();
                int intrinsicHeight = leftMark.getIntrinsicHeight();

                int xMarkLeft = itemView.getLeft() + xMarkMargin;
                int xMarkRight = itemView.getLeft() + xMarkMargin + intrinsicWidth;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight)/2;
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
                if (parent.getItemAnimator().isRunning() && !lastWasUndo) {

                    // some items might be animating down and some items might be animating up to close the gap left by the removed item
                    // this is not exclusive, both movement can be happening at the same time
                    // to reproduce this leave just enough items so the first one and the last one would be just a little off screen
                    // then remove one from the middle

                    // find first child with translationY > 0
                    // and last one with translationY < 0
                    // we're after a rect that is not covered in recycler-view views at this point in time
                    View lastViewComingDown = null;
                    View firstViewComingUp = null;

                    // this is fixed
                    int left = 0;
                    int right = parent.getWidth();

                    // this we need to find out
                    int top = 0;
                    int bottom = 0;

                    // find relevant translating views
                    int childCount = parent.getLayoutManager().getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = parent.getLayoutManager().getChildAt(i);
                        if (child.getTranslationY() < 0) {
                            // view is coming down
                            lastViewComingDown = child;
                        } else if (child.getTranslationY() > 0) {
                            // view is coming up
                            if (firstViewComingUp == null) {
                                firstViewComingUp = child;
                            }
                        }
                    }

                    if (lastViewComingDown != null && firstViewComingUp != null) {
                        // views are coming down AND going up to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    } else if (lastViewComingDown != null) {
                        // views are going down to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = lastViewComingDown.getBottom();
                    } else if (firstViewComingUp != null) {
                        // views are coming up to fill the void
                        top = firstViewComingUp.getTop();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    }

                    if (headerHeight == 0)
                        headerHeight = bottom - top - itemHeight;

                    background.setBounds(left, top + headerHeight, right, bottom);
                    background.draw(c);
                }
                super.onDraw(c, parent, state);
            }
        });
    }

}
