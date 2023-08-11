package de.symeda.sormas.app.environment.edit;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;
import static de.symeda.sormas.app.core.notification.NotificationType.WARNING;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.environment.Environment;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.environment.EnvironmentSection;

public class EnvironmentEditActivity extends BaseEditActivity<Environment> {

	private AsyncTask saveTask;

	private List<PageMenuItem> pageMenuItems;

	public static void startActivity(Context context, String rootUuid) {
		BaseEditActivity.startActivity(context, EnvironmentEditActivity.class, buildBundle(rootUuid));
	}

	public static void startActivity(Context context, String recordUuid, EnvironmentSection section) {
		BaseActivity.startActivity(context, EnvironmentEditActivity.class, buildBundle(recordUuid, section));
	}

	@Override
	public Enum getPageStatus() {
		return null;
	}

	@Override
	protected int getActivityTitle() {
		return R.string.heading_environment_edit;
	}

	@Override
	protected Environment queryRootEntity(String recordUuid) {
		return DatabaseHelper.getEnvironmentDao().queryUuidWithEmbedded(recordUuid);
	}

	@Override
	protected Environment buildRootEntity() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<PageMenuItem> getPageMenuData() {
		List<PageMenuItem> menuItems = PageMenuItem.fromEnum(EnvironmentSection.values(), getContext());

		return menuItems;
	}

	@Override
	protected BaseEditFragment buildEditFragment(PageMenuItem menuItem, Environment activityRootData) {
		BaseEditFragment fragment;
		fragment = EnvironmentEditFragment.newInstance(activityRootData);
		return fragment;
	}

	@Override
	public void saveData() {
		if (saveTask != null) {
			NotificationHelper.showNotification(this, WARNING, getString(R.string.message_already_saving));
			return; // don't save multiple times
		}

		final Environment changedEnvironment = getStoredRootEntity();
		EnvironmentEditFragment fragment = (EnvironmentEditFragment) getActiveFragment();

		try {
			FragmentValidator.validate(getContext(), fragment.getContentBinding());
		} catch (ValidationException e) {
			NotificationHelper.showNotification(this, ERROR, e.getMessage());
			return;
		}

		saveTask = new SavingAsyncTask(getRootView(), changedEnvironment) {

			@Override
			protected void onPreExecute() {
				showPreloader();
			}

			@Override
			public void doInBackground(TaskResultHolder resultHolder) throws DaoException {
				synchronized (EnvironmentEditActivity.this) {
					DatabaseHelper.getEnvironmentDao().saveAndSnapshot(changedEnvironment);
				}
			}

			@Override
			protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
				hidePreloader();
				super.onPostExecute(taskResult);
				if (taskResult.getResultStatus().isSuccess()) {
					finish();
				} else {
					onResume();
				}
				saveTask = null;
			}
		}.executeOnThreadPool();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (saveTask != null && !saveTask.isCancelled()) {
			saveTask.cancel(true);
		}
	}
}
