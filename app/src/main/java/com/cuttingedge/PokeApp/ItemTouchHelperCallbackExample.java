package com.cuttingedge.PokeApp;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.cuttingedge.adapter2recycler.Adapter.ItemTouchHelperBaseCallback;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public final class ItemTouchHelperCallbackExample extends ItemTouchHelperBaseCallback {

    private int xMarkMargin;
    private boolean lastWasSwiped;
    private Drawable background;
    private Drawable rightBackground = new ColorDrawable(Color.TRANSPARENT);
    private Drawable rightMark = new ColorDrawable(Color.TRANSPARENT);
    private Drawable leftBackground = new ColorDrawable(Color.TRANSPARENT);
    private Drawable leftMark = new ColorDrawable(Color.TRANSPARENT);

    public ItemTouchHelperCallbackExample(RecyclerView recyclerView) {
        xMarkMargin = (int) recyclerView.getResources().getDimension(R.dimen.ic_clear_margin);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

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

    /**
     * Set attributes for swiping to the left.
     *
     * @param color color drawn behind view
     * @param icon icon drawn behind view
     */
    public void setSwipeLeft(int color, Drawable icon) {
        rightBackground = new ColorDrawable(color);
        rightMark = icon;
    }


    /**
     * Set attributes for swiping to the right.
     *
     * @param color color drawn behind view
     * @param icon icon drawn behind view
     */
    public void setSwipeRight(int color, Drawable icon) {
        leftBackground = new ColorDrawable(color);
        leftMark = icon;
    }

    /**
     * Called when viewHolder was swiped out of RecyclerView.
     *
     * @param viewHolder swiped ViewHolder.
     * @param swipeDir swipe direction.
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
        View v = viewHolder.itemView;

        if (swipeDir == ItemTouchHelper.LEFT)
            background = rightBackground;
        else if (swipeDir == ItemTouchHelper.RIGHT)
            background = leftBackground;

        background.setBounds(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());

        lastWasSwiped = true;
        super.onSwiped(viewHolder, swipeDir);
    }


    /**
     * Called by ItemTouchHelper on RecyclerView's onDraw callback.
     */
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        if (actionState != ItemTouchHelper.ACTION_STATE_SWIPE) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            return;
        }

        View itemView = viewHolder.itemView;

        // not sure why, but this method get's called for viewholder that are already swiped away
        if (viewHolder.getAdapterPosition() == -1)
            return;

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
}
