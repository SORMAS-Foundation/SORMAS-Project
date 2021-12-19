package de.symeda.sormas.backend.sample.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;

import de.symeda.sormas.api.sample.SampleAssociationType;
import de.symeda.sormas.api.sample.SampleListCriteria;
import de.symeda.sormas.api.sample.SampleListEntryDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.sample.PathogenTest;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleJoins;
import de.symeda.sormas.backend.sample.SampleQueryContext;
import de.symeda.sormas.backend.sample.transformers.SampleListEntryDtoResultTransformer;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class SampleListService extends BaseSampleService {

	public List<SampleListEntryDto> getEntriesList(SampleListCriteria sampleListCriteria, Integer first, Integer max) {
		if (sampleListCriteria == null) {
			return Collections.emptyList();
		}

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		final Root<Sample> sample = cq.from(Sample.class);

		SampleQueryContext sampleQueryContext = new SampleQueryContext(cb, cq, sample);
		SampleJoins<Sample> joins = (SampleJoins<Sample>) sampleQueryContext.getJoins();

		cq.distinct(true);

		List<Selection<?>> selections = new ArrayList<>(
			Arrays.asList(
				sample.get(Sample.UUID),
				sample.get(Sample.SAMPLE_MATERIAL),
				sample.get(Sample.PATHOGEN_TEST_RESULT),
				sample.get(Sample.SPECIMEN_CONDITION),
				sample.get(Sample.SAMPLE_PURPOSE),
				joins.getReferredSample().get(Sample.UUID),
				sample.get(Sample.RECEIVED),
				sample.get(Sample.RECEIVED_DATE),
				sample.get(Sample.SHIPPED),
				sample.get(Sample.SHIPMENT_DATE),
				sample.get(Sample.SAMPLE_DATE_TIME),
				joins.getLab().get(Facility.NAME),
				joins.getLab().get(Facility.UUID),
				sample.get(Sample.SAMPLING_REASON),
				sample.get(Sample.SAMPLING_REASON_DETAILS),
				sample.get(Sample.ADDITIONAL_TESTING_REQUESTED),
				cb.isNotEmpty(sample.get(Sample.ADDITIONAL_TESTS))));

		// Tests count subquery
		Subquery<Long> testCountSq = cq.subquery(Long.class);
		Root<PathogenTest> testCountRoot = testCountSq.from(PathogenTest.class);
		testCountSq.where(
			cb.equal(testCountRoot.join(PathogenTest.SAMPLE, JoinType.LEFT).get(Sample.ID), sample.get(Sample.ID)),
			cb.isFalse(testCountRoot.get(PathogenTest.DELETED)));
		testCountSq.select(cb.countDistinct(testCountRoot.get(PathogenTest.ID)));
		selections.add(testCountSq.getSelection());

		selections.addAll(getJurisdictionSelections(sampleQueryContext));
		cq.multiselect(selections);

		Predicate filter = createUserFilter(cq, cb, joins, sampleListCriteria.getSampleAssociationType());
		Predicate criteriaFilter = buildCriteriaFilter(sampleListCriteria, cb, joins);
		filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);

		if (filter != null) {
			cq.where(filter);
		}

		cq.orderBy(cb.desc(sample.get(Sample.SAMPLE_DATE_TIME)));

		return createQuery(cq, first, max).unwrap(org.hibernate.query.Query.class)
			.setResultTransformer(new SampleListEntryDtoResultTransformer())
			.getResultList();
	}

	private Predicate createUserFilter(CriteriaQuery cq, CriteriaBuilder cb, SampleJoins joins, SampleAssociationType sampleAssociationType) {
		Predicate filter = createUserFilterWithoutAssociations(cb, joins);

		User currentUser = getCurrentUser();
		final JurisdictionLevel jurisdictionLevel = currentUser.getCalculatedJurisdictionLevel();
		if (jurisdictionLevel == JurisdictionLevel.LABORATORY || jurisdictionLevel == JurisdictionLevel.EXTERNAL_LABORATORY) {
			return filter;
		}

		filter = getUSerFilterFromSampleAssociations(cq, cb, joins, sampleAssociationType, filter, currentUser);

		return filter;
	}

	private Predicate buildCriteriaFilter(SampleListCriteria criteria, CriteriaBuilder cb, SampleJoins joins) {
		Predicate filter = null;
		final SampleAssociationType sampleAssociationType = criteria.getSampleAssociationType();
		if (sampleAssociationType == SampleAssociationType.CASE) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isNotNull(joins.getCaze()));
		} else if (sampleAssociationType == SampleAssociationType.CONTACT) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isNotNull(joins.getContact()));
		} else if (sampleAssociationType == SampleAssociationType.EVENT_PARTICIPANT) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isNotNull(joins.getEventParticipant()));
		}

		if (criteria.getCaseReferenceDto() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getCaze().get(Case.UUID), criteria.getCaseReferenceDto().getUuid()));
		}
		if (criteria.getContactReferenceDto() != null) {
			filter =
				CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getContact().get(Contact.UUID), criteria.getContactReferenceDto().getUuid()));
		}
		if (criteria.getEventParticipantReferenceDto() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.equal(joins.getEventParticipant().get(EventParticipant.UUID), criteria.getEventParticipantReferenceDto().getUuid()));
		}

		return filter;
	}
}
