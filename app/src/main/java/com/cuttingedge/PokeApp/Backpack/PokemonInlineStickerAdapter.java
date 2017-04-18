package com.cuttingedge.PokeApp.Backpack;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import com.cuttingedge.PokeApp.Pokemon;
import com.cuttingedge.PokeApp.R;
import com.cuttingedge.adapter2recycler.Adapter.ModularAdapter;
import com.cuttingedge.adapter2recycler.Adapter.ModularAdapterBuilder;
import com.cuttingedge.adapter2recycler.ModularItem;

import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderAdapter;
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration;

public final class PokemonInlineStickerAdapter<VH extends RecyclerView.ViewHolder, I extends ModularItem> extends ModularAdapter<VH, I> implements
		StickyHeaderAdapter<HeaderViewHolder> {

	public static class Builder<I extends ModularItem> extends ModularAdapterBuilder<I> {
		public Builder(RecyclerView recyclerView, List<I> list) {
			super(recyclerView, list);
		}

		@Override
		public <VH extends RecyclerView.ViewHolder> PokemonInlineStickerAdapter<VH, I> build() {
			return new PokemonInlineStickerAdapter<>(this);
		}
	}

	public PokemonInlineStickerAdapter(Builder<I> builder) {
		super(builder);
	}

	public PokemonInlineStickerAdapter(ModularAdapterBuilder<I> builder) {
		super(builder);
	}

	// header id for the item at the given position.
	// 1 sticky header <-> 1 item
	@Override
	public long getHeaderId(int position) {
		if (position == 3)
			return StickyHeaderDecoration.NO_HEADER_ID;
		return position;
	}

	@Override
	public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
		View headerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_header, parent, false);
		return new HeaderViewHolder(headerView);
	}

	@Override
	public void onBindHeaderViewHolder(HeaderViewHolder viewHolder, int position) {
		Pokemon item = (Pokemon) this.getList().get(position);
		viewHolder.vText.setText(item.name.substring(0, 1) + position);
	}
}
