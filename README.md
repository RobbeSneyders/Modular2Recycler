# UndoRecycler
Adapter with integrated 
- Swipe to remove/archive/any action
- Undo
- Headers
- Onclicklistener
- Onlongclicklistener.


[![UndoRecycler](http://i.imgur.com/jFQTroq.gif)](http://imgur.com/jFQTroq) [![UndoRecyclerAlphabetic](http://i.imgur.com/5bgXPR2.gif)](http://imgur.com/5bgXPR2)

##How to use

Create a new **Adapter that extends UndoAdapter**:

This is an example with pseudocode.
For a working example, check the app module of the repository.

```
public class MyAdapter extends MyAdapter<MyAdapter.MyViewHolder> {

    /**
     * @param context     Context
     * @param list        List of UndoItems. This list should be sorted alphabetically.
     * @param recycler    Recycler for which this adapter is used
     * @param rootView    Rootview to attach snackbar to
     * @param withHeaders Use headers if true
     */
    public MyAdapter(Context context, List<UndoItem> list, RecyclerView recycler, View rootView, boolean withHeaders) {
        super(context, list, recycler, withHeaders);
        // Set swipe left to delete
        setSwipeLeft(Color.RED, context.getResources().getDrawable(R.drawable.ic_delete_white_24dp), rootView);
        // set swipe left to archive
        setSwipeRight(context.getResources().getColor(R.color.green), context.getResources().getDrawable(R.drawable.ic_archive), rootView);
    }

    @Override
    protected MyViewHolder onCreateItemViewHolder(ViewGroup parent) {
        // Inflate view used for items and return ViewHolder with this view.
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row, parent, false);
        return new MyViewHolder(itemView, TYPE_ITEM);
    }

    @Override
    protected MyViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        // Inflate view used for headers and return ViewHolder with this view.
        View headerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_header, parent, false);
        return new MyViewHolder(headerView, TYPE_HEADER);
    }

    @Override
    protected void onBindItemViewHolder(MyViewHolder holder, int position) {
        // Set up item data in item view
        MyObject myObject = (MyObject) list.get(position).object;
        holder.vText.setText(myObject.name);
        holder.vIcon.setImageDrawable(myObject.icon);
    }

    @Override
    protected void onBindHeaderViewHolder(MyViewHolder holder, int position) {
        // Set up header data in header view
        holder.vText.setText(list.get(position).header);
    }

    @Override
    protected String swipedLeft(Object swipedItem) {
        // swipe left is delete -> remove from database
        // undo reference to swipedItem is kept by super class
        Database.remove((MyObject) swipedItem);
        // Return the message to be displayed in the toast
        return ((MyObject) swipedItem).name + " was deleted";
    }

    @Override
    protected String swipedRight(Object swipedItem) {
        // swipe left is archive -> move myObject to archive
        // undo reference to swipedItem is kept by super class
        Database.archive((MyObject) swipedItem);
        // Return the message to be displayed in the toast
        return ((MyObject) swipedItem).name + " was archived";
    }

    @Override
    protected void undoLeft(Object restoreItem) {
        // Undo delete -> add restoreItem back to database
        Database.add((MyObject) restoreItem);
    }

    @Override
    protected void undoRight(Object restoreItem) {
        // Undo archive -> move restore item back from archive.
        Database.getFromArchive((MyObject) restoreItem);
    }

    @Override
    protected void itemClicked(RecyclerView recyclerView, int position, View v) {
        // Handle item click
    }

    @Override
    protected boolean itemLongClicked(RecyclerView recyclerView, int position, View v) {
        // Handle item long click
        // return true if click is consumed, otherwise false
        return true;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView vIcon;
        public TextView vText;

        public MyViewHolder(View view, int type) {
            super(view);
            if (type == TYPE_ITEM) {
                vIcon = (ImageView) view.findViewById(R.id.imageView);
            }
            vText = (TextView) view.findViewById(R.id.textView);
        }
    }
}
```

**Set up the RecyclerView in your activity** like this:

```
private void fillList() {

        // The objects used in your database (MyObject in this example), should be wrapped in UndoItem before giving the list of 
        // objects to the adapter.
        // UndoItem undoItem = new UndoItem(String header, Object MyObject).
        // header is the header this item should belong to.
        
        List<MyObject> myObjectList = Database.getAllMyObjects(); // Should be sorted alphabetically on name if items should be sorted alphabetically under header (check gif)
        List<UndoItem> list = new ArrayList<>();
        
        for (int i = 0; i < myObjectList.size(); i++) {
            UndoItem undoItem = new UndoItem(myObjectList.get(i).type, myObjectList.get(i));
            
            // If you want the alphabet as headers (see 2nd gif), use:
            // UndoItem undoItem = new UndoItem(myObjectList.get(i).name.substring(0,1), myObjectList.get(i));
            // This sets the first character of the myObject's name as header
            
            list.add(i, undoItem);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final MyAdapter adapter = new MyAdapter(this, list , rootView, recyclerView, true);
        recyclerView.setAdapter(adapter);
    } 
```

###Enable/Disable functions of adapter:

- **Swipe to remove** and **undo** can be enabled by calling setSwipeLeft(...) or/and setSwipeRight(...)
- **Headers** can be disabled by **boolean in constructor**.
- **Click and long click** can be disabled by **not implementing itemClicked and itemLongClicked** method.



**Find another example of UndoAdapter in the app Swipe Shortcuts Widget:**

<a href="https://play.google.com/store/apps/details?id=com.cuttingedge.swipeshortcuts"><img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/apps/en-play-badge-border.png" width="300" /></a>

[![UndoRecycler](http://i.imgur.com/R0zn49u.gif)](http://imgur.com/R0zn49u) [![UndoRecyclerAlphabetic](http://i.imgur.com/0hifpra.gif)](http://imgur.com/0hifpra)
