/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.externalsurveillancetool;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.audit.AuditIgnore;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.common.progress.ProcessedEntityStatus;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolException;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolFacade;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolResponse;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.share.ExternalShareStatus;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventService;
import de.symeda.sormas.backend.share.ExternalShareInfoService;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "ExternalSurveillanceToolFacade")
public class ExternalSurveillanceToolGatewayFacadeEjb implements ExternalSurveillanceToolFacade {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private ConfigFacadeEjbLocal configFacade;

	@EJB
	private CaseService caseService;
	@EJB
	private EventService eventService;
	@EJB
	private ExternalShareInfoService shareInfoService;
	@EJB
	private UserService userService;

	@Override
	@AuditIgnore
	@PermitAll
	public boolean isFeatureEnabled() {
		return configFacade.isExternalSurveillanceToolGatewayConfigured();
	}

	@Override
	@RightsAllowed(UserRight._EXTERNAL_SURVEILLANCE_SHARE)
	public List<ProcessedEntity> sendCases(List<String> caseUuids) {
		List<ProcessedEntity> processedCases = new ArrayList<>();
		try {
			doSendCases(caseUuids, false);
			processedCases.addAll(shareInfoService.buildProcessedEntities(caseUuids, ProcessedEntityStatus.SUCCESS));
		} catch (AccessDeniedException e) {
			processedCases.addAll(shareInfoService.buildProcessedEntities(caseUuids, ProcessedEntityStatus.ACCESS_DENIED_FAILURE));
		} catch (ExternalSurveillanceToolException e) {
			processedCases.addAll(shareInfoService.buildProcessedEntities(caseUuids, ProcessedEntityStatus.EXTERNAL_SURVEILLANCE_FAILURE));
		} catch (Exception e) {
			processedCases.addAll(shareInfoService.buildProcessedEntities(caseUuids, ProcessedEntityStatus.INTERNAL_FAILURE));
		}
		return processedCases;
	}

	@RightsAllowed(UserRight._CASE_ARCHIVE)
	public List<ProcessedEntity> sendCasesInternal(List<String> caseUuids, boolean archived) {
		List<ProcessedEntity> processedCases = new ArrayList<>();

		if (isFeatureEnabled()) {
			try {
				doSendCases(caseUuids, archived);
				processedCases.addAll(shareInfoService.buildProcessedEntities(caseUuids, ProcessedEntityStatus.SUCCESS));
			} catch (ExternalSurveillanceToolException e) {
				processedCases.addAll(shareInfoService.buildProcessedEntities(caseUuids, ProcessedEntityStatus.EXTERNAL_SURVEILLANCE_FAILURE));
			} catch (AccessDeniedException e) {
				processedCases.addAll(shareInfoService.buildProcessedEntities(caseUuids, ProcessedEntityStatus.ACCESS_DENIED_FAILURE));
			}
		} else {
			processedCases.addAll(shareInfoService.buildProcessedEntities(caseUuids, ProcessedEntityStatus.EXTERNAL_SURVEILLANCE_FAILURE));
		}

		return processedCases;
	}

	private void doSendCases(List<String> caseUuids, boolean archived) throws ExternalSurveillanceToolException {
		if (!userService.hasRight(UserRight.CASE_EDIT)) {
			throw new AccessDeniedException(I18nProperties.getString(Strings.errorForbidden));
		}

		ExportParameters params = new ExportParameters();
		params.setCaseUuids(caseUuids);
		params.setArchived(archived);

		sendRequest(params);
	}

