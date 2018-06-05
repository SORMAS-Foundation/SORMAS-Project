package de.symeda.sormas.app.core.async;

/**
 * Created by Orson on 11/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public interface ITaskResultHolderIterator {
    boolean hasNext();
    <E> E next();
}
