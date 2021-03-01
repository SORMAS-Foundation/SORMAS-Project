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

package de.symeda.sormas.app.contact.edit;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.contact.ContactCategory;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactIdentificationSource;
import de.symeda.sormas.api.contact.ContactProximity;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.api.contact.EndOfQuarantineReason;
import de.symeda.sormas.api.contact.QuarantineType;
import de.symeda.sormas.api.contact.TracingApp;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.CountryFieldVisibilityChecker;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.caze.edit.CaseNewActivity;
import de.symeda.sormas.app.caze.read.CaseReadActivity;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.component.dialog.ConfirmationDialog;
import de.symeda.sormas.app.databinding.FragmentContactEditLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.DiseaseConfigurationCache;
import de.symeda.sormas.app.util.InfrastructureHelper;

public class ContactEditFragment extends BaseEditFragment<FragmentContactEditLayoutBinding, Contact, Contact> {

	private Contact record;
	private Case sourceCase;

	// Enum lists

	private List<Item> relationshipList;
	private List<Item> contactClassificationList;
	private List<Item> quarantineList;
	private List<Item> initialRegions;
	private List<Item> allDistricts;
	private List<Item> initialDistricts;
	private List<Item> initialCommunities;
	private List<Item> diseaseList;
	private List<Item> categoryList;
	private List<Item> contactIdentificationSources;
	private List<Item> tracingApps;
	private List<Item> endOfQuarantineReasons;

	// Instance methods

	public static ContactEditFragment newInstance(Contact activityRootData) {
		return newInstanceWithFieldCheckers(
			ContactEditFragment.class,
			null,
			activityRootData,
			FieldVisibilityCheckers.withDisease(activityRootData.getDisease())
				.add(new CountryFieldVisibilityChecker(ConfigProvider.getServerLocale())),
			UiFieldAccessCheckers.forSensitiveData(activityRootData.isPseudonymized()));
	}

	private void setUpControlListeners(FragmentContactEditLayoutBinding contentBinding) {
		contentBinding.createCase.setOnClickListener(v -> CaseNewActivity.startActivityFromContact(getContext(), record.getUuid()));

		contentBinding.openSourceCase.setOnClickListener(v -> CaseReadActivity.startActivity(getActivity(), sourceCase.getUuid(), true));

		contentBinding.openResultingCase.setOnClickListener(v -> CaseReadActivity.startActivity(getActivity(), record.getResultingCaseUuid(), true));
	}

	private void setUpFieldVisibilities(FragmentContactEditLayoutBinding contentBinding) {
		setFieldVisibilitiesAndAccesses(ContactDto.class, contentBinding.mainContent);
		// TODO [vaccination info] integrate vaccination info
//		setFieldVisibilitiesAndAccesses(VaccinationInfoDto.class, contentBinding.vaccinationInfoEditLayout.mainContent);

		if (record.getResultingCaseUuid() != null) {
			contentBinding.createCase.setVisibility(GONE);
			if (DatabaseHelper.getCaseDao().queryUuidBasic(record.getResultingCaseUuid()) == null) {
				contentBinding.openResultingCase.setVisibility(GONE);
			}
		} else {
			contentBinding.openResultingCase.setVisibility(GONE);
		}
		if (sourceCase == null) {
			contentBinding.openSourceCase.setVisibility(GONE);
		} else {
			contentBinding.contactDisease.setVisibility(GONE);
			contentBinding.contactCaseIdExternalSystem.setVisibility(GONE);
			contentBinding.contactCaseOrEventInformation.setVisibility(GONE);
		}

		if (record.getContactClassification() != ContactClassification.CONFIRMED) {
			contentBinding.createCase.setVisibility(GONE);
		}

		if (!record.isMultiDayContact()) {
			contentBinding.contactFirstContactDate.setValue(null);
		}

		if (!ConfigProvider.isConfiguredServer(CountryHelper.COUNTRY_CODE_GERMANY)) {
			contentBinding.contactImmunosuppressiveTherapyBasicDisease.setVisibility(GONE);
			contentBinding.contactImmunosuppressiveTherapyBasicDiseaseDetails.setVisibility(GONE);
			contentBinding.contactCareForPeopleOver60.setVisibility(GONE);
			contentBinding.contactExternalID.setVisibility(GONE);
			contentBinding.contactExternalToken.setVisibility(GONE);
		} else {
			contentBinding.contactImmunosuppressiveTherapyBasicDisease.addValueChangedListener(e -> {
				if (YesNoUnknown.YES.equals(e.getValue())) {
					contentBinding.contactHighPriority.setValue(true);
				}
			});
			contentBinding.contactCareForPeopleOver60.addValueChangedListener(e -> {
				if (YesNoUnknown.YES.equals(e.getValue())) {
					contentBinding.contactHighPriority.setValue(true);
				}
			});
		}

		contentBinding.contactQuarantineExtended.setVisibility(record.isQuarantineExtended() ? VISIBLE : GONE);
		contentBinding.contactQuarantineReduced.setVisibility(record.isQuarantineReduced() ? VISIBLE : GONE);

		// TODO [vaccination info] integrate vaccination info
//		if (!isVisibleAllowed(VaccinationInfoDto.class, contentBinding.vaccinationInfoEditLayout.vaccinationInfoVaccination)) {
//			contentBinding.medicalInformationHeader.setVisibility(GONE);
//		}
	}

