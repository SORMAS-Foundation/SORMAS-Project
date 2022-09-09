package de.symeda.sormas.ui.travelentry;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.deletionconfiguration.DeletionInfoDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.travelentry.TravelEntryIndexDto;
import de.symeda.sormas.api.travelentry.TravelEntryListCriteria;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.travelentry.components.TravelEntryCreateForm;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CoreEntityArchiveMessages;
import de.symeda.sormas.ui.utils.DeletableUtils;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.components.automaticdeletion.DeletionLabel;
import de.symeda.sormas.ui.utils.components.page.title.TitleLayout;
import de.symeda.sormas.ui.utils.components.page.title.TitleLayoutHelper;

public class TravelEntryController {

	public void registerViews(Navigator navigator) {
		navigator.addView(TravelEntriesView.VIEW_NAME, TravelEntriesView.class);
		navigator.addView(TravelEntryDataView.VIEW_NAME, TravelEntryDataView.class);
		navigator.addView(TravelEntryPersonView.VIEW_NAME, TravelEntryPersonView.class);
	}

	public void create() {
		CommitDiscardWrapperComponent<TravelEntryCreateForm> travelEntryCreateComponent = getTravelEntryCreateComponent(null, null);
		VaadinUiUtil.showModalPopupWindow(travelEntryCreateComponent, I18nProperties.getString(Strings.headingCreateNewTravelEntry));
	}

	public void create(TravelEntryListCriteria travelEntryListCriteria) {
		CommitDiscardWrapperComponent<TravelEntryCreateForm> travelEntryCreateComponent =
			getTravelEntryCreateComponent(travelEntryListCriteria.getCaseReferenceDto(), travelEntryListCriteria.getPersonReferenceDto());
		VaadinUiUtil.showModalPopupWindow(travelEntryCreateComponent, I18nProperties.getString(Strings.headingCreateNewTravelEntry));
	}

	private CommitDiscardWrapperComponent<TravelEntryCreateForm> getTravelEntryCreateComponent(
		CaseReferenceDto caseReferenceDto,
		PersonReferenceDto personReferenceDto) {

		TravelEntryCreateForm createForm;
		TravelEntryDto travelEntry = TravelEntryDto.build(null);
		travelEntry.setDeaContent(FacadeProvider.getTravelEntryFacade().getDeaContentOfLastTravelEntry());

		if (caseReferenceDto != null) {
			CaseDataDto caseDataDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseReferenceDto.getUuid());
			PersonReferenceDto casePersonReferenceDto = caseDataDto.getPerson();
			travelEntry.setResultingCase(caseReferenceDto);
			travelEntry.setPerson(casePersonReferenceDto);
			travelEntry.setDisease(caseDataDto.getDisease());
			createForm = new TravelEntryCreateForm(casePersonReferenceDto);
			createForm.setPerson(FacadeProvider.getPersonFacade().getByUuid(casePersonReferenceDto.getUuid()));
		} else if (personReferenceDto != null) {
			travelEntry.setPerson(personReferenceDto);
			createForm = new TravelEntryCreateForm(personReferenceDto);
			createForm.setPerson(FacadeProvider.getPersonFacade().getByUuid(personReferenceDto.getUuid()));
		} else {
			createForm = new TravelEntryCreateForm();
		}

		travelEntry.setReportingUser(UserProvider.getCurrent().getUserReference());
		createForm.setValue(travelEntry);

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
		DeletionInfoDto automaticDeletionInfoDto = FacadeProvider.getTravelEntryFacade().getAutomaticDeletionInfo(travelEntryUuid);
		DeletionInfoDto manuallyDeletionInfoDto = FacadeProvider.getTravelEntryFacade().getManuallyDeletionInfo(travelEntryUuid);

		TravelEntryDataForm travelEntryEditForm = new TravelEntryDataForm(travelEntryUuid, travelEntry.isPseudonymized());
		travelEntryEditForm.setValue(travelEntry);

		CommitDiscardWrapperComponent<TravelEntryDataForm> editComponent = new CommitDiscardWrapperComponent<>(
			travelEntryEditForm,
			UserProvider.getCurrent().hasUserRight(UserRight.TRAVEL_ENTRY_EDIT),
			travelEntryEditForm.getFieldGroup());

		editComponent.getButtonsPanel()
			.addComponentAsFirst(
				new DeletionLabel(automaticDeletionInfoDto, manuallyDeletionInfoDto, travelEntry.isDeleted(), TravelEntryDto.I18N_PREFIX));

		if (travelEntry.isDeleted()) {
			editComponent.getWrappedComponent().getField(TravelEntryDto.DELETION_REASON).setVisible(true);
			if (editComponent.getWrappedComponent().getField(TravelEntryDto.DELETION_REASON).getValue() == DeletionReason.OTHER_REASON) {
				editComponent.getWrappedComponent().getField(TravelEntryDto.OTHER_DELETION_REASON).setVisible(true);
			}
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
			editComponent.addDeleteWithReasonListener((deleteDetails) -> {
				FacadeProvider.getTravelEntryFacade().delete(travelEntry.getUuid(), deleteDetails);
				UI.getCurrent().getNavigator().navigateTo(TravelEntriesView.VIEW_NAME);
			}, I18nProperties.getString(Strings.entityTravelEntry));
		}

		// Initialize 'Archive' button
		if (UserProvider.getCurrent().hasUserRight(UserRight.TRAVEL_ENTRY_ARCHIVE)) {
			ControllerProvider.getArchiveController()
				.addArchivingButton(
					travelEntry,
					FacadeProvider.getTravelEntryFacade(),
					CoreEntityArchiveMessages.TRAVEL_ENTRY,
					editComponent,
					() -> navigateToTravelEntry(travelEntry.getUuid()));
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
		PersonDto person = FacadeProvider.getPersonFacade().getByUuid(travelEntry.getPerson().getUuid());
		StringBuilder mainRowText = TitleLayoutHelper.buildPersonString(person);
		mainRowText.append(mainRowText.length() > 0 ? " (" + shortUuid + ")" : shortUuid);
		titleLayout.addMainRow(mainRowText.toString());

		return titleLayout;
	}

	public void deleteAllSelectedItems(Collection<TravelEntryIndexDto> selectedRows, Runnable callback) {
		if (selectedRows.size() == 0) {
			new Notification(
				I18nProperties.getString(Strings.headingNoTravelEntriesSelected),
				I18nProperties.getString(Strings.messageNoTravelEntriesSelected),
				Notification.Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
		} else {
			DeletableUtils.showDeleteWithReasonPopup(
				String.format(I18nProperties.getString(Strings.confirmationDeleteTravelEntries), selectedRows.size()),
				(deleteDetails) -> {
					for (TravelEntryIndexDto selectedRow : selectedRows) {
						FacadeProvider.getTravelEntryFacade().delete(selectedRow.getUuid(), deleteDetails);
					}
					callback.run();
					new Notification(
						I18nProperties.getString(Strings.headingTravelEntriesDeleted),
						I18nProperties.getString(Strings.messageTravelEntriesDeleted),
						Notification.Type.HUMANIZED_MESSAGE,
						false).show(Page.getCurrent());
				});
		}
	}
}
