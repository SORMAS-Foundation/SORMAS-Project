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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.vaadin.server.Sizeable;
import com.vaadin.server.UserError;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.CheckBox;

import de.symeda.sormas.api.ArchivableFacade;
import de.symeda.sormas.api.CoreFacade;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.campaign.CampaignFacade;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.common.progress.ProcessedEntityStatus;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactFacade;
import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.environment.EnvironmentFacade;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventFacade;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantFacade;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolRuntimeException;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.immunization.ImmunizationFacade;
import de.symeda.sormas.api.infrastructure.InfrastructureDto;
import de.symeda.sormas.api.infrastructure.InfrastructureFacade;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskFacade;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.travelentry.TravelEntryFacade;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.api.utils.UtilDate;
import de.symeda.sormas.ui.utils.ArchivingController.IArchiveHandler;

public final class ArchiveHandlers {

	private ArchiveHandlers() {
	}

	public static CaseArchiveHandler forCase() {
		return new CaseArchiveHandler();
	}

	public static EventArchiveHandler forEvent() {
		return new EventArchiveHandler();
	}

	public static CoreEntityArchiveHandler<ContactDto, ContactFacade> forContact() {
		return new CoreEntityArchiveHandler<>(FacadeProvider.getContactFacade(), ArchiveMessages.CONTACT);
	}

	public static CoreEntityArchiveHandler<EnvironmentDto, EnvironmentFacade> forEnvironment() {
		return new CoreEntityArchiveHandler<>(FacadeProvider.getEnvironmentFacade(), ArchiveMessages.ENVIRONMENT);
	}

	public static CoreEntityArchiveHandler<EventParticipantDto, EventParticipantFacade> forEventParticipant() {
		return new CoreEntityArchiveHandler<>(FacadeProvider.getEventParticipantFacade(), ArchiveMessages.EVENT_PARTICIPANT);
	}

	public static CoreEntityArchiveHandler<CampaignDto, CampaignFacade> forCampaign() {
		return new CoreEntityArchiveHandler<>(FacadeProvider.getCampaignFacade(), ArchiveMessages.CAMPAIGN);
	}

	public static CoreEntityArchiveHandler<ImmunizationDto, ImmunizationFacade> forImmunization() {
		return new CoreEntityArchiveHandler<>(FacadeProvider.getImmunizationFacade(), ArchiveMessages.IMMUNIZATION);
	}

	public static CoreEntityArchiveHandler<TravelEntryDto, TravelEntryFacade> forTravelEntry() {
		return new CoreEntityArchiveHandler<>(FacadeProvider.getTravelEntryFacade(), ArchiveMessages.TRAVEL_ENTRY);
	}

	public static TaskArchiveHandler forTask() {
		return new TaskArchiveHandler();
	}

	public static <T extends InfrastructureDto, F extends InfrastructureFacade<T, ?, ?, ?>> InfrastructureArchiveHandler<T, F> forInfrastructure(
		F facade,
		ArchiveMessages messages) {
		return new InfrastructureArchiveHandler<>(facade, messages);
	}

	public static abstract class ArchiveHandler<T extends EntityDto, F extends ArchivableFacade> implements IArchiveHandler<T> {

		protected final F entityFacade;

		private final ArchiveMessages archiveMessages;

		protected ArchiveHandler(F entityFacade, ArchiveMessages archiveMessages) {
			this.entityFacade = entityFacade;
			this.archiveMessages = archiveMessages;
		}

		@Override
		public void archive(String entityUuid) {
			entityFacade.archive(entityUuid);
		}

		@Override
		public List<ProcessedEntity> archive(List<String> entityUuids) {
			return entityFacade.archive(entityUuids);
		}

		@Override
		public List<ProcessedEntity> dearchive(String entityUuid) {
			return Collections.singletonList(entityFacade.dearchive(entityUuid));
		}

		public List<ProcessedEntity> dearchive(List<String> entityUuids) {
			return entityFacade.dearchive(entityUuids);
		}

		@Override
		public boolean isArchived(T entity) {
			return entityFacade.isArchived(entity.getUuid());
		}

		@Override
		public ArchiveMessages getArchiveMessages() {
			return archiveMessages;
		}

		@Override
		public boolean validateAdditionalDearchivationFields() {
			return true;
		}
	}

	public static class CoreEntityArchiveHandler<T extends EntityDto, F extends CoreFacade<T, ?, ?, ?>> implements IArchiveHandler<T> {

		protected final F entityFacade;

		private final ArchiveMessages archiveMessages;

		protected DateField endOfProcessingDateField;
		protected TextArea dearchiveReasonField;

		protected CoreEntityArchiveHandler(F entityFacade, ArchiveMessages archiveMessages) {
			this.entityFacade = entityFacade;
			this.archiveMessages = archiveMessages;
		}

		@Override
		public boolean isArchived(T entity) {
			return entityFacade.isArchived(entity.getUuid());
		}

		@Override
		public ArchiveMessages getArchiveMessages() {
			return archiveMessages;
		}

