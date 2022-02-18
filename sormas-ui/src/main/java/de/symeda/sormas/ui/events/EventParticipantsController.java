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
package de.symeda.sormas.ui.events;

import java.util.Collection;
import java.util.function.Consumer;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.ui.contact.ContactDataView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import org.apache.commons.lang3.StringUtils;

import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Window;
import com.vaadin.v7.data.Validator;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.deletionconfiguration.AutomaticDeletionInfoDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantFacade;
import de.symeda.sormas.api.event.EventParticipantIndexDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.components.automaticdeletion.AutomaticDeletionLabel;
import de.symeda.sormas.ui.utils.components.page.title.TitleLayout;
import de.symeda.sormas.ui.utils.components.page.title.TitleLayoutHelper;

public class EventParticipantsController {

	private final EventParticipantFacade eventParticipantFacade = FacadeProvider.getEventParticipantFacade();
	private final PersonFacade personFacade = FacadeProvider.getPersonFacade();

	public EventParticipantDto createEventParticipant(EventReferenceDto eventRef, Consumer<EventParticipantReferenceDto> doneConsumer) {
		final EventParticipantDto eventParticipant = EventParticipantDto.build(eventRef, UserProvider.getCurrent().getUserReference());
		return createEventParticipant(eventRef, doneConsumer, eventParticipant, true);
	}

	public EventParticipantDto createEventParticipant(
		EventReferenceDto eventRef,
		Consumer<EventParticipantReferenceDto> doneConsumer,
		EventParticipantDto eventParticipant) {
		return createEventParticipant(eventRef, doneConsumer, eventParticipant, false);
	}

	public EventParticipantDto createEventParticipant(
		EventReferenceDto eventRef,
		Consumer<EventParticipantReferenceDto> doneConsumer,
		EventParticipantDto eventParticipant,
		boolean navigateOnCommit) {

		EventParticipantCreateForm createForm =
			new EventParticipantCreateForm(!FacadeProvider.getEventFacade().hasRegionAndDistrict(eventRef.getUuid()));
		createForm.setValue(eventParticipant);
		final CommitDiscardWrapperComponent<EventParticipantCreateForm> createComponent = new CommitDiscardWrapperComponent<>(
			createForm,
			UserProvider.getCurrent().hasUserRight(UserRight.EVENTPARTICIPANT_CREATE),
			createForm.getFieldGroup());

		createComponent.addCommitListener(() -> {
			if (!createForm.getFieldGroup().isModified()) {
				final EventParticipantDto dto = createForm.getValue();

				PersonDto searchedPerson = createForm.getSearchedPerson();
				if (searchedPerson != null) {
					dto.setPerson(searchedPerson);
				}

				if (dto.getPerson() == null) {
					final PersonDto person = PersonDto.build();
					person.setFirstName(createForm.getPersonFirstName());
					person.setLastName(createForm.getPersonLastName());
					person.setSex(createForm.getPersonSex());

					ControllerProvider.getPersonController()
						.selectOrCreatePerson(
							person,
							I18nProperties.getString(Strings.infoSelectOrCreatePersonForEventParticipant),
							selectedPerson -> {
								if (selectedPerson != null) {
									if (FacadeProvider.getEventParticipantFacade().exists(selectedPerson.getUuid(), eventRef.getUuid())) {
										throw new Validator.InvalidValueException(I18nProperties.getString(Strings.messageAlreadyEventParticipant));
									} else {
										dto.setPerson(FacadeProvider.getPersonFacade().getPersonByUuid(selectedPerson.getUuid()));
										EventParticipantDto savedDto = eventParticipantFacade.saveEventParticipant(dto);

										Notification notification = new Notification(
											I18nProperties.getString(Strings.messagePersonAddedAsEventParticipant),
											"",
											Type.HUMANIZED_MESSAGE);
										notification.show(Page.getCurrent());

										Notification
											.show(I18nProperties.getString(Strings.messageEventParticipantCreated), Type.ASSISTIVE_NOTIFICATION);
										if (navigateOnCommit) {
											navigateToData(savedDto.getUuid());
										} else {
											SormasUI.refreshView();
										}
									}
								}
							},
							true);
				} else {
					if (FacadeProvider.getEventParticipantFacade().exists(dto.getPerson().getUuid(), eventRef.getUuid())) {
						throw new Validator.InvalidValueException(I18nProperties.getString(Strings.messageAlreadyEventParticipant));
					}
					EventParticipantDto savedDto = eventParticipantFacade.saveEventParticipant(dto);
					Notification.show(I18nProperties.getString(Strings.messageEventParticipantCreated), Type.ASSISTIVE_NOTIFICATION);
					if (navigateOnCommit) {
						navigateToData(savedDto.getUuid());
					} else {
						SormasUI.refreshView();
					}
				}
			}
		});

		Window window = VaadinUiUtil.showModalPopupWindow(createComponent, I18nProperties.getString(Strings.headingCreateNewEventParticipant));
		window.addCloseListener(e -> {
			doneConsumer.accept(null);
		});

		return createComponent.getWrappedComponent().getValue();
	}

