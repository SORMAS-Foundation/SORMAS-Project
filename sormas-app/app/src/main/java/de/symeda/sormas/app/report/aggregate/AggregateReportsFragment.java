package de.symeda.sormas.app.report.aggregate;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableArrayList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.app.BaseReportFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.report.AggregateReport;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.dialog.ConfirmationDialog;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.databinding.FragmentReportsAggregateLayoutBinding;
import de.symeda.sormas.app.databinding.RowReportAggregateLayoutBinding;
import de.symeda.sormas.app.report.EpiWeekFilterOption;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.DateFormatHelper;

public class AggregateReportsFragment extends BaseReportFragment<FragmentReportsAggregateLayoutBinding> {

    private EpiWeek lastUpdateEpiWeek;

    private AsyncTask confirmCaseNumbersTask;
    private FragmentReportsAggregateLayoutBinding contentBinding;

    private List<AggregateReport> reports = new ArrayList<>();

    public static AggregateReportsFragment newInstance() {
        return newInstance(AggregateReportsFragment.class, null);
    }

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        // Not required
    }

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.hint_case_numbers_not_submitted);
    }

    @Override
    protected void onLayoutBinding(final FragmentReportsAggregateLayoutBinding contentBinding) {
        this.contentBinding = contentBinding;

        contentBinding.setReportFilterOptionClass(EpiWeekFilterOption.class);
        setupControls();
    }

    @Override
    protected void onAfterLayoutBinding(FragmentReportsAggregateLayoutBinding contentBinding) {
        super.onAfterLayoutBinding(contentBinding);
        contentBinding.reportSelector.setValue(EpiWeekFilterOption.THIS_WEEK);
    }

    private void setupControls() {
        EpiWeek epiWeek = DateHelper.getPreviousEpiWeek(new Date());

        contentBinding.aggregateReportsYear.initializeSpinner(DataUtils.toItems(DateHelper.getYearsToNow()), epiWeek.getYear(), field -> {
            Integer year = (Integer) field.getValue();
            if (year != null) {
                contentBinding.aggregateReportsWeek.setSpinnerData(DataUtils.toItems(DateHelper.createEpiWeekList(year)));
            } else {
                contentBinding.aggregateReportsWeek.setSpinnerData(null);
            }
        });

        contentBinding.aggregateReportsWeek.initializeSpinner(DataUtils.toItems(DateHelper.createEpiWeekList(epiWeek.getYear())), epiWeek, field -> {
            updateByEpiWeek();
        });

        contentBinding.reportSelector.addValueChangedListener(field -> {
            EpiWeekFilterOption filter = (EpiWeekFilterOption) field.getValue();
            if (filter != null) {
                switch (filter) {
                    case LAST_WEEK:
                        setEpiWeek(DateHelper.getPreviousEpiWeek(new Date()));
                        contentBinding.aggregateReportsYear.setEnabled(false);
                        contentBinding.aggregateReportsWeek.setEnabled(false);
                        break;
                    case THIS_WEEK:
                        setEpiWeek(DateHelper.getEpiWeek(new Date()));
                        contentBinding.aggregateReportsYear.setEnabled(false);
                        contentBinding.aggregateReportsWeek.setEnabled(false);
                        break;
                    case SPECIFY_WEEK:
                        contentBinding.aggregateReportsYear.setEnabled(true);
                        contentBinding.aggregateReportsWeek.setEnabled(true);
                        break;
                    default:
                        throw new IllegalArgumentException(filter.toString());
                }
            }
        });

        contentBinding.submitReport.setOnClickListener(view -> showSubmitCaseNumbersConfirmationDialog());
    }

    public void setEpiWeek(EpiWeek epiWeek) {
        if (!DataHelper.equal(contentBinding.aggregateReportsYear.getValue(), epiWeek.getYear())) {
            contentBinding.aggregateReportsYear.setValue(epiWeek.getYear());
        }
        if (!DataHelper.equal(contentBinding.aggregateReportsWeek.getValue(), epiWeek)) {
            contentBinding.aggregateReportsWeek.setValue(epiWeek);
        }
    }

    private void updateByEpiWeek() {
        EpiWeek epiWeek = (EpiWeek) contentBinding.aggregateReportsWeek.getValue();

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

        if (epiWeek != null) {
            showReportData();
        }
    }

    private void showReportData() {
        User user = ConfigProvider.getUser();
        EpiWeek epiWeek = (EpiWeek) contentBinding.aggregateReportsWeek.getValue();

        reports = DatabaseHelper.getAggregateReportDao().getReportsByEpiWeekAndUser(epiWeek, user);

        contentBinding.reportContent.removeAllViews();

        Date latestLocalChangeDate = null;

        for (AggregateReport report : reports) {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            RowReportAggregateLayoutBinding binding = DataBindingUtil.inflate(inflater, R.layout.row_report_aggregate_layout, contentBinding.reportContent, true);
            binding.setData(report);

            if (latestLocalChangeDate == null || (report.getLocalChangeDate() != null && latestLocalChangeDate.before(report.getLocalChangeDate()))) {
                latestLocalChangeDate = report.getLocalChangeDate();
            }
        }

        Resources r = getResources();
        if (latestLocalChangeDate == null) {
            getSubHeadingHandler().updateSubHeadingTitle(r.getString(R.string.hint_case_numbers_not_submitted));
        } else {
            getSubHeadingHandler().updateSubHeadingTitle(String.format(r.getString(R.string.caption_latest_submission), DateFormatHelper.formatLocalDateTime(latestLocalChangeDate)));
        }
    }

    private void showSubmitCaseNumbersConfirmationDialog() {
        final ConfirmationDialog confirmationDialog = new ConfirmationDialog(getActivity(),
                R.string.heading_confirmation_dialog,
                R.string.info_add_cases_before_report_submit,
                R.string.action_submit_case_numbers,
                R.string.action_cancel);

        confirmationDialog.setPositiveCallback(() -> {
                confirmCaseNumbersTask = new DefaultAsyncTask(getContext()) {

                    @Override
                    public void onPreExecute() {
                        getBaseActivity().showPreloader();
                    }

                    @Override
                    public void doInBackground(TaskResultHolder resultHolder) throws DaoException {
                        for (AggregateReport report : reports) {
                            // Don't save if a generated report has no case numbers
                            if (report.getLocalChangeDate() == null &&
                                    (report.getNewCases() == null || report.getNewCases() == 0)
                                    &&  (report.getLabConfirmations() == null || report.getLabConfirmations() == 0)
                                    && (report.getDeaths() == null || report.getDeaths() == 0)) {
                                continue;
                            }

                            if (report.getNewCases() == null) {
                                report.setNewCases(0);
                            }
                            if (report.getLabConfirmations() == null) {
                                report.setLabConfirmations(0);
                            }
                            if (report.getDeaths() == null) {
                                report.setDeaths(0);
                            }

                            DatabaseHelper.getAggregateReportDao().saveAndSnapshot(report);
                        }
                    }

                    @Override
                    protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
                        super.onPostExecute(taskResult);
                        getBaseActivity().hidePreloader();
                        Intent intent = new Intent(getContext(), AggregateReportsActivity.class);
                        getContext().startActivity(intent);

                        if (!taskResult.getResultStatus().isSuccess()) {
                            NotificationHelper.showNotification((NotificationContext) getActivity(), NotificationType.ERROR, taskResult.getResultStatus().getMessage());
                            return;
                        }

                        NotificationHelper.showNotification((NotificationContext) getActivity(), NotificationType.SUCCESS, R.string.message_case_numbers_submitted);
                    }
                }.executeOnThreadPool();
            });

        confirmationDialog.show();
    }

    @Override
    protected int getReportLayout() {
        return R.layout.fragment_reports_aggregate_layout;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (confirmCaseNumbersTask != null && !confirmCaseNumbersTask.isCancelled()) {
            confirmCaseNumbersTask.cancel(true);
        }
    }

    private ObservableArrayList makeObservable(List<AggregateReport> reports) {
        ObservableArrayList<AggregateReport> newList = new ObservableArrayList<>();
        newList.addAll(reports);
        return newList;
    }

}
