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
package de.symeda.sormas.backend.disease;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.disease.DiseaseBurdenDto;
import de.symeda.sormas.api.disease.DiseaseFacade;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.outbreak.OutbreakCriteria;

import de.symeda.sormas.api.person.PresentCondition;

import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.disease.DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventFacadeEjb.EventFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.outbreak.OutbreakFacadeEjb.OutbreakFacadeEjbLocal;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;

/**
 * Provides the application configuration settings
 */
@Stateless(name = "DiseaseFacade")
public class DiseaseFacadeEjb implements DiseaseFacade {

	@EJB
	private CaseFacadeEjbLocal caseFacade;
	@EJB
	private EventFacadeEjbLocal eventFacade;
	@EJB
	private OutbreakFacadeEjbLocal outbreakFacade;
	@EJB
	private PersonFacadeEjbLocal personFacade;

	@EJB
	private RegionFacadeEjb.RegionFacadeEjbLocal regionFacade;
	@EJB
	private DiseaseConfigurationFacadeEjbLocal diseaseConfigurationFacade;

	@Override
	public List<DiseaseBurdenDto> getDiseaseBurdenForDashboard(
		RegionReferenceDto regionRef,
		DistrictReferenceDto districtRef,
		Date from,
		Date to,
		Date previousFrom,
		Date previousTo) {

		//diseases
		List<Disease> diseases = diseaseConfigurationFacade.getAllDiseases(true, true, true);

		//new cases
		CaseCriteria caseCriteria = new CaseCriteria().newCaseDateBetween(from, to, null).region(regionRef).district(districtRef);

		Map<Disease, Long> newCases = caseFacade.getCaseCountByDisease(caseCriteria, true, true);

		//events
		Map<Disease, Long> events =
			eventFacade.getEventCountByDisease(new EventCriteria().region(regionRef).district(districtRef).reportedBetween(from, to));

		//outbreaks
		Map<Disease, Long> outbreakDistrictsCount = outbreakFacade
			.getOutbreakDistrictCountByDisease(new OutbreakCriteria().region(regionRef).district(districtRef).reportedBetween(from, to));

		//last report district
		Map<Disease, District> lastReportedDistricts = caseFacade.getLastReportedDistrictByDisease(caseCriteria, true, true);

		//case fatalities
		Map<Disease, Long> caseFatalities = personFacade.getDeathCountByDisease(caseCriteria, true, true);

		//previous cases
		caseCriteria.newCaseDateBetween(previousFrom, previousTo, null);
		Map<Disease, Long> previousCases = caseFacade.getCaseCountByDisease(caseCriteria, true, true);

		//build diseasesBurden
		List<DiseaseBurdenDto> diseasesBurden = diseases.stream().map(disease -> {
			Long caseCount = newCases.getOrDefault(disease, 0L);
			Long previousCaseCount = previousCases.getOrDefault(disease, 0L);
			Long eventCount = events.getOrDefault(disease, 0L);
			Long outbreakDistrictCount = outbreakDistrictsCount.getOrDefault(disease, 0L);
			Long caseFatalityCount = caseFatalities.getOrDefault(disease, 0L);
			District lastReportedDistrict = lastReportedDistricts.getOrDefault(disease, null);

			String lastReportedDistrictName = lastReportedDistrict == null ? "" : lastReportedDistrict.getName();

			return new DiseaseBurdenDto(
				disease,
				caseCount,
				previousCaseCount,
				eventCount,
				outbreakDistrictCount,
				caseFatalityCount,
				lastReportedDistrictName);

		}).collect(Collectors.toList());

		return diseasesBurden;
	}