		@Override
		public void archive(String entityUuid) {
			try {
				entityFacade.archive(entityUuid, UtilDate.from(endOfProcessingDateField.getValue()));
			} catch (ExternalSurveillanceToolRuntimeException e) {
				Notification.show(e.getMessage(), Notification.Type.WARNING_MESSAGE);
			}
		}

		@Override
		public List<ProcessedEntity> archive(List<String> entityUuids) {
			return entityFacade.archive(entityUuids);
		}

		@Override
		public List<ProcessedEntity> dearchive(String entityUuid) {
			return entityFacade.dearchive(Collections.singletonList(entityUuid), dearchiveReasonField.getValue());
		}

		@Override
		public List<ProcessedEntity> dearchive(List<String> entityUuids) {
			return entityFacade.dearchive(entityUuids, dearchiveReasonField.getValue());
		}

		@Override
		public void addAdditionalArchiveFields(VerticalLayout verticalLayout, EntityDto entityDto) {
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

		@Override
		public String getArchiveButtonCaptionProperty(boolean archived) {
			return archived ? Captions.actionDearchiveCoreEntity : Captions.actionArchiveCoreEntity;
		}
	}

	private static final class EventArchiveHandler extends CoreEntityArchiveHandler<EventDto, EventFacade> {

		private EventArchiveHandler() {
			super(FacadeProvider.getEventFacade(), ArchiveMessages.EVENT);
		}

		@Override
		public List<ProcessedEntity> dearchive(String entityUuid) {
			List<ProcessedEntity> processedEntities = new ArrayList<>();
			try {
				processedEntities = Collections.singletonList(entityFacade.dearchive(entityUuid, dearchiveReasonField.getValue()));
			} catch (ExternalSurveillanceToolRuntimeException e) {
				Notification.show(e.getMessage(), Notification.Type.WARNING_MESSAGE);
			}

			return processedEntities;

		}

	}

	private static final class CaseArchiveHandler extends CoreEntityArchiveHandler<CaseDataDto, CaseFacade> {

		private CheckBox archiveWithContacts;

		private CaseArchiveHandler() {
			super(FacadeProvider.getCaseFacade(), ArchiveMessages.CASE);
		}

		@Override
		public void archive(String entityUuid) {
			try {
				entityFacade.archive(entityUuid, UtilDate.from(endOfProcessingDateField.getValue()), archiveWithContacts.getValue());
			} catch (ExternalSurveillanceToolRuntimeException e) {
				Notification.show(e.getMessage(), Notification.Type.WARNING_MESSAGE);
			}
		}

		@Override
		public List<ProcessedEntity> dearchive(String entityUuid) {
			List<ProcessedEntity> processedEntities = new ArrayList<>();
			try {
				processedEntities =
					Collections.singletonList(entityFacade.dearchive(entityUuid, dearchiveReasonField.getValue(), archiveWithContacts.getValue()));
			} catch (ExternalSurveillanceToolRuntimeException e) {
				Notification.show(e.getMessage(), Notification.Type.WARNING_MESSAGE);
			}

			return processedEntities;
		}

		@Override
		public List<ProcessedEntity> archive(List<String> entityUuids) {
			return entityFacade.archive(entityUuids, archiveWithContacts.getValue());
		}

		@Override
		public List<ProcessedEntity> dearchive(List<String> uuidList) {
			return entityFacade.dearchive(uuidList, dearchiveReasonField.getValue(), archiveWithContacts.getValue());
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

	public static class TaskArchiveHandler extends ArchiveHandler<TaskDto, TaskFacade> {

		protected TaskArchiveHandler() {
			super(FacadeProvider.getTaskFacade(), ArchiveMessages.TASK);
		}

		@Override
		public String getArchiveButtonCaptionProperty(boolean archived) {
			return archived ? Captions.actionDearchiveCoreEntity : Captions.actionArchiveCoreEntity;
		}
	}

	public static class InfrastructureArchiveHandler<T extends InfrastructureDto, F extends InfrastructureFacade<T, ?, ?, ?>>
		extends ArchiveHandler<T, F> {

		protected InfrastructureArchiveHandler(F entityFacade, ArchiveMessages archiveMessages) {
			super(entityFacade, archiveMessages);
		}

		@Override
		public void archive(String entityUuid) {
			try {
				super.archive(entityUuid);
			} catch (AccessDeniedException e) {
				Notification.show(e.getMessage(), Notification.Type.WARNING_MESSAGE);
			}
		}

		@Override
		public List<ProcessedEntity> dearchive(String entityUuid) {
			List<ProcessedEntity> processedEntities = new ArrayList<>();

			try {
				processedEntities = super.dearchive(entityUuid);
			} catch (AccessDeniedException e) {
				processedEntities.add(new ProcessedEntity(entityUuid, ProcessedEntityStatus.ACCESS_DENIED_FAILURE));
				Notification.show(e.getMessage(), Notification.Type.WARNING_MESSAGE);
			}
			return processedEntities;
		}

		@Override
		public boolean isArchived(T entity) {
			return entity.isArchived();
		}

		@Override
		public String getArchiveButtonCaptionProperty(boolean archived) {
			return archived ? Captions.actionDearchiveInfrastructure : Captions.actionArchiveInfrastructure;
		}
	}
}
