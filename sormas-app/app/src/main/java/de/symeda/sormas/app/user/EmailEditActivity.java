package de.symeda.sormas.app.user;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;
import static de.symeda.sormas.app.core.notification.NotificationType.WARNING;

import android.os.AsyncTask;

import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;

public class EmailEditActivity extends BaseEditActivity<User> {

	private AsyncTask saveTask;

	@Override
	protected User queryRootEntity(String recordUuid) {
		return ConfigProvider.getUser();
	}

	@Override
	protected User buildRootEntity() {
		return ConfigProvider.getUser();
	}

	@Override
	protected EmailEditFragment buildEditFragment(PageMenuItem menuItem, User activityRootData) {
		return EmailEditFragment.newInstance(activityRootData);
	}

	@Override
	public void saveData() {

		if (saveTask != null) {
			NotificationHelper.showNotification(this, WARNING, getString(R.string.message_already_saving));
			return; // don't save multiple times
		}

		final User userToSave = (User) getActiveFragment().getPrimaryData();
		EmailEditFragment fragment = (EmailEditFragment) getActiveFragment();

		try {
			FragmentValidator.validate(getContext(), fragment.getContentBinding());
		} catch (ValidationException e) {
			NotificationHelper.showNotification(this, ERROR, e.getMessage());
			return;
		}

		saveTask = new SavingAsyncTask(getRootView(), userToSave) {

			@Override
			protected void onPreExecute() {
				showPreloader();
			}

			@Override
			public void doInBackground(TaskResultHolder resultHolder) throws DaoException {
				DatabaseHelper.getUserDao().saveAndSnapshot(userToSave);
			}

			@Override
			protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
				hidePreloader();
				super.onPostExecute(taskResult);
				if (taskResult.getResultStatus().isSuccess()) {
					goToNextPage();
				} else {
					onResume(); // reload data
				}
				saveTask = null;
			}
		}.executeOnThreadPool();
	}

	@Override
	protected int getActivityTitle() {
		return R.string.heading_email_edit;
	}

	@Override
	public Enum getPageStatus() {
		return null;
	}
}
