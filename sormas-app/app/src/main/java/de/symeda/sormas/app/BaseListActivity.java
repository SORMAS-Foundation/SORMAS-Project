package de.symeda.sormas.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.component.menu.LandingPageMenuAdapter;
import de.symeda.sormas.app.component.menu.LandingPageMenuControl;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.component.menu.LandingPageMenuParser;
import de.symeda.sormas.app.component.menu.OnLandingPageMenuClickListener;
import de.symeda.sormas.app.component.menu.OnNotificationCountChangingListener;
import de.symeda.sormas.app.component.menu.OnSelectInitialActiveMenuItemListener;
import de.symeda.sormas.app.core.IListNavigationCapsule;
import de.symeda.sormas.app.core.INotificationContext;
import de.symeda.sormas.app.core.IUpdateSubHeadingTitle;
import de.symeda.sormas.app.core.SearchBy;
import de.symeda.sormas.app.core.enumeration.IStatusElaborator;
import de.symeda.sormas.app.core.enumeration.StatusElaboratorFactory;
import de.symeda.sormas.app.util.ConstantHelper;

/**
 * Created by Orson on 03/12/2017.
 */

public abstract class BaseListActivity<TListItemData extends AbstractDomainObject> extends AbstractSormasActivity implements IUpdateSubHeadingTitle, INotificationContext, OnLandingPageMenuClickListener, OnSelectInitialActiveMenuItemListener, OnNotificationCountChangingListener {

    private View statusFrame = null;
    private View applicationTitleBar = null;
    private TextView subHeadingListActivityTitle;
    private View fragmentFrame = null;
    private View rootView;
    private MenuItem newMenu = null;
    private BaseListActivityFragment activeFragment = null;

