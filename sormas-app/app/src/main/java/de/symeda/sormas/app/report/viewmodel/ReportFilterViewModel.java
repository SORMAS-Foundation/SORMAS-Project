package de.symeda.sormas.app.report.viewmodel;

import java.util.Calendar;
import java.util.Date;

import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;

/**
 * Created by Orson on 24/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public class ReportFilterViewModel {

    private ReportFilterOption mOption;
    private Integer mYear;
    private Integer mWeek;
    private EpiWeek mEpiWeek;


    public ReportFilterViewModel() {
        this.mOption = ReportFilterOption.SPECIFY_WEEK;
        this.mYear = Calendar.getInstance().get(Calendar.YEAR);
        this.mWeek = DateHelper.getEpiWeek(new Date()).getWeek();
        this.mEpiWeek = new EpiWeek(mYear, mWeek);
    }

    public ReportFilterOption getOption() {
        return mOption;
    }

    public void setOption(ReportFilterOption option) {
        this.mOption = option;
    }

    public Integer getYear() {
        return mYear;
    }

    public void setYear(Integer year) {
        this.mYear = year;
        this.mEpiWeek = new EpiWeek(year, mWeek);
    }

    public Integer getWeek() {
        return mWeek;
    }

    public void setWeek(Integer week) {
        this.mWeek = week;
        this.mEpiWeek = new EpiWeek(mYear, week);
    }

    public EpiWeek getEpiWeek() {
        return mEpiWeek;
    }

    public String getStartDate() {
        return DateHelper.formatLocalShortDate(DateHelper.getEpiWeekStart(mEpiWeek));
    }

    public String getEndDate() {
        return DateHelper.formatLocalShortDate(DateHelper.getEpiWeekEnd(mEpiWeek));
    }
}

