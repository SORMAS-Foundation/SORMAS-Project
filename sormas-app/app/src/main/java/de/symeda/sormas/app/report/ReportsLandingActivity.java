package de.symeda.sormas.app.report;

import android.os.Bundle;
import android.support.annotation.Nullable;

import de.symeda.sormas.app.BaseLandingActivity;
import de.symeda.sormas.app.BaseLandingActivityFragment;
import de.symeda.sormas.app.R;

/**
 * Created by Orson on 21/11/2017.
 */

public class ReportsLandingActivity extends BaseLandingActivity {

    private BaseLandingActivityFragment activeFragment = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initializeActivity(Bundle arguments) {

    }

    @Override
    public BaseLandingActivityFragment getActiveLandingFragment() throws IllegalAccessException, InstantiationException {
        return null;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.main_menu_reports;
    }
}
