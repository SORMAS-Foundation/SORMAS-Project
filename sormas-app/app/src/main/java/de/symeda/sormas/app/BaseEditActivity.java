package de.symeda.sormas.app;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.core.INavigationCapsule;
import de.symeda.sormas.app.core.IUpdateSubHeadingTitle;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.enumeration.IStatusElaborator;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.util.ConstantHelper;
import de.symeda.sormas.app.util.Consumer;

public abstract class BaseEditActivity<ActivityRootEntity extends AbstractDomainObject> extends BaseActivity implements IUpdateSubHeadingTitle {

    private LinearLayout notificationFrame;
    private TextView subHeadingListActivityTitle;

    private BaseEditFragment activeFragment = null;

    private AsyncTask getRootEntityTask;
    private ActivityRootEntity storedRootEntity = null;

    private String initialRootEntityUuid;

    private MenuItem saveMenu = null;
    private MenuItem addMenu = null;

    @Override
    protected boolean isSubActivitiy() {
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveRootEntityUuidState(outState, initialRootEntityUuid);
    }

    protected void onCreateInner(Bundle savedInstanceState) {
        subHeadingListActivityTitle = (TextView) findViewById(R.id.subHeadingActivityTitle);
        notificationFrame = (LinearLayout) findViewById(R.id.notificationFrame);

        initialRootEntityUuid = getRecordUuidArg(savedInstanceState);

        if (notificationFrame != null) {
            notificationFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setVisibility(View.GONE);

                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        requestRootData(new Consumer<ActivityRootEntity>() {
            @Override
            public void accept(ActivityRootEntity result) {
                replaceFragment(buildEditFragment(getActivePage(), result));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save:
                saveData();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void requestRootData(final Consumer<ActivityRootEntity> callback) {

        getRootEntityTask = new DefaultAsyncTask(getContext()) {
            @Override
            public void onPreExecute() {
                showPreloader();
            }

            @Override
            public void doInBackground(TaskResultHolder resultHolder) {
                ActivityRootEntity result;
                if (initialRootEntityUuid != null && !initialRootEntityUuid.isEmpty()) {
                    result = queryRootEntity(initialRootEntityUuid);

                    if (result == null) {
                        result = buildRootEntity();
                    }
                } else {
                    result = buildRootEntity();
                }
                resultHolder.forItem().add(result);
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
                hidePreloader();

                if (taskResult.getResultStatus().isSuccess()) {
                    ITaskResultHolderIterator itemIterator = taskResult.getResult().forItem().iterator();

                    boolean hadRootEntity = storedRootEntity != null;
                    if (itemIterator.hasNext())
                        storedRootEntity = itemIterator.next();
                    else
                        storedRootEntity = null;

                    if (storedRootEntity != null
                            && !storedRootEntity.isNew()
                            && storedRootEntity.isUnreadOrChildUnread()) {
                        // TODO #704 do in background and retrieve entity again
//                        DatabaseHelper.getAdoDao(storedRootEntity.getClass()).markAsReadWithCast(storedRootEntity);
                        if (hadRootEntity) {
                            NotificationHelper.showNotification(BaseEditActivity.this, NotificationType.WARNING,
                                    String.format(getResources().getString(R.string.snackbar_entity_overridden), storedRootEntity.getEntityName()));
                        }
                    }

                    callback.accept(storedRootEntity);
                }
            }
        }.executeOnThreadPool();
    }

    protected abstract ActivityRootEntity queryRootEntity(String recordUuid);

    protected abstract ActivityRootEntity buildRootEntity();

    protected ActivityRootEntity getStoredRootEntity() {
        return storedRootEntity;
    }

    protected String getRootEntityUuid() {
        if (storedRootEntity != null) {
            return storedRootEntity.getUuid();
        } else {
            return initialRootEntityUuid;
        }
    }

    protected BaseEditFragment getActiveFragment() {
        return activeFragment;
    }

    public void setSubHeadingTitle(String title) {
        String t = (title == null) ? "" : title;

        if (subHeadingListActivityTitle != null)
            subHeadingListActivityTitle.setText(t);
    }

    @Override
    public void updateSubHeadingTitle() {
        String subHeadingTitle = "";

        if (activeFragment != null) {
            subHeadingTitle = (getActivePage() == null) ? activeFragment.getSubHeadingTitle() : getActivePage().getTitle();
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
    protected int getRootActivityLayout() {
        return R.layout.activity_root_with_title_edit_layout;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_action_menu, menu);

        saveMenu = menu.findItem(R.id.action_save);
        addMenu = menu.findItem(R.id.action_new);

        processActionbarMenu();

        return true;
    }

    private void processActionbarMenu() {
        if (activeFragment == null)
            return;

        if (saveMenu != null)
            saveMenu.setVisible(activeFragment.isShowSaveAction());

        if (addMenu != null)
            addMenu.setVisible(activeFragment.isShowAddAction());
    }

    public MenuItem getSaveMenu() {
        return saveMenu;
    }

    public MenuItem getAddMenu() {
        return addMenu;
    }

    public void replaceFragment(BaseEditFragment f) {
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

    protected static <TActivity extends BaseActivity, TCapsule extends INavigationCapsule> void goToActivity(Context fromActivity, Class<TActivity> toActivity, TCapsule dataCapsule) {

        int activeMenuKey = dataCapsule.getActiveMenuKey();
        String dataUuid = dataCapsule.getRecordUuid();
        IStatusElaborator filterStatus = dataCapsule.getFilterStatus();
        IStatusElaborator pageStatus = dataCapsule.getPageStatus();
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

        Intent intent = new Intent(fromActivity, toActivity);

        Bundle bundle = new Bundle();

        bundle.putInt(ConstantHelper.KEY_ACTIVE_MENU, activeMenuKey);
        bundle.putString(ConstantHelper.KEY_DATA_UUID, dataUuid);
        bundle.putString(ConstantHelper.KEY_PERSON_UUID, personUuid);
        bundle.putString(ConstantHelper.KEY_CASE_UUID, caseUuid);
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

    protected abstract BaseEditFragment buildEditFragment(PageMenuItem menuItem, ActivityRootEntity activityRootData);

    @Override
    protected boolean openPage(PageMenuItem menuItem) {
        BaseEditFragment newActiveFragment = buildEditFragment(menuItem, storedRootEntity);
        if (newActiveFragment == null)
            return false;
        replaceFragment(newActiveFragment);
        return true;
    }

    public abstract void saveData();


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

    protected String getEventUuidArg(Bundle arguments) {
        String result = null;
        if (arguments != null && !arguments.isEmpty()) {
            if (arguments.containsKey(ConstantHelper.KEY_EVENT_UUID)) {
                result = (String) arguments.getString(ConstantHelper.KEY_EVENT_UUID);
            }
        }

        return result;
    }

    protected String getContactUuidArg(Bundle arguments) {
        String result = null;
        if (arguments != null && !arguments.isEmpty()) {
            if (arguments.containsKey(ConstantHelper.KEY_CONTACT_UUID)) {
                result = (String) arguments.getString(ConstantHelper.KEY_CONTACT_UUID);
            }
        }

        return result;
    }

    protected String getCaseUuidArg(Bundle arguments) {
        String result = null;
        if (arguments != null && !arguments.isEmpty()) {
            if (arguments.containsKey(ConstantHelper.KEY_CASE_UUID)) {
                result = (String) arguments.getString(ConstantHelper.KEY_CASE_UUID);
            }
        }

        return result;
    }

    protected void saveRootEntityUuidState(Bundle outState, String recordUuid) {
        if (outState != null) {
            outState.putString(ConstantHelper.KEY_DATA_UUID, recordUuid);
        }
    }

    protected void saveEventUuidState(Bundle outState, String eventUuid) {
        if (outState != null) {
            outState.putString(ConstantHelper.KEY_EVENT_UUID, eventUuid);
        }
    }

    protected void saveContactUuidState(Bundle outState, String contactUuid) {
        if (outState != null) {
            outState.putString(ConstantHelper.KEY_CONTACT_UUID, contactUuid);
        }
    }

    protected void saveCaseUuidState(Bundle outState, String caseUuid) {
        if (outState != null) {
            outState.putString(ConstantHelper.KEY_CASE_UUID, caseUuid);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (getRootEntityTask != null && !getRootEntityTask.isCancelled())
            getRootEntityTask.cancel(true);
    }
}