	@Override
	public DiseaseBurdenDto getDiseaseForDashboard(
		RegionReferenceDto regionRef,
		DistrictReferenceDto districtRef,
		Disease disease,
		Date from,
		Date to,
		Date previousFrom,
		Date previousTo) {

		//new cases
		CaseCriteria caseCriteria = new CaseCriteria()
				.newCaseDateBetween(from, to, null).region(regionRef).district(districtRef).disease(disease);

		Map<Disease, Long> newCases = caseFacade.getCaseCountByDisease(caseCriteria, true, true);

		//events
		Map<Disease, Long> events =
			eventFacade.getEventCountByDisease(new EventCriteria().region(regionRef).district(districtRef).reportedBetween(from, to));


		//outbreaks
		Map<Disease, Long> outbreakDistrictsCount = outbreakFacade
				.getOutbreakDistrictCountByDisease(new OutbreakCriteria().region(regionRef).district(districtRef).reportedBetween(from, to));

		//outbreaks
		Map<Disease, District> outbreakDistricts = outbreakFacade
			.getOutbreakDistrictNameByDisease(new OutbreakCriteria().disease(disease).region(regionRef).district(districtRef).reportedBetween(from, to));

		//last report district
		Map<Disease, District> lastReportedDistricts = caseFacade.getLastReportedDistrictByDisease(caseCriteria, true, true);

		//case fatalities
		Map<Disease, Long> caseFatalities = personFacade.getDeathCountByDisease(caseCriteria, true, true);

		//previous cases
		caseCriteria.newCaseDateBetween(previousFrom, previousTo, null);
		Map<Disease, Long> previousCases = caseFacade.getCaseCountByDisease(caseCriteria, true, true);

		//build diseasesBurden
		Long caseCount = newCases.getOrDefault(disease, 0L);
		Long previousCaseCount = previousCases.getOrDefault(disease, 0L);
		Long eventCount = events.getOrDefault(disease, 0L);
		Long outbreakDistrictCount = outbreakDistrictsCount.getOrDefault(disease, 0L);
		Long caseFatalityCount = caseFatalities.getOrDefault(disease, 0L);
		District lastReportedDistrict = lastReportedDistricts.getOrDefault(disease, null);
		District outbreakDistrict = outbreakDistricts.getOrDefault(disease, null);

		String lastReportedDistrictName = lastReportedDistrict == null ? "" : lastReportedDistrict.getName();
		String outbreakDistrictName = outbreakDistrict == null ? "" : outbreakDistrict.getName();

//			return new DiseaseBurdenDto(
//				regionDto,
//					disease,
//				caseCount,
//				previousCaseCount,
//				eventCount,
//				outbreakDistrictCount,
//				caseFatalityCount,
//				lastReportedDistrictName,
//				"",
//				null
//				);
			return new DiseaseBurdenDto(
					disease,
					caseCount,
					previousCaseCount,
					eventCount,
					outbreakDistrictCount,
					caseFatalityCount,
					lastReportedDistrictName,
					outbreakDistrictName);
//
//		}).collect(Collectors.toList())

//		return null;
	}


	
	public DiseaseBurdenDto getDiseaseGridForDashboard(
			RegionReferenceDto regionRef,
			DistrictReferenceDto districtRef,
			Disease disease,
			Date from,
			Date to,
			Date previousFrom,
			Date previousTo) {

		//Get the region
		RegionDto regionDto = regionFacade.getByUuid(regionRef.getUuid());

		//new cases
		CaseCriteria caseCriteria = new CaseCriteria().newCaseDateBetween(from, to, null).region(regionRef).disease(disease);

		//Load count all dead/ fatalities
		Map<Disease, Long> allCasesFetched = caseFacade.getCaseCountByDisease(caseCriteria, true, true);

		//case fatalities
//		caseCriteria.setOutcome(CaseOutcome.DECEASED);
		Map<Disease, Long> caseFatalities = personFacade.getDeathCountByDisease(caseCriteria, true, true);

		caseCriteria.setOutcome(CaseOutcome.RECOVERED);
		Map<Disease, Long> recoveredCase = personFacade.getDeathCountByDisease(caseCriteria, true, true);

		caseCriteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
		Map<Disease, Long> archievedCase = caseFacade.getCaseCountByDisease(caseCriteria, true, true);

		//build diseasesBurden
		Long totalCaseCount = allCasesFetched.getOrDefault(disease, 0L);
		Long activeCaseCount = archievedCase.getOrDefault(disease, 0L);
		Long recoveredCaseCount = recoveredCase.getOrDefault(disease, 0L);
		Long caseFatalityCount = caseFatalities.getOrDefault(disease, 0L);

		return new DiseaseBurdenDto(
				regionDto,
				totalCaseCount.toString(),
				activeCaseCount.toString(),
				recoveredCaseCount.toString(),
				caseFatalityCount.toString()
		);
	}

	@LocalBean
	@Stateless
	public static class DiseaseFacadeEjbLocal extends DiseaseFacadeEjb {

	}
}
