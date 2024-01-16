package de.symeda.sormas.backend.contact;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.collections4.CollectionUtils;

import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactIndexDetailedDto;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.person.PersonContactDetailType;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.ExtendedPostgreSQL94Dialect;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseQueryContext;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonContactDetail;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.JurisdictionHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless
@LocalBean
public class ContactListCriteriaBuilder {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private CaseService caseService;

	@EJB
	private ContactService contactService;

	public CriteriaQuery<Tuple> buildIndexCriteria(ContactCriteria contactCriteria, List<SortProperty> sortProperties, List<Long> ids) {
		return buildIndexCriteria(Tuple.class, this::getContactIndexSelections, contactCriteria, this::getIndexOrders, sortProperties, ids);
	}

	public CriteriaQuery<Tuple> buildIndexDetailedCriteria(ContactCriteria contactCriteria, List<SortProperty> sortProperties, List<Long> ids) {

		return buildIndexCriteria(
			Tuple.class,
			this::getContactIndexDetailedSelections,
			contactCriteria,
			this::getIndexDetailOrders,
			sortProperties,
			ids);
	}

	public CriteriaQuery<Tuple> buildIndexCriteriaPrefetchIds(ContactCriteria contactCriteria, List<SortProperty> sortProperties) {
		return buildIndexCriteria(Tuple.class, this::getContactIndexSelections, contactCriteria, this::getIndexOrders, sortProperties, null);
	}

	public CriteriaQuery<Tuple> buildIndexDetailedCriteriaPrefetchIds(ContactCriteria contactCriteria, List<SortProperty> sortProperties) {

		return buildIndexCriteria(
			Tuple.class,
			this::getContactIndexDetailedSelections,
			contactCriteria,
			this::getIndexDetailOrders,
			sortProperties,
			null);
	}

	public List<Selection<?>> getContactIndexSelections(Root<Contact> contact, ContactQueryContext contactQueryContext) {

		ContactJoins joins = contactQueryContext.getJoins();
		CriteriaBuilder cb = contactQueryContext.getCriteriaBuilder();

		return Arrays.asList(
			contact.get(Contact.UUID),
			joins.getPerson().get(Person.UUID),
			joins.getPerson().get(Person.FIRST_NAME),
			joins.getPerson().get(Person.LAST_NAME),
			joins.getCaze().get(Case.UUID),
			contact.get(Contact.DISEASE),
			contact.get(Contact.DISEASE_DETAILS),
			joins.getCasePerson().get(Person.FIRST_NAME),
			joins.getCasePerson().get(Person.LAST_NAME),
			joins.getRegion().get(Region.NAME),
			joins.getDistrict().get(District.NAME),
			contact.get(Contact.LAST_CONTACT_DATE),
			contact.get(Contact.CONTACT_CATEGORY),
			contact.get(Contact.CONTACT_PROXIMITY),
			contact.get(Contact.CONTACT_CLASSIFICATION),
			contact.get(Contact.CONTACT_STATUS),
			contact.get(Contact.COMPLETENESS),
			contact.get(Contact.FOLLOW_UP_STATUS),
			contact.get(Contact.FOLLOW_UP_UNTIL),
			joins.getPerson().get(Person.SYMPTOM_JOURNAL_STATUS),
			contact.get(Contact.VACCINATION_STATUS),
			joins.getContactOfficer().get(User.UUID),
			joins.getReportingUser().get(User.UUID),
			contact.get(Contact.REPORT_DATE_TIME),
			joins.getCaze().get(Case.CASE_CLASSIFICATION),
			joins.getCaseRegion().get(Region.NAME),
			joins.getCaseDistrict().get(District.NAME),
			contact.get(Contact.CHANGE_DATE),
			contact.get(Contact.EXTERNAL_ID),
			contact.get(Contact.EXTERNAL_TOKEN),
			contact.get(Contact.INTERNAL_TOKEN),
			contact.get(Contact.DELETION_REASON),
			contact.get(Contact.OTHER_DELETION_REASON),
			JurisdictionHelper.booleanSelector(cb, contactService.inJurisdictionOrOwned(contactQueryContext)),
			JurisdictionHelper.booleanSelector(
				cb,
				caseService.inJurisdictionOrOwned(
					new CaseQueryContext(
						contactQueryContext.getCriteriaBuilder(),
						contactQueryContext.getQuery(),
						contactQueryContext.getJoins().getCaseJoins()))));
	}

