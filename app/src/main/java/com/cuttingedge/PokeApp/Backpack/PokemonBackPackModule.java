package com.cuttingedge.PokeApp.Backpack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cuttingedge.PokeApp.Pokedex;
import com.cuttingedge.PokeApp.Pokemon;
import com.cuttingedge.PokeApp.PokemonModule;
import com.cuttingedge.PokeApp.R;
import com.cuttingedge.adapter2recycler.Modules.ItemClickPlugin;
import com.cuttingedge.adapter2recycler.Modules.ItemLongClickPlugin;

import java.util.List;

import androidx.recyclerview.widget.ItemTouchHelper;

/**
 * Created by Robbe Sneyders
 *
 * Module to handle the behaviour of the Pokemon item in the backpack context
 */
class PokemonBackPackModule extends PokemonModule
        implements ItemClickPlugin<Pokemon>, ItemLongClickPlugin<Pokemon> {

    private Context context;


    /*************************
     * AdapterModule methods *
     *************************/

    PokemonBackPackModule(Context context) {
        this.context = context;
    }

    @Override
    public PokemonViewHolder onCreateViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row, parent, false);
        return new PokemonViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PokemonViewHolder viewHolder, Pokemon pokemon) {
        viewHolder.vText.setText(pokemon.name);
        viewHolder.vIcon.setImageDrawable(pokemon.icon);
    }


    /*************************************
     * ItemClickPlugin methods *
     *************************************/

    @Override
    public void onItemClicked(Pokemon pokemon) {
        ((BackpackActivity) context).toast("Clicked " + pokemon.name + "!", Toast.LENGTH_SHORT);
    }


    /*****************************************
     * ItemLongClickPlugin methods *
     *****************************************/

    @Override
    public boolean onItemLongClicked(Pokemon pokemon) {
        ((BackpackActivity) context).toast("Long clicked " + pokemon.name + "!", Toast.LENGTH_SHORT);
        return true;
    }


    /*********************************
     * SwipePlugin methods *
     *********************************/

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

    @Override
    public int getDragDirs() {
        return ItemTouchHelper.UP | ItemTouchHelper.DOWN;
    }

    @Override
    public void onMoved(Pokemon movedPokemon, List adapterList) {
        // Do nothing
        // The way Pokedex class works should be changed to be able to handle moved pokemon
    }
}
