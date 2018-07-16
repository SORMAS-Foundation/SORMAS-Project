package de.symeda.sormas.app.report;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;

import de.symeda.sormas.app.BaseReportActivity;
import de.symeda.sormas.app.BaseReportFragment;
import de.symeda.sormas.app.R;

public class ReportLandingActivity extends BaseReportActivity {

    private final static String TAG = ReportLandingActivity.class.getSimpleName();

    private BaseReportFragment activeFragment = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initializeActivity(Bundle arguments) {

    }

    @Override
    public BaseReportFragment getActiveFragment() {
        if (activeFragment == null) {
            activeFragment = ReportFragment.newInstance();
        }
        return activeFragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        return true;
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
