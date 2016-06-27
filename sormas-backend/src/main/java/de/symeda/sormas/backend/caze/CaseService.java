package de.symeda.sormas.backend.caze;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.person.Person;



@Stateless
@LocalBean
public class CaseService extends AbstractAdoService<Case> {
	
	public CaseService() {
		super(Case.class);
	}

	public Case createCase(Person person) {
		
		Case caze = new Case();
		caze.setPerson(person);
		return caze;
	}
}
