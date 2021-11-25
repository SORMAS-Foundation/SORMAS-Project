package de.symeda.sormas.ui.utils;

import com.vaadin.server.Page;
import com.vaadin.ui.Notification;

public final class NotificationHelper {

	private NotificationHelper() {
		// Hide Utility Class Constructor
	}

	public static void showNotification(String caption, Notification.Type type, int delayMsec) {
		Notification notification = new Notification(caption, "", type);
		notification.setDelayMsec(delayMsec);
		notification.show(Page.getCurrent());
	}
}
