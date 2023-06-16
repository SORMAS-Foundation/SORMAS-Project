/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.utils;

import java.util.Collections;
import java.util.List;

import com.vaadin.server.Sizeable;
import com.vaadin.server.UserError;
import com.vaadin.ui.DateField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.CheckBox;

import de.symeda.sormas.api.ArchivableFacade;
import de.symeda.sormas.api.CoreFacade;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignFacade;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.contact.ContactFacade;
import de.symeda.sormas.api.event.EventFacade;
import de.symeda.sormas.api.event.EventParticipantFacade;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.immunization.ImmunizationFacade;
import de.symeda.sormas.api.task.TaskFacade;
import de.symeda.sormas.api.travelentry.TravelEntryFacade;
import de.symeda.sormas.api.utils.UtilDate;

public class ArchivingHandlers {

	private ArchivingHandlers() {
	}

	public static CoreEntityArchiveHandler<CaseFacade> forCase() {
		return new CaseArchiveHandler();
	}

	public static CoreEntityArchiveHandler<ContactFacade> forContact() {
		return new CoreEntityArchiveHandler<>(FacadeProvider.getContactFacade(), ArchiveMessages.CONTACT);
	}

	public static CoreEntityArchiveHandler<EventFacade> forEvent() {
		return new CoreEntityArchiveHandler<>(FacadeProvider.getEventFacade(), ArchiveMessages.EVENT);
	}

	public static CoreEntityArchiveHandler<EventParticipantFacade> forEventParticipant() {
		return new CoreEntityArchiveHandler<>(FacadeProvider.getEventParticipantFacade(), ArchiveMessages.EVENT_PARTICIPANT);
	}

	public static CoreEntityArchiveHandler<CampaignFacade> forCampaign() {
		return new CoreEntityArchiveHandler<>(FacadeProvider.getCampaignFacade(), ArchiveMessages.CAMPAIGN);
	}

	public static CoreEntityArchiveHandler<ImmunizationFacade> forImmunization() {
		return new CoreEntityArchiveHandler<>(FacadeProvider.getImmunizationFacade(), ArchiveMessages.IMMUNIZATION);
	}

	public static CoreEntityArchiveHandler<TravelEntryFacade> forTravelEntry() {
		return new CoreEntityArchiveHandler<>(FacadeProvider.getTravelEntryFacade(), ArchiveMessages.TRAVEL_ENTRY);
	}

	public static ArchiveHandler<TaskFacade> forTask() {
		return new ArchiveHandler<>(FacadeProvider.getTaskFacade(), ArchiveMessages.TASK);
	}

	public static class ArchiveHandler<T extends ArchivableFacade> {

		protected final T entityFacade;

		private final ArchiveMessages archiveMessages;

		protected ArchiveHandler(T entityFacade, ArchiveMessages archiveMessages) {
			this.entityFacade = entityFacade;
			this.archiveMessages = archiveMessages;
		}

		public void archive(String entityUuid) {
			entityFacade.archive(Collections.singletonList(entityUuid));
		}

		public void archive(List<String> entityUuids) {
			entityFacade.archive(entityUuids);
		}

		public void dearchive(List<String> entityUuids) {
			entityFacade.dearchive(entityUuids, null);
		}

		public boolean isArchived(String uuid) {
			return entityFacade.isArchived(uuid);
		}

		public ArchiveMessages getArchiveMessages() {
			return archiveMessages;
		}

		public void addAdditionalArchiveFields(VerticalLayout verticalLayout, EntityDto entityDto) {
		}

		public void addAdditionalDearchiveFields(VerticalLayout verticalLayout) {
		}

		public boolean validateAdditionalDearchivationFields() {
			return true;
		}
	}

	public static class CoreEntityArchiveHandler<T extends CoreFacade<?, ?, ?, ?>> extends ArchiveHandler<T> {

		protected DateField endOfProcessingDateField;
		protected TextArea dearchiveReasonField;

