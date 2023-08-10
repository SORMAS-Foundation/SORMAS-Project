package de.symeda.sormas.app.environment.edit;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;
import static de.symeda.sormas.app.core.notification.NotificationType.WARNING;

import android.content.Context;
import android.os.AsyncTask;
import android.view.MenuItem;

import de.symeda.sormas.api.utils.ValidationException;
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
import de.symeda.sormas.app.util.Bundler;
import de.symeda.sormas.app.util.NavigationHelper;

public class EnvironmentNewActivity extends BaseEditActivity<Environment> {

	private AsyncTask saveTask;

	public static void startActivity(Context context) {
		BaseEditActivity.startActivity(context, EnvironmentNewActivity.class, buildBundle());
	}

	public static Bundler buildBundle() {
		return BaseEditActivity.buildBundle(null);
	}

	@Override
	public Enum getPageStatus() {
		return null;
	}

	@Override
	protected int getActivityTitle() {
		return R.string.heading_environment_new;
	}

	@Override
	protected Environment queryRootEntity(String recordUuid) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Environment buildRootEntity() {
		final Environment environment;
		environment = DatabaseHelper.getEnvironmentDao().build();
		return environment;
	}

	@Override
	protected BaseEditFragment buildEditFragment(PageMenuItem menuItem, Environment activityRootData) {
		BaseEditFragment fragment;

		fragment = EnvironmentNewFragment.newInstance(activityRootData);
		fragment.setLiveValidationDisabled(true);

		return fragment;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			NavigationHelper.goToEnvironments(getContext());
			finish();
		} else {
			super.onOptionsItemSelected(item);
		}

		return true;
	}

	@Override
	public void saveData() {
		if (saveTask != null) {
			NotificationHelper.showNotification(this, WARNING, getString(R.string.message_already_saving));
			return; // don't save multiple times
		}

		final Environment environmentToSave = (Environment) getActiveFragment().getPrimaryData();
		EnvironmentNewFragment fragment = (EnvironmentNewFragment) getActiveFragment();
		fragment.setLiveValidationDisabled(false);

		try {
			FragmentValidator.validate(getContext(), fragment.getContentBinding());
		} catch (ValidationException e) {
			NotificationHelper.showNotification(this, ERROR, e.getMessage());
			return;
		}

		saveTask = new SavingAsyncTask(getRootView(), environmentToSave) {

			@Override
			protected void onPreExecute() {
				showPreloader();
			}

			@Override
			public void doInBackground(TaskResultHolder resultHolder) throws DaoException {
				DatabaseHelper.getEnvironmentDao().saveAndSnapshot(environmentToSave);
			}

			@Override
			protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
				hidePreloader();
				super.onPostExecute(taskResult);
				if (taskResult.getResultStatus().isSuccess()) {
					finish();
					EnvironmentEditActivity.startActivity(getContext(), environmentToSave.getUuid());
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
