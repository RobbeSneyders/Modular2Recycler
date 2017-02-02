package com.cuttingedge.PokeApp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.cuttingedge.undorecycler.ModularAdapter;
import com.cuttingedge.undorecycler.ModularAdapter.ModularAdapterBuilder;
import com.cuttingedge.undorecycler.ModularItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity that sets up the RecyclerView.
 *
 * Created by Robbe Sneyders on 19/08/2016.
 */
public class BackpackActivity extends BaseActivity implements SwipeCallBack{

    private ModularAdapter adapter;
    private boolean isAlphabetic;

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar.setTitle(getString(R.string.backpack));

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleAlphabetic();
            }
        });

        setupList();
    }

    private void setupList() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);

        Pokedex.setup(this);
        List<Pokemon> pokemonList = Pokedex.getAllPokemonAlphabetic();
        isAlphabetic = true;
        List<ModularItem> list = addHeaders(pokemonList);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ModularAdapterBuilder(recyclerView, list)
                .setSwipeableLeft(Color.RED, getResources().getDrawable(R.drawable.ic_delete_white_24dp))
                .setSwipeableRight(Color.GREEN, getResources().getDrawable(R.drawable.ic_cloud_upload_white_24dp))
                .build();

        new PokemonDelegate(this, adapter);
        new HeaderDelegate(adapter);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Toggles headers between type and alphabetic
     */
    private void toggleAlphabetic() {
        List<Pokemon> pokemonList;

        if (isAlphabetic) {
            pokemonList = Pokedex.getAllPokemonByType();
            isAlphabetic = false;
        }
        else {
            pokemonList = Pokedex.getAllPokemonAlphabetic();
            isAlphabetic = true;
        }

        List<ModularItem> list = addHeaders(pokemonList);
        // swaps the data of the adapter
        adapter.swap(list);
    }

    public List<ModularItem> addHeaders(List<Pokemon> list) {
        List<ModularItem> newList = new ArrayList<>();
        if (list.size() == 0)
            return newList;

        String header = "";
        if (isAlphabetic) {
            for (Pokemon pokemon: list) {
                if (!pokemon.name.substring(0, 1).toUpperCase().equals(header)) {
                    header = pokemon.name.substring(0, 1).toUpperCase();
                    newList.add(new Header(header));
                    newList.add(pokemon);
                } else
                    newList.add(pokemon);
            }
        }
        else {
            for (Pokemon pokemon: list) {
                if (!pokemon.type.toUpperCase().equals(header)) {
                    header = pokemon.type.toUpperCase();
                    newList.add(new Header(header));
                    newList.add(pokemon);
                } else
                    newList.add(pokemon);
            }
        }
        return newList;
    }

    // Reset database and get new list
    @Override
    protected void reset() {
        Pokedex.initiate(this);
        isAlphabetic = !isAlphabetic;
        toggleAlphabetic();
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