	public List<Selection<?>> getMergeContactIndexSelections(Root<Contact> contact, ContactQueryContext contactQueryContext) {

		ContactJoins joins = contactQueryContext.getJoins();
		CriteriaBuilder cb = contactQueryContext.getCriteriaBuilder();

		return Arrays.asList(
			contact.get(Contact.ID),
			contact.get(Contact.UUID),
			joins.getPerson().get(Person.FIRST_NAME),
			joins.getPerson().get(Person.LAST_NAME),
			joins.getPerson().get(Person.APPROXIMATE_AGE),
			joins.getPerson().get(Person.APPROXIMATE_AGE_TYPE),
			joins.getPerson().get(Person.BIRTHDATE_DD),
			joins.getPerson().get(Person.BIRTHDATE_MM),
			joins.getPerson().get(Person.BIRTHDATE_YYYY),
			joins.getPerson().get(Person.SEX),
			joins.getCaze().get(Case.UUID),
			joins.getCasePerson().get(Person.FIRST_NAME),
			joins.getCasePerson().get(Person.LAST_NAME),
			contact.get(Contact.DISEASE),
			contact.get(Contact.DISEASE_DETAILS),
			joins.getRegion().get(Region.NAME),
			joins.getDistrict().get(District.NAME),
			contact.get(Contact.LAST_CONTACT_DATE),
			contact.get(Contact.CREATION_DATE),
			contact.get(Contact.CONTACT_CLASSIFICATION),
			contact.get(Contact.COMPLETENESS),
			contact.get(Contact.REPORT_DATE_TIME),
			JurisdictionHelper.booleanSelector(cb, contactService.inJurisdictionOrOwned(contactQueryContext)),
			JurisdictionHelper.booleanSelector(
				cb,
				caseService.inJurisdictionOrOwned(
					new CaseQueryContext(
						contactQueryContext.getCriteriaBuilder(),
						contactQueryContext.getQuery(),
						contactQueryContext.getJoins().getCaseJoins()))));
	}

	private List<Expression<?>> getIndexOrders(SortProperty sortProperty, Root<Contact> contact, ContactJoins joins, CriteriaBuilder cb) {

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
		case ContactIndexDto.VACCINATION_STATUS:
			expressions.add(contact.get(sortProperty.propertyName));
			break;
		case ContactIndexDto.EXTERNAL_ID:
		case ContactIndexDto.EXTERNAL_TOKEN:
		case ContactIndexDto.INTERNAL_TOKEN:
			expressions.add(cb.lower(contact.get(sortProperty.propertyName)));
			break;
		case ContactIndexDto.PERSON_UUID:
			expressions.add(joins.getPerson().get(Person.UUID));
			break;
		case ContactIndexDto.PERSON_FIRST_NAME:
		case ContactIndexDto.PERSON_LAST_NAME:
			expressions.add(cb.lower(joins.getPerson().get(sortProperty.propertyName)));
			break;
		case ContactIndexDto.SYMPTOM_JOURNAL_STATUS:
			expressions.add(joins.getPerson().get(sortProperty.propertyName));
			break;
		case ContactIndexDto.CAZE:
			expressions.add(cb.lower(joins.getCasePerson().get(Person.FIRST_NAME)));
			expressions.add(cb.lower(joins.getCasePerson().get(Person.LAST_NAME)));
			break;
		case ContactIndexDto.REGION_UUID:
			expressions.add(cb.lower(joins.getRegion().get(Region.NAME)));
			break;
		case ContactIndexDto.DISTRICT_UUID:
			expressions.add(cb.lower(joins.getDistrict().get(District.NAME)));
			break;
		default:
			throw new IllegalArgumentException(sortProperty.propertyName);
		}