	@Override
	@RightsAllowed(UserRight._EXTERNAL_SURVEILLANCE_SHARE)
	public List<ProcessedEntity> sendEvents(List<String> eventUuids) {
		List<ProcessedEntity> processedEvents = new ArrayList<>();
		try {
			doSendEvents(eventUuids, false);
			processedEvents.addAll(shareInfoService.buildProcessedEntities(eventUuids, ProcessedEntityStatus.SUCCESS));
		} catch (AccessDeniedException e) {
			processedEvents.addAll(shareInfoService.buildProcessedEntities(eventUuids, ProcessedEntityStatus.ACCESS_DENIED_FAILURE));
		} catch (ExternalSurveillanceToolException e) {
			processedEvents.addAll(shareInfoService.buildProcessedEntities(eventUuids, ProcessedEntityStatus.EXTERNAL_SURVEILLANCE_FAILURE));
		} catch (Exception e) {
			processedEvents.addAll(shareInfoService.buildProcessedEntities(eventUuids, ProcessedEntityStatus.INTERNAL_FAILURE));
		}
		return processedEvents;
	}

	@RightsAllowed(UserRight._EVENT_ARCHIVE)
	public List<ProcessedEntity> sendEventsInternal(List<String> eventUuids, boolean archived) {
		List<ProcessedEntity> processedEvents = new ArrayList<>();

		if (isFeatureEnabled()) {
			try {
				doSendEvents(eventUuids, archived);
				processedEvents.addAll(shareInfoService.buildProcessedEntities(eventUuids, ProcessedEntityStatus.SUCCESS));
			} catch (ExternalSurveillanceToolException e) {
				processedEvents.addAll(shareInfoService.buildProcessedEntities(eventUuids, ProcessedEntityStatus.EXTERNAL_SURVEILLANCE_FAILURE));
			} catch (AccessDeniedException e) {
				processedEvents.addAll(shareInfoService.buildProcessedEntities(eventUuids, ProcessedEntityStatus.ACCESS_DENIED_FAILURE));
			}
		} else {
			processedEvents.addAll(shareInfoService.buildProcessedEntities(eventUuids, ProcessedEntityStatus.EXTERNAL_SURVEILLANCE_FAILURE));
		}

		return processedEvents;
	}

	private void doSendEvents(List<String> eventUuids, boolean archived) throws ExternalSurveillanceToolException {
		if (!userService.hasRight(UserRight.EVENT_EDIT)) {
			throw new AccessDeniedException(I18nProperties.getString(Strings.errorForbidden));
		}

		ExportParameters params = new ExportParameters();
		params.setEventUuids(eventUuids);
		params.setArchived(archived);

		sendRequest(params);
	}

	private void sendRequest(ExportParameters params) throws ExternalSurveillanceToolException {
		String serviceUrl = configFacade.getExternalSurveillanceToolGatewayUrl().trim();

		Invocation.Builder request =
			ClientBuilder.newBuilder().connectTimeout(30, TimeUnit.SECONDS).build().target(serviceUrl).path("export").request();

		Response response;
		try {
			response = request.post(Entity.json(params));
		} catch (Exception e) {
			logger.error("Failed to send request to external surveillance tool", e);
			throw new ExternalSurveillanceToolException(I18nProperties.getString(Strings.ExternalSurveillanceToolGateway_notificationErrorSending));
		}
		int status = response.getStatus();

		switch (status) {
		case HttpServletResponse.SC_OK:
		case HttpServletResponse.SC_NO_CONTENT:
			if (params.getCaseUuids() != null) {
				caseService.getByUuids(params.getCaseUuids())
					.forEach(caze -> shareInfoService.createAndPersistShareInfo(caze, ExternalShareStatus.SHARED));
			}

			if (params.getEventUuids() != null) {
				eventService.getByUuids(params.getEventUuids())
					.forEach(event -> shareInfoService.createAndPersistShareInfo(event, ExternalShareStatus.SHARED));
			}
			return;
		case HttpServletResponse.SC_NOT_FOUND:
			throw new ExternalSurveillanceToolException(I18nProperties.getString(Strings.ExternalSurveillanceToolGateway_notificationErrorSending));
		case HttpServletResponse.SC_BAD_REQUEST:
			throw new ExternalSurveillanceToolException(I18nProperties.getString(Strings.ExternalSurveillanceToolGateway_notificationEntryNotSent));
		default:
			ExternalSurveillanceToolResponse entity = response.readEntity(ExternalSurveillanceToolResponse.class);
			if (entity == null || StringUtils.isBlank(entity.getMessage())) {
				throw new ExternalSurveillanceToolException(
					I18nProperties.getString(Strings.ExternalSurveillanceToolGateway_notificationErrorSending));
			} else if (StringUtils.isNotBlank(entity.getErrorCode())) {
				throw new ExternalSurveillanceToolException(entity.getMessage(), entity.getErrorCode());
			} else {
				throw new ExternalSurveillanceToolException(entity.getMessage());
			}
		}
	}

