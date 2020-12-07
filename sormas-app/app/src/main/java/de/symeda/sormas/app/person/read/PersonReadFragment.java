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

package de.symeda.sormas.app.person.read;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.ObservableArrayList;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.contact.ContactEditAuthorization;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.component.dialog.InfoDialog;
import de.symeda.sormas.app.core.FieldHelper;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.databinding.FragmentPersonReadLayoutBinding;
import de.symeda.sormas.app.person.edit.PersonEditFragment;
import de.symeda.sormas.app.util.FieldVisibilityAndAccessHelper;
import de.symeda.sormas.app.util.InfrastructureHelper;

import static android.view.View.GONE;

public class PersonReadFragment extends BaseReadFragment<FragmentPersonReadLayoutBinding, Person, AbstractDomainObject> {

	public static final String TAG = PersonReadFragment.class.getSimpleName();

	private Person record;
	private AbstractDomainObject rootData;
	private boolean birthDayVisibility = true;
	private IEntryItemOnClickListener onAddressItemClickListener;

	// Instance methods

	public static PersonReadFragment newInstance(Case activityRootData) {
		return newInstanceWithFieldCheckers(
			PersonReadFragment.class,
			null,
			activityRootData,
			FieldVisibilityCheckers.withDisease(activityRootData.getDisease()),
			UiFieldAccessCheckers.getDefault(activityRootData.isPseudonymized()));
	}

	public static PersonReadFragment newInstance(Contact activityRootData) {
		return newInstanceWithFieldCheckers(
			PersonReadFragment.class,
			null,
			activityRootData,
			FieldVisibilityCheckers.withDisease(activityRootData.getDisease()),
			UiFieldAccessCheckers.getDefault(activityRootData.isPseudonymized()));
	}

	private void setUpControlListeners() {
		onAddressItemClickListener = (v, item) -> {
			InfoDialog infoDialog = new InfoDialog(
				getContext(),
				R.layout.dialog_location_read_layout,
				item,
				bindedView -> setFieldAccesses(LocationDto.class, bindedView));
			infoDialog.show();
		};
	}

	public static void setUpFieldVisibilities(
		BaseReadFragment fragment,
		FragmentPersonReadLayoutBinding contentBinding,
		AbstractDomainObject rootData) {
		fragment.setFieldVisibilitiesAndAccesses(PersonDto.class, contentBinding.mainContent);

		InfrastructureHelper.initializeHealthFacilityDetailsFieldVisibility(
			contentBinding.personPlaceOfBirthFacility,
			contentBinding.personPlaceOfBirthFacilityDetails);
		PersonEditFragment.initializeCauseOfDeathDetailsFieldVisibility(
			contentBinding.personCauseOfDeath,
			contentBinding.personCauseOfDeathDisease,
			contentBinding.personCauseOfDeathDetails);

		if (!ConfigProvider.isConfiguredServer(CountryHelper.COUNTRY_CODE_GERMANY)) {
			contentBinding.personArmedForcesRelationType.setVisibility(GONE);
		}
	}

	// Overrides

	@Override
	protected void prepareFragmentData(Bundle savedInstanceState) {
		AbstractDomainObject ado = getActivityRootData();

		if (ado instanceof Case) {
			record = ((Case) ado).getPerson();
			rootData = ado;
		} else if (ado instanceof Contact) {
			record = ((Contact) ado).getPerson();
			rootData = ado;
		} else {
			throw new UnsupportedOperationException(
				"ActivityRootData of class " + ado.getClass().getSimpleName() + " does not support PersonReadFragment");
		}

		// Workaround because person is not an embedded entity and therefore the locations are not
		// automatically loaded (because there's no additional queryForId call for person when the
		// parent data is loaded)
		DatabaseHelper.getPersonDao().initLocations(record);
	}

	@Override
	public void onLayoutBinding(FragmentPersonReadLayoutBinding contentBinding) {
		setUpControlListeners();

		ObservableArrayList<Location> addresses = new ObservableArrayList<>();
		addresses.addAll(record.getAddresses());

		contentBinding.setData(record);

		contentBinding.setAddressList(addresses);
		contentBinding.setAddressItemClickCallback(onAddressItemClickListener);
		contentBinding.setAddressBindCallback(v -> {
			setFieldAccesses(LocationDto.class, v);
		});
	}

	@Override
	public void onAfterLayoutBinding(FragmentPersonReadLayoutBinding contentBinding) {
		PersonReadFragment.setUpFieldVisibilities(this, contentBinding, rootData);
	}

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.caption_patient_information);
	}

	@Override
	public Person getPrimaryData() {
		return record;
	}

	@Override
	public int getReadLayout() {
		return R.layout.fragment_person_read_layout;
	}

	private void setFieldAccesses(Class<?> dtoClass, View view) {
		FieldVisibilityAndAccessHelper
			.setFieldVisibilitiesAndAccesses(dtoClass, (ViewGroup) view, new FieldVisibilityCheckers(), getFieldAccessCheckers());
	}

}
