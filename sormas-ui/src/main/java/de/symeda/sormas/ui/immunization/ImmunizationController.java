/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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
package de.symeda.sormas.ui.immunization;

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Notification;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.VaccinationInfoSource;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.deletionconfiguration.DeletionInfoDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.immunization.ImmunizationSimilarityCriteria;
import de.symeda.sormas.api.immunization.ImmunizationStatus;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.immunization.components.fields.pickorcreate.ImmunizationPickOrCreateField;
import de.symeda.sormas.ui.immunization.components.fields.popup.SimilarImmunizationPopup;
import de.symeda.sormas.ui.immunization.components.form.ImmunizationCreationForm;
import de.symeda.sormas.ui.immunization.components.form.ImmunizationDataForm;
import de.symeda.sormas.ui.immunization.components.form.QuickImmunizationCreationForm;
import de.symeda.sormas.ui.person.PersonSelectionGrid;
import de.symeda.sormas.ui.utils.ArchiveHandlers;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.NotificationHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.components.automaticdeletion.DeletionLabel;
import de.symeda.sormas.ui.utils.components.page.title.TitleLayout;
import de.symeda.sormas.ui.utils.components.page.title.TitleLayoutHelper;

public class ImmunizationController {

	private boolean immunizationSaveTriggered;
	private CommitDiscardWrapperComponent<ImmunizationCreationForm> immunizationCreateComponent;

	public void registerViews(Navigator navigator) {
		navigator.addView(ImmunizationsView.VIEW_NAME, ImmunizationsView.class);
		navigator.addView(ImmunizationDataView.VIEW_NAME, ImmunizationDataView.class);
		navigator.addView(ImmunizationPersonView.VIEW_NAME, ImmunizationPersonView.class);
	}

	public void create() {
		immunizationCreateComponent = getImmunizationCreateComponent();
		if (immunizationCreateComponent != null) {
			VaadinUiUtil.showModalPopupWindow(immunizationCreateComponent, I18nProperties.getString(Strings.headingCreateNewImmunization));
		}
	}

	public void create(PersonReferenceDto person, Disease disease) {
		create(person, disease, null, null);
	}

	public void create(PersonReferenceDto person, Disease disease, Runnable savedCallback) {
		create(person, disease, null, savedCallback);
	}

	public void create(PersonReferenceDto person, Disease disease, String diseaseDetails) {
		create(person, disease, diseaseDetails, null);
	}

	public void create(PersonReferenceDto person, Disease disease, String diseaseDetails, Runnable savedCallback) {
		create(person, disease, diseaseDetails, savedCallback, null);
	}

	/**
	 * Opens the creation popup for an immunization of {@code person} and remembers the case, contact or event participant it was
	 * started from.
	 * <p>
	 * Example: started from the immunization side component of case {@code ABC123-...}, saving the new immunization
	 * {@code XYZ789-...} navigates to {@code immunizations/data/XYZ789-...?caseUuid=ABC123-...}, so the back link to the case is
	 * shown. With {@code origin == null} it navigates to {@code immunizations/data/XYZ789-...} as before. The quick creation
	 * (system configuration {@code USE_QUICK_IMMUNIZATION_CREATION}) does not navigate and only runs {@code savedCallback}.
	 */
	public void create(PersonReferenceDto person, Disease disease, String diseaseDetails, Runnable savedCallback, ReferenceDto origin) {
		CommitDiscardWrapperComponent<?> createComponent = getImmunizationCreateComponent(person, disease, diseaseDetails, savedCallback, origin);
		if (createComponent != null) {
			VaadinUiUtil.showModalPopupWindow(createComponent, I18nProperties.getString(Strings.headingCreateNewImmunization));
		}
	}

