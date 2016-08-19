package com.cuttingedge.PokeApp;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.cuttingedge.undorecycler.UndoAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity that sets up the RecyclerView.
 *
 * Created by Robbe Sneyders on 19/08/2016.
 */
public class BackpackActivity extends BaseActivity {

    private PokemonAdapter adapter;
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

        // The Pokemon class should be wrapped in UndoItem before giving the list of
        // objects to the adapter.
        // UndoItem undoItem = new UndoItem(String header, Object MyObject).
        // header is the header this item should belong to.

        Pokedex.setup(this);
        List<Pokemon> pokemonList = Pokedex.getAllPokemonAlphabetic();
        List<UndoAdapter.UndoItem> list = new ArrayList<>();

        for (int i = 0; i < pokemonList.size(); i++) {
            UndoAdapter.UndoItem undoItem = new UndoAdapter.UndoItem(pokemonList.get(i).type, pokemonList.get(i));
            list.add(i, undoItem);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        View rootView = findViewById(R.id.coordinator);
        adapter = new PokemonAdapter(this, list , recyclerView, rootView, true);
        recyclerView.setAdapter(adapter);
        isAlphabetic = false;
    }

    /**
     * Toggles headers between type and alphabetic
     */
    private void toggleAlphabetic() {
        // List is ordered alphabetically within each header
        List<Pokemon> pokemonList = Pokedex.getAllPokemonAlphabetic();
        List<UndoAdapter.UndoItem> list = new ArrayList<>();

        if (isAlphabetic) {
            for (int i = 0; i < pokemonList.size(); i++) {
                UndoAdapter.UndoItem undoItem = new UndoAdapter.UndoItem(pokemonList.get(i).type, pokemonList.get(i));
                list.add(i, undoItem);
            }
            isAlphabetic = false;
        } else {
            for (int i = 0; i < pokemonList.size(); i++) {
                // Set first character of name as header
                UndoAdapter.UndoItem undoItem = new UndoAdapter.UndoItem(pokemonList.get(i).name.substring(0, 1), pokemonList.get(i));
                list.add(i, undoItem);
            }
            isAlphabetic = true;
        }
        // swaps the data of the adapter
        adapter.swap(list);
    }

    // Reset database and get new list
    @Override
    protected void reset() {
        Pokedex.initiate(this);
        isAlphabetic = !isAlphabetic;
        toggleAlphabetic();
    }
}
