package de.symeda.sormas.backend.person;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractAdoService;



@Stateless
@LocalBean
public class PersonService extends AbstractAdoService<Person> {

	public PersonService() {
		super(Person.class);
	}

	public Person createPerson() {

		Person person = new Person();
		return person;
	}
	
	public List<Person> getAllNoCase() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Person> cq = cb.createQuery(Person.class);
		Root<Person> from = cq.from(Person.class);
		Join<Person, Case> join = from.join(Person.CAZE, JoinType.LEFT);
		cq.where(cb.isNull(join.get(Case.ID)));
		cq.orderBy(cb.asc(from.get(Person.FIRST_NAME)), cb.asc(from.get(Person.LAST_NAME)));
		return em.createQuery(cq).getResultList();
	}
}