	// Overrides

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.caption_contact_information);
	}

	@Override
	public Contact getPrimaryData() {
		return record;
	}

	@Override
	protected void prepareFragmentData() {
		record = getActivityRootData();
		if (record.getCaseUuid() != null) {
			sourceCase = DatabaseHelper.getCaseDao().queryUuidBasic(record.getCaseUuid());
		}

		relationshipList = DataUtils.getEnumItems(ContactRelation.class, true);
		contactClassificationList = DataUtils.getEnumItems(ContactClassification.class, true);
		quarantineList = DataUtils.getEnumItems(QuarantineType.class, true);
		initialRegions = InfrastructureHelper.loadRegions();
		allDistricts = InfrastructureHelper.loadAllDistricts();
		initialDistricts = InfrastructureHelper.loadDistricts(record.getRegion());
		initialCommunities = InfrastructureHelper.loadCommunities(record.getDistrict());
		diseaseList = DataUtils.toItems(DiseaseConfigurationCache.getInstance().getAllDiseases(true, true, true));
		categoryList = DataUtils.getEnumItems(ContactCategory.class, true);
		contactIdentificationSources = DataUtils.getEnumItems(ContactIdentificationSource.class, true);
		tracingApps = DataUtils.getEnumItems(TracingApp.class, true);
		endOfQuarantineReasons = DataUtils.getEnumItems(EndOfQuarantineReason.class, true);

		if (record.getQuarantineTo() == null) {
			record.setQuarantineTo(record.getFollowUpUntil());
		}
	}

	@Override
	public void onLayoutBinding(FragmentContactEditLayoutBinding contentBinding) {
		setUpControlListeners(contentBinding);

		contentBinding.setData(record);
		contentBinding.setCaze(sourceCase);
		contentBinding.setYesNoUnknownClass(YesNoUnknown.class);

		InfrastructureHelper.initializeRegionFields(
			contentBinding.contactRegion,
			initialRegions,
			record.getRegion(),
			contentBinding.contactDistrict,
			initialDistricts,
			record.getDistrict(),
			contentBinding.contactCommunity,
			initialCommunities,
			record.getCommunity());
		contentBinding.contactDisease.initializeSpinner(diseaseList, DiseaseConfigurationCache.getInstance().getDefaultDisease());
		contentBinding.contactDisease.addValueChangedListener(e -> {
			contentBinding.contactContactProximity.setVisibility(e.getValue() == null ? GONE : VISIBLE);
			contentBinding.contactContactProximity.clear();
			contentBinding.contactContactProximity
				.setItems(DataUtils.toItems(Arrays.asList(ContactProximity.getValues((Disease) e.getValue(), ConfigProvider.getServerLocale()))));
		});

		contentBinding.contactFirstContactDate.addValueChangedListener(e -> contentBinding.contactLastContactDate.setRequired(e.getValue() != null));

		contentBinding.contactContactProximity
			.setItems(DataUtils.toItems(Arrays.asList(ContactProximity.getValues(record.getDisease(), ConfigProvider.getServerLocale()))));

		contentBinding.contactQuarantine.addValueChangedListener(e -> {
			boolean visible = QuarantineType.HOME.equals(contentBinding.contactQuarantine.getValue())
				|| QuarantineType.INSTITUTIONELL.equals(contentBinding.contactQuarantine.getValue());
			if (visible) {
				if (ConfigProvider.isConfiguredServer(CountryHelper.COUNTRY_CODE_GERMANY)
					|| ConfigProvider.isConfiguredServer(CountryHelper.COUNTRY_CODE_SWITZERLAND)) {
					contentBinding.contactQuarantineOrderedVerbally.setVisibility(VISIBLE);
					contentBinding.contactQuarantineOrderedOfficialDocument.setVisibility(VISIBLE);
				}
			} else {
				contentBinding.contactQuarantineOrderedVerbally.setVisibility(GONE);
				contentBinding.contactQuarantineOrderedOfficialDocument.setVisibility(GONE);
				contentBinding.contactQuarantineExtended.setVisibility(GONE);
				contentBinding.contactQuarantineReduced.setVisibility(GONE);
			}
		});

		if (!ConfigProvider.isConfiguredServer(CountryHelper.COUNTRY_CODE_GERMANY)
			&& !ConfigProvider.isConfiguredServer(CountryHelper.COUNTRY_CODE_SWITZERLAND)) {
			contentBinding.contactQuarantineOrderedVerbally.setVisibility(GONE);
			contentBinding.contactQuarantineOrderedVerballyDate.setVisibility(GONE);
			contentBinding.contactQuarantineOrderedOfficialDocument.setVisibility(GONE);
			contentBinding.contactQuarantineOrderedOfficialDocumentDate.setVisibility(GONE);
			contentBinding.contactQuarantineOfficialOrderSent.setVisibility(GONE);
			contentBinding.contactQuarantineOfficialOrderSentDate.setVisibility(GONE);
		}

		contentBinding.contactQuarantineExtended.setEnabled(false);
		contentBinding.contactQuarantineReduced.setEnabled(false);
		contentBinding.contactFollowUpUntil.setEnabled(false);

		contentBinding.contactQuarantineTo.addValueChangedListener(new ValueChangeListener() {

			private Date currentQuarantineTo = record.getQuarantineTo();
			private boolean currentQuarantineExtended = record.isQuarantineExtended();
			private boolean currentQuarantineReduced = record.isQuarantineReduced();

			@Override
			public void onChange(ControlPropertyField e) {
				Date newQuarantineTo = (Date) e.getValue();

				if (newQuarantineTo == null) {
					contentBinding.contactQuarantineExtended.setValue(false);
					contentBinding.contactQuarantineReduced.setValue(false);
				}
				if (currentQuarantineTo != null && newQuarantineTo != null && newQuarantineTo.after(currentQuarantineTo)) {
					extendQuarantine(newQuarantineTo);
				} else if (!currentQuarantineExtended) {
					contentBinding.contactQuarantineExtended.setValue(false);
				}
				if (currentQuarantineTo != null && newQuarantineTo != null && newQuarantineTo.before(currentQuarantineTo)) {
					reduceQuarantine();
				} else if (!currentQuarantineReduced) {
					contentBinding.contactQuarantineReduced.setValue(false);
				}
			}

			private void extendQuarantine(Date newQuarantineTo) {
				final ConfirmationDialog confirmationDialog = new ConfirmationDialog(
					getActivity(),
					R.string.heading_extend_quarantine,
					R.string.confirmation_extend_quarantine,
					R.string.yes,
					R.string.no);

				confirmationDialog.setPositiveCallback(() -> {
					contentBinding.contactQuarantineExtended.setValue(true);
					contentBinding.contactQuarantineReduced.setValue(false);
					if (record.getFollowUpUntil() != null) {
						extendFollowUpPeriod(newQuarantineTo);
					}
				});
				confirmationDialog.setNegativeCallback(() -> contentBinding.contactQuarantineTo.setValue(currentQuarantineTo));
				confirmationDialog.show();
			}

			private void extendFollowUpPeriod(Date newQuarantineTo) {
				if (newQuarantineTo.after(record.getFollowUpUntil())) {
					final ConfirmationDialog confirmationDialog = new ConfirmationDialog(
						getActivity(),
						R.string.heading_extend_followup,
						R.string.confirmation_extend_followup,
						R.string.yes,
						R.string.no);

					confirmationDialog.setPositiveCallback(() -> {
						contentBinding.contactFollowUpUntil.setValue(newQuarantineTo);
					});
					confirmationDialog.show();
				}
			}

			private void reduceQuarantine() {
				final ConfirmationDialog confirmationDialog = new ConfirmationDialog(
					getActivity(),
					R.string.heading_reduce_quarantine,
					R.string.confirmation_reduce_quarantine,
					R.string.yes,
					R.string.no);

				confirmationDialog.setPositiveCallback(() -> {
					contentBinding.contactQuarantineExtended.setValue(false);
					contentBinding.contactQuarantineReduced.setValue(true);
				});
				confirmationDialog.setNegativeCallback(() -> contentBinding.contactQuarantineTo.setValue(currentQuarantineTo));
				confirmationDialog.show();
			}
		});

		contentBinding.contactContactIdentificationSource.addValueChangedListener(e -> {
			if (ContactIdentificationSource.TRACING_APP.equals(e.getValue())) {
				contentBinding.contactTracingApp.setVisibility(VISIBLE);
			} else {
				contentBinding.contactTracingApp.setVisibility(GONE);
				contentBinding.contactTracingApp.setValue(null);
				contentBinding.contactTracingAppDetails.setVisibility(GONE);
				contentBinding.contactTracingAppDetails.setValue("");
			}
		});
		if (ConfigProvider.isConfiguredServer(CountryHelper.COUNTRY_CODE_GERMANY)) {
			contentBinding.contactContactProximity.addValueChangedListener(
				e -> updateContactCategory(contentBinding, (ContactProximity) contentBinding.contactContactProximity.getValue()));
		} else {
			contentBinding.contactContactIdentificationSource.setVisibility(GONE);
			contentBinding.contactContactProximityDetails.setVisibility(GONE);
			contentBinding.contactContactCategory.setVisibility(GONE);
		}

		if (record.getCaseUuid() != null) {
			contentBinding.contactDisease.setVisibility(GONE);
			contentBinding.contactCaseIdExternalSystem.setVisibility(GONE);
			contentBinding.contactCaseOrEventInformation.setVisibility(GONE);
		} else {
			contentBinding.contactDisease.setRequired(true);
			contentBinding.contactRegion.setRequired(true);
			contentBinding.contactDistrict.setRequired(true);
		}

		ContactValidator.initializeLastContactDateValidation(record, contentBinding);
		ContactValidator.initializeProhibitionToWorkIntervalValidator(contentBinding);

		//contentBinding.setContactProximityClass(ContactProximity.class);

		contentBinding.contactQuarantineExtended
			.addValueChangedListener(e -> contentBinding.contactQuarantineExtended.setVisibility(record.isQuarantineExtended() ? VISIBLE : GONE));
		contentBinding.contactQuarantineReduced
			.addValueChangedListener(e -> contentBinding.contactQuarantineReduced.setVisibility(record.isQuarantineReduced() ? VISIBLE : GONE));
	}

	/*
	 * Only used for Systems in Germany. Follows specific rules for german systems.
	 */
	private void updateContactCategory(FragmentContactEditLayoutBinding contentBinding, ContactProximity proximity) {
		if (proximity != null) {
			switch (proximity) {
			case FACE_TO_FACE_LONG:
			case TOUCHED_FLUID:
			case AEROSOL:
				contentBinding.contactContactCategory.setValue(ContactCategory.HIGH_RISK);
				break;
			case MEDICAL_UNSAFE:
				contentBinding.contactContactCategory.setValue(ContactCategory.HIGH_RISK_MED);
				break;
			case MEDICAL_LIMITED:
				contentBinding.contactContactCategory.setValue(ContactCategory.MEDIUM_RISK_MED);
				break;
			case SAME_ROOM:
			case FACE_TO_FACE_SHORT:
			case MEDICAL_SAME_ROOM:
				contentBinding.contactContactCategory.setValue(ContactCategory.LOW_RISK);
				break;
			case MEDICAL_DISTANT:
			case MEDICAL_SAFE:
				contentBinding.contactContactCategory.setValue(ContactCategory.NO_RISK);
				break;
			default:
			}
		}
	}

	@Override
	public void onAfterLayoutBinding(FragmentContactEditLayoutBinding contentBinding) {
		// TODO [vaccination info] integrate vaccination info
//		VaccinationInfoEditFragment.setUpLayoutBinding(this, record.getVaccinationInfo(), contentBinding.vaccinationInfoEditLayout);
		setUpFieldVisibilities(contentBinding);

		// Initialize ControlSpinnerFields
		contentBinding.contactRelationToCase.initializeSpinner(relationshipList);
		contentBinding.contactContactClassification.initializeSpinner(contactClassificationList);
		contentBinding.contactQuarantine.initializeSpinner(quarantineList);
		contentBinding.contactContactCategory.initializeSpinner(categoryList);
		contentBinding.contactContactIdentificationSource.initializeSpinner(contactIdentificationSources);
		contentBinding.contactTracingApp.initializeSpinner(tracingApps);
		contentBinding.contactEndOfQuarantineReason.initializeSpinner(endOfQuarantineReasons);
		contentBinding.contactReportingDistrict.initializeSpinner(allDistricts);

		// Initialize ControlDateFields
		contentBinding.contactFirstContactDate.initializeDateField(getFragmentManager());
		contentBinding.contactLastContactDate.initializeDateField(getFragmentManager());
		contentBinding.contactReportDateTime.initializeDateField(getFragmentManager());
		contentBinding.contactQuarantineFrom.initializeDateField(getFragmentManager());
		contentBinding.contactQuarantineTo.initializeDateField(getFragmentManager());
		contentBinding.contactQuarantineOrderedVerballyDate.initializeDateField(getChildFragmentManager());
		contentBinding.contactQuarantineOrderedOfficialDocumentDate.initializeDateField(getChildFragmentManager());
		contentBinding.contactQuarantineOfficialOrderSentDate.initializeDateField(getChildFragmentManager());

		contentBinding.contactProhibitionToWorkFrom.initializeDateField(getChildFragmentManager());
		contentBinding.contactProhibitionToWorkUntil.initializeDateField(getChildFragmentManager());
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_contact_edit_layout;
	}

}
