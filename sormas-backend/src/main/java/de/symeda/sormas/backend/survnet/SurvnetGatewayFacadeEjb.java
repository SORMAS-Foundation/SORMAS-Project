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

package de.symeda.sormas.backend.survnet;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.event.EventDto;
import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.survnet.SurvnetGatewayFacade;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;

@Stateless(name = "SurvnetGatewayFacade")
public class SurvnetGatewayFacadeEjb implements SurvnetGatewayFacade {

	@EJB
	private ConfigFacadeEjbLocal configFacade;

	@Override
	public boolean isFeatureEnabled() {
		return StringUtils.isNoneBlank(configFacade.getSurvnetGatewayUrl());
	}

	@Override
	public int sendCases(List<String> caseUuids) {
		ExportParameters params = new ExportParameters();
		params.setCaseUuids(caseUuids);

		return sendRequest(params);
	}

	@Override
	public int sendEvents(List<String> eventUuids) {
		ExportParameters params = new ExportParameters();
		params.setEventUuids(eventUuids);

		return sendRequest(params);
	}

	private int sendRequest(ExportParameters params) {
		String serviceUrl = configFacade.getSurvnetGatewayUrl().trim();

		Response response = ClientBuilder.newBuilder()
			.connectTimeout(30, TimeUnit.SECONDS)
			.build()
			.target(serviceUrl)
			.path("export")
			.request()
			.post(Entity.json(params));
		return response.getStatus();
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
		String serviceUrl = configFacade.getSurvnetGatewayUrl().trim();
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
}
