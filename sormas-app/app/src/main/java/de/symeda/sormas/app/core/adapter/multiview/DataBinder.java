package de.symeda.sormas.app.core.adapter.multiview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Orson on 27/11/2017.
 */

public abstract class DataBinder<T extends RecyclerView.ViewHolder, TDataItem> {

    private int currentDataPosition = -1;
    private RecyclerViewDataBinderAdapter recyclerViewDataBinderAdapter;
    private Context context;

    public DataBinder() {
        this.recyclerViewDataBinderAdapter = null;
    }

    public DataBinder(RecyclerViewDataBinderAdapter recyclerViewDataBinderAdapter) {
        this.recyclerViewDataBinderAdapter = recyclerViewDataBinderAdapter;
    }


    public abstract T createViewHolder(ViewGroup parent);

    public abstract void bindToViewHolder(T viewHolder, int position);

    public abstract int getItemCount();

    public abstract void addAll(List<TDataItem> data);

    public final void setContext(Context context) {
        this.context = context;
    }

    public final Context getContext() {
         return this.context;
    }

    public final void setDataBinderAdapter(RecyclerViewDataBinderAdapter recyclerViewDataBinderAdapter) {
        this.recyclerViewDataBinderAdapter = recyclerViewDataBinderAdapter;
    }

    public final void notifyDataSetChanged() {
        if (this.recyclerViewDataBinderAdapter == null)
            throw new NullPointerException("Data binder adapter is null");

        this.recyclerViewDataBinderAdapter.notifyDataSetChanged();
    }

    public final void notifyBinderDataSetChanged() {
        notifyBinderItemRangeChanged(0, getItemCount());
    }

    public final void notifyBinderItemChanged(int position) {
        this.recyclerViewDataBinderAdapter.notifyBinderItemChanged(this, position);
    }

    public final void notifyBinderItemRangeChanged(int positionStart, int itemCount) {
        this.recyclerViewDataBinderAdapter.notifyBinderItemRangeChanged(this, positionStart, itemCount);
    }

    public final void notifyBinderItemInserted(int position) {
        this.recyclerViewDataBinderAdapter.notifyBinderItemInserted(this, position);
    }

    public final void notifyBinderItemMoved(int fromPosition, int toPosition) {
        this.recyclerViewDataBinderAdapter.notifyBinderItemMoved(this, fromPosition, toPosition);
    }

    public final void notifyBinderItemRangeInserted(int positionStart, int itemCount) {
        this.recyclerViewDataBinderAdapter.notifyBinderItemRangeInserted(this, positionStart, itemCount);
    }

    public final void notifyBinderItemRemoved(int position) {
        this.recyclerViewDataBinderAdapter.notifyBinderItemRemoved(this, position);
    }

    public final void notifyBinderItemRangeRemoved(int positionStart, int itemCount) {
        this.recyclerViewDataBinderAdapter.notifyBinderItemRangeRemoved(this, positionStart, itemCount);
    }

    public int getCurrentDataPosition() {
        return currentDataPosition;
    }

    public boolean moveNextDataPosition() {
        if (getItemCount() == 0 || getItemCount() <= (currentDataPosition + 1)) {
            return false;
        }

        currentDataPosition = currentDataPosition + 1;
        return true;
    }

    private void reset() {
        currentDataPosition = -1;
    }
}
