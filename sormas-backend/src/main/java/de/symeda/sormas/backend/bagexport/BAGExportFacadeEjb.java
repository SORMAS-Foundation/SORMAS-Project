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

package de.symeda.sormas.backend.bagexport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.bagexport.BAGExportCaseDto;
import de.symeda.sormas.api.bagexport.BAGExportFacade;
import de.symeda.sormas.api.person.PersonAddressType;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.location.LocationService;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.sample.PathogenTest;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.utils.CaseJoins;

@Stateless(name = "BAGExportFacade")
public class BAGExportFacadeEjb implements BAGExportFacade {

	private static final String TODO_VALUE = "";

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private LocationService locationService;

	@Override
	public List<BAGExportCaseDto> getCaseExportList(int first, int max) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BAGExportCaseDto> cq = cb.createQuery(BAGExportCaseDto.class);
		Root<Case> caseRoot = cq.from(Case.class);

		CaseJoins<Case> caseCaseJoins = new CaseJoins<>(caseRoot);

		Join<Case, Person> person = caseCaseJoins.getPerson();

		Expression<String> TODO = cb.literal(TODO_VALUE);

		cq.multiselect(
			caseRoot.get(Case.CASE_ID_ISM),
			caseRoot.get(Case.ID),
			person.get(Person.ID),
			person.get(Person.LAST_NAME),
			person.get(Person.FIRST_NAME),
			person.get(Person.PHONE),
			TODO,
			person.get(Person.EMAIL_ADDRESS),
			person.get(Person.SEX),
			person.get(Person.BIRTHDATE_DD),
			person.get(Person.BIRTHDATE_MM),
			person.get(Person.BIRTHDATE_YYYY),
			person.get(Person.OCCUPATION_TYPE),
			caseCaseJoins.getSymptoms().get(Symptoms.SYMPTOMATIC),
			caseRoot.get(Case.COVID_TEST_REASON),
			caseRoot.get(Case.COVID_TEST_REASON_DETAILS),
			caseCaseJoins.getSymptoms().get(Symptoms.ONSET_DATE),
			caseRoot.get(Case.CONTACT_TRACING_FIRST_CONTACT_DATE),
			caseRoot.get(Case.QUARANTINE),
			caseRoot.get(Case.QUARANTINE_TYPE_DETAILS),
			caseRoot.get(Case.FOLLOW_UP_UNTIL),
			caseRoot.get(Case.QUARANTINE_TO),
			caseRoot.get(Case.END_OF_ISOLATION_REASON),
			caseRoot.get(Case.END_OF_ISOLATION_REASON_DETAILS));

		List<BAGExportCaseDto> exportList =
			em.createQuery(cq).setHint(ModelConstants.HINT_HIBERNATE_READ_ONLY, true).setFirstResult(first).setMaxResults(max).getResultList();

		Map<Long, List<Location>> personAddresses = new HashMap<>();
		Map<Long, List<Sample>> samples = new HashMap<>();

		if (exportList.size() > 0) {
			List<Long> caseIdIds = exportList.stream().map(BAGExportCaseDto::getCaseId).collect(Collectors.toList());
			List<Long> personIds = exportList.stream().map(BAGExportCaseDto::getPersonId).collect(Collectors.toList());

			// get addresses
			CriteriaQuery<Object[]> addressesCq = cb.createQuery(Object[].class);
			Root<Location> addressesRoot = addressesCq.from(Location.class);

			addressesCq.where(addressesRoot.get(Location.PERSON).get(Person.ID).in(personIds));

			addressesCq.multiselect(addressesRoot.get(Location.PERSON).get(Person.ID), addressesRoot);
			List<Object[]> personIdLocationList = em.createQuery(addressesCq).getResultList();

			personIdLocationList.forEach(personIdLocation -> {
				Long personId = (Long) personIdLocation[0];
				Location location = (Location) personIdLocation[1];

				List<Location> personLocations = personAddresses.get(personId);
				if (personLocations == null) {
					personLocations = new ArrayList<>();
				}

				personLocations.add(location);
				personAddresses.put(personId, personLocations);
			});

			// get samples
			CriteriaQuery<Sample> samplesCq = cb.createQuery(Sample.class);
			Root<Sample> samplesRoot = samplesCq.from(Sample.class);

			Path<Object> caseIdExpr = samplesRoot.join(Sample.ASSOCIATED_CASE, JoinType.LEFT).get(Case.ID);
			samplesCq.where(caseIdExpr.in(caseIdIds)).orderBy(cb.desc(samplesRoot.get(Sample.REPORT_DATE_TIME)));

			List<Sample> samplesList = em.createQuery(samplesCq).setHint(ModelConstants.HINT_HIBERNATE_READ_ONLY, true).getResultList();
			samples.putAll(samplesList.stream().collect(Collectors.groupingBy(s -> s.getAssociatedCase().getId())));
		}

