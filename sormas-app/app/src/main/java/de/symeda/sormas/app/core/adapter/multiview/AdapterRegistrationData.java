package de.symeda.sormas.app.core.adapter.multiview;

import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * Created by Orson on 28/11/2017.
 */
public class AdapterRegistrationData<TDataItem, T1 extends RecyclerView.ViewHolder> implements IAdapterRegistrationData<TDataItem> {

    private List<TDataItem> data;
    private DataBinder<T1, TDataItem> dataBinder;

    public AdapterRegistrationData(DataBinder<T1, TDataItem> dataBinder) {
        this.dataBinder = dataBinder;
    }

    @Override
    public IAdapterRegistrationData<TDataItem> registerData(List<TDataItem> data) {
        this.data = data;
        this.dataBinder.addAll(data);
        return this;
    }

    public IAdapterRegistrationData<TDataItem> forEach(IAdapterDataModifier modifier) {

        //For each item in data
        int position = 0;
        for(TDataItem item: this.data) {
            modifier.modify(item, position);
            position = position + 1;
        }
        return this;
    }


}
/*

public interface IAdapterRegistrationData<TDataItem> {
    IAdapterRegistrationData<TDataItem> registerData(List<TDataItem> data);
    IAdapterRegistrationData<TDataItem> forEach(IAdapterDataModifier modifier);
}


*/

