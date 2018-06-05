package de.symeda.sormas.app.core;

/**
 * Created by Orson on 08/05/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public interface ISaveableWithCallback {

    void save(INotificationContext nContext, Callback.IAction callback);

}
