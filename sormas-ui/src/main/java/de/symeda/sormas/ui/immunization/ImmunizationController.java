package de.symeda.sormas.ui.immunization;

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
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
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.immunization.components.fields.pickorcreate.ImmunizationPickOrCreateField;
import de.symeda.sormas.ui.immunization.components.fields.popup.SimilarImmunizationPopup;
import de.symeda.sormas.ui.immunization.components.form.ImmunizationCreationForm;
import de.symeda.sormas.ui.immunization.components.form.ImmunizationDataForm;
import de.symeda.sormas.ui.immunization.components.layout.MainHeaderLayout;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.NotificationHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

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
		final String navigationState = ImmunizationDataView.VIEW_NAME + "/" + uuid;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}

	private CommitDiscardWrapperComponent<ImmunizationCreationForm> getImmunizationCreateComponent() {
		UserProvider currentUserProvider = UserProvider.getCurrent();
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
					final PersonDto person = createForm.getPerson();
					ControllerProvider.getPersonController()
						.selectOrCreatePerson(person, I18nProperties.getString(Strings.infoSelectOrCreatePersonForImmunization), selectedPerson -> {
							if (selectedPerson != null) {
								selectOrCreateimmunizationForPerson(dto, selectedPerson);
							}
						}, true);
				}
			});
			return viewComponent;
		}
		return null;
	}

	private CommitDiscardWrapperComponent<ImmunizationCreationForm> getImmunizationCreateComponent(
		PersonReferenceDto personReferenceDto,
		Disease disease) {
		UserProvider currentUserProvider = UserProvider.getCurrent();
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

	public CommitDiscardWrapperComponent<ImmunizationDataForm> getImmunizationDataEditComponent(String immunizationUuid) {

		ImmunizationDto immunizationDto = findImmunization(immunizationUuid);

		ImmunizationDataForm immunizationDataForm = new ImmunizationDataForm(immunizationDto.isPseudonymized(), immunizationDto.getRelatedCase());
		immunizationDataForm.setValue(immunizationDto);

		UserProvider currentUserProvider = UserProvider.getCurrent();
		CommitDiscardWrapperComponent<ImmunizationDataForm> editComponent = new CommitDiscardWrapperComponent<>(
			immunizationDataForm,
			currentUserProvider != null && currentUserProvider.hasUserRight(UserRight.IMMUNIZATION_EDIT),
			immunizationDataForm.getFieldGroup());

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
		if (UserProvider.getCurrent().hasUserRight(UserRight.IMMUNIZATION_DELETE)) {
			editComponent.addDeleteListener(() -> {
				FacadeProvider.getImmunizationFacade().deleteImmunization(immunizationDto.getUuid());
				UI.getCurrent().getNavigator().navigateTo(ImmunizationsView.VIEW_NAME);
			}, I18nProperties.getString(Strings.entityImmunization));
		}

		// Initialize 'Archive' button
		if (UserProvider.getCurrent().hasUserRight(UserRight.IMMUNIZATION_ARCHIVE)) {
			boolean archived = FacadeProvider.getImmunizationFacade().isArchived(immunizationUuid);
			Button archiveButton = ButtonHelper.createButton(archived ? Captions.actionDearchive : Captions.actionArchive, e -> {
				editComponent.commit();
				archiveOrDearchiveImmunization(immunizationUuid, !archived);
			}, ValoTheme.BUTTON_LINK);

			editComponent.getButtonsPanel().addComponentAsFirst(archiveButton);
			editComponent.getButtonsPanel().setComponentAlignment(archiveButton, Alignment.BOTTOM_LEFT);
		}

		return editComponent;
	}

	private void saveImmunization(ImmunizationDto immunizationDtoValue) {
		FacadeProvider.getImmunizationFacade().save(immunizationDtoValue);
		Notification.show(I18nProperties.getString(Strings.messageImmunizationSaved), Notification.Type.WARNING_MESSAGE);
		SormasUI.refreshView();
	}

	public MainHeaderLayout getImmunizationMainHeaderLayout(String uuid) {
		ImmunizationDto immunizationDto = findImmunization(uuid);

		String shortUuid = DataHelper.getShortUuid(immunizationDto.getUuid());
		PersonReferenceDto person = immunizationDto.getPerson();
		String text = person.getFirstName() + " " + person.getLastName() + " (" + shortUuid + ")";

		return new MainHeaderLayout(text);
	}

	private ImmunizationDto findImmunization(String uuid) {
		return FacadeProvider.getImmunizationFacade().getByUuid(uuid);
	}

	private void archiveOrDearchiveImmunization(String uuid, boolean archive) {

		if (archive) {
			Label contentLabel = new Label(
				String.format(
					I18nProperties.getString(Strings.confirmationArchiveImmunization),
					I18nProperties.getString(Strings.entityImmunization).toLowerCase(),
					I18nProperties.getString(Strings.entityImmunization).toLowerCase()));
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingArchiveImmunization),
				contentLabel,
				I18nProperties.getString(Strings.yes),
				I18nProperties.getString(Strings.no),
				640,
				e -> {
					if (e) {
						FacadeProvider.getImmunizationFacade().archiveOrDearchiveImmunization(uuid, true);
						Notification.show(
							String.format(
								I18nProperties.getString(Strings.messageImmunizationArchived),
								I18nProperties.getString(Strings.entityImmunization)),
							Notification.Type.ASSISTIVE_NOTIFICATION);
						navigateToImmunization(uuid);
					}
				});
		} else {
			Label contentLabel = new Label(
				String.format(
					I18nProperties.getString(Strings.confirmationDearchiveImmunization),
					I18nProperties.getString(Strings.entityImmunization).toLowerCase(),
					I18nProperties.getString(Strings.entityImmunization).toLowerCase()));
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingDearchiveImmunization),
				contentLabel,
				I18nProperties.getString(Strings.yes),
				I18nProperties.getString(Strings.no),
				640,
				e -> {
					if (e) {
						FacadeProvider.getImmunizationFacade().archiveOrDearchiveImmunization(uuid, false);
						Notification.show(
							String.format(
								I18nProperties.getString(Strings.messageImmunizationDearchived),
								I18nProperties.getString(Strings.entityImmunization)),
							Notification.Type.ASSISTIVE_NOTIFICATION);
						navigateToImmunization(uuid);
					}
				});
		}
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
