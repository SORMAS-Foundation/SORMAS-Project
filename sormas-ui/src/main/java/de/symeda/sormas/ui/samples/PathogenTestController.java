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

import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
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

	public void create(
		SampleReferenceDto sampleRef,
		int caseSampleCount,
		Runnable callback,
		BiConsumer<PathogenTestDto, Runnable> onSavedPathogenTest) {
		SampleDto sampleDto = FacadeProvider.getSampleFacade().getSampleByUuid(sampleRef.getUuid());
		final CommitDiscardWrapperComponent<PathogenTestForm> editView =
			getPathogenTestCreateComponent(sampleDto, caseSampleCount, callback, onSavedPathogenTest, false);

		VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.headingCreatePathogenTestResult));
	}

	public CommitDiscardWrapperComponent<PathogenTestForm> getPathogenTestCreateComponent(
		SampleDto sampleDto,
		int caseSampleCount,
		Runnable callback,
		BiConsumer<PathogenTestDto, Runnable> onSavedPathogenTest,
		boolean suppressNavigateToCase) {
		PathogenTestForm createForm = new PathogenTestForm(sampleDto, true, caseSampleCount, false);
		createForm.setValue(PathogenTestDto.build(sampleDto, UserProvider.getCurrent().getUser()));
		final CommitDiscardWrapperComponent<PathogenTestForm> editView = new CommitDiscardWrapperComponent<>(
			createForm,
			UserProvider.getCurrent().hasUserRight(UserRight.PATHOGEN_TEST_CREATE),
			createForm.getFieldGroup());

		editView.addCommitListener(() -> {
			if (!createForm.getFieldGroup().isModified()) {
				savePathogenTest(createForm.getValue(), onSavedPathogenTest, false, suppressNavigateToCase);
				callback.run();
			}
		});
		return editView;
	}

	public void edit(String pathogenTestUuid, Runnable doneCallback, BiConsumer<PathogenTestDto, Runnable> onSavedPathogenTest) {
		final CommitDiscardWrapperComponent<PathogenTestForm> editView =
			getPathogenTestEditComponent(pathogenTestUuid, doneCallback, onSavedPathogenTest);

		Window popupWindow = VaadinUiUtil.createPopupWindow();

		if (UserProvider.getCurrent().hasUserRight(UserRight.PATHOGEN_TEST_DELETE)) {
			editView.addDeleteListener(() -> {
				FacadeProvider.getPathogenTestFacade().deletePathogenTest(pathogenTestUuid);
				UI.getCurrent().removeWindow(popupWindow);
				doneCallback.run();
			}, I18nProperties.getCaption(PathogenTestDto.I18N_PREFIX));
		}
		editView.addCommitListener(popupWindow::close);
		editView.addDiscardListener(popupWindow::close);

		popupWindow.setContent(editView);
		popupWindow.setCaption(I18nProperties.getString(Strings.headingEditPathogenTestResult));
		UI.getCurrent().addWindow(popupWindow);
	}

	public CommitDiscardWrapperComponent<PathogenTestForm> getPathogenTestEditComponent(
		String pathogenTestUuid,
		Runnable doneCallback,
		BiConsumer<PathogenTestDto, Runnable> onSavedPathogenTest) {

		// get fresh data
		PathogenTestDto pathogenTest = facade.getByUuid(pathogenTestUuid);
		SampleDto sample = FacadeProvider.getSampleFacade().getSampleByUuid(pathogenTest.getSample().getUuid());
		PathogenTestForm form = new PathogenTestForm(sample, false, 0, pathogenTest.isPseudonymized());
		form.setValue(pathogenTest);

		final CommitDiscardWrapperComponent<PathogenTestForm> editView =
			new CommitDiscardWrapperComponent<>(form, UserProvider.getCurrent().hasUserRight(UserRight.PATHOGEN_TEST_EDIT), form.getFieldGroup());

		editView.addCommitListener(() -> {
			if (!form.getFieldGroup().isModified()) {
				savePathogenTest(form.getValue(), onSavedPathogenTest, false, false);
				doneCallback.run();
			}
		});

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
					FacadeProvider.getCaseFacade().saveCase(caseDataByUuid);
				}
				if (callback != null) {
					callback.accept(yes);
				}
			});
	}

	public PathogenTestDto savePathogenTest(
		PathogenTestDto dto,
		BiConsumer<PathogenTestDto, Runnable> onSavedPathogenTest,
		boolean suppressSampleResultUpdatePopup,
		boolean suppressNavigateToCase) {
		PathogenTestDto savedDto = facade.savePathogenTest(dto);
		final SampleDto sample = FacadeProvider.getSampleFacade().getSampleByUuid(dto.getSample().getUuid());
		final CaseReferenceDto associatedCase = sample.getAssociatedCase();
		final ContactReferenceDto associatedContact = sample.getAssociatedContact();
		final EventParticipantReferenceDto associatedEventParticipant = sample.getAssociatedEventParticipant();
		if (associatedCase != null) {
			handleAssociatedCase(dto, onSavedPathogenTest, associatedCase, suppressSampleResultUpdatePopup, suppressNavigateToCase);
		}
		if (associatedContact != null) {
			handleAssociatedContact(dto, onSavedPathogenTest, associatedContact, suppressSampleResultUpdatePopup);
		}
		if (associatedEventParticipant != null) {
			handleAssociatedEventParticipant(dto, onSavedPathogenTest, associatedEventParticipant, suppressSampleResultUpdatePopup);
		}
		Notification.show(I18nProperties.getString(Strings.messagePathogenTestSavedShort), TRAY_NOTIFICATION);
		return savedDto;
	}

	private void handleAssociatedCase(
		PathogenTestDto dto,
		BiConsumer<PathogenTestDto, Runnable> onSavedPathogenTest,
		CaseReferenceDto associatedCase,
		boolean suppressSampleResultUpdatePopup,
		boolean suppressNavigateToCase) {

		// Negative test result AND test result verified
		// a) Tested disease == case disease AND test result != sample pathogen test result: Ask user whether to update the sample pathogen test result
		// b) Tested disease != case disease: Do nothing

		// Positive test result AND test result verified
		// a) Tested disease == case disease: Ask user whether to update the sample pathogen test result
		// a.1) Tested disease variant != case disease variant: Ask user to change the case disease variant
		// a.2) Case classification != confirmed: Ask user whether to confirm the case
		// b) Tested disease != case disease: Ask user to create a new case for the tested disease

		CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(associatedCase.getUuid());

		final boolean equalDisease = dto.getTestedDisease() == caze.getDisease();

		Runnable callback = () -> {
			if (equalDisease
				&& PathogenTestResultType.NEGATIVE.equals(dto.getTestResult())
				&& dto.getTestResultVerified()
				&& !suppressSampleResultUpdatePopup) {
				showChangeAssociatedSampleResultDialog(dto, null);
			} else if (PathogenTestResultType.POSITIVE.equals(dto.getTestResult()) && dto.getTestResultVerified()) {
				if (equalDisease && suppressSampleResultUpdatePopup) {
					checkForDiseaseVariantUpdate(dto, caze, suppressNavigateToCase, this::showConfirmCaseDialog);
				} else if (equalDisease) {
					showChangeAssociatedSampleResultDialog(dto, (accepted) -> {
						if (accepted) {
							checkForDiseaseVariantUpdate(dto, caze, suppressNavigateToCase, this::showConfirmCaseDialog);
						}
					});
				} else {
					showCaseCloningWithNewDiseaseDialog(
						caze,
						dto.getTestedDisease(),
						dto.getTestedDiseaseDetails(),
						dto.getTestedDiseaseVariant(),
						dto.getTestedDiseaseVariantDetails());
				}
			}
		};

		if (onSavedPathogenTest != null) {
			onSavedPathogenTest.accept(dto, callback);
		} else {
			callback.run();
		}
	}

	private void handleAssociatedContact(
		PathogenTestDto dto,
		BiConsumer<PathogenTestDto, Runnable> onSavedPathogenTest,
		ContactReferenceDto associatedContact,
		boolean suppressSampleResultUpdatePopup) {

		// Negative test result AND test result verified
		// a) Tested disease == contact disease AND test result != sample pathogen test result: Ask user whether to update the sample pathogen test result
		// b) Tested disease != contact disease: Do nothing

		// Positive test result AND test result verified
		// a) Tested disease == contact disease: Ask user to convert the contact to a case
		// a.1) If contact is converted, update the sample pathogen test result
		// a.2) If contact is not converted (or there already is a resulting case), ask user whether to update the sample pathogen test result
		// b) Tested disease != contact disease: Ask user to create a new case for the tested disease

		final ContactDto contact = FacadeProvider.getContactFacade().getContactByUuid(associatedContact.getUuid());
		final boolean equalDisease = dto.getTestedDisease() == contact.getDisease();

		Runnable callback = () -> {
			if (equalDisease
				&& PathogenTestResultType.NEGATIVE.equals(dto.getTestResult())
				&& dto.getTestResultVerified()
				&& !suppressSampleResultUpdatePopup) {
				showChangeAssociatedSampleResultDialog(dto, null);
			} else if (PathogenTestResultType.POSITIVE.equals(dto.getTestResult()) && dto.getTestResultVerified()) {
				if (equalDisease) {
					if (contact.getResultingCase() == null && !ContactStatus.CONVERTED.equals(contact.getContactStatus())) {
						showConvertContactToCaseDialog(contact, converted -> {
							handleCaseCreationFromContactOrEventParticipant(converted, dto);
						});
					} else if (!suppressSampleResultUpdatePopup) {
						showChangeAssociatedSampleResultDialog(dto, null);
					}
				} else {
					showCreateContactCaseDialog(contact, dto.getTestedDisease());
				}
			}
		};

		if (onSavedPathogenTest != null) {
			onSavedPathogenTest.accept(dto, callback);
		} else {
			callback.run();
		}
	}

	private void handleAssociatedEventParticipant(
		PathogenTestDto dto,
		BiConsumer<PathogenTestDto, Runnable> onSavedPathogenTest,
		EventParticipantReferenceDto associatedEventParticipant,
		boolean suppressSampleResultUpdatePopup) {

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
		final boolean equalDisease = eventDisease != null && eventDisease.equals(dto.getTestedDisease());

		Runnable callback = () -> {
			if (equalDisease
				&& PathogenTestResultType.NEGATIVE.equals(dto.getTestResult())
				&& dto.getTestResultVerified()
				&& !suppressSampleResultUpdatePopup) {
				showChangeAssociatedSampleResultDialog(dto, null);
			} else if (PathogenTestResultType.POSITIVE.equals(dto.getTestResult()) && dto.getTestResultVerified()) {
				if (equalDisease) {
					if (eventParticipant.getResultingCase() == null) {
						showConvertEventParticipantToCaseDialog(eventParticipant, dto.getTestedDisease(), caseCreated -> {
							handleCaseCreationFromContactOrEventParticipant(caseCreated, dto);
						});
					} else if (!suppressSampleResultUpdatePopup) {
						showChangeAssociatedSampleResultDialog(dto, null);
					}
				} else {
					showConvertEventParticipantToCaseDialog(eventParticipant, dto.getTestedDisease(), caseCreated -> {
						if (eventDisease == null) {
							handleCaseCreationFromContactOrEventParticipant(caseCreated, dto);
						}
					});
				}
			}
		};

		if (onSavedPathogenTest != null) {
			onSavedPathogenTest.accept(dto, callback);
		} else {
			callback.run();
		}
	}

	private void checkForDiseaseVariantUpdate(PathogenTestDto test, CaseDataDto caze, boolean suppressNavigateToCase, Consumer<CaseDataDto> callback) {
		if (test.getTestedDiseaseVariant() != null && !DataHelper.equal(test.getTestedDiseaseVariant(), caze.getDiseaseVariant())) {
			showCaseUpdateWithNewDiseaseVariantDialog(caze, test.getTestedDiseaseVariant(), test.getTestedDiseaseVariantDetails(), yes -> {
				if (yes && !suppressNavigateToCase) {
					ControllerProvider.getCaseController().navigateToCase(caze.getUuid());
				}
				// Retrieve the case again because it might have changed
				callback.accept(FacadeProvider.getCaseFacade().getByUuid(caze.getUuid()));
			});
		} else {
			callback.accept(caze);
		}
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
					CaseDataDto clonedCase = FacadeProvider.getCaseFacade().cloneCase(existingCaseDto);
					clonedCase.setCaseClassification(CaseClassification.NOT_CLASSIFIED);
					clonedCase.setClassificationUser(null);
					clonedCase.setDisease(disease);
					clonedCase.setDiseaseDetails(diseaseDetails);
					clonedCase.setDiseaseVariant(diseaseVariant);
					clonedCase.setDiseaseVariantDetails(diseaseVariantDetails);
					clonedCase.setEpidNumber(null);
					clonedCase.setReportDate(new Date());
					FacadeProvider.getCaseFacade().saveCase(clonedCase);
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
					FacadeProvider.getCaseFacade().saveCase(caseDataByUuid);
					ControllerProvider.getCaseController().navigateToCase(caseDataByUuid.getUuid());
				}
			});
	}

}
