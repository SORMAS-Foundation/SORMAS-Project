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

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.CountryFieldVisibilityChecker;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.caze.read.CaseReadActivity;
import de.symeda.sormas.app.databinding.FragmentContactReadLayoutBinding;

public class ContactReadFragment extends BaseReadFragment<FragmentContactReadLayoutBinding, Contact, Contact> {

	private Contact record;
	private Case sourceCase;

	public static ContactReadFragment newInstance(Contact activityRootData) {
		return newInstanceWithFieldCheckers(
			ContactReadFragment.class,
			null,
			activityRootData,
			FieldVisibilityCheckers.withDisease(activityRootData.getDisease())
				.add(new CountryFieldVisibilityChecker(ConfigProvider.getServerLocale())),
			null);
	}

	private void setUpControlListeners(FragmentContactReadLayoutBinding contentBinding) {
		contentBinding.openSourceCase.setOnClickListener(v -> CaseReadActivity.startActivity(getContext(), sourceCase.getUuid(), true));
		contentBinding.openResultingCase.setOnClickListener(v -> CaseReadActivity.startActivity(getContext(), record.getResultingCaseUuid(), true));
	}

	private void setUpFieldVisibilities(FragmentContactReadLayoutBinding contentBinding) {
		setFieldVisibilitiesAndAccesses(ContactDto.class, contentBinding.mainContent);

		if (record.getResultingCaseUuid() == null || DatabaseHelper.getCaseDao().queryUuidBasic(record.getResultingCaseUuid()) == null) {
			contentBinding.openResultingCase.setVisibility(GONE);
		}
		if (sourceCase == null) {
			contentBinding.openSourceCase.setVisibility(GONE);
		} else {
			contentBinding.contactDisease.setVisibility(GONE);
			contentBinding.contactCaseIdExternalSystem.setVisibility(GONE);
			contentBinding.contactCaseOrEventInformation.setVisibility(GONE);
		}

		if (!ConfigProvider.isConfiguredServer(CountryHelper.COUNTRY_CODE_GERMANY)
			&& !ConfigProvider.isConfiguredServer(CountryHelper.COUNTRY_CODE_SWITZERLAND)) {
			contentBinding.contactQuarantineOrderedVerbally.setVisibility(GONE);
			contentBinding.contactQuarantineOrderedVerballyDate.setVisibility(GONE);
			contentBinding.contactQuarantineOrderedOfficialDocument.setVisibility(GONE);
			contentBinding.contactQuarantineOrderedOfficialDocumentDate.setVisibility(GONE);
			contentBinding.contactQuarantineOfficialOrderSent.setVisibility(GONE);
			contentBinding.contactQuarantineOfficialOrderSentDate.setVisibility(GONE);
		}
		if (!ConfigProvider.isConfiguredServer(CountryHelper.COUNTRY_CODE_GERMANY)) {
			contentBinding.contactImmunosuppressiveTherapyBasicDisease.setVisibility(GONE);
			contentBinding.contactImmunosuppressiveTherapyBasicDiseaseDetails.setVisibility(GONE);
			contentBinding.contactCareForPeopleOver60.setVisibility(GONE);
			contentBinding.contactExternalID.setVisibility(GONE);
			contentBinding.contactContactIdentificationSource.setVisibility(GONE);
		}

		contentBinding.contactQuarantineExtended.setVisibility(record.isQuarantineExtended() ? VISIBLE : GONE);
		contentBinding.contactQuarantineReduced.setVisibility(record.isQuarantineReduced() ? VISIBLE : GONE);
	}

	// Overrides

	@Override
	protected void prepareFragmentData(Bundle savedInstanceState) {
		record = getActivityRootData();
		if (record.getCaseUuid() != null) {
			sourceCase = DatabaseHelper.getCaseDao().queryUuidBasic(record.getCaseUuid());
		}
	}

	@Override
	public void onLayoutBinding(FragmentContactReadLayoutBinding contentBinding) {
		setUpControlListeners(contentBinding);

		contentBinding.setData(record);
		contentBinding.setCaze(sourceCase);
	}

	@Override
	public void onAfterLayoutBinding(FragmentContactReadLayoutBinding contentBinding) {
		setUpFieldVisibilities(contentBinding);
		contentBinding.contactReportingUser.setPseudonymized(record.isPseudonymized());
	}

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.caption_contact_information);
	}

	@Override
	public Contact getPrimaryData() {
		return record;
	}

	@Override
	public int getReadLayout() {
		return R.layout.fragment_contact_read_layout;
	}
}
