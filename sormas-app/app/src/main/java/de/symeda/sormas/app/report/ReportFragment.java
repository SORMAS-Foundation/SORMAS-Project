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
import de.symeda.sormas.app.caze.edit.CaseNewActivity;
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
import de.symeda.sormas.app.report.adapter.PendingReportAdapter;
import de.symeda.sormas.app.report.adapter.WeeklyReportAdapter;
import de.symeda.sormas.app.report.adapter.WeeklyReportOverviewAdapter;
import de.symeda.sormas.app.report.viewmodel.EpiWeekFilterOption;
import de.symeda.sormas.app.report.viewmodel.PendingReportViewModel;
import de.symeda.sormas.app.report.viewmodel.WeeklyReportOverviewViewModel;
import de.symeda.sormas.app.report.viewmodel.WeeklyReportViewModel;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.SyncCallback;

public class ReportFragment extends BaseReportFragment<FragmentReportWeeklyLayoutBinding> {

    private AsyncTask confirmReportTask;
    private AsyncTask loadReportTask;
    private List<Item> mYearList;

    private LinearLayoutManager mLinearLayoutManager;
    private WeeklyReportAdapter mWeeklyReportAdapter;
    private PendingReportAdapter mPendingReportAdapter;
    private WeeklyReportOverviewAdapter mWeeklyReportOverviewAdapter;

    private String mReportDate = "";
    private EpiWeek lastUpdateEpiWeek;

    private FragmentReportWeeklyLayoutBinding contentBinding;


