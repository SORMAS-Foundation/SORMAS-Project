package de.symeda.sormas.app.core;

/**
 * Created by Orson on 03/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class BoolResult {

    private final int mValue;
    private final boolean mSuccess;
    private final String mMessage;

    public static final BoolResult TRUE = new BoolResult(true, "");
    public static final BoolResult FALSE = new BoolResult(false, "");

    public BoolResult(boolean success, String message) {
        this.mValue = (success)? 1 : 0;
        this.mSuccess = success;
        this.mMessage = message;
    }

    public boolean isSuccess() {
        return mSuccess;
    }

    public String getMessage() {
        return mMessage;
    }

    /*public void setStatus(BoolResult status) {
        this.mSuccess = status.isSuccess();
        this.mMessage = status.getMessage();
    }*/

    // <editor-fold defaultstate="collapsed" desc="Overrides">

    @Override
    public int hashCode() {
        return mValue + 37 * mValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BoolResult)) {
            return false;
        }
        BoolResult other = (BoolResult) obj;
        return mValue == other.mValue;
    }

    @Override
    public String toString() {
        return mMessage;
    }

    // </editor-fold>
}
