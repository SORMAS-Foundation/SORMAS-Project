package de.symeda.sormas.app.core.async;

/**
 * Created by Orson on 03/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class AsyncTaskResult<T> {
    private T result;
    private Exception error;

    public T getResult() {
        return result;
    }

    public Exception getError() {
        return error;
    }

    public AsyncTaskResult(T result) {
        super();
        this.result = result;
    }

    public AsyncTaskResult(Exception error) {
        super();
        this.error = error;
    }
}