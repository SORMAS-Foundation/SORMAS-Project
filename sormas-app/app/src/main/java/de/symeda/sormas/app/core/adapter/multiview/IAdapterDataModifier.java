package de.symeda.sormas.app.core.adapter.multiview;

/**
 * Created by Orson on 01/12/2017.
 */
public interface IAdapterDataModifier<TDataItem> {
    void modify(TDataItem item, int position);
}
