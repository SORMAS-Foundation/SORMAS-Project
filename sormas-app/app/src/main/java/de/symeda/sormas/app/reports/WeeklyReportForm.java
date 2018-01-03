package de.symeda.sormas.app.reports;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.report.WeeklyReport;
import de.symeda.sormas.app.backend.report.WeeklyReportEntry;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.FieldHelper;
import de.symeda.sormas.app.component.SpinnerField;
import de.symeda.sormas.app.databinding.WeeklyReportFragmentLayoutBinding;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.FormTab;
import de.symeda.sormas.app.util.Item;
import de.symeda.sormas.app.util.SyncCallback;

/**
 * Created by Mate Strysewske on 08.09.2017.
 * TODO actually this is not a form, but a component
 */
public class WeeklyReportForm extends FormTab {

    private WeeklyReportFragmentLayoutBinding binding;
    protected Tracker tracker;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.weekly_report_fragment_layout, container, false);

        // report button is bound to UserRight.WEEKLYREPORT_CREATE
        editOrCreateUserRight = null;

        SormasApplication application = (SormasApplication) getActivity().getApplication();
        tracker = application.getDefaultTracker();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());

        // init years ...
        final List yearsList = DataUtils.toItems(DateHelper.getYearsToNow());
        yearsList.add(Calendar.getInstance().get(Calendar.YEAR) + 1);

        FieldHelper.initSpinnerField(binding.weeklyReportYear, DataUtils.toItems(DateHelper.getYearsToNow()), new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                updateWeeksList();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        if (binding.weeklyReportYear.getValue() == null) {
            binding.weeklyReportYear.setValue(DateHelper.getPreviousEpiWeek(new Date()).getYear());
        }

        // .. and weeks
        final List initialWeeksList = DataUtils.toItems(DateHelper.createIntegerEpiWeeksList((int)binding.weeklyReportYear.getValue()));

        FieldHelper.initSpinnerField(binding.weeklyReportWeek, initialWeeksList, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                updateByEpiWeek();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        if (binding.weeklyReportWeek.getValue() == null) {
            binding.weeklyReportWeek.setValue(DateHelper.getPreviousEpiWeek(new Date()).getWeek());
        }

        binding.weeklyReportYear.setCaption(getResources().getString(R.string.caption_weekly_report_year));
        binding.weeklyReportWeek.setCaption(getResources().getString(R.string.caption_weekly_report_week));
        binding.weeklyReportStart.setCaption(getResources().getString(R.string.caption_weekly_report_start));
        binding.weeklyReportEnd.setCaption(getResources().getString(R.string.caption_weekly_report_end));
        binding.weeklyReportReportDate.setCaption(getResources().getString(R.string.caption_weekly_report_report_date));

        binding.weeklyReportShowLastWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date now = new Date();
                EpiWeek epiWeek = DateHelper.getPreviousEpiWeek(now);
                binding.weeklyReportYear.setValue(epiWeek.getYear());
                updateWeeksList(); // necessary because weeklyReportYear.onItemSelected is not called immediately
                binding.weeklyReportWeek.setValue(epiWeek.getWeek());
            }
        });
        binding.weeklyReportShowThisWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar epiCalendar = DateHelper.getEpiCalendar();
                Date now = new Date();
                EpiWeek epiWeek = DateHelper.getEpiWeek(now);
                binding.weeklyReportYear.setValue(epiWeek.getYear());
                updateWeeksList(); // necessary because weeklyReportYear.onItemSelected is not called immediately
                binding.weeklyReportWeek.setValue(epiWeek.getWeek());
            }
        });

        boolean canReport = ConfigProvider.getUser().hasUserRight(UserRight.WEEKLYREPORT_CREATE);
        if (canReport) {
            binding.weeklyReportConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EpiWeek epiWeek = new EpiWeek((int) binding.weeklyReportYear.getValue(), (int) binding.weeklyReportWeek.getValue());
                    try {
                        DatabaseHelper.getWeeklyReportDao().create(epiWeek);

                        if (RetroProvider.isConnected()) {
                            SynchronizeDataAsync.callWithProgressDialog(SynchronizeDataAsync.SyncMode.ChangesOnly, WeeklyReportForm.this.getContext(), new SyncCallback() {
                                @Override
                                public void call(boolean syncFailed, String syncFailedMessage) {
                                    if (syncFailed) {
                                        Snackbar.make(getActivity().findViewById(R.id.base_layout), getActivity().getString(R.string.snackbar_weekly_report_sync_confirmed), Snackbar.LENGTH_LONG).show();
                                    } else {
                                        Snackbar.make(getActivity().findViewById(R.id.base_layout), getActivity().getString(R.string.snackbar_weekly_report_confirmed), Snackbar.LENGTH_LONG).show();
                                    }
                                    reloadFragment();
                                }
                            });
                        } else {
                            Snackbar.make(getActivity().findViewById(R.id.base_layout), getActivity().getString(R.string.snackbar_weekly_report_confirmed), Snackbar.LENGTH_LONG).show();
                            reloadFragment();
                        }
                    } catch (DaoException e) {
                        Log.e(getClass().getName(), "Error while trying to create weekly report", e);
                        Log.e(getClass().getName(), "- root cause: ", ErrorReportingHelper.getRootCause(e));
                        Snackbar.make(getActivity().findViewById(R.id.base_layout), getActivity().getString(R.string.snackbar_weekly_report_error), Snackbar.LENGTH_LONG).show();
                        ErrorReportingHelper.sendCaughtException(tracker, e, null, true);
                    }
                }
            });
        }
    }

    private void updateByEpiWeek() {

        Object selectedWeekValue = binding.weeklyReportWeek.getValue();
        if (selectedWeekValue != null) {

            EpiWeek epiWeek = new EpiWeek((int) binding.weeklyReportYear.getValue(), (int) binding.weeklyReportWeek.getValue());
            final User user = ConfigProvider.getUser();

            if (user.hasUserRole(UserRole.INFORMANT)) {
                WeeklyReport report = DatabaseHelper.getWeeklyReportDao().queryForEpiWeek(epiWeek, user);

                // Epi week shown = this week; table is shown if the report for the last week has been confirmed; no buttons
                if (DateHelper.getEpiWeek(new Date()).equals(epiWeek)) {
                    binding.weeklyReportReportDate.setValue(getActivity().getString(R.string.hint_report_not_yet_submitted));

                    if (DatabaseHelper.getWeeklyReportDao().queryForEpiWeek(DateHelper.getPreviousEpiWeek(epiWeek), user) != null) {
                        setVisibilityForTable(false);
                        showPendingReport(epiWeek, user);
                    } else {
                        setVisibilityForNoReportHint();
                    }
                    // Epi week shown = last week; table is shown, buttons are shown if the report has not been confirmed yet
                } else if (DateHelper.getPreviousEpiWeek(new Date()).equals(epiWeek)) {
                    if (report == null) {
                        setVisibilityForTable(true);
                        binding.weeklyReportReportDate.setValue(getActivity().getString(R.string.hint_report_not_yet_submitted));
                        showPendingReport(epiWeek, user);
                    } else {
                        setVisibilityForTable(false);
                        binding.weeklyReportReportDate.setValue(DateHelper.formatShortDate(report.getReportDateTime()));
                        showWeeklyReport(report, user);
                    }
                    // Epi week shown = any other week; table is shown for dates in the past, 'no data' hint is shown for dates in the future
                } else {
                    if (report == null) {
                        if (DateHelper.isEpiWeekAfter(DateHelper.getEpiWeek(new Date()), epiWeek)) {
                            setVisibilityForNoData();
                            binding.weeklyReportReportDate.setValue(null);
                        } else {
                            setVisibilityForTable(false);
                            binding.weeklyReportReportDate.setValue(getActivity().getString(R.string.hint_report_not_submitted));
                            showPendingReport(epiWeek, user);
                        }
                    } else {
                        setVisibilityForTable(false);
                        binding.weeklyReportReportDate.setValue(DateHelper.formatShortDate(report.getReportDateTime()));
                        showWeeklyReport(report, user);
                    }
                }

                binding.weeklyReportReportDate.setCaption("Confirmation date");
                binding.weeklyReportAddMissing.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((ReportsActivity) getActivity()).showCaseNewView();
                    }
                });
            } else {
                setVisibilityForTable(false);
                binding.weeklyReportReportDate.setCaption("");
                binding.weeklyReportReportDate.setValue(null);
                showWeeklyReportOverview(epiWeek, user);
            }

            binding.weeklyReportStart.setValue(DateHelper.formatShortDate(DateHelper.getEpiWeekStart(epiWeek)));
            binding.weeklyReportEnd.setValue(DateHelper.formatShortDate(DateHelper.getEpiWeekEnd(epiWeek)));
        } else {

            binding.weeklyReportReportDate.setCaption("");
            binding.weeklyReportReportDate.setValue(null);
            binding.weeklyReportStart.setValue(null);
            binding.weeklyReportEnd.setValue(null);
        }
    }

    private void updateWeeksList() {
        if (binding.weeklyReportWeek != null) {
            Object selectedYearValue = binding.weeklyReportYear.getValue();
            List<Item> weeksList;
            if (selectedYearValue != null) {
                weeksList = DataUtils.toItems(DateHelper.createIntegerEpiWeeksList((int) selectedYearValue));
            } else {
                weeksList = Collections.emptyList();
            }
            binding.weeklyReportWeek.setAdapterAndValue(binding.weeklyReportWeek.getValue(), weeksList);
        }
    }

    private void addHeaderRow(User user) {
        TableLayout tableLayout = (TableLayout) getActivity().findViewById(R.id.report_table_layout);
        TableRow row;
        if (user.hasUserRole(UserRole.INFORMANT)) {
            row = (TableRow) LayoutInflater.from(getContext()).inflate(R.layout.weekly_report_informant_table_header_layout, null);
        } else {
            row = (TableRow) LayoutInflater.from(getContext()).inflate(R.layout.weekly_report_officer_table_header_layout, null);
        }
        tableLayout.addView(row);
    }

    private void addInformantTableRow(Disease disease, int numberOfCases, boolean even) {
        TableLayout tableLayout = (TableLayout) getActivity().findViewById(R.id.report_table_layout);
        TableRow row = (TableRow) LayoutInflater.from(getContext()).inflate(R.layout.weekly_report_informant_table_row_layout, null);
        TextView diseaseView = (TextView) row.findViewById(R.id.disease_row);
        diseaseView.setText(disease.toString());
        diseaseView.setBackground(even ? ContextCompat.getDrawable(getContext(), R.drawable.table_border) : ContextCompat.getDrawable(getContext(), R.drawable.table_border_light_grey));
        TextView caseNumberView = (TextView) row.findViewById(R.id.informant_case_number_row);
        caseNumberView.setText(String.valueOf(numberOfCases));
        caseNumberView.setBackground(even ? ContextCompat.getDrawable(getContext(), R.drawable.table_border) : ContextCompat.getDrawable(getContext(), R.drawable.table_border_light_grey));
        tableLayout.addView(row);
    }

    private void addOfficerTableRow(Facility healthFacility, User informant, int numberOfCases, boolean confirmed, boolean even) {
        TableLayout tableLayout = (TableLayout) getActivity().findViewById(R.id.report_table_layout);
        TableRow row = (TableRow) LayoutInflater.from(getContext()).inflate(R.layout.weekly_report_officer_table_row_layout, null);
        TextView facilityInformantView = (TextView) row.findViewById(R.id.facility_informant_row);
        facilityInformantView.setText((healthFacility != null ? healthFacility.toString() + " | " : "") + informant.toString());
        facilityInformantView.setBackground(even ? ContextCompat.getDrawable(getContext(), R.drawable.table_border) : ContextCompat.getDrawable(getContext(), R.drawable.table_border_light_grey));
        TextView caseNumberView = (TextView) row.findViewById(R.id.officer_case_number_row);
        caseNumberView.setText(String.valueOf(numberOfCases));
        caseNumberView.setBackground(even ? ContextCompat.getDrawable(getContext(), R.drawable.table_border) : ContextCompat.getDrawable(getContext(), R.drawable.table_border_light_grey));
        TextView confirmedView = (TextView) row.findViewById(R.id.confirmed_row);
        confirmedView.setText(confirmed ? getActivity().getString(R.string.action_yes) : getActivity().getString(R.string.action_no));
        confirmedView.setBackground(even ? ContextCompat.getDrawable(getContext(), R.drawable.table_border) : ContextCompat.getDrawable(getContext(), R.drawable.table_border_light_grey));
        tableLayout.addView(row);
    }

    private void showPendingReport(EpiWeek epiWeek, User user) {
        binding.reportTableLayout.removeAllViews();
        addHeaderRow(user);
        int rowNumber = 1;
        for (Disease disease : Disease.values()) {
            int numberOfCases = DatabaseHelper.getCaseDao().getNumberOfCasesForEpiWeekAndDisease(epiWeek, disease, user);
            addInformantTableRow(disease, numberOfCases, rowNumber % 2 == 0);
            rowNumber++;
        }
    }

    private void showWeeklyReport(WeeklyReport report, User user) {
        binding.reportTableLayout.removeAllViews();
        addHeaderRow(user);
        int rowNumber = 1;
        if (user.hasUserRole(UserRole.INFORMANT)) {
            for (WeeklyReportEntry entry : DatabaseHelper.getWeeklyReportEntryDao().getAllByWeeklyReport(report)) {
                addInformantTableRow(entry.getDisease(), entry.getNumberOfCases(), rowNumber % 2 == 0);
                rowNumber++;
            }
        }
    }

    private void showWeeklyReportOverview(EpiWeek epiWeek, User user) {
        binding.reportTableLayout.removeAllViews();
        addHeaderRow(user);
        int rowNumber = 1;
        List<User> informants = DatabaseHelper.getUserDao().getByDistrictAndRole(user.getDistrict(), UserRole.INFORMANT, User.HEALTH_FACILITY + "_id");
        for (User informant : informants) {
            WeeklyReport report = DatabaseHelper.getWeeklyReportDao().queryForEpiWeek(epiWeek, informant);
            int numberOfCases = DatabaseHelper.getCaseDao().getNumberOfCasesForEpiWeek(epiWeek, informant);
            addOfficerTableRow(informant.getHealthFacility(), informant, numberOfCases, report != null, rowNumber % 2 == 0);
            rowNumber++;
        }
    }

    private void setVisibilityForTable(boolean showButtons) {
        binding.reportTableLayout.setVisibility(View.VISIBLE);
        binding.weeklyReportNoReport.setVisibility(View.GONE);
        binding.weeklyReportNoData.setVisibility(View.GONE);

        if (showButtons && !ConfigProvider.getUser().hasUserRight(UserRight.WEEKLYREPORT_CREATE)) {
            showButtons = false;
        }
        binding.weeklyReportAddMissing.setVisibility(showButtons ? View.VISIBLE : View.GONE);
        binding.weeklyReportConfirm.setVisibility(showButtons ? View.VISIBLE : View.GONE);
    }

    private void setVisibilityForNoReportHint() {
        binding.reportTableLayout.setVisibility(View.GONE);
        binding.weeklyReportNoReport.setVisibility(View.VISIBLE);
        binding.weeklyReportNoData.setVisibility(View.GONE);
        binding.weeklyReportAddMissing.setVisibility(View.GONE);
        binding.weeklyReportConfirm.setVisibility(View.GONE);
    }

    private void setVisibilityForNoData() {
        binding.reportTableLayout.setVisibility(View.GONE);
        binding.weeklyReportNoReport.setVisibility(View.GONE);
        binding.weeklyReportNoData.setVisibility(View.VISIBLE);
        binding.weeklyReportAddMissing.setVisibility(View.GONE);
        binding.weeklyReportConfirm.setVisibility(View.GONE);
    }

    @Override
    public AbstractDomainObject getData() {
        return null;
    }

}
