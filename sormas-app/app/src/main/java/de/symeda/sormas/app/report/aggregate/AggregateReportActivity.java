package de.symeda.sormas.app.report.aggregate;

import android.os.Bundle;

import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.component.menu.PageMenuItem;

public class AggregateReportActivity extends BaseActivity {

    @Override
    protected void onCreateInner(Bundle savedInstanceState) {

    }

    @Override
    protected boolean isSubActivity() {
        return false;
    }

    @Override
    public Enum getPageStatus() {
        return null;
    }

    @Override
    protected int getActivityTitle() {
        return 0;
    }

    @Override
    protected boolean openPage(PageMenuItem menuItem) {
        return false;
    }

}
