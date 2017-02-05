package com.cuttingedge.PokeApp.Backpack;

import com.cuttingedge.adapter2recycler.ModularItem;

/**
 * Created by Robbe Sneyders
 *
 * Extendsion of ModularItem to actt as header
 */
public class Header extends ModularItem {
    String name;

    public Header(String name) {
        this.name = name;
        // set isHeader = true to make ModularAdapter recognize this as a header class.
        isHeader = true;
    }
}