	@Override
	@RightsAllowed(UserRight._EXTERNAL_SURVEILLANCE_SHARE)
	public void createCaseShareInfo(List<String> caseUuids) {
		if (!userService.hasAnyRight(EnumSet.of(UserRight.CASE_CREATE, UserRight.CASE_EDIT))) {
			throw new AccessDeniedException(I18nProperties.getString(Strings.errorForbidden));
		}

		caseService.getByUuids(caseUuids).forEach(caze -> shareInfoService.createAndPersistShareInfo(caze, ExternalShareStatus.SHARED));
	}

	@Override
	@RightsAllowed(UserRight._EXTERNAL_SURVEILLANCE_SHARE)
	public void createEventShareInfo(List<String> eventUuids) {
		if (!userService.hasAnyRight(EnumSet.of(UserRight.EVENT_CREATE, UserRight.EVENT_EDIT))) {
			throw new AccessDeniedException(I18nProperties.getString(Strings.errorForbidden));
		}

		eventService.getByUuids(eventUuids).forEach(event -> shareInfoService.createAndPersistShareInfo(event, ExternalShareStatus.SHARED));
	}

	@Override
	@RightsAllowed(UserRight._EXTERNAL_SURVEILLANCE_DELETE)
	public void deleteCases(List<CaseDataDto> cases) throws ExternalSurveillanceToolException {
		if (!userService.hasRight(UserRight.CASE_EDIT)) {
			throw new AccessDeniedException(I18nProperties.getString(Strings.errorForbidden));
		}

		doDeleteCases(cases);
	}

	@RightsAllowed({
		UserRight._SORMAS_TO_SORMAS_SHARE,
		UserRight._SORMAS_TO_SORMAS_CLIENT,
		UserRight._CASE_DELETE,
		UserRight._SYSTEM })
	public void deleteCasesInternal(List<CaseDataDto> cases) throws ExternalSurveillanceToolException {
		doDeleteCases(cases);
	}

	private void doDeleteCases(List<CaseDataDto> cases) throws ExternalSurveillanceToolException {
		DeleteParameters params = new DeleteParameters();
		params.setCases(cases);

		sendDeleteRequest(params);

		caseService.getByUuids(cases.stream().map(CaseDataDto::getUuid).collect(Collectors.toList())).forEach(caze -> {
			shareInfoService.createAndPersistShareInfo(caze, ExternalShareStatus.DELETED);
		});
	}

	@Override
	@RightsAllowed(UserRight._EXTERNAL_SURVEILLANCE_DELETE)
	public void deleteEvents(List<EventDto> events) throws ExternalSurveillanceToolException {
		if (!userService.hasRight(UserRight.EVENT_EDIT)) {
			throw new AccessDeniedException(I18nProperties.getString(Strings.errorForbidden));
		}

		doDeleteEvents(events);
	}

	@RightsAllowed({
		UserRight._SORMAS_TO_SORMAS_SHARE,
		UserRight._SORMAS_TO_SORMAS_CLIENT,
		UserRight._EVENT_DELETE,
		UserRight._SYSTEM })
	public void deleteEventsInternal(List<EventDto> events) throws ExternalSurveillanceToolException {
		doDeleteEvents(events);
	}

