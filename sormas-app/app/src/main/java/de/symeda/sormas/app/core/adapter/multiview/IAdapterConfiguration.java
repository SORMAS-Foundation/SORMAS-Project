package de.symeda.sormas.app.core.adapter.multiview;

/**
 * Created by Orson on 27/11/2017.
 */

public interface IAdapterConfiguration<E extends Enum<E>> {
    IAdapterConfiguration forViewType(E type, IAdapterRegistrationService serivce) throws IllegalAccessException, InstantiationException;
}
