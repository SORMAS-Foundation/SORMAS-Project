/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.person;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;
import androidx.fragment.app.FragmentActivity;

import de.symeda.sormas.api.person.PersonContactDetailType;
import de.symeda.sormas.api.person.PhoneNumberType;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.PseudonymizableAdo;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonContactDetail;
import de.symeda.sormas.app.component.dialog.FormDialog;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.databinding.DialogPersonContactDetailEditLayoutBinding;
import de.symeda.sormas.app.epidata.ExposureDialog;
import de.symeda.sormas.app.util.DataUtils;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;
import static de.symeda.sormas.app.epidata.EpiDataFragmentHelper.getDiseaseOfCaseOrContact;

public class PersonContactDetailDialog extends FormDialog {

	private final PersonContactDetail data;
	private DialogPersonContactDetailEditLayoutBinding contentBinding;
	private final boolean create;

	public PersonContactDetailDialog(
		final FragmentActivity activity,
		PersonContactDetail personContactDetail,
		Person person,
		PseudonymizableAdo activityRootData,
		boolean create) {
		super(
			activity,
			R.layout.dialog_root_layout,
			R.layout.dialog_person_contact_detail_edit_layout,
			R.layout.dialog_root_three_button_panel_layout,
			R.string.heading_person_contact_detail,
			-1,
			false,
			UiFieldAccessCheckers.forSensitiveData(personContactDetail.isPseudonymized()),
			FieldVisibilityCheckers.withDisease(getDiseaseOfCaseOrContact(activityRootData)));

		this.data = personContactDetail;
		this.create = create;
		this.data.setPerson(person);
	}

	@Override
	protected void setContentBinding(Context context, ViewDataBinding binding, String layoutName) {
		this.contentBinding = (DialogPersonContactDetailEditLayoutBinding) binding;
		if (!binding.setVariable(BR.data, data)) {
			Log.e(TAG, "There is no variable 'data' in layout " + layoutName);
		}
	}

	@Override
	protected void initializeContentView(ViewDataBinding rootBinding, ViewDataBinding buttonPanelBinding) {
		contentBinding.personContactDetailPersonContactDetailType.initializeSpinner(DataUtils.getEnumItems(PersonContactDetailType.class, false));
		contentBinding.personContactDetailPhoneNumberType.initializeSpinner(DataUtils.getEnumItems(PhoneNumberType.class, true));

		contentBinding.personContactDetailThirdParty.setVisibility(View.VISIBLE);
		contentBinding.personContactDetailThirdPartyRole.setVisibility(View.VISIBLE);
		contentBinding.personContactDetailThirdPartyName.setVisibility(View.VISIBLE);
		contentBinding.personContactDetailPersonContactDetailType.setVisibility(View.VISIBLE);
		contentBinding.personContactDetailPhoneNumberType.setVisibility(View.VISIBLE);
		contentBinding.personContactDetailDetails.setVisibility(View.VISIBLE);
		contentBinding.personContactDetailContactInformation.setVisibility(View.VISIBLE);
		contentBinding.personContactDetailAdditionalInformation.setVisibility(View.VISIBLE);
		contentBinding.personContactDetailPrimaryContact.setVisibility(View.VISIBLE);

	}

	public void configureAsPersonContactDetailDialog(boolean showDeleteButton) {
		if (showDeleteButton) {
			getDeleteButton().setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onPositiveClick() {
		setLiveValidationDisabled(false);
		try {
			FragmentValidator.validate(getContext(), contentBinding);
		} catch (ValidationException e) {
			NotificationHelper.showDialogNotification(PersonContactDetailDialog.this, ERROR, e.getMessage());
			return;
		}

		super.setCloseOnPositiveButtonClick(true);
		super.onPositiveClick();
	}

	@Override
	public boolean isDeleteButtonVisible() {
		return !create;
	}
}
