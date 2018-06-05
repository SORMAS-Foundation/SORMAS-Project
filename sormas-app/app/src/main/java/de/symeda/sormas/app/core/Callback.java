package de.symeda.sormas.app.core;

/**
 * Created by Orson on 16/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public class Callback {
    public static interface IAction<T> {
        void call(T result);
    }

    public static interface IAction2<T1, T2> {
        void call(T1 result1, T2 result2);
    }

    public static interface IAction3<T1, T2, T3> {
        void call(T1 result1, T2 result2, T3 result3);
    }

    public static interface IFunc<T, TResult> {
        TResult call(T result);
    }
}
