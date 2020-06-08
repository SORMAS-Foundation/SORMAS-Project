/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.core.async;

import java.lang.ref.WeakReference;

import android.view.View;

import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DaoException;
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
				} else {
					NotificationHelper.showNotification(
						notificationView,
						NotificationType.ERROR,
						String.format(notificationView.getResources().getString(R.string.message_save_error), entityName));
				}
			} else {
				NotificationHelper.showNotification(
					notificationView,
					NotificationType.SUCCESS,
					String.format(notificationView.getResources().getString(R.string.message_save_success), entityName));
			}
		}
	}
}
