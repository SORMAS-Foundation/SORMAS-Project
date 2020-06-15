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

package de.symeda.sormas.app.report;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.view.View;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.report.WeeklyReport;
import de.symeda.sormas.app.backend.report.WeeklyReportEntry;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentReportWeeklyLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.DiseaseConfigurationCache;

public class ReportOverviewFragment extends ReportFragment {

	public static ReportOverviewFragment newInstance() {
		return newInstance(ReportOverviewFragment.class, null);
	}

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.heading_informant_reports);
	}

	@Override
	protected void onAfterLayoutBinding(FragmentReportWeeklyLayoutBinding contentBinding) {
		super.onAfterLayoutBinding(contentBinding);

		List<Item> diseaseList = DataUtils.toItems(DiseaseConfigurationCache.getInstance().getAllDiseases(true, true, true));
		contentBinding.weeklyReportEntryDisease.initializeSpinner(diseaseList);
		contentBinding.weeklyReportEntryDisease.setVisibility(VISIBLE);
		contentBinding.weeklyReportEntryDisease.addValueChangedListener(new ValueChangeListener() {

			@Override
			public void onChange(ControlPropertyField field) {
				showReportData(); // update
			}
		});

		contentBinding.reportContentHeader.setText(R.string.caption_reported_informant_case_count);
		contentBinding.submitReport.setVisibility(GONE);
	}

	@Override
	protected void showReportData() {

		EpiWeek epiWeek = getEpiWeek();
		if (epiWeek == null
			|| DateHelper.isEpiWeekAfter(DateHelper.getEpiWeek(new Date()), epiWeek)
			|| !ConfigProvider.getUser().hasUserRole(UserRole.SURVEILLANCE_OFFICER)) {
			setVisibilityForNoData();
		} else {
			showWeeklyReportOverview();
		}
	}

	/**
	 * Shows an overview of all informant reports for the user's district
	 */
	private void showWeeklyReportOverview() {
		if (ConfigProvider.getUser() == null) {
			return;
		}

		if (!ConfigProvider.getUser().hasUserRole(UserRole.SURVEILLANCE_OFFICER)) {
			return;
		}

		getContentBinding().noWeeklyReportData.setVisibility(GONE);

		loadReportTask = new DefaultAsyncTask(getContext()) {

			@Override
			public void onPreExecute() {
				getBaseReportActivity().showPreloader();
			}

			@Override
			public void doInBackground(TaskResultHolder resultHolder) {
				List<WeeklyReportOverviewListItem> list = new ArrayList<>();

				Disease disease = (Disease) getContentBinding().weeklyReportEntryDisease.getValue();

				// confirmed reports
				List<User> informants = DatabaseHelper.getUserDao().getInformantsByAssociatedOfficer(ConfigProvider.getUser());
				for (User informant : informants) {
					WeeklyReport report = DatabaseHelper.getWeeklyReportDao().queryByEpiWeekAndUser(getEpiWeek(), informant);
					if (report != null) {
						if (disease != null) {
							WeeklyReportEntry entry = report.getReportEntry(disease);
							list.add(
								new WeeklyReportOverviewListItem(
									report.getHealthFacility(),
									report.getCommunity(),
									report.getReportingUser(),
									entry != null ? entry.getNumberOfCases() : 0,
									report.getReportDateTime()));
						} else {
							list.add(
								new WeeklyReportOverviewListItem(
									report.getHealthFacility(),
									report.getCommunity(),
									report.getReportingUser(),
									report.getTotalNumberOfCases(),
									report.getReportDateTime()));
						}
					} else {
						int numberOfCases;
						if (disease != null) {
							numberOfCases = DatabaseHelper.getCaseDao().getNumberOfCasesForEpiWeekAndDisease(getEpiWeek(), disease, informant);
						} else {
							numberOfCases = DatabaseHelper.getCaseDao().getNumberOfCasesForEpiWeek(getEpiWeek(), informant);
						}
						list.add(
							new WeeklyReportOverviewListItem(
								informant.getHealthFacility(),
								informant.getCommunity(),
								informant,
								numberOfCases,
								null));
					}
				}

				resultHolder.forOther().add(list);
			}

			@Override
			protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
				super.onPostExecute(taskResult);
				getBaseActivity().hidePreloader();

				if (taskResult.getResult() == null) {
					return;
				}

				List<WeeklyReportOverviewListItem> list = new ArrayList<>();
				ITaskResultHolderIterator otherIterator = taskResult.getResult().forOther().iterator();

				if (otherIterator.hasNext())
					list = otherIterator.next();

				weeklyReportOverviewAdapter = new WeeklyReportOverviewAdapter(getContext(), list);
				getContentBinding().reportContent.setLayoutManager(linearLayoutManager);
				getContentBinding().reportContent.setAdapter(weeklyReportOverviewAdapter);
				getContentBinding().reportContentFrame.setVisibility(View.VISIBLE);
				weeklyReportOverviewAdapter.notifyDataSetChanged();
			}
		}.executeOnThreadPool();
	}
}
