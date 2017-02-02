package com.cuttingedge.undorecycler;

import android.support.v7.widget.RecyclerView.ViewHolder;

import com.cuttingedge.undorecycler.Modules.AdapterModule;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Robbe Sneyders
 *
 * Manager for different AdapterModules
 */
public class AdapterModuleManager<M extends AdapterModule> {


    /**
     * TreeMap connecting ViewTypes with AdapterModules
     */
    TreeMap<Integer, M> types = new TreeMap<>();


    /**
     * Add new AdapterModule to TreeMap with newly generated ViewType.
     *
     * @param module AdapterModule to be added.
     */
    public void addAdapterModule(M module) {
        if (!types.containsValue(module));
            types.put(getNewType(), module);
    }


    /**
     * Get AdapterModule to handle certain ViewType.
     * @param viewType ViewType to be handled.
     * @return Responsible AdapterModule.
     */
    public AdapterModule getAdapterModule(int viewType) {
        return types.get(viewType);
    }


    /**
     * Get ViewType corresponding to item.
     */
    public <VH extends ViewHolder, Item extends ModularItem> int getViewType(ModularItem item) {
        for (AdapterModule<VH, Item> value : types.values()) {
            if (value.returnedClass().isInstance(item)) {
                return getKeyByValue(types, value);
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
        if (types.isEmpty())
            return 0;
        return types.lastKey() + 1;
    }


    /**
     * Get TreeMap key from value.
     *
     * @param map map in question.
     * @param value value in question.
     * @return matching key
     */
    private int getKeyByValue(TreeMap<Integer, M> map, AdapterModule value) {
        for (Map.Entry<Integer, M> entry : map.entrySet()) {
            if (value == entry.getValue()) {
                return entry.getKey();
            }
        }
        return -1;
    }
}
