package de.symeda.sormas.app.environment.edit;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;
import static de.symeda.sormas.app.core.notification.NotificationType.WARNING;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.environment.Environment;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.dialog.ConfirmationDialog;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.environment.EnvironmentSection;
import de.symeda.sormas.app.task.edit.TaskNewActivity;
import de.symeda.sormas.app.util.Consumer;
import de.symeda.sormas.app.util.NavigationHelper;

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

		// Sections must be removed in reverse order
		if (DatabaseHelper.getFeatureConfigurationDao().isFeatureDisabled(FeatureType.TASK_MANAGEMENT)) {
			menuItems.set(EnvironmentSection.TASKS.ordinal(), null);
		}

		return menuItems;
	}

	@Override
	protected BaseEditFragment buildEditFragment(PageMenuItem menuItem, Environment activityRootData) {
		EnvironmentSection section = EnvironmentSection.fromOrdinal(menuItem.getPosition());
		BaseEditFragment fragment;

		switch (section) {
		case ENVIRONMENT_INFO:
			fragment = EnvironmentEditFragment.newInstance(activityRootData);
			break;
		case TASKS:
			fragment = EnvironmentEditTaskListFragment.newInstance(activityRootData);
			break;
		default:
			throw new IndexOutOfBoundsException(DataHelper.toStringNullable(section));
		}

		return fragment;
	}

	@Override
	public void goToNewView() {
		EnvironmentSection section = EnvironmentSection.fromOrdinal(getActivePage().getPosition());

		switch (section) {
		case TASKS:
			TaskNewActivity.startActivityFromEnvironment(getContext(), getRootUuid());
			break;
		default:
			throw new IllegalArgumentException(DataHelper.toStringNullable(section));
		}
	}

	private void confirmJurisdictionChange() {
		final ConfirmationDialog confirmationDialog = new ConfirmationDialog(
			this,
			R.string.heading_environment_location_update,
			R.string.confirmation_environment_location_change,
			R.string.yes,
			R.string.no);

		confirmationDialog.setPositiveCallback(() -> {
			saveData(parameter -> NavigationHelper.goToEnvironments(getContext()));
		});
		confirmationDialog.setNegativeCallback(() -> {
		});
		confirmationDialog.show();
	}

	@Override
	public void saveData() {
		final Environment changedEnvironment = getStoredRootEntity();
		EnvironmentEditFragment fragment = (EnvironmentEditFragment) getActiveFragment();
		User currentUser = ConfigProvider.getUser();
		Location location = (Location) fragment.getContentBinding().environmentLocation.getValue();

		final Region currentUserRegion = currentUser.getRegion();
		final Region environmentRegion = location.getRegion();
		final District currentUserDistrict = currentUser.getDistrict();
		final District environmentDistrict = location.getDistrict();

		boolean outsideJurisdiction = (!DataHelper.isSame(changedEnvironment.getReportingUser(), currentUser)
			&& (currentUserRegion != null && !DataHelper.isSame(currentUserRegion, environmentRegion)
				|| currentUserDistrict != null && !DataHelper.isSame(currentUserDistrict, environmentDistrict)));

		if (outsideJurisdiction) {
			confirmJurisdictionChange();
			return;
		}

		saveData(parameter -> goToNextPage());
	}

	private void saveData(final Consumer<Environment> successCallback) {
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
					successCallback.accept(changedEnvironment);
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
