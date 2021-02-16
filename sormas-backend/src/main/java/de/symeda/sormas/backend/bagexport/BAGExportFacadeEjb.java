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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
import de.symeda.sormas.api.bagexport.BAGExportContactDto;
import de.symeda.sormas.api.bagexport.BAGExportFacade;
import de.symeda.sormas.api.person.PersonAddressType;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactJoins;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.sample.PathogenTest;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.utils.CaseJoins;
import org.apache.commons.collections.CollectionUtils;

@Stateless(name = "BAGExportFacade")
public class BAGExportFacadeEjb implements BAGExportFacade {

	private static final String TODO_VALUE = "";

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Override
	public List<BAGExportCaseDto> getCaseExportList(Collection<String> selectedRows, int first, int max) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BAGExportCaseDto> cq = cb.createQuery(BAGExportCaseDto.class);
		Root<Case> caseRoot = cq.from(Case.class);

		CaseJoins<Case> caseJoins = new CaseJoins<>(caseRoot);

		Join<Case, Person> person = caseJoins.getPerson();
		Join<Person, Location> homeAddress = caseJoins.getPersonAddress();

		Expression<String> homeAddressCountry = cb.literal(TODO_VALUE);
		Expression<String> mobileNumber = cb.literal(TODO_VALUE);
		Expression<String> activityMappingYn = cb.literal(TODO_VALUE);

		cq.multiselect(
			caseRoot.get(Case.CASE_ID_ISM),
			caseRoot.get(Case.ID),
			person.get(Person.ID),
			person.get(Person.LAST_NAME),
			person.get(Person.FIRST_NAME),
			homeAddress.get(Location.STREET),
			homeAddress.get(Location.HOUSE_NUMBER),
			homeAddress.get(Location.CITY),
			homeAddress.get(Location.POSTAL_CODE),
			homeAddressCountry,
			person.get(Person.PHONE),
			mobileNumber,
			person.get(Person.EMAIL_ADDRESS),
			person.get(Person.SEX),
			person.get(Person.BIRTHDATE_DD),
			person.get(Person.BIRTHDATE_MM),
			person.get(Person.BIRTHDATE_YYYY),
			person.get(Person.OCCUPATION_TYPE),

			caseJoins.getSymptoms().get(Symptoms.SYMPTOMATIC),
			caseRoot.get(Case.COVID_TEST_REASON),
			caseRoot.get(Case.COVID_TEST_REASON_DETAILS),
			caseJoins.getSymptoms().get(Symptoms.ONSET_DATE),

			activityMappingYn,

			caseRoot.get(Case.CONTACT_TRACING_FIRST_CONTACT_DATE),

			caseRoot.get(Case.WAS_IN_QUARANTINE_BEFORE_ISOLATION),
			caseRoot.get(Case.QUARANTINE_REASON_BEFORE_ISOLATION),
			caseRoot.get(Case.QUARANTINE_REASON_BEFORE_ISOLATION_DETAILS),

			caseRoot.get(Case.QUARANTINE),
			caseRoot.get(Case.QUARANTINE_TYPE_DETAILS),
			caseRoot.get(Case.QUARANTINE_FROM),
			caseRoot.get(Case.QUARANTINE_TO),
			caseRoot.get(Case.END_OF_ISOLATION_REASON),
			caseRoot.get(Case.END_OF_ISOLATION_REASON_DETAILS));

		if (CollectionUtils.isNotEmpty(selectedRows)) {
			cq.where(CriteriaBuilderHelper.andInValues(selectedRows, null, cb, caseRoot.get(Case.UUID)));
		}

		List<BAGExportCaseDto> exportList =
			em.createQuery(cq).setHint(ModelConstants.HINT_HIBERNATE_READ_ONLY, true).setFirstResult(first).setMaxResults(max).getResultList();

		Map<Long, List<Location>> personAddresses = new HashMap<>();
		Map<Long, List<Sample>> samples = new HashMap<>();

		if (exportList.size() > 0) {
			List<Long> caseIds = exportList.stream().map(BAGExportCaseDto::getCaseId).collect(Collectors.toList());
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
			samplesCq.where(caseIdExpr.in(caseIds)).orderBy(cb.asc(samplesRoot.get(Sample.REPORT_DATE_TIME)));

			List<Sample> samplesList = em.createQuery(samplesCq).setHint(ModelConstants.HINT_HIBERNATE_READ_ONLY, true).getResultList();
			samples.putAll(samplesList.stream().collect(Collectors.groupingBy(s -> s.getAssociatedCase().getId())));
		}

