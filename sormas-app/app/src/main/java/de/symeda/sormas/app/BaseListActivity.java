package de.symeda.sormas.app;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.TextView;

import de.symeda.sormas.app.component.menu.PageMenuControl;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.core.IUpdateSubHeadingTitle;
import de.symeda.sormas.app.util.Bundler;

public abstract class BaseListActivity extends BaseActivity implements IUpdateSubHeadingTitle, PageMenuControl.NotificationCountChangingListener {

    private TextView subHeadingListActivityTitle;
    private MenuItem newMenu = null;
    private BaseListFragment activeFragment = null;

    public static Bundler buildBundle(Enum listFilter) {
        return BaseActivity.buildBundle(listFilter.ordinal());
    }

    @Override
    protected boolean isSubActivitiy() {
        return false;
    }

    @Override
    public Enum getPageStatus() {
        return null;
    }

    protected void onCreateInner(Bundle savedInstanceState) {
        subHeadingListActivityTitle = (TextView) findViewById(R.id.subHeadingActivityTitle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        replaceFragment(buildListFragment(getActivePage()));
    }

    public BaseListFragment getActiveFragment() {
        return activeFragment;
    }

    public void replaceFragment(BaseListFragment f) {
        BaseListFragment previousFragment = activeFragment;
        activeFragment = f;

        if (activeFragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout);
            ft.replace(R.id.fragment_frame, activeFragment);
            if (previousFragment != null) {
                ft.addToBackStack(null);
            }
            ft.commit();
        }

        updateStatusFrame();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_action_bar, menu);

        newMenu = menu.findItem(R.id.action_new);

        processActionbarMenu();

        return true;
    }

    protected boolean isEntryCreateAllowed() {
        return false;
    }

    private void processActionbarMenu() {
        if (activeFragment == null)
            return;

        if (newMenu != null)
            newMenu.setVisible(isEntryCreateAllowed());
    }

    public MenuItem getNewMenu() {
        return newMenu;
    }

    public void setSubHeadingTitle(String title) {
        String t = (title == null) ? "" : title;

        if (subHeadingListActivityTitle != null)
            subHeadingListActivityTitle.setText(t);
    }

    @Override
    public void updateSubHeadingTitle() {
        throw new UnsupportedOperationException();
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
    protected int getRootActivityLayout() {
        return R.layout.activity_root_list_layout;
    }

    public abstract int onNotificationCountChangingAsync(AdapterView<?> parent, PageMenuItem menuItem, int position);

    @Override
    protected boolean openPage(PageMenuItem menuItem) {
        BaseListFragment newActiveFragment = buildListFragment(menuItem); //, storedListData
        if (newActiveFragment == null)
            return false;
        replaceFragment(newActiveFragment);
        return true;
    }

    protected abstract BaseListFragment buildListFragment(PageMenuItem menuItem);
}
