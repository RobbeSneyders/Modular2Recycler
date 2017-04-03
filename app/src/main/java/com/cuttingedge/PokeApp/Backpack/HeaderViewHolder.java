package com.cuttingedge.PokeApp.Backpack;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.cuttingedge.PokeApp.R;

public final class HeaderViewHolder extends RecyclerView.ViewHolder {
	TextView vText;

	HeaderViewHolder(View view) {
		super(view);
		vText = (TextView) view.findViewById(R.id.textView);
	}
}
