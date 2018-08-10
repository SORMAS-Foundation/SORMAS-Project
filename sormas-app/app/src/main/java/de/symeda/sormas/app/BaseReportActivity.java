package de.symeda.sormas.app;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.core.IUpdateSubHeadingTitle;

public abstract class BaseReportActivity extends BaseActivity implements IUpdateSubHeadingTitle {

    private final static String TAG = BaseReportActivity.class.getSimpleName();

    private View applicationTitleBar = null;
    private BaseReportFragment activeFragment = null;
    private TextView subHeadingActivityTitle;

    @Override
    protected boolean isSubActivitiy() {
        return false;
    }

    public void setSubHeadingTitle(String title) {
        String t = (title == null) ? "" : title;

        if (subHeadingActivityTitle != null) {
            if (!DataHelper.isNullOrEmpty(title)) {
                subHeadingActivityTitle.setText(title);
                subHeadingActivityTitle.setVisibility(View.VISIBLE);
            } else {
                subHeadingActivityTitle.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected boolean openPage(PageMenuItem menuItem) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateSubHeadingTitle() {
        setSubHeadingTitle(activeFragment.getSubHeadingTitle());
    }

    @Override
    public void updateSubHeadingTitle(String title) {
        setSubHeadingTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dashboard_action_menu, menu);

        processActionbarMenu();

        return true;
    }

    @Override
    protected int getRootActivityLayout() {
        return R.layout.activity_root_with_title_layout;
    }

    public BaseReportFragment getActiveFragment() {
        return activeFragment;
    }

    protected void onCreateInner(Bundle savedInstanceState) {
        subHeadingActivityTitle = (TextView) findViewById(R.id.subHeadingActivityTitle);

        if (showTitleBar()) {
            applicationTitleBar = findViewById(R.id.applicationTitleBar);

            if (applicationTitleBar != null)
                applicationTitleBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        replaceFragment(buildReportFragment());
    }

    protected abstract BaseReportFragment buildReportFragment();

    protected boolean showTitleBar() {
        return true;
    }

    private void replaceFragment(BaseReportFragment f) {
        BaseFragment previousFragment = activeFragment;
        activeFragment = f;

        if (activeFragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout);
            ft.replace(R.id.fragment_frame, activeFragment);
            ft.commit();
        }

        updateStatusFrame();
    }

    private void processActionbarMenu() {
        if (activeFragment == null)
            return;
    }
}
