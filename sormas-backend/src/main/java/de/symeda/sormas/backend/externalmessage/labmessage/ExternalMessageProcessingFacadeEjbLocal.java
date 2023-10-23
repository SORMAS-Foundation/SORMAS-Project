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

package de.symeda.sormas.backend.externalmessage.labmessage;

import java.util.Arrays;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import de.symeda.sormas.api.externalmessage.processing.ExternalMessageProcessingFacade;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.surveillancereport.SurveillanceReportFacadeEjb.SurveillanceReportFacadeEjbLocal;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.customizableenum.CustomizableEnumFacadeEjb.CustomizableEnumFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventFacadeEjb.EventFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventParticipantFacadeEjb.EventParticipantFacadeEjbLocal;
import de.symeda.sormas.backend.externalmessage.ExternalMessageFacadeEjb.ExternalMessageFacadeEjbLocal;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.country.CountryFacadeEjb.CountryFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.facility.FacilityFacadeEjb.FacilityFacadeEjbLocal;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.sample.PathogenTestFacadeEjb.PathogenTestFacadeEjbLocal;
import de.symeda.sormas.backend.sample.SampleFacadeEjb.SampleFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserService;

@LocalBean
@Stateless
public class ExternalMessageProcessingFacadeEjbLocal extends ExternalMessageProcessingFacade {

	private final UserService userService;

	@Inject
	public ExternalMessageProcessingFacadeEjbLocal(
		ExternalMessageFacadeEjbLocal externalMessageFacade,
		ConfigFacadeEjbLocal configFacade,
		FeatureConfigurationFacadeEjbLocal featureConfigurationFacade,
		PersonFacadeEjbLocal personFacade,
		CaseFacadeEjbLocal caseFacade,
		ContactFacadeEjbLocal contactFacade,
		EventFacadeEjbLocal eventFacade,
		EventParticipantFacadeEjbLocal eventParticipantFacade,
		SampleFacadeEjbLocal sampleFacade,
		PathogenTestFacadeEjbLocal pathogenTestFacade,
		FacilityFacadeEjbLocal facilityFacade,
		CustomizableEnumFacadeEjbLocal customizableEnumFacade,
		CountryFacadeEjbLocal countryFacade,
		SurveillanceReportFacadeEjbLocal surveillanceReportFacade,
		UserService userService) {
		super(
			externalMessageFacade,
			configFacade,
			featureConfigurationFacade,
			personFacade,
			caseFacade,
			contactFacade,
			eventFacade,
			eventParticipantFacade,
			sampleFacade,
			pathogenTestFacade,
			facilityFacade,
			customizableEnumFacade,
			countryFacade,
			surveillanceReportFacade);
		this.userService = userService;
	}

	@Override
	public boolean hasAllUserRights(UserRight... userRights) {
		return Arrays.stream(userRights).allMatch(userService::hasRight);
	}
}
