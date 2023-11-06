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

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

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
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleDto;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleReferenceDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestFacade;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class PathogenTestController {

	private final PathogenTestFacade facade = FacadeProvider.getPathogenTestFacade();

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
			PathogenTestDto.build(sampleDto, UserProvider.getCurrent().getUser()),
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
		PathogenTestForm createForm = new PathogenTestForm(sampleDto, true, caseSampleCount, false, true); // Valid because jurisdiction doesn't matter for entities that are about to be created 
		createForm.setValue(pathogenTest);
		final CommitDiscardWrapperComponent<PathogenTestForm> editView = new CommitDiscardWrapperComponent<>(
			createForm,
			UserProvider.getCurrent().hasUserRight(UserRight.PATHOGEN_TEST_CREATE),
			createForm.getFieldGroup());

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

		PathogenTestForm createForm = new PathogenTestForm(sampleDto, true, false, true); // Valid because jurisdiction doesn't matter for entities that are about to be created
		createForm.setValue(PathogenTestDto.build(sampleDto, UserProvider.getCurrent().getUser()));

		final CommitDiscardWrapperComponent<PathogenTestForm> editView = new CommitDiscardWrapperComponent<>(
			createForm,
			UserProvider.getCurrent().hasUserRight(UserRight.PATHOGEN_TEST_CREATE),
			createForm.getFieldGroup());

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
		final PathogenTestForm form;
		if (pathogenTest.getSample() != null) {
			SampleDto sample = FacadeProvider.getSampleFacade().getSampleByUuid(pathogenTest.getSample().getUuid());
			form = new PathogenTestForm(sample, false, 0, pathogenTest.isPseudonymized(), pathogenTest.isInJurisdiction());
		} else {
			EnvironmentSampleDto environmentSample =
				FacadeProvider.getEnvironmentSampleFacade().getByUuid(pathogenTest.getEnvironmentSample().getUuid());
			form = new PathogenTestForm(environmentSample, false, pathogenTest.isPseudonymized(), pathogenTest.isInJurisdiction());
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
				editView.getWrappedComponent().getField(PathogenTestDto.DELETION_REASON).setVisible(true);
				if (editView.getWrappedComponent().getField(PathogenTestDto.DELETION_REASON).getValue() == DeletionReason.OTHER_REASON) {
					editView.getWrappedComponent().getField(PathogenTestDto.OTHER_DELETION_REASON).setVisible(true);
				}
			}
			editView.restrictEditableComponentsOnEditView(
				UserRight.SAMPLE_EDIT,
				UserRight.PATHOGEN_TEST_EDIT,
				UserRight.PATHOGEN_TEST_DELETE,
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
					diseaseVariant.toString())),
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
			facade.savePathogenTest(p);
		});
		if (associatedContact != null) {
			handleAssociatedContact(pathogenTests, associatedContact);
		} else if (associatedEventParticipant != null) {
			handleAssociatedEventParticipant(pathogenTests, associatedEventParticipant);
		} else if (associatedCase != null) {
			handleAssociatedCase(pathogenTests, associatedCase, suppressNavigateToCase);
		}

		Notification.show(I18nProperties.getString(Strings.messagePathogenTestsSavedShort), TRAY_NOTIFICATION);
	}

	private void handleAssociatedCase(List<PathogenTestDto> pathogenTests, CaseReferenceDto associatedCase, boolean suppressNavigateToCase) {

		if (!UserProvider.getCurrent().hasUserRight(UserRight.CASE_EDIT)) {
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

		CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(associatedCase.getUuid());

		Map<Disease, List<PathogenTestDto>> testsByDisease = pathogenTests.stream().collect(Collectors.groupingBy(PathogenTestDto::getTestedDisease));
		Optional<PathogenTestDto> positiveWithSameDisease = testsByDisease.getOrDefault(caze.getDisease(), Collections.emptyList())
			.stream()
			.filter(t -> t.getTestResult() == PathogenTestResultType.POSITIVE && t.getTestResultVerified())
			.findFirst();

		Optional<PathogenTestDto> negativeWithSameDisease = testsByDisease.getOrDefault(caze.getDisease(), Collections.emptyList())
			.stream()
			.filter(t -> t.getTestResult() == PathogenTestResultType.NEGATIVE && t.getTestResultVerified())
			.findFirst();

		if (positiveWithSameDisease.isPresent()) {
			showChangeAssociatedSampleResultDialog(positiveWithSameDisease.get(), (accepted) -> {
				if (accepted) {
					checkForDiseaseVariantUpdate(positiveWithSameDisease.get(), caze, suppressNavigateToCase, this::showConfirmCaseDialog);
				}
			});
		} else if (negativeWithSameDisease.isPresent()) {
			showChangeAssociatedSampleResultDialog(negativeWithSameDisease.get(), null);
		}

		testsByDisease.keySet().stream().filter(disease -> disease != caze.getDisease()).forEach((disease) -> {
			List<PathogenTestDto> tests = testsByDisease.get(disease);

			Optional<PathogenTestDto> positiveWithOtherDisease =
				tests.stream().filter(t -> t.getTestResult() == PathogenTestResultType.POSITIVE && t.getTestResultVerified()).findFirst();

			if (positiveWithOtherDisease.isPresent()) {
				List<CaseDataDto> duplicatedCases =
					FacadeProvider.getCaseFacade().getDuplicatesWithPathogenTest(caze.getPerson(), positiveWithOtherDisease.get());
				if (duplicatedCases == null || duplicatedCases.size() == 0) {
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

		if (!UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_EDIT)) {
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
			.filter(t -> t.getTestResult() == PathogenTestResultType.POSITIVE && t.getTestResultVerified())
			.findFirst();

		Optional<PathogenTestDto> negativeWithSameDisease = testsByDisease.getOrDefault(contact.getDisease(), Collections.emptyList())
			.stream()
			.filter(t -> t.getTestResult() == PathogenTestResultType.NEGATIVE && t.getTestResultVerified())
			.findFirst();

		final boolean isCaseSurveillanceFeatureEnabled =
			FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.CASE_SURVEILANCE);
		if (positiveWithSameDisease.isPresent()) {
			if (contact.getResultingCase() == null
				&& !ContactStatus.CONVERTED.equals(contact.getContactStatus())
				&& isCaseSurveillanceFeatureEnabled) {
				showConvertContactToCaseDialog(
					contact,
					converted -> handleCaseCreationFromContactOrEventParticipant(converted, positiveWithSameDisease.get()));
			} else {
				showChangeAssociatedSampleResultDialog(positiveWithSameDisease.get(), null);
			}
		} else if (negativeWithSameDisease.isPresent()) {
			showChangeAssociatedSampleResultDialog(negativeWithSameDisease.get(), null);
		}

		if (isCaseSurveillanceFeatureEnabled) {
			testsByDisease.keySet().stream().filter(disease -> disease != contact.getDisease()).forEach((disease) -> {
				List<PathogenTestDto> tests = testsByDisease.get(disease);

				Optional<PathogenTestDto> positiveWithOtherDisease =
					tests.stream().filter(t -> t.getTestResult() == PathogenTestResultType.POSITIVE && t.getTestResultVerified()).findFirst();
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

		if (!UserProvider.getCurrent().hasUserRight(UserRight.EVENTPARTICIPANT_EDIT)) {
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
			.filter(t -> t.getTestResult() == PathogenTestResultType.POSITIVE && t.getTestResultVerified())
			.findFirst();

		Optional<PathogenTestDto> negativeWithSameDisease = testsByDisease.getOrDefault(eventDisease, Collections.emptyList())
			.stream()
			.filter(t -> t.getTestResult() == PathogenTestResultType.NEGATIVE && t.getTestResultVerified())
			.findFirst();

		final boolean isCaseSurveillanceFeatureEnabled =
			FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.CASE_SURVEILANCE);

		if (positiveWithSameDisease.isPresent()) {
			if (eventParticipant.getResultingCase() == null && isCaseSurveillanceFeatureEnabled) {
				showConvertEventParticipantToCaseDialog(eventParticipant, positiveWithSameDisease.get().getTestedDisease(), caseCreated -> {
					handleCaseCreationFromContactOrEventParticipant(caseCreated, positiveWithSameDisease.get());
				});
			} else {
				showChangeAssociatedSampleResultDialog(positiveWithSameDisease.get(), null);
			}
		} else if (negativeWithSameDisease.isPresent()) {
			showChangeAssociatedSampleResultDialog(negativeWithSameDisease.get(), null);
		}

		if (isCaseSurveillanceFeatureEnabled) {
			testsByDisease.keySet().stream().filter(disease -> disease != eventDisease).forEach((disease) -> {
				List<PathogenTestDto> tests = testsByDisease.get(disease);

				Optional<PathogenTestDto> positiveWithOtherDisease =
					tests.stream().filter(t -> t.getTestResult() == PathogenTestResultType.POSITIVE && t.getTestResultVerified()).findFirst();
				if (positiveWithOtherDisease.isPresent()
					&& FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.CASE_SURVEILANCE)) {
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
		if (test.getTestedDiseaseVariant() != null
			&& !DataHelper.equal(test.getTestedDiseaseVariant(), caze.getDiseaseVariant())
			&& isNotYetRelatedDiseaseVariant(test)) {
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
				if (confirmed) {
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
				if (confirmed) {
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
				if (confirmed) {
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
				if (confirmed) {
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
				if (confirmed) {
					CaseDataDto caseDataByUuid = FacadeProvider.getCaseFacade().getCaseDataByUuid(caze.getUuid());
					caseDataByUuid.setCaseClassification(CaseClassification.CONFIRMED);
					FacadeProvider.getCaseFacade().save(caseDataByUuid);
					ControllerProvider.getCaseController().navigateToCase(caseDataByUuid.getUuid());
				}
			});
	}
}
