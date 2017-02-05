package com.cuttingedge.PokeApp;

import android.graphics.drawable.Drawable;

import com.cuttingedge.adapter2recycler.ModularItem;

/**
  * Created by Robbe Sneyders on 19/08/2016.
 *
 * Pokemon item for use with ModularAdapter
 */
public class Pokemon extends ModularItem {
    public int id;
    public String name;
    public String type = "";
    public Drawable icon;
}
