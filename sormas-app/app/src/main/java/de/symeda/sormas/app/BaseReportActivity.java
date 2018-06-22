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

/**
 * Created by Orson on 24/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public abstract class BaseReportActivity extends AbstractSormasActivity implements NotificationContext, IUpdateSubHeadingTitle {

    private final static String TAG = BaseReportActivity.class.getSimpleName();

    private View rootView;
    private View fragmentFrame = null;
    private View applicationTitleBar = null;
    private BaseReportActivityFragment activeFragment = null;
    private TextView subHeadingActivityTitle;

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        initializeActivity(savedInstanceState);
    }

    @Override
    protected boolean setHomeAsUpIndicator() {
        return true;
    }

    @Override
    public void showFragmentView() {
        if (fragmentFrame != null)
            fragmentFrame.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideFragmentView() {
        if (fragmentFrame != null)
            fragmentFrame.setVisibility(View.GONE);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
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

    public abstract BaseReportActivityFragment getActiveFragment();


    protected void initializeBaseActivity(Bundle savedInstanceState) {
        rootView = findViewById(R.id.base_layout);
        subHeadingActivityTitle = (TextView)findViewById(R.id.subHeadingActivityTitle);

        if (showTitleBar()) {
            applicationTitleBar = findViewById(R.id.applicationTitleBar);

            if (applicationTitleBar != null)
                applicationTitleBar.setVisibility(View.VISIBLE);
        }

        fragmentFrame = findViewById(R.id.fragment_frame);
        if (fragmentFrame != null) {
            if (savedInstanceState == null) {
                replaceFragment(getActiveFragment());
            }
        }
    }

    protected boolean showTitleBar() {
        return true;
    }

    protected static <TActivity extends AbstractSormasActivity> void goToActivity(Context fromActivity, Class<TActivity> toActivity) {
        Intent intent = new Intent(fromActivity, toActivity);
        fromActivity.startActivity(intent);
    }

    private void replaceFragment(BaseReportActivityFragment f) {
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
