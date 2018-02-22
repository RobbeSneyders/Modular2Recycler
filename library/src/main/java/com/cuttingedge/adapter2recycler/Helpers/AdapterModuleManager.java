package com.cuttingedge.adapter2recycler.Helpers;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;

import com.cuttingedge.adapter2recycler.ModularItem;
import com.cuttingedge.adapter2recycler.Modules.AdapterModule;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Robbe Sneyders
 * <p>
 * Manager for different AdapterModules
 */
public class AdapterModuleManager<M extends AdapterModule<VH, I>, VH extends ViewHolder, I extends ModularItem> {


	private static final String TAG = "AdapterModuleManager";
	/**
	 * TreeMap connecting ViewTypes with AdapterModules
	 */
	private TreeMap<Integer, M> types = new TreeMap<>();


	/**
	 * Add new AdapterModule to TreeMap with newly generated ViewType.
	 *
	 * @param module
	 * 		AdapterModule to be added.
	 */
	public void addAdapterModule(M module) {
		if (!types.containsValue(module)) {
			Class<I> newItemClass = module.getItemClass();
			int viewType = getViewType(newItemClass);
			if (viewType == -1) {
				types.put(getNewType(), module);
			} else {
				throw new IllegalStateException("Trying to add adapter module with itemclass which is already registered! " + newItemClass);
			}
		} else {
			Log.w(TAG, "Attempting to add adapter module which is already added - " + module.getClass());
		}
	}


	/**
	 * Get AdapterModule to handle certain ViewType.
	 *
	 * @param viewType
	 * 		ViewType to be handled.
	 *
	 * @return Responsible AdapterModule.
	 */
	public M getAdapterModule(int viewType) {
		return types.get(viewType);
	}


	/**
	 * Get ViewType corresponding to item.
	 */
	public int getViewType(I item) {
		// Firstly try to find exact match
		for (M module : types.values()) {
			if (module.getItemClass().equals(item.getClass())) {
				return getKeyByValue(types, module);
			}
		}
		// If not found - try instanceof match
		for (M value : types.values()) {
			if (value.getItemClass().isInstance(item)) {
				return getKeyByValue(types, value);
			}
		}
		return -1;
	}

	/**
	 * Try to check if module with itemClass is registered
	 */
	private int getViewType(Class<I> itemClass) {
		for (M module : types.values()) {
			if (module.getItemClass().equals(itemClass)) {
				return getKeyByValue(types, module);
			}
		}
		return -1;
	}


	/**
	 * Generate a new ViewType.
	 *
	 * @return new ViewType.
	 */
	private int getNewType() {
		if (types.isEmpty()) {
			return 0;
		}
		return types.lastKey() + 1;
	}


	/**
	 * Get TreeMap key from value.
	 *
	 * @param map
	 * 		map in question.
	 * @param value
	 * 		value in question.
	 *
	 * @return matching key
	 */
	private int getKeyByValue(TreeMap<Integer, M> map, M value) {
		for (Map.Entry<Integer, M> entry : map.entrySet()) {
			if (value == entry.getValue()) {
				return entry.getKey();
			}
		}
		return -1;
	}
}