	public void navigateToData(String eventParticipantUuid) {
		final String navigationState = EventParticipantDataView.VIEW_NAME + "/" + eventParticipantUuid;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}

	public void deleteAllSelectedItems(Collection<EventParticipantIndexDto> selectedRows, Runnable callback) {
		if (selectedRows.size() == 0) {
			new Notification(
				I18nProperties.getString(Strings.headingNoEventParticipantsSelected),
				I18nProperties.getString(Strings.messageNoEventParticipantsSelected),
				Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showDeleteConfirmationWindow(
				String.format(I18nProperties.getString(Strings.confirmationDeleteEventParticipants), selectedRows.size()),
				() -> {
					for (Object selectedRow : selectedRows) {
						FacadeProvider.getEventParticipantFacade()
							.deleteEventParticipant(new EventParticipantReferenceDto(((EventParticipantIndexDto) selectedRow).getUuid()));
					}
					callback.run();
					new Notification(
						I18nProperties.getString(Strings.headingEventParticipantsDeleted),
						I18nProperties.getString(Strings.messageEventParticipantsDeleted),
						Type.HUMANIZED_MESSAGE,
						false).show(Page.getCurrent());
				});
		}
	}

	public void deleteEventParticipant(String eventUuid, String personUuid, Runnable callback) {
		VaadinUiUtil.showDeleteConfirmationWindow(
			String.format(I18nProperties.getString(Strings.confirmationDeleteEntity), I18nProperties.getString(Strings.entityEventParticipant)),
			() -> {
				EventParticipantReferenceDto eventParticipantRef =
					FacadeProvider.getEventParticipantFacade().getReferenceByEventAndPerson(eventUuid, personUuid);
				if (eventParticipantRef != null) {
					FacadeProvider.getEventParticipantFacade().deleteEventParticipant(eventParticipantRef);
					callback.run();
				} else {
					Notification.show(I18nProperties.getString(Strings.errorOccurred), Type.ERROR_MESSAGE);
				}
			});
	}

	public CommitDiscardWrapperComponent<?> getEventParticipantDataEditComponent(String eventParticipantUuid) {
		final EventParticipantDto eventParticipant = FacadeProvider.getEventParticipantFacade().getEventParticipantByUuid(eventParticipantUuid);
		final EventDto event = FacadeProvider.getEventFacade().getEventByUuid(eventParticipant.getEvent().getUuid(), false);
		AutomaticDeletionInfoDto automaticDeletionInfoDto = FacadeProvider.getEventParticipantFacade().getAutomaticDeletionInfo(eventParticipantUuid);

		final EventParticipantEditForm editForm =
			new EventParticipantEditForm(event, eventParticipant.isPseudonymized(), eventParticipant.getPerson().isPseudonymized(), false);
		editForm.setValue(eventParticipant);
		editForm.setWidth(100, Unit.PERCENTAGE);

		final CommitDiscardWrapperComponent<EventParticipantEditForm> editComponent = createEventParticipantEditCommitWrapper(editForm, null);

		if (automaticDeletionInfoDto != null) {
			editComponent.getButtonsPanel().addComponentAsFirst(new AutomaticDeletionLabel(automaticDeletionInfoDto));
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.EVENTPARTICIPANT_DELETE)) {
			editComponent.addDeleteListener(() -> {
				FacadeProvider.getEventParticipantFacade().deleteEventParticipant(eventParticipant.toReference());
				ControllerProvider.getEventController().navigateToParticipants(eventParticipant.getEvent().getUuid());
			}, I18nProperties.getString(Strings.entityEventParticipant));
		}

		// Initialize 'Archive' button
		if (UserProvider.getCurrent().hasUserRight(UserRight.EVENTPARTICIPANT_ARCHIVE)) {
			boolean archived = FacadeProvider.getEventParticipantFacade().isArchived(eventParticipant.getUuid());
			Button archiveButton = ButtonHelper.createButton(archived ? Captions.actionDearchive : Captions.actionArchive, e -> {
				editComponent.commit();

				if (archived) {
					ControllerProvider.getArchiveController()
						.dearchiveEntity(
							eventParticipant,
							FacadeProvider.getEventParticipantFacade(),
							Strings.headingDearchiveEventParticipant,
							Strings.confirmationDearchiveEventParticipant,
							Strings.entityEventParticipant,
							Strings.messageEventParticipantDearchived,
							() -> navigateToData(eventParticipant.getUuid()));
				} else {
					ControllerProvider.getArchiveController()
						.archiveEntity(
							eventParticipant,
							FacadeProvider.getEventParticipantFacade(),
							Strings.headingArchiveEventParticipant,
							Strings.confirmationArchiveEventParticipant,
							Strings.entityEventParticipant,
							Strings.messageEventParticipantArchived,
							() -> navigateToData(eventParticipant.getUuid()));
				}

			}, ValoTheme.BUTTON_LINK);

			editComponent.getButtonsPanel().addComponentAsFirst(archiveButton);
			editComponent.getButtonsPanel().setComponentAlignment(archiveButton, Alignment.BOTTOM_LEFT);
		}

		return editComponent;
	}

