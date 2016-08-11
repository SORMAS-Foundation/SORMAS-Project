package de.symeda.sormas.backend.person;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.CasePersonDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.api.utils.DataHelper.Pair;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.location.LocationFacadeEjb;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "PersonFacade")
public class PersonFacadeEjb implements PersonFacade {
	
	@EJB
	private PersonService personService;
	@EJB
	private FacilityService facilityService;
	@EJB
	private LocationFacadeEjb locationFacade;
	
	@Override
	public List<PersonDto> getAllPersons() {

		return personService.getAll().stream()
			.map(c -> toDto(c))
			.collect(Collectors.toList());
	}
	
	@Override
	public List<PersonDto> getAllPersonsAfter(Date date) {
		return personService.getAllAfter(date).stream()
			.map(c -> toDto(c))
			.collect(Collectors.toList());
	}
	
	@Override
	public List<CasePersonDto> getAllCasePersonsAfter(Date date) {
		return personService.getAllAfter(date).stream()
			.map(c -> toCasePersonDto(c))
			.collect(Collectors.toList());
	}

	@Override
	public List<ReferenceDto> getAllNoCaseAsReference() {

		return personService.getAllNoCase().stream()
				.map(c -> DtoHelper.toReferenceDto(c))
				.collect(Collectors.toList());
	}

	@Override
	public PersonDto getByUuid(String uuid) {
		return Optional.of(uuid)
				.map(u -> personService.getByUuid(u))
				.map(c -> toDto(c))
				.orElse(null);
	}
	
	@Override
	public CasePersonDto getCasePersonByUuid(String uuid) {
		return Optional.of(uuid)
				.map(u -> personService.getByUuid(u))
				.map(c -> toCasePersonDto(c))
				.orElse(null);
	}
	
	@Override
	public PersonDto savePerson(PersonDto dto) {
		Person person = toPerson(dto);
		personService.ensurePersisted(person);
		
		return toDto(person);
		
	}
	
	@Override
	public CasePersonDto savePerson(CasePersonDto dto) {
		Person person = fromCasePersonDto(dto);
		personService.ensurePersisted(person);
		
		return toCasePersonDto(person);
		
	}
	
	public Person toPerson(@NotNull PersonDto dto) {
		Person bo = personService.getByUuid(dto.getUuid());
		if(bo==null) {
			bo = personService.createPerson();
			if (dto.getCreationDate() != null) {
				bo.setCreationDate(new Timestamp(dto.getCreationDate().getTime()));
			}
		}
		bo.setUuid(dto.getUuid());
		bo.setFirstName(dto.getFirstName());
		bo.setLastName(dto.getLastName());
		return bo;
	}
	
	public Person fromCasePersonDto(@NotNull CasePersonDto dto) {
		Person bo = personService.getByUuid(dto.getUuid());
		if(bo==null) {
			bo = personService.createPerson();
			if (dto.getCreationDate() != null) {
				bo.setCreationDate(new Timestamp(dto.getCreationDate().getTime()));
			}
		}
		bo.setUuid(dto.getUuid());
		bo.setFirstName(dto.getFirstName());
		bo.setLastName(dto.getLastName());
		bo.setSex(dto.getSex());
		
		bo.setPresentCondition(dto.getPresentCondition());
		bo.setBirthDate(dto.getBirthDate());
		bo.setApproximateAge(dto.getApproximateAge());
		bo.setApproximateAgeType(dto.getApproximateAgeType());
		bo.setDeathDate(dto.getDeathDate());
		bo.setDead(dto.getDeathDate()!=null);
		
		bo.setPhone(dto.getPhone());
		bo.setPhoneOwner(dto.getPhoneOwner());
		bo.setAddress(locationFacade.fromLocationDto(dto.getAddress()));
		
		bo.setOccupationType(dto.getOccupationType());
		bo.setOccupationDetails(dto.getOccupationDetails());
		bo.setOccupationFacility(DtoHelper.fromReferenceDto(dto.getOccupationFacility(), facilityService));
		return bo;
	}
	
	public static PersonDto toDto(Person person) {
		PersonDto dto = new PersonDto();
		dto.setCreationDate(person.getCreationDate());
		dto.setChangeDate(person.getChangeDate());
		dto.setUuid(person.getUuid());
		dto.setFirstName(person.getFirstName());
		dto.setLastName(person.getLastName());
		if (person.getCaze() != null) {
			dto.setCaseUuid(person.getCaze().getUuid());
		}
		return dto;
	}
	
	public static CasePersonDto toCasePersonDto(Person person) {
		CasePersonDto dto = new CasePersonDto();
		dto.setCreationDate(person.getCreationDate());
		dto.setChangeDate(person.getChangeDate());
		dto.setUuid(person.getUuid());
		
		dto.setFirstName(person.getFirstName());
		dto.setLastName(person.getLastName());
		dto.setSex(person.getSex());
		if (person.getCaze() != null) {
			dto.setCaseUuid(person.getCaze().getUuid());
		}
		
		dto.setPresentCondition(person.getPresentCondition());
		dto.setBirthDate(person.getBirthDate());
		dto.setDeathDate(person.getDeathDate());
		if(person.getBirthDate()!=null) {
			Pair<Integer, ApproximateAgeType> pair = DateHelper.getApproximateAge(
					person.getBirthDate(),
					person.getDeathDate()
					);
			dto.setApproximateAge(pair.getElement0());
			dto.setApproximateAgeType(pair.getElement1());
		}
		else {
			dto.setApproximateAge(person.getApproximateAge());
			dto.setApproximateAgeType(person.getApproximateAgeType());
		}
		
		dto.setPhone(person.getPhone());
		dto.setPhoneOwner(person.getPhoneOwner());
		dto.setAddress(LocationFacadeEjb.toLocationDto(person.getAddress()));
		
		dto.setOccupationType(person.getOccupationType());
		dto.setOccupationDetails(person.getOccupationDetails());
		dto.setOccupationFacility(DtoHelper.toReferenceDto(person.getOccupationFacility()));
		return dto;
	}

}
