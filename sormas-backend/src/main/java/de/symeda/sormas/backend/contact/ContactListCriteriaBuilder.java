package de.symeda.sormas.backend.contact;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactIndexDetailedDto;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.ContactJurisdictionDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseQueryContext;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.infrastructure.PointOfEntry;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless
@LocalBean
public class ContactListCriteriaBuilder {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private ContactService contactService;

	public CriteriaQuery<ContactIndexDto> buildIndexCriteria(ContactCriteria contactCriteria, List<SortProperty> sortProperties) {
		return buildIndexCriteria(ContactIndexDto.class, this::getContactIndexSelections, contactCriteria, this::getIndexOrders, sortProperties);
	}

	public CriteriaQuery<ContactIndexDetailedDto> buildIndexDetailedCriteria(ContactCriteria contactCriteria, List<SortProperty> sortProperties) {

		return buildIndexCriteria(
			ContactIndexDetailedDto.class,
			this::getContactIndexDetailedSelections,
			contactCriteria,
			this::getIndexDetailOrders,
			sortProperties);
	}

	public Stream<Selection<?>> getJurisdictionSelections(ContactJoins joins) {

		return Stream.of(
			joins.getReportingUser().get(User.UUID),
			joins.getRegion().get(Region.UUID),
			joins.getDistrict().get(District.UUID),
			joins.getCommunity().get(Community.UUID),
			joins.getCaseReportingUser().get(User.UUID),
			joins.getCaseRegion().get(Region.UUID),
			joins.getCaseDistrict().get(District.UUID),
			joins.getCaseCommunity().get(Community.UUID),
			joins.getCaseHealthFacility().get(Facility.UUID),
			joins.getCaseasePointOfEntry().get(PointOfEntry.UUID));
	}

	private List<Selection<?>> getContactIndexSelections(Root<Contact> contact, ContactQueryContext contactQueryContext) {

		ContactJoins joins = (ContactJoins) contactQueryContext.getJoins();

		return Arrays.asList(
			contact.get(Contact.UUID),
			joins.getPerson().get(Person.FIRST_NAME),
			joins.getPerson().get(Person.LAST_NAME),
			joins.getCaze().get(Case.UUID),
			contact.get(Contact.DISEASE),
			contact.get(Contact.DISEASE_DETAILS),
			joins.getCasePerson().get(Person.FIRST_NAME),
			joins.getCasePerson().get(Person.LAST_NAME),
			joins.getRegion().get(Region.UUID),
			joins.getRegion().get(Region.NAME),
			joins.getDistrict().get(District.UUID),
			joins.getDistrict().get(District.NAME),
			joins.getCommunity().get(Community.UUID),
			contact.get(Contact.LAST_CONTACT_DATE),
			contact.get(Contact.CONTACT_CATEGORY),
			contact.get(Contact.CONTACT_PROXIMITY),
			contact.get(Contact.CONTACT_CLASSIFICATION),
			contact.get(Contact.CONTACT_STATUS),
			contact.get(Contact.FOLLOW_UP_STATUS),
			contact.get(Contact.FOLLOW_UP_UNTIL),
			joins.getPerson().get(Person.SYMPTOM_JOURNAL_STATUS),
			joins.getContactOfficer().get(User.UUID),
			joins.getReportingUser().get(User.UUID),
			contact.get(Contact.REPORT_DATE_TIME),
			joins.getCaze().get(Case.CASE_CLASSIFICATION),
			joins.getCaseReportingUser().get(User.UUID),
			joins.getCaseRegion().get(Region.UUID),
			joins.getCaseRegion().get(Region.NAME),
			joins.getCaseDistrict().get(District.UUID),
			joins.getCaseDistrict().get(District.NAME),
			joins.getCaseCommunity().get(Community.UUID),
			joins.getCaseHealthFacility().get(Facility.UUID),
			joins.getCaseasePointOfEntry().get(PointOfEntry.UUID),
			contact.get(Contact.CHANGE_DATE),
			contact.get(Contact.EXTERNAL_ID),
			contact.get(Contact.EXTERNAL_TOKEN));
	}

	private List<Expression<?>> getIndexOrders(SortProperty sortProperty, Root<Contact> contact, ContactJoins joins) {

		List<Expression<?>> expressions = new ArrayList<>();
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
		case ContactIndexDto.EXTERNAL_ID:
		case ContactIndexDto.EXTERNAL_TOKEN:
			expressions.add(contact.get(sortProperty.propertyName));
			break;
		case ContactIndexDto.PERSON_FIRST_NAME:
		case ContactIndexDto.PERSON_LAST_NAME:
		case ContactIndexDto.SYMPTOM_JOURNAL_STATUS:
			expressions.add(joins.getPerson().get(sortProperty.propertyName));
			break;
		case ContactIndexDto.CAZE:
			expressions.add(joins.getCasePerson().get(Person.FIRST_NAME));
			expressions.add(joins.getCasePerson().get(Person.LAST_NAME));
			break;
		case ContactJurisdictionDto.REGION_UUID:
			expressions.add(joins.getRegion().get(Region.NAME));
			break;
		case ContactJurisdictionDto.DISTRICT_UUID:
			expressions.add(joins.getDistrict().get(District.NAME));
			break;
		default:
			throw new IllegalArgumentException(sortProperty.propertyName);
		}

