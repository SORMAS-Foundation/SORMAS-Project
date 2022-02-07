package de.symeda.sormas.ui.travelentry;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.navigator.Navigator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.deletionconfiguration.AutomaticDeletionInfoDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.travelentry.components.TravelEntryCreateForm;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.components.automaticdeletion.AutomaticDeletionLabel;
import de.symeda.sormas.ui.utils.components.page.title.TitleLayout;
import de.symeda.sormas.ui.utils.components.page.title.TitleLayoutHelper;

public class TravelEntryController {

	public void registerViews(Navigator navigator) {
		navigator.addView(TravelEntriesView.VIEW_NAME, TravelEntriesView.class);
		navigator.addView(TravelEntryDataView.VIEW_NAME, TravelEntryDataView.class);
		navigator.addView(TravelEntryPersonView.VIEW_NAME, TravelEntryPersonView.class);
	}

	public void create(CaseReferenceDto caseReferenceDto) {
		CommitDiscardWrapperComponent<TravelEntryCreateForm> travelEntryCreateComponent = getTravelEntryCreateComponent(caseReferenceDto);
		VaadinUiUtil.showModalPopupWindow(travelEntryCreateComponent, I18nProperties.getString(Strings.headingCreateNewTravelEntry));
	}

	private CommitDiscardWrapperComponent<TravelEntryCreateForm> getTravelEntryCreateComponent(CaseReferenceDto caseReferenceDto) {

		TravelEntryCreateForm createForm = new TravelEntryCreateForm();
		TravelEntryDto travelEntry = TravelEntryDto.build(null);
		travelEntry.setDeaContent(FacadeProvider.getTravelEntryFacade().getDeaContentOfLastTravelEntry());

		PersonDto personDto = null;
		if (caseReferenceDto != null) {
			CaseDataDto caseDataDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseReferenceDto.getUuid());
			PersonReferenceDto personReferenceDto = caseDataDto.getPerson();
			personDto = FacadeProvider.getPersonFacade().getPersonByUuid(personReferenceDto.getUuid());
			travelEntry.setResultingCase(caseReferenceDto);
			travelEntry.setPerson(personReferenceDto);
			travelEntry.setDisease(caseDataDto.getDisease());
		}

		travelEntry.setReportingUser(UserProvider.getCurrent().getUserReference());
		createForm.setValue(travelEntry);

		if (personDto != null) {
			createForm.setPerson(personDto);
			createForm.setPersonalDetailsReadOnlyIfNotEmpty(true);
		}

		if (caseReferenceDto != null) {
			createForm.setDiseaseReadOnly(true);
		}

		final CommitDiscardWrapperComponent<TravelEntryCreateForm> editView = new CommitDiscardWrapperComponent<>(
			createForm,
			UserProvider.getCurrent().hasUserRight(UserRight.TRAVEL_ENTRY_CREATE),
			createForm.getFieldGroup());

		editView.addCommitListener(() -> {
			if (!createForm.getFieldGroup().isModified()) {

				final TravelEntryDto dto = createForm.getValue();

				PersonDto searchedPerson = createForm.getSearchedPerson();
				if (searchedPerson != null) {
					dto.setPerson(searchedPerson.toReference());
				}

				if (dto.getPerson() == null) {
					final PersonDto person = createForm.getPerson();
					ControllerProvider.getPersonController()
						.selectOrCreatePerson(person, I18nProperties.getString(Strings.infoSelectOrCreatePersonForCase), selectedPerson -> {
							if (selectedPerson != null) {
								dto.setPerson(selectedPerson);
								FacadeProvider.getTravelEntryFacade().save(dto);
								navigateToTravelEntry(dto.getUuid());
							}
						}, true);
				} else {
					FacadeProvider.getTravelEntryFacade().save(dto);
					navigateToTravelEntry(dto.getUuid());
				}
			}
		});