		exportList.forEach(caze -> {
			List<Location> addresses = personAddresses.getOrDefault(caze.getPersonId(), Collections.emptyList());

			addresses.stream().filter(a -> PersonAddressType.PLACE_OF_WORK.equals(a.getAddressType())).findFirst().ifPresent(workAddress -> {
				caze.setWorkPlaceName(TODO_VALUE);
				caze.setWorkPlaceStreet(workAddress.getStreet());
				caze.setWorkPlaceStreetNumber(workAddress.getHouseNumber());
				caze.setWorkPlaceCity(workAddress.getCity());
				caze.setWorkPlacePostalCode(workAddress.getPostalCode());
				caze.setWorkPlaceCountry(TODO_VALUE);
			});

			caze.setExposureLocationYn(YesNoUnknown.NO);
			addresses.stream().filter(a -> PersonAddressType.PLACE_OF_EXPOSURE.equals(a.getAddressType())).findFirst().ifPresent(exposureAddress -> {
				caze.setExposureLocationYn(YesNoUnknown.YES);
				caze.setExposureCountry(TODO_VALUE);
				caze.setExposureLocationType(exposureAddress.getFacilityType());
				caze.setExposureLocationFlightDetail(exposureAddress.getFacilityDetails());
				caze.setExposureLocationName(TODO_VALUE);
				caze.setExposureLocationStreet(exposureAddress.getStreet());
				caze.setExposureLocationStreetNumber(exposureAddress.getHouseNumber());
				caze.setExposureLocationCity(exposureAddress.getCity());
				caze.setExposureLocationPostalCode(exposureAddress.getPostalCode());
				caze.setExposureLocationFlightDetail(TODO_VALUE);
			});

			addresses.stream()
				.filter(a -> PersonAddressType.PLACE_OF_ISOLATION.equals(a.getAddressType()))
				.findFirst()
				.ifPresent(isolationAddress -> {
					caze.setIsolationLocationStreet(isolationAddress.getStreet());
					caze.setIsolationLocationStreetNumber(isolationAddress.getHouseNumber());
					caze.setIsolationLocationCity(isolationAddress.getCity());
					caze.setIsolationLocationPostalCode(isolationAddress.getPostalCode());
					caze.setIsolationLocationCountry(TODO_VALUE);
				});

			List<Sample> caseSamples = samples.get(caze.getCaseId());
			if (caseSamples != null && caseSamples.size() > 0) {
				Sample firstSample = caseSamples.get(0);
				caze.setSampleDate(firstSample.getSampleDateTime());

				List<PathogenTest> sortedTests =
					firstSample.getPathogenTests().stream().sorted(Comparator.comparing(PathogenTest::getTestDateTime)).collect(Collectors.toList());

				Optional<PathogenTest> positiveTest =
					sortedTests.stream().filter(t -> t.getTestResult() == PathogenTestResultType.POSITIVE).findFirst();
				if (positiveTest.isPresent()) {
					setCasePathogenTestData(caze, positiveTest.get());
				} else if (sortedTests.size() > 0) {
					setCasePathogenTestData(caze, sortedTests.get(0));
				}
			}
		});