		return expressions;
	}

	private List<Selection<?>> getContactIndexDetailedSelections(Root<Contact> contact, ContactQueryContext contactQueryContext) {

		final ContactJoins joins = (ContactJoins) contactQueryContext.getJoins();
		final List<Selection<?>> indexSelection = new ArrayList<>(getContactIndexSelections(contact, contactQueryContext));
		List<Selection<?>> selections = Arrays.asList(
				joins.getPerson().get(Person.SEX),
				joins.getPerson().get(Person.APPROXIMATE_AGE),
				joins.getPerson().get(Person.APPROXIMATE_AGE_TYPE),
				joins.getAddress().get(Location.CITY),
				joins.getAddress().get(Location.STREET),
				joins.getAddress().get(Location.HOUSE_NUMBER),
				joins.getAddress().get(Location.ADDITIONAL_INFORMATION),
				joins.getAddress().get(Location.POSTAL_CODE),
				((Selection<String>) contactQueryContext.getSubqueryExpression(CaseQueryContext.PERSON_PHONE_SUBQUERY)),
				joins.getReportingUser().get(User.FIRST_NAME),
				joins.getReportingUser().get(User.LAST_NAME));
		indexSelection.addAll(selections);

		return indexSelection;
	}

	private List<Expression<?>> getIndexDetailOrders(SortProperty sortProperty, Root<Contact> contact, ContactJoins joins) {

		switch (sortProperty.propertyName) {
		case ContactIndexDetailedDto.SEX:
		case ContactIndexDetailedDto.APPROXIMATE_AGE:
		case ContactIndexDetailedDto.PHONE:
			return Collections.singletonList(joins.getPerson().get(sortProperty.propertyName));
		case ContactIndexDetailedDto.DISTRICT_NAME:
			return Collections.singletonList(joins.getDistrict().get(District.NAME));
		case ContactIndexDetailedDto.CITY:
		case ContactIndexDetailedDto.STREET:
		case ContactIndexDetailedDto.HOUSE_NUMBER:
		case ContactIndexDetailedDto.ADDITIONAL_INFORMATION:
		case ContactIndexDetailedDto.POSTAL_CODE:
			return Collections.singletonList(joins.getAddress().get(sortProperty.propertyName));
		case ContactIndexDetailedDto.REPORTING_USER:
			return Arrays.asList(joins.getReportingUser().get(User.FIRST_NAME), joins.getReportingUser().get(User.LAST_NAME));
		default:
			return this.getIndexOrders(sortProperty, contact, joins);
		}
	}

	private <T> CriteriaQuery<T> buildIndexCriteria(
		Class<T> type,
		BiFunction<Root<Contact>, ContactQueryContext, List<Selection<?>>> selectionProvider,
		ContactCriteria contactCriteria,
		OrderExpressionProvider orderExpressionProvider,
		List<SortProperty> sortProperties) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<T> cq = cb.createQuery(type);
		final Root<Contact> contact = cq.from(Contact.class);
		final ContactQueryContext contactQueryContext = new ContactQueryContext(cb, cq, contact);
		final ContactJoins joins = new ContactJoins(contact);

		List<Selection<?>> selections = new ArrayList<>(selectionProvider.apply(contact, contactQueryContext));
		selections.add(cb.size(contact.get(Contact.VISITS)));

		Predicate filter = buildContactFilter(contactCriteria,contactQueryContext);

		if (filter != null) {
			cq.where(filter);
		}

		cq.multiselect(selections);
		cq.distinct(true);

		if (sortProperties == null || sortProperties.size() == 0) {
			cq.orderBy(cb.desc(contact.get(Contact.CHANGE_DATE)));
		} else {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				order.addAll(
					orderExpressionProvider.forProperty(sortProperty, contact, joins)
						.stream()
						.map(e -> sortProperty.ascending ? cb.asc(e) : cb.desc(e))
						.collect(Collectors.toList()));
			}
			cq.orderBy(order);
		}

		return cq;
	}

	public Predicate buildContactFilter(
		ContactCriteria contactCriteria,
		ContactQueryContext contactQueryContext) {

		Predicate filter = null;

		// Only use user filter if no restricting case is specified
		if (contactCriteria == null || contactCriteria.getCaze() == null) {
			filter = contactService.createUserFilterForJoin(contactQueryContext, contactCriteria);
		}

		if (contactCriteria != null) {
			Predicate criteriaFilter = contactService.buildCriteriaFilter(contactCriteria, contactQueryContext);
			filter = CriteriaBuilderHelper.and(contactQueryContext.getCriteriaBuilder(), filter, criteriaFilter);
		}
		return filter;
	}

	private interface OrderExpressionProvider {

		List<Expression<?>> forProperty(SortProperty sortProperty, Root<Contact> contact, ContactJoins joins);
	}
}
