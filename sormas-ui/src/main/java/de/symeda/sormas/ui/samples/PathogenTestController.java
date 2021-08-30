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

import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;

import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactClassification;
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
import de.symeda.sormas.api.user.UserRole;
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
		final CommitDiscardWrapperComponent<PathogenTestForm> editView =
			getPathogenTestCreateComponent(sampleRef, caseSampleCount, callback, onSavedPathogenTest);

		VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.headingCreatePathogenTestResult));
	}

	public CommitDiscardWrapperComponent<PathogenTestForm> getPathogenTestCreateComponent(
		SampleReferenceDto sampleRef,
		int caseSampleCount,
		Runnable callback,
		BiConsumer<PathogenTestDto, Runnable> onSavedPathogenTest) {
		SampleDto sampleDto = FacadeProvider.getSampleFacade().getSampleByUuid(sampleRef.getUuid());
		PathogenTestForm createForm = new PathogenTestForm(sampleDto, true, caseSampleCount, false);
		createForm.setValue(PathogenTestDto.build(sampleDto, UserProvider.getCurrent().getUser()));
		final CommitDiscardWrapperComponent<PathogenTestForm> editView = new CommitDiscardWrapperComponent<>(
			createForm,
			UserProvider.getCurrent().hasUserRight(UserRight.PATHOGEN_TEST_CREATE),
			createForm.getFieldGroup());

		editView.addCommitListener(() -> {
			if (!createForm.getFieldGroup().isModified()) {
				savePathogenTest(createForm.getValue(), onSavedPathogenTest);
				callback.run();
			}
		});
		return editView;
	}

	public void edit(PathogenTestDto dto, int caseSampleCount, Runnable doneCallback, BiConsumer<PathogenTestDto, Runnable> onSavedPathogenTest) {
		final CommitDiscardWrapperComponent<PathogenTestForm> editView =
			getPathogenTestEditComponent(dto, caseSampleCount, doneCallback, onSavedPathogenTest);

		Window popupWindow = VaadinUiUtil.createPopupWindow();

		if (UserProvider.getCurrent().hasUserRole(UserRole.ADMIN)) {
			editView.addDeleteListener(() -> {
				FacadeProvider.getPathogenTestFacade().deletePathogenTest(dto.getUuid());
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
		PathogenTestDto dto,
		int caseSampleCount,
		Runnable doneCallback,
		BiConsumer<PathogenTestDto, Runnable> onSavedPathogenTest) {

		// get fresh data
		PathogenTestDto pathogenTest = facade.getByUuid(dto.getUuid());
		SampleDto sample = FacadeProvider.getSampleFacade().getSampleByUuid(dto.getSample().getUuid());
		PathogenTestForm form = new PathogenTestForm(sample, false, caseSampleCount, pathogenTest.isPseudonymized());
		form.setValue(pathogenTest);

		final CommitDiscardWrapperComponent<PathogenTestForm> editView =
			new CommitDiscardWrapperComponent<>(form, UserProvider.getCurrent().hasUserRight(UserRight.PATHOGEN_TEST_EDIT), form.getFieldGroup());

		editView.addCommitListener(() -> {
			if (!form.getFieldGroup().isModified()) {
				savePathogenTest(form.getValue(), onSavedPathogenTest);
				doneCallback.run();
			}
		});

		return editView;
	}

	public static void showCaseUpdateWithNewDiseaseVariantDialog(CaseDataDto existingCaseDto, DiseaseVariant diseaseVariant) {

		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getString(Strings.headingUpdateCaseWithNewDiseaseVariant),
			new Label(I18nProperties.getString(Strings.messageUpdateCaseWithNewDiseaseVariant)),
			I18nProperties.getString(Strings.yes),
			I18nProperties.getString(Strings.no),
			800,
			e -> {
				if (e) {
					CaseDataDto caseDataByUuid = FacadeProvider.getCaseFacade().getCaseDataByUuid(existingCaseDto.getUuid());
					caseDataByUuid.setDiseaseVariant(diseaseVariant);
					FacadeProvider.getCaseFacade().saveCase(caseDataByUuid);
					ControllerProvider.getCaseController().navigateToCase(caseDataByUuid.getUuid());
				}
			});
	}

	private void savePathogenTest(PathogenTestDto dto, BiConsumer<PathogenTestDto, Runnable> onSavedPathogenTest) {
		facade.savePathogenTest(dto);
		final SampleDto sample = FacadeProvider.getSampleFacade().getSampleByUuid(dto.getSample().getUuid());
		final CaseReferenceDto associatedCase = sample.getAssociatedCase();
		final ContactReferenceDto associatedContact = sample.getAssociatedContact();
		final EventParticipantReferenceDto associatedEventParticipant = sample.getAssociatedEventParticipant();
		if (associatedCase != null) {
			handleAssociatedCase(dto, onSavedPathogenTest, associatedCase);
		} else if (associatedContact != null) {
			handleAssociatedContact(dto, onSavedPathogenTest, associatedContact);
		} else if (associatedEventParticipant != null) {
			handleAssociatedEventParticipant(dto, onSavedPathogenTest, associatedEventParticipant);
		}
	}

	private void handleAssociatedCase(
		PathogenTestDto dto,
		BiConsumer<PathogenTestDto, Runnable> onSavedPathogenTest,
		CaseReferenceDto associatedCase) {
		CaseDataDto preSaveCaseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(associatedCase.getUuid());
		CaseDataDto postSaveCaseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(associatedCase.getUuid());
		showSaveNotification(preSaveCaseDto, postSaveCaseDto);

		Runnable confirmCaseCallback = () -> {
			if (dto.getTestedDisease() == postSaveCaseDto.getDisease()
				&& PathogenTestResultType.POSITIVE.equals(dto.getTestResult())
				&& dto.getTestResultVerified()
				&& postSaveCaseDto.getCaseClassification() != CaseClassification.CONFIRMED
				&& postSaveCaseDto.getCaseClassification() != CaseClassification.NO_CASE) {
				showConfirmCaseDialog(postSaveCaseDto);
			}
		};

		Runnable caseCloningCallback = () -> {
			if (dto.getTestedDisease() != postSaveCaseDto.getDisease()
				&& dto.getTestResult() == PathogenTestResultType.POSITIVE
				&& dto.getTestResultVerified()) {
				showCaseCloningWithNewDiseaseDialog(postSaveCaseDto, dto.getTestedDisease());
			}
		};

		Runnable caseDiseaseVariantCallback = () -> {
			if (dto.getTestedDiseaseVariant() != null
				&& !DataHelper.equal(dto.getTestedDiseaseVariant(), postSaveCaseDto.getDiseaseVariant())
				&& dto.getTestResult() == PathogenTestResultType.POSITIVE
				&& dto.getTestResultVerified()) {
				showCaseUpdateWithNewDiseaseVariantDialog(postSaveCaseDto, dto.getTestedDiseaseVariant());
			}
		};

		if (onSavedPathogenTest != null) {
			onSavedPathogenTest.accept(dto, () -> {
				confirmCaseCallback.run();
				caseCloningCallback.run();
				caseDiseaseVariantCallback.run();
			});
		} else {
			confirmCaseCallback.run();
			caseCloningCallback.run();
			caseDiseaseVariantCallback.run();
		}
	}

	private void handleAssociatedContact(
		PathogenTestDto dto,
		BiConsumer<PathogenTestDto, Runnable> onSavedPathogenTest,
		ContactReferenceDto associatedContact) {
		final ContactDto contact = FacadeProvider.getContactFacade().getContactByUuid(associatedContact.getUuid());
		Runnable contactConvertToCaseCallback = () -> {
			if (PathogenTestResultType.POSITIVE.equals(dto.getTestResult())
				&& dto.getTestResultVerified()
				&& ContactClassification.UNCONFIRMED.equals(contact.getContactClassification())
				&& !ContactStatus.CONVERTED.equals(contact.getContactStatus())) {
				if (contact.getDisease() != null && contact.getDisease().equals(dto.getTestedDisease())) {
					showConvertContactToCaseDialog(contact);
				} else if (contact.getDisease() != null) {
					showCreateContactCaseDialog(contact, dto.getTestedDisease());
				}
			}
		};
		if (onSavedPathogenTest != null) {
			onSavedPathogenTest.accept(dto, contactConvertToCaseCallback);
		} else {
			contactConvertToCaseCallback.run();
		}
	}

	private void handleAssociatedEventParticipant(
		PathogenTestDto dto,
		BiConsumer<PathogenTestDto, Runnable> onSavedPathogenTest,
		EventParticipantReferenceDto associatedEventParticipant) {
		final EventParticipantDto eventParticipant =
			FacadeProvider.getEventParticipantFacade().getEventParticipantByUuid(associatedEventParticipant.getUuid());
		Runnable eventParticipantConvertToCaseCallback = () -> {
			if (PathogenTestResultType.POSITIVE.equals(dto.getTestResult()) && dto.getTestResultVerified()) {
				showConvertEventParticipantToCaseDialog(eventParticipant, dto.getTestedDisease());
			}
		};
		if (onSavedPathogenTest != null) {
			onSavedPathogenTest.accept(dto, eventParticipantConvertToCaseCallback);
		} else {
			eventParticipantConvertToCaseCallback.run();
		}
	}

	public void showConvertEventParticipantToCaseDialog(EventParticipantDto eventParticipant, Disease testedDisease) {
		final EventDto event = FacadeProvider.getEventFacade().getEventByUuid(eventParticipant.getEvent().getUuid(), false);
		final boolean differentDiseases = event.getDisease() == null || !event.getDisease().equals(testedDisease);
		Label dialogContent = differentDiseases
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
			});
	}

	public void showConvertContactToCaseDialog(ContactDto contact) {
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
			});
	}

	public void showCreateContactCaseDialog(ContactDto contact, Disease disease) {
		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getCaption(Captions.contactCreateContactCase),
			new Label(I18nProperties.getString(Strings.messageCreateContactCase)),
			I18nProperties.getString(Strings.yes),
			I18nProperties.getString(Strings.no),
			800,
			confirmed -> {
				if (confirmed) {
					ControllerProvider.getCaseController().createFromUnrelatedContact(contact, disease);
				}
			});
	}

	public static void showCaseCloningWithNewDiseaseDialog(CaseDataDto existingCaseDto, Disease disease) {

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
					clonedCase.setEpidNumber(null);
					clonedCase.setReportDate(new Date());
					FacadeProvider.getCaseFacade().saveCase(clonedCase);
					ControllerProvider.getCaseController().navigateToCase(clonedCase.getUuid());
				}
			});
	}

	public void showConfirmCaseDialog(CaseDataDto caze) {

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
				}
			});

	}

	private void showSaveNotification(CaseDataDto existingCaseDto, CaseDataDto newCaseDto) {
		if (isNewCaseClassification(existingCaseDto, newCaseDto)) {
			Notification.show(
				String.format(I18nProperties.getString(Strings.messagePathogenTestSaved), newCaseDto.getCaseClassification().toString()),
				Type.TRAY_NOTIFICATION);
		} else {
			Notification.show(I18nProperties.getString(Strings.messagePathogenTestSavedShort), Type.TRAY_NOTIFICATION);
		}
	}

	private boolean isNewCaseClassification(CaseDataDto existingCaseDto, CaseDataDto newCaseDto) {
		return existingCaseDto.getCaseClassification() != newCaseDto.getCaseClassification() && newCaseDto.getClassificationUser() == null;
	}
}
