package de.symeda.sormas.backend.sample;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.sample.AdditionalTestCriteria;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilter;
import de.symeda.sormas.backend.common.CoreAdo;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.QueryHelper;

@Stateless
@LocalBean
public class AdditionalTestService extends AdoServiceWithUserFilter<AdditionalTest> {

	@EJB
	private SampleService sampleService;

	public AdditionalTestService() {
		super(AdditionalTest.class);
	}

	public List<AdditionalTest> getAllActiveAdditionalTestsAfter(Date date, User user, Integer batchSize, String lastSynchronizedUuid) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AdditionalTest> cq = cb.createQuery(getElementClass());
		Root<AdditionalTest> from = cq.from(getElementClass());

		Predicate filter = createActiveSamplesFilter(cb, from);

		if (user != null) {
			Predicate userFilter = createUserFilter(cb, cq, from);
			filter = CriteriaBuilderHelper.and(cb, filter, userFilter);
		}

		if (date != null) {
			Predicate dateFilter = createChangeDateFilter(cb, from, date, lastSynchronizedUuid);
			filter = cb.and(filter, dateFilter);
		}

		cq.where(filter);
		cq.distinct(true);

		return getBatchedQueryResults(cb, cq, from, batchSize);
	}

	public Predicate buildCriteriaFilter(AdditionalTestCriteria additionalTestCriteria, CriteriaBuilder cb, Root<AdditionalTest> from) {
		Predicate filter = createActiveSamplesFilter(cb, from);

		if (additionalTestCriteria.getSample() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(from.get(AdditionalTest.SAMPLE).get(Sample.UUID), additionalTestCriteria.getSample().getUuid()));
		}

		return filter;
	}

	public List<AdditionalTest> getIndexList(
		AdditionalTestCriteria additionalTestCriteria,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AdditionalTest> cq = cb.createQuery(getElementClass());
		Root<AdditionalTest> from = cq.from(getElementClass());

		Predicate filter = null;
		if (additionalTestCriteria != null) {
			filter = buildCriteriaFilter(additionalTestCriteria, cb, from);
		}
		if (filter != null) {
			cq.where(filter);
		}
		cq.orderBy(cb.desc(from.get(AdditionalTest.CHANGE_DATE)));
		cq.distinct(true);

		List<Order> order = new ArrayList<>();
		if (sortProperties != null && sortProperties.size() > 0) {
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case AdditionalTest.UUID:
				case AdditionalTest.SAMPLE:
				case AdditionalTest.TEST_DATE_TIME:
					expression = from.get(sortProperty.propertyName);
					order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
		}
		order.add(cb.desc(from.get(AdditionalTest.UUID)));
		cq.orderBy(order);

		return QueryHelper.getResultList(em, cq, first, max);
	}

	public long count(AdditionalTestCriteria additionalTestCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AdditionalTest> additionalTestRoot = cq.from(getElementClass());

		Predicate filter = createActiveSamplesFilter(cb, additionalTestRoot);

		if (additionalTestCriteria != null) {
			Predicate criteriaFilter = buildCriteriaFilter(additionalTestCriteria, cb, additionalTestRoot);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.countDistinct(additionalTestRoot));
		return em.createQuery(cq).getSingleResult();
	}

	public List<String> getAllActiveUuids(User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AdditionalTest> from = cq.from(getElementClass());
		Predicate filter = createActiveSamplesFilter(cb, from);

		if (user != null) {
			Predicate userFilter = createUserFilter(cb, cq, from);
			filter = CriteriaBuilderHelper.and(cb, filter, userFilter);
		}

		cq.where(filter);
		cq.select(from.get(AdditionalTest.UUID));

		return em.createQuery(cq).getResultList();
	}

	public List<AdditionalTest> getAllBySample(Sample sample) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AdditionalTest> cq = cb.createQuery(getElementClass());
		Root<AdditionalTest> from = cq.from(getElementClass());

		if (sample != null) {
			cq.where(cb.equal(from.get(AdditionalTest.SAMPLE), sample));
		}
		cq.orderBy(cb.desc(from.get(AdditionalTest.TEST_DATE_TIME)));

		List<AdditionalTest> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}

	/**
	 * Creates a filter that excludes all samples that are {@link CoreAdo#deleted} or associated with
	 * cases that are {@link Case#archived}, contacts that are {@link Contact#deleted}. or event participants that are
	 * {@link EventParticipant#deleted}
	 */
	public Predicate createActiveSamplesFilter(CriteriaBuilder cb, Root<AdditionalTest> root) {
		Join<AdditionalTest, Sample> sample = root.join(AdditionalTest.SAMPLE, JoinType.LEFT);
		Join<Sample, Case> caze = sample.join(Sample.ASSOCIATED_CASE, JoinType.LEFT);
		Join<Sample, Contact> contact = sample.join(Sample.ASSOCIATED_CONTACT, JoinType.LEFT);
		Join<Sample, EventParticipant> event = sample.join(Sample.ASSOCIATED_EVENT_PARTICIPANT, JoinType.LEFT);
		Predicate pred =
			cb.or(cb.isFalse(caze.get(Case.ARCHIVED)), cb.isFalse(contact.get(Contact.DELETED)), cb.isFalse(event.get(EventParticipant.DELETED)));
		return cb.and(pred, cb.isFalse(sample.get(Sample.DELETED)));
	}

	/**
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, AdditionalTest> additionalTestPath) {

		// whoever created the sample the additional test is associated with is allowed to access it
		Join<Sample, Sample> sampleJoin = additionalTestPath.join(AdditionalTest.SAMPLE);
		Predicate filter = sampleService.createUserFilter(cb, cq, sampleJoin);

		return filter;
	}

	/**
	 * @param additionalTestUuids
	 *            {@link AdditionalTest}s identified by {@code uuid} to be deleted.
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void delete(List<String> additionalTestUuids) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<AdditionalTest> cd = cb.createCriteriaDelete(AdditionalTest.class);
		Root<AdditionalTest> root = cd.from(AdditionalTest.class);

		cd.where(root.get(AdditionalTest.UUID).in(additionalTestUuids));

		em.createQuery(cd).executeUpdate();
	}
}
