package de.symeda.sormas.app.core;

/**
 * Created by Orson on 25/01/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public interface INotificationCommunicator {

    void showNotification(NotificationType type, String message);
    void hideNotification();
}
