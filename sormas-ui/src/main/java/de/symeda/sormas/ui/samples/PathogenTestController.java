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

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;

import com.vaadin.server.Page;
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
import de.symeda.sormas.api.contact.ContactReferenceDto;
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
		PathogenTestForm createForm = new PathogenTestForm(sampleDto, true, caseSampleCount);
		createForm.setValue(PathogenTestDto.build(sampleDto, UserProvider.getCurrent().getUser()));
		final CommitDiscardWrapperComponent<PathogenTestForm> editView = new CommitDiscardWrapperComponent<PathogenTestForm>(
			createForm,
			UserProvider.getCurrent().hasUserRight(UserRight.PATHOGEN_TEST_CREATE),
			createForm.getFieldGroup());

		editView.addCommitListener(() -> {
			if (!createForm.getFieldGroup().isModified()) {
				savePathogenTest(createForm.getValue(), onSavedPathogenTest);
				callback.run();
			}
		});

		VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.headingCreatePathogenTestResult));
	}

	public void edit(PathogenTestDto dto, int caseSampleCount, Runnable doneCallback, BiConsumer<PathogenTestDto, Runnable> onSavedPathogenTest) {
		// get fresh data
		PathogenTestDto newDto = facade.getByUuid(dto.getUuid());

		PathogenTestForm form =
			new PathogenTestForm(FacadeProvider.getSampleFacade().getSampleByUuid(dto.getSample().getUuid()), false, caseSampleCount);
		form.setValue(newDto);
		final CommitDiscardWrapperComponent<PathogenTestForm> editView = new CommitDiscardWrapperComponent<PathogenTestForm>(
			form,
			UserProvider.getCurrent().hasUserRight(UserRight.PATHOGEN_TEST_EDIT),
			form.getFieldGroup());

		Window popupWindow = VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.headingEditPathogenTestResult));

		editView.addCommitListener(() -> {
			if (!form.getFieldGroup().isModified()) {
				savePathogenTest(form.getValue(), onSavedPathogenTest);
				doneCallback.run();
			}
		});

		if (UserProvider.getCurrent().hasUserRole(UserRole.ADMIN)) {
			editView.addDeleteListener(() -> {
				FacadeProvider.getPathogenTestFacade().deletePathogenTest(dto.getUuid());
				UI.getCurrent().removeWindow(popupWindow);
				doneCallback.run();
			}, I18nProperties.getCaption(PathogenTestDto.I18N_PREFIX));
		}
	}

	private void savePathogenTest(PathogenTestDto dto, BiConsumer<PathogenTestDto, Runnable> onSavedPathogenTest) {
		final SampleDto sample = FacadeProvider.getSampleFacade().getSampleByUuid(dto.getSample().getUuid());
		final CaseReferenceDto associatedCase = sample.getAssociatedCase();
		final ContactReferenceDto associatedContact = sample.getAssociatedContact();
		final EventParticipantReferenceDto associatedEventParticipant = sample.getAssociatedEventParticipant();
		if (associatedCase != null) {
			CaseDataDto preSaveCaseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(associatedCase.getUuid());
			facade.savePathogenTest(dto);
			CaseDataDto postSaveCaseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(associatedCase.getUuid());
			showSaveNotification(preSaveCaseDto, postSaveCaseDto);

			Runnable confirmCaseCallback = () -> {
				if (dto.getTestedDisease() == postSaveCaseDto.getDisease()
					&& PathogenTestResultType.POSITIVE.equals(dto.getTestResult())
					&& dto.getTestResultVerified().booleanValue() == true
					&& postSaveCaseDto.getCaseClassification() != CaseClassification.CONFIRMED
					&& postSaveCaseDto.getCaseClassification() != CaseClassification.NO_CASE) {
					showConfirmCaseDialog(postSaveCaseDto);
				}
			};

			Runnable caseCloningCallback = () -> {
				if (dto.getTestedDisease() != postSaveCaseDto.getDisease()
					&& dto.getTestResult() == PathogenTestResultType.POSITIVE
					&& dto.getTestResultVerified().booleanValue() == true) {
					showCaseCloningWithNewDiseaseDialog(postSaveCaseDto, dto.getTestedDisease());
				}
			};

			if (onSavedPathogenTest != null) {
				onSavedPathogenTest.accept(dto, () -> {
					confirmCaseCallback.run();
					caseCloningCallback.run();
				});
			} else {
				confirmCaseCallback.run();
				caseCloningCallback.run();
			}
		} else if (associatedContact != null) {
			facade.savePathogenTest(dto);
		} else if (associatedEventParticipant != null) {
			facade.savePathogenTest(dto);
			final EventParticipantDto eventParticipant =
				FacadeProvider.getEventParticipantFacade().getEventParticipantByUuid(associatedEventParticipant.getUuid());

			Runnable eventParticipantConvertToCaseCallback = () -> {
				if (PathogenTestResultType.POSITIVE.equals(dto.getTestResult()) && dto.getTestResultVerified().booleanValue() == true) {
					showConvertEventParticipantToCaseDialog(eventParticipant);
				}
			};

			if (onSavedPathogenTest != null) {
				onSavedPathogenTest.accept(dto, () -> eventParticipantConvertToCaseCallback.run());
			} else {
				eventParticipantConvertToCaseCallback.run();
			}
		}
	}

	public void showConvertEventParticipantToCaseDialog(EventParticipantDto eventParticipant) {
		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getCaption(Captions.convertEventParticipantToCase),
			new Label(I18nProperties.getString(Strings.messageConvertEventParticipantToCase)),
			I18nProperties.getString(Strings.yes),
			I18nProperties.getString(Strings.no),
			800,
			e -> {
				if (e.booleanValue() == true) {
					ControllerProvider.getCaseController().createFromEventParticipant(eventParticipant);
				}
			});
	}

	private void showCaseCloningWithNewDiseaseDialog(CaseDataDto existingCaseDto, Disease disease) {

		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getCaption(Captions.caseCloneCaseWithNewDisease) + " " + I18nProperties.getEnumCaption(disease) + "?",
			new Label(I18nProperties.getString(Strings.messageCloneCaseWithNewDisease)),
			I18nProperties.getString(Strings.yes),
			I18nProperties.getString(Strings.no),
			800,
			e -> {
				if (e.booleanValue() == true) {
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

	private void showConfirmCaseDialog(CaseDataDto caze) {

		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getCaption(Captions.caseConfirmCase),
			new Label(I18nProperties.getString(Strings.messageConfirmCaseAfterPathogenTest)),
			I18nProperties.getString(Strings.yes),
			I18nProperties.getString(Strings.no),
			800,
			e -> {
				if (e.booleanValue() == true) {
					caze.setCaseClassification(CaseClassification.CONFIRMED);
					FacadeProvider.getCaseFacade().saveCase(caze);
				}
			});

	}

	private void showSaveNotification(CaseDataDto existingCaseDto, CaseDataDto newCaseDto) {

		if (existingCaseDto.getCaseClassification() != newCaseDto.getCaseClassification() && newCaseDto.getClassificationUser() == null) {
			Notification.show(
				String.format(I18nProperties.getString(Strings.messagePathogenTestSaved), newCaseDto.getCaseClassification().toString()),
				Type.TRAY_NOTIFICATION);
		} else {
			Notification.show(I18nProperties.getString(Strings.messagePathogenTestSavedShort), Type.TRAY_NOTIFICATION);
		}
	}

	public void deleteAllSelectedItems(Collection<Object> selectedRows, Runnable callback) {

		if (selectedRows.size() == 0) {
			new Notification(
				I18nProperties.getString(Strings.headingNoPathogenTestsSelected),
				I18nProperties.getString(Strings.messageNoPathogenTestsSelected),
				Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showDeleteConfirmationWindow(
				String.format(I18nProperties.getString(Strings.confirmationDeletePathogenTests), selectedRows.size()),
				() -> {
					for (Object selectedRow : selectedRows) {
						FacadeProvider.getPathogenTestFacade().deletePathogenTest(((PathogenTestDto) selectedRow).getUuid());
					}
					callback.run();
					new Notification(
						I18nProperties.getString(Strings.headingPathogenTestsDeleted),
						I18nProperties.getString(Strings.messagePathogenTestsDeleted),
						Type.HUMANIZED_MESSAGE,
						false).show(Page.getCurrent());
				});
		}
	}
}
