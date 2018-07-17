package de.symeda.sormas.app.component.menu;

import android.widget.AdapterView;

public interface NotificationCountChangingListener {

    int onNotificationCountChangingAsync(AdapterView<?> parent, PageMenuItem menuItem, int position);
}
