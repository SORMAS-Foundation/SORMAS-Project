package de.symeda.sormas.backend.person;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.CasePersonDto;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.utils.DataHelper.Pair;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.location.LocationFacadeEjb;
import de.symeda.sormas.backend.location.LocationFacadeEjb.LocationFacadeEjbLocal;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "PersonFacade")
public class PersonFacadeEjb implements PersonFacade {
	
	@EJB
	private PersonService personService;
	@EJB
	private FacilityService facilityService;
	@EJB
	private LocationFacadeEjbLocal locationFacade;
	
	@Override
	public List<PersonReferenceDto> getAllPersons() {

		return personService.getAll().stream()
			.map(c -> toReferenceDto(c))
			.collect(Collectors.toList());
	}
	
	@Override
	public List<PersonReferenceDto> getAllPersonsAfter(Date date) {
		return personService.getAllAfter(date).stream()
			.map(c -> toReferenceDto(c))
			.collect(Collectors.toList());
	}
	
	@Override
	public List<CasePersonDto> getAllCasePersonsAfter(Date date) {
		List<CasePersonDto> result = personService.getAllAfter(date).stream()
			.map(c -> toCasePersonDto(c))
			.collect(Collectors.toList());
		return result;
	}

	@Override
	public List<PersonReferenceDto> getAllNoCasePersons() {

		return personService.getAllNoCase().stream()
				.map(c -> toReferenceDto(c))
				.collect(Collectors.toList());
	}

	@Override
	public PersonReferenceDto getByUuid(String uuid) {
		return Optional.of(uuid)
				.map(u -> personService.getByUuid(u))
				.map(c -> toReferenceDto(c))
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
	public PersonReferenceDto savePerson(PersonReferenceDto dto) {
		Person person = toPerson(dto);
		personService.ensurePersisted(person);
		
		return toReferenceDto(person);
		
	}
	
	@Override
	public CasePersonDto savePerson(CasePersonDto dto) {
		Person person = fromCasePersonDto(dto);
		personService.ensurePersisted(person);
		
		return toCasePersonDto(person);
		
	}
	
	public Person toPerson(@NotNull PersonReferenceDto dto) {
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
			bo.setUuid(dto.getUuid());
			if (dto.getCreationDate() != null) {
				bo.setCreationDate(new Timestamp(dto.getCreationDate().getTime()));
			}
		}
		
		// case uuid is ignored!
		
		bo.setFirstName(dto.getFirstName());
		bo.setLastName(dto.getLastName());
		bo.setSex(dto.getSex());
		
		bo.setPresentCondition(dto.getPresentCondition());
		bo.setBirthdateDD(dto.getBirthdateDD());
		bo.setBirthdateMM(dto.getBirthdateMM());
		bo.setBirthdateYYYY(dto.getBirthdateYYYY());
		bo.setApproximateAge(dto.getApproximateAge());
		bo.setApproximateAgeType(dto.getApproximateAgeType());
		bo.setDeathDate(dto.getDeathDate());
		bo.setDead(dto.getDeathDate()!=null);
		
		bo.setPhone(dto.getPhone());
		bo.setPhoneOwner(dto.getPhoneOwner());
		bo.setAddress(locationFacade.fromLocationDto(dto.getAddress()));
		
		bo.setOccupationType(dto.getOccupationType());
		bo.setOccupationDetails(dto.getOccupationDetails());
		bo.setOccupationFacility(facilityService.getByReferenceDto(dto.getOccupationFacility()));
		return bo;
	}
	
	public static PersonReferenceDto toReferenceDto(Person entity) {
		PersonReferenceDto dto = new PersonReferenceDto();
		DtoHelper.fillReferenceDto(dto, entity);

		dto.setFirstName(entity.getFirstName());
		dto.setLastName(entity.getLastName());
		if (entity.getCaze() != null) {
			dto.setCaseUuid(entity.getCaze().getUuid());
		}
		return dto;
	}
	
	public static CasePersonDto toCasePersonDto(Person entity) {
		CasePersonDto dto = new CasePersonDto();
		DtoHelper.fillReferenceDto(dto, entity);
		
		dto.setFirstName(entity.getFirstName());
		dto.setLastName(entity.getLastName());
		dto.setSex(entity.getSex());
		if (entity.getCaze() != null) {
			dto.setCaseUuid(entity.getCaze().getUuid());
		}
		
		dto.setPresentCondition(entity.getPresentCondition());
		dto.setBirthdateDD(entity.getBirthdateDD());
		dto.setBirthdateMM(entity.getBirthdateMM());
		dto.setBirthdateYYYY(entity.getBirthdateYYYY());
		dto.setDeathDate(entity.getDeathDate());
		
		if (entity.getBirthdateYYYY() != null) {
			Calendar birthdate = new GregorianCalendar();
			birthdate.set(entity.getBirthdateYYYY(), entity.getBirthdateMM()!=null?entity.getBirthdateMM()-1:0, entity.getBirthdateDD()!=null?entity.getBirthdateDD():1);
			
			Pair<Integer, ApproximateAgeType> pair = DateHelper.getApproximateAge(
					birthdate.getTime(),
					entity.getDeathDate()
					);
			dto.setApproximateAge(pair.getElement0());
			dto.setApproximateAgeType(pair.getElement1());
		}
		else {
			dto.setApproximateAge(entity.getApproximateAge());
			dto.setApproximateAgeType(entity.getApproximateAgeType());
		}
		
		dto.setPhone(entity.getPhone());
		dto.setPhoneOwner(entity.getPhoneOwner());
		dto.setAddress(LocationFacadeEjb.toLocationDto(entity.getAddress()));
		
		dto.setOccupationType(entity.getOccupationType());
		dto.setOccupationDetails(entity.getOccupationDetails());
		dto.setOccupationFacility(DtoHelper.toReferenceDto(entity.getOccupationFacility()));
		return dto;
	}

}
