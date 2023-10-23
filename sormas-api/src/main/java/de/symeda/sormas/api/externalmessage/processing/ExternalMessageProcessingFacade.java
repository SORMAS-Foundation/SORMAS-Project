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

package de.symeda.sormas.api.externalmessage.processing;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import de.symeda.sormas.api.ConfigFacade;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.caze.CaseSimilarityCriteria;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportDto;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportFacade;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactFacade;
import de.symeda.sormas.api.contact.ContactSimilarityCriteria;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.customizableenum.CustomEnumNotFoundException;
import de.symeda.sormas.api.customizableenum.CustomizableEnumFacade;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventFacade;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.event.EventParticipantCriteria;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantFacade;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.event.SimilarEventParticipantDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageFacade;
import de.symeda.sormas.api.feature.FeatureConfigurationFacade;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.infrastructure.country.CountryFacade;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityFacade;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestFacade;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleFacade;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.sample.SampleSimilarityCriteria;
import de.symeda.sormas.api.user.UserRight;

public abstract class ExternalMessageProcessingFacade {

	protected final ExternalMessageFacade externalMessageFacade;
	protected final ConfigFacade configFacade;
	protected final FeatureConfigurationFacade featureConfigurationFacade;
	protected final CaseFacade caseFacade;
	protected final FacilityFacade facilityFacade;
	private final ContactFacade contactFacade;
	private final EventFacade eventFacade;
	private final EventParticipantFacade eventParticipantFacade;
	private final SampleFacade sampleFacade;
	private final PathogenTestFacade pathogenTestFacade;
	private final CustomizableEnumFacade customizableEnumFacade;
	private final CountryFacade countryFacade;
	private final SurveillanceReportFacade surveillanceReportFacade;

	public ExternalMessageProcessingFacade(
		ExternalMessageFacade externalMessageFacade,
		ConfigFacade configFacade,
		FeatureConfigurationFacade featureConfigurationFacade,
		CaseFacade caseFacade,
		ContactFacade contactFacade,
		EventFacade eventFacade,
		EventParticipantFacade eventParticipantFacade,
		SampleFacade sampleFacade,
		PathogenTestFacade pathogenTestFacade,
		FacilityFacade facilityFacade,
		CustomizableEnumFacade customizableEnumFacade,
		CountryFacade countryFacade,
		SurveillanceReportFacade surveillanceReportFacade) {
		this.externalMessageFacade = externalMessageFacade;
		this.configFacade = configFacade;
		this.featureConfigurationFacade = featureConfigurationFacade;
		this.caseFacade = caseFacade;
		this.facilityFacade = facilityFacade;
		this.contactFacade = contactFacade;
		this.eventFacade = eventFacade;
		this.eventParticipantFacade = eventParticipantFacade;
		this.sampleFacade = sampleFacade;
		this.pathogenTestFacade = pathogenTestFacade;
		this.customizableEnumFacade = customizableEnumFacade;
		this.countryFacade = countryFacade;
		this.surveillanceReportFacade = surveillanceReportFacade;
	}

	public boolean existsForwardedExternalMessageWith(String reportId) {
		return externalMessageFacade.existsForwardedExternalMessageWith(reportId);
	}

	public boolean isFeatureDisabled(FeatureType featureType) {
		return featureConfigurationFacade.isFeatureDisabled(featureType);
	}

	public abstract boolean hasAllUserRights(UserRight... userRights);

	public List<CaseSelectionDto> getSimilarCases(CaseSimilarityCriteria caseSimilarityCriteria) {
		return caseFacade.getSimilarCases(caseSimilarityCriteria);
	}

	public CaseDataDto getCaseDataByUuid(String uuid) {
		return caseFacade.getCaseDataByUuid(uuid);
	}

	public List<SimilarContactDto> getMatchingContacts(ContactSimilarityCriteria contactSimilarityCriteria) {
		return contactFacade.getMatchingContacts(contactSimilarityCriteria);
	}

	public ContactDto getContactByUuid(String uuid) {
		return contactFacade.getByUuid(uuid);
	}

	public List<EventIndexDto> getEventsByCriteria(EventCriteria eventCriteria) {
		return eventFacade.getIndexList(eventCriteria, null, null, null);
	}

	public EventDto getEventByUuid(String uuid) {
		return eventFacade.getEventByUuid(uuid, false);
	}

	public List<SimilarEventParticipantDto> getMatchingEventParticipants(EventParticipantCriteria eventParticipantCriteria) {
		return eventParticipantFacade.getMatchingEventParticipants(eventParticipantCriteria);
	}

	public EventParticipantDto getEventParticipantByUuid(String uuid) {
		return eventParticipantFacade.getByUuid(uuid);
	}

	public EventParticipantReferenceDto getEventParticipantRefByEventAndPerson(String eventUuid, String personUuid) {
		return eventParticipantFacade.getReferenceByEventAndPerson(eventUuid, personUuid);
	}

	public List<PathogenTestDto> getPathogenTestsBySample(SampleReferenceDto sample) {
		return pathogenTestFacade.getAllBySample(sample);
	}

	public FacilityDto getFacilityByUuid(String uuid) {
		return facilityFacade.getByUuid(uuid);
	}

	public FacilityReferenceDto getFacilityReferenceByUuid(String uuid) {
		return facilityFacade.getReferenceByUuid(uuid);
	}

	public List<SampleDto> getSamplesByCriteria(SampleCriteria sampleCriteria) {
		return sampleFacade.getSamplesByCriteria(sampleCriteria);
	}

	public List<SampleDto> getSimilarSamples(SampleSimilarityCriteria sampleSimilarityCriteria) {
		return sampleFacade.getSimilarSamples(sampleSimilarityCriteria);
	}

	public boolean isConfiguredCountry(String countryCode) {
		return configFacade.isConfiguredCountry(countryCode);
	}

	public DiseaseVariant getDiseaseVariant(String diseaseVariantValue, Disease disease) throws CustomEnumNotFoundException {
		return customizableEnumFacade.getEnumValue(CustomizableEnumType.DISEASE_VARIANT, diseaseVariantValue, disease);
	}

	public CountryReferenceDto getServerCountry() {
		return countryFacade.getServerCountry();
	}

	public FacilityReferenceDto getFacilityReference(List<String> facilityExternalIds) {

		List<FacilityReferenceDto> labs;

		if (facilityExternalIds != null && !facilityExternalIds.isEmpty()) {

			labs = facilityExternalIds.stream()
				.filter(Objects::nonNull)
				.map(id -> facilityFacade.getByExternalIdAndType(id, FacilityType.LABORATORY, false))
				.flatMap(List::stream)
				.collect(Collectors.toList());
		} else {
			labs = null;
		}

		if (labs == null || labs.isEmpty()) {
			return facilityFacade.getReferenceByUuid(FacilityDto.OTHER_FACILITY_UUID);
		} else if (labs.size() == 1) {
			return labs.get(0);
		} else {
			return null;
		}
	}

	public void saveExternalMessage(ExternalMessageDto externalMessage) {
		externalMessageFacade.save(externalMessage);
	}

	public void saveSurveillanceReport(SurveillanceReportDto surveillanceReport) {
		surveillanceReportFacade.save(surveillanceReport);
	}
}