		return exportList;
	}

	@Override
	public List<BAGExportContactDto> getContactExportList(Collection<String> selectedRows, int first, int max) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BAGExportContactDto> cq = cb.createQuery(BAGExportContactDto.class);
		Root<Contact> contactRoot = cq.from(Contact.class);

		ContactJoins contactJoins = new ContactJoins(contactRoot);

		Join<Contact, Person> person = contactJoins.getPerson();
		Join<Person, Location> homeAddress = contactJoins.getPersonAddress();
		Join<Contact, Case> caze = contactJoins.getCaze();

		Expression<String> mobileNumber = cb.literal(TODO_VALUE);
		Expression<Date> caseLinkContactDate = cb.nullLiteral(Date.class);

		cq.multiselect(
			contactRoot.get(Contact.ID),
			person.get(Person.ID),
			person.get(Person.LAST_NAME),
			person.get(Person.FIRST_NAME),
			homeAddress.get(Location.STREET),
			homeAddress.get(Location.HOUSE_NUMBER),
			homeAddress.get(Location.CITY),
			homeAddress.get(Location.POSTAL_CODE),
			person.get(Person.PHONE),
			mobileNumber,
			person.get(Person.SEX),
			person.get(Person.BIRTHDATE_DD),
			person.get(Person.BIRTHDATE_MM),
			person.get(Person.BIRTHDATE_YYYY),
			person.get(Person.OCCUPATION_TYPE),
			contactRoot.get(Contact.QUARANTINE),
			contactRoot.get(Contact.QUARANTINE_TYPE_DETAILS),
			caze.get(Case.CASE_ID_ISM),
			caze.get(Case.ID),
			caseLinkContactDate,
			contactRoot.get(Contact.QUARANTINE_FROM),
			contactRoot.get(Contact.QUARANTINE_TO),
			contactRoot.get(Contact.END_OF_QUARANTINE_REASON),
			contactRoot.get(Contact.END_OF_QUARANTINE_REASON_DETAILS));

		if (CollectionUtils.isNotEmpty(selectedRows)) {
			cq.where(CriteriaBuilderHelper.andInValues(selectedRows, null, cb, contactRoot.get(Contact.UUID)));
		}

		List<BAGExportContactDto> exportList =
			em.createQuery(cq).setHint(ModelConstants.HINT_HIBERNATE_READ_ONLY, true).setFirstResult(first).setMaxResults(max).getResultList();

		Map<Long, List<Location>> personAddresses = new HashMap<>();
		Map<Long, List<Sample>> samples = new HashMap<>();

		if (exportList.size() > 0) {
			List<Long> contactIds = exportList.stream().map(BAGExportContactDto::getContactId).collect(Collectors.toList());
			List<Long> personIds = exportList.stream().map(BAGExportContactDto::getPersonId).collect(Collectors.toList());

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

			Path<Object> contactIdExpr = samplesRoot.join(Sample.ASSOCIATED_CONTACT, JoinType.LEFT).get(Contact.ID);
			samplesCq.where(contactIdExpr.in(contactIds)).orderBy(cb.asc(samplesRoot.get(Sample.REPORT_DATE_TIME)));

			List<Sample> samplesList = em.createQuery(samplesCq).setHint(ModelConstants.HINT_HIBERNATE_READ_ONLY, true).getResultList();
			samples.putAll(samplesList.stream().collect(Collectors.groupingBy(s -> s.getAssociatedContact().getId())));
		}

		exportList.forEach(contact -> {
			List<Location> addresses = personAddresses.getOrDefault(contact.getPersonId(), Collections.emptyList());

			addresses.stream().filter(a -> PersonAddressType.PLACE_OF_WORK.equals(a.getAddressType())).findFirst().ifPresent(workAddress -> {
				contact.setWorkPlaceName(TODO_VALUE);
				contact.setWorkPlacePostalCode(workAddress.getPostalCode());
				contact.setWorkPlaceName(TODO_VALUE);
			});

			addresses.stream().filter(a -> PersonAddressType.PLACE_OF_EXPOSURE.equals(a.getAddressType())).findFirst().ifPresent(exposureAddress -> {
				contact.setExposureLocationCountry(TODO_VALUE);
				contact.setExposureLocationType(exposureAddress.getFacilityType());
				contact.setExposureLocationTypeDetails(exposureAddress.getFacilityDetails());
				contact.setExposureLocationName(TODO_VALUE);
				contact.setOtherExposureLocation(exposureAddress.getFacilityDetails());
				contact.setExposureLocationStreet(exposureAddress.getStreet());
				contact.setExposureLocationStreetNumber(exposureAddress.getHouseNumber());
				contact.setExposureLocationCity(exposureAddress.getCity());
				contact.setExposureLocationPostalCode(exposureAddress.getPostalCode());
				contact.setExposureLocationFlightDetail(TODO_VALUE);
			});

			List<Sample> contactSamples = samples.get(contact.getContactId());
			if (contactSamples != null && contactSamples.size() > 0) {
				Sample firstSample = contactSamples.get(0);
				contact.setSampleDate(firstSample.getSampleDateTime());

				List<PathogenTest> sortedTests =
					firstSample.getPathogenTests().stream().sorted(Comparator.comparing(PathogenTest::getTestDateTime)).collect(Collectors.toList());

				Optional<PathogenTest> positiveTest =
					sortedTests.stream().filter(t -> t.getTestResult() == PathogenTestResultType.POSITIVE).findFirst();
				if (positiveTest.isPresent()) {
					setContactPathogenTestData(contact, positiveTest.get());
				} else if (sortedTests.size() > 0) {
					setContactPathogenTestData(contact, sortedTests.get(0));
				}
			}
		});

		return exportList;
	}

	private void setCasePathogenTestData(BAGExportCaseDto caze, PathogenTest test) {
		caze.setLabReportDate(test.getTestDateTime());
		caze.setTestType(test.getTestType());
		caze.setTestResult(test.getTestResult());
	}

	private void setContactPathogenTestData(BAGExportContactDto contact, PathogenTest test) {
		contact.setTestType(test.getTestType());
		contact.setTestResult(test.getTestResult());
	}

	@LocalBean
	@Stateless
	public static class BAGExportFacadeEjbLocal extends BAGExportFacadeEjb {
	}
}
