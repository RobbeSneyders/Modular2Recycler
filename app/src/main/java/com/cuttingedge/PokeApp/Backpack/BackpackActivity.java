package com.cuttingedge.PokeApp.Backpack;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import com.cuttingedge.PokeApp.BaseActivity;
import com.cuttingedge.PokeApp.Pokedex;
import com.cuttingedge.PokeApp.Pokemon;
import com.cuttingedge.PokeApp.R;
import com.cuttingedge.adapter2recycler.ModularItem;

import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration;

/**
 * Created by Robbe Sneyders
 *
 * Activity to handle backpack section in app
 */
public class BackpackActivity extends BaseActivity {

    private PokemonInlineStickerAdapter adapter;
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
        List<ModularItem> list = formDataWithHeaders(pokemonList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PokemonInlineStickerAdapter.Builder<>(recyclerView, list).build();

        new PokemonBackPackModule(this).bindToAdapter(adapter);

        StickyHeaderDecoration decor = new StickyHeaderDecoration(adapter, true);
        recyclerView.addItemDecoration(decor);
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

        List<ModularItem> newList = formDataWithHeaders(pokemonList);
        List<ModularItem> oldList = adapter.getList();
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffCallback(oldList, newList));
        adapter.swap(newList, false);
        result.dispatchUpdatesTo(adapter);
    }


    public List<ModularItem> formDataWithHeaders(List<Pokemon> list) {
        List<ModularItem> newList = new ArrayList<>();
        if (list.size() == 0)
            return newList;

        newList.add(list.get(1));
        newList.add(list.get(2));
        newList.add(list.get(3));
        newList.add(list.get(4));
        newList.add(list.get(5));
        newList.add(list.get(6));
        newList.add(list.get(7));
        newList.add(list.get(8));
        newList.add(list.get(9));
        newList.add(list.get(10));
        newList.add(list.get(11));
        newList.add(list.get(12));
        newList.add(list.get(13));
        newList.add(list.get(14));
        newList.add(list.get(15));
//        if (isAlphabetic) {
//            for (Pokemon pokemon: list) {
//                if (!pokemon.name.substring(0, 1).toUpperCase().equals(header)) {
//                    header = pokemon.name.substring(0, 1).toUpperCase();
//                    newList.add(new Header(header));
//                    newList.add(pokemon);
//                } else
//                    newList.add(pokemon);
//            }
//        }
//        else {
//            for (Pokemon pokemon: list) {
//                if (!pokemon.type.toUpperCase().equals(header)) {
//                    header = pokemon.type.toUpperCase();
//                    newList.add(new Header(header));
//                    newList.add(pokemon);
//                } else
//                    newList.add(pokemon);
//            }
//        }
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
