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
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.v7.data.Validator;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantCriteria;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantFacade;
import de.symeda.sormas.api.event.EventParticipantIndexDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class EventParticipantsController {

	private final EventParticipantFacade eventParticipantFacade = FacadeProvider.getEventParticipantFacade();
	private final PersonFacade personFacade = FacadeProvider.getPersonFacade();

	public EventParticipantDto createEventParticipant(EventReferenceDto eventRef, Consumer<EventParticipantReferenceDto> doneConsumer) {
		final EventParticipantDto eventParticipant = EventParticipantDto.build(eventRef, UserProvider.getCurrent().getUserReference());
		return createEventParticipant(eventRef, doneConsumer, eventParticipant);
	}

	public EventParticipantDto createEventParticipant(
		EventReferenceDto eventRef,
		Consumer<EventParticipantReferenceDto> doneConsumer,
		EventParticipantDto eventParticipant) {
		EventParticipantCreateForm createForm = new EventParticipantCreateForm();
		createForm.setValue(eventParticipant);
		final CommitDiscardWrapperComponent<EventParticipantCreateForm> createComponent = new CommitDiscardWrapperComponent<>(
			createForm,
			UserProvider.getCurrent().hasUserRight(UserRight.EVENTPARTICIPANT_CREATE),
			createForm.getFieldGroup());

		createComponent.addCommitListener(() -> {
			if (!createForm.getFieldGroup().isModified()) {
				final EventParticipantDto dto = createForm.getValue();

				if (dto.getPerson() == null) {
					final PersonDto person = PersonDto.build();
					person.setFirstName(createForm.getPersonFirstName());
					person.setLastName(createForm.getPersonLastName());

					ControllerProvider.getPersonController()
						.selectOrCreatePerson(
							person,
							I18nProperties.getString(Strings.infoSelectOrCreatePersonForEventParticipant),
							selectedPerson -> {
								if (selectedPerson != null) {
									EventParticipantCriteria criteria = new EventParticipantCriteria();
									criteria.event(eventRef);
									List<EventParticipantIndexDto> currentEventParticipants =
										FacadeProvider.getEventParticipantFacade().getIndexList(criteria, null, null, null);
									Boolean alreadyParticipant = false;
									for (EventParticipantIndexDto participant : currentEventParticipants) {
										if (selectedPerson.getUuid().equals(participant.getPersonUuid())) {
											alreadyParticipant = true;
											break;
										}
									}
									if (alreadyParticipant) {
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
										ControllerProvider.getEventParticipantController().createEventParticipant(savedDto.getUuid(), doneConsumer);
									}
								}
							},
							true);
				} else {
					EventParticipantDto savedDto = eventParticipantFacade.saveEventParticipant(dto);
					Notification.show(I18nProperties.getString(Strings.messageEventParticipantCreated), Type.ASSISTIVE_NOTIFICATION);
					ControllerProvider.getEventParticipantController().createEventParticipant(savedDto.getUuid(), doneConsumer);
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

	public void createEventParticipant(String eventParticipantUuid, Consumer<EventParticipantReferenceDto> doneConsumer) {

		EventParticipantDto eventParticipant = FacadeProvider.getEventParticipantFacade().getEventParticipantByUuid(eventParticipantUuid);
		EventParticipantEditForm editForm =
			new EventParticipantEditForm(FacadeProvider.getEventFacade().getEventByUuid(eventParticipant.getEvent().getUuid()), false);
		editForm.setValue(eventParticipant);

		CommitDiscardWrapperComponent<EventParticipantEditForm> createView = createEventParticipantEditCommitWrapper(editForm, doneConsumer);

		Window window = VaadinUiUtil.showModalPopupWindow(createView, I18nProperties.getString(Strings.headingEditEventParticipant));
		// form is too big for typical screens
		window.setHeight(80, Unit.PERCENTAGE);

		if (UserProvider.getCurrent().hasUserRight(UserRight.EVENTPARTICIPANT_DELETE)) {
			createView.addDeleteListener(() -> {
				FacadeProvider.getEventParticipantFacade().deleteEventParticipant(editForm.getValue().toReference());
				UI.getCurrent().removeWindow(window);
				SormasUI.refreshView();
			}, I18nProperties.getCaption(EventParticipantDto.I18N_PREFIX));
		}
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
		final EventDto event = FacadeProvider.getEventFacade().getEventByUuid(eventParticipant.getEvent().getUuid());

		final EventParticipantEditForm editForm = new EventParticipantEditForm(event, eventParticipant.isPseudonymized());
		editForm.setValue(eventParticipant);
		editForm.setWidth(100, Unit.PERCENTAGE);

		final CommitDiscardWrapperComponent<EventParticipantEditForm> editComponent = createEventParticipantEditCommitWrapper(editForm, null);

		if (UserProvider.getCurrent().hasUserRight(UserRight.EVENTPARTICIPANT_DELETE)) {
			editComponent.addDeleteListener(() -> {
				FacadeProvider.getEventParticipantFacade().deleteEventParticipant(eventParticipant.toReference());
				UI.getCurrent().getNavigator().navigateTo(EventParticipantsView.VIEW_NAME);
			}, I18nProperties.getString(Strings.entityEventParticipant));
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
				EventDto eventDto = FacadeProvider.getEventFacade().getEventByUuid(dto.getEvent().getUuid());
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

	public VerticalLayout getEventParticipantViewTitleLayout(EventParticipantDto eventParticipant) {
		EventDto event = FacadeProvider.getEventFacade().getEventByUuid(eventParticipant.getEvent().getUuid());

		VerticalLayout titleLayout = new VerticalLayout();
		titleLayout.addStyleNames(CssStyles.LAYOUT_MINIMAL, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_4);
		titleLayout.setSpacing(false);

		String eventShortUuid = DataHelper.getShortUuid(event.getUuid());
		String eventTitle = event.getEventTitle();
		Label eventLabel = new Label(StringUtils.isNotBlank(eventTitle) ? eventTitle + " (" + eventShortUuid + ")" : eventShortUuid);
		eventLabel.addStyleNames(CssStyles.H3, CssStyles.VSPACE_NONE, CssStyles.VSPACE_TOP_NONE);
		titleLayout.addComponent(eventLabel);

		if (event.getStartDate() != null) {
			Label eventStartDateLabel = new Label(
				event.getEndDate() != null
					? DateFormatHelper.buildPeriodString(event.getStartDate(), event.getEndDate())
					: DateFormatHelper.formatDate(event.getStartDate()));
			eventStartDateLabel.addStyleNames(CssStyles.H3, CssStyles.VSPACE_NONE, CssStyles.VSPACE_TOP_NONE);
			titleLayout.addComponent(eventStartDateLabel);
		}

		String shortUuid = DataHelper.getShortUuid(eventParticipant.getUuid());
		String personFullName = eventParticipant.getPerson().toReference().getCaption();
		StringBuilder eventLabelSb = new StringBuilder();
		if (StringUtils.isNotBlank(personFullName)) {
			eventLabelSb.append(personFullName);

			if (eventParticipant.getPerson().getBirthdateDD() != null
				&& eventParticipant.getPerson().getBirthdateMM() != null
				&& eventParticipant.getPerson().getBirthdateYYYY() != null) {

				eventLabelSb.append(" (* ")
					.append(
						PersonHelper.formatBirthdate(
							eventParticipant.getPerson().getBirthdateDD(),
							eventParticipant.getPerson().getBirthdateMM(),
							eventParticipant.getPerson().getBirthdateYYYY(),
							I18nProperties.getUserLanguage()))
					.append(")");
			}
		}
		eventLabelSb.append(eventLabelSb.length() > 0 ? " (" + shortUuid + ")" : shortUuid);
		Label eventParticipantLabel = new Label(eventLabelSb.toString());
		eventParticipantLabel.addStyleNames(CssStyles.H2, CssStyles.VSPACE_NONE, CssStyles.VSPACE_TOP_NONE, CssStyles.LABEL_PRIMARY);
		titleLayout.addComponents(eventParticipantLabel);

		return titleLayout;
	}
}
