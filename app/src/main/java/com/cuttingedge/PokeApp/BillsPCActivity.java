package com.cuttingedge.PokeApp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.cuttingedge.undorecycler.ModularAdapter;
import com.cuttingedge.undorecycler.ModularItem;

import java.util.List;

/**
 * Activity that sets up the RecyclerView.
 *
 * Created by Robbe Sneyders on 19/08/2016.
 */
public class BillsPCActivity extends BaseActivity implements SwipeCallBack {

    ModularAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        setupList();
    }

    private void setupList() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);

        // The Pokemon class should be wrapped in UndoItem before giving the list of
        // objects to the adapter.
        // UndoItem undoItem = new UndoItem(String header, Object MyObject).
        // header is the header this item should belong to.

        // List is ordered by pokemon id
        List<Pokemon> pokemonList = Pokedex.getBillsPCID();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ModularAdapter.ModularAdapterBuilder(recyclerView, pokemonList)
                .setSwipeableLeft(Color.RED, getResources().getDrawable(R.drawable.ic_delete_white_24dp))
                .setSwipeableRight(Color.GREEN, getResources().getDrawable(R.drawable.ic_cloud_upload_white_24dp))
                .build();

        new PokemonDelegate(this, adapter);
        new HeaderDelegate(adapter);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Reset database and get new list.
     */
    @Override
    protected void reset() {
        Pokedex.initiate(this);

        List<? extends ModularItem> pokemonList = Pokedex.getBillsPCID();
        // swaps the data of the adapter
        adapter.swap(pokemonList);
    }




    @Override
    public String onSwiped(Pokemon pokemon, int swipeDir) {
        if (swipeDir == ItemTouchHelper.LEFT) {
            Pokedex.removePokemon(pokemon);
            return pokemon.name + " was set free";
        }
        else if (swipeDir == ItemTouchHelper.RIGHT) {
            Pokedex.sendToBill(pokemon);
            return pokemon.name + " was uploaded to Bill's PC";
        }
        return null;
    }

    @Override
    public void onUndo(Pokemon pokemon, int swipeDir) {
        if (swipeDir == ItemTouchHelper.LEFT) {
            Pokedex.addToPokedex(pokemon);
        }
        else if (swipeDir == ItemTouchHelper.RIGHT) {
            Pokedex.getFromBill(pokemon);
        }
    }
}
