package com.cuttingedge.undorecycler;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Robbe Sneyders on 13/08/2016.
 *
 * Base class for an adapter with integrated headers and undo function.
 * @param <VH> Subclass of ViewHolder.
 */
public abstract class UndoAdapter<VH extends ViewHolder> extends Adapter<VH> {

    protected static final int TYPE_HEADER = 0;
    protected static final int TYPE_ITEM = 1;

    // List keeps track of all the items shown.
    // Sub classes should find items by calling list.get(position).
    protected List<UndoItem> list;

    private UndoItem pendingRemovalItem;
    private UndoItem pendingRemovalHeader;
    private int pendingRemovalPosition;

    private View rootView;
    private RecyclerView recycler;

    private Context context;

    //Used for animating header without red background when last shortcut of type gets deleted
    private int headerHeight;
    private int itemHeight;

    //Red animation behind item should only run on delete, not on undo.
    private boolean lastWasUndo;

    /**
     * Constructor
     *
     * @param list List of UndoItems. This list should be sorted alphabetically.
     * @param recycler Recycler for which this adapter is used
     * @param withHeaders Use headers if true
     */
    public UndoAdapter(Context context, List<UndoItem> list, RecyclerView recycler, boolean withHeaders) {
        this.context = context;
        this.list = list;
        this.recycler = recycler;

        if (withHeaders)
            insertHeaders();

        ItemClickSupport.addTo(recycler).setOnItemClickListener(clickListener);
        ItemClickSupport.addTo(recycler).setOnItemLongClickListener(longClickListener);
    }

    /**
     * Sort list on header field and insert header items.
     */
    private void insertHeaders() {
        Collections.sort(list, new Comparator<UndoItem>(){
            public int compare(UndoItem uI1, UndoItem uI2) {
                return uI1.header.compareToIgnoreCase(uI2.header);
            }
        });
        if (list.size() >= 1) {
            list.add(0, new UndoItem(TYPE_HEADER, list.get(0).header));
            for (int i = 2; i < list.size(); i++) {
                if (!list.get(i).header.equals(list.get(i - 1).header)) {
                    list.add(i, new UndoItem(TYPE_HEADER, list.get(i).header));
                }
            }
        }
    }