	private CommitDiscardWrapperComponent<QuickImmunizationCreationForm> buildQuickCreateComponent(
		PersonReferenceDto person,
		Disease disease,
		String diseaseDetails,
		Runnable savedCallback) {

		UserProvider currentUserProvider = UiUtil.getCurrentUserProvider();
		if (currentUserProvider == null) {
			return null;
		}

		QuickImmunizationCreationForm createForm = new QuickImmunizationCreationForm();

		ImmunizationDto immunization = ImmunizationDto.build(person);
		immunization.setDisease(disease);
		immunization.setDiseaseDetails(diseaseDetails);
		immunization.setReportingUser(currentUserProvider.getUserReference());

		UserDto currentUser = currentUserProvider.getUser();
		RegionReferenceDto userRegion = currentUser.getRegion();
		DistrictReferenceDto userDistrict = currentUser.getDistrict();
		immunization.setResponsibleRegion(userRegion != null ? userRegion : FacadeProvider.getRegionFacade().getDefaultInfrastructureReference());
		immunization
			.setResponsibleDistrict(userDistrict != null ? userDistrict : FacadeProvider.getDistrictFacade().getDefaultInfrastructureReference());

		createForm.setValue(immunization);

		final CommitDiscardWrapperComponent<QuickImmunizationCreationForm> viewComponent = new CommitDiscardWrapperComponent<>(
			createForm,
			currentUserProvider.hasUserRight(UserRight.IMMUNIZATION_CREATE),
			createForm.getFieldGroup());

		viewComponent.addCommitListener(() -> {
			if (!createForm.getFieldGroup().isModified()) {
				final ImmunizationDto dto = createForm.getValue();
				VaccinationInfoSource vaccinationInfoSource = createForm.getVaccinationInfoSource();
				Date dateOfMostRecentDose = createForm.getDateOfMostRecentDose();

				FacadeProvider.getImmunizationFacade().saveQuickImmunization(dto, vaccinationInfoSource, dateOfMostRecentDose);

				Notification.show(I18nProperties.getString(Strings.messageQuickImmunizationSaved), Notification.Type.HUMANIZED_MESSAGE);
				if (savedCallback != null) {
					savedCallback.run();
				}
			}
		});

		return viewComponent;
	}

	public void navigateToImmunization(String uuid) {
		navigateToView(ImmunizationDataView.VIEW_NAME, uuid);
	}

	/**
	 * Opens the immunization and remembers the case, contact or event participant it was opened from, so the immunization views
	 * show a link back to it.
	 * <p>
	 * Example: with a {@code ContactReferenceDto} of UUID {@code ABC123-...},
	 * {@code navigateToImmunization("XYZ789-...", contactRef)} navigates to
	 * {@code immunizations/data/XYZ789-...?contactUuid=ABC123-...}. With {@code origin == null}, a blank UUID or any other
	 * reference type it behaves exactly like {@link #navigateToImmunization(String)} and navigates to
	 * {@code immunizations/data/XYZ789-...}.
	 */
	public void navigateToImmunization(String uuid, ReferenceDto origin) {
		final String originParam = getOriginParam(origin);
		if (originParam == null || StringUtils.isBlank(origin.getUuid())) {
			navigateToImmunization(uuid);
		} else {
			navigateToView(ImmunizationDataView.VIEW_NAME, uuid + "?" + originParam + "=" + origin.getUuid());
		}
	}

	/**
	 * @return The URL parameter name for the type of {@code origin}, e.g. {@code "caseUuid"} for a {@code CaseReferenceDto}, or
	 *         {@code null} for {@code null} and unsupported reference types.
	 */
	private static String getOriginParam(ReferenceDto origin) {
		if (origin instanceof CaseReferenceDto) {
			return AbstractImmunizationView.ORIGIN_CASE_PARAM;
		} else if (origin instanceof ContactReferenceDto) {
			return AbstractImmunizationView.ORIGIN_CONTACT_PARAM;
		} else if (origin instanceof EventParticipantReferenceDto) {
			return AbstractImmunizationView.ORIGIN_EVENT_PARTICIPANT_PARAM;
		} else {
			return null;
		}
	}

	public void navigateToView(String viewName, String uuid) {
		final String navigationState = viewName + "/" + uuid;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}

