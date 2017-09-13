package de.symeda.sormas.app.reports;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.report.WeeklyReport;
import de.symeda.sormas.app.backend.report.WeeklyReportEntry;
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
 */
public class WeeklyReportForm extends FormTab {

    private WeeklyReportFragmentLayoutBinding binding;
    protected Tracker tracker;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.weekly_report_fragment_layout, container, false);

        SormasApplication application = (SormasApplication) getActivity().getApplication();
        tracker = application.getDefaultTracker();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

//        binding.weeklyReportInfo.setText(Html.fromHtml(String.format(getResources().getString(R.string.hint_weekly_report_info), "<b>10</b>", "<b>01/01/2001</b>", "<b>07/01/2001</b>")));

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());

        final List yearsList = DataUtils.toItems(DateHelper.getYearsToNow());
        yearsList.add(Calendar.getInstance().get(Calendar.YEAR) + 1);
        final List initialWeeksList = DataUtils.toItems(createWeeksList(Calendar.getInstance().get(Calendar.YEAR)));

        FieldHelper.initSpinnerField(binding.weeklyReportYear, DataUtils.toItems(DateHelper.getYearsToNow()), new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SpinnerField weekSpinner = binding.weeklyReportWeek;
                Object selectedValue = binding.weeklyReportYear.getValue();
                if (weekSpinner != null) {
                    // TODO select week 37 on startup, remove second empty item from list
                    List<Item> weeksList;
                    if (selectedValue != null) {
                        weeksList = DataUtils.toItems(createWeeksList((int) selectedValue));
                    } else {
                        weeksList = Collections.emptyList();
                    }
                    weekSpinner.setAdapterAndValue(weekSpinner.getValue(), weeksList);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        FieldHelper.initSpinnerField(binding.weeklyReportWeek, initialWeeksList, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                EpiWeek epiWeek = new EpiWeek((int) binding.weeklyReportYear.getValue(), (int) binding.weeklyReportWeek.getValue());
                WeeklyReport report = DatabaseHelper.getWeeklyReportDao().queryForEpiWeek(epiWeek);

                if (DateHelper.getEpiWeek(new Date()).equals(epiWeek)) {
                    binding.weeklyReportAddMissing.setEnabled(false);
                    binding.weeklyReportConfirm.setEnabled(false);
                    binding.reportTableLayout.setVisibility(View.VISIBLE);
                    binding.weeklyReportNoReport.setVisibility(View.GONE);
                    binding.weeklyReportReportDate.setValue(getActivity().getString(R.string.hint_report_not_submitted));
                    showPendingReport(epiWeek);
                } else if (DateHelper.getPreviousEpiWeek(new Date()).equals(epiWeek)) {
                    binding.reportTableLayout.setVisibility(View.VISIBLE);
                    binding.weeklyReportNoReport.setVisibility(View.GONE);
                    if (report == null) {
                        binding.weeklyReportAddMissing.setEnabled(true);
                        binding.weeklyReportConfirm.setEnabled(true);
                        binding.weeklyReportReportDate.setValue(Html.toHtml(Html.fromHtml("<b>" + getActivity().getString(R.string.hint_report_not_submitted) + "</b>")));
                        showPendingReport(epiWeek);
                    } else {
                        binding.weeklyReportAddMissing.setEnabled(false);
                        binding.weeklyReportConfirm.setEnabled(false);
                        binding.weeklyReportReportDate.setValue(DateHelper.formatShortDate(report.getReportDateTime()));
                        showWeeklyReport(report);
                    }
                } else {
                    binding.weeklyReportAddMissing.setEnabled(false);
                    binding.weeklyReportConfirm.setEnabled(false);
                    if (report == null) {
                        binding.reportTableLayout.setVisibility(View.GONE);
                        binding.weeklyReportNoReport.setVisibility(View.VISIBLE);
                        binding.weeklyReportReportDate.setValue(null);
                    } else {
                        binding.reportTableLayout.setVisibility(View.VISIBLE);
                        binding.weeklyReportNoReport.setVisibility(View.GONE);
                        binding.weeklyReportReportDate.setValue(DateHelper.formatShortDate(report.getReportDateTime()));
                        showWeeklyReport(report);
                    }
                }

                binding.weeklyReportStart.setValue(DateHelper.formatShortDate(DateHelper.getEpiWeekStart(epiWeek)));
                binding.weeklyReportEnd.setValue(DateHelper.formatShortDate(DateHelper.getEpiWeekEnd(epiWeek)));
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        if (binding.weeklyReportYear.getValue() == null) {
            binding.weeklyReportYear.setValue(DateHelper.getPreviousEpiWeek(new Date()).getYear());
        }
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
                EpiWeek epiWeek = DateHelper.getPreviousEpiWeek(new Date());
                binding.weeklyReportYear.setValue(epiWeek.getYear());
                binding.weeklyReportWeek.setValue(epiWeek.getWeek());
            }
        });
        binding.weeklyReportShowThisWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EpiWeek epiWeek = DateHelper.getEpiWeek(new Date());
                binding.weeklyReportYear.setValue(epiWeek.getYear());
                binding.weeklyReportWeek.setValue(epiWeek.getWeek());
            }
        });
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

    private List createWeeksList(int year) {
        Calendar calendar = DateHelper.getEpiCalendar();
        calendar.set(year, 0, 1);
        List weeksList = new ArrayList<>();
        for (int week = 1; week <= calendar.getActualMaximum(Calendar.WEEK_OF_YEAR); week++) {
            weeksList.add(week);
        }
        return weeksList;
    }

    private void addHeaderRow() {
        TableLayout tableLayout = (TableLayout) getActivity().findViewById(R.id.report_table_layout);
        TableRow row = (TableRow) LayoutInflater.from(getContext()).inflate(R.layout.weekly_report_table_header_layout, null);
        tableLayout.addView(row);
    }

    private void addTableRow(Disease disease, int numberOfCases, boolean even) {
        TableLayout tableLayout = (TableLayout) getActivity().findViewById(R.id.report_table_layout);
        TableRow row = (TableRow) LayoutInflater.from(getContext()).inflate(R.layout.weekly_report_table_row_layout, null);
        TextView diseaseView = (TextView) row.findViewById(R.id.disease_row);
        diseaseView.setText(disease.toString());
        diseaseView.setBackground(even ? ContextCompat.getDrawable(getContext(), R.drawable.table_border) : ContextCompat.getDrawable(getContext(), R.drawable.table_border_light_grey));
        TextView caseNumberView = (TextView) row.findViewById(R.id.case_number_row);
        caseNumberView.setText(String.valueOf(numberOfCases));
        caseNumberView.setBackground(even ? ContextCompat.getDrawable(getContext(), R.drawable.table_border) : ContextCompat.getDrawable(getContext(), R.drawable.table_border_light_grey));
        tableLayout.addView(row);
    }

    private void showPendingReport(EpiWeek epiWeek) {
        binding.reportTableLayout.removeAllViews();
        addHeaderRow();
        int rowNumber = 1;
        for (Disease disease : Disease.values()) {
            int numberOfCases = DatabaseHelper.getCaseDao().getNumberOfCasesForEpiWeekAndDisease(epiWeek, disease);
            addTableRow(disease, numberOfCases, rowNumber % 2 == 0);
            rowNumber++;
        }
    }

    private void showWeeklyReport(WeeklyReport report) {
        binding.reportTableLayout.removeAllViews();
        addHeaderRow();
        int rowNumber = 1;
        for (WeeklyReportEntry entry : DatabaseHelper.getWeeklyReportEntryDao().getAllByWeeklyReport(report)) {
            addTableRow(entry.getDisease(), entry.getNumberOfCases(), rowNumber % 2 == 0);
            rowNumber++;
        }
    }

    @Override
    public AbstractDomainObject getData() {
        return null;
    }

}
