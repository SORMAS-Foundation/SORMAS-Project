package de.symeda.sormas.app.event.edit;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.AbstractSormasActivity;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.contact.ContactSection;
import de.symeda.sormas.app.contact.edit.ContactEditFollowUpVisitListFragment;
import de.symeda.sormas.app.contact.edit.ContactEditFragment;
import de.symeda.sormas.app.contact.edit.ContactEditPersonFragment;
import de.symeda.sormas.app.contact.edit.ContactEditTaskListFragment;
import de.symeda.sormas.app.event.EventSection;
import de.symeda.sormas.app.event.edit.sub.EventNewPersonsInvolvedActivity;
import de.symeda.sormas.app.core.ISaveable;
import de.symeda.sormas.app.shared.EventFormNavigationCapsule;
import de.symeda.sormas.app.util.ConstantHelper;
import de.symeda.sormas.app.util.MenuOptionsHelper;

/**
 * Created by Orson on 07/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class EventEditActivity extends BaseEditActivity<Event> {

    public static final String TAG = EventEditActivity.class.getSimpleName();

    private AsyncTask saveTask;
    private final int DATA_XML_PAGE_MENU = R.xml.data_form_page_alert_menu; // "xml/data_edit_page_alert_menu.xml";

    private boolean showStatusFrame = false;
    private boolean showTitleBar = true;
    private boolean showPageMenu = false;

    private EventStatus pageStatus = null;
    private String recordUuid = null;
    private BaseEditActivityFragment activeFragment = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        savePageStatusState(outState, pageStatus);
        saveRecordUuidState(outState, recordUuid);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initializeActivity(Bundle arguments) {
        pageStatus = (EventStatus)getPageStatusArg(arguments);
        recordUuid = getRecordUuidArg(arguments);

        this.showStatusFrame = true;
        this.showTitleBar = true;
        this.showPageMenu = true;
    }

    @Override
    protected Event getActivityRootData(String recordUuid) {
        return DatabaseHelper.getEventDao().queryUuid(recordUuid);
    }

    @Override
    protected Event getActivityRootDataIfRecordUuidNull() {
        return null;
    }

    @Override
    public BaseEditActivityFragment getActiveEditFragment(Event activityRootData) {
        if (activeFragment == null) {
            EventFormNavigationCapsule dataCapsule = new EventFormNavigationCapsule(EventEditActivity.this,
                    recordUuid, pageStatus);
            activeFragment = EventEditFragment.newInstance(this, dataCapsule, activityRootData);
        }

        return activeFragment;
    }

    @Override
    public boolean showStatusFrame() {
        return showStatusFrame;
    }

    @Override
    public boolean showTitleBar() {
        return showTitleBar;
    }

    @Override
    public boolean showPageMenu() {
        return showPageMenu;
    }

    @Override
    public Enum getPageStatus() {
        return pageStatus;
    }

    @Override
    public int getPageMenuData() {
        return DATA_XML_PAGE_MENU;
    }

    @Override
    protected BaseEditActivityFragment getEditFragment(LandingPageMenuItem menuItem, Event activityRootData) {
        EventFormNavigationCapsule dataCapsule = new EventFormNavigationCapsule(EventEditActivity.this,
                recordUuid, pageStatus);

        EventSection section = EventSection.fromMenuKey(menuItem.getKey());
        switch (section) {
            case EVENT_INFO:
                activeFragment = EventEditFragment.newInstance(this, dataCapsule, activityRootData);
                break;
            case EVENT_PERSONS:
                activeFragment = EventEditPersonsInvolvedListFragment.newInstance(this, dataCapsule, activityRootData);
                break;
            case TASKS:
                activeFragment = EventEditTaskListFragement.newInstance(this, dataCapsule, activityRootData);
                break;
            default:
                throw new IndexOutOfBoundsException(DataHelper.toStringNullable(section));
        }

        return activeFragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getSaveMenu().setTitle(R.string.action_save_event);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!MenuOptionsHelper.handleEditModuleOptionsItemSelected(this, item))
            return super.onOptionsItemSelected(item);

        return true;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level4_event_edit;
    }

    @Override
    public void goToNewView() {
        EventFormNavigationCapsule dataCapsule = (EventFormNavigationCapsule)new EventFormNavigationCapsule(getContext(), pageStatus)
                .setEventUuid(recordUuid);
        EventNewPersonsInvolvedActivity.goToActivity(EventEditActivity.this, dataCapsule);
    }

    @Override
    public void saveData() {
        if (activeFragment == null)
            return;

        ISaveable fragment = (ISaveable)activeFragment;

        if (fragment != null)
            fragment.save(this);
    }

    public static <TActivity extends AbstractSormasActivity> void
    goToActivity(Context fromActivity, EventFormNavigationCapsule dataCapsule) {
        BaseEditActivity.goToActivity(fromActivity, EventEditActivity.class, dataCapsule);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (saveTask != null && !saveTask.isCancelled())
            saveTask.cancel(true);
    }

}