		protected CoreEntityArchiveHandler(T entityFacade, ArchiveMessages archiveMessages) {
			super(entityFacade, archiveMessages);
		}

		@Override
		public void archive(String entityUuid) {
			entityFacade.archive(entityUuid, UtilDate.from(endOfProcessingDateField.getValue()));
		}

		@Override
		public void dearchive(List<String> entityUuids) {
			entityFacade.dearchive(entityUuids, dearchiveReasonField.getValue());
		}

		@Override
		public void addAdditionalArchiveFields(VerticalLayout verticalLayout, EntityDto entityDto) {
			super.addAdditionalArchiveFields(verticalLayout, entityDto);

			if (entityDto != null) {
				endOfProcessingDateField = new DateField();
				endOfProcessingDateField.setValue(UtilDate.toLocalDate(entityFacade.calculateEndOfProcessingDate(entityDto.getUuid())));
				endOfProcessingDateField.setCaption(I18nProperties.getCaption(Captions.endOfProcessingDate));
				endOfProcessingDateField.setDateFormat(DateFormatHelper.getDateFormatPattern());
				endOfProcessingDateField.setEnabled(false);

				verticalLayout.addComponent(endOfProcessingDateField);
			}
		}

		@Override
		public void addAdditionalDearchiveFields(VerticalLayout verticalLayout) {
			super.addAdditionalDearchiveFields(verticalLayout);

			dearchiveReasonField = new TextArea();
			dearchiveReasonField.setCaption(I18nProperties.getCaption(Captions.dearchiveReason));
			dearchiveReasonField.setWidth(100, Sizeable.Unit.PERCENTAGE);
			dearchiveReasonField.setRows(2);
			dearchiveReasonField.setRequiredIndicatorVisible(true);
			verticalLayout.addComponent(dearchiveReasonField);
		}

		@Override
		public boolean validateAdditionalDearchivationFields() {
			if (dearchiveReasonField != null && dearchiveReasonField.getValue().isEmpty()) {
				dearchiveReasonField.setComponentError(new UserError(I18nProperties.getString(Strings.messageArchiveUndoneReasonMandatory)));
				return false;
			}

			return true;
		}
	}

	private static final class CaseArchiveHandler extends CoreEntityArchiveHandler<CaseFacade> {

		private CheckBox archiveWithContacts;

		private CaseArchiveHandler() {
			super(FacadeProvider.getCaseFacade(), ArchiveMessages.CASE);
		}

		@Override
		public void archive(String entityUuid) {
			entityFacade.archive(entityUuid, UtilDate.from(endOfProcessingDateField.getValue()), archiveWithContacts.getValue());
		}

		@Override
		public void archive(List<String> entityUuids) {
			entityFacade.archive(entityUuids, archiveWithContacts.getValue());
		}

		@Override
		public void dearchive(List<String> uuidList) {
			entityFacade.dearchive(uuidList, dearchiveReasonField.getValue(), archiveWithContacts.getValue());
		}

		@Override
		public void addAdditionalArchiveFields(VerticalLayout verticalLayout, EntityDto entityDto) {
			super.addAdditionalArchiveFields(verticalLayout, entityDto);

			archiveWithContacts = new CheckBox();
			archiveWithContacts.setCaption(I18nProperties.getString(Strings.confirmationArchiveCaseWithContacts));
			archiveWithContacts.setValue(false);
			verticalLayout.addComponent(archiveWithContacts);
		}

		@Override
		public void addAdditionalDearchiveFields(VerticalLayout verticalLayout) {
			super.addAdditionalDearchiveFields(verticalLayout);

			archiveWithContacts = new CheckBox();
			archiveWithContacts.setCaption(I18nProperties.getString(Strings.confirmationDearchiveCaseWithContacts));
			archiveWithContacts.setValue(false);
			verticalLayout.addComponent(archiveWithContacts);
		}
	}
}
