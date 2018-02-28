package de.symeda.sormas.app.core.valueobjects;

/**
 * Created by Orson on 01/01/2018.
 */

public interface IBaseValueObject<T> {
    T getValue();
    boolean isEqualTo(IBaseValueObject<T> value);
}