    private List<TListItemData> storedListData = null;
    private LandingPageMenuControl pageMenu = null;
    private List<LandingPageMenuItem> menuList;
    private LandingPageMenuItem activeMenu = null;
    private int activeMenuKey = ConstantHelper.INDEX_FIRST_MENU;

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        //initializeActivity(savedInstanceState);
        activeMenuKey = getActiveMenuArg(savedInstanceState);
        initializeActivity(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SaveActiveMenuState(outState, activeMenuKey);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    protected void initializeBaseActivity(Bundle savedInstanceState) {
        rootView = findViewById(R.id.base_layout);
        subHeadingListActivityTitle = (TextView)findViewById(R.id.subHeadingListActivityTitle);
        menuList = new ArrayList<LandingPageMenuItem>();
        pageMenu = (LandingPageMenuControl) findViewById(R.id.landingPageMenuControl);
        /*Bundle arguments = (savedInstanceState != null)? savedInstanceState : getIntent().getExtras();
        initializeActivity(arguments);*/

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getIntent().getBundleExtra(ConstantHelper.ARG_NAVIGATION_CAPSULE_INTENT_DATA);
        activeMenuKey = getActiveMenuArg(arguments);
        initializeActivity(arguments);

        try {
            if(pageMenu != null)
                pageMenu.hide();

            if (pageMenu != null) {
                Context menuControlContext = this.pageMenu.getContext();


                pageMenu.setOnNotificationCountChangingListener(this);
                pageMenu.setOnLandingPageMenuClickListener(this);
                pageMenu.setOnSelectInitialActiveMenuItem(this);

                //pageMenu.setOnLandingPageMenuClickListener(this);
                //pageMenu.setOnSelectInitialActiveMenuItem(this);

                //pageMenu.setAdapter(new PageMenuNavAdapter(menuControlContext));

                pageMenu.setAdapter(new LandingPageMenuAdapter(menuControlContext));
                pageMenu.setMenuParser(new LandingPageMenuParser(menuControlContext));
                pageMenu.setMenuData(getPageMenuData());

                //configureFab(fab, pageMenu);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }


        if (showTitleBar()) {
            applicationTitleBar = findViewById(R.id.applicationTitleBar);
            statusFrame = findViewById(R.id.statusFrame);
        }

        fragmentFrame = findViewById(R.id.fragment_frame);
        if (fragmentFrame != null) {
            try {
                if (savedInstanceState == null) {
                    // setting the fragment_frame
                    BaseListActivityFragment activeFragment = null;
                    activeFragment = getActiveReadFragment();
                    replaceFragment(activeFragment);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    protected abstract void initializeActivity(Bundle arguments);

    public abstract BaseListActivityFragment getActiveReadFragment() throws IllegalAccessException, InstantiationException;

    public void replaceFragment(BaseListActivityFragment f) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        activeFragment = f;

        if (activeFragment != null) {
            if (activeFragment.getArguments() == null)
                activeFragment.setArguments(getIntent().getBundleExtra(ConstantHelper.ARG_NAVIGATION_CAPSULE_INTENT_DATA));

            ft.setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout);
            ft.replace(R.id.fragment_frame, activeFragment);
            ft.addToBackStack(null);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*if (menuDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
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
    protected void onResume() {
        super.onResume();

        if (applicationTitleBar != null) {
            if (showTitleBar()) {
                applicationTitleBar.setVisibility(View.VISIBLE);

                if (statusFrame != null) {
                    if (showStatusFrame()) {
                        Context statusFrameContext = statusFrame.getContext();

                        Drawable drw = (Drawable) ContextCompat.getDrawable(statusFrameContext, R.drawable.indicator_status_circle);
                        drw.setColorFilter(statusFrameContext.getResources().getColor(getStatusColorResource(statusFrameContext)), PorterDuff.Mode.SRC_OVER);

                        TextView txtStatusName = (TextView)statusFrame.findViewById(R.id.txtStatusName);
                        ImageView imgStatus = (ImageView)statusFrame.findViewById(R.id.statusIcon);


                        txtStatusName.setText(getStatusName(statusFrameContext));
                        imgStatus.setBackground(drw);

                        statusFrame.setVisibility(View.VISIBLE);
                    } else {
                        statusFrame.setVisibility(View.GONE);
                    }
                }
            }
        } else {
            applicationTitleBar.setVisibility(View.GONE);
        }
    }

    public void setSubHeadingTitle(String title) {
        String t = (title == null)? "" : title;

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

    public abstract Enum getStatus();

    public abstract boolean showStatusFrame();

    public abstract boolean showTitleBar();

    @Override
    protected int getRootActivityLayout() {
        return R.layout.activity_root_list_layout;
    }

    //<editor-fold desc="Landing Menu Methods">

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

        for(LandingPageMenuItem m: menuList){
            if (m.getKey() == activeMenuKey){
                activeMenu = m;
            }
        }

        return activeMenu;
    }

    @Override
    public boolean onLandingPageMenuClick(AdapterView<?> parent, View view, LandingPageMenuItem menuItem, int position, long id) throws IllegalAccessException, InstantiationException {
        BaseListActivityFragment newActiveFragment = getNextFragment(menuItem); //, storedListData

        if (newActiveFragment == null)
            return false;

        setActiveMenu(menuItem);
        replaceFragment(newActiveFragment);

        processActionbarMenu();
        //updateSubHeadingTitle();

        return true;
    }

    protected boolean goToNextMenu() {
        if (pageMenu == null)
            return false;

        if (menuList == null || menuList.size() <= 0)
            return false;

        int lastMenuKey = menuList.size() - 1;

        if (activeMenuKey == lastMenuKey)
            return false;

        int newMenukey = activeMenuKey + 1;

        LandingPageMenuItem m = menuList.get(newMenukey);
        setActiveMenu(m);

        BaseListActivityFragment newActiveFragment = getNextFragment(m); //, storedListData

        if (newActiveFragment == null)
            return false;

        setActiveMenu(m);

        pageMenu.markActiveMenuItem(m);

        replaceFragment(newActiveFragment);

        //this.activeFragment = newActiveFragment;

        processActionbarMenu();
        //updateSubHeadingTitle();

        return true;
    }

    protected BaseListActivityFragment getNextFragment(LandingPageMenuItem menuItem) { //, List<TListItemData> activityListData
        return null;
    }

    protected boolean changeFragment(BaseListActivityFragment newActiveFragment) {
        if (newActiveFragment == null)
            return false;

        replaceFragment(newActiveFragment);

        //this.activeFragment = newActiveFragment;

        processActionbarMenu();
        //updateSubHeadingTitle();

        return true;
    }

    //</editor-fold>


    public String getStatusName(Context context) {
        Enum status = getStatus();

        if (status != null) {
            IStatusElaborator elaborator = StatusElaboratorFactory.getElaborator(context, status);
            if (elaborator != null)
                return elaborator.getFriendlyName();
        }

        return "";
    }

    public int getStatusColorResource(Context context) {
        Enum status = getStatus();

        if (status != null) {
            IStatusElaborator elaborator = StatusElaboratorFactory.getElaborator(context, status);
            if (elaborator != null)
                return elaborator.getColorIndicatorResource();
        }

        return R.color.noColor;
    }

    protected String getRecordUuidArg(Bundle arguments) {
        String result = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.KEY_DATA_UUID)) {
                result = (String) arguments.getString(ConstantHelper.KEY_DATA_UUID);
            }
        }

        return result;
    }

    protected <E extends Enum<E>> E getFilterStatusArg(Bundle arguments) {
        E e = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.ARG_FILTER_STATUS)) {
                e = (E) arguments.getSerializable(ConstantHelper.ARG_FILTER_STATUS);
            }
        }

        return e;
    }

    protected SearchBy getSearchStrategyArg(Bundle arguments) {
        SearchBy e = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.ARG_SEARCH_STRATEGY)) {
                e = (SearchBy) arguments.getSerializable(ConstantHelper.ARG_SEARCH_STRATEGY);
            }
        }

        return e;
    }

    protected int getActiveMenuArg(Bundle arguments) {
        int result = ConstantHelper.INDEX_FIRST_MENU;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.KEY_ACTIVE_MENU)) {
                result = (int) arguments.getInt(ConstantHelper.KEY_ACTIVE_MENU);
            }
        }

        return result;
    }



    protected <E extends Enum<E>> void SaveFilterStatusState(Bundle outState, E status) {
        if (outState != null) {
            outState.putSerializable(ConstantHelper.ARG_FILTER_STATUS, status);
        }
    }

    protected void SaveRecordUuidState(Bundle outState, String recordUuid) {
        if (outState != null) {
            outState.putString(ConstantHelper.KEY_DATA_UUID, recordUuid);
        }
    }

    protected void SaveSearchStrategyState(Bundle outState, SearchBy status) {
        if (outState != null) {
            outState.putSerializable(ConstantHelper.ARG_FILTER_STATUS, status);
        }
    }

    protected void SaveActiveMenuState(Bundle outState, int activeMenuKey) {
        if (outState != null) {
            outState.putInt(ConstantHelper.KEY_ACTIVE_MENU, activeMenuKey);
        }
    }

    protected static <TActivity extends AbstractSormasActivity, TCapsule extends IListNavigationCapsule>
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