	private CommitDiscardWrapperComponent<EventParticipantEditForm> createEventParticipantEditCommitWrapper(
		EventParticipantEditForm editForm,
		Consumer<EventParticipantReferenceDto> doneConsumer) {
		final CommitDiscardWrapperComponent<EventParticipantEditForm> editComponent = new CommitDiscardWrapperComponent<>(
			editForm,
			UserProvider.getCurrent().hasUserRight(UserRight.EVENTPARTICIPANT_EDIT),
			editForm.getFieldGroup());

		editComponent.addCommitListener(() -> {

			if (!editForm.getFieldGroup().isModified()) {

				EventParticipantDto dto = editForm.getValue();
				EventDto eventDto = FacadeProvider.getEventFacade().getEventByUuid(dto.getEvent().getUuid(), false);
				UserDto user = UserProvider.getCurrent().getUser();

				RegionReferenceDto userRegion = user.getRegion();
				DistrictReferenceDto userDistrict = user.getDistrict();

				RegionReferenceDto epResponsibleRegion = dto.getRegion();
				DistrictReferenceDto epResponsibleDistrict = dto.getDistrict();

				RegionReferenceDto epEventRegion = eventDto.getEventLocation().getRegion();
				DistrictReferenceDto epEventDistrict = eventDto.getEventLocation().getDistrict();

				//Responsible region of the event participant is filled and differs from user Region - warning about loosing full access rights to the event participant should be triggered
				Boolean responsibleRegionDiffersFromUserRegion =
					(userRegion != null && epResponsibleRegion != null && !userRegion.equals(epResponsibleRegion));

				//Responsible district of the event participant is filled and differs from user District - warning about loosing full access rights to the event participant should be triggered
				Boolean responsibleDistrictDiffersFromUserDistrict =
					(userDistrict != null && epResponsibleDistrict != null && !userDistrict.equals(epResponsibleDistrict));

				//both responsible region and district of the event participant are empty and the fall back towards event location: 
				// event region or event district is different from user's region or district - warning about loosing full access rights to the event participant should be triggered
				Boolean responsibleFieldsEmptyAndEventOutsideJurisdiction = (epResponsibleRegion == null
					&& epResponsibleDistrict == null
					&& (userRegion != null && !userRegion.equals(epEventRegion) || userDistrict != null && !userDistrict.equals(epEventDistrict)));

				//if only district is not filled and either responsible region or district are different from user region or district - warning about loosing full access rights to the event participant should be triggered
				Boolean responsibleDistrictIsEmpty = ((epResponsibleRegion != null && epResponsibleDistrict == null)
					&& (userRegion != null && !userRegion.equals(epResponsibleRegion) || userDistrict != null));

				if (responsibleRegionDiffersFromUserRegion
					|| responsibleDistrictDiffersFromUserDistrict
					|| responsibleFieldsEmptyAndEventOutsideJurisdiction
					|| responsibleDistrictIsEmpty) {
					VaadinUiUtil.showConfirmationPopup(
						I18nProperties.getString(Strings.headingEventParticipantResponsibleJurisdictionUpdated),
						new Label(I18nProperties.getString(Strings.messageEventParticipantResponsibleJurisdictionUpdated)),
						I18nProperties.getString(Strings.yes),
						I18nProperties.getString(Strings.no),
						500,
						confirmed -> {
							if (confirmed) {
								savePersonAndEventParticipant(doneConsumer, dto);
							}
						});
				} else {
					savePersonAndEventParticipant(doneConsumer, dto);
				}
			}
		});

		editComponent.addDiscardListener(() -> SormasUI.refreshView());

		return editComponent;
	}

