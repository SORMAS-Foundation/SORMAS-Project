package de.symeda.sormas.ui.immunization;

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Notification;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.deletionconfiguration.DeletionInfoDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.immunization.ImmunizationSimilarityCriteria;
import de.symeda.sormas.api.immunization.ImmunizationStatus;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
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
import de.symeda.sormas.ui.utils.ArchiveHandlers;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.NotificationHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.components.automaticdeletion.DeletionLabel;
import de.symeda.sormas.ui.utils.components.page.title.TitleLayout;
import de.symeda.sormas.ui.utils.components.page.title.TitleLayoutHelper;

public class ImmunizationController {

	public void registerViews(Navigator navigator) {
		navigator.addView(ImmunizationsView.VIEW_NAME, ImmunizationsView.class);
		navigator.addView(ImmunizationDataView.VIEW_NAME, ImmunizationDataView.class);
		navigator.addView(ImmunizationPersonView.VIEW_NAME, ImmunizationPersonView.class);
	}

	public void create() {
		CommitDiscardWrapperComponent<ImmunizationCreationForm> immunizationCreateComponent = getImmunizationCreateComponent();
		if (immunizationCreateComponent != null) {
			VaadinUiUtil.showModalPopupWindow(immunizationCreateComponent, I18nProperties.getString(Strings.headingCreateNewImmunization));
		}
	}

	public void create(PersonReferenceDto person, Disease disease) {
		CommitDiscardWrapperComponent<ImmunizationCreationForm> immunizationCreateComponent = getImmunizationCreateComponent(person, disease);
		if (immunizationCreateComponent != null) {
			VaadinUiUtil.showModalPopupWindow(immunizationCreateComponent, I18nProperties.getString(Strings.headingCreateNewImmunization));
		}
	}

	public void navigateToImmunization(String uuid) {
		navigateToView(ImmunizationDataView.VIEW_NAME, uuid);
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

			viewComponent.addCommitListener(() -> {
				if (!createForm.getFieldGroup().isModified()) {
					final ImmunizationDto dto = createForm.getValue();
					PersonDto searchedPerson = createForm.getSearchedPerson();
					if (searchedPerson != null) {
						dto.setPerson(searchedPerson.toReference());
						selectOrCreateimmunizationForPerson(dto, searchedPerson.toReference());
					} else {
						final PersonDto person = createForm.getPerson();
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
			});
			return viewComponent;
		}
		return null;
	}

	private CommitDiscardWrapperComponent<ImmunizationCreationForm> getImmunizationCreateComponent(
		PersonReferenceDto personReferenceDto,
		Disease disease) {
		UserProvider currentUserProvider = UiUtil.getCurrentUserProvider();
		if (currentUserProvider != null) {
			ImmunizationCreationForm createForm = new ImmunizationCreationForm(personReferenceDto, disease);
			ImmunizationDto immunization = ImmunizationDto.build(personReferenceDto);
			immunization.setDisease(disease);
			immunization.setReportingUser(currentUserProvider.getUserReference());
			createForm.setValue(immunization);
			final CommitDiscardWrapperComponent<ImmunizationCreationForm> viewComponent = new CommitDiscardWrapperComponent<>(
				createForm,
				currentUserProvider.hasUserRight(UserRight.IMMUNIZATION_CREATE),
				createForm.getFieldGroup());

			viewComponent.addCommitListener(() -> {
				if (!createForm.getFieldGroup().isModified()) {

					final ImmunizationDto dto = createForm.getValue();
					selectOrCreateimmunizationForPerson(dto, personReferenceDto);
				}
			});
			return viewComponent;
		}
		return null;
	}

	public CommitDiscardWrapperComponent<ImmunizationDataForm> getImmunizationDataEditComponent(
		ImmunizationDto immunizationDto,
		Consumer<Runnable> actionCallback) {

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

		DeletionInfoDto automaticDeletionInfoDto = FacadeProvider.getImmunizationFacade().getAutomaticDeletionInfo(immunizationDto.getUuid());
		DeletionInfoDto manuallyDeletionInfoDto = FacadeProvider.getImmunizationFacade().getManuallyDeletionInfo(immunizationDto.getUuid());

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
				List<ImmunizationDto> similarImmunizations = findSimilarImmunizations(immunizationDtoValue);
				if (similarImmunizations.isEmpty()) {
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
				} else {
					showSimilarImmunizationPopup(immunizationDtoValue, similarImmunizations.get(0), this::saveImmunization);
				}
			}
		});

		// Initialize 'Delete' button
		if (UiUtil.permitted(UserRight.IMMUNIZATION_DELETE)) {
			editComponent.addDeleteWithReasonOrRestoreListener(
				ImmunizationsView.VIEW_NAME,
				null,
				I18nProperties.getString(Strings.entityImmunization),
				immunizationDto.getUuid(),
				FacadeProvider.getImmunizationFacade());
		}

		// Initialize 'Archive' button
		if (UiUtil.permitted(UserRight.IMMUNIZATION_ARCHIVE)) {
			ControllerProvider.getArchiveController()
				.addArchivingButton(
					immunizationDto,
					ArchiveHandlers.forImmunization(),
					editComponent,
					() -> navigateToImmunization(immunizationDto.getUuid()));
		}

		editComponent.restrictEditableComponentsOnEditView(
			UserRight.IMMUNIZATION_EDIT,
			null,
			UserRight.IMMUNIZATION_DELETE,
			UserRight.IMMUNIZATION_ARCHIVE,
			FacadeProvider.getImmunizationFacade().getEditPermissionType(immunizationDto.getUuid()),
			immunizationDto.isInJurisdiction());

		return editComponent;
	}

	private void saveImmunization(ImmunizationDto immunizationDtoValue) {
		FacadeProvider.getImmunizationFacade().save(immunizationDtoValue);
		Notification.show(I18nProperties.getString(Strings.messageImmunizationSaved), Notification.Type.WARNING_MESSAGE);
		SormasUI.refreshView();
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
		return FacadeProvider.getImmunizationFacade().getByUuid(uuid);
	}

	private void selectOrCreateimmunizationForPerson(ImmunizationDto dto, PersonReferenceDto selectedPerson) {
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
			navigateToImmunization(uuid);
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
