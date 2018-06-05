package de.symeda.sormas.app.core.adapter.multiview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import de.symeda.sormas.app.core.ClassUtil;

/**
 * Created by Orson on 28/11/2017.
 */
public class AdapterRegistrationContext<E extends Enum<E>> implements IAdapterRegistrationContext {

    private Context context;
    private E viewType;
    private EnumMapDataBinderAdapter<E> dataBindAdapter;

    public AdapterRegistrationContext(Context context, E viewType, EnumMapDataBinderAdapter<E> dataBindAdapter) {
        this.context = context;
        this.viewType = viewType;
        this.dataBindAdapter = dataBindAdapter;
    }


    @Override
    public <TDataItem, T1 extends RecyclerView.ViewHolder, T2 extends DataBinder<T1, TDataItem>> IAdapterRegistrationData<TDataItem> registerBinder(Class<T2> binder) throws IllegalAccessException, InstantiationException {

        if (!ClassUtil.hasParameterlessPublicConstructor(binder))
            throw new InstantiationException("DataBind MUST have a parameterless constructor.");

        T2 dataBinder = binder.newInstance();
        dataBinder.setContext(this.context);
        dataBinder.setDataBinderAdapter(this.dataBindAdapter);
        this.dataBindAdapter.registerBinder(this.viewType, dataBinder);

        return new AdapterRegistrationData(dataBinder);
    }
}
