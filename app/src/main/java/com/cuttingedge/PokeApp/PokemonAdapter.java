package com.cuttingedge.PokeApp;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cuttingedge.undorecycler.UndoAdapter;

import java.util.List;

/**
 * Adapter that extends UndoAdapter.
 * All your adapter logic should come in here.
 *
 * Created by Robbe Sneyders on 19/08/2016.
 */
public class PokemonAdapter extends UndoAdapter<PokemonAdapter.PokemonViewHolder> {

    Toast toast;

    /**
     * @param context     Context
     * @param list        List of UndoItems. This list should be sorted alphabetically.
     * @param recycler    Recycler for which this adapter is used
     * @param rootView    Rootview to attach snackbar to
     * @param withHeaders Use headers if true
     */
    public PokemonAdapter(Context context, List<UndoItem> list, RecyclerView recycler, View rootView, boolean withHeaders) {
        super(context, list, recycler, withHeaders);
        // Set swipe left to delete
        setSwipeLeft(Color.RED, context.getResources().getDrawable(R.drawable.ic_delete_white_24dp), rootView);
        // set swipe left to archive
        setSwipeRight(context.getResources().getColor(R.color.green), context.getResources().getDrawable(R.drawable.ic_cloud_upload_white_24dp), rootView);
    }

    @Override
    protected PokemonViewHolder onCreateItemViewHolder(ViewGroup parent) {
        // Inflate view used for items and return ViewHolder with this view.
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row, parent, false);
        return new PokemonViewHolder(itemView, TYPE_ITEM);
    }

    @Override
    protected PokemonViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        // Inflate view used for headers and return ViewHolder with this view.
        View headerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_header, parent, false);
        return new PokemonViewHolder(headerView, TYPE_HEADER);
    }

    @Override
    protected void onBindItemViewHolder(PokemonViewHolder holder, int position) {
        // Set up item data in item view
        Pokemon pokemon = (Pokemon) list.get(position).object;
        holder.vText.setText(pokemon.name);
        holder.vIcon.setImageDrawable(pokemon.icon);
    }

    @Override
    protected void onBindHeaderViewHolder(PokemonViewHolder holder, int position) {
        // Set up header data in header view
        holder.vText.setText(list.get(position).header);
    }

    @Override
    protected String swipedLeft(Object swipedItem) {
        // swipe left is delete -> remove pokemon from database
        // undo reference to pokemon is kept by super class
        Pokedex.removePokemon((Pokemon) swipedItem);
        // Return the message to be displayed in the toast
        return ((Pokemon) swipedItem).name + " was set free";
    }

    @Override
    protected String swipedRight(Object swipedItem) {
        // swipe left is send to Bill -> move pokemon to billsPC database
        // undo reference to pokemon is kept by super class
        Pokedex.sendToBill((Pokemon) swipedItem);
        // Return the message to be displayed in the toast
        return ((Pokemon) swipedItem).name + " was uploaded to Bill's PC";
    }

    @Override
    protected void undoLeft(Object restoreItem) {
        // Undo delete -> add pokemon back to database
        Pokedex.addToPokedex((Pokemon) restoreItem);
    }

    @Override
    protected void undoRight(Object restoreItem) {
        // Undo send to Bill -> move pokemon back from billsPC database;
        Pokedex.getFromBill((Pokemon) restoreItem);
    }

    @Override
    protected void itemClicked(RecyclerView recyclerView, int position, View v) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, "Clicked " + ((Pokemon) list.get(position).object).name + "!", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected boolean itemLongClicked(RecyclerView recyclerView, int position, View v) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, "Long clicked " + ((Pokemon) list.get(position).object).name + "!", Toast.LENGTH_SHORT);
        toast.show();
        return true;
    }

    public class PokemonViewHolder extends RecyclerView.ViewHolder {
        public ImageView vIcon;
        public TextView vText;

        public PokemonViewHolder(View view, int type) {
            super(view);
            if (type == TYPE_ITEM) {
                vIcon = (ImageView) view.findViewById(R.id.imageView);
            }
            vText = (TextView) view.findViewById(R.id.textView);
        }
    }
}
