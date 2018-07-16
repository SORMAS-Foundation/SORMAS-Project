package de.symeda.sormas.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.IUpdateSubHeadingTitle;
import de.symeda.sormas.app.util.ConstantHelper;

public abstract class BaseReportActivity extends BaseActivity implements NotificationContext, IUpdateSubHeadingTitle {

    private final static String TAG = BaseReportActivity.class.getSimpleName();

    private View rootView;
    private View applicationTitleBar = null;
    private BaseReportFragment activeFragment = null;
    private TextView subHeadingActivityTitle;

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        initializeActivity(savedInstanceState);
    }

    @Override
    protected boolean isSubActivitiy() {
        return false;
    }

    public void setSubHeadingTitle(String title) {
        String t = (title == null)? "" : title;

        if (subHeadingActivityTitle != null)
            subHeadingActivityTitle.setText(t);
    }

    @Override
    public void updateSubHeadingTitle() {
        setSubHeadingTitle(activeFragment.getSubHeadingTitle());
    }

    @Override
    public void updateSubHeadingTitle(int titleResId) {
        setSubHeadingTitle(getApplicationContext().getResources().getString(titleResId));
    }

    @Override
    public void updateSubHeadingTitle(String title) {
        setSubHeadingTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_action_bar, menu);

        processActionbarMenu();

        return true;
    }

    @Override
    protected int getRootActivityLayout() {
        return R.layout.activity_root_with_title_layout;
    }

    @Override
    public View getRootView() {
        return rootView;
    }



    protected abstract void initializeActivity(Bundle arguments);

    public abstract BaseReportFragment getActiveFragment();


    protected void onCreateInner(Bundle savedInstanceState) {
        rootView = findViewById(R.id.base_layout);
        subHeadingActivityTitle = (TextView)findViewById(R.id.subHeadingActivityTitle);

        if (showTitleBar()) {
            applicationTitleBar = findViewById(R.id.applicationTitleBar);

            if (applicationTitleBar != null)
                applicationTitleBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        replaceFragment(getActiveFragment());
    }

    protected boolean showTitleBar() {
        return true;
    }

    protected static <TActivity extends BaseActivity> void goToActivity(Context fromActivity, Class<TActivity> toActivity) {
        Intent intent = new Intent(fromActivity, toActivity);
        fromActivity.startActivity(intent);
    }

    private void replaceFragment(BaseReportFragment f) {
        BaseFragment previousFragment = activeFragment;
        activeFragment = f;

        if (activeFragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if (activeFragment.getArguments() == null)
                activeFragment.setArguments(getIntent().getBundleExtra(ConstantHelper.ARG_NAVIGATION_CAPSULE_INTENT_DATA));

            ft.setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout);
            ft.replace(R.id.fragment_frame, activeFragment);
            if (previousFragment != null) {
                ft.addToBackStack(null);
            }
            ft.commit();
        }
    }

    private void processActionbarMenu() {
        if (activeFragment == null)
            return;
    }


}
