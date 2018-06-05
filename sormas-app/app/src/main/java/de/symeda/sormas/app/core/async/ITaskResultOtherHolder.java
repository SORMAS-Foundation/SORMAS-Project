package de.symeda.sormas.app.core.async;

/**
 * Created by Orson on 13/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public interface ITaskResultOtherHolder {

    <T> void add(T value);

    ITaskResultHolderIterator iterator();
}
