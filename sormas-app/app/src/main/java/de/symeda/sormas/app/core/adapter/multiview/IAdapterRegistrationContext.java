package de.symeda.sormas.app.core.adapter.multiview;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Orson on 28/11/2017.
 */
public interface IAdapterRegistrationContext {
    <TDataItem, T1 extends RecyclerView.ViewHolder, T2 extends DataBinder<T1, TDataItem>> IAdapterRegistrationData<TDataItem> registerBinder(Class<T2> binder) throws IllegalAccessException, InstantiationException;
}
