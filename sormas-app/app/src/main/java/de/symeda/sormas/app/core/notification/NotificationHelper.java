package de.symeda.sormas.app.core.notification;

import android.content.Context;
import android.content.res.Resources;
import android.databinding.ViewDataBinding;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.INotificationContext;

/**
 * Created by Orson on 01/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class NotificationHelper {



    public static void showNotification(ViewDataBinding binding, NotificationType type, int messageResId) {
        showNotification(binding, NotificationPosition.TOP, type, messageResId);
    }

    public static void showNotification(ViewDataBinding binding, NotificationPosition position, NotificationType type, int messageResId) {
        if (messageResId <= 0)
            return;

        if (binding == null)
            return;

        View notificationRoot = binding.getRoot();

        if (notificationRoot == null)
            return;

        Resources resources = notificationRoot.getResources();
        String message = resources.getString(messageResId);

        showNotification(binding.getRoot(), position, type, message);
    }

    public static void showNotification(ViewDataBinding binding, NotificationType type, String message) {
        showNotification(binding, NotificationPosition.TOP, type, message);
    }

    public static void showNotification(ViewDataBinding binding, NotificationPosition position, NotificationType type, String message) {
        if (binding == null)
            return;

        showNotification(binding.getRoot(), position, type, message);
    }



    //Many functions call this
    private static void showNotification(View notificationRoot, NotificationPosition position, NotificationType type, String message) {
        LinearLayout notificationFrame = (LinearLayout)notificationRoot.findViewById(R.id.notificationFrame);
        TextView tvNotificationMessage = (TextView)notificationRoot.findViewById(R.id.tvNotificationMessage);

        if (notificationFrame == null)
            return;

        if (tvNotificationMessage == null)
            return;

        showNotification(notificationFrame, tvNotificationMessage, position, type, message);
    }




    public static void showNotification(View notificationFrame, TextView tvNotificationMessage, NotificationType type, int messageResId) {
        showNotification(notificationFrame, tvNotificationMessage, NotificationPosition.TOP, type, messageResId);
    }

    public static void showNotification(View notificationFrame, TextView tvNotificationMessage, NotificationPosition position, NotificationType type, int messageResId) {
        if (messageResId <= 0)
            return;

        if (notificationFrame == null)
            return;

        Resources resources = notificationFrame.getResources();

        showNotification(notificationFrame, tvNotificationMessage, position, type, resources.getString(messageResId));
    }

    private static void showNotification(View notificationFrame, TextView tvNotificationMessage, NotificationPosition position, NotificationType type, String message) {
        if (notificationFrame == null)
            return;

        if (tvNotificationMessage == null)
            return;

        Resources resources = notificationFrame.getResources();

        int backgroundColor =  resources.getColor(type.getBackgroundColor());
        int textColor = resources.getColor(type.getTextColor());

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) notificationFrame.getLayoutParams();

        if (position.equals(NotificationPosition.TOP))
            layoutParams.gravity = Gravity.TOP;

        if (position.equals(NotificationPosition.BOTTOM))
            layoutParams.gravity = Gravity.BOTTOM;

        notificationFrame.setOnClickListener(new View.OnClickListener() {
            private View nf;

            @Override
            public void onClick(View v) {
                hideNotification(nf);
            }

            private View.OnClickListener init(View nf){
                this.nf = nf;
                return this;
            }

        }.init(notificationFrame));

        notificationFrame.setBackgroundColor(backgroundColor);
        tvNotificationMessage.setTextColor(textColor);
        tvNotificationMessage.setText(message);

        notificationFrame.setVisibility(View.VISIBLE);
    }




    public static void hideNotification(ViewDataBinding binding) {
        if (binding == null)
            return;

        hideNotification(binding.getRoot());
    }

    public static void hideNotification(View notificationRoot) {
        View view = notificationRoot;

        if (notificationRoot.getId() != R.id.notificationFrame)
            view = (LinearLayout)notificationRoot.findViewById(R.id.notificationFrame);

        if (view == null)
            return;

        view.setVisibility(View.GONE);
    }



    public static void showNotification(INotificationContext communicator, NotificationType type, String message) {
        showNotification(communicator, NotificationPosition.TOP, type, message);
    }

    public static void showNotification(INotificationContext communicator, NotificationPosition position, NotificationType type, String message) {
        View rootView = communicator.getRootView();

        if (rootView == null)
            return;

        showNotification(rootView, position, type, message);
    }

    public static void showNotification(INotificationContext communicator, NotificationType type, int messageResId) {
        showNotification(communicator, NotificationPosition.TOP, type, messageResId);
    }

    public static void showNotification(INotificationContext communicator, NotificationPosition position, NotificationType type, int messageResId) {
        View rootView = communicator.getRootView();

        if (rootView == null)
            return;

        Context context = rootView.getContext();

        if (context == null)
            return;

        Resources resources = context.getResources();

        if (resources == null)
            return;

        showNotification(communicator.getRootView(), position, type, resources.getString(messageResId));
    }

    public static void hideNotification(INotificationContext communicator) {
        View rootView = communicator.getRootView();

        if (rootView == null)
            return;

        View view = rootView;

        if (rootView.getId() != R.id.notificationFrame)
            view = (LinearLayout)rootView.findViewById(R.id.notificationFrame);

        if (view == null)
            return;

        view.setVisibility(View.GONE);
    }
}
