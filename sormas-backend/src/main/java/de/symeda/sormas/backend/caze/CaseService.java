package de.symeda.sormas.backend.caze;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.caze.CaseDto;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonService;



@Stateless
@LocalBean
//@RolesAllowed({
//		Permission._ADMIN,
//		Permission._USER })
//@DeclareRoles({
//		Permission._ADMIN,
//		Permission._USER })
public class CaseService extends AbstractAdoService<Case> {
	
	@EJB
	private PersonService ps;

	public CaseService() {
		super(Case.class);
	}

	public Case createCase(Person person) {
		
		Case caze = new Case();
		caze.setPerson(person);
		return caze;
	}
	
	public Case toCase(@NotNull CaseDto dto) {
		Case caze = getByUuid(dto.getUuid());
		
		Person person;
		if(dto.getPerson()!=null) {
			person = ps.toPerson(dto.getPerson());
		}
		else {
			person = ps.createPerson();
		}
		if(caze==null) {
			caze = createCase(person);
		}
		caze.setUuid(dto.getUuid());
		caze.setPerson(person);
		caze.setDescription(dto.getDescription());
		caze.setCaseStatus(dto.getCaseStatus());
		return caze;
	}
}
