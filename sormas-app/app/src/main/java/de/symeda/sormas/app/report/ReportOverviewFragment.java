/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.report;

import android.view.View;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.report.WeeklyReport;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentReportWeeklyLayoutBinding;

import static android.view.View.GONE;

public class ReportOverviewFragment extends ReportFragment {

    public static ReportOverviewFragment newInstance() {
        return newInstance(ReportOverviewFragment.class, null);
    }

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.headline_informant_reports);
    }

    @Override
    protected void onAfterLayoutBinding(FragmentReportWeeklyLayoutBinding contentBinding) {
        super.onAfterLayoutBinding(contentBinding);
        contentBinding.submitReport.setVisibility(GONE);
    }

    @Override
    protected void showReportData(User user, EpiWeek epiWeek, EpiWeekFilterOption filterOption) {
        if (epiWeek == null || DateHelper.isEpiWeekAfter(DateHelper.getEpiWeek(new Date()), epiWeek)
                || !user.hasUserRole(UserRole.SURVEILLANCE_OFFICER)) {
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

        loadReportTask = new DefaultAsyncTask(getContext()) {
            @Override
            public void onPreExecute() {
                getBaseReportActivity().showPreloader();
            }

            @Override
            public void doInBackground(TaskResultHolder resultHolder) {
                List<WeeklyReportOverviewListItem> list = new ArrayList<>();

                // confirmed reports
                List<User> informants = DatabaseHelper.getUserDao().getInformantsByAssociatedOfficer(ConfigProvider.getUser());
                for (User informant : informants) {
                    WeeklyReport report = DatabaseHelper.getWeeklyReportDao().queryByEpiWeekAndUser(getEpiWeek(), informant);
                    if (report != null) {
                        list.add(new WeeklyReportOverviewListItem(report.getHealthFacility(), report.getCommunity(), report.getReportingUser(), report.getTotalNumberOfCases(), report.getReportDateTime()));
                    } else {
                        int numberOfCases = DatabaseHelper.getCaseDao().getNumberOfCasesForEpiWeek(getEpiWeek(), informant);
                        list.add(new WeeklyReportOverviewListItem(informant.getHealthFacility(), informant.getCommunity(), informant, numberOfCases, null));
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
