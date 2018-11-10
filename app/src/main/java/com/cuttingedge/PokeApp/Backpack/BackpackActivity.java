package com.cuttingedge.PokeApp.Backpack;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.cuttingedge.PokeApp.BaseActivity;
import com.cuttingedge.PokeApp.Pokedex;
import com.cuttingedge.PokeApp.Pokemon;
import com.cuttingedge.PokeApp.R;
import com.cuttingedge.adapter2recycler.Adapter.ItemTouchHelperCallbackExample;
import com.cuttingedge.adapter2recycler.Adapter.ModularAdapter;
import com.cuttingedge.adapter2recycler.Adapter.ModularAdapterBuilder;
import com.cuttingedge.adapter2recycler.ModularItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

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

        ItemTouchHelperCallbackExample touchHelperCallback = new ItemTouchHelperCallbackExample(recyclerView);
        touchHelperCallback.setSwipeLeft(Color.RED, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_delete_white_24dp, null));
        touchHelperCallback.setSwipeRight(Color.GREEN, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_cloud_download_white_24dp, null));

        adapter = new ModularAdapterBuilder<>(recyclerView, list, touchHelperCallback).build();
        touchHelperCallback.setAdapter(adapter);

        new PokemonBackPackModule(this).bindToAdapter(adapter);
        new HeaderModule().bindToAdapter(adapter);
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

        List<ModularItem> newList = addHeaders(pokemonList);
        List<ModularItem> oldList = adapter.getList();
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffCallback(oldList, newList));
        adapter.swap(newList, false);
        result.dispatchUpdatesTo(adapter);
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