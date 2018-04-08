package de.symeda.sormas.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.component.menu.LandingPageMenuControl;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.component.menu.LandingPageMenuParser;
import de.symeda.sormas.app.component.menu.OnLandingPageMenuClickListener;
import de.symeda.sormas.app.component.menu.OnSelectInitialActiveMenuItemListener;
import de.symeda.sormas.app.component.menu.PageMenuNavAdapter;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityRootDataRequestor;
import de.symeda.sormas.app.core.ICallback;
import de.symeda.sormas.app.core.INavigationCapsule;
import de.symeda.sormas.app.core.INotificationContext;
import de.symeda.sormas.app.core.IUpdateSubHeadingTitle;
import de.symeda.sormas.app.core.async.IJobDefinition;
import de.symeda.sormas.app.core.async.ITaskExecutor;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskExecutorFor;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.enumeration.IStatusElaborator;
import de.symeda.sormas.app.core.enumeration.StatusElaboratorFactory;
import de.symeda.sormas.app.util.ConstantHelper;

/**
 * Created by Orson on 10/12/2017.
 */

public abstract class BaseReadActivity<TActivityRootData extends AbstractDomainObject> extends AbstractSormasActivity implements IUpdateSubHeadingTitle, OnLandingPageMenuClickListener, OnSelectInitialActiveMenuItemListener, INotificationContext, IActivityRootDataRequestor<TActivityRootData> {

    private AsyncTask processActivityRootDataTask;
    private View rootView;
    private TActivityRootData storedActivityRootData = null;
    private View fragmentFrame = null;
    private View statusFrame = null;
    private View applicationTitleBar = null;
    private TextView subHeadingListActivityTitle;

    private LandingPageMenuControl pageMenu = null;
    private ArrayList<LandingPageMenuItem> menuList;
    private LandingPageMenuItem activeMenu = null;
    private int activeMenuKey = ConstantHelper.INDEX_FIRST_MENU;

    private Enum pageStatus;
    private String recordUuid;
    private BaseReadActivityFragment activeFragment = null;
    private MenuItem editMenu = null;

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

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
        return false;
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
        fragmentFrame = findViewById(R.id.fragment_frame);
        menuList = new ArrayList<LandingPageMenuItem>();
        pageMenu = (LandingPageMenuControl) findViewById(R.id.landingPageMenuControl);

        ensureFabHiddenOnSoftKeyboardShown(pageMenu);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getIntent().getBundleExtra(ConstantHelper.ARG_NAVIGATION_CAPSULE_INTENT_DATA);

        activeMenuKey = getActiveMenuArg(arguments);
        pageStatus = getPageStatusArg(arguments);
        recordUuid = getRecordUuidArg(arguments);

        initializeActivity(arguments);

