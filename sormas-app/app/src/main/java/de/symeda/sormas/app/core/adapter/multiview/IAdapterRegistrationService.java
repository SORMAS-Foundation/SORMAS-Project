package de.symeda.sormas.app.core.adapter.multiview;

/**
 * Created by Orson on 28/11/2017.
 */
public interface IAdapterRegistrationService {
    void register(IAdapterRegistrationContext context) throws InstantiationException, IllegalAccessException;
}