	private CommitDiscardWrapperComponent<ImmunizationCreationForm> getImmunizationCreateComponent() {
		UserProvider currentUserProvider = UiUtil.getCurrentUserProvider();
		if (currentUserProvider != null) {
			ImmunizationCreationForm createForm = new ImmunizationCreationForm();
			ImmunizationDto immunization = ImmunizationDto.build(null);
			immunization.setReportingUser(currentUserProvider.getUserReference());
			createForm.setValue(immunization);
			final CommitDiscardWrapperComponent<ImmunizationCreationForm> viewComponent = new CommitDiscardWrapperComponent<>(
				createForm,
				currentUserProvider.hasUserRight(UserRight.IMMUNIZATION_CREATE),
				createForm.getFieldGroup());

			immunizationSaveTriggered = false;
			viewComponent.addCommitListener(() -> {
				if (!createForm.getFieldGroup().isModified()) {
					final ImmunizationDto dto = createForm.getValue();
					PersonDto searchedPerson = createForm.getSearchedPerson();
					if (searchedPerson != null) {
						dto.setPerson(searchedPerson.toReference());
						selectOrCreateimmunizationForPerson(dto, searchedPerson.toReference());
					} else {
						final PersonDto person = createForm.getPerson();
						if (createForm.getWarningSimilarPersons() != null) {
							CommitDiscardWrapperComponent<PersonSelectionGrid> warningPopUpDuplicatePerson =
								(CommitDiscardWrapperComponent<PersonSelectionGrid>) viewComponent.getWrappedComponent()
									.getWarningSimilarPersons()
									.getContent();
							warningPopUpDuplicatePerson.getDiscardButton().setVisible(true);
							warningPopUpDuplicatePerson.getCommitButton().setCaption(I18nProperties.getCaption(Captions.actionContinue));
							warningPopUpDuplicatePerson.addDoneListener(() -> {
								if (!immunizationSaveTriggered) {
									VaadinUiUtil.showModalPopupWindow(viewComponent, I18nProperties.getString(Strings.headingCreateNewImmunization));
								}
							});

							warningPopUpDuplicatePerson.addCommitListener(() -> {
								immunizationSaveTriggered = true;
								ControllerProvider.getPersonController()
									.selectOrCreatePerson(
										person,
										I18nProperties.getString(Strings.infoSelectOrCreatePersonForImmunization),
										selectedPerson -> {
											if (selectedPerson != null) {
												selectOrCreateimmunizationForPerson(dto, selectedPerson);
											}
										},
										true);
							});
						} else {
							ControllerProvider.getPersonController()
								.selectOrCreatePerson(
									person,
									I18nProperties.getString(Strings.infoSelectOrCreatePersonForImmunization),
									selectedPerson -> {
										if (selectedPerson != null) {
											selectOrCreateimmunizationForPerson(dto, selectedPerson);
										}
									},
									true);
						}
					}
				}
			});
			return viewComponent;
		}
		return null;
	}

	private CommitDiscardWrapperComponent<?> getImmunizationCreateComponent(
		PersonReferenceDto personReferenceDto,
		Disease disease,
		String diseaseDetails,
		Runnable savedCallback,
		ReferenceDto origin) {

		if (FacadeProvider.getImmunizationFacade().isUseQuickImmunizationCreation()) {
			return buildQuickCreateComponent(personReferenceDto, disease, diseaseDetails, savedCallback);
		}

		UserProvider currentUserProvider = UiUtil.getCurrentUserProvider();
		if (currentUserProvider != null) {
			ImmunizationCreationForm createForm = new ImmunizationCreationForm(personReferenceDto, disease);
			ImmunizationDto immunization = ImmunizationDto.build(personReferenceDto);
			immunization.setDisease(disease);
			immunization.setDiseaseDetails(diseaseDetails);
			immunization.setReportingUser(currentUserProvider.getUserReference());
			createForm.setValue(immunization);
			final CommitDiscardWrapperComponent<ImmunizationCreationForm> viewComponent = new CommitDiscardWrapperComponent<>(
				createForm,
				currentUserProvider.hasUserRight(UserRight.IMMUNIZATION_CREATE),
				createForm.getFieldGroup());

			viewComponent.addCommitListener(() -> {
				if (!createForm.getFieldGroup().isModified()) {

					final ImmunizationDto dto = createForm.getValue();
					selectOrCreateimmunizationForPerson(dto, personReferenceDto, origin);
				}
			});
			return viewComponent;
		}
		return null;
	}

