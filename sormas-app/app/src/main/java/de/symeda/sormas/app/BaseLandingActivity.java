package de.symeda.sormas.app;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import de.symeda.sormas.app.component.menu.PageMenuItem;

public abstract class BaseLandingActivity extends BaseActivity {

    public static final String TAG = BaseLandingActivity.class.getSimpleName();

    private CharSequence mainViewTitle;

    private BaseLandingFragment activeFragment;
    private MenuItem saveMenu = null;
    private MenuItem newMenu = null;

    protected void onCreateInner(Bundle savedInstanceState) {
    }

    protected boolean isSubActivitiy() {
        return true;
    }

    @Override
    protected boolean openPage(PageMenuItem menuItem) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void onResume() {
        super.onResume();
        replaceFragment(buildLandingFragment());
    }

    public abstract BaseLandingFragment buildLandingFragment();

    public BaseLandingFragment getActiveFragment() {
        return activeFragment;
    }

    public void replaceFragment(BaseLandingFragment f) {
        BaseFragment previousFragment = activeFragment;
        activeFragment = f;

        if (activeFragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout);
            ft.replace(R.id.fragment_frame, activeFragment);
            ft.commit();
        }

        processActionbarMenu();

        updateStatusFrame();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.landing_action_menu, menu);

        saveMenu = menu.findItem(R.id.action_save);
        newMenu = menu.findItem(R.id.action_new);

        processActionbarMenu();

        return true;
    }

    private void processActionbarMenu() {
        if (activeFragment == null)
            return;

        if (saveMenu != null)
            saveMenu.setVisible(activeFragment.isShowSaveAction());

        if (newMenu != null)
            newMenu.setVisible(activeFragment.isShowNewAction());
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

    public MenuItem getSaveMenu() {
        return saveMenu;
    }

    public MenuItem getNewMenu() {
        return newMenu;
    }
}