    public static ReportFragment newInstance() {
        return newInstance(ReportFragment.class, null);
    }

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        String defaultValue = r.getString(R.string.hint_report_not_submitted);
        boolean isInformant = ConfigProvider.getUser().hasUserRole(UserRole.HOSPITAL_INFORMANT);
        if (DataHelper.isNullOrEmpty(mReportDate)) {
            if (isInformant) {
                return defaultValue;
            } else {
                return null;
            }
        } else {
            String format = isInformant ? r.getString(R.string.caption_confirmation_date) : r.getString(R.string.caption_report_date);
            return String.format(format, mReportDate);
        }
    }

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        mYearList = DataUtils.toItems(DateHelper.getYearsToNow());
    }

    @Override
    protected void onLayoutBinding(final FragmentReportWeeklyLayoutBinding contentBinding) {

        this.contentBinding = contentBinding;
        mLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        contentBinding.setReportFilterOptionClass(EpiWeekFilterOption.class);
        setupControls();
    }

    @Override
    protected void onAfterLayoutBinding(FragmentReportWeeklyLayoutBinding contentBinding) {
        super.onAfterLayoutBinding(contentBinding);
        contentBinding.reportSelector.setValue(EpiWeekFilterOption.LAST_WEEK);
    }

    private void setupControls() {

        EpiWeek epiWeek = DateHelper.getPreviousEpiWeek(new Date());

        contentBinding.weeklyReportYear.initializeSpinner(mYearList, epiWeek.getYear(), new ValueChangeListener() {
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
                            break;
                        case THIS_WEEK:
                            setEpiWeek(DateHelper.getEpiWeek(new Date()));
                            break;
                        case SPECIFY_WEEK:
                            // do nothing
                            break;
                        default:
                            throw new IllegalArgumentException(filter.toString());
                    }
                }
            }
        });

        contentBinding.addMissingCase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CaseNewActivity.startActivity(getContext());
            }
        });

        contentBinding.confirmReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                confirmReportTask = new DefaultAsyncTask(getContext()) {

                    @Override
                    public void onPreExecute() {
                        getBaseActivity().showPreloader();
                    }

                    @Override
                    public void doInBackground(TaskResultHolder resultHolder) throws DaoException {
                        DatabaseHelper.getWeeklyReportDao().create(getEpiWeek());
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
                                        NotificationHelper.showNotification((NotificationContext) getActivity(), NotificationType.SUCCESS, R.string.snackbar_weekly_report_sync_confirmed);
                                    } else {
                                        NotificationHelper.showNotification((NotificationContext) getActivity(), NotificationType.WARNING, R.string.snackbar_weekly_report_confirmed);
                                    }
                                    getActivity().finish();
                                }
                            });
                        } else {
                            NotificationHelper.showNotification((NotificationContext) getActivity(), NotificationType.WARNING, R.string.snackbar_weekly_report_confirmed);
                            getActivity().finish();
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
        if (epiWeek == null) {
            setVisibilityForNoData();
        } else if (user.hasUserRole(UserRole.HOSPITAL_INFORMANT)) {
            WeeklyReport weeklyReport = DatabaseHelper.getWeeklyReportDao().queryForEpiWeek(epiWeek, user);

            if (EpiWeekFilterOption.THIS_WEEK.equals(filterOption)) {
                // table is shown if the report for the last week has been confirmed; no buttons
                mReportDate = getActivity().getString(R.string.hint_report_not_submitted);
                if (DatabaseHelper.getWeeklyReportDao().queryForEpiWeek(DateHelper.getPreviousEpiWeek(epiWeek), user) != null) {
                    setVisibilityForTable(false);
                    showPendingReport();
                } else {
                    setVisibilityForNoReportHint();
                    showNoReport();
                }
            } else if (EpiWeekFilterOption.LAST_WEEK.equals(filterOption)) {
                // table is shown, buttons are shown if the report has not been confirmed yet
                if (weeklyReport == null) {
                    setVisibilityForTable(true);
                    mReportDate = getActivity().getString(R.string.hint_report_not_submitted);
                    showPendingReport();
                } else {
                    setVisibilityForTable(false);
                    mReportDate = DateHelper.formatLocalDate(weeklyReport.getReportDateTime());
                    showWeeklyReport(weeklyReport);
                }
            } else { // any other week;
                if (weeklyReport == null) {
                    if (DateHelper.isEpiWeekAfter(DateHelper.getEpiWeek(new Date()), epiWeek)) {
                        // 'no data' hint is shown for dates in the future
                        setVisibilityForNoData();
                        mReportDate = "";
                        showNoReport();
                    } else {
                        // table is shown for dates in the past
                        setVisibilityForTable(false);
                        mReportDate = getActivity().getString(R.string.hint_report_not_submitted);
                        showPendingReport();
                    }
                } else {
                    setVisibilityForTable(false);
                    mReportDate = DateHelper.formatLocalDate(weeklyReport.getReportDateTime());
                    showWeeklyReport(weeklyReport);
                }
            }
        } else {
            setVisibilityForTable(false);
            mReportDate = "";
            showWeeklyReportOverview();
        }

        getSubHeadingHandler().updateSubHeadingTitle(getSubHeadingTitle());
    }

    private void setVisibilityForTable(boolean showButtons) {
        contentBinding.noWeeklyReportHint.setVisibility(View.GONE);
        contentBinding.noWeeklyReportData.setVisibility(View.GONE);

        if (showButtons && !ConfigProvider.getUser().hasUserRight(UserRight.WEEKLYREPORT_CREATE)) {
            showButtons = false;
        }
        contentBinding.addMissingCase.setVisibility(showButtons ? View.VISIBLE : View.GONE);
        contentBinding.confirmReport.setVisibility(showButtons ? View.VISIBLE : View.GONE);
    }

    private void setVisibilityForNoReportHint() {
        contentBinding.noWeeklyReportHint.setVisibility(View.VISIBLE);
        contentBinding.noWeeklyReportData.setVisibility(View.GONE);
        contentBinding.addMissingCase.setVisibility(View.GONE);
        contentBinding.confirmReport.setVisibility(View.GONE);
    }

    private void setVisibilityForNoData() {
        contentBinding.noWeeklyReportHint.setVisibility(View.GONE);
        contentBinding.noWeeklyReportData.setVisibility(View.VISIBLE);
        contentBinding.addMissingCase.setVisibility(View.GONE);
        contentBinding.confirmReport.setVisibility(View.GONE);
    }

    private void showNoReport() {
        getContentBinding().recyclerViewForList.setVisibility(View.GONE);
    }

    private void showPendingReport() {

        if (ConfigProvider.getUser() == null)
            return;

        if (ConfigProvider.getUser().hasUserRole(UserRole.HOSPITAL_INFORMANT)) {
            loadReportTask = new DefaultAsyncTask(getContext()) {
                @Override
                public void onPreExecute() {
                    getBaseActivity().showPreloader();
                }

                @Override
                public void doInBackground(TaskResultHolder resultHolder) {
                    List<PendingReportViewModel> list = new ArrayList<>();

                    User user = ConfigProvider.getUser();
                    for (Disease disease : Disease.values()) {

                        int numberOfCases = DatabaseHelper.getCaseDao().getNumberOfCasesForEpiWeekAndDisease(getEpiWeek(), disease, user);
                        list.add(new PendingReportViewModel(disease, numberOfCases));
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

                    List<PendingReportViewModel> list = new ArrayList<>();
                    ITaskResultHolderIterator otherIterator = taskResult.getResult().forOther().iterator();

                    if (otherIterator.hasNext())
                        list = otherIterator.next();

                    mPendingReportAdapter = new PendingReportAdapter(getContext(), list);
                    getContentBinding().recyclerViewForList.setLayoutManager(mLinearLayoutManager);
                    getContentBinding().recyclerViewForList.setAdapter(mPendingReportAdapter);
                    getContentBinding().recyclerViewForList.setVisibility(View.VISIBLE);
                    mPendingReportAdapter.notifyDataSetChanged();
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
                    List<WeeklyReportViewModel> list = new ArrayList<>();
                    for (WeeklyReportEntry entry : DatabaseHelper.getWeeklyReportEntryDao().getAllByWeeklyReport(weeklyReport)) {
                        list.add(new WeeklyReportViewModel(entry.getDisease(), entry.getNumberOfCases()));
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

                List<WeeklyReportViewModel> list = new ArrayList<>();
                ITaskResultHolderIterator otherIterator = taskResult.getResult().forOther().iterator();

                if (otherIterator.hasNext())
                    list = otherIterator.next();

                mWeeklyReportAdapter = new WeeklyReportAdapter(getContext(), list);
                getContentBinding().recyclerViewForList.setLayoutManager(mLinearLayoutManager);
                getContentBinding().recyclerViewForList.setAdapter(mWeeklyReportAdapter);
                getContentBinding().recyclerViewForList.setVisibility(View.VISIBLE);
                mWeeklyReportAdapter.notifyDataSetChanged();
            }
        }.executeOnThreadPool();
    }

    /**
     * Shows an overview of all informant reports for the user's district
     */
    private void showWeeklyReportOverview() {

        if (ConfigProvider.getUser() == null)
            return;

        loadReportTask = new DefaultAsyncTask(getContext()) {
            @Override
            public void onPreExecute() {
                getBaseReportActivity().showPreloader();
            }

            @Override
            public void doInBackground(TaskResultHolder resultHolder) {
                List<WeeklyReportOverviewViewModel> list = new ArrayList<>();

                // confirmed reports
                List<WeeklyReport> reports = DatabaseHelper.getWeeklyReportDao().queryByDistrict(getEpiWeek(), ConfigProvider.getUser().getDistrict());
                List<User> doneInformants = new ArrayList<User>();
                for (WeeklyReport report : reports) {
                    list.add(new WeeklyReportOverviewViewModel(report.getHealthFacility(), report.getInformant(), report.getTotalNumberOfCases(), true));
                    doneInformants.add(report.getInformant());
                }

                // get data for unconfirmed
                List<User> informants = DatabaseHelper.getUserDao().getByDistrictAndRole(ConfigProvider.getUser().getDistrict(), UserRole.HOSPITAL_INFORMANT, User.HEALTH_FACILITY + "_id");
                for (User informant : informants) {
                    if (doneInformants.contains(informant))
                        continue;
                    int numberOfCases = DatabaseHelper.getCaseDao().getNumberOfCasesForEpiWeek(getEpiWeek(), informant);
                    list.add(new WeeklyReportOverviewViewModel(informant.getHealthFacility(), informant, numberOfCases, false));
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

                List<WeeklyReportOverviewViewModel> list = new ArrayList<>();
                ITaskResultHolderIterator otherIterator = taskResult.getResult().forOther().iterator();

                if (otherIterator.hasNext())
                    list = otherIterator.next();

                mWeeklyReportOverviewAdapter = new WeeklyReportOverviewAdapter(getContext(), list);
                getContentBinding().recyclerViewForList.setLayoutManager(mLinearLayoutManager);
                getContentBinding().recyclerViewForList.setAdapter(mWeeklyReportOverviewAdapter);
                getContentBinding().recyclerViewForList.setVisibility(View.VISIBLE);
                mWeeklyReportOverviewAdapter.notifyDataSetChanged();
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
