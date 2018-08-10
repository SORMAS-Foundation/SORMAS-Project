package de.symeda.sormas.app.report;

import android.content.Context;

import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseReportActivity;
import de.symeda.sormas.app.BaseReportFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.caze.read.CaseReadActivity;

public class ReportActivity extends BaseReportActivity {

    private final static String TAG = ReportActivity.class.getSimpleName();

    public static void startActivity(Context context) {
        BaseActivity.startActivity(context, ReportActivity.class, buildBundle(0));
    }

    @Override
    public BaseReportFragment buildReportFragment() {
        return ReportFragment.newInstance();
    }

    @Override
    public Enum getPageStatus() {
        return null;
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
