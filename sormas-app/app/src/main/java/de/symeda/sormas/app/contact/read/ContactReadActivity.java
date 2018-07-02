package de.symeda.sormas.app.contact.read;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;

import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.BaseReadActivity;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.contact.ContactSection;
import de.symeda.sormas.app.contact.edit.ContactEditActivity;
import de.symeda.sormas.app.shared.ContactFormNavigationCapsule;
import de.symeda.sormas.app.util.MenuOptionsHelper;

public class ContactReadActivity extends BaseReadActivity<Contact> {

    public static final String TAG = ContactReadActivity.class.getSimpleName();

    @Override
    protected Contact queryRootData(String recordUuid) {
        Contact _contact = DatabaseHelper.getContactDao().queryUuid(recordUuid);
        return _contact;
    }

    @Override
    public int getPageMenuData() {
        return R.xml.data_form_page_contact_menu;
    }

    @Override
    public ContactClassification getPageStatus() {
        return (ContactClassification)super.getPageStatus();
    }

    @Override
    protected BaseReadFragment buildReadFragment(LandingPageMenuItem menuItem, Contact activityRootData) {
        ContactFormNavigationCapsule dataCapsule = new ContactFormNavigationCapsule(
                ContactReadActivity.this, getRootEntityUuid(), getPageStatus());

        ContactSection section = ContactSection.fromMenuKey(menuItem.getKey());
        BaseReadFragment fragment;
        switch (section) {
            case CONTACT_INFO:
                fragment = ContactReadFragment.newInstance(dataCapsule, activityRootData);
                break;
            case PERSON_INFO:
                fragment = ContactReadPersonFragment.newInstance(dataCapsule, activityRootData);
                break;
            case VISITS:
                fragment = ContactReadFollowUpVisitListFragment.newInstance(dataCapsule, activityRootData);
                break;
            case TASKS:
                fragment = ContactReadTaskListFragment.newInstance(dataCapsule, activityRootData);
                break;
            default:
                throw new IndexOutOfBoundsException(DataHelper.toStringNullable(section));
        }
        return fragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getEditMenu().setTitle(R.string.action_edit_contact);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!MenuOptionsHelper.handleReadModuleOptionsItemSelected(this, item))
            return super.onOptionsItemSelected(item);

        return true;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level3_contact_read;
    }

    @Override
    public void goToEditView() {
        ContactFormNavigationCapsule dataCapsule = new ContactFormNavigationCapsule(this, getRootEntityUuid(), getPageStatus());
        ContactEditActivity.goToActivity(ContactReadActivity.this, dataCapsule);
    }

    public static void goToActivity(Context fromActivity, ContactFormNavigationCapsule dataCapsule) {
        BaseReadActivity.goToActivity(fromActivity, ContactReadActivity.class, dataCapsule);
    }
}

