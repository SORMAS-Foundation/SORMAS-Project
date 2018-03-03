package de.symeda.sormas.app.core;

/**
 * Created by Orson on 03/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class BoolResult {


    private final boolean mSuccess;
    private final String mMessage;

    public static final BoolResult TRUE = new BoolResult(true, "");
    public static final BoolResult FALSE = new BoolResult(true, "");

    public BoolResult(boolean success, String message) {
        this.mSuccess = success;
        this.mMessage = message;
    }

    public boolean isSuccess() {
        return mSuccess;
    }

    public String getMessage() {
        return mMessage;
    }
}