	private void savePersonAndEventParticipant(Consumer<EventParticipantReferenceDto> doneConsumer, EventParticipantDto dto) {
		personFacade.savePerson(dto.getPerson());
		eventParticipantFacade.saveEventParticipant(dto);
		Notification.show(I18nProperties.getString(Strings.messageEventParticipantSaved), Type.WARNING_MESSAGE);
		if (doneConsumer != null)
			doneConsumer.accept(null);
		SormasUI.refreshView();
	}

	public TitleLayout getEventParticipantViewTitleLayout(EventParticipantDto eventParticipant) {
		EventDto event = FacadeProvider.getEventFacade().getEventByUuid(eventParticipant.getEvent().getUuid(), false);

		TitleLayout titleLayout = new TitleLayout();

		String eventShortUuid = DataHelper.getShortUuid(event.getUuid());
		String eventTitle = event.getEventTitle();
		String eventLabel = StringUtils.isNotBlank(eventTitle) ? eventTitle + " (" + eventShortUuid + ")" : eventShortUuid;
		titleLayout.addRow(eventLabel);

		if (event.getStartDate() != null) {
			String eventStartDateLabel = event.getEndDate() != null
				? DateFormatHelper.buildPeriodString(event.getStartDate(), event.getEndDate())
				: DateFormatHelper.formatDate(event.getStartDate());
			titleLayout.addRow(eventStartDateLabel);
		}

		String shortUuid = DataHelper.getShortUuid(eventParticipant.getUuid());
		StringBuilder mainRowText = TitleLayoutHelper.buildPersonString(eventParticipant.getPerson());
		mainRowText.append(mainRowText.length() > 0 ? " (" + shortUuid + ")" : shortUuid);
		titleLayout.addMainRow(mainRowText.toString());

		return titleLayout;
	}
}
