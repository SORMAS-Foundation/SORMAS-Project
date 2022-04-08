package de.symeda.sormas.backend.labmessage;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.labmessage.LabMessageCriteria;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilter;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class LabMessageService extends AdoServiceWithUserFilter<LabMessage> {

	@EJB
	private TestReportService testReportService;

	public LabMessageService() {
		super(LabMessage.class);
	}

	@Override
	public void deletePermanent(LabMessage labMessage) {

		labMessage.getTestReports().forEach(t -> testReportService.deletePermanent(t));

		super.deletePermanent(labMessage);
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, LabMessage> from) {
		return null;
	}

	public Predicate buildCriteriaFilter(CriteriaBuilder cb, Root<LabMessage> labMessage, LabMessageCriteria criteria) {
		Predicate filter = null;
		if (criteria.getUuid() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(labMessage.get(LabMessage.UUID), criteria.getUuid()));
		}
		if (criteria.getLabMessageStatus() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(labMessage.get(LabMessage.STATUS), criteria.getLabMessageStatus()));
		}
		if (criteria.getSample() != null) {
			filter =
				CriteriaBuilderHelper.and(cb, filter, cb.equal(labMessage.get(LabMessage.SAMPLE).get(Sample.UUID), criteria.getSample().getUuid()));
		}
		if (criteria.getSearchFieldLike() != null) {
			String[] textFilters = criteria.getSearchFieldLike().split("\\s+");
			for (String textFilter : textFilters) {
				if (DataHelper.isNullOrEmpty(textFilter)) {
					continue;
				}

				Predicate likeFilters = cb.or(
					CriteriaBuilderHelper.ilike(cb, labMessage.get(LabMessage.UUID), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, labMessage.get(LabMessage.PERSON_FIRST_NAME), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, labMessage.get(LabMessage.PERSON_LAST_NAME), textFilter),
					CriteriaBuilderHelper.ilike(cb, labMessage.get(LabMessage.PERSON_POSTAL_CODE), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, labMessage.get(LabMessage.LAB_NAME), textFilter),
					CriteriaBuilderHelper.ilike(cb, labMessage.get(LabMessage.LAB_POSTAL_CODE), textFilter));
				filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
			}
		}
		if (criteria.getMessageDateFrom() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.greaterThanOrEqualTo(labMessage.get(LabMessage.MESSAGE_DATE_TIME), criteria.getMessageDateFrom()));
		}
		if (criteria.getMessageDateTo() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.lessThanOrEqualTo(labMessage.get(LabMessage.MESSAGE_DATE_TIME), criteria.getMessageDateTo()));
		}
		if (criteria.getBirthDateFrom() != null) {
			Calendar birthdayFrom = Calendar.getInstance();
			birthdayFrom.setTime(criteria.getBirthDateFrom());
			int yearFrom = birthdayFrom.get(Calendar.YEAR);
			int monthFrom = birthdayFrom.get(Calendar.MONTH) + 1;
			int dayFrom = birthdayFrom.get(Calendar.DAY_OF_MONTH);
			Predicate birthDateFromFilter = CriteriaBuilderHelper.or(
				cb,
				cb.greaterThan(labMessage.get(LabMessage.PERSON_BIRTH_DATE_YYYY), yearFrom),
				CriteriaBuilderHelper.and(
					cb,
					cb.equal(labMessage.get(LabMessage.PERSON_BIRTH_DATE_YYYY), yearFrom),
					CriteriaBuilderHelper.or(
						cb,
						cb.greaterThan(labMessage.get(LabMessage.PERSON_BIRTH_DATE_MM), monthFrom),
						CriteriaBuilderHelper.and(
							cb,
							cb.equal(labMessage.get(LabMessage.PERSON_BIRTH_DATE_MM), monthFrom),
							cb.greaterThanOrEqualTo(labMessage.get(LabMessage.PERSON_BIRTH_DATE_DD), dayFrom)))));
			filter = CriteriaBuilderHelper.and(cb, filter, birthDateFromFilter);
		}
		if (criteria.getBirthDateTo() != null) {
			Calendar birthdayTo = Calendar.getInstance();
			birthdayTo.setTime(criteria.getBirthDateTo());
			int yearTo = birthdayTo.get(Calendar.YEAR);
			int monthTo = birthdayTo.get(Calendar.MONTH) + 1;
			int dayTo = birthdayTo.get(Calendar.DAY_OF_MONTH);
			Predicate birthDateToFilter = CriteriaBuilderHelper.or(
				cb,
				cb.lessThan(labMessage.get(LabMessage.PERSON_BIRTH_DATE_YYYY), yearTo),
				CriteriaBuilderHelper.and(
					cb,
					cb.equal(labMessage.get(LabMessage.PERSON_BIRTH_DATE_YYYY), yearTo),
					CriteriaBuilderHelper.or(
						cb,
						cb.lessThan(labMessage.get(LabMessage.PERSON_BIRTH_DATE_MM), monthTo),
						CriteriaBuilderHelper.and(
							cb,
							cb.equal(labMessage.get(LabMessage.PERSON_BIRTH_DATE_MM), monthTo),
							cb.lessThanOrEqualTo(labMessage.get(LabMessage.PERSON_BIRTH_DATE_DD), dayTo)))));
			filter = CriteriaBuilderHelper.and(cb, filter, birthDateToFilter);
		}

		if (criteria.getAssignee() != null) {
			if (ReferenceDto.NO_REFERENCE_UUID.equals(criteria.getAssignee().getUuid())) {
				filter = cb.and(filter, labMessage.get(LabMessage.ASSIGNEE).isNull());
			} else {
				filter = CriteriaBuilderHelper
					.and(cb, filter, cb.equal(labMessage.join(LabMessage.ASSIGNEE, JoinType.LEFT).get(User.UUID), criteria.getAssignee().getUuid()));
			}
		}
		return filter;
	}

	public List<LabMessage> getForSample(SampleReferenceDto sample) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<LabMessage> cq = cb.createQuery(LabMessage.class);
		Root<LabMessage> labMessageRoot = cq.from(LabMessage.class);

		LabMessageCriteria criteria = new LabMessageCriteria();
		criteria.setSample(sample);

		Predicate filter = buildCriteriaFilter(cb, labMessageRoot, criteria);

		cq.where(filter);
		cq.distinct(true);

		cq.orderBy(cb.desc(labMessageRoot.get(LabMessage.CREATION_DATE)));

		return em.createQuery(cq).getResultList();
	}

	public long countForCase(String caseUuid) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<LabMessage> labMessageRoot = cq.from(LabMessage.class);
		Join<LabMessage, Sample> sampleJoin = labMessageRoot.join(LabMessage.SAMPLE, JoinType.LEFT);
		Join<Sample, Case> caseJoin = sampleJoin.join(Sample.ASSOCIATED_CASE, JoinType.LEFT);

		cq.where(caseJoin.get(AbstractDomainObject.UUID).in(Collections.singleton(caseUuid)));
		cq.select(cb.countDistinct(labMessageRoot));

		return em.createQuery(cq).getSingleResult();
	}

	public long countForContact(String contactUuid) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<LabMessage> labMessageRoot = cq.from(LabMessage.class);
		Join<LabMessage, Sample> sampleJoin = labMessageRoot.join(LabMessage.SAMPLE, JoinType.LEFT);
		Join<Sample, Contact> contactJoin = sampleJoin.join(Sample.ASSOCIATED_CONTACT, JoinType.LEFT);

		cq.where(contactJoin.get(AbstractDomainObject.UUID).in(Collections.singleton(contactUuid)));
		cq.select(cb.countDistinct(labMessageRoot));

		return em.createQuery(cq).getSingleResult();
	}

	public long countForEventParticipant(String eventParticipantUuid) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<LabMessage> labMessageRoot = cq.from(LabMessage.class);
		Join<LabMessage, Sample> sampleJoin = labMessageRoot.join(LabMessage.SAMPLE, JoinType.LEFT);
		Join<Sample, EventParticipant> eventParticipantJoin = sampleJoin.join(Sample.ASSOCIATED_EVENT_PARTICIPANT, JoinType.LEFT);

		cq.where(eventParticipantJoin.get(AbstractDomainObject.UUID).in(Collections.singleton(eventParticipantUuid)));
		cq.select(cb.countDistinct(labMessageRoot));

		return em.createQuery(cq).getSingleResult();
	}
}
