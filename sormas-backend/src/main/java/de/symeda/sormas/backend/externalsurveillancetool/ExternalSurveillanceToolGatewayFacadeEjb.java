/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolException;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolFacade;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.share.ExternalShareStatus;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventService;
import de.symeda.sormas.backend.share.ExternalShareInfoService;

@Stateless(name = "ExternalSurveillanceToolFacade")
public class ExternalSurveillanceToolGatewayFacadeEjb implements ExternalSurveillanceToolFacade {

	@EJB
	private ConfigFacadeEjbLocal configFacade;

	@EJB
	private CaseService caseService;
	@EJB
	private EventService eventService;
	@EJB
	private ExternalShareInfoService shareInfoService;

	@Override
	public boolean isFeatureEnabled() {
		return StringUtils.isNoneBlank(configFacade.getExternalSurveillanceToolGatewayUrl());
	}

	@Override
	public void sendCases(List<String> caseUuids) throws ExternalSurveillanceToolException {
		ExportParameters params = new ExportParameters();
		params.setCaseUuids(caseUuids);

		sendRequest(params);
	}

	@Override
	public void sendEvents(List<String> eventUuids) throws ExternalSurveillanceToolException {
		ExportParameters params = new ExportParameters();
		params.setEventUuids(eventUuids);

		sendRequest(params);
	}

	private void sendRequest(ExportParameters params) throws ExternalSurveillanceToolException {
		String serviceUrl = configFacade.getExternalSurveillanceToolGatewayUrl().trim();

		Response response = ClientBuilder.newBuilder()
			.connectTimeout(30, TimeUnit.SECONDS)
			.build()
			.target(serviceUrl)
			.path("export")
			.request()
			.post(Entity.json(params));
		int status = response.getStatus();

		switch (status) {
		case HttpServletResponse.SC_OK:
		case HttpServletResponse.SC_NO_CONTENT:
			if (params.getCaseUuids() != null) {
				caseService.getByUuids(params.getCaseUuids()).forEach(caze -> {
					shareInfoService.createAndPersistShareInfo(caze, ExternalShareStatus.SHARED);
				});
			}

			if (params.getEventUuids() != null) {
				eventService.getByUuids(params.getEventUuids()).forEach(event -> {
					shareInfoService.createAndPersistShareInfo(event, ExternalShareStatus.SHARED);
				});
			}
			return;
		case HttpServletResponse.SC_BAD_REQUEST:
			throw new ExternalSurveillanceToolException(Strings.ExternalSurveillanceToolGateway_notificationEntryNotSent);
		default:
			throw new ExternalSurveillanceToolException(Strings.ExternalSurveillanceToolGateway_notificationErrorSending);
		}
	}

	@Override
	public int deleteCases(List<CaseDataDto> cases) {
		DeleteParameters params = new DeleteParameters();
		params.setCases(cases);

		return sendDeleteRequest(params);
	}

	@Override
	public int deleteEvents(List<EventDto> events) {
		DeleteParameters params = new DeleteParameters();
		params.setEvents(events);

		return sendDeleteRequest(params);
	}

	private int sendDeleteRequest(DeleteParameters params) {
		String serviceUrl = configFacade.getExternalSurveillanceToolGatewayUrl().trim();
		Response response = ClientBuilder.newBuilder()
			.connectTimeout(30, TimeUnit.SECONDS)
			.build()
			.target(serviceUrl)
			.path("delete")
			.request()
			.post(Entity.json(params));
		return response.getStatus();
	}

	public static class ExportParameters {

		private List<String> caseUuids;
		private List<String> eventUuids;

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
