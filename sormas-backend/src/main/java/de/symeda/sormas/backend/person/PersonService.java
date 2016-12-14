package de.symeda.sormas.backend.person;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.region.Region;



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

	/**
	 * All persons with an address in the region
	 */
	public List<Person> getPersonsByRegion(Region region) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Person> cq = cb.createQuery(Person.class);
		Root<Person> from = cq.from(Person.class);
		Join<Person, Location> join = from.join(Person.ADDRESS, JoinType.LEFT);
		cq.where(cb.equal(join.get(Location.REGION), region));
		cq.orderBy(cb.asc(from.get(Person.FIRST_NAME)), cb.asc(from.get(Person.LAST_NAME)));
		return em.createQuery(cq).getResultList();
	}
}
