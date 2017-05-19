package com.cuttingedge.PokeApp.Backpack;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cuttingedge.PokeApp.R;
import com.cuttingedge.adapter2recycler.Modules.AdapterModule;

/**
 * Created by Robbe Sneyders
 *
 * Module to handle the behaviour of the Header item
 */

class HeaderModule extends AdapterModule<HeaderModule.HeaderViewHolder, Header> {

    @Override
    public HeaderViewHolder onCreateViewHolder(ViewGroup parent) {
        View headerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_header, parent, false);
        return new HeaderViewHolder(headerView);
    }

    @Override
    public void onBindViewHolder(HeaderViewHolder viewHolder, Header header) {
        viewHolder.vText.setText(header.name);
    }

    static class HeaderViewHolder extends ViewHolder {
        TextView vText;

        HeaderViewHolder(View view) {
            super(view);
            vText = (TextView) view.findViewById(R.id.textView);
        }
    }
}
