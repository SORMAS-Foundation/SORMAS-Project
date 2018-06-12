package de.symeda.sormas.app.core.async;

/**
 * Created by Orson on 08/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public interface ITaskResultHolderEnumerableIterator<T> {
    boolean hasNext();
    T next();
}
