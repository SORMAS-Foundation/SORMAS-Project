package de.symeda.sormas.backend.person;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.ApproximateAgeType.ApproximateAgeHelper;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.api.person.PersonIndexDto;
import de.symeda.sormas.api.person.PersonNameDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DataHelper.Pair;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.location.LocationFacadeEjb;
import de.symeda.sormas.backend.location.LocationFacadeEjb.LocationFacadeEjbLocal;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "PersonFacade")
public class PersonFacadeEjb implements PersonFacade {
	
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
	private LocationFacadeEjbLocal locationFacade;
	@EJB
	private UserService userService;

	
	@Override
	public List<String> getAllUuids(String userUuid) {
		
		User user = userService.getByUuid(userUuid);
		
		if (user == null) {
			return Collections.emptyList();
		}
		
		return personService.getAllUuids(user);
	}
	
	@Override
	public List<PersonNameDto> getNameDtos(UserReferenceDto userRef) {
		
		User user = userService.getByReferenceDto(userRef);
		if (user == null) {
			return Collections.emptyList();
		}

		return new ArrayList<PersonNameDto>(personService.getNameDtos(user));
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
	public List<PersonDto> getPersonsAfter(Date date, String uuid) {
		
		User user = userService.getByUuid(uuid);
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
	public List<PersonDto> getDeathsBetween(Date fromDate, Date toDate, DistrictReferenceDto districtRef, Disease disease, String uuid) {
		User user = userService.getByUuid(uuid);
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
	public PersonDto savePerson(PersonDto source) {
		Person person = personService.getByUuid(source.getUuid());
		PersonDto existingPerson = toDto(person);
		
		person = fillOrBuildEntity(source, person);
		personService.ensurePersisted(person);
		
		onPersonChanged(existingPerson, person);
		
		return toDto(person);
	}
	
	public void onPersonChanged(PersonDto existingPerson, Person newPerson) {

		if (existingPerson != null) {
			if (newPerson.getPresentCondition() != null 
					&& existingPerson.getPresentCondition() != newPerson.getPresentCondition()) {
				
				// present condition changed -> update cases
				
				// CHECK Should we better use only dtos here and use CaseFacade.saveCase?
				List<Case> personCases = caseService.findBy(new CaseCriteria().personEquals(new PersonReferenceDto(newPerson.getUuid())));

				for (Case personCase : personCases) {
					if (newPerson.getPresentCondition().isDeceased()) {
						if (personCase.getOutcome() == CaseOutcome.NO_OUTCOME) {
							CaseDataDto existingCase = CaseFacadeEjbLocal.toDto(personCase);
							personCase.setOutcome(CaseOutcome.DECEASED);
							personCase.setOutcomeDate(new Date());
							// attention: this may lead to infinite recursion when not properly implemented
							caseFacade.onCaseChanged(existingCase, personCase);
						}
					} else {
						if (personCase.getOutcome() == CaseOutcome.DECEASED) {
							CaseDataDto existingCase = CaseFacadeEjbLocal.toDto(personCase);
							personCase.setOutcome(CaseOutcome.NO_OUTCOME);
							personCase.setOutcomeDate(null);
							// attention: this may lead to infinite recursion when not properly implemented
							caseFacade.onCaseChanged(existingCase, personCase);
						}
					}
				}
			}
		}
	}
	
	@Override
	public PersonIndexDto getIndexDto(Long id) {
		Person person = personService.getById(id);
		return toIndexDto(person);
	}
	
	@Override
	public PersonDto buildPerson() {
		PersonDto person = new PersonDto();
		person.setUuid(DataHelper.createUuid());
		
		return person;
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
		
		target.setOccupationType(source.getOccupationType());
		target.setOccupationDetails(source.getOccupationDetails());
		target.setOccupationFacility(facilityService.getByReferenceDto(source.getOccupationFacility()));
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
			Calendar birthdate = new GregorianCalendar();
			birthdate.set(source.getBirthdateYYYY(), source.getBirthdateMM()!=null?source.getBirthdateMM()-1:0, source.getBirthdateDD()!=null?source.getBirthdateDD():1);
			
			Pair<Integer, ApproximateAgeType> pair = ApproximateAgeHelper.getApproximateAge(
					birthdate.getTime(),
					source.getDeathDate()
					);
			target.setApproximateAge(pair.getElement0());
			target.setApproximateAgeType(pair.getElement1());
		}
		else {
			target.setApproximateAge(source.getApproximateAge());
			target.setApproximateAgeType(source.getApproximateAgeType());
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
		
		target.setOccupationType(source.getOccupationType());
		target.setOccupationDetails(source.getOccupationDetails());
		target.setOccupationFacility(FacilityFacadeEjb.toReferenceDto(source.getOccupationFacility()));
		return target;
	}
	
	@LocalBean
	@Stateless
	public static class PersonFacadeEjbLocal extends PersonFacadeEjb {
	}

}
