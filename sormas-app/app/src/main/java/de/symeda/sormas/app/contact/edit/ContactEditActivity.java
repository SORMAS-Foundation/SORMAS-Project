package de.symeda.sormas.app.contact.edit;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Date;

import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.app.AbstractSormasActivity;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.ISaveableWithCallback;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.shared.ContactFormNavigationCapsule;
import de.symeda.sormas.app.util.MenuOptionsHelper;

/**
 * Created by Orson on 12/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class ContactEditActivity extends BaseEditActivity<Contact> {

    public static final String TAG = ContactEditActivity.class.getSimpleName();

    private final int DATA_XML_PAGE_MENU = R.xml.data_form_page_contact_menu;// "xml/data_edit_page_contact_menu.xml";

    private AsyncTask saveTask;
    private static final int MENU_INDEX_CONTACT_INFO = 0;
    private static final int MENU_INDEX_PERSON_INFO = 1;
    private static final int MENU_INDEX_FOLLOWUP_VISIT = 2;
    private static final int MENU_INDEX_TASK = 3;

    private boolean showStatusFrame;
    private boolean showTitleBar;
    private boolean showPageMenu;
    private MenuItem saveMenu = null;
    private MenuItem addMenu = null;

    private ContactClassification pageStatus = null;
    private String recordUuid = null;
    private BaseEditActivityFragment activeFragment = null; //

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
        pageStatus = (ContactClassification) getPageStatusArg(arguments);
        recordUuid = getRecordUuidArg(arguments);

        this.showStatusFrame = true;
        this.showTitleBar = true;
        this.showPageMenu = true;
    }

    @Override
    protected Contact getActivityRootData(String recordUuid) {
        Contact _contact = DatabaseHelper.getContactDao().queryUuid(recordUuid);
        return _contact;
    }

    @Override
    protected Contact getActivityRootDataIfRecordUuidNull() {
        Person _person = DatabaseHelper.getPersonDao().build();
        Contact _contact = DatabaseHelper.getContactDao().build();

        _contact.setPerson(_person);
        _contact.setReportDateTime(new Date());
        _contact.setContactClassification(ContactClassification.UNCONFIRMED);
        _contact.setContactStatus(ContactStatus.ACTIVE);
        _contact.setFollowUpStatus(FollowUpStatus.FOLLOW_UP);
        _contact.setReportingUser(ConfigProvider.getUser());

        return _contact;
    }

    @Override
    public BaseEditActivityFragment getActiveEditFragment(Contact activityRootData) {
        if (activeFragment == null) {
            ContactFormNavigationCapsule dataCapsule = new ContactFormNavigationCapsule(ContactEditActivity.this,
                    recordUuid, pageStatus);
            activeFragment = ContactEditFragment.newInstance(this, dataCapsule, activityRootData);
        }

        return activeFragment;
    }

    @Override
    public int getPageMenuData() {
        return DATA_XML_PAGE_MENU;
    }

    @Override
    protected BaseEditActivityFragment getNextFragment(LandingPageMenuItem menuItem, Contact activityRootData) {
        ContactFormNavigationCapsule dataCapsule = new ContactFormNavigationCapsule(ContactEditActivity.this,
                recordUuid, pageStatus);

        try {
            if (menuItem.getKey() == MENU_INDEX_CONTACT_INFO) {
                activeFragment = ContactEditFragment.newInstance(this, dataCapsule, activityRootData);
            } else if (menuItem.getKey() == MENU_INDEX_PERSON_INFO) {
                activeFragment = ContactEditPersonFragment.newInstance(this, dataCapsule, activityRootData);
            } else if (menuItem.getKey() == MENU_INDEX_FOLLOWUP_VISIT) {
                activeFragment = ContactEditFollowUpVisitListFragment.newInstance(this, dataCapsule, activityRootData);
            } else if (menuItem.getKey() == MENU_INDEX_TASK) {
                activeFragment = ContactEditTaskListFragment.newInstance(this, dataCapsule, activityRootData);
            }

        } catch (InstantiationException e) {
            Log.e(TAG, e.getMessage());
        } catch (IllegalAccessException e) {
            Log.e(TAG, e.getMessage());
        }

        return activeFragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getSaveMenu().setTitle(R.string.action_save_contact);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!MenuOptionsHelper.handleEditModuleOptionsItemSelected(this, item))
            return super.onOptionsItemSelected(item);

        return true;
    }

    @Override
    public void saveData() {
        if (activeFragment == null)
            return;

        int activeMenuKey = getActiveMenuItem().getKey();

        if (activeMenuKey == MENU_INDEX_FOLLOWUP_VISIT || activeMenuKey == MENU_INDEX_TASK)
            return;

        ISaveableWithCallback fragment = (ISaveableWithCallback)activeFragment;

        if (fragment != null)
            fragment.save(this, new Callback.IAction() {
                @Override
                public void call(Object result) {
                    if (!goToNextMenu())
                        NotificationHelper.showNotification(ContactEditActivity.this, NotificationType.INFO, R.string.notification_reach_last_menu);
                }
            });
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level4_contact_edit;
    }

    public static <TActivity extends AbstractSormasActivity> void
    goToActivity(Context fromActivity, ContactFormNavigationCapsule dataCapsule) {
        BaseEditActivity.goToActivity(fromActivity, ContactEditActivity.class, dataCapsule);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}