		return editView;
	}

	public void navigateToTravelEntry(String uuid) {
		final String navigationState = TravelEntryDataView.VIEW_NAME + "/" + uuid;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}

	public CommitDiscardWrapperComponent<TravelEntryDataForm> getTravelEntryDataEditComponent(String travelEntryUuid) {

		TravelEntryDto travelEntry = findTravelEntry(travelEntryUuid);
		AutomaticDeletionInfoDto automaticDeletionInfoDto = FacadeProvider.getTravelEntryFacade().getAutomaticDeletionInfo(travelEntryUuid);

		TravelEntryDataForm travelEntryEditForm = new TravelEntryDataForm(travelEntryUuid, travelEntry.isPseudonymized());
		travelEntryEditForm.setValue(travelEntry);

		CommitDiscardWrapperComponent<TravelEntryDataForm> editComponent = new CommitDiscardWrapperComponent<>(
			travelEntryEditForm,
			UserProvider.getCurrent().hasUserRight(UserRight.TRAVEL_ENTRY_EDIT),
			travelEntryEditForm.getFieldGroup());

		if (automaticDeletionInfoDto != null) {
			editComponent.getButtonsPanel().addComponentAsFirst(new AutomaticDeletionLabel(automaticDeletionInfoDto));
		}

		editComponent.addCommitListener(() -> {
			if (!travelEntryEditForm.getFieldGroup().isModified()) {
				TravelEntryDto travelEntryDto = travelEntryEditForm.getValue();
				FacadeProvider.getTravelEntryFacade().save(travelEntryDto);
				Notification.show(I18nProperties.getString(Strings.messageTravelEntrySaved), Notification.Type.WARNING_MESSAGE);
				SormasUI.refreshView();
			}
		});

		editComponent.addDiscardListener(() -> travelEntryEditForm.onDiscard());

		// Initialize 'Delete' button
		if (UserProvider.getCurrent().hasUserRight(UserRight.TRAVEL_ENTRY_DELETE)) {
			editComponent.addDeleteListener(() -> {
				FacadeProvider.getTravelEntryFacade().deleteTravelEntry(travelEntry.getUuid());
				UI.getCurrent().getNavigator().navigateTo(TravelEntriesView.VIEW_NAME);
			}, I18nProperties.getString(Strings.entityTravel));
		}

		// Initialize 'Archive' button
		if (UserProvider.getCurrent().hasUserRight(UserRight.TRAVEL_ENTRY_ARCHIVE)) {
			boolean archived = FacadeProvider.getTravelEntryFacade().isArchived(travelEntryUuid);
			Button archiveTravelEntryButton = ButtonHelper.createButton(archived ? Captions.actionDearchive : Captions.actionArchive, e -> {
				editComponent.commit();
				archiveOrDearchiveTraveEntry(travelEntryUuid, !archived);
			}, ValoTheme.BUTTON_LINK);

			editComponent.getButtonsPanel().addComponentAsFirst(archiveTravelEntryButton);
			editComponent.getButtonsPanel().setComponentAlignment(archiveTravelEntryButton, Alignment.BOTTOM_LEFT);
		}

		return editComponent;
	}

	private TravelEntryDto findTravelEntry(String uuid) {
		return FacadeProvider.getTravelEntryFacade().getByUuid(uuid);
	}

	public TitleLayout getTravelEntryViewTitleLayout(String uuid) {
		TravelEntryDto travelEntry = findTravelEntry(uuid);

		TitleLayout titleLayout = new TitleLayout();

		String pointOfEntryName = FacadeProvider.getPointOfEntryFacade().getByUuid(travelEntry.getPointOfEntry().getUuid()).getName();
		String pointOfEntryDetails = travelEntry.getPointOfEntryDetails();
		String travelEntryPointOfEntry = StringUtils.isNotBlank(pointOfEntryDetails) ? pointOfEntryDetails : pointOfEntryName;

		titleLayout.addRow(travelEntryPointOfEntry);

		String shortUuid = DataHelper.getShortUuid(travelEntry.getUuid());
		PersonDto person = FacadeProvider.getPersonFacade().getPersonByUuid(travelEntry.getPerson().getUuid());
		StringBuilder mainRowText = TitleLayoutHelper.buildPersonString(person);
		mainRowText.append(mainRowText.length() > 0 ? " (" + shortUuid + ")" : shortUuid);
		titleLayout.addMainRow(mainRowText.toString());

		return titleLayout;
	}

	private void archiveOrDearchiveTraveEntry(String travelEntryUuid, boolean archive) {

		if (archive) {
			Label contentLabel = new Label(
				String.format(
					I18nProperties.getString(Strings.confirmationArchiveTravelEntry),
					I18nProperties.getString(Strings.entityTravel).toLowerCase(),
					I18nProperties.getString(Strings.entityTravel).toLowerCase()));
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingArchiveTravelEntry),
				contentLabel,
				I18nProperties.getString(Strings.yes),
				I18nProperties.getString(Strings.no),
				640,
				e -> {
					if (e) {
						FacadeProvider.getTravelEntryFacade().archiveOrDearchiveTravelEntry(travelEntryUuid, true);
						Notification.show(
							String
								.format(I18nProperties.getString(Strings.messageTravelEntryArchived), I18nProperties.getString(Strings.entityTravel)),
							Notification.Type.ASSISTIVE_NOTIFICATION);
						navigateToTravelEntry(travelEntryUuid);
					}
				});
		} else {
			Label contentLabel = new Label(
				String.format(
					I18nProperties.getString(Strings.confirmationDearchiveTravelEntry),
					I18nProperties.getString(Strings.entityTravel).toLowerCase(),
					I18nProperties.getString(Strings.entityTravel).toLowerCase()));
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingDearchiveTravelEntry),
				contentLabel,
				I18nProperties.getString(Strings.yes),
				I18nProperties.getString(Strings.no),
				640,
				e -> {
					if (e) {
						FacadeProvider.getTravelEntryFacade().archiveOrDearchiveTravelEntry(travelEntryUuid, false);
						Notification.show(
							String.format(
								I18nProperties.getString(Strings.messageTravelEntryDearchived),
								I18nProperties.getString(Strings.entityTravel)),
							Notification.Type.ASSISTIVE_NOTIFICATION);
						navigateToTravelEntry(travelEntryUuid);
					}
				});
		}
	}
}