	public CommitDiscardWrapperComponent<ImmunizationDataForm> getImmunizationDataEditComponent(String uuid, Consumer<Runnable> actionCallback) {

		//String uuid = immunizationDto.getUuid();
		ImmunizationDto immunizationDto = FacadeProvider.getImmunizationFacade().getImmunizationByUuid(uuid);

		ImmunizationDataForm immunizationDataForm = new ImmunizationDataForm(
			immunizationDto.isPseudonymized(),
			immunizationDto.isInJurisdiction(),
			immunizationDto.getRelatedCase(),
			actionCallback);
		immunizationDataForm.setValue(immunizationDto);

		CommitDiscardWrapperComponent<ImmunizationDataForm> editComponent =
			new CommitDiscardWrapperComponent<ImmunizationDataForm>(immunizationDataForm, true, immunizationDataForm.getFieldGroup()) {

				@Override
				public void discard() {
					immunizationDataForm.discard();
					super.discard();
				}
			};

		DeletionInfoDto automaticDeletionInfoDto = FacadeProvider.getImmunizationFacade().getAutomaticDeletionInfo(uuid);
		DeletionInfoDto manuallyDeletionInfoDto = FacadeProvider.getImmunizationFacade().getManuallyDeletionInfo(uuid);

		editComponent.getButtonsPanel()
			.addComponentAsFirst(
				new DeletionLabel(automaticDeletionInfoDto, manuallyDeletionInfoDto, immunizationDto.isDeleted(), ImmunizationDto.I18N_PREFIX));

		if (immunizationDto.isDeleted()) {
			editComponent.getWrappedComponent().getField(ImmunizationDto.DELETION_REASON).setVisible(true);
			if (editComponent.getWrappedComponent().getField(ImmunizationDto.DELETION_REASON).getValue() == DeletionReason.OTHER_REASON) {
				editComponent.getWrappedComponent().getField(ImmunizationDto.OTHER_DELETION_REASON).setVisible(true);
			}
		}

		editComponent.addCommitListener(() -> {
			if (!immunizationDataForm.getFieldGroup().isModified()) {
				ImmunizationDto immunizationDtoValue = immunizationDataForm.getValue();

				FacadeProvider.getImmunizationFacade().save(immunizationDtoValue);
				if (immunizationDtoValue.getImmunizationStatus() == ImmunizationStatus.ACQUIRED) {
					NotificationHelper.showNotification(
						I18nProperties.getString(Strings.messageImmunizationSavedVaccinationStatusUpdated),
						Notification.Type.WARNING_MESSAGE,
						-1);
				} else {
					Notification.show(I18nProperties.getString(Strings.messageImmunizationSaved), Notification.Type.WARNING_MESSAGE);
				}
				SormasUI.refreshView();
			}
		});

		// Initialize 'Delete' button
		if (UiUtil.permitted(UserRight.IMMUNIZATION_DELETE)) {
			editComponent.addDeleteWithReasonOrRestoreListener(
				ImmunizationsView.VIEW_NAME,
				null,
				I18nProperties.getString(Strings.entityImmunization),
				uuid,
				FacadeProvider.getImmunizationFacade());
		}

		// Initialize 'Archive' button
		if (UiUtil.permitted(UserRight.IMMUNIZATION_ARCHIVE)) {
			ControllerProvider.getArchiveController()
				.addArchivingButton(immunizationDto, ArchiveHandlers.forImmunization(), editComponent, SormasUI::refreshView);
		}

		editComponent.restrictEditableComponentsOnEditView(
			UserRight.IMMUNIZATION_EDIT,
			null,
			UserRight.IMMUNIZATION_DELETE,
			UserRight.IMMUNIZATION_ARCHIVE,
			FacadeProvider.getImmunizationFacade().getEditPermissionType(uuid),
			immunizationDto.isInJurisdiction());

		return editComponent;
	}

	public TitleLayout getImmunizationViewTitleLayout(String uuid) {
		ImmunizationDto immunizationDto = findImmunization(uuid);

		TitleLayout titleLayout = new TitleLayout();

		String shortUuid = DataHelper.getShortUuid(immunizationDto.getUuid());
		PersonDto person = FacadeProvider.getPersonFacade().getByUuid(immunizationDto.getPerson().getUuid());
		StringBuilder mainRowText = TitleLayoutHelper.buildPersonString(person);
		mainRowText.append(mainRowText.length() > 0 ? " (" + shortUuid + ")" : shortUuid);
		titleLayout.addMainRow(mainRowText.toString());

		return titleLayout;
	}

	private ImmunizationDto findImmunization(String uuid) {
		return FacadeProvider.getImmunizationFacade().getImmunizationByUuid(uuid);
	}

	private void selectOrCreateimmunizationForPerson(ImmunizationDto dto, PersonReferenceDto selectedPerson) {
		selectOrCreateimmunizationForPerson(dto, selectedPerson, null);
	}

