package de.symeda.sormas.backend.contact;

import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactIndexDetailedDto;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.ModelConstants;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Stateless
@LocalBean
public class ContactListCriteriaBuilder {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private ContactService contactService;

	public CriteriaQuery<ContactIndexDto> buildIndexCriteria(ContactCriteria contactCriteria, List<SortProperty> sortProperties) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ContactIndexDto> cq = cb.createQuery(ContactIndexDto.class);
		Root<Contact> contact = cq.from(Contact.class);

		ContactJoins joins = new ContactJoins(contact);

		cq.multiselect(getContactIndexSelections(contact, joins));

		Predicate filter = buildContactFilter(contactCriteria, cb, contact, cq);

		if (filter != null) {
			cq.where(filter);
		}

		if (sortProperties == null || sortProperties.size() == 0) {
			cq.orderBy(cb.desc(contact.get(Contact.CHANGE_DATE)));
		}
		else {
			applySortOrderingOrdering(sortProperties, cb, cq, contact, joins);
		}

		return cq;
	}

	private List<Selection<?>> getContactIndexSelections(Root<Contact> contact, ContactJoins joins) {
		return Arrays.asList(contact.get(Contact.UUID),
				joins.getContactPerson().get(Person.FIRST_NAME), joins.getContactPerson().get(Person.LAST_NAME),
				joins.getContactCase().get(Case.UUID), contact.get(Contact.DISEASE), contact.get(Contact.DISEASE_DETAILS),
				joins.getContactCasePerson().get(Person.FIRST_NAME), joins.getContactCasePerson().get(Person.LAST_NAME),
				joins.getContactCaseRegion().get(Region.UUID), joins.getContactCaseDistrict().get(District.UUID),
				contact.get(Contact.LAST_CONTACT_DATE),
				contact.get(Contact.CONTACT_CATEGORY), contact.get(Contact.CONTACT_PROXIMITY),
				contact.get(Contact.CONTACT_CLASSIFICATION), contact.get(Contact.CONTACT_STATUS),
				contact.get(Contact.FOLLOW_UP_STATUS), contact.get(Contact.FOLLOW_UP_UNTIL),
				joins.getContactOfficer().get(User.UUID), contact.get(Contact.REPORT_DATE_TIME),
				joins.getContactCase().get(Case.CASE_CLASSIFICATION));
	}

	private <T> void applySortOrderingOrdering(List<SortProperty> sortProperties, CriteriaBuilder cb, CriteriaQuery<T> cq, Root<Contact> contact, ContactJoins joins) {
		List<Order> order = new ArrayList<>(sortProperties.size());
		for (SortProperty sortProperty : sortProperties) {
			Expression<?> expression;
			switch (sortProperty.propertyName) {
				case ContactIndexDto.UUID:
				case ContactIndexDto.LAST_CONTACT_DATE:
				case ContactIndexDto.CONTACT_PROXIMITY:
				case ContactIndexDto.CONTACT_CATEGORY:
				case ContactIndexDto.CONTACT_CLASSIFICATION:
				case ContactIndexDto.CONTACT_STATUS:
				case ContactIndexDto.FOLLOW_UP_STATUS:
				case ContactIndexDto.FOLLOW_UP_UNTIL:
				case ContactIndexDto.REPORT_DATE_TIME:
				case ContactIndexDto.DISEASE:
				case ContactIndexDto.CASE_CLASSIFICATION:
					expression = contact.get(sortProperty.propertyName);
					break;
				case ContactIndexDto.PERSON_FIRST_NAME:
				case ContactIndexDto.PERSON_LAST_NAME:
					expression = joins.getContactPerson().get(sortProperty.propertyName);
					break;
				case ContactIndexDto.CAZE:
					expression = joins.getContactCasePerson().get(Person.FIRST_NAME);
					order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
					expression = joins.getContactCasePerson().get(Person.LAST_NAME);
					break;
				case ContactIndexDto.REGION_UUID:
					expression = joins.getContactCaseRegion().get(Region.NAME);
					break;
				case ContactIndexDto.DISTRICT_UUID:
					expression = joins.getContactCaseDistrict().get(District.NAME);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
			}
			order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
		}
		cq.orderBy(order);
	}

	public CriteriaQuery<ContactIndexDetailedDto> buildIndexDetaildCriteria(ContactCriteria contactCriteria, List<SortProperty> sortProperties) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ContactIndexDetailedDto> cq = cb.createQuery(ContactIndexDetailedDto.class);
		Root<Contact> contact = cq.from(Contact.class);

		ContactJoins joins = new ContactJoins(contact);

		cq.multiselect(getContactIndexDetailedSelections(contact, joins));

		Predicate filter = buildContactFilter(contactCriteria, cb, contact, cq);

		if (filter != null) {
			cq.where(filter);
		}

		if (sortProperties == null || sortProperties.size() == 0) {
			cq.orderBy(cb.desc(contact.get(Contact.CHANGE_DATE)));
		}
		else {
			applySortOrderingOrdering(sortProperties, cb, cq, contact, joins);
		}

		return cq;
	}

	private List<Selection<?>> getContactIndexDetailedSelections(Root<Contact> contact, ContactJoins joins) {
		final List<Selection<?>> indexSelection = new ArrayList<>(getContactIndexSelections(contact, joins));
		indexSelection.addAll(Arrays.asList(
				joins.getContactPerson().get(Person.SEX), joins.getContactPerson().get(Person.APPROXIMATE_AGE), joins.getContactPerson().get(Person.APPROXIMATE_AGE_TYPE),
				joins.getAddressDistrict().get(District.NAME),
				joins.getAddress().get(Location.CITY), joins.getAddress().get(Location.ADDRESS), joins.getAddress().get(Location.POSTAL_CODE),
				joins.getContactPerson().get(Person.PHONE),
				joins.getReportingUser().get(User.UUID), joins.getReportingUser().get(User.FIRST_NAME), joins.getReportingUser().get(User.LAST_NAME)
		));

		return indexSelection;
	}

	public Predicate buildContactFilter(ContactCriteria contactCriteria, CriteriaBuilder cb, Root<Contact> contact,
										CriteriaQuery<?> query) {
		Predicate filter = null;

		// Only use user filter if no restricting case is specified
		if (contactCriteria == null || contactCriteria.getCaze() == null) {
			filter = contactService.createUserFilter(cb, query, contact);
		}

		if (contactCriteria != null) {
			Predicate criteriaFilter = contactService.buildCriteriaFilter(contactCriteria, cb, contact);
			filter = AbstractAdoService.and(cb, filter, criteriaFilter);
		}
		return filter;
	}

	private static class ContactJoins {
		private Root<Contact> contact;

		private Join<Contact, Person> contactPerson;

		Join<Contact, Case> contactCase;
		Join<Case, Person> contactCasePerson;
		Join<Case, Region> contactCaseRegion;
		Join<Case, District> contactCaseDistrict;
		Join<Contact, User> contactOfficer;
		Join<Person, Location> address;
		Join<Location, District> addressDistrict;
		Join<Contact, User> reportingUser;

		public ContactJoins(Root<Contact> contact) {
			this.contact = contact;
		}

		public Join<Contact, Person> getContactPerson() {
			return getOrCreate(contactPerson, Contact.PERSON, JoinType.LEFT);
		}

		public Join<Contact, Case> getContactCase() {
			return getOrCreate(contactCase, Contact.CAZE, JoinType.LEFT);
		}

		public Join<Case, Person> getContactCasePerson() {
			return getOrCreate(contactCasePerson, Case.PERSON, JoinType.LEFT, getContactCase());
		}

		public Join<Case, Region> getContactCaseRegion() {
			return getOrCreate(contactCaseRegion, Case.REGION, JoinType.LEFT, getContactCase());
		}

		public Join<Case, District> getContactCaseDistrict() {
			return getOrCreate(contactCaseDistrict, Case.DISTRICT, JoinType.LEFT, getContactCase());
		}

		public Join<Contact, User> getContactOfficer() {
			return getOrCreate(contactOfficer, Contact.CONTACT_OFFICER, JoinType.LEFT);
		}

		public Join<Person, Location> getAddress() {
			return getOrCreate(address, Person.ADDRESS, JoinType.LEFT, getContactPerson());
		}

		public Join<Location, District> getAddressDistrict() {
			return getOrCreate(addressDistrict, Location.DISTRICT, JoinType.LEFT, getAddress());
		}

		public Join<Contact, User> getReportingUser() {
			return getOrCreate(reportingUser, Contact.REPORTING_USER, JoinType.LEFT);
		}

		private <T> Join<Contact, T> getOrCreate(Join<Contact, T> join, String attribute, JoinType joinType) {
			return getOrCreate(join, attribute, joinType, contact);
		}

		private <P, T> Join<P, T> getOrCreate(Join<P, T> join, String attribute, JoinType joinType, From<?, P> parent) {
			if (join != null) {
				return join;
			}

			return join = parent.join(attribute, joinType);
		}
	}
}