        try {
            if(pageMenu != null)
                pageMenu.hide();

            if (pageMenu != null) {
                Context menuControlContext = this.pageMenu.getContext();

                pageMenu.setOnLandingPageMenuClickListener(this);
                pageMenu.setOnSelectInitialActiveMenuItem(this);

                pageMenu.setAdapter(new PageMenuNavAdapter(menuControlContext));
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

        if (fragmentFrame != null && savedInstanceState == null) {
            processActivityRootData(new ICallback<TActivityRootData>() {
                @Override
                public void result(TActivityRootData result) {
                    try {
                        activeFragment = getActiveReadFragment(result);
                        replaceFragment(activeFragment);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    }
                }
            });


        }
    }

    private void ensureFabHiddenOnSoftKeyboardShown(final LandingPageMenuControl landingPageMenuControl) {
        final View _rootView = getRootView();

        if (_rootView == null)
            return;

        _rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                _rootView.getWindowVisibleDisplayFrame(r);
                int heightDiff = _rootView.getRootView().getHeight() - (r.bottom - r.top);

                if (heightDiff > 100) {

                    if (landingPageMenuControl != null) {
                        landingPageMenuControl.hideAll();
                    }
                } else{
                    landingPageMenuControl.showFab();
                }
            }
        });
    }

    @Override
    public void requestActivityRootData(ICallback<TActivityRootData> callback) {
        processActivityRootData(callback);
    }

    private void processActivityRootData(final ICallback<TActivityRootData> callback) {
        try {
            ITaskExecutor executor = TaskExecutorFor.job(new IJobDefinition() {
                @Override
                public void preExecute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    showPreloader();
                    hideFragmentView();
                }

                @Override
                public void execute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    resultHolder.forItem().add(getActivityRootDataProxy());
                }
            });
            processActivityRootDataTask = executor.execute(new ITaskResultCallback() {
                @Override
                public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    hidePreloader();
                    showFragmentView();

                    if (resultHolder == null){
                        return;
                    }

                    ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

                    if (itemIterator.hasNext())
                        storedActivityRootData = itemIterator.next();

                    callback.result(storedActivityRootData);
                }
            });
        } catch (Exception ex) {
            hidePreloader();
            showFragmentView();
        }
    }

    private TActivityRootData getActivityRootDataProxy() {
        TActivityRootData result;
        if (recordUuid != null && !recordUuid.isEmpty()) {
            result = getActivityRootData(recordUuid);

            if (result == null) {
                result = getActivityRootDataIfRecordUuidNull();
            }
        } else {
            result = getActivityRootDataIfRecordUuidNull();
        }

        return result;
    }

    protected abstract TActivityRootData getActivityRootData(String recordUuid);

    protected abstract TActivityRootData getActivityRootDataIfRecordUuidNull();

    protected TActivityRootData getStoredActivityRootData() {
        return storedActivityRootData;
    }

    protected abstract void initializeActivity(Bundle arguments);

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
                        drw.setColorFilter(statusFrameContext.getResources().getColor(getStatusColorResource(statusFrameContext)), PorterDuff.Mode.SRC);

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
        String subHeadingTitle = "";
        LandingPageMenuItem activeMenu = getActiveMenuItem();

        if (activeFragment != null) {
            subHeadingTitle = (activeMenu == null)? activeFragment.getSubHeadingTitle() : activeMenu.getTitle();
        }

        setSubHeadingTitle(subHeadingTitle);
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
    public LandingPageMenuItem onSelectInitialActiveMenuItem(ArrayList<LandingPageMenuItem> menuList) {
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
    protected int getRootActivityLayout() {
        return R.layout.activity_root_with_title_layout;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.read_action_menu, menu);

        editMenu = menu.findItem(R.id.action_edit);

        processActionbarMenu();

        return true;
    }

    private void processActionbarMenu() {
        if (activeFragment == null)
            return;

        if (editMenu != null)
            editMenu.setVisible(activeFragment.showEditAction());
    }

    public MenuItem getEditMenu() {
        return editMenu;
    }

    public String getStatusName(Context context) {
        Enum pageStatus = getPageStatus();

        if (pageStatus != null) {
            IStatusElaborator elaborator = StatusElaboratorFactory.getElaborator(context, pageStatus);
            if (elaborator != null)
                return elaborator.getFriendlyName();
        }

        return "";
    }

    public int getStatusColorResource(Context context) {
        Enum pageStatus = getPageStatus();

        if (pageStatus != null) {
            IStatusElaborator elaborator = StatusElaboratorFactory.getElaborator(context, pageStatus);
            if (elaborator != null)
                return elaborator.getColorIndicatorResource();
        }

        return R.color.noColor;
    }

    public void replaceFragment(BaseReadActivityFragment f) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        activeFragment = f;

        if (activeFragment != null) {
            if (activeFragment.getArguments() == null)
                activeFragment.setArguments(getIntent().getBundleExtra(ConstantHelper.ARG_NAVIGATION_CAPSULE_INTENT_DATA));

            ft.replace(R.id.fragment_frame, activeFragment);
            ft.addToBackStack(null);
            ft.commit();

            processActionbarMenu();
        }
    }

    /*public void setFabUpDrawable() {
        Drawable drw = (Drawable) ContextCompat.getDrawable(fab.getContext(), R.drawable.ic_landing_menu_open_black_24dp);
        drw.setTint(fab.getContext().getResources().getColor(R.color.fabIcon));
        fab.setImageDrawable(drw);
    }

    public void setFabDownDrawable() {
        Drawable drw = (Drawable) ContextCompat.getDrawable(fab.getContext(), R.drawable.ic_landing_menu_close_black_24dp);
        drw.setTint(fab.getContext().getResources().getColor(R.color.fabIcon));
        fab.setImageDrawable(drw);
    }*/

    protected static <TActivity extends AbstractSormasActivity, TCapsule extends INavigationCapsule>
    void goToActivity(Context fromActivity, Class<TActivity> toActivity, TCapsule dataCapsule) {

        int activeMenuKey = dataCapsule.getActiveMenuKey();
        String dataUuid = dataCapsule.getRecordUuid();
        IStatusElaborator filterStatus = dataCapsule.getFilterStatus();
        IStatusElaborator pageStatus = dataCapsule.getPageStatus();
        String sampleMaterial = dataCapsule.getSampleMaterial();
        String personUuid = dataCapsule.getPersonUuid();
        String caseUuid = dataCapsule.getCaseUuid();
        String eventUuid = dataCapsule.getEventUuid();
        String taskUuid = dataCapsule.getTaskUuid();
        String contactUuid = dataCapsule.getContactUuid();
        String sampleUuid = dataCapsule.getSampleUuid();
        Disease disease = dataCapsule.getDisease();
        boolean isForVisit = dataCapsule.isForVisit();
        boolean isVisitCooperative = dataCapsule.isVisitCooperative();
        UserRight userRight = dataCapsule.getUserRight();
        //AbstractDomainObject record = dataCapsule.getRecord();

        Intent intent = new Intent(fromActivity, toActivity);

        Bundle bundle = new Bundle();

        bundle.putInt(ConstantHelper.KEY_ACTIVE_MENU, activeMenuKey);
        bundle.putString(ConstantHelper.KEY_DATA_UUID, dataUuid);
        bundle.putString(ConstantHelper.KEY_PERSON_UUID, personUuid);
        bundle.putString(ConstantHelper.KEY_CASE_UUID, caseUuid);
        bundle.putString(ConstantHelper.KEY_SAMPLE_MATERIAL, sampleMaterial);
        bundle.putString(ConstantHelper.KEY_EVENT_UUID, eventUuid);
        bundle.putString(ConstantHelper.KEY_TASK_UUID, taskUuid);
        bundle.putString(ConstantHelper.KEY_CONTACT_UUID, contactUuid);
        bundle.putString(ConstantHelper.KEY_SAMPLE_UUID, sampleUuid);
        bundle.putSerializable(ConstantHelper.ARG_DISEASE, disease);
        bundle.putBoolean(ConstantHelper.ARG_FOR_VISIT, isForVisit);
        bundle.putBoolean(ConstantHelper.ARG_VISIT_COOPERATIVE, isVisitCooperative);
        bundle.putSerializable(ConstantHelper.ARG_EDIT_OR_CREATE_USER_RIGHT, userRight);

        if (filterStatus != null)
            bundle.putSerializable(ConstantHelper.ARG_FILTER_STATUS, filterStatus.getValue());

        if (pageStatus != null)
            bundle.putSerializable(ConstantHelper.ARG_PAGE_STATUS, pageStatus.getValue());

        intent.putExtra(ConstantHelper.ARG_NAVIGATION_CAPSULE_INTENT_DATA, bundle);

        /*if (record != null)
            intent.putExtra(ConstantHelper.ARG_PAGE_RECORD, record);*/

        /*for (IStatusElaborator e: dataCapsule.getOtherStatus()) {
            if (e != null)
                intent.putExtra(e.getStatekey(), e.getValue());
        }*/
        fromActivity.startActivity(intent);
    }

    public abstract BaseReadActivityFragment getActiveReadFragment(TActivityRootData activityRootData) throws IllegalAccessException, InstantiationException;

    public LandingPageMenuItem getActiveMenuItem() {
        return activeMenu;
    }

    @Override
    public View getRootView() {
        return rootView;
    }

    public boolean showStatusFrame() {
        return true;
    }

    public boolean showTitleBar() {
        return true;
    }

    public boolean showPageMenu() {
        return getPageMenuData() > 0;
    }

    public Enum getPageStatus() {
        return pageStatus;
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

    public boolean onLandingPageMenuClick(AdapterView<?> parent, View view, LandingPageMenuItem menuItem, int position, long id) throws IllegalAccessException, InstantiationException {
        BaseReadActivityFragment newActiveFragment = getNextFragment(menuItem, storedActivityRootData);

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

        BaseReadActivityFragment newActiveFragment = getNextFragment(m, storedActivityRootData);

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

    protected BaseReadActivityFragment getNextFragment(LandingPageMenuItem menuItem, TActivityRootData activityRootData) {
        return null;
    }

    protected boolean changeFragment(BaseReadActivityFragment newActiveFragment) {
        if (newActiveFragment == null)
            return false;

        replaceFragment(newActiveFragment);

        //this.activeFragment = newActiveFragment;

        processActionbarMenu();
        //updateSubHeadingTitle();

        return true;
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

    protected <E extends Enum<E>> E getPageStatusArg(Bundle arguments) {
        E e = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.ARG_PAGE_STATUS)) {
                e = (E) arguments.getSerializable(ConstantHelper.ARG_PAGE_STATUS);
            }
        }

        return e;
    }

    protected <E extends Enum<E>> E getArgByElaboratorKey(Bundle arguments, String key) {
        E e = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(key)) {
                e = (E) arguments.getSerializable(key);
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

    protected String getEventUuidArg(Bundle arguments) {
        String result = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.KEY_EVENT_UUID)) {
                result = (String) arguments.getString(ConstantHelper.KEY_EVENT_UUID);
            }
        }

        return result;
    }

    protected String getTaskUuidArg(Bundle arguments) {
        String result = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.KEY_TASK_UUID)) {
                result = (String) arguments.getString(ConstantHelper.KEY_TASK_UUID);
            }
        }

        return result;
    }

    protected String getContactUuidArg(Bundle arguments) {
        String result = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.KEY_CONTACT_UUID)) {
                result = (String) arguments.getString(ConstantHelper.KEY_CONTACT_UUID);
            }
        }

        return result;
    }

    protected String getCaseUuidArg(Bundle arguments) {
        String result = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.KEY_CASE_UUID)) {
                result = (String) arguments.getString(ConstantHelper.KEY_CASE_UUID);
            }
        }

        return result;
    }

    protected String getSampleUuidArg(Bundle arguments) {
        String result = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.KEY_SAMPLE_UUID)) {
                result = (String) arguments.getString(ConstantHelper.KEY_SAMPLE_UUID);
            }
        }

        return result;
    }

    protected Disease getDiseaseArg(Bundle arguments) {
        Disease result = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.ARG_DISEASE)) {
                result = (Disease) arguments.getSerializable(ConstantHelper.ARG_DISEASE);
            }
        }

        return result;
    }

    protected boolean getForVisitArg(Bundle arguments) {
        boolean result = false;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.ARG_FOR_VISIT)) {
                result = (boolean) arguments.getBoolean(ConstantHelper.ARG_FOR_VISIT);
            }
        }

        return result;
    }

    protected boolean getVisitCooperativeArg(Bundle arguments) {
        boolean result = false;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.ARG_VISIT_COOPERATIVE)) {
                result = (boolean) arguments.getBoolean(ConstantHelper.ARG_VISIT_COOPERATIVE);
            }
        }

        return result;
    }

    protected UserRight getUserRightArg(Bundle arguments) {
        UserRight e = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.ARG_EDIT_OR_CREATE_USER_RIGHT)) {
                e = (UserRight) arguments.getSerializable(ConstantHelper.ARG_EDIT_OR_CREATE_USER_RIGHT);
            }
        }

        return e;
    }

    protected String getSampleMaterialArg(Bundle arguments) {
        String result = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.KEY_SAMPLE_MATERIAL)) {
                result = (String) arguments.getString(ConstantHelper.KEY_SAMPLE_MATERIAL);
            }
        }

        return result;
    }



    protected <E extends Enum<E>> void SaveFilterStatusState(Bundle outState, E status) {
        if (outState != null) {
            outState.putSerializable(ConstantHelper.ARG_FILTER_STATUS, status);
        }
    }

    protected <E extends Enum<E>> void SavePageStatusState(Bundle outState, E status) {
        if (outState != null) {
            outState.putSerializable(ConstantHelper.ARG_PAGE_STATUS, status);
        }
    }

    protected void SaveRecordUuidState(Bundle outState, String recordUuid) {
        if (outState != null) {
            outState.putString(ConstantHelper.KEY_DATA_UUID, recordUuid);
        }
    }

    protected void SaveActiveMenuState(Bundle outState, int activeMenuKey) {
        if (outState != null) {
            outState.putInt(ConstantHelper.KEY_ACTIVE_MENU, activeMenuKey);
        }
    }

    protected void SaveEventUuidState(Bundle outState, String eventUuid) {
        if (outState != null) {
            outState.putString(ConstantHelper.KEY_EVENT_UUID, eventUuid);
        }
    }

    protected void SaveTaskUuidState(Bundle outState, String taskUuid) {
        if (outState != null) {
            outState.putString(ConstantHelper.KEY_TASK_UUID, taskUuid);
        }
    }

    protected void SaveContactUuidState(Bundle outState, String contactUuid) {
        if (outState != null) {
            outState.putString(ConstantHelper.KEY_CONTACT_UUID, contactUuid);
        }
    }

    protected void SaveCaseUuidState(Bundle outState, String caseUuid) {
        if (outState != null) {
            outState.putString(ConstantHelper.KEY_CASE_UUID, caseUuid);
        }
    }

    protected void SaveSampleUuidState(Bundle outState, String sampleUuid) {
        if (outState != null) {
            outState.putString(ConstantHelper.KEY_SAMPLE_UUID, sampleUuid);
        }
    }

    protected void SaveDiseaseState(Bundle outState, Disease disease) {
        if (outState != null) {
            outState.putSerializable(ConstantHelper.ARG_DISEASE, disease);
        }
    }

    protected void SaveForVisitState(Bundle outState, boolean isForVisit) {
        if (outState != null) {
            outState.putBoolean(ConstantHelper.ARG_FOR_VISIT, isForVisit);
        }
    }

    protected void SaveVisitCooperativeState(Bundle outState, boolean isVisitCooperative) {
        if (outState != null) {
            outState.putBoolean(ConstantHelper.ARG_VISIT_COOPERATIVE, isVisitCooperative);
        }
    }

    protected void SaveUserRightState(Bundle outState, UserRight userRight) {
        if (outState != null) {
            outState.putSerializable(ConstantHelper.ARG_EDIT_OR_CREATE_USER_RIGHT, userRight);
        }
    }

    protected void SaveSampleMaterialState(Bundle outState, String sampleMaterial) {
        if (outState != null) {
            outState.putString(ConstantHelper.KEY_SAMPLE_MATERIAL, sampleMaterial);
        }
    }



    private <E extends Enum<E>> E RestoreFilterStatusState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            return (E)savedInstanceState.getSerializable(ConstantHelper.ARG_FILTER_STATUS);
        }

        return null;
    }

    private <E extends Enum<E>> E RestorePageStatusState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            return (E)savedInstanceState.getSerializable(ConstantHelper.ARG_PAGE_STATUS);
        }

        return null;
    }

    private String RestoreRecordUuidState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            return savedInstanceState.getString(ConstantHelper.KEY_DATA_UUID);
        }

        return null;
    }

    private int RestoreActiveMenuState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            return savedInstanceState.getInt(ConstantHelper.KEY_ACTIVE_MENU);
        }

        return -1;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (processActivityRootDataTask != null && !processActivityRootDataTask.isCancelled())
            processActivityRootDataTask.cancel(true);
    }

}
