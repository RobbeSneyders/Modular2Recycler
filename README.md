# UndoRecycler
Adapter with integrated 
- Swipe to remove
- Undo
- Headers
- Onclicklistener
- Onlongclicklistener.


[![UndoRecycler](http://i.imgur.com/R0zn49u.gif)](http://imgur.com/R0zn49u) [![UndoRecyclerAlphabetic](http://i.imgur.com/0hifpra.gif)](http://imgur.com/0hifpra)

**Find an example of UndoAdapter in the app Swipe Shortcuts Widget:**

<a href="https://play.google.com/store/apps/details?id=com.cuttingedge.swipeshortcuts"><img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/apps/en-play-badge-border.png" width="300" /></a>

##How to use

Create a new **Adapter that extends UndoAdapter**:

```
public class MyAdapter extends UndoAdapter<MyViewHolder> {

    public MyAdapter(Context context, List<UndoItem> list, View rootView, RecyclerView recycler, String itemName, boolean withHeader) {
        super(context, list, rootView, recycler, itemName, withHeader);
    }

    @Override
    protected MyViewHolder onCreateItemViewHolder(ViewGroup parent) {
        // Inflate view used for items and return ViewHolder with this view.
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
        return new MyViewHolder(itemView, TYPE_ITEM);
    }

    @Override
    protected MyViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        // Inflate view used for headers and return ViewHolder with this view.
        View headerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_layout, parent, false);
        return new MyViewHolder(headerView, TYPE_HEADER);
    }

    @Override
    protected void onBindItemViewHolder(MyViewHolder holder, int position) {
        // Set up item data in item view
        MyObject myObject = (MyObject) list.get(position).object;
        holder.vText.setText(myObject.text);
        holder.vIcon.setImageDrawable(myObject.icon);
    }

    @Override
    protected void onBindHeaderViewHolder(MyViewHolder holder, int position) {
        // Set up header data in header view
        holder.vText.setText(list.get(position).index);
    }

    @Override
    protected void remove(Object removeItem) {
        // Delete removeItem from database.
    }

    @Override
    protected void readd(Object readdItem) {
        // Add readdItem to database.
    }

    @Override
    protected void itemClicked(RecyclerView recyclerView, int position, View v) {
        // Handle click
    }

    @Override
    protected boolean itemLongClicked(RecyclerView recyclerView, int position, View v) {
        // Handle longclick and return true
        // Do nothing and return false
        return false;
    }

    // Extend RecyclerView.Viewholder like you would in a normal adapter.
    public class MyViewHolder extends RecyclerView.ViewHolder {
        protected ImageView vIcon;
        protected TextView vText;

        public MyViewHolder(View view, int type) {
            super(view);
            if (type == TYPE_ITEM) {
                vIcon = (ImageView) view.findViewById(R.id.iconImageView);
                vText = (TextView) view.findViewById(R.id.appNameTextView);
            }
            else if (type == TYPE_HEADER) {
                vText = (TextView) view.findViewById(R.id.separator);
            }
        }
    }
}
```

**Set up the RecyclerView in your activity** like this:

```
private void fillList() {
        recycler.setHasFixedSize(false);

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
            
            list.add(i, undoItem);
        }

        shortcutsRecycler.setLayoutManager(new LinearLayoutManager(this));

        final MyAdapter adapter = new MyAdapter(this, list , rootView, recycler, "Item", true);
        shortcutsRecycler.setAdapter(adapter);
    } 
```

###Disable functions of adapter:

- **Swipe to remove** and **undo** can be disabled by **using other constructor**.
- **Headers** can be disabled by **boolean in constructor**.
- **Click and long click** can be disabled by **not implementing itemClicked and itemLongClicked** method.

