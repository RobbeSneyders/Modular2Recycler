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
public class BillsPCActivity extends BaseActivity {

    BillsPCAdapter adapter;

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
        List<UndoAdapter.UndoItem> list = new ArrayList<>();

        for (int i = 0; i < pokemonList.size(); i++) {
            // no headers used in this adapter
            UndoAdapter.UndoItem undoItem = new UndoAdapter.UndoItem("", pokemonList.get(i));
            list.add(i, undoItem);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        View rootView = findViewById(R.id.coordinator);
        // no headers used in this adapter
        adapter = new BillsPCAdapter(this, list , recyclerView, rootView, false);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Reset database and get new list.
     */
    @Override
    protected void reset() {
        Pokedex.initiate(this);

        List<Pokemon> pokemonList = Pokedex.getBillsPCID();
        List<UndoAdapter.UndoItem> list = new ArrayList<>();

        for (int i = 0; i < pokemonList.size(); i++) {
            UndoAdapter.UndoItem undoItem = new UndoAdapter.UndoItem("", pokemonList.get(i));
            list.add(i, undoItem);
        }
        // swaps the data of the adapter
        adapter.swap(list);
    }
}
