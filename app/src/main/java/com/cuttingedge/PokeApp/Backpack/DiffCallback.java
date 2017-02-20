package com.cuttingedge.PokeApp.Backpack;

import android.support.v7.util.DiffUtil;

import com.cuttingedge.adapter2recycler.ModularItem;

import java.util.List;

/**
 * Created by Robbe Sneyders.
 *
 * Class that calculates the difference between two lists.
 * Demonstrate the use of DiffUtil
 */
public class DiffCallback extends DiffUtil.Callback {
    private List<ModularItem> mOldList;
    private List<ModularItem> mNewList;

    public DiffCallback(List<ModularItem> oldList, List<ModularItem> newList) {
        this.mOldList = oldList;
        this.mNewList = newList;
    }

    @Override
    public int getOldListSize() {
        return mOldList != null ? mOldList.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return mNewList != null ? mNewList.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mNewList.get(newItemPosition) == mOldList.get(oldItemPosition);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return mNewList.get(newItemPosition).equals(mOldList.get(oldItemPosition));
    }
}
