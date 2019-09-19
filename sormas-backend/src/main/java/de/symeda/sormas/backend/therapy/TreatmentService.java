package de.symeda.sormas.backend.therapy;

import java.util.Date;
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

import com.auth0.jwt.internal.org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.therapy.TreatmentCriteria;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class TreatmentService extends AbstractAdoService<Treatment> {

	@EJB
	CaseService caseService;
	
	public TreatmentService() {
		super(Treatment.class);
	}

	public List<Treatment> findBy(TreatmentCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Treatment> cq = cb.createQuery(getElementClass());
		Root<Treatment> from = cq.from(getElementClass());
		
		Predicate filter = buildCriteriaFilter(criteria, cb, from);
		
		if (filter != null) {
			cq.where(filter);
		}
		cq.orderBy(cb.asc(from.get(Treatment.CREATION_DATE)));
		
		List<Treatment> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}

	public int getTreatmentCountByCase(long caseId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Treatment> from = cq.from(getElementClass());

		cq.select(cb.count(from));
		cq.where(cb.equal(from.join(Treatment.THERAPY, JoinType.LEFT).get(Therapy.CASE).get(Case.ID), caseId));

		return em.createQuery(cq).getSingleResult().intValue();
	}
	
	public List<Treatment> getAllActiveTreatmentsAfter(Date date, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Treatment> cq = cb.createQuery(getElementClass());
		Root<Treatment> from = cq.from(getElementClass());
		Join<Treatment, Therapy> therapy = from.join(Treatment.THERAPY, JoinType.LEFT);
		Join<Therapy, Case> caze = therapy.join(Therapy.CASE, JoinType.LEFT);
	
		Predicate filter = cb.or(
				cb.equal(caze.get(Case.ARCHIVED), false),
				cb.isNull(caze.get(Case.ARCHIVED)));
		
		if (user != null) {
			Predicate userFilter = createUserFilter(cb, cq, from, user);
			filter = AbstractAdoService.and(cb, filter, userFilter);
		}
		
		if (date != null) {
			Predicate dateFilter = createChangeDateFilter(cb, from, date);
			filter = AbstractAdoService.and(cb, filter, dateFilter);
		}
		
		cq.where(filter);
		cq.orderBy(cb.desc(from.get(Treatment.CHANGE_DATE)));
		cq.distinct(true);
		
		return em.createQuery(cq).getResultList();
	}
	
	public List<String> getAllActiveUuids(User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Treatment> from = cq.from(getElementClass());
		Join<Treatment, Therapy> therapy = from.join(Treatment.THERAPY, JoinType.LEFT);
		Join<Therapy, Case> caze = therapy.join(Therapy.CASE, JoinType.LEFT);
		
		Predicate filter = cb.or(
				cb.equal(caze.get(Case.ARCHIVED), false),
				cb.isNull(caze.get(Case.ARCHIVED)));
		
		if (user != null) {
			Predicate userFilter = createUserFilter(cb, cq, from, user);
			filter = AbstractAdoService.and(cb, filter, userFilter);
		}
		
		cq.where(filter);
		cq.select(from.get(Treatment.UUID));
		
		return em.createQuery(cq).getResultList();
	}
	
	public Predicate buildCriteriaFilter(TreatmentCriteria criteria, CriteriaBuilder cb, Root<Treatment> treatment) {
		Predicate filter = null;
		Join<Treatment, Therapy> therapy = treatment.join(Treatment.THERAPY, JoinType.LEFT);
		
		if (criteria.getTherapy() != null) {
			filter = and(cb, filter, cb.equal(therapy.get(Therapy.UUID), criteria.getTherapy().getUuid()));
		}
		if (criteria.getTreatmentType() != null) {
			filter = and(cb, filter, cb.equal(treatment.get(Treatment.TREATMENT_TYPE), criteria.getTreatmentType()));
		}
		if (!StringUtils.isEmpty(criteria.getTextFilter())) {
			String[] textFilters = criteria.getTextFilter().split("\\s+");
			for (int i = 0; i < textFilters.length; i++) {
				String textFilter = "%" + textFilters[i].toLowerCase() + "%";
				if (!StringUtils.isEmpty(textFilter)) {
					Predicate likeFilters = cb.or(
							cb.like(cb.lower(treatment.get(Treatment.TREATMENT_TYPE)), textFilter),
							cb.like(cb.lower(treatment.get(Treatment.TREATMENT_DETAILS)), textFilter),
							cb.like(cb.lower(treatment.get(Treatment.TYPE_OF_DRUG)), textFilter),
							cb.like(cb.lower(treatment.get(Treatment.EXECUTING_CLINICIAN)), textFilter));
					filter = and(cb, filter, likeFilters);
				}
			}
		}
		
		return filter;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<Treatment, Treatment> from, User user) {
		Join<Treatment, Therapy> therapy = from.join(Treatment.THERAPY, JoinType.LEFT);
		return caseService.createUserFilter(cb, cq, therapy.join(Therapy.CASE, JoinType.LEFT), user);
	}
	
}
