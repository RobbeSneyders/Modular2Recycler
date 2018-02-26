package com.cuttingedge.adapter2recycler.Modules;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.ViewGroup;

import com.cuttingedge.adapter2recycler.Adapter.ModularAdapter;
import com.cuttingedge.adapter2recycler.ModularItem;

import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Created by Robbe Sneyders
 * <p>
 * This module should be extended for every different item to be shown in the recyclerview
 */
@SuppressWarnings("unused")
public abstract class AdapterModule<VH extends ViewHolder, I extends ModularItem> {
    private Class<I> itemClass;

    public AdapterModule() {
        itemClass = returnedItemClass();
    }

    /**
     * Binds the module to the manager via adapter.
     *
     * @param adapter ModularAdapter for which this module is meant to be used.
     */
    public void bindToAdapter(ModularAdapter adapter) {
        adapter.addAdapterModule(this);
    }


    /**
     * Called when RecyclerView needs a new ViewHolder of the type associated with the receiving
     * implementation of this module
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     *
     * @return A new ViewHolder that holds a View of the given view type.
     */
    public abstract VH onCreateViewHolder(ViewGroup parent);


    /**
     * Called by RecyclerView to display the data at the specified position. This method
     * should update the contents of the {@link ViewHolder#itemView} to reflect the item at
     * the given position.
     *
     * @param viewHolder The ViewHolder which should be updated to represent the contents of the item.
     * @param item item to get contents from.
     */
    public abstract void onBindViewHolder(VH viewHolder, I item);


    /**
     * Called by RecyclerView to display the data at the specified position. This method
     * should update the contents of the {@link ViewHolder#itemView} to reflect the item at
     * the given position.
     *
     * @param viewHolder The ViewHolder which should be updated to represent the contents of the item.
     * @param item item to get contents from.
     * @param payloads A non-null list of merged payloads. Can be empty list if requires full update.
     */
    public void onBindViewHolder(VH viewHolder, I item, List<Object> payloads) {
        onBindViewHolder(viewHolder, item);
    }


    /**
     * Called when a view created by the corresponding adapter has been attached to a window.
     *
     * @param viewHolder holder of the attached view.
     */
    public void onViewAttachedToWindow(VH viewHolder) {}


    /**
     * Called when a view created by the corresponding adapter has been detached from a window.
     *
     * @param viewHolder holder of the detached view.
     */
    public void onViewDetachedFromWindow(VH viewHolder) {}


    /**
     * This method is called whenever the view in the ViewHolder is recycled.
     *
     * @param viewHolder holder of recycled view.
     */
    public void onViewRecycled(VH viewHolder) {}


	/**
	 * Use reflection to find used subclass of ModularItem. Called once in constructor.
	 * Override and return item class directly if you are using parametrized Modules with inheritance, for example ChildModule<VH extends ParentModule.VH, I extends ChildModule.Item> extends ParentModule<VH, I>
	 *
	 * @return used subclass of ModularItem.
	 */
	@SuppressWarnings("WeakerAccess")
    protected Class<I> returnedItemClass() {
		Class c = getClass();
		while (!(c.getGenericSuperclass() instanceof ParameterizedType)) {
			c = c.getSuperclass();
		}

		ParameterizedType parameterizedType = (ParameterizedType) c.getGenericSuperclass();
		return (Class<I>) parameterizedType.getActualTypeArguments()[1];
	}

    public Class<I> getItemClass() {
        return itemClass;
    }
}