		exportList.forEach(caze -> {
			List<Location> addresses = personAddresses.getOrDefault(caze.getPersonId(), Collections.emptyList());

			addresses.stream().filter(a -> PersonAddressType.HOME.equals(a.getAddressType())).findFirst().ifPresent(homeAddress -> {
				caze.setHomeAddressStreet(homeAddress.getStreet());
				caze.setHomeAddressHouseNumber(homeAddress.getHouseNumber());
				caze.setHomeAddressCity(homeAddress.getCity());
				caze.setHomeAddressPostalCode(homeAddress.getPostalCode());
			});

			addresses.stream().filter(a -> PersonAddressType.PLACE_OF_WORK.equals(a.getAddressType())).findFirst().ifPresent(workAddress -> {
				caze.setWorkPlaceStreet(workAddress.getStreet());
				caze.setWorkPlaceStreetNumber(workAddress.getHouseNumber());
				caze.setWorkPlaceLocation(workAddress.getCity());
				caze.setWorkPlacePostalCode(workAddress.getPostalCode());
			});

			caze.setInfectionLocationYn(YesNoUnknown.NO);
			addresses.stream().filter(a -> PersonAddressType.PLACE_OF_EXPOSURE.equals(a.getAddressType())).findFirst().ifPresent(exposureAddress -> {
				caze.setInfectionLocationYn(YesNoUnknown.YES);

				caze.setInfectionLocationStreet(exposureAddress.getStreet());
				caze.setInfectionLocationStreetNumber(exposureAddress.getHouseNumber());
				caze.setInfectionLocationCity(exposureAddress.getCity());
				caze.setInfectionLocationPostalCode(exposureAddress.getPostalCode());
			});

			addresses.stream()
				.filter(a -> PersonAddressType.PLACE_OF_ISOLATION.equals(a.getAddressType()))
				.findFirst()
				.ifPresent(isolationAddress -> {
					caze.setIsolationLocationStreet(isolationAddress.getStreet());
					caze.setIsolationLocationStreetNumber(isolationAddress.getHouseNumber());
					caze.setIsolationLocationCity(isolationAddress.getCity());
					caze.setIsolationLocationPostalCode(isolationAddress.getPostalCode());
				});

			List<Sample> caseSamples = samples.get(caze.getCaseId());
			if (caseSamples != null && caseSamples.size() > 0) {
				Sample newestSample = caseSamples.get(0);
				caze.setSampleDate(newestSample.getSampleDateTime());
				newestSample.getPathogenTests().stream().max(Comparator.comparing(PathogenTest::getTestDateTime)).ifPresent(pathogenTest -> {
					caze.setLabReportDate(pathogenTest.getTestDateTime());
					caze.setTestType(pathogenTest.getTestType());
					caze.setTestResult(pathogenTest.getTestResult());
				});
			}
		});

		return exportList;
	}

	@LocalBean
	@Stateless
	public static class BAGExportFacadeEjbLocal extends BAGExportFacadeEjb {
	}
}
