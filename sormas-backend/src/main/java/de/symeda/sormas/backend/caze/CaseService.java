package de.symeda.sormas.backend.caze;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

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
}
