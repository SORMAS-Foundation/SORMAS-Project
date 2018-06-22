package de.symeda.sormas.app;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import de.symeda.sormas.app.core.NotificationContext;

/**
 * Created by Orson on 03/12/2017.
 */

public abstract class BaseLandingActivity extends AbstractSormasActivity implements NotificationContext {

    public static final String TAG = BaseLandingActivity.class.getSimpleName();

    private View rootView;
    private CharSequence mainViewTitle;


    private View fragmentFrame = null;
    private BaseLandingActivityFragment activeFragment;
    private MenuItem newMenu = null;

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        initializeActivity(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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

    protected void initializeBaseActivity(Bundle savedInstanceState) {
        rootView = findViewById(R.id.base_layout);
        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getIntent().getExtras();
        initializeActivity(arguments);

        /*if (setHomeAsUpIndicator())
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_blue_36dp);

        setupDrawer(navigationView);*/

        fragmentFrame = findViewById(R.id.fragment_frame);
        if (fragmentFrame != null) {
            if (savedInstanceState == null) {
                replaceFragment(getActiveLandingFragment());
            }
        }
    }

    protected abstract void initializeActivity(Bundle arguments);

    public abstract BaseLandingActivityFragment getActiveLandingFragment();

    public void replaceFragment(BaseLandingActivityFragment f) {
        BaseFragment previousFragment = activeFragment;
        activeFragment = f;

        if (activeFragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            if (activeFragment.getArguments() == null) {
                activeFragment.setArguments(getIntent().getExtras());
            }

            ft.setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout);
            ft.replace(R.id.fragment_frame, activeFragment);
            if (previousFragment != null) {
                ft.addToBackStack(null);
            }
            ft.commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (activeFragment != null)
            activeFragment.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //outState.putInt(KEY_PAGE, currentTab);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.landing_action_bar, menu);

        newMenu = menu.findItem(R.id.action_new);

        processActionbarMenu();

        return true;
    }

    private void processActionbarMenu() {
        if (activeFragment == null)
            return;

        if (newMenu != null)
            newMenu.setVisible(activeFragment.showNewAction());
    }

    public MenuItem getNewMenu() {
        return newMenu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*if (menuDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title) {
        mainViewTitle = title;
        String userRole = "";
        /*if (ConfigProvider.getUser()!=null && ConfigProvider.getUser().getUserRole() !=null) {
            userRole = " - " + ConfigProvider.getUser().getUserRole().toShortString();
        }*/
        getSupportActionBar().setTitle(mainViewTitle + userRole);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public View getRootView() {
        return rootView;
    }
}
