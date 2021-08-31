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
import java.util.function.Consumer;

import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseLogic;
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
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
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

	public PathogenTestDto savePathogenTest(PathogenTestDto dto, BiConsumer<PathogenTestDto, Runnable> onSavedPathogenTest) {
		PathogenTestDto savedDto = facade.savePathogenTest(dto);
		final SampleDto sample = FacadeProvider.getSampleFacade().getSampleByUuid(dto.getSample().getUuid());
		final CaseReferenceDto associatedCase = sample.getAssociatedCase();
		final ContactReferenceDto associatedContact = sample.getAssociatedContact();
		final EventParticipantReferenceDto associatedEventParticipant = sample.getAssociatedEventParticipant();
		if (associatedCase != null) {
			handleAssociatedCase(dto, onSavedPathogenTest, associatedCase);
		}
		if (associatedContact != null) {
			handleAssociatedContact(dto, onSavedPathogenTest, associatedContact);
		}
		if (associatedEventParticipant != null) {
			handleAssociatedEventParticipant(dto, onSavedPathogenTest, associatedEventParticipant);
		}
		return savedDto;
	}

	private void handleAssociatedCase(
		PathogenTestDto dto,
		BiConsumer<PathogenTestDto, Runnable> onSavedPathogenTest,
		CaseReferenceDto associatedCase) {
		CaseDataDto preSaveCaseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(associatedCase.getUuid());
		CaseDataDto postSaveCaseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(associatedCase.getUuid());
		showSaveNotification(preSaveCaseDto, postSaveCaseDto);

		// RULESET according to #5816
		// 1. If pathogentestresult negative AND confirmed
		// 1.1 If Diseases equal: ask user whether to update sampleResult (if sampleResult != pathogenTestResult)
		// 1.1.1 if yes, update case classification to probable if (sampleCollectionDate - mostRelevantDate < 30)
		// 1.2 if Diseases do not Equal: do nothing

		// 2. If pathogentestresult positive AND confirmed
		// 2.1 If Diseases Equal: ask user if sampleResult should be updated, change classification if yes & (sampleCollectionDate - mostRelevantDate < 30)
		// 2.1.1 If Disease Variant does not equal: show dialogue suggesting changing the variant
		// 2.2 If Diseases do not Equal: Suggest creating new case for that disease and set classification of the new case to confirmed. SampleResult remains unchanged

		final Boolean equalDisease = dto.getTestedDisease() == postSaveCaseDto.getDisease();

		// 1.
		Runnable negativeTestCallback = () -> {
			if (equalDisease && PathogenTestResultType.NEGATIVE.equals(dto.getTestResult()) && dto.getTestResultVerified()) {
				// 1.1
				changeAssociatedSampleResult(dto, () -> {
				}, () -> {
					// 1.1.1
					Date sampleCollectionDate = FacadeProvider.getSampleFacade().getSampleByUuid(dto.getSample().getUuid()).getSampleDateTime();
					if (DateHelper.subtractDays(sampleCollectionDate, 30).before(CaseLogic.getStartDate(postSaveCaseDto))) {
						CaseDataDto caseDataByUuid = FacadeProvider.getCaseFacade().getCaseDataByUuid(postSaveCaseDto.getUuid());
						caseDataByUuid.setCaseClassification(CaseClassification.PROBABLE);
						caseDataByUuid = FacadeProvider.getCaseFacade().saveCase(caseDataByUuid);

						showSaveNotification(preSaveCaseDto, caseDataByUuid);
					}

				});
			}
		};

		// 2.
		Runnable positiveTestCallback = () -> {
			if (PathogenTestResultType.POSITIVE.equals(dto.getTestResult()) && dto.getTestResultVerified()) {
				if (equalDisease) {
					// 2.1
					changeAssociatedSampleResult(dto, () -> {
						Date sampleCollectionDate = FacadeProvider.getSampleFacade().getSampleByUuid(dto.getSample().getUuid()).getSampleDateTime();
						if (DateHelper.subtractDays(sampleCollectionDate, 30).before(CaseLogic.getStartDate(postSaveCaseDto))) {
							showConfirmCaseDialog(postSaveCaseDto);
						}

						// 2.1.1
						if (dto.getTestedDiseaseVariant() != null
							&& !DataHelper.equal(dto.getTestedDiseaseVariant(), postSaveCaseDto.getDiseaseVariant())) {
							showCaseUpdateWithNewDiseaseVariantDialog(postSaveCaseDto, dto.getTestedDiseaseVariant());
						}
					}, () -> {
					});
				} else {
					// 2.2
					showCaseCloningWithNewDiseaseDialog(postSaveCaseDto, dto.getTestedDisease());
				}
			}
		};

		if (onSavedPathogenTest != null) {
			onSavedPathogenTest.accept(dto, () -> {
				positiveTestCallback.run();
				negativeTestCallback.run();
			});
		} else {
			positiveTestCallback.run();
			negativeTestCallback.run();
		}
	}

	private void handleAssociatedContact(
		PathogenTestDto dto,
		BiConsumer<PathogenTestDto, Runnable> onSavedPathogenTest,
		ContactReferenceDto associatedContact) {
		final ContactDto contact = FacadeProvider.getContactFacade().getContactByUuid(associatedContact.getUuid());

		// RULESET according to #5816
		// 1. If pathogentestresult negative AND confirmed
		// 1.1 If Diseases equal: ask user whether to update sampleResult (if sampleResult != pathogenTestResult)
		// 1.2 if Diseases do not Equal: do nothing

		// 2. If pathogentestresult positive AND confirmed
		// 2.1 If Diseases Equal: Suggest converting to Case if contact has not yet been converted
		// 2.1.1 If Yes, set sampleResult to positive & caseClassification to confirmed
		// 2.1.2 If No, ask user whether to update sampleResult to positive
		// 2.2 If Diseases do not Equal: Suggest creating new case for that disease and set classification of the new case to confirmed. SampleResult remains unchanged

		final boolean equalDisease = contact.getDisease().equals(dto.getTestedDisease());

		Runnable negativeTestCallback = () -> {
			// 1.1
			if (equalDisease && PathogenTestResultType.NEGATIVE.equals(dto.getTestResult()) && dto.getTestResultVerified()) {
				changeAssociatedSampleResult(dto, () -> {
				}, () -> {
				});
			}
		};

		Runnable positiveTestCallback = () -> {
			if (PathogenTestResultType.POSITIVE.equals(dto.getTestResult()) && dto.getTestResultVerified()) {
				if (equalDisease) {
					// 2.1
					if (!ContactStatus.CONVERTED.equals(contact.getContactStatus())) {
						showConvertContactToCaseDialog(contact, converted -> {
							if (converted) {
								// 2.1.1
								SampleDto sample = FacadeProvider.getSampleFacade().getSampleByUuid(dto.getSample().getUuid());
								if (sample.getPathogenTestResult() != dto.getTestResult()) {
									sample.setPathogenTestResult(dto.getTestResult());
									FacadeProvider.getSampleFacade().saveSample(sample);
								}
							} else {
								// 2.1.2
								changeAssociatedSampleResult(dto, () -> {
								}, () -> {
								});
							}
						});
					}
				} else {
					// 2.2
					showCreateContactCaseDialog(contact, dto.getTestedDisease());
				}
			}
		};

		if (onSavedPathogenTest != null) {
			onSavedPathogenTest.accept(dto, () -> {
				positiveTestCallback.run();
				negativeTestCallback.run();
			});
		} else {
			positiveTestCallback.run();
			negativeTestCallback.run();
		}
	}

	private void handleAssociatedEventParticipant(
		PathogenTestDto dto,
		BiConsumer<PathogenTestDto, Runnable> onSavedPathogenTest,
		EventParticipantReferenceDto associatedEventParticipant) {
		final EventParticipantDto eventParticipant =
			FacadeProvider.getEventParticipantFacade().getEventParticipantByUuid(associatedEventParticipant.getUuid());

		// RULESET according to #5816
		// 1. If pathogentestresult negative AND confirmed
		// 1.1 If Diseases equal: ask user whether to update sampleResult (if sampleResult != pathogenTestResult)
		// 1.2 if Diseases do not Equal: do nothing

		// 2. If pathogentestresult positive AND confirmed
		// 2.1 If Diseases Equal: Suggest creating a case
		// 2.1.1 If Yes, set sampleResult to positive & caseClassification to confirmed
		// 2.1.2 If No, ask user whether to update sampleResult to positive
		// 2.2 If Diseases do not Equal: Suggest creating new case for that disease and set classification of the new case to confirmed. SampleResult remains unchanged

		final Disease eventDisease = FacadeProvider.getEventFacade().getEventByUuid(eventParticipant.getEvent().getUuid(), false).getDisease();
		final boolean equalDisease = eventDisease != null && eventDisease.equals(dto.getTestedDisease());

		Runnable negativeTestCallback = () -> {
			// 1.1
			if (equalDisease && PathogenTestResultType.NEGATIVE.equals(dto.getTestResult()) && dto.getTestResultVerified()) {
				changeAssociatedSampleResult(dto, () -> {
				}, () -> {
				});
			}
		};

		Runnable positiveTestCallback = () -> {
			if (PathogenTestResultType.POSITIVE.equals(dto.getTestResult()) && dto.getTestResultVerified()) {
				if (equalDisease) {
					// 2.1
					showConvertEventParticipantToCaseDialog(eventParticipant, dto.getTestedDisease(), caseCreated -> {
						if (caseCreated) {
							// 2.1.1
							SampleDto sample = FacadeProvider.getSampleFacade().getSampleByUuid(dto.getSample().getUuid());
							if (sample.getPathogenTestResult() != dto.getTestResult()) {
								sample.setPathogenTestResult(dto.getTestResult());
								FacadeProvider.getSampleFacade().saveSample(sample);
							}
						} else {
							// 2.1.2
							changeAssociatedSampleResult(dto, () -> {
							}, () -> {
							});
						}
					});
				} else {
					// 2.2
					showConvertEventParticipantToCaseDialog(eventParticipant, dto.getTestedDisease(), caseCreated -> {
					});
				}
			}
		};

		if (onSavedPathogenTest != null) {
			onSavedPathogenTest.accept(dto, () -> {
				positiveTestCallback.run();
				negativeTestCallback.run();
			});
		} else {
			positiveTestCallback.run();
			negativeTestCallback.run();
		}
	}

	private void changeAssociatedSampleResult(PathogenTestDto dto, Runnable samplePositiveCallback, Runnable sampleNegativeCallback) {
		if (dto.getTestResult() != FacadeProvider.getSampleFacade().getSampleByUuid(dto.getSample().getUuid()).getPathogenTestResult()) {
			ControllerProvider.getSampleController().showChangePathogenTestResultWindow(null, dto.getSample().getUuid(), dto.getTestResult(), () -> {
				PathogenTestResultType sampleResult =
					FacadeProvider.getSampleFacade().getSampleByUuid(dto.getSample().getUuid()).getPathogenTestResult();
				if (sampleResult.equals(PathogenTestResultType.POSITIVE)) {
					samplePositiveCallback.run();
				} else if (sampleResult.equals(PathogenTestResultType.NEGATIVE)) {
					sampleNegativeCallback.run();
				}
			});
		} else {
			if (dto.getTestResult().equals(PathogenTestResultType.POSITIVE)) {
				samplePositiveCallback.run();
			} else if (dto.getTestResult().equals(PathogenTestResultType.NEGATIVE)) {
				sampleNegativeCallback.run();
			}
		}
	}

	public void showConvertEventParticipantToCaseDialog(EventParticipantDto eventParticipant, Disease testedDisease, Consumer<Boolean> callback) {
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
						ControllerProvider.getCaseController()
							.createFromEventParticipantDifferentDisease(eventParticipant, testedDisease, CaseClassification.CONFIRMED);
					} else {
						ControllerProvider.getCaseController().createFromEventParticipant(eventParticipant, CaseClassification.CONFIRMED);
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
					ControllerProvider.getCaseController().createFromContact(contact, CaseClassification.CONFIRMED);
				}
				callback.accept(confirmed);
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
					ControllerProvider.getCaseController().createFromUnrelatedContact(contact, disease, CaseClassification.CONFIRMED);
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
					clonedCase.setCaseClassification(CaseClassification.CONFIRMED);
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
