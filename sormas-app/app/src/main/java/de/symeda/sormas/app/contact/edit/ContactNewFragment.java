/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.contact.edit;

import java.util.List;

import de.symeda.sormas.api.contact.ContactProximity;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.caze.edit.CaseNewFragment;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.databinding.FragmentContactNewLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.visit.edit.VisitValidator;

public class ContactNewFragment extends BaseEditFragment<FragmentContactNewLayoutBinding, Contact, Contact> {

    public static final String TAG = CaseNewFragment.class.getSimpleName();

    private Contact record;

    private List<Item> relationshipList;

    public static ContactNewFragment newInstance(Contact activityRootData) {
        return newInstance(ContactNewFragment.class, null, activityRootData);
    }

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.caption_new_contact);
    }

    @Override
    public Contact getPrimaryData() {
        return record;
    }

    @Override
    protected void prepareFragmentData() {
        record = getActivityRootData();
        relationshipList = DataUtils.getEnumItems(ContactRelation.class, true);
    }

    @Override
    public void onLayoutBinding(FragmentContactNewLayoutBinding contentBinding) {
        contentBinding.setData(record);
        contentBinding.setContactProximityClass(ContactProximity.class);

        ContactValidator.initializeValidation(record, contentBinding);
    }

    @Override
    public void onAfterLayoutBinding(FragmentContactNewLayoutBinding contentBinding) {
        contentBinding.contactRelationToCase.initializeSpinner(relationshipList);
        contentBinding.contactLastContactDate.initializeDateField(getFragmentManager());
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_contact_new_layout;
    }
}
