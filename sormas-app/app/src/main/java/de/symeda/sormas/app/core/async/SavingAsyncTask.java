package de.symeda.sormas.app.core.async;

import android.view.View;

import java.lang.ref.WeakReference;

import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.ServerConnectionException;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;

public abstract class SavingAsyncTask extends DefaultAsyncTask {

    // for notifications
    private final WeakReference<View> notificationViewReference;
    private final String entityName;


    public SavingAsyncTask(View notificationView, AbstractDomainObject relatedEntity) {
        super(notificationView.getContext(), relatedEntity);
        this.notificationViewReference = new WeakReference<>(notificationView);

        if (relatedEntity != null) {
            entityName = relatedEntity.getEntityName();
        } else {
            entityName = null;
        }
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {

        View notificationView = notificationViewReference.get();
        if (notificationView != null) {
            if (taskResult.getResultStatus().isFailed()) {
                if (taskResult.getError() instanceof ValidationException) {
                    NotificationHelper.showNotification(notificationView, NotificationType.ERROR, taskResult.getResultStatus().getMessage());
                } else if (taskResult.getError() instanceof DaoException) {
                    NotificationHelper.showNotification(notificationView, NotificationType.ERROR, taskResult.getResultStatus().getMessage());
                } else if (taskResult.getError() instanceof ServerConnectionException) {
                    NotificationHelper.showNotification(notificationView, NotificationType.ERROR, taskResult.getResultStatus().getMessage());
                } else {
                    NotificationHelper.showNotification(notificationView, NotificationType.ERROR,
                            String.format(notificationView.getResources().getString(R.string.snackbar_save_error), entityName));
                }
            } else {
                NotificationHelper.showNotification(notificationView, NotificationType.SUCCESS,
                        String.format(notificationView.getResources().getString(R.string.snackbar_save_success), entityName));
            }
        }
    }
}
