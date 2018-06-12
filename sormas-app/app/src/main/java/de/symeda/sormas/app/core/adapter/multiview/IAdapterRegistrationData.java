package de.symeda.sormas.app.core.adapter.multiview;

import java.util.List;

/**
 * Created by Orson on 28/11/2017.
 */
public interface IAdapterRegistrationData<TDataItem> {
    IAdapterRegistrationData<TDataItem> registerData(List<TDataItem> data);
    IAdapterRegistrationData<TDataItem> forEach(IAdapterDataModifier modifier);
}