    /**
     * Splits onCreateViewHolder method into two separate methods for items and headers.
     */
    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            return onCreateItemViewHolder(parent);
        }
        else if (viewType == TYPE_HEADER) {
            return onCreateHeaderViewHolder(parent);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType);
    }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} to represent
     * an item.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.     *
     * @return A new ViewHolder that holds a View of the given view type.
     */
    protected abstract VH onCreateItemViewHolder(ViewGroup parent);

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} to represent
     * a header.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.     *
     * @return A new ViewHolder that holds a View of the given view type.
     */
    protected abstract VH onCreateHeaderViewHolder(ViewGroup parent);

    /**
     * Splits onBindViewHolder method into two separate methods for items and headers.
     */
    @Override
    public void onBindViewHolder(VH holder, int position) {
        if (list.get(position).type == TYPE_ITEM) {
            onBindItemViewHolder(holder, position);
        }
        else if (list.get(position).type == TYPE_HEADER) {
            onBindHeaderViewHolder(holder, position);
        }
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method
     * should update the contents of the {@link ViewHolder#itemView} to reflect the item at
     * the given position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *               item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    protected abstract void onBindItemViewHolder(VH holder, int position);

    /**
     * Called by RecyclerView to display the data at the specified position. This method
     * should update the contents of the {@link ViewHolder#itemView} to reflect the header at
     * the given position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *               item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    protected abstract void onBindHeaderViewHolder(VH holder, int position);

    /**
     * Returns the item count in this adapter (items + headers).
     *
     * @return item count.
     */
    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * Returns type (item or header) of item at specified position.
     *
     * @param position position of item.
     * @return type of item.
     */
    @Override
    public int getItemViewType(int position) {
        return list.get(position).type;
    }

    /**
     * Items used in this adapter should be of this type.
     *
     * type is either TYPE_ITEM or TYPE_HEADER
     * header is the header the item should belong to
     */
    public static class UndoItem {
        public int type;
        public String header;
        public Object object;

        // Used for header creation
        // Should not be used outside UndoAdapter.
        public UndoItem(int type, String header) {
            this.type = type;
            this.header = header;
        }

        // Used for item creation
        public UndoItem(String header, Object object) {
            this.type = TYPE_ITEM;
            this.header = header;
            this.object = object;
        }
    }

    /**
     * Called when item status should be pending removal.
     * The item gets cached in the adapter and removed from the database.
     *
     * @param position Position of item to be removed.
     */
    public void pendingRemoval(int position, int swipeDir) {
        lastWasUndo = false;
        pendingRemovalItem = list.get(position);
        pendingRemovalPosition = position;

        if (position != 0 && getItemViewType(position - 1) == TYPE_HEADER &&
                (position == list.size() - 1 || getItemViewType(position + 1) == TYPE_HEADER)) {
            pendingRemovalHeader = list.get(position - 1);
            list.remove(position);
            list.remove(position - 1);
            notifyItemRangeRemoved(position -1, 2);
        } else {
            pendingRemovalHeader = null;
            list.remove(position);
            notifyItemRemoved(position);
        }

        if (swipeDir == ItemTouchHelper.LEFT) {
            swipedLeft(pendingRemovalItem.object);
            showSnackbar(leftMessage);
        }
        else if (swipeDir == ItemTouchHelper.RIGHT){
            swipedRight(pendingRemovalItem.object);
            showSnackbar(rightMessage);
        }
    }

    /**
     * Show snackbar with undo button.
     */
    private void showSnackbar(String message) {
        final Snackbar snackBar = Snackbar.make(rootView,
                message, Snackbar.LENGTH_LONG);

        snackBar.setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undo();
                snackBar.dismiss();
            }
        });
        snackBar.show();
    }

    /**
     * Called when user presses undo.
     * Call readd() method to readd the item to the database.
     * Add the item back to the itemList on it's old location and notify for changes.
     */
    private void undo() {
        readd(pendingRemovalItem.object);
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
    }

    /**
     * Called when an item is swiped left.
     *
     * @param swipedItem Item that was swiped.
     */
    protected abstract void swipedLeft(Object swipedItem);


    /**
     * Called when an item is swiped Right.
     *
     * @param swipedItem Item that was swiped.
     */
    protected abstract void swipedRight(Object swipedItem);


    /**
     * Called after undo, when an item needs to be readded to the database.
     *
     * @param readdItem Item to readd to database.
     */
    protected abstract void readd(Object readdItem);

    /**
     * Sets up the ItemTouchHelper and attaches it to the RecyclerView.
     */
    private void setUpItemTouchHelper() {
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(recycler);
        setUpAnimationDecoratorHelper(recycler);
    }

    private Drawable leftBackground;
    private Drawable rightBackground;
    private Drawable background;
    private Drawable leftMark;
    private Drawable rightMark;
    private String leftMessage;
    private String rightMessage;
    private boolean initialized;

    /**
     * Make items swipeable to the left.
     *
     * @param color Color used for background behind swiped item.
     * @param icon Icon shown behind swiped item.
     * @param rootView Root view used for the creation of a snackbar.
     * @param message Message shown in snackbar when item gets swiped.
     */
    protected void setSwipeLeft(int color, Drawable icon, View rootView, String message) {
        if (!initialized)
            setUpItemTouchHelper();
        initialized = true;

        if (leftBackground != null) {
            simpleItemTouchCallback.setDefaultSwipeDirs(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        } else {
            simpleItemTouchCallback.setDefaultSwipeDirs(ItemTouchHelper.LEFT);
        }

        rightBackground = new ColorDrawable(color);
        rightMark = icon;
        leftMessage = message;
        this.rootView = rootView;
    }

    /**
     * Make items swipeable to the right.
     *
     * @param color Color used for background behind swiped item.
     * @param icon Icon shown behind swiped item.
     * @param rootView Root view used for the creation of a snackbar.
     * @param message Message shown in snackbar when item gets swiped.
     */
    public void setSwipeRight(int color, Drawable icon, View rootView, String message) {
        if (!initialized)
            setUpItemTouchHelper();
        initialized = true;

        if (rightBackground != null) {
            simpleItemTouchCallback.setDefaultSwipeDirs(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        } else {
            simpleItemTouchCallback.setDefaultSwipeDirs(ItemTouchHelper.RIGHT);
        }

        leftBackground = new ColorDrawable(color);
        leftMark = icon;
        rightMessage = message;
        this.rootView = rootView;
    }

    /**
     * Callback used in ItemTouchHelper to draw a background behind the swiped item.
     */
    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, 0) {

        // we want to cache these and not allocate anything repeatedly in the onChildDraw method
        int xMarkMargin;
        boolean initiated;

        private void init() {
            xMarkMargin = (int) context.getResources().getDimension(R.dimen.ic_clear_margin);
            initiated = true;
        }

        // not important, we don't want drag & drop
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }


        @Override
        public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (recyclerView.getAdapter().getItemViewType(viewHolder.getAdapterPosition()) == TYPE_HEADER)
                return 0;

            return super.getSwipeDirs(recyclerView, viewHolder);
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

    /**
     * OnItemClickListener defined in ItemClickSupport
     */
    ItemClickSupport.OnItemClickListener clickListener = new ItemClickSupport.OnItemClickListener() {
        @Override
        public void onItemClicked(RecyclerView recyclerView, int position, View v) {
            if (getItemViewType(position) == TYPE_ITEM)
                itemClicked(recyclerView, position, v);
        }
    };

    /**
     * OnItemLongClickListener defined in ItemClickSupport
     */
    ItemClickSupport.OnItemLongClickListener longClickListener = new ItemClickSupport.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
            return itemLongClicked(recyclerView, position, v);
        }
    };

    /**
     * Called when an item is clicked.
     *
     * @param recyclerView Parent of clicked view.
     * @param position Position of clicked view in recyclerView.
     * @param v clicked View.
     */
    protected abstract void itemClicked(RecyclerView recyclerView, int position, View v);

    /**
     * Called when an item is long clicked.
     *
     * @param recyclerView Parent of long clicked view.
     * @param position Position of long clicked view in recyclerView.
     * @param v long clicked View.
     */
    protected abstract boolean itemLongClicked(RecyclerView recyclerView, int position, View v);
}
