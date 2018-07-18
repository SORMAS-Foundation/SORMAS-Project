package de.symeda.sormas.app.report;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;

import de.symeda.sormas.app.BaseReportActivity;
import de.symeda.sormas.app.BaseReportFragment;
import de.symeda.sormas.app.R;

public class ReportLandingActivity extends BaseReportActivity {

    private final static String TAG = ReportLandingActivity.class.getSimpleName();

    @Override
    public BaseReportFragment buildReportFragment() {
        return ReportFragment.newInstance();
    }

    @Override
    protected int getActivityTitle() {
        return R.string.main_menu_reports;
    }

    @Override
    protected boolean showTitleBar() {
        return true;
    }
}
