/*******************************************************************************
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.samples;

import static com.vaadin.ui.Notification.Type.TRAY_NOTIFICATION;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleDto;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleReferenceDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestFacade;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class PathogenTestController {

	private final PathogenTestFacade facade = FacadeProvider.getPathogenTestFacade();

	// Antibiotic susceptibility test is applicable for TB(Lux), IMI, IPI and Shigellosis. For others it should be null.
	private static final List<Disease> AST_ALLOWED_DISEASES =
		Arrays.asList(Disease.INVASIVE_MENINGOCOCCAL_INFECTION, Disease.INVASIVE_PNEUMOCOCCAL_INFECTION, Disease.SHIGELLOSIS);

	public PathogenTestController() {
	}

	public List<PathogenTestDto> getPathogenTestsBySample(SampleReferenceDto sampleRef) {
		return facade.getAllBySample(sampleRef);
	}

	public List<PathogenTestDto> getPathogenTestsByEnvironmentSample(EnvironmentSampleReferenceDto sampleRef) {
		return facade.getAllByEnvironmentSample(sampleRef);
	}

	public void create(SampleReferenceDto sampleRef, int caseSampleCount) {
		SampleDto sampleDto = FacadeProvider.getSampleFacade().getSampleByUuid(sampleRef.getUuid());
		final CommitDiscardWrapperComponent<PathogenTestForm> editView = getPathogenTestCreateComponent(sampleDto, caseSampleCount, null, false);

		VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.headingCreatePathogenTestResult));
	}

	public void create(EnvironmentSampleReferenceDto sampleRef) {
		EnvironmentSampleDto sampleDto = FacadeProvider.getEnvironmentSampleFacade().getByUuid(sampleRef.getUuid());

		final CommitDiscardWrapperComponent<PathogenTestForm> editView = getPathogenTestCreateComponent(sampleDto);

		VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.headingCreatePathogenTestResult));
	}

	public CommitDiscardWrapperComponent<PathogenTestForm> getPathogenTestCreateComponent(
		SampleDto sampleDto,
		int caseSampleCount,
		Consumer<PathogenTestDto> onSavedPathogenTest,
		boolean suppressNavigateToCase) {
		return getPathogenTestCreateComponent(
			PathogenTestDto.build(sampleDto, UiUtil.getUser()),
			sampleDto,
			caseSampleCount,
			onSavedPathogenTest,
			suppressNavigateToCase);
	}

	public CommitDiscardWrapperComponent<PathogenTestForm> getPathogenTestCreateComponent(
		PathogenTestDto pathogenTest,
		SampleDto sampleDto,
		int caseSampleCount,
		Consumer<PathogenTestDto> onSavedPathogenTest,
		boolean suppressNavigateToCase) {
		// Pathogen tests can be created for a sample that is associated with a case, event participant or contact.
		Disease associatedEventOrCaseOrContactDisease = null;
		if (sampleDto.getAssociatedCase() != null) {
			CaseDataDto caseDataDto = FacadeProvider.getCaseFacade().getByUuid(sampleDto.getAssociatedCase().getUuid());
			associatedEventOrCaseOrContactDisease = caseDataDto.getDisease();
		}
		if (associatedEventOrCaseOrContactDisease == null && sampleDto.getAssociatedEventParticipant() != null) {
			EventParticipantDto eventParticipant =
				FacadeProvider.getEventParticipantFacade().getEventParticipantByUuid(sampleDto.getAssociatedEventParticipant().getUuid());
			EventReferenceDto eventDto = eventParticipant.getEvent();
			EventDto participantEvent = FacadeProvider.getEventFacade().getEventByUuid(eventDto.getUuid(), false);
			associatedEventOrCaseOrContactDisease = participantEvent.getDisease();
		}
		if (associatedEventOrCaseOrContactDisease == null && sampleDto.getAssociatedContact() != null) {
			ContactDto contact = FacadeProvider.getContactFacade().getByUuid(sampleDto.getAssociatedContact().getUuid());
			associatedEventOrCaseOrContactDisease = contact.getDisease();
		}
		PathogenTestForm createForm = new PathogenTestForm(sampleDto, true, caseSampleCount, false, true, associatedEventOrCaseOrContactDisease);
		pathogenTest.setTestedDisease(associatedEventOrCaseOrContactDisease);
		createForm.setValue(pathogenTest);

		// Lab is mandatory for non-internal samples (consistent with creating a test alongside a new sample)
		createForm.setLabRequired(!SamplePurpose.INTERNAL.equals(sampleDto.getSamplePurpose()));

		// Trigger the "test date not before sample date" alarm when saving the test itself,
		// rather than only later when saving the sample
		createForm.addTestDateAfterSampleDateValidator(
			sampleDto::getSampleDateTime,
			I18nProperties.getValidationError(
				Validations.afterDate,
				I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_DATE_TIME),
				I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.SAMPLE_DATE_TIME)));

		final CommitDiscardWrapperComponent<PathogenTestForm> editView =
			new CommitDiscardWrapperComponent<>(createForm, UiUtil.permitted(UserRight.PATHOGEN_TEST_CREATE), createForm.getFieldGroup());

		editView.addCommitListener(() -> {
			if (!createForm.getFieldGroup().isModified()) {
				PathogenTestDto editedPathogenTest = createForm.getValue();
				savePathogenTestForSample(editedPathogenTest, suppressNavigateToCase);

				if (onSavedPathogenTest != null) {
					onSavedPathogenTest.accept(editedPathogenTest);
				}

				SormasUI.refreshView();
			}
		});
		return editView;
	}

	public CommitDiscardWrapperComponent<PathogenTestForm> getPathogenTestCreateComponent(EnvironmentSampleDto sampleDto) {

		PathogenTestForm createForm = new PathogenTestForm(sampleDto, true, false, true, null); // Valid because jurisdiction doesn't matter for entities that are about to be created
		createForm.setValue(PathogenTestDto.build(sampleDto, UiUtil.getUser()));

		final CommitDiscardWrapperComponent<PathogenTestForm> editView =
			new CommitDiscardWrapperComponent<>(createForm, UiUtil.permitted(UserRight.ENVIRONMENT_PATHOGEN_TEST_CREATE), createForm.getFieldGroup());

		editView.addCommitListener(() -> {
			if (!createForm.getFieldGroup().isModified()) {
				PathogenTestDto pathogenTest = createForm.getValue();
				savePathogenTestForEnvironmentSample(pathogenTest);

				SormasUI.refreshView();
			}
		});
		return editView;
	}

	public void edit(String pathogenTestUuid, Runnable doneCallback, boolean isEditAllowed, boolean isDeleteAllowed) {
		final CommitDiscardWrapperComponent<PathogenTestForm> editView =
			getPathogenTestEditComponent(pathogenTestUuid, doneCallback, isEditAllowed, isDeleteAllowed);

		Window popupWindow = VaadinUiUtil.createPopupWindow();

		if (isDeleteAllowed) {
			editView.addDeleteWithReasonOrRestoreListener((deleteDetails) -> {
				FacadeProvider.getPathogenTestFacade().deletePathogenTest(pathogenTestUuid, deleteDetails);
				UI.getCurrent().removeWindow(popupWindow);
				doneCallback.run();
			}, I18nProperties.getCaption(PathogenTestDto.I18N_PREFIX));
		}
		editView.addCommitListener(popupWindow::close);
		editView.addDiscardListener(popupWindow::close);

		popupWindow.setContent(editView);
		popupWindow
			.setCaption(I18nProperties.getString(!isEditAllowed ? Strings.headingViewPathogenTestResult : Strings.headingEditPathogenTestResult));
		UI.getCurrent().addWindow(popupWindow);
	}

	public CommitDiscardWrapperComponent<PathogenTestForm> getPathogenTestEditComponent(
		String pathogenTestUuid,
		Runnable doneCallback,
		boolean isEditAllowed,
		boolean isDeleteAllowed) {

		// get fresh data
		PathogenTestDto pathogenTest = facade.getByUuid(pathogenTestUuid);
		boolean forHumanSample = pathogenTest.getSample() != null;
		final PathogenTestForm form;
		if (forHumanSample) {
			SampleDto sample = FacadeProvider.getSampleFacade().getSampleByUuid(pathogenTest.getSample().getUuid());
			form = new PathogenTestForm(
				sample,
				false,
				0,
				pathogenTest.isPseudonymized(),
				pathogenTest.isInJurisdiction(),
				pathogenTest.getTestedDisease());
		} else {
			EnvironmentSampleDto environmentSample =
				FacadeProvider.getEnvironmentSampleFacade().getByUuid(pathogenTest.getEnvironmentSample().getUuid());
			form = new PathogenTestForm(
				environmentSample,
				false,
				pathogenTest.isPseudonymized(),
				pathogenTest.isInJurisdiction(),
				pathogenTest.getTestedDisease());
		}

		form.setValue(pathogenTest);

		boolean isEditOrDeleteAllowed = isEditAllowed || isDeleteAllowed;
		final CommitDiscardWrapperComponent<PathogenTestForm> editView =
			new CommitDiscardWrapperComponent<>(form, isEditOrDeleteAllowed, form.getFieldGroup());

		if (isEditOrDeleteAllowed) {
			editView.addCommitListener(() -> {
				if (!form.getFieldGroup().isModified()) {
					PathogenTestDto editedPathogenTest = form.getValue();
					if (editedPathogenTest.getSample() != null) {
						savePathogenTestForSample(form.getValue(), false);
					}

					if (editedPathogenTest.getEnvironmentSample() != null) {
						savePathogenTestForEnvironmentSample(form.getValue());
					}

					doneCallback.run();
					SormasUI.refreshView();
				}
			});

			if (pathogenTest.isDeleted()) {
				editView.getWrappedComponent().showDeletionInfo(pathogenTest.getDeletionReason());
			}
			editView.restrictEditableComponentsOnEditView(
				forHumanSample ? UserRight.SAMPLE_EDIT : UserRight.ENVIRONMENT_SAMPLE_EDIT,
				forHumanSample ? UserRight.PATHOGEN_TEST_EDIT : UserRight.ENVIRONMENT_PATHOGEN_TEST_EDIT,
				forHumanSample ? UserRight.PATHOGEN_TEST_DELETE : UserRight.ENVIRONMENT_PATHOGEN_TEST_DELETE,
				null,
				pathogenTest.isInJurisdiction());

		}
		editView.getButtonsPanel().setVisible(isEditOrDeleteAllowed);

		return editView;
	}

	public static void showCaseUpdateWithNewDiseaseVariantDialog(
		CaseDataDto existingCaseDto,
		DiseaseVariant diseaseVariant,
		String diseaseVariantDetails,
		Consumer<Boolean> callback) {

		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getString(Strings.headingUpdateCaseWithNewDiseaseVariant),
			new Label(
				String.format(
					I18nProperties.getString(Strings.messageUpdateCaseWithNewDiseaseVariant),
					existingCaseDto.getDiseaseVariant() == null
						? "[" + I18nProperties.getCaption(Captions.caseNoDiseaseVariant) + "]"
						: existingCaseDto.getDiseaseVariant().toString(),
					diseaseVariant != null ? diseaseVariant.toString() : "[" + I18nProperties.getCaption(Captions.caseNoDiseaseVariant) + "]")),
			I18nProperties.getString(Strings.yes),
			I18nProperties.getString(Strings.no),
			800,
			yes -> {
				if (yes) {
					CaseDataDto caseDataByUuid = FacadeProvider.getCaseFacade().getCaseDataByUuid(existingCaseDto.getUuid());
					caseDataByUuid.setDiseaseVariant(diseaseVariant);
					caseDataByUuid.setDiseaseVariantDetails(diseaseVariantDetails);
					FacadeProvider.getCaseFacade().save(caseDataByUuid);
				}
				if (callback != null) {
					callback.accept(yes);
				}
			}).bringToFront();
	}

	public void savePathogenTestForSample(PathogenTestDto dto, boolean suppressNavigateToCase) {
		savePathogenTests(Collections.singletonList(dto), dto.getSample(), suppressNavigateToCase);
	}

	public void savePathogenTestForEnvironmentSample(PathogenTestDto dto) {
		savePathogenTestsForEnvironmentSample(Collections.singletonList(dto), dto.getEnvironmentSample());
	}

	public void savePathogenTestsForEnvironmentSample(List<PathogenTestDto> pathogenTests, EnvironmentSampleReferenceDto sampleRef) {
		pathogenTests.forEach(p -> {
			p.setEnvironmentSample(sampleRef);
			facade.savePathogenTest(p);
		});
		Notification.show(I18nProperties.getString(Strings.messagePathogenTestsSavedShort), TRAY_NOTIFICATION);
	}

	public void savePathogenTests(List<PathogenTestDto> pathogenTests, SampleReferenceDto sampleRef, boolean suppressNavigateToCase) {

		final SampleDto sample = FacadeProvider.getSampleFacade().getSampleByUuid(sampleRef.getUuid());

		final CaseReferenceDto associatedCase = sample.getAssociatedCase();
		final ContactReferenceDto associatedContact = sample.getAssociatedContact();
		final EventParticipantReferenceDto associatedEventParticipant = sample.getAssociatedEventParticipant();

		pathogenTests.forEach(p -> {
			p.setSample(sampleRef);
			boolean luxTB = FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG)
				&& Disease.TUBERCULOSIS == p.getTestedDisease();
			//the susceptibility test is applicable only for LUX TB and all-countries invasive disease
			if (PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY == p.getTestType() && !luxTB && !AST_ALLOWED_DISEASES.contains(p.getTestedDisease())) {
				p.setDrugSusceptibility(null);
			}
			facade.savePathogenTest(p);
		});
		if (associatedContact != null) {
			handleAssociatedContact(pathogenTests, associatedContact);
		} else if (associatedEventParticipant != null) {
			handleAssociatedEventParticipant(pathogenTests, associatedEventParticipant);
		} else if (associatedCase != null) {
			handleAssociatedCase(pathogenTests, associatedCase, suppressNavigateToCase);
		}

		Notification.show(
			I18nProperties.getString(pathogenTests.size() == 1 ? Strings.messagePathogenTestSavedShort : Strings.messagePathogenTestsSavedShort),
			TRAY_NOTIFICATION);
	}

	/**
	 * Handles the association of a pathogen test with a case.
	 * Based on pathogen test results the following logic is applied:
	 *
	 * <p>
	 * Negative test result AND test result verified
	 * <ol>
	 * <li>Tested disease == case disease AND test result != sample pathogen test result: Ask user whether to update the sample pathogen
	 * test result</li>
	 * <li>Tested disease != case disease: Do nothing</li>
	 * </ol>
	 * </p>
	 * <p>
	 * Positive test result AND test result verified
	 * <ol>
	 * <li>Tested disease == case disease: Ask user whether to update the sample pathogen test result
	 * <ol>
	 * <li>Tested disease variant != case disease variant: Ask user to change the case disease variant</li>
	 * <li>Case classification != confirmed: Ask user whether to confirm the case</li>
	 * </ol>
	 * </li>
	 * <li>Tested disease != case disease: Ask user to create a new case for the tested disease</li>
	 * </ol>
	 * </p>
	 *
	 * @param pathogenTests
	 *            the pathogen tests
	 * @param associatedCase
	 *            the associated case
	 * @param suppressNavigateToCase
	 *            whether to suppress navigation to the case
	 *
	 */
	private void handleAssociatedCase(List<PathogenTestDto> pathogenTests, CaseReferenceDto associatedCase, boolean suppressNavigateToCase) {

		if (!UiUtil.permitted(UserRight.CASE_EDIT)) {
			return;
		}

		// Negative test result AND test result verified
		// a) Tested disease == case disease AND test result != sample pathogen test result: Ask user whether to update the sample pathogen test result
		// b) Tested disease != case disease: Do nothing

		// Positive test result AND test result verified
		// a) Tested disease == case disease: Ask user whether to update the sample pathogen test result
		// a.1) Tested disease variant != case disease variant: Ask user to change the case disease variant
		// a.2) Case classification != confirmed: Ask user whether to confirm the case
		// b) Tested disease != case disease: Ask user to create a new case for the tested disease

		final CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(associatedCase.getUuid());

		final Map<Disease, List<PathogenTestDto>> testsByDisease =
			pathogenTests.stream().collect(Collectors.groupingBy(PathogenTestDto::getTestedDisease));
		final Optional<PathogenTestDto> positiveWithSameDisease = testsByDisease.getOrDefault(caze.getDisease(), Collections.emptyList())
			.stream()
			.filter(t -> t.getTestResult() == PathogenTestResultType.POSITIVE && Boolean.TRUE.equals(t.getTestResultVerified()))
			.findFirst();

		final Optional<PathogenTestDto> negativeWithSameDisease = testsByDisease.getOrDefault(caze.getDisease(), Collections.emptyList())
			.stream()
			.filter(t -> t.getTestResult() == PathogenTestResultType.NEGATIVE && Boolean.TRUE.equals(t.getTestResultVerified()))
			.findFirst();

		final boolean hasVerifiedPositiveTest = positiveWithSameDisease.isPresent();
		final boolean hasVerifiedNegativeTest = negativeWithSameDisease.isPresent();

		final boolean hasVerifiedTests = hasVerifiedPositiveTest || hasVerifiedNegativeTest;

		// 1. Ask user to update sample overall result if latest test result is different
		// 2. Ask user to update disease variant if case variant is different
		// 3. Ask user if they want to confirm the case only if any of the tests are verified either positive or negative (not pending or other)

		// We need to display popups if any of the tests are verified either positive or negative
		if (hasVerifiedTests) {
			// get either the positive or negative test
			final PathogenTestDto resultedPathogenTest = hasVerifiedPositiveTest ? positiveWithSameDisease.get() : negativeWithSameDisease.get();

			// just a sanity check
			if (resultedPathogenTest == null) {
				throw new IllegalStateException("No verified test found for disease " + caze.getDisease());
			}

			showChangeAssociatedSampleResultDialog(resultedPathogenTest, accepted -> { // Change sample result
				// Accepted SR may have changed
				if (Boolean.TRUE.equals(accepted)) {
					checkForDiseaseVariantUpdate(resultedPathogenTest, caze, suppressNavigateToCase, c -> { // Update disease variant
						// Only show the confirmation dialog if there are verified positive tests
						// We decided this based on the intented text in the dialog but based on the test results instead of the sample overall result
						if (hasVerifiedPositiveTest) {
							// The final laboratory result of the sample the saved pathogen test belongs to is positive. <-- sample overall result
							// However, the case cannot be automatically classified as a confirmed case because it is missing some information.
							// Do you want to set the case classification to confirmed anyway?
							this.showConfirmCaseDialog(c); // Case classification
						}
					});
				}
			});
		}

		testsByDisease.keySet().stream().filter(disease -> disease != caze.getDisease()).forEach((disease) -> {
			List<PathogenTestDto> tests = testsByDisease.get(disease);

			Optional<PathogenTestDto> positiveWithOtherDisease = tests.stream()
				.filter(t -> t.getTestResult() == PathogenTestResultType.POSITIVE && Boolean.TRUE.equals(t.getTestResultVerified()))
				.findFirst();

			if (positiveWithOtherDisease.isPresent()) {
				List<CaseDataDto> duplicatedCases =
					FacadeProvider.getCaseFacade().getDuplicatesWithPathogenTest(caze.getPerson(), positiveWithOtherDisease.get());
				if (duplicatedCases == null || duplicatedCases.isEmpty()) {
					PathogenTestDto positiveTestWithOtherDisease = positiveWithOtherDisease.get();

					showCaseCloningWithNewDiseaseDialog(
						caze,
						positiveTestWithOtherDisease.getTestedDisease(),
						positiveTestWithOtherDisease.getTestedDiseaseDetails(),
						positiveTestWithOtherDisease.getTestedDiseaseVariant(),
						positiveTestWithOtherDisease.getTestedDiseaseVariantDetails());
				}
			}
		});
	}

	private void handleAssociatedContact(List<PathogenTestDto> pathogenTests, ContactReferenceDto associatedContact) {

		if (!UiUtil.permitted(UserRight.CONTACT_EDIT)) {
			return;
		}

		// Negative test result AND test result verified
		// a) Tested disease == contact disease AND test result != sample pathogen test result: Ask user whether to update the sample pathogen test result
		// b) Tested disease != contact disease: Do nothing

		// Positive test result AND test result verified
		// a) Tested disease == contact disease: Ask user to convert the contact to a case
		// a.1) If contact is converted, update the sample pathogen test result
		// a.2) If contact is not converted (or there already is a resulting case), ask user whether to update the sample pathogen test result
		// b) Tested disease != contact disease: Ask user to create a new case for the tested disease

		final ContactDto contact = FacadeProvider.getContactFacade().getByUuid(associatedContact.getUuid());

		Map<Disease, List<PathogenTestDto>> testsByDisease = pathogenTests.stream().collect(Collectors.groupingBy(PathogenTestDto::getTestedDisease));
		Optional<PathogenTestDto> positiveWithSameDisease = testsByDisease.getOrDefault(contact.getDisease(), Collections.emptyList())
			.stream()
			.filter(t -> t.getTestResult() == PathogenTestResultType.POSITIVE && Boolean.TRUE.equals(t.getTestResultVerified()))
			.findFirst();

		Optional<PathogenTestDto> negativeWithSameDisease = testsByDisease.getOrDefault(contact.getDisease(), Collections.emptyList())
			.stream()
			.filter(t -> t.getTestResult() == PathogenTestResultType.NEGATIVE && Boolean.TRUE.equals(t.getTestResultVerified()))
			.findFirst();

		final boolean caseCreationPossible = UiUtil.permitted(FeatureType.CASE_SURVEILANCE, UserRight.CASE_CREATE);
		if (positiveWithSameDisease.isPresent()) {
			if (contact.getResultingCase() == null && !ContactStatus.CONVERTED.equals(contact.getContactStatus()) && caseCreationPossible) {
				showConvertContactToCaseDialog(
					contact,
					converted -> handleCaseCreationFromContactOrEventParticipant(converted, positiveWithSameDisease.get()));
			} else {
				showChangeAssociatedSampleResultDialog(positiveWithSameDisease.get(), null);
			}
		} else if (negativeWithSameDisease.isPresent()) {
			showChangeAssociatedSampleResultDialog(negativeWithSameDisease.get(), null);
		}

		if (caseCreationPossible) {
			testsByDisease.keySet().stream().filter(disease -> disease != contact.getDisease()).forEach((disease) -> {
				List<PathogenTestDto> tests = testsByDisease.get(disease);

				Optional<PathogenTestDto> positiveWithOtherDisease = tests.stream()
					.filter(t -> t.getTestResult() == PathogenTestResultType.POSITIVE && Boolean.TRUE.equals(t.getTestResultVerified()))
					.findFirst();
				if (positiveWithOtherDisease.isPresent()) {
					List<CaseDataDto> duplicatedCases =
						FacadeProvider.getCaseFacade().getDuplicatesWithPathogenTest(contact.getPerson(), positiveWithOtherDisease.get());
					if (CollectionUtils.isEmpty(duplicatedCases)) {
						showCreateContactCaseDialog(contact, positiveWithOtherDisease.get().getTestedDisease());
					}
				}
			});
		}
	}

	private void handleAssociatedEventParticipant(List<PathogenTestDto> pathogenTests, EventParticipantReferenceDto associatedEventParticipant) {

		if (!UiUtil.permitted(UserRight.EVENTPARTICIPANT_EDIT)) {
			return;
		}

		// Negative test result AND test result verified
		// a) Tested disease == event disease AND test result != sample pathogen test result: Ask user whether to update the sample pathogen test result
		// b) Tested disease != event disease: Do nothing

		// Positive test result AND test result verified
		// a) Tested disease == event disease: Ask user to create a case linked to the event participant
		// a.1) If a case is created, update the sample pathogen test result
		// a.2) If no case is created (or there already is an existing case), ask user whether to update the sample pathogen test result
		// b) Tested disease != event disease: Ask user to create a case for the event participant person
		// b.1) If the event has no disease and a case is created, update the sample pathogen test result
		// b.2) If the event has no disease and no case is created, ask user whether to update the sample pathogen test result

		final EventParticipantDto eventParticipant =
			FacadeProvider.getEventParticipantFacade().getEventParticipantByUuid(associatedEventParticipant.getUuid());
		final Disease eventDisease = FacadeProvider.getEventFacade().getEventByUuid(eventParticipant.getEvent().getUuid(), false).getDisease();

		Map<Disease, List<PathogenTestDto>> testsByDisease = pathogenTests.stream().collect(Collectors.groupingBy(PathogenTestDto::getTestedDisease));
		Optional<PathogenTestDto> positiveWithSameDisease = testsByDisease.getOrDefault(eventDisease, Collections.emptyList())
			.stream()
			.filter(t -> t.getTestResult() == PathogenTestResultType.POSITIVE && Boolean.TRUE.equals(t.getTestResultVerified()))
			.findFirst();

		Optional<PathogenTestDto> negativeWithSameDisease = testsByDisease.getOrDefault(eventDisease, Collections.emptyList())
			.stream()
			.filter(t -> t.getTestResult() == PathogenTestResultType.NEGATIVE && Boolean.TRUE.equals(t.getTestResultVerified()))
			.findFirst();

		final boolean caseCreationPossible = UiUtil.permitted(FeatureType.CASE_SURVEILANCE, UserRight.CASE_CREATE);

		if (positiveWithSameDisease.isPresent()) {
			if (eventParticipant.getResultingCase() == null && caseCreationPossible) {
				showConvertEventParticipantToCaseDialog(eventParticipant, positiveWithSameDisease.get().getTestedDisease(), caseCreated -> {
					handleCaseCreationFromContactOrEventParticipant(caseCreated, positiveWithSameDisease.get());
				});
			} else {
				showChangeAssociatedSampleResultDialog(positiveWithSameDisease.get(), null);
			}
		} else if (negativeWithSameDisease.isPresent()) {
			showChangeAssociatedSampleResultDialog(negativeWithSameDisease.get(), null);
		}

		if (caseCreationPossible) {
			testsByDisease.keySet().stream().filter(disease -> disease != eventDisease).forEach((disease) -> {
				List<PathogenTestDto> tests = testsByDisease.get(disease);

				Optional<PathogenTestDto> positiveWithOtherDisease = tests.stream()
					.filter(t -> t.getTestResult() == PathogenTestResultType.POSITIVE && Boolean.TRUE.equals(t.getTestResultVerified()))
					.findFirst();
				if (positiveWithOtherDisease.isPresent() && UiUtil.enabled(FeatureType.CASE_SURVEILANCE)) {
					List<CaseDataDto> duplicatedCases = FacadeProvider.getCaseFacade()
						.getDuplicatesWithPathogenTest(eventParticipant.getPerson().toReference(), positiveWithOtherDisease.get());
					if (CollectionUtils.isEmpty(duplicatedCases)) {
						showConvertEventParticipantToCaseDialog(eventParticipant, positiveWithOtherDisease.get().getTestedDisease(), caseCreated -> {
							if (eventDisease == null) {
								handleCaseCreationFromContactOrEventParticipant(caseCreated, positiveWithOtherDisease.get());
							}
						});
					}
				}
			});
		}
	}

	private void checkForDiseaseVariantUpdate(
		PathogenTestDto test,
		CaseDataDto caze,
		boolean suppressNavigateToCase,
		Consumer<CaseDataDto> callback) {
		if (!DataHelper.equal(test.getTestedDiseaseVariant(), caze.getDiseaseVariant()) && isNotYetRelatedDiseaseVariant(test)) {
			showCaseUpdateWithNewDiseaseVariantDialog(caze, test.getTestedDiseaseVariant(), test.getTestedDiseaseVariantDetails(), yes -> {
				if (yes && !suppressNavigateToCase) {
					ControllerProvider.getCaseController().navigateToCase(caze.getUuid());
				} else if (yes) {
					// Refresh view because it might already show the case
					SormasUI.refreshView();
				}
				// Retrieve the case again because it might have changed
				callback.accept(FacadeProvider.getCaseFacade().getByUuid(caze.getUuid()));
			});
		} else {
			callback.accept(caze);
		}
	}

	private boolean isNotYetRelatedDiseaseVariant(PathogenTestDto savedTest) {
		List<DiseaseVariant> relatedVariants = FacadeProvider.getSampleFacade().getAssociatedDiseaseVariants(savedTest.getSample().getUuid());
		AtomicInteger savedTestsWithSameVariant = new AtomicInteger();
		relatedVariants.forEach(v -> {
			if (v != null && v.equals(savedTest.getTestedDiseaseVariant())) {
				savedTestsWithSameVariant.getAndIncrement();
			}
		});
		return savedTestsWithSameVariant.get() <= 1; // one occurrence is the saved test's one
	}

	private void handleCaseCreationFromContactOrEventParticipant(boolean caseCreated, PathogenTestDto pathogenTest) {
		if (caseCreated) {
			SampleDto sample = FacadeProvider.getSampleFacade().getSampleByUuid(pathogenTest.getSample().getUuid());
			if (sample.getPathogenTestResult() != pathogenTest.getTestResult()) {
				sample.setPathogenTestResult(pathogenTest.getTestResult());
				FacadeProvider.getSampleFacade().saveSample(sample);
			}
		} else {
			showChangeAssociatedSampleResultDialog(pathogenTest, null);
		}
	}

	private void showChangeAssociatedSampleResultDialog(PathogenTestDto dto, Consumer<Boolean> callback) {
		if (dto.getTestResult() != FacadeProvider.getSampleFacade().getSampleByUuid(dto.getSample().getUuid()).getPathogenTestResult()) {
			ControllerProvider.getSampleController()
				.showChangePathogenTestResultWindow(null, dto.getSample().getUuid(), dto.getTestResult(), callback);
		} else if (callback != null) {
			callback.accept(true);
		}
	}

	public void showConvertEventParticipantToCaseDialog(EventParticipantDto eventParticipant, Disease testedDisease, Consumer<Boolean> callback) {
		final EventDto event = FacadeProvider.getEventFacade().getEventByUuid(eventParticipant.getEvent().getUuid(), false);
		final boolean differentDiseases = testedDisease != event.getDisease();
		final boolean noEventDisease = event.getDisease() == null;
		Label dialogContent = noEventDisease
			? new Label(I18nProperties.getString(Strings.messageConvertEventParticipantToCaseNoDisease))
			: differentDiseases
				? new Label(I18nProperties.getString(Strings.messageConvertEventParticipantToCaseDifferentDiseases))
				: new Label(I18nProperties.getString(Strings.messageConvertEventParticipantToCase));
		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getCaption(Captions.convertEventParticipantToCase),
			dialogContent,
			I18nProperties.getString(Strings.yes),
			I18nProperties.getString(Strings.no),
			800,
			confirmed -> {
				if (Boolean.TRUE.equals(confirmed)) {
					if (differentDiseases) {
						ControllerProvider.getCaseController().createFromEventParticipantDifferentDisease(eventParticipant, testedDisease);
					} else {
						ControllerProvider.getCaseController().createFromEventParticipant(eventParticipant);
					}
				}
				callback.accept(confirmed);
			});
	}

	public void showConvertContactToCaseDialog(ContactDto contact, Consumer<Boolean> callback) {
		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getCaption(Captions.convertContactToCase),
			new Label(I18nProperties.getString(Strings.messageConvertContactToCase)),
			I18nProperties.getString(Strings.yes),
			I18nProperties.getString(Strings.no),
			800,
			confirmed -> {
				if (Boolean.TRUE.equals(confirmed)) {
					ControllerProvider.getCaseController().createFromContact(contact);
				}
				callback.accept(confirmed);
			});
	}

	public void showCreateContactCaseDialog(ContactDto contact, Disease disease) {
		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getCaption(Captions.contactCreateContactCase),
			new Label(I18nProperties.getString(Strings.messageConvertContactToCaseDifferentDiseases)),
			I18nProperties.getString(Strings.yes),
			I18nProperties.getString(Strings.no),
			800,
			confirmed -> {
				if (Boolean.TRUE.equals(confirmed)) {
					ControllerProvider.getCaseController().createFromUnrelatedContact(contact, disease);
				}
			});
	}

	public static void showCaseCloningWithNewDiseaseDialog(
		CaseDataDto existingCaseDto,
		Disease disease,
		String diseaseDetails,
		DiseaseVariant diseaseVariant,
		String diseaseVariantDetails) {

		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getCaption(Captions.caseCloneCaseWithNewDisease) + " " + I18nProperties.getEnumCaption(disease) + "?",
			new Label(I18nProperties.getString(Strings.messageCloneCaseWithNewDisease)),
			I18nProperties.getString(Strings.yes),
			I18nProperties.getString(Strings.no),
			800,
			confirmed -> {
				if (Boolean.TRUE.equals(confirmed)) {
					existingCaseDto.setCaseClassification(CaseClassification.NOT_CLASSIFIED);
					existingCaseDto.setClassificationUser(null);
					existingCaseDto.setDisease(disease);
					existingCaseDto.setDiseaseDetails(diseaseDetails);
					existingCaseDto.setDiseaseVariant(diseaseVariant);
					existingCaseDto.setDiseaseVariantDetails(diseaseVariantDetails);
					existingCaseDto.setEpidNumber(null);
					existingCaseDto.setReportDate(new Date());
					CaseDataDto clonedCase = FacadeProvider.getCaseFacade().cloneCase(existingCaseDto);
					ControllerProvider.getCaseController().navigateToCase(clonedCase.getUuid());
				}
			});
	}

	public void showConfirmCaseDialog(CaseDataDto caze) {

		if (FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_GERMANY)) {
			return;
		}

		if (caze.getCaseClassification() == CaseClassification.CONFIRMED) {
			return;
		}

		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getCaption(Captions.caseConfirmCase),
			new Label(I18nProperties.getString(Strings.messageConfirmCaseAfterPathogenTest)),
			I18nProperties.getString(Strings.yes),
			I18nProperties.getString(Strings.no),
			800,
			confirmed -> {
				if (Boolean.TRUE.equals(confirmed)) {
					CaseDataDto caseDataByUuid = FacadeProvider.getCaseFacade().getCaseDataByUuid(caze.getUuid());
					caseDataByUuid.setCaseClassification(CaseClassification.CONFIRMED);
					FacadeProvider.getCaseFacade().save(caseDataByUuid);
					ControllerProvider.getCaseController().navigateToCase(caseDataByUuid.getUuid());
				}
			});
	}
}
