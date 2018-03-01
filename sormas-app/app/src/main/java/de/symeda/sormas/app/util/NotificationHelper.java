package de.symeda.sormas.app.util;

import android.content.res.Resources;
import android.databinding.ViewDataBinding;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.NotificationType;

/**
 * Created by Orson on 01/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class NotificationHelper {

    public static void showNotification(View notificationFrame, TextView tvNotificationMessage, NotificationType type, int messageResId) {
        if (messageResId <= 0)
            return;

        if (notificationFrame == null)
            return;

        if (tvNotificationMessage == null)
            return;

        Resources resources = notificationFrame.getResources();
        String message = resources.getString(messageResId);

        int backgroundColor =  resources.getColor(type.getBackgroundColor());
        int textColor = resources.getColor(type.getTextColor());

        notificationFrame.setBackgroundColor(backgroundColor);
        tvNotificationMessage.setTextColor(textColor);
        tvNotificationMessage.setText(message);

        notificationFrame.setVisibility(View.VISIBLE);
    }

    public static void showNotification(View notificationFrame, TextView tvNotificationMessage, NotificationType type, String message) {
        if (notificationFrame == null)
            return;

        if (tvNotificationMessage == null)
            return;

        Resources resources = notificationFrame.getResources();

        int backgroundColor =  resources.getColor(type.getBackgroundColor());
        int textColor = resources.getColor(type.getTextColor());

        notificationFrame.setBackgroundColor(backgroundColor);
        tvNotificationMessage.setTextColor(textColor);
        tvNotificationMessage.setText(message);

        notificationFrame.setVisibility(View.VISIBLE);
    }

    public static void showNotification(ViewDataBinding binding, NotificationType type, int messageResId) {
        if (messageResId <= 0)
            return;

        if (binding == null)
            return;

        View notificationRoot = binding.getRoot();

        LinearLayout notificationFrame = (LinearLayout)notificationRoot.findViewById(R.id.notificationFrame);
        TextView tvNotificationMessage = (TextView)notificationRoot.findViewById(R.id.tvNotificationMessage);

        if (notificationFrame == null)
            return;

        if (tvNotificationMessage == null)
            return;

        Resources resources = notificationFrame.getResources();
        String message = resources.getString(messageResId);

        int backgroundColor =  resources.getColor(type.getBackgroundColor());
        int textColor = resources.getColor(type.getTextColor());

        notificationFrame.setBackgroundColor(backgroundColor);
        tvNotificationMessage.setTextColor(textColor);
        tvNotificationMessage.setText(message);

        notificationFrame.setVisibility(View.VISIBLE);
    }

    public static void showNotification(ViewDataBinding binding, NotificationType type, String message) {
        if (binding == null)
            return;

        View notificationRoot = binding.getRoot();

        LinearLayout notificationFrame = (LinearLayout)notificationRoot.findViewById(R.id.notificationFrame);
        TextView tvNotificationMessage = (TextView)notificationRoot.findViewById(R.id.tvNotificationMessage);

        if (notificationFrame == null)
            return;

        if (tvNotificationMessage == null)
            return;

        Resources resources = notificationFrame.getResources();

        int backgroundColor =  resources.getColor(type.getBackgroundColor());
        int textColor = resources.getColor(type.getTextColor());

        notificationFrame.setBackgroundColor(backgroundColor);
        tvNotificationMessage.setTextColor(textColor);
        tvNotificationMessage.setText(message);

        notificationFrame.setVisibility(View.VISIBLE);
    }

    public static void hideNotification(View notificationFrame) {
        if (notificationFrame == null)
            return;

        notificationFrame.setVisibility(View.GONE);
    }

    public static void hideNotification(ViewDataBinding binding) {
        if (binding == null)
            return;

        View notificationRoot = binding.getRoot();

        LinearLayout notificationFrame = (LinearLayout)notificationRoot.findViewById(R.id.notificationFrame);

        if (notificationFrame == null)
            return;

        notificationFrame.setVisibility(View.GONE);
    }
}
