package de.symeda.sormas.backend.person;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.ApproximateAgeType.ApproximateAgeHelper;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.api.person.PersonIndexDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper.Pair;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.location.LocationFacadeEjb;
import de.symeda.sormas.backend.location.LocationFacadeEjb.LocationFacadeEjbLocal;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "PersonFacade")
public class PersonFacadeEjb implements PersonFacade {
	
	@EJB
	private PersonService service;
	@EJB
	private FacilityService facilityService;
	@EJB
	private RegionService regionService;
	@EJB
	private LocationFacadeEjbLocal locationFacade;
	@EJB
	private UserService userService;

	
	@Override
	public List<PersonReferenceDto> getAllPersons(UserReferenceDto userRef) {

		User user = userService.getByReferenceDto(userRef);
		if (user == null) {
			return Collections.emptyList();
		}

		return service.getAllAfter(null, user).stream()
			.map(c -> toReferenceDto(c))
			.collect(Collectors.toList());
	}
	
	@Override
	public List<PersonIndexDto> getIndexList(UserReferenceDto userRef) {
		
		User user = userService.getByReferenceDto(userRef);
		if (user == null) {
			return Collections.emptyList();
		}

		return service.getAllAfter(null, user).stream()
			.map(c -> toIndexDto(c))
			.collect(Collectors.toList());
	}
	
	@Override
	public List<PersonReferenceDto> getPersonReferencesAfter(Date date, UserReferenceDto userRef) {
		
		User user = userService.getByReferenceDto(userRef);
		if (user == null) {
			return Collections.emptyList();
		}
		
		return service.getAllAfter(date, user).stream()
			.map(c -> toReferenceDto(c))
			.collect(Collectors.toList());
	}
	
	@Override
	public List<PersonDto> getPersonsAfter(Date date, String uuid) {
		
		User user = userService.getByUuid(uuid);
		if (user == null) {
			return Collections.emptyList();
		}
		
		List<PersonDto> result = service.getAllAfter(date, user).stream()
			.map(c -> toDto(c))
			.collect(Collectors.toList());
		return result;
	}
	
	@Override
	public List<PersonDto> getDeathsBetween(Date fromDate, Date toDate, Disease disease, String uuid) {
		User user = userService.getByUuid(uuid);
		if (user == null) {
			return Collections.emptyList();
		}
		
		List<PersonDto> result = service.getDeathsBetween(fromDate, toDate, disease, user).stream()
				.map(c -> toDto(c))
				.collect(Collectors.toList());
		return result;
	}

	@Override
	public PersonReferenceDto getReferenceByUuid(String uuid) {
		return Optional.of(uuid)
				.map(u -> service.getByUuid(u))
				.map(c -> toReferenceDto(c))
				.orElse(null);
	}
	
	@Override
	public PersonDto getPersonByUuid(String uuid) {
		return Optional.of(uuid)
				.map(u -> service.getByUuid(u))
				.map(c -> toDto(c))
				.orElse(null);
	}
	
	@Override
	public PersonReferenceDto savePerson(PersonReferenceDto dto) {
		Person person = fromDto(dto);
		service.ensurePersisted(person);
		return toReferenceDto(person);
	}
	
	@Override
	public PersonDto savePerson(PersonDto dto) {
		Person person = fromDto(dto);
		service.ensurePersisted(person);
		
		return toDto(person);
		
	}
	
	public Person fromDto(@NotNull PersonReferenceDto source) {
		
		Person target = service.getByUuid(source.getUuid());
		if(target==null) {
			target = service.createPerson();
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		DtoHelper.validateDto(source, target);
		
		target.setUuid(source.getUuid());
		target.setFirstName(source.getFirstName());
		target.setLastName(source.getLastName());
		return target;
	}
	
	public Person fromDto(@NotNull PersonDto source) {
		
		Person target = service.getByUuid(source.getUuid());
		if(target==null) {
			target = service.createPerson();
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
		target.setDeathDate(source.getDeathDate());
		target.setDead(source.getDeathDate()!=null);
		
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
		PersonReferenceDto dto = new PersonReferenceDto();
		DtoHelper.fillReferenceDto(dto, entity);

		dto.setFirstName(entity.getFirstName());
		dto.setLastName(entity.getLastName());
		
		return dto;
	}
	
	public static PersonIndexDto toIndexDto(Person entity) {
		PersonIndexDto dto = new PersonIndexDto();
		DtoHelper.fillReferenceDto(dto, entity);

		dto.setFirstName(entity.getFirstName());
		dto.setLastName(entity.getLastName());
		dto.setSex(entity.getSex());
		dto.setPresentCondition(entity.getPresentCondition());
		
		if (entity.getBirthdateYYYY() != null) {
			Calendar birthdate = new GregorianCalendar();
			birthdate.set(entity.getBirthdateYYYY(), entity.getBirthdateMM()!=null?entity.getBirthdateMM()-1:0, entity.getBirthdateDD()!=null?entity.getBirthdateDD():1);
			
			Pair<Integer, ApproximateAgeType> pair = ApproximateAgeHelper.getApproximateAge(
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

		return dto;
	}
	
	public static PersonDto toDto(Person entity) {
		PersonDto dto = new PersonDto();
		DtoHelper.fillReferenceDto(dto, entity);
		
		dto.setFirstName(entity.getFirstName());
		dto.setLastName(entity.getLastName());
		dto.setSex(entity.getSex());
		
		dto.setPresentCondition(entity.getPresentCondition());
		dto.setBirthdateDD(entity.getBirthdateDD());
		dto.setBirthdateMM(entity.getBirthdateMM());
		dto.setBirthdateYYYY(entity.getBirthdateYYYY());
		dto.setDeathDate(entity.getDeathDate());
		
		if (entity.getBirthdateYYYY() != null) {
			Calendar birthdate = new GregorianCalendar();
			birthdate.set(entity.getBirthdateYYYY(), entity.getBirthdateMM()!=null?entity.getBirthdateMM()-1:0, entity.getBirthdateDD()!=null?entity.getBirthdateDD():1);
			
			Pair<Integer, ApproximateAgeType> pair = ApproximateAgeHelper.getApproximateAge(
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
		
		dto.setNickname(entity.getNickname());
		dto.setMothersMaidenName(entity.getMothersMaidenName());
		
		dto.setPhone(entity.getPhone());
		dto.setPhoneOwner(entity.getPhoneOwner());
		dto.setAddress(LocationFacadeEjb.toDto(entity.getAddress()));
		
		dto.setOccupationType(entity.getOccupationType());
		dto.setOccupationDetails(entity.getOccupationDetails());
		dto.setOccupationFacility(FacilityFacadeEjb.toReferenceDto(entity.getOccupationFacility()));
		return dto;
	}

}
