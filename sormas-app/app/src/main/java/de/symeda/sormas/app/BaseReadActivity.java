package de.symeda.sormas.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.component.menu.LandingPageMenuControl;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.component.menu.LandingPageMenuParser;
import de.symeda.sormas.app.component.menu.OnLandingPageMenuClickListener;
import de.symeda.sormas.app.component.menu.OnSelectInitialActiveMenuItemListener;
import de.symeda.sormas.app.component.menu.PageMenuNavAdapter;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.INavigationCapsule;
import de.symeda.sormas.app.core.IUpdateSubHeadingTitle;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.enumeration.IStatusElaborator;
import de.symeda.sormas.app.core.enumeration.StatusElaboratorFactory;
import de.symeda.sormas.app.util.ConstantHelper;
import de.symeda.sormas.app.util.MenuOptionsHelper;

public abstract class BaseReadActivity<ActivityRootEntity extends AbstractDomainObject> extends BaseActivity implements IUpdateSubHeadingTitle, OnLandingPageMenuClickListener, OnSelectInitialActiveMenuItemListener, NotificationContext {

    private AsyncTask getRootEntityTask;
    private View rootView;
    private ActivityRootEntity storedRootEntity = null;
    private TextView subHeadingListActivityTitle;

    private LandingPageMenuControl pageMenu = null;
    private List<LandingPageMenuItem> menuList;
    private LandingPageMenuItem activeMenu = null;
    private int activeMenuKey = 0;

    private String rootEntityUuid;

    private BaseReadFragment activeFragment = null;
    private MenuItem editMenu = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveActiveMenuState(outState, activeMenuKey);
        saveRootEntityUuidState(outState, rootEntityUuid);
    }

    @Override
    protected boolean setHomeAsUpIndicator() {
        return false;
    }

    protected void onCreateInner(Bundle savedInstanceState) {
        rootView = findViewById(R.id.base_layout);
        subHeadingListActivityTitle = (TextView) findViewById(R.id.subHeadingActivityTitle);
        menuList = new ArrayList<LandingPageMenuItem>();
        pageMenu = (LandingPageMenuControl) findViewById(R.id.landingPageMenuControl);

        ensureFabHiddenOnSoftKeyboardShown(pageMenu);

        Bundle arguments = (savedInstanceState != null) ? savedInstanceState : getIntent().getBundleExtra(ConstantHelper.ARG_NAVIGATION_CAPSULE_INTENT_DATA);

        activeMenuKey = getActiveMenuArg(arguments);
        rootEntityUuid = getRecordUuidArg(arguments);

        if (pageMenu != null)
            pageMenu.hide();

        if (pageMenu != null) {
            Context menuControlContext = this.pageMenu.getContext();

            pageMenu.setOnLandingPageMenuClickListener(this);
            pageMenu.setOnSelectInitialActiveMenuItem(this);

            pageMenu.setAdapter(new PageMenuNavAdapter(menuControlContext));
            pageMenu.setMenuParser(new LandingPageMenuParser(menuControlContext));
            pageMenu.setMenuData(getPageMenuData());
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
                } else {
                    landingPageMenuControl.showFab();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!MenuOptionsHelper.handleReadModuleOptionsItemSelected(this, item))
            return super.onOptionsItemSelected(item);
        return true;
    }

    protected void requestRootData(final Callback.IAction<ActivityRootEntity> callback) {

        getRootEntityTask = new DefaultAsyncTask(getContext()) {
            @Override
            public void onPreExecute() {
                showPreloader();
            }

            @Override
            public void doInBackground(TaskResultHolder resultHolder) {
                ActivityRootEntity result;
                if (rootEntityUuid != null && !rootEntityUuid.isEmpty()) {
                    result = queryRootData(rootEntityUuid);
                } else {
                    result = null;
                }
                resultHolder.forItem().add(result);
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
                hidePreloader();

                if (taskResult.getResultStatus().isSuccess()) {
                    ITaskResultHolderIterator itemIterator = taskResult.getResult().forItem().iterator();

                    if (itemIterator.hasNext())
                        storedRootEntity = itemIterator.next();
                    else
                        storedRootEntity = null;

                    callback.call(storedRootEntity);
                }
            }
        }.executeOnThreadPool();
    }

    protected abstract ActivityRootEntity queryRootData(String recordUuid);

    protected ActivityRootEntity getStoredRootEntity() {
        return storedRootEntity;
    }

    @Override
    protected void onResume() {
        super.onResume();

        requestRootData(new Callback.IAction<ActivityRootEntity>() {
            @Override
            public void call(ActivityRootEntity result) {
                replaceFragment(buildReadFragment(getActiveMenuItem(), result));
            }
        });
    }

    public void setSubHeadingTitle(String title) {
        String t = (title == null) ? "" : title;

        if (subHeadingListActivityTitle != null)
            subHeadingListActivityTitle.setText(t);
    }

    @Override
    public void updateSubHeadingTitle() {
        String subHeadingTitle = "";
        LandingPageMenuItem activeMenu = getActiveMenuItem();

        if (activeFragment != null) {
            subHeadingTitle = (activeMenu == null) ? activeFragment.getSubHeadingTitle() : activeMenu.getTitle();
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
    protected int getRootActivityLayout() {
        return R.layout.activity_root_with_title_layout;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.read_action_menu, menu);

        editMenu = menu.findItem(R.id.action_edit);
        editMenu.setTitle(R.string.action_edit);

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

    public void replaceFragment(BaseReadFragment f) {
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
            processActionbarMenu();
        }
    }

    protected static <TActivity extends BaseActivity, TCapsule extends INavigationCapsule>
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

        fromActivity.startActivity(intent);
    }

    public LandingPageMenuItem getActiveMenuItem() {
        return activeMenu;
    }

    @Override
    public View getRootView() {
        return rootView;
    }

    public boolean showPageMenu() {
        return getPageMenuData() > 0;
    }

    public int getPageMenuData() {
        return -1;
    }

    public int getActiveMenuKey() {
        return activeMenuKey;
    }

    protected String getRootEntityUuid() {
        return rootEntityUuid;
    }

    protected void setActiveMenu(LandingPageMenuItem menuItem) {
        activeMenu = menuItem;
        activeMenuKey = menuItem.getKey();
    }

    public boolean onLandingPageMenuClick(AdapterView<?> parent, View view, LandingPageMenuItem menuItem, int position, long id) throws IllegalAccessException, InstantiationException {
        BaseReadFragment newActiveFragment = buildReadFragment(menuItem, storedRootEntity);

        if (newActiveFragment == null)
            return false;

        setActiveMenu(menuItem);
        replaceFragment(newActiveFragment);

        processActionbarMenu();

        return true;
    }

    public abstract void goToEditView();

    protected abstract BaseReadFragment buildReadFragment(LandingPageMenuItem menuItem, ActivityRootEntity activityRootData);

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

    protected void saveRootEntityUuidState(Bundle outState, String recordUuid) {
        if (outState != null) {
            outState.putString(ConstantHelper.KEY_DATA_UUID, recordUuid);
        }
    }

    protected void saveActiveMenuState(Bundle outState, int activeMenuKey) {
        if (outState != null) {
            outState.putInt(ConstantHelper.KEY_ACTIVE_MENU, activeMenuKey);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (getRootEntityTask != null && !getRootEntityTask.isCancelled())
            getRootEntityTask.cancel(true);

        if (pageMenu != null) {
            pageMenu.onDestroy();
        }
    }

}