		return expressions;
	}

	private List<Selection<?>> getContactIndexDetailedSelections(Root<Contact> contact, ContactQueryContext contactQueryContext) {

		final ContactJoins joins = contactQueryContext.getJoins();

		Join<Person, PersonContactDetail> phone = joins.getPersonJoins().getPhone();
		CriteriaBuilder cb = contactQueryContext.getCriteriaBuilder();
		phone.on(
			cb.and(
				cb.isTrue(phone.get(PersonContactDetail.PRIMARY_CONTACT)),
				cb.equal(phone.get(PersonContactDetail.PERSON_CONTACT_DETAIL_TYPE), PersonContactDetailType.PHONE)));

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
			phone.get(PersonContactDetail.CONTACT_INFORMATION),
			joins.getReportingUser().get(User.FIRST_NAME),
			joins.getReportingUser().get(User.LAST_NAME),
			contact.get(Contact.RELATION_TO_CASE));
		indexSelection.addAll(selections);

		return indexSelection;
	}

	private List<Expression<?>> getIndexDetailOrders(SortProperty sortProperty, Root<Contact> contact, ContactJoins joins, CriteriaBuilder cb) {

		switch (sortProperty.propertyName) {
		case ContactIndexDetailedDto.SEX:
		case ContactIndexDetailedDto.APPROXIMATE_AGE:
			return Collections.singletonList(joins.getPerson().get(sortProperty.propertyName));
		case ContactIndexDetailedDto.PHONE:
			return Collections.singletonList(joins.getPersonJoins().getPhone().get(PersonContactDetail.CONTACT_INFORMATION));
		case ContactIndexDetailedDto.DISTRICT_NAME:
			return Collections.singletonList(cb.lower(joins.getDistrict().get(District.NAME)));
		case ContactIndexDetailedDto.CITY:
		case ContactIndexDetailedDto.STREET:
		case ContactIndexDetailedDto.HOUSE_NUMBER:
		case ContactIndexDetailedDto.ADDITIONAL_INFORMATION:
		case ContactIndexDetailedDto.POSTAL_CODE:
			return Collections.singletonList(cb.lower(joins.getAddress().get(sortProperty.propertyName)));
		case ContactIndexDetailedDto.REPORTING_USER:
			return Arrays.asList(cb.lower(joins.getReportingUser().get(User.FIRST_NAME)), cb.lower(joins.getReportingUser().get(User.LAST_NAME)));
		case ContactIndexDetailedDto.RELATION_TO_CASE:
			return Collections.singletonList(contact.get(Contact.RELATION_TO_CASE));
		default:
			return this.getIndexOrders(sortProperty, contact, joins, cb);
		}
	}

	private <T> CriteriaQuery<T> buildIndexCriteria(
		Class<T> type,
		BiFunction<Root<Contact>, ContactQueryContext, List<Selection<?>>> selectionProvider,
		ContactCriteria contactCriteria,
		OrderExpressionProvider orderExpressionProvider,
		List<SortProperty> sortProperties,
		List<Long> ids) {

		boolean prefetchIds = ids == null;

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<T> cq = cb.createQuery(type);
		final Root<Contact> contact = cq.from(Contact.class);
		final ContactQueryContext contactQueryContext = new ContactQueryContext(cb, cq, contact);
		final ContactJoins joins = contactQueryContext.getJoins();

		List<Selection<?>> selections = new ArrayList<>();

		Expression<Date> latestChangedDateFunction = cb
			.function(ExtendedPostgreSQL94Dialect.GREATEST, Date.class, contact.get(Contact.CHANGE_DATE), joins.getPerson().get(Person.CHANGE_DATE));

		List<Selection<?>> orderBySelections = new ArrayList<>();
		if (CollectionUtils.isEmpty(sortProperties)) {
			orderBySelections.add(latestChangedDateFunction);
			cq.orderBy(cb.desc(latestChangedDateFunction));
		} else {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				List<Expression<?>> expressions = orderExpressionProvider.forProperty(sortProperty, contact, joins, cb);
				orderBySelections.addAll(expressions);
				order.addAll(expressions.stream().map(e -> sortProperty.ascending ? cb.asc(e) : cb.desc(e)).collect(Collectors.toList()));
			}
			cq.orderBy(order);
		}

		if (prefetchIds) {
			selections.add(contact.get(AbstractDomainObject.ID));
		} else {
			selections.addAll(selectionProvider.apply(contact, contactQueryContext));
			selections.add(cb.size(contact.get(Contact.VISITS)));
			// This is needed in selection because of the combination of distinct and orderBy clauses - every operator in the orderBy has to be part of the select IF distinct is used
		}

		// include order by in the select
		selections.addAll(orderBySelections);

		Predicate filter = buildContactFilter(contactCriteria, contactQueryContext);

		if (!prefetchIds) {
			filter = CriteriaBuilderHelper.and(cb, filter, contact.get(AbstractDomainObject.ID).in(ids));
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.multiselect(selections);
		cq.distinct(true);

		return cq;
	}

	public Predicate buildContactFilter(ContactCriteria contactCriteria, ContactQueryContext contactQueryContext) {

		Predicate filter = null;

		// Only use user filter if no restricting case is specified
		if (contactCriteria == null || contactCriteria.getCaze() == null) {
			filter = contactService.createUserFilter(contactQueryContext, contactCriteria);
		}

		if (contactCriteria != null) {
			Predicate criteriaFilter = contactService.buildCriteriaFilter(contactCriteria, contactQueryContext);
			filter = CriteriaBuilderHelper.and(contactQueryContext.getCriteriaBuilder(), filter, criteriaFilter);
		}
		return filter;
	}

	private interface OrderExpressionProvider {

		List<Expression<?>> forProperty(SortProperty sortProperty, Root<Contact> contact, ContactJoins joins, CriteriaBuilder cb);
	}
}
