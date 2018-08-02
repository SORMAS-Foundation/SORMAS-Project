package de.symeda.sormas.app.contact.read;

import android.content.Context;
import android.view.Menu;

import java.util.List;

import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.BaseReadActivity;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.contact.ContactSection;
import de.symeda.sormas.app.contact.edit.ContactEditActivity;
import de.symeda.sormas.app.person.read.PersonReadFragment;

public class ContactReadActivity extends BaseReadActivity<Contact> {

    public static final String TAG = ContactReadActivity.class.getSimpleName();

    public static void startActivity(Context context, String rootUuid, boolean finishInsteadOfUpNav) {
        BaseReadActivity.startActivity(context, ContactReadActivity.class, buildBundle(rootUuid, finishInsteadOfUpNav));
    }

    @Override
    protected Contact queryRootData(String recordUuid) {
        Contact _contact = DatabaseHelper.getContactDao().queryUuid(recordUuid);
        return _contact;
    }

    @Override
    public List<PageMenuItem> getPageMenuData() {
        return PageMenuItem.fromEnum(ContactSection.values(), getContext());
    }

    @Override
    public ContactClassification getPageStatus() {
        return getStoredRootEntity() == null ? null : getStoredRootEntity().getContactClassification();
    }

    @Override
    protected BaseReadFragment buildReadFragment(PageMenuItem menuItem, Contact activityRootData) {
        ContactSection section = ContactSection.fromOrdinal(menuItem.getKey());
        BaseReadFragment fragment;
        switch (section) {
            case CONTACT_INFO:
                fragment = ContactReadFragment.newInstance(activityRootData);
                break;
            case PERSON_INFO:
                fragment = PersonReadFragment.newInstance(activityRootData);
                break;
            case VISITS:
                fragment = ContactReadFollowUpVisitListFragment.newInstance(activityRootData);
                break;
            case TASKS:
                fragment = ContactReadTaskListFragment.newInstance(activityRootData);
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
    protected int getActivityTitle() {
        return R.string.heading_level3_contact_read;
    }

    @Override
    public void goToEditView() {
        ContactSection section = ContactSection.fromOrdinal(getActivePage().getKey());
        ContactEditActivity.startActivity(ContactReadActivity.this, getRootUuid(), section);
    }
}

