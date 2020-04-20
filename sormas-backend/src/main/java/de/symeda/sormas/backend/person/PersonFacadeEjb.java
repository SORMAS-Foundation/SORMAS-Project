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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.person;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import com.auth0.jwt.internal.org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.ApproximateAgeType.ApproximateAgeHelper;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.api.person.PersonIndexDto;
import de.symeda.sormas.api.person.PersonNameDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.PersonSimilarityCriteria;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper.Pair;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.location.LocationFacadeEjb;
import de.symeda.sormas.backend.location.LocationFacadeEjb.LocationFacadeEjbLocal;
import de.symeda.sormas.backend.region.CommunityFacadeEjb;
import de.symeda.sormas.backend.region.CommunityService;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.DistrictFacadeEjb;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.RegionFacadeEjb;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "PersonFacade")
public class PersonFacadeEjb implements PersonFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private PersonService personService;
	@EJB
	private CaseService caseService;
	@EJB
	private CaseFacadeEjbLocal caseFacade;
	@EJB
	private FacilityService facilityService;
	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private CommunityService communityService;
	@EJB
	private LocationFacadeEjbLocal locationFacade;
	@EJB
	private UserService userService;


	@Override
	public List<String> getAllUuids() {
		if (userService.getCurrentUser() == null) {
			return Collections.emptyList();
		}

		return personService.getAllUuids();
	}

	@Override
	public List<PersonNameDto> getMatchingNameDtos(UserReferenceDto userRef, PersonSimilarityCriteria criteria) {
		User user = userService.getByReferenceDto(userRef);
		if (user == null) {
			return Collections.emptyList();
		}

		return new ArrayList<PersonNameDto>(personService.getMatchingNameDtos(user, criteria));
	}

	@Override
	public List<PersonIndexDto> getIndexDtosByUuids(List<String> personUuids) {
		List<Person> persons = personService.getByUuids(personUuids);

		if (persons == null) {
			return new ArrayList<>();
		} else {
			return persons.stream()
					.map(c -> toIndexDto(c))
					.collect(Collectors.toList());
		}
	}

	// multiselect does not work for person, because getting all persons requires multiple querries and we currently don't have an abstraction for this
	//	@Override
	//	public List<PersonIndexDto> getIndexList(UserReferenceDto userRef) {
	//
	//		User user = userService.getByReferenceDto(userRef);
	//		if (user == null) {
	//			return Collections.emptyList();
	//		}
	//		
	//		CriteriaBuilder cb = em.getCriteriaBuilder();
	//		CriteriaQuery<PersonIndexDto> cq = cb.createQuery(PersonIndexDto.class);
	//		Root<Person> person = cq.from(Person.class);
	//
	//		cq.multiselect(person.get(Person.UUID), 
	//				person.get(Person.SEX), person.get(Person.FIRST_NAME), person.get(Person.LAST_NAME),
	//				person.get(Person.PRESENT_CONDITION), person.get(Person.BIRTHDATE_DD), person.get(Person.BIRTHDATE_MM),
	//				person.get(Person.BIRTHDATE_YYYY), person.get(Person.APPROXIMATE_AGE), person.get(Person.APPROXIMATE_AGE_TYPE),
	//				person.get(Person.DEATH_DATE));
	//
	//		Predicate filter = personService.createUserFilter(cb, cq, person, user);
	//
	//		if (filter != null) {
	//			cq.where(filter);
	//		}
	//
	//		List<PersonIndexDto> resultList = em.createQuery(cq).getResultList();
	//		return resultList;
	//	}

	@Override
	public Map<Disease, Long> getDeathCountByDisease(CaseCriteria caseCriteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Case> root = cq.from(Case.class);
		Join<Case, Person> person = root.join(Case.PERSON, JoinType.LEFT);
		
		Predicate filter = caseService.createUserFilter(cb, cq, root);
		filter = AbstractAdoService.and(cb, filter, caseService.createCriteriaFilter(caseCriteria, cb, cq, root));
		filter = AbstractAdoService.and(cb, filter, cb.equal(person.get(Person.CAUSE_OF_DEATH_DISEASE), root.get(Case.DISEASE)));

		if (filter != null) {
			cq.where(filter);
		}

		cq.multiselect(person.get(Person.CAUSE_OF_DEATH_DISEASE), cb.count(person));
		cq.groupBy(person.get(Person.CAUSE_OF_DEATH_DISEASE));

		List<Object[]> results = em.createQuery(cq).getResultList();

		Map<Disease, Long> outbreaks = results.stream().collect(Collectors.toMap(e -> (Disease) e[0], e -> (Long) e[1]));

		return outbreaks;
	}

	@Override
	public List<PersonDto> getPersonsAfter(Date date) {

		User user = userService.getCurrentUser();
		if (user == null) {
			return Collections.emptyList();
		}

		List<PersonDto> result = personService.getAllAfter(date, user).stream()
				.map(c -> toDto(c))
				.collect(Collectors.toList());
		return result;
	}

	@Override
	public List<PersonDto> getByUuids(List<String> uuids) {
		return personService.getByUuids(uuids)
				.stream()
				.map(c -> toDto(c))
				.collect(Collectors.toList());
	}

	@Override
	public List<PersonDto> getDeathsBetween(Date fromDate, Date toDate, DistrictReferenceDto districtRef, Disease disease) {
		User user = userService.getCurrentUser();
		District district = districtService.getByReferenceDto(districtRef);

		if (user == null) {
			return Collections.emptyList();
		}

		List<PersonDto> result = personService.getDeathsBetween(fromDate, toDate, district, disease, user).stream()
				.map(c -> toDto(c))
				.collect(Collectors.toList());
		return result;
	}

	@Override
	public PersonReferenceDto getReferenceByUuid(String uuid) {
		return Optional.of(uuid)
				.map(u -> personService.getByUuid(u))
				.map(c -> toReferenceDto(c))
				.orElse(null);
	}

	@Override
	public PersonDto getPersonByUuid(String uuid) {
		return Optional.of(uuid)
				.map(u -> personService.getByUuid(u))
				.map(c -> toDto(c))
				.orElse(null);
	}

	@Override
	public PersonDto savePerson(PersonDto source) throws ValidationRuntimeException {
		Person person = personService.getByUuid(source.getUuid());
		PersonDto existingPerson = toDto(person);

		validate(source);

		person = fillOrBuildEntity(source, person);
		personService.ensurePersisted(person);

		onPersonChanged(existingPerson, person);

		return toDto(person);
	}

	@Override
	public void validate(PersonDto source) throws ValidationRuntimeException {
		if (StringUtils.isEmpty(source.getFirstName())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.specifyFirstName));
		}
		if (StringUtils.isEmpty(source.getLastName())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.specifyLastName));
		}
	}
	
	/**
	 * Makes sure that there is no invalid data associated with this person. For example, when the present condition
	 * is set to "Alive", all fields depending on the status being "Dead" or "Buried" are cleared.
	 */
	private void cleanUp(Person person) {
		if (person.getPresentCondition() == null || person.getPresentCondition() == PresentCondition.ALIVE) {
			person.setDeathDate(null);
			person.setCauseOfDeath(null);
			person.setCauseOfDeathDisease(null);
			person.setCauseOfDeathDetails(null);
			person.setDeathPlaceType(null);
			person.setDeathPlaceDescription(null);
		}
		if (!PresentCondition.BURIED.equals(person.getPresentCondition())) {
			person.setBurialDate(null);
			person.setBurialPlaceDescription(null);
			person.setBurialConductor(null);
		}
	}

	public void onPersonChanged(PersonDto existingPerson, Person newPerson) {
		List<Case> personCases = caseService.findBy(new CaseCriteria().person(new PersonReferenceDto(newPerson.getUuid())), null);
		// Call onCaseChanged once for every case to update case classification
		// Attention: this may lead to infinite recursion when not properly implemented
		for (Case personCase : personCases) {
			CaseDataDto existingCase = CaseFacadeEjbLocal.toDto(personCase);
			caseFacade.onCaseChanged(existingCase, personCase);
		}

		// Update cases if present condition has changed
		if (existingPerson != null) {
			if (newPerson.getPresentCondition() != null 
					&& existingPerson.getPresentCondition() != newPerson.getPresentCondition()) {
				// Update case list after previous onCaseChanged
				personCases = caseService.findBy(new CaseCriteria().person(new PersonReferenceDto(newPerson.getUuid())), null);
				for (Case personCase : personCases) {
					if (newPerson.getPresentCondition().isDeceased()) {
						if (personCase.getOutcome() == CaseOutcome.NO_OUTCOME) {
							CaseDataDto existingCase = CaseFacadeEjbLocal.toDto(personCase);
							personCase.setOutcome(CaseOutcome.DECEASED);
							personCase.setOutcomeDate(new Date());
							// Attention: this may lead to infinite recursion when not properly implemented
							caseFacade.onCaseChanged(existingCase, personCase);
						}
					} else if (personCase.getOutcome() == CaseOutcome.DECEASED) {
						CaseDataDto existingCase = CaseFacadeEjbLocal.toDto(personCase);
						personCase.setOutcome(CaseOutcome.NO_OUTCOME);
						personCase.setOutcomeDate(null);
						// Attention: this may lead to infinite recursion when not properly implemented
						caseFacade.onCaseChanged(existingCase, personCase);
					}
				}
			}
		}

		// Set approximate age if it hasn't been set before
		if (newPerson.getApproximateAge() == null && newPerson.getBirthdateYYYY() != null) {
			Pair<Integer, ApproximateAgeType> pair = ApproximateAgeHelper.getApproximateAge(
					newPerson.getBirthdateYYYY(), newPerson.getBirthdateMM(), newPerson.getBirthdateDD(), newPerson.getDeathDate()
					);
			newPerson.setApproximateAge(pair.getElement0());
			newPerson.setApproximateAgeType(pair.getElement1());
			newPerson.setApproximateAgeReferenceDate(newPerson.getDeathDate() != null ? newPerson.getDeathDate() : new Date());
		}

		// Update caseAge of all associated cases when approximateAge has changed
		if ((existingPerson == null && newPerson.getApproximateAge() != null) || 
				(existingPerson != null && existingPerson.getApproximateAge() != newPerson.getApproximateAge())) {
			// Update case list after previous onCaseChanged
			personCases = caseService.findBy(new CaseCriteria().person(new PersonReferenceDto(newPerson.getUuid())), null);
			for (Case personCase : personCases) {
				CaseDataDto existingCase = CaseFacadeEjbLocal.toDto(personCase);
				if (newPerson.getApproximateAge() == null) {
					personCase.setCaseAge(null);
				} else if (newPerson.getApproximateAgeType() == ApproximateAgeType.MONTHS) {
					personCase.setCaseAge(0);
				} else {
					Date now = new Date();
					personCase.setCaseAge(newPerson.getApproximateAge() - DateHelper.getYearsBetween(personCase.getReportDate(), now));
					if (personCase.getCaseAge() < 0) {
						personCase.setCaseAge(0);
					}
				}
				caseFacade.onCaseChanged(existingCase, personCase);
			}
		}
		
		cleanUp(newPerson);
	}

	@Override
	public PersonIndexDto getIndexDto(String uuid) {
		Person person = personService.getByUuid(uuid);
		return toIndexDto(person);
	}

	public Person fillOrBuildEntity(@NotNull PersonDto source, Person target) {

		if(target==null) {
			target = personService.createPerson();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		DtoHelper.validateDto(source, target);

		target.setFirstName(source.getFirstName());
		target.setLastName(source.getLastName());
		target.setSex(source.getSex());

		target.setPresentCondition(source.getPresentCondition());
		target.setBirthdateDD(source.getBirthdateDD());
		target.setBirthdateMM(source.getBirthdateMM());
		target.setBirthdateYYYY(source.getBirthdateYYYY());
		target.setApproximateAge(source.getApproximateAge());
		target.setApproximateAgeType(source.getApproximateAgeType());
		target.setApproximateAgeReferenceDate(source.getApproximateAgeReferenceDate());
		target.setCauseOfDeath(source.getCauseOfDeath());
		target.setCauseOfDeathDetails(source.getCauseOfDeathDetails());
		target.setCauseOfDeathDisease(source.getCauseOfDeathDisease());
		target.setDeathDate(source.getDeathDate());
		target.setDeathPlaceType(source.getDeathPlaceType());
		target.setDeathPlaceDescription(source.getDeathPlaceDescription());
		target.setBurialDate(source.getBurialDate());
		target.setBurialPlaceDescription(source.getBurialPlaceDescription());
		target.setBurialConductor(source.getBurialConductor());

		target.setNickname(source.getNickname());
		target.setMothersMaidenName(source.getMothersMaidenName());

		target.setPhone(source.getPhone());
		target.setPhoneOwner(source.getPhoneOwner());
		target.setAddress(locationFacade.fromDto(source.getAddress()));

		target.setEducationType(source.getEducationType());
		target.setEducationDetails(source.getEducationDetails());

		target.setOccupationType(source.getOccupationType());
		target.setOccupationDetails(source.getOccupationDetails());
		target.setOccupationRegion(regionService.getByReferenceDto(source.getOccupationRegion()));
		target.setOccupationDistrict(districtService.getByReferenceDto(source.getOccupationDistrict()));
		target.setOccupationCommunity(communityService.getByReferenceDto(source.getOccupationCommunity()));
		target.setOccupationFacility(facilityService.getByReferenceDto(source.getOccupationFacility()));
		target.setOccupationFacilityDetails(source.getOccupationFacilityDetails());

		target.setMothersName(source.getMothersName());
		target.setFathersName(source.getFathersName());
		target.setPlaceOfBirthRegion(regionService.getByReferenceDto(source.getPlaceOfBirthRegion()));
		target.setPlaceOfBirthDistrict(districtService.getByReferenceDto(source.getPlaceOfBirthDistrict()));
		target.setPlaceOfBirthCommunity(communityService.getByReferenceDto(source.getPlaceOfBirthCommunity()));
		target.setPlaceOfBirthFacility(facilityService.getByReferenceDto(source.getPlaceOfBirthFacility()));
		target.setPlaceOfBirthFacilityDetails(source.getPlaceOfBirthFacilityDetails());
		target.setGestationAgeAtBirth(source.getGestationAgeAtBirth());
		target.setBirthWeight(source.getBirthWeight());
		target.setGeneralPractitionerDetails(source.getGeneralPractitionerDetails());

		target.setEmailAddress(source.getEmailAddress());
		target.setPassportNumber(source.getPassportNumber());
		target.setNationalHealthId(source.getNationalHealthId());

		return target;
	}

	public static PersonReferenceDto toReferenceDto(Person entity) {
		if (entity == null) {
			return null;
		}
		PersonReferenceDto dto = new PersonReferenceDto(entity.getUuid(), entity.toString());
		return dto;
	}

	public static PersonIndexDto toIndexDto(Person entity) {
		PersonIndexDto dto = new PersonIndexDto(entity.getUuid(), entity.getSex(), entity.getFirstName(), entity.getLastName(), 
				entity.getPresentCondition(), entity.getBirthdateDD(), entity.getBirthdateMM(), entity.getBirthdateYYYY(),
				entity.getApproximateAge(), entity.getApproximateAgeType(), entity.getDeathDate(), entity.getNickname(),
				entity.getAddress().getRegion() != null ? entity.getAddress().getRegion().getName() : null,
						entity.getAddress().getDistrict() != null ? entity.getAddress().getDistrict().getName() : null,
								entity.getAddress().getCommunity() != null ? entity.getAddress().getCommunity().getName() : null,
										entity.getAddress().getCity());
		return dto;
	}

	public static PersonDto toDto(Person source) {
		if (source == null) {
			return null;
		}
		PersonDto target = new PersonDto();
		DtoHelper.fillDto(target, source);

		target.setFirstName(source.getFirstName());
		target.setLastName(source.getLastName());
		target.setSex(source.getSex());

		target.setPresentCondition(source.getPresentCondition());
		target.setBirthdateDD(source.getBirthdateDD());
		target.setBirthdateMM(source.getBirthdateMM());
		target.setBirthdateYYYY(source.getBirthdateYYYY());

		if (source.getBirthdateYYYY() != null) {

			// calculate the approximate age based on the birth date
			// still not sure whether this is a good solution

			Pair<Integer, ApproximateAgeType> pair = ApproximateAgeHelper.getApproximateAge(
					source.getBirthdateYYYY(), source.getBirthdateMM(), source.getBirthdateDD(), source.getDeathDate()
					);
			target.setApproximateAge(pair.getElement0());
			target.setApproximateAgeType(pair.getElement1());
			target.setApproximateAgeReferenceDate(source.getDeathDate() != null ? source.getDeathDate() : new Date());

		}
		else {
			target.setApproximateAge(source.getApproximateAge());
			target.setApproximateAgeType(source.getApproximateAgeType());
			target.setApproximateAgeReferenceDate(source.getApproximateAgeReferenceDate());
		}

		target.setCauseOfDeath(source.getCauseOfDeath());
		target.setCauseOfDeathDetails(source.getCauseOfDeathDetails());
		target.setCauseOfDeathDisease(source.getCauseOfDeathDisease());
		target.setDeathDate(source.getDeathDate());
		target.setDeathPlaceType(source.getDeathPlaceType());
		target.setDeathPlaceDescription(source.getDeathPlaceDescription());
		target.setBurialDate(source.getBurialDate());
		target.setBurialPlaceDescription(source.getBurialPlaceDescription());
		target.setBurialConductor(source.getBurialConductor());

		target.setNickname(source.getNickname());
		target.setMothersMaidenName(source.getMothersMaidenName());

		target.setPhone(source.getPhone());
		target.setPhoneOwner(source.getPhoneOwner());
		target.setAddress(LocationFacadeEjb.toDto(source.getAddress()));

		target.setEducationType(source.getEducationType());
		target.setEducationDetails(source.getEducationDetails());

		target.setOccupationType(source.getOccupationType());
		target.setOccupationDetails(source.getOccupationDetails());
		target.setOccupationRegion(RegionFacadeEjb.toReferenceDto(source.getOccupationRegion()));
		target.setOccupationDistrict(DistrictFacadeEjb.toReferenceDto(source.getOccupationDistrict()));
		target.setOccupationCommunity(CommunityFacadeEjb.toReferenceDto(source.getOccupationCommunity()));
		target.setOccupationFacility(FacilityFacadeEjb.toReferenceDto(source.getOccupationFacility()));
		target.setOccupationFacilityDetails(source.getOccupationFacilityDetails());

		target.setMothersName(source.getMothersName());
		target.setFathersName(source.getFathersName());
		target.setPlaceOfBirthRegion(RegionFacadeEjb.toReferenceDto(source.getPlaceOfBirthRegion()));
		target.setPlaceOfBirthDistrict(DistrictFacadeEjb.toReferenceDto(source.getPlaceOfBirthDistrict()));
		target.setPlaceOfBirthCommunity(CommunityFacadeEjb.toReferenceDto(source.getPlaceOfBirthCommunity()));
		target.setPlaceOfBirthFacility(FacilityFacadeEjb.toReferenceDto(source.getPlaceOfBirthFacility()));
		target.setPlaceOfBirthFacilityDetails(source.getPlaceOfBirthFacilityDetails());
		target.setGestationAgeAtBirth(source.getGestationAgeAtBirth());
		target.setBirthWeight(source.getBirthWeight());
		target.setGeneralPractitionerDetails(source.getGeneralPractitionerDetails());

		target.setEmailAddress(source.getEmailAddress());
		target.setPassportNumber(source.getPassportNumber());
		target.setNationalHealthId(source.getNationalHealthId());

		return target;
	}

	@LocalBean
	@Stateless
	public static class PersonFacadeEjbLocal extends PersonFacadeEjb {

	}

}
