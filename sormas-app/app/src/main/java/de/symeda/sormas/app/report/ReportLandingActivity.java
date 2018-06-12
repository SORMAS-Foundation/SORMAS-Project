package de.symeda.sormas.app.report;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import de.symeda.sormas.app.BaseReportActivity;
import de.symeda.sormas.app.BaseReportActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.util.MenuOptionsHelper;

/**
 * Created by Orson on 21/11/2017.
 */

public class ReportLandingActivity extends BaseReportActivity {

    private final static String TAG = ReportLandingActivity.class.getSimpleName();

    private BaseReportActivityFragment activeFragment = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initializeActivity(Bundle arguments) {

    }

    @Override
    public BaseReportActivityFragment getActiveFragment() {
        if (activeFragment == null) {
            activeFragment = ReportFragment.newInstance(this);
        }

        return activeFragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!MenuOptionsHelper.handleListModuleOptionsItemSelected(this, item))
            return super.onOptionsItemSelected(item);

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
