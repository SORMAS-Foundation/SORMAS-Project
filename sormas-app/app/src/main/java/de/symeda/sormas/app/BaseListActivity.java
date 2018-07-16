package de.symeda.sormas.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.List;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.component.menu.LandingPageMenuAdapter;
import de.symeda.sormas.app.component.menu.LandingPageMenuControl;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.component.menu.LandingPageMenuParser;
import de.symeda.sormas.app.component.menu.OnLandingPageMenuClickListener;
import de.symeda.sormas.app.component.menu.OnNotificationCountChangingListener;
import de.symeda.sormas.app.component.menu.OnSelectInitialActiveMenuItemListener;
import de.symeda.sormas.app.core.IListNavigationCapsule;
import de.symeda.sormas.app.core.IUpdateSubHeadingTitle;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.SearchBy;
import de.symeda.sormas.app.core.enumeration.IStatusElaborator;
import de.symeda.sormas.app.util.ConstantHelper;

public abstract class BaseListActivity<TListItemData extends AbstractDomainObject> extends BaseActivity implements IUpdateSubHeadingTitle, NotificationContext, OnLandingPageMenuClickListener, OnSelectInitialActiveMenuItemListener, OnNotificationCountChangingListener {

    private TextView subHeadingListActivityTitle;
    private View rootView;
    private MenuItem newMenu = null;
    private BaseFragment activeFragment = null;

    private LandingPageMenuControl pageMenu = null;
    private List<LandingPageMenuItem> menuList = null;
    private LandingPageMenuItem activeMenu = null;
    private int activeMenuKey = 0;

    @Override
    protected boolean isSubActivitiy() {
        return false;
    }

    protected void onCreateInner(Bundle savedInstanceState) {
        rootView = findViewById(R.id.base_layout);
        subHeadingListActivityTitle = (TextView) findViewById(R.id.subHeadingActivityTitle);
        pageMenu = (LandingPageMenuControl) findViewById(R.id.landingPageMenuControl);

        Bundle arguments = (savedInstanceState != null) ? savedInstanceState : getIntent().getBundleExtra(ConstantHelper.ARG_NAVIGATION_CAPSULE_INTENT_DATA);
        activeMenuKey = getActiveMenuArg(arguments);

        if (pageMenu != null) {
            pageMenu.hide();
            Context menuControlContext = this.pageMenu.getContext();

            pageMenu.setOnNotificationCountChangingListener(this);
            pageMenu.setOnLandingPageMenuClickListener(this);
            pageMenu.setOnSelectInitialActiveMenuItem(this);
            pageMenu.setAdapter(new LandingPageMenuAdapter(menuControlContext));
            pageMenu.setMenuParser(new LandingPageMenuParser(menuControlContext));
            pageMenu.setMenuData(getPageMenuData());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveActiveMenuState(outState, activeMenuKey);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        replaceFragment(getActiveListFragment());
    }

    public abstract BaseListFragment getActiveListFragment();

    public void replaceFragment(BaseFragment f) {
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

    public int getPageMenuData() {
        return -1;
    }

    public int getActiveMenuKey() {
        return activeMenuKey;
    }

    protected void setActiveMenu(LandingPageMenuItem menuItem) {
        activeMenu = menuItem;
        activeMenuKey = menuItem.getKey();
    }

    public abstract int onNotificationCountChangingAsync(AdapterView<?> parent, LandingPageMenuItem menuItem, int position);

    @Override
    public LandingPageMenuItem onSelectInitialActiveMenuItem(List<LandingPageMenuItem> menuList) {
        if (menuList == null || menuList.size() <= 0)
            return null;

        this.menuList = menuList;

        activeMenu = menuList.get(0);

        for (LandingPageMenuItem m : menuList) {
            if (m.getKey() == activeMenuKey) {
                activeMenu = m;
            }
        }

        return activeMenu;
    }

    @Override
    public boolean onLandingPageMenuClick(AdapterView<?> parent, View view, LandingPageMenuItem menuItem, int position, long id) throws IllegalAccessException, InstantiationException {
        BaseListFragment newActiveFragment = getListFragment(menuItem); //, storedListData

        if (newActiveFragment == null)
            return false;

        setActiveMenu(menuItem);
        replaceFragment(newActiveFragment);

        processActionbarMenu();

        return true;
    }

    protected BaseListFragment getListFragment(LandingPageMenuItem menuItem) {
        return null;
    }

    protected String getRecordUuidArg(Bundle arguments) {
        String result = null;
        if (arguments != null && !arguments.isEmpty()) {
            if (arguments.containsKey(ConstantHelper.KEY_DATA_UUID)) {
                result = (String) arguments.getString(ConstantHelper.KEY_DATA_UUID);
            }
        }

        return result;
    }

    protected <E extends Enum<E>> E getFilterStatusArg(Bundle arguments) {
        E e = null;
        if (arguments != null && !arguments.isEmpty()) {
            if (arguments.containsKey(ConstantHelper.ARG_FILTER_STATUS)) {
                e = (E) arguments.getSerializable(ConstantHelper.ARG_FILTER_STATUS);
            }
        }

        return e;
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

    protected int getActiveMenuArg(Bundle arguments) {
        if (arguments != null && !arguments.isEmpty()) {
            if (arguments.containsKey(ConstantHelper.KEY_ACTIVE_MENU)) {
                return arguments.getInt(ConstantHelper.KEY_ACTIVE_MENU);
            }
        }
        return 0;
    }

    protected <E extends Enum<E>> void saveFilterStatusState(Bundle outState, E status) {
        if (outState != null) {
            outState.putSerializable(ConstantHelper.ARG_FILTER_STATUS, status);
        }
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

    protected void saveActiveMenuState(Bundle outState, int activeMenuKey) {
        if (outState != null) {
            outState.putInt(ConstantHelper.KEY_ACTIVE_MENU, activeMenuKey);
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


    @Override
    public View getRootView() {
        return rootView;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (pageMenu != null) {
            pageMenu.onDestroy();
        }
    }
}
