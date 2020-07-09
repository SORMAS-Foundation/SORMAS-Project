/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.contact.read;

import java.util.List;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.BaseReadActivity;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.contact.ContactEditAuthorization;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.contact.ContactSection;
import de.symeda.sormas.app.contact.edit.ContactEditActivity;
import de.symeda.sormas.app.epidata.EpidemiologicalDataReadFragment;
import de.symeda.sormas.app.person.read.PersonReadFragment;

public class ContactReadActivity extends BaseReadActivity<Contact> {

	public static final String TAG = ContactReadActivity.class.getSimpleName();

	public static void startActivity(Context context, String rootUuid, boolean finishInsteadOfUpNav) {
		BaseReadActivity.startActivity(context, ContactReadActivity.class, buildBundle(rootUuid, finishInsteadOfUpNav));
	}

	@Override
	protected Contact queryRootEntity(String recordUuid) {
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
		ContactSection section = ContactSection.fromOrdinal(menuItem.getPosition());
		BaseReadFragment fragment;
		switch (section) {
		case CONTACT_INFO:
			fragment = ContactReadFragment.newInstance(activityRootData);
			break;
		case PERSON_INFO:
			fragment = PersonReadFragment.newInstance(activityRootData);
			break;
		case VISITS:
			fragment = ContactReadVisitsListFragment.newInstance(activityRootData);
			break;
		case TASKS:
			fragment = ContactReadTaskListFragment.newInstance(activityRootData);
			break;
		case EPIDEMIOLOGICAL_DATA:
			fragment = EpidemiologicalDataReadFragment.newInstance(activityRootData);
			break;
			default:
			throw new IndexOutOfBoundsException(DataHelper.toStringNullable(section));
		}
		return fragment;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		getEditMenu().setTitle(R.string.action_edit_contact);

		return result;
	}

	@Override
	protected void processActionbarMenu() {
		super.processActionbarMenu();

		final MenuItem editMenu = getEditMenu();

		final ReferenceDto referenceDto = new ContactReferenceDto(getRootUuid());
		final Contact selectedContact = DatabaseHelper.getContactDao().getByReferenceDto(referenceDto);

		if (editMenu != null) {
			if (ContactEditAuthorization.isContactEditAllowed(selectedContact)) {
				editMenu.setVisible(true);
			} else {
				editMenu.setVisible(false);
			}
		}
	}

	@Override
	protected int getActivityTitle() {
		return R.string.heading_contact_read;
	}

	@Override
	public void goToEditView() {
		ContactSection section = ContactSection.fromOrdinal(getActivePage().getPosition());
		ContactEditActivity.startActivity(ContactReadActivity.this, getRootUuid(), section);
	}
}
