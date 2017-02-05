package com.cuttingedge.PokeApp.Backpack;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;

import com.cuttingedge.PokeApp.BaseActivity;
import com.cuttingedge.PokeApp.Pokedex;
import com.cuttingedge.PokeApp.Pokemon;
import com.cuttingedge.PokeApp.R;
import com.cuttingedge.adapter2recycler.Adapter.ModularAdapter;
import com.cuttingedge.adapter2recycler.Adapter.ModularAdapterBuilder;
import com.cuttingedge.adapter2recycler.ModularItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robbe Sneyders
 *
 * Activity to handle backpack section in app
 */
public class BackpackActivity extends BaseActivity {

    private ModularAdapter<ViewHolder, ModularItem> adapter;
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


    /**
     * Sets up the RecyclerView and Adapter
     */
    private void setupList() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);

        List<Pokemon> pokemonList = Pokedex.getAllPokemonAlphabetic();
        isAlphabetic = true;
        List<ModularItem> list = addHeaders(pokemonList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ModularAdapterBuilder<>(recyclerView, list)
                .setSwipeLeft(Color.RED, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_delete_white_24dp, null))
                .setSwipeRight(Color.GREEN, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_cloud_upload_white_24dp, null))
                .build();

        new PokemonBackPackModule(this, adapter);
        new HeaderModule(adapter);
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


    /**
     * Adds alphabetic or type headers to a list of pokemon.
     *
     * @param list List of pokemon to which headers should be added.
     * @return List with headers inserted.
     */
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


    /**
     * Reset pokedex and get new list
     */
    @Override
    protected void reset() {
        Pokedex.initiate(this);
        isAlphabetic = !isAlphabetic;
        toggleAlphabetic();
    }
}