	private void selectOrCreateimmunizationForPerson(ImmunizationDto dto, PersonReferenceDto selectedPerson, ReferenceDto origin) {
		dto.setPerson(selectedPerson);
		selectOrCreateImmunization(dto, uuid -> {
			if (uuid == null) {
				return;
			}
			if (!uuid.equals(dto.getUuid())) {
				dto.setUuid(uuid);
				dto.setChangeDate(new Date());
			}
			FacadeProvider.getImmunizationFacade().save(dto);
			navigateToImmunization(uuid, origin);
		});
	}

	private void selectOrCreateImmunization(ImmunizationDto immunizationDto, Consumer<String> selectedImmunizationUuidConsumer) {
		ImmunizationSimilarityCriteria criteria = new ImmunizationSimilarityCriteria.Builder().withDisease(immunizationDto.getDisease())
			.withStartDate(immunizationDto.getStartDate())
			.withEndDate(immunizationDto.getEndDate())
			.withPerson(immunizationDto.getPerson().getUuid())
			.withMeansOfImmunization(immunizationDto.getMeansOfImmunization())
			.build();

		List<ImmunizationDto> similarImmunizations = FacadeProvider.getImmunizationFacade().getSimilarImmunizations(criteria);

		if (!similarImmunizations.isEmpty()) {
			ImmunizationPickOrCreateField pickOrCreateField = new ImmunizationPickOrCreateField(immunizationDto, similarImmunizations);
			pickOrCreateField.setWidth(1280, Sizeable.Unit.PIXELS);

			final CommitDiscardWrapperComponent<ImmunizationPickOrCreateField> component = new CommitDiscardWrapperComponent<>(pickOrCreateField);
			component.getCommitButton().setCaption(I18nProperties.getCaption(Captions.actionConfirm));
			component.getCommitButton().setEnabled(false);
			component.addCommitListener(() -> selectedImmunizationUuidConsumer.accept(pickOrCreateField.getValue()));

			pickOrCreateField.setSelectionChangeCallback((commitAllowed) -> component.getCommitButton().setEnabled(commitAllowed));

			VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingPickOrCreateImmunization));
		} else {
			selectedImmunizationUuidConsumer.accept(immunizationDto.getUuid());
		}
	}

	/**
	 * Finds similar immunizations based on start date and end date.
	 * 
	 * @deprecated It has been deprecated, kept for now in case the functionality will be revised and needed again.
	 * @param immunizationDto
	 * @return
	 */
	@Deprecated(forRemoval = true)
	private List<ImmunizationDto> findSimilarImmunizations(ImmunizationDto immunizationDto) {
		ImmunizationSimilarityCriteria criteria = new ImmunizationSimilarityCriteria.Builder().withImmunization(immunizationDto.getUuid())
			.withDisease(immunizationDto.getDisease())
			.withStartDate(immunizationDto.getStartDate())
			.withEndDate(immunizationDto.getEndDate())
			.withPerson(immunizationDto.getPerson().getUuid())
			.withMeansOfImmunization(immunizationDto.getMeansOfImmunization())
			.build();

		return FacadeProvider.getImmunizationFacade().getSimilarImmunizations(criteria);
	}

	/**
	 * Dispays a dialog to notify user of similar immunizations.
	 * 
	 * @deprecated It has been deprecated, kept for now in case the functionality will be revised and needed again.
	 * @param immunizationDto
	 * @param similarImmunization
	 * @param callback
	 */
	@Deprecated(forRemoval = true)
	private void showSimilarImmunizationPopup(
		ImmunizationDto immunizationDto,
		ImmunizationDto similarImmunization,
		Consumer<ImmunizationDto> callback) {
		SimilarImmunizationPopup similarImmunizationPopup = new SimilarImmunizationPopup(immunizationDto, similarImmunization);
		similarImmunizationPopup.setWidth(1280, Sizeable.Unit.PIXELS);

		final CommitDiscardWrapperComponent<SimilarImmunizationPopup> component = new CommitDiscardWrapperComponent<>(similarImmunizationPopup);
		component.getCommitButton().addClickListener(clickEvent -> callback.accept(immunizationDto));
		component.getCommitButton().setCaption(I18nProperties.getCaption(Captions.actionSaveChanges));
		component.getDiscardButton().setCaption(I18nProperties.getCaption(Captions.actionAdjustChanges));

		VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingSimilarImmunization));
	}
}
