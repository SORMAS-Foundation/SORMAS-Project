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

package de.symeda.sormas.app.pathogentest.edit;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;
import static de.symeda.sormas.app.core.notification.NotificationType.WARNING;

import android.content.Context;
import android.os.AsyncTask;
import android.view.Menu;

import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.PathogenTest;
import de.symeda.sormas.app.component.dialog.ConfirmationDialog;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;

public class PathogenTestEditActivity extends BaseEditActivity<PathogenTest> {

	public static final String TAG = PathogenTestEditActivity.class.getSimpleName();

	private AsyncTask saveTask;

	public static void startActivity(Context context, String rootUuid) {
		BaseActivity.startActivity(context, PathogenTestEditActivity.class, buildBundle(rootUuid));
	}

	@Override
	protected PathogenTest queryRootEntity(String recordUuid) {
		return DatabaseHelper.getSampleTestDao().queryUuid(recordUuid);
	}

	@Override
	protected PathogenTest buildRootEntity() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Enum getPageStatus() {
		return null;
	}

	@Override
	protected BaseEditFragment buildEditFragment(PageMenuItem menuItem, PathogenTest activityRootData) {
		return PathogenTestEditFragment.newInstance(activityRootData);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getSaveMenu().setTitle(R.string.action_save_pathogen_test);

		return true;
	}

	@Override
	public void saveData() {
		if (saveTask != null) {
			NotificationHelper.showNotification(this, WARNING, getString(R.string.message_already_saving));
			return; // don't save multiple times
		}

		final PathogenTest pathogenTestToSave = getStoredRootEntity();

		try {
			FragmentValidator.validate(getContext(), getActiveFragment().getContentBinding());
		} catch (ValidationException e) {
			NotificationHelper.showNotification(this, ERROR, e.getMessage());
			return;
		}

		saveTask = new SavingAsyncTask(getRootView(), pathogenTestToSave) {

			@Override
			public void doInBackground(TaskResultHolder resultHolder) throws DaoException {
				DatabaseHelper.getSampleTestDao().saveAndSnapshot(pathogenTestToSave);
			}

			@Override
			protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
				super.onPostExecute(taskResult);

				if (taskResult.getResultStatus().isSuccess()) {
					if (Boolean.TRUE == pathogenTestToSave.getTestResultVerified()
						&& pathogenTestToSave.getTestedDisease() == pathogenTestToSave.getSample().getAssociatedCase().getDisease()
						&& pathogenTestToSave.getTestResult() != pathogenTestToSave.getSample().getPathogenTestResult()) {
						final ConfirmationDialog confirmationDialog = new ConfirmationDialog(
							getActiveActivity(),
							R.string.heading_change_laboratory_result,
							R.string.message_change_final_laboratory_result,
							R.string.yes,
							R.string.no);

						confirmationDialog.setPositiveCallback(() -> {
							pathogenTestToSave.getSample().setPathogenTestResult(pathogenTestToSave.getTestResult());
							try {
								DatabaseHelper.getSampleDao().saveAndSnapshot(pathogenTestToSave.getSample());
							} catch (DaoException e) {
								NotificationHelper.showNotification(
									getActiveActivity().getRootView(),
									ERROR,
									String.format(
										getActiveActivity().getResources().getString(R.string.message_save_error),
										pathogenTestToSave.getEntityName()));
							} finally {
								finish();
							}
						});
						confirmationDialog.setNegativeCallback(() -> {
							finish();
						});

						confirmationDialog.show();
					} else {
						finish();
					}
				} else {
					onResume(); // reload data
				}
				saveTask = null;
			}
		}.executeOnThreadPool();
	}

	@Override
	protected int getActivityTitle() {
		return R.string.heading_pathogen_test_edit;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (saveTask != null && !saveTask.isCancelled())
			saveTask.cancel(true);
	}
}
