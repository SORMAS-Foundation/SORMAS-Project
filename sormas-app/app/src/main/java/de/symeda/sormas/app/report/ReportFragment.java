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

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.app.BaseReportFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.report.WeeklyReport;
import de.symeda.sormas.app.backend.report.WeeklyReportEntry;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.databinding.FragmentReportWeeklyLayoutBinding;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.SyncCallback;

import static android.view.View.GONE;

public class ReportFragment extends BaseReportFragment<FragmentReportWeeklyLayoutBinding> {

    private AsyncTask confirmReportTask;
    protected AsyncTask loadReportTask;
    private List<Item> yearList;

    protected LinearLayoutManager linearLayoutManager;
    private WeeklyReportAdapter weeklyReportAdapter;
    private WeeklyReportAdapter pendingReportAdapter;
    protected WeeklyReportOverviewAdapter weeklyReportOverviewAdapter;

    private String reportDate = "";
    private EpiWeek lastUpdateEpiWeek;

    private FragmentReportWeeklyLayoutBinding contentBinding;

    public static ReportFragment newInstance() {
        return newInstance(ReportFragment.class, null);
    }

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        String defaultValue = r.getString(R.string.hint_report_not_submitted);
        boolean isInformant = ConfigProvider.getUser().hasUserRole(UserRole.HOSPITAL_INFORMANT)
                || ConfigProvider.getUser().hasUserRole(UserRole.COMMUNITY_INFORMANT);
        if (DataHelper.isNullOrEmpty(reportDate)) {
            if (isInformant) {
                return defaultValue;
            } else {
                return null;
            }
        } else {
            String format = isInformant ? r.getString(R.string.caption_confirmation_date) : r.getString(R.string.caption_report_date);
            return String.format(format, reportDate);
        }
    }

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        yearList = DataUtils.toItems(DateHelper.getYearsToNow());
    }

    @Override
    protected void onLayoutBinding(final FragmentReportWeeklyLayoutBinding contentBinding) {
        this.contentBinding = contentBinding;
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        contentBinding.setReportFilterOptionClass(EpiWeekFilterOption.class);
        setupControls();
    }

    @Override
    protected void onAfterLayoutBinding(FragmentReportWeeklyLayoutBinding contentBinding) {
        super.onAfterLayoutBinding(contentBinding);
        EpiWeek epiWeek = ((ReportActivity) getActivity()).getEpiWeek();
        if (epiWeek == null) {
            contentBinding.reportSelector.setValue(EpiWeekFilterOption.LAST_WEEK);
        } else {
            setEpiWeek(epiWeek);
            updateByEpiWeek();
        }
    }

    private void setupControls() {
        EpiWeek epiWeek = DateHelper.getPreviousEpiWeek(new Date());

        contentBinding.weeklyReportYear.initializeSpinner(yearList, epiWeek.getYear(), new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                Integer year = (Integer) field.getValue();
                if (year != null) {
                    contentBinding.weeklyReportEpiWeek.setSpinnerData(DataUtils.toItems(DateHelper.createEpiWeekList(year)));
                } else {
                    contentBinding.weeklyReportEpiWeek.setSpinnerData(null);
                }
            }
        });

        contentBinding.weeklyReportEpiWeek.initializeSpinner(DataUtils.toItems(DateHelper.createEpiWeekList(epiWeek.getYear())),
                epiWeek, new ValueChangeListener() {
                    @Override
                    public void onChange(ControlPropertyField field) {
                        ((ReportActivity) getActivity()).setEpiWeek((EpiWeek) field.getValue());
                        updateByEpiWeek();
                    }
                });

        contentBinding.reportSelector.addValueChangedListener(new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                EpiWeekFilterOption filter = (EpiWeekFilterOption) field.getValue();
                if (filter != null) {
                    switch (filter) {
                        case LAST_WEEK:
                            setEpiWeek(DateHelper.getPreviousEpiWeek(new Date()));
                            contentBinding.weeklyReportYear.setEnabled(false);
                            contentBinding.weeklyReportEpiWeek.setEnabled(false);
                            break;
                        case THIS_WEEK:
                            setEpiWeek(DateHelper.getEpiWeek(new Date()));
                            contentBinding.weeklyReportYear.setEnabled(false);
                            contentBinding.weeklyReportEpiWeek.setEnabled(false);
                            break;
                        case SPECIFY_WEEK:
                            contentBinding.weeklyReportYear.setEnabled(true);
                            contentBinding.weeklyReportEpiWeek.setEnabled(true);
                            break;
                        default:
                            throw new IllegalArgumentException(filter.toString());
                    }
                }
            }
        });

        contentBinding.submitReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmReportTask = new DefaultAsyncTask(getContext()) {

                    @Override
                    public void onPreExecute() {
                        getBaseActivity().showPreloader();
                    }

                    @Override
                    public void doInBackground(TaskResultHolder resultHolder) throws DaoException {
                        WeeklyReport weeklyReport = DatabaseHelper.getWeeklyReportDao().build(getEpiWeek());
                        DatabaseHelper.getWeeklyReportDao().saveAndSnapshot(weeklyReport);
                    }

                    @Override
                    protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
                        super.onPostExecute(taskResult);
                        getBaseActivity().hidePreloader();

                        if (!taskResult.getResultStatus().isSuccess()) {
                            NotificationHelper.showNotification((NotificationContext) getActivity(), NotificationType.ERROR, taskResult.getResultStatus().getMessage());
                            return;
                        }

                        if (RetroProvider.isConnected()) {
                            SynchronizeDataAsync.callWithProgressDialog(SynchronizeDataAsync.SyncMode.Changes, getActivity(), new SyncCallback() {
                                @Override
                                public void call(boolean syncFailed, String syncFailedMessage) {
                                    if (syncFailed) {
                                        NotificationHelper.showNotification((NotificationContext) getActivity(), NotificationType.WARNING, R.string.snackbar_weekly_report_sync_confirmed);
                                    } else {
                                        NotificationHelper.showNotification((NotificationContext) getActivity(), NotificationType.SUCCESS, R.string.snackbar_weekly_report_confirmed);
                                    }

                                    contentBinding.submitReport.setVisibility(GONE);
                                }
                            });
                        } else {
                            NotificationHelper.showNotification((NotificationContext) getActivity(), NotificationType.SUCCESS, R.string.snackbar_weekly_report_confirmed);
                        }
                    }
                }.executeOnThreadPool();
            }
        });
    }

    public void setEpiWeek(EpiWeek epiWeek) {
        if (!DataHelper.equal(contentBinding.weeklyReportYear.getValue(), epiWeek.getYear())) {
            contentBinding.weeklyReportYear.setValue(epiWeek.getYear());
        }
        if (!DataHelper.equal(contentBinding.weeklyReportEpiWeek.getValue(), epiWeek)) {
            contentBinding.weeklyReportEpiWeek.setValue(epiWeek);
        }

        ((ReportActivity) getActivity()).setEpiWeek(epiWeek);
    }

    public EpiWeek getEpiWeek() {
        return (EpiWeek) contentBinding.weeklyReportEpiWeek.getValue();
    }

    private void updateByEpiWeek() {
        EpiWeek epiWeek = getEpiWeek();
        // TODO this is only necessary because the field value changes are triggered multiple times
        if (DataHelper.equal(epiWeek, lastUpdateEpiWeek)) {
            return;
        }
        lastUpdateEpiWeek = epiWeek;

        EpiWeekFilterOption filterOption;
        if (epiWeek == null) {
            filterOption = EpiWeekFilterOption.SPECIFY_WEEK;
        } else if (epiWeek.equals(DateHelper.getPreviousEpiWeek(new Date()))) {
            filterOption = EpiWeekFilterOption.LAST_WEEK;
        } else if (epiWeek.equals(DateHelper.getEpiWeek(new Date()))) {
            filterOption = EpiWeekFilterOption.THIS_WEEK;
        } else {
            filterOption = EpiWeekFilterOption.SPECIFY_WEEK;
        }
        if (!DataHelper.equal(contentBinding.reportSelector.getValue(), filterOption)) {
            contentBinding.reportSelector.setValue(filterOption);
        }

        final User user = ConfigProvider.getUser();
        showReportData(user, epiWeek, filterOption);

        getSubHeadingHandler().updateSubHeadingTitle(getSubHeadingTitle());
    }

    protected void showReportData(User user, EpiWeek epiWeek, EpiWeekFilterOption filterOption) {
        if (epiWeek == null || !ConfigProvider.hasUserRight(UserRight.WEEKLYREPORT_CREATE)) {
            setVisibilityForNoData();
        } else {
            WeeklyReport weeklyReport = DatabaseHelper.getWeeklyReportDao().queryByEpiWeekAndUser(epiWeek, user);

            if (EpiWeekFilterOption.THIS_WEEK.equals(filterOption)) {
                // table is shown if the report for the last week has been confirmed; no buttons
                reportDate = getResources().getString(R.string.hint_report_not_submitted);
                setVisibilityForTable(false);
                showPendingReport();
            } else if (EpiWeekFilterOption.LAST_WEEK.equals(filterOption)) {
                // table is shown, buttons are shown if the report has not been confirmed yet
                if (weeklyReport == null) {
                    setVisibilityForTable(true);
                    reportDate = getResources().getString(R.string.hint_report_not_submitted);
                    showPendingReport();
                } else {
                    setVisibilityForTable(false);
                    reportDate = DateHelper.formatLocalDate(weeklyReport.getReportDateTime());
                    showWeeklyReport(weeklyReport);
                }
            } else { // any other week;
                if (weeklyReport == null) {
                    if (DateHelper.isEpiWeekAfter(DateHelper.getEpiWeek(new Date()), epiWeek)) {
                        // 'no data' hint is shown for dates in the future
                        setVisibilityForNoData();
                        reportDate = "";
                        showNoReport();
                    } else {
                        // table is shown for dates in the past
                        setVisibilityForTable(true);
                        reportDate = getResources().getString(R.string.hint_report_not_submitted);
                        showPendingReport();
                    }
                } else {
                    setVisibilityForTable(false);
                    reportDate = DateHelper.formatLocalDate(weeklyReport.getReportDateTime());
                    showWeeklyReport(weeklyReport);
                }
            }
        }
    }

    protected void setVisibilityForTable(boolean showButtons) {
        contentBinding.reportContentFrame.setVisibility(View.VISIBLE);
        contentBinding.noWeeklyReportData.setVisibility(GONE);

        if (showButtons && !ConfigProvider.hasUserRight(UserRight.WEEKLYREPORT_CREATE)) {
            showButtons = false;
        }
        contentBinding.submitReport.setVisibility(showButtons ? View.VISIBLE : GONE);
    }

    protected void setVisibilityForNoData() {
        contentBinding.noWeeklyReportData.setVisibility(View.VISIBLE);
        contentBinding.reportContentFrame.setVisibility(GONE);
        contentBinding.submitReport.setVisibility(GONE);
    }

    private void showNoReport() {
        getContentBinding().reportContentFrame.setVisibility(GONE);
    }

    private void showPendingReport() {
        if (ConfigProvider.getUser() == null) {
            return;
        }

        if (ConfigProvider.hasUserRight(UserRight.WEEKLYREPORT_CREATE)) {
            loadReportTask = new DefaultAsyncTask(getContext()) {
                @Override
                public void onPreExecute() {
                    getBaseActivity().showPreloader();
                }

                @Override
                public void doInBackground(TaskResultHolder resultHolder) {
                    User user = ConfigProvider.getUser();
                    List<WeeklyReportListItem> list = new ArrayList<>();
                    List<User> informants = null;
                    if (user.hasUserRole(UserRole.SURVEILLANCE_OFFICER)) {
                        informants = DatabaseHelper.getUserDao().getInformantsByAssociatedOfficer(ConfigProvider.getUser());
                    }

                    for (Disease disease : Disease.values()) {
                        int numberOfCases = DatabaseHelper.getCaseDao().getNumberOfCasesForEpiWeekAndDisease(getEpiWeek(), disease, user);

                        if (informants != null) {
                            for (User informant : informants) {
                                numberOfCases += DatabaseHelper.getCaseDao().getNumberOfCasesForEpiWeekAndDisease(getEpiWeek(), disease, informant);
                            }
                        }

                        list.add(new WeeklyReportListItem(disease, numberOfCases));
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

                    List<WeeklyReportListItem> list = new ArrayList<>();
                    ITaskResultHolderIterator otherIterator = taskResult.getResult().forOther().iterator();

                    if (otherIterator.hasNext())
                        list = otherIterator.next();

                    pendingReportAdapter = new WeeklyReportAdapter(getContext(), list);
                    getContentBinding().reportContent.setLayoutManager(linearLayoutManager);
                    getContentBinding().reportContent.setAdapter(pendingReportAdapter);
                    getContentBinding().reportContentFrame.setVisibility(View.VISIBLE);
                    pendingReportAdapter.notifyDataSetChanged();
                }
            }.executeOnThreadPool();
        }
    }

    /**
     * Show a confirmed weekly report of the user
     */
    private void showWeeklyReport(final WeeklyReport weeklyReport) {
        if (ConfigProvider.getUser() == null)
            return;

        loadReportTask = new DefaultAsyncTask(getContext()) {

            @Override
            public void onPreExecute() {
                getBaseActivity().showPreloader();
            }

            @Override
            public void doInBackground(TaskResultHolder resultHolder) {
                if (weeklyReport != null) {
                    List<WeeklyReportListItem> list = new ArrayList<>();
                    for (WeeklyReportEntry entry : DatabaseHelper.getWeeklyReportEntryDao().getByWeeklyReport(weeklyReport)) {
                        list.add(new WeeklyReportListItem(entry.getDisease(), entry.getNumberOfCases()));
                    }
                    resultHolder.forOther().add(list);
                }
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
                super.onPostExecute(taskResult);
                getBaseActivity().hidePreloader();

                if (taskResult.getResult() == null) {
                    return;
                }

                List<WeeklyReportListItem> list = new ArrayList<>();
                ITaskResultHolderIterator otherIterator = taskResult.getResult().forOther().iterator();

                if (otherIterator.hasNext())
                    list = otherIterator.next();

                weeklyReportAdapter = new WeeklyReportAdapter(getContext(), list);
                getContentBinding().reportContent.setLayoutManager(linearLayoutManager);
                getContentBinding().reportContent.setAdapter(weeklyReportAdapter);
                getContentBinding().reportContentFrame.setVisibility(View.VISIBLE);
                weeklyReportAdapter.notifyDataSetChanged();
            }
        }.executeOnThreadPool();
    }

    @Override
    protected int getReportLayout() {
        return R.layout.fragment_report_weekly_layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (confirmReportTask != null && !confirmReportTask.isCancelled())
            confirmReportTask.cancel(true);

        if (loadReportTask != null && !loadReportTask.isCancelled())
            loadReportTask.cancel(true);
    }
}
