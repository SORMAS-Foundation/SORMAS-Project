package de.symeda.sormas.app.core.adapter.multiview;

import android.content.Context;

/**
 * Created by Orson on 28/11/2017.
 */
public class AdapterConfiguration<E extends Enum<E>> implements IAdapterConfiguration<E> {

    private Context context;
    private IAdapterRegistrationContext registrationContext;
    private EnumMapDataBinderAdapter<E> dataBindAdapter;

    public AdapterConfiguration(Context context, EnumMapDataBinderAdapter<E> dataBindAdapter) {
        this.context = context;
        this.dataBindAdapter = dataBindAdapter;
    }

    @Override
    public IAdapterConfiguration forViewType(E viewType, IAdapterRegistrationService serivce) throws IllegalAccessException, InstantiationException {
        this.registrationContext = new AdapterRegistrationContext<E>(this.context, viewType, this.dataBindAdapter);
        serivce.register(this.registrationContext);
        return this;
    }
}
