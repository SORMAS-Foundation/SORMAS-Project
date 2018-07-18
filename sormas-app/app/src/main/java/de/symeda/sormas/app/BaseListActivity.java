package de.symeda.sormas.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.TextView;

import de.symeda.sormas.app.component.menu.PageMenuControl;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.core.IListNavigationCapsule;
import de.symeda.sormas.app.core.IUpdateSubHeadingTitle;
import de.symeda.sormas.app.core.SearchBy;
import de.symeda.sormas.app.core.enumeration.IStatusElaborator;
import de.symeda.sormas.app.util.ConstantHelper;

public abstract class BaseListActivity extends BaseActivity implements IUpdateSubHeadingTitle, PageMenuControl.NotificationCountChangingListener {

    private TextView subHeadingListActivityTitle;
    private MenuItem newMenu = null;
    private BaseListFragment activeFragment = null;

    @Override
    protected boolean isSubActivitiy() {
        return false;
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

    protected String getRecordUuidArg(Bundle arguments) {
        String result = null;
        if (arguments != null && !arguments.isEmpty()) {
            if (arguments.containsKey(ConstantHelper.KEY_DATA_UUID)) {
                result = (String) arguments.getString(ConstantHelper.KEY_DATA_UUID);
            }
        }

        return result;
    }

    protected SearchBy getSearchStrategyArg(Bundle arguments) {
        SearchBy e = null;
        if (arguments != null && !arguments.isEmpty()) {
            if (arguments.containsKey(ConstantHelper.ARG_SEARCH_STRATEGY)) {
                e = (SearchBy) arguments.getSerializable(ConstantHelper.ARG_SEARCH_STRATEGY);
            }
        }

        return e;
    }

    protected void saveRecordUuidState(Bundle outState, String recordUuid) {
        if (outState != null) {
            outState.putString(ConstantHelper.KEY_DATA_UUID, recordUuid);
        }
    }

    protected void saveSearchStrategyState(Bundle outState, SearchBy status) {
        if (outState != null) {
            outState.putSerializable(ConstantHelper.ARG_SEARCH_STRATEGY, status);
        }
    }

    protected static <TActivity extends BaseActivity, TCapsule extends IListNavigationCapsule>
    void goToActivity(Context fromActivity, Class<TActivity> toActivity, TCapsule dataCapsule) {

        IStatusElaborator filterStatus = dataCapsule.getFilterStatus();
        IStatusElaborator pageStatus = dataCapsule.getPageStatus();
        SearchBy searchBy = dataCapsule.getSearchStrategy();
        int activeMenuKey = dataCapsule.getActiveMenuKey();

        Intent intent = new Intent(fromActivity, toActivity);

        Bundle bundle = new Bundle();

        bundle.putInt(ConstantHelper.KEY_ACTIVE_MENU, activeMenuKey);

        if (filterStatus != null)
            bundle.putSerializable(ConstantHelper.ARG_FILTER_STATUS, filterStatus.getValue());

        if (pageStatus != null)
            bundle.putSerializable(ConstantHelper.ARG_PAGE_STATUS, pageStatus.getValue());

        if (searchBy != null)
            bundle.putSerializable(ConstantHelper.ARG_SEARCH_STRATEGY, searchBy);

        intent.putExtra(ConstantHelper.ARG_NAVIGATION_CAPSULE_INTENT_DATA, bundle);

        fromActivity.startActivity(intent);
    }
}
