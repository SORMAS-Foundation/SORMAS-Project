package de.symeda.sormas.app.util;

/**
 * Created by Orson on 08/11/2017.
 */

public interface SyncCallback {
    void call(boolean syncFailed, String syncFailedMessage);
}