	private void doDeleteEvents(List<EventDto> events) throws ExternalSurveillanceToolException {
		DeleteParameters params = new DeleteParameters();
		params.setEvents(events);

		sendDeleteRequest(params);

		eventService.getByUuids(events.stream().map(EventDto::getUuid).collect(Collectors.toList())).forEach(event -> {
			shareInfoService.createAndPersistShareInfo(event, ExternalShareStatus.DELETED);
		});
	}

	private void sendDeleteRequest(DeleteParameters params) throws ExternalSurveillanceToolException {
		String serviceUrl = configFacade.getExternalSurveillanceToolGatewayUrl().trim();

		Invocation.Builder request =
			ClientBuilder.newBuilder().connectTimeout(30, TimeUnit.SECONDS).build().target(serviceUrl).path("delete").request();

		Response response;
		try {
			response = request.post(Entity.json(params));
		} catch (Exception e) {
			logger.error("Failed to send delete request to external surveillance tool", e);
			throw new ExternalSurveillanceToolException(I18nProperties.getString(Strings.ExternalSurveillanceToolGateway_notificationErrorDeleting));
		}

		int statusCode = response.getStatus();

		switch (statusCode) {
		case HttpServletResponse.SC_OK:
		case HttpServletResponse.SC_NO_CONTENT:
			return;
		case HttpServletResponse.SC_BAD_REQUEST:
			throw new ExternalSurveillanceToolException(
				I18nProperties.getString(Strings.ExternalSurveillanceToolGateway_notificationEntryNotDeleted));
		default:
			throw new ExternalSurveillanceToolException(I18nProperties.getString(Strings.ExternalSurveillanceToolGateway_notificationErrorDeleting));
		}
	}

	@Override
	@PermitAll
	public String getVersion() throws ExternalSurveillanceToolException {
		String serviceUrl = configFacade.getExternalSurveillanceToolGatewayUrl().trim();
		String versionEndpoint = configFacade.getExternalSurveillanceToolVersionEndpoint().trim();

		try {
			Response response =
				ClientBuilder.newBuilder().connectTimeout(30, TimeUnit.SECONDS).build().target(serviceUrl).path(versionEndpoint).request().get();
			int status = response.getStatus();

			if (status != HttpServletResponse.SC_OK) {
				throw new ExternalSurveillanceToolException(I18nProperties.getString(Strings.ExternalSurveillanceToolGateway_versionRequestError));
			}

			ExternalSurveillanceToolResponse entity = response.readEntity(ExternalSurveillanceToolResponse.class);
			return entity.getMessage();
		} catch (Exception e) {
			logger.error("Couldn't get version of external surveillance tool at {}{}", serviceUrl, versionEndpoint, e);
			throw new ExternalSurveillanceToolException(I18nProperties.getString(Strings.ExternalSurveillanceToolGateway_versionRequestError));
		}
	}

	public static class ExportParameters {

		private List<String> caseUuids;
		private List<String> eventUuids;
		private boolean archived;

		public List<String> getCaseUuids() {
			return caseUuids;
		}

		public void setCaseUuids(List<String> caseUuids) {
			this.caseUuids = caseUuids;
		}

		public List<String> getEventUuids() {
			return eventUuids;
		}

		public void setEventUuids(List<String> eventUuids) {
			this.eventUuids = eventUuids;
		}

		public boolean isArchived() {
			return archived;
		}

		public void setArchived(boolean archived) {
			this.archived = archived;
		}
	}

	public static class DeleteParameters {

		private List<CaseDataDto> cases;

		private List<EventDto> events;

		public List<CaseDataDto> getCases() {
			return cases;
		}

		public void setCases(List<CaseDataDto> cases) {
			this.cases = cases;
		}

		public List<EventDto> getEvents() {
			return events;
		}

		public void setEvents(List<EventDto> events) {
			this.events = events;
		}
	}

	@LocalBean
	@Stateless
	public static class ExternalSurveillanceToolGatewayFacadeEjbLocal extends ExternalSurveillanceToolGatewayFacadeEjb {

	}
}
