/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.sample;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.sample.DashboardSampleDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractCoreAdoService;
import de.symeda.sormas.backend.common.CoreAdo;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class SampleService extends AbstractCoreAdoService<Sample> {

	@EJB
	private CaseService caseService;	
	@EJB
	private PathogenTestService pathogenTestService;
	@EJB
	private AdditionalTestService additionalTestService;

	public SampleService() {
		super(Sample.class);
	}

	public List<Sample> findBy(SampleCriteria criteria, User user, String sortProperty, boolean ascending) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Sample> cq = cb.createQuery(getElementClass());
		Root<Sample> from = cq.from(getElementClass());

		Predicate filter = buildCriteriaFilter(criteria, cb, from);

		if (user != null) {
			filter = and(cb, filter, createUserFilter(cb, cq, from, user));
		}
		if (filter != null) {
			cq.where(filter);
		}

		if (ascending) {
			cq.orderBy(cb.asc(from.get(sortProperty)));
		} else {
			cq.orderBy(cb.desc(from.get(sortProperty)));
		}

		List<Sample> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}

	public List<Sample> findBy(SampleCriteria criteria, User user) {
		return findBy(criteria, user, Sample.CREATION_DATE, true);
	}

	public List<Sample> getAllActiveSamplesAfter(Date date, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Sample> cq = cb.createQuery(getElementClass());
		Root<Sample> from = cq.from(getElementClass());

		Predicate filter = createActiveSamplesFilter(cb, from);

		if (user != null) {
			Predicate userFilter = createUserFilter(cb, cq, from, user);
			filter = AbstractAdoService.and(cb, filter, userFilter);
		}

		if (date != null) {
			Predicate dateFilter = createChangeDateFilter(cb, from, date);
			filter = AbstractAdoService.and(cb, filter, dateFilter);	
		}

		cq.where(filter);
		cq.orderBy(cb.desc(from.get(Sample.CHANGE_DATE)));
		cq.distinct(true);

		return em.createQuery(cq).getResultList();
	}

	public List<String> getAllActiveUuids(User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Sample> from = cq.from(getElementClass());

		Predicate filter = createActiveSamplesFilter(cb, from);

		if (user != null) {
			Predicate userFilter = createUserFilter(cb, cq, from, user);
			filter = AbstractAdoService.and(cb, filter, userFilter);
		}

		cq.where(filter);
		cq.select(from.get(Sample.UUID));

		return em.createQuery(cq).getResultList();
	}

	public int getSampleCountByCase(Case caze) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Sample> from = cq.from(getElementClass());

		cq.select(cb.count(from));
		cq.where(cb.and(
				createDefaultFilter(cb, from),
				cb.equal(from.get(Sample.ASSOCIATED_CASE), caze)));

		return em.createQuery(cq).getSingleResult().intValue();
	}

	/**
	 * Returns the sample that refers to the sample identified by the sampleUuid.
	 * 
	 * @param sampleUuid The UUID of the sample to get the referral for.
	 * @return The sample that refers to this sample, or null if none is found.
	 */
	public Sample getReferredFrom(String sampleUuid) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Sample> cq = cb.createQuery(getElementClass());
		Root<Sample> from = cq.from(getElementClass());

		cq.where(cb.equal(from.get(Sample.REFERRED_TO), getByUuid(sampleUuid)));
		try {
			return em.createQuery(cq).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<DashboardSampleDto> getNewSamplesForDashboard(Region region, District district, Disease disease, Date from, Date to, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DashboardSampleDto> cq = cb.createQuery(DashboardSampleDto.class);
		Root<Sample> sample = cq.from(getElementClass());
		Join<Sample, Case> caze = sample.join(Sample.ASSOCIATED_CASE, JoinType.LEFT);

		Predicate filter = createDefaultFilter(cb, sample);
		filter = AbstractAdoService.and(cb, filter, createUserFilter(cb, cq, sample, user));
		Predicate dateFilter = cb.between(sample.get(Sample.REPORT_DATE_TIME), from, to);
		if (filter != null) {
			filter = cb.and(filter, dateFilter);
		} else {
			filter = dateFilter;
		}

		if (region != null) {
			Predicate regionFilter = cb.equal(caze.get(Case.REGION), region);
			if (filter != null) {
				filter = cb.and(filter, regionFilter);
			} else {
				filter = regionFilter;
			}
		}

		if (district != null) {
			Predicate districtFilter = cb.equal(caze.get(Case.DISTRICT), district);
			if (filter != null) {
				filter = cb.and(filter, districtFilter);
			} else {
				filter = districtFilter;
			}
		}

		if (disease != null) {
			Predicate diseaseFilter = cb.equal(caze.get(Case.DISEASE), disease);
			if (filter != null) {
				filter = cb.and(filter, diseaseFilter);
			} else {
				filter = diseaseFilter;
			}
		}

		List<DashboardSampleDto> result;
		if (filter != null) {
			cq.where(filter);
			cq.multiselect(
					sample.get(Sample.SHIPPED),
					sample.get(Sample.RECEIVED)
					);

			result = em.createQuery(cq).getResultList();
		} else {
			result = Collections.emptyList();
		}

		return result;
	}

	public List<String> getDeletedUuidsSince(User user, Date since) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Sample> sample = cq.from(Sample.class);

		Predicate filter = createUserFilter(cb, cq, sample, user);
		if (since != null) {
			Predicate dateFilter = cb.greaterThanOrEqualTo(sample.get(Sample.CHANGE_DATE), since);
			if (filter != null) {
				filter = cb.and(filter, dateFilter);
			} else {
				filter = dateFilter;
			}
		}

		Predicate deletedFilter = cb.equal(sample.get(Sample.DELETED), true);
		if (filter != null) {
			filter = cb.and(filter, deletedFilter);
		} else {
			filter = deletedFilter;
		}

		cq.where(filter);
		cq.select(sample.get(Sample.UUID));

		return em.createQuery(cq).getResultList();
	}
	
	public Map<PathogenTestResultType, Long> getNewTestResultCountByResultType(Region region, District district, Disease disease, Date from, Date to, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Sample> sampleRoot = cq.from(Sample.class);
		Join<Sample, Case> caseJoin = sampleRoot.join(Sample.ASSOCIATED_CASE, JoinType.LEFT);
		
		cq.multiselect(sampleRoot.get(Sample.PATHOGEN_TEST_RESULT), cb.count(sampleRoot));
		cq.groupBy(sampleRoot.get(Sample.PATHOGEN_TEST_RESULT));
		
		Predicate filter = createDefaultFilter(cb, sampleRoot);
		filter = AbstractAdoService.and(cb, filter, createUserFilter(cb, cq, sampleRoot, user));
		
		if (from != null || to != null) {
			filter = and(cb, filter, cb.between(sampleRoot.get(Sample.PATHOGEN_TEST_RESULT_CHANGE_DATE), from, to));
		}
		
		if (region != null) {
			filter = and(cb, filter, cb.equal(caseJoin.get(Case.REGION), region));
		}

		if (district != null) {
			filter = and(cb, filter, cb.equal(caseJoin.get(Case.DISTRICT), district));
		}

		if (disease != null) {
			filter = and(cb, filter, cb.equal(caseJoin.get(Case.DISEASE), disease));
		}
		
		if (filter != null) {
			cq.where(filter);
		}
		
		List<Object[]> results = em.createQuery(cq).getResultList();
		return results.stream().collect(Collectors.toMap(e -> (PathogenTestResultType) e[0], e -> (Long) e[1]));
	}

	@Override
	@SuppressWarnings({ "rawtypes" })
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<Sample,Sample> samplePath, User user) {
		Predicate filter = createUserFilterWithoutCase(cb, cq, samplePath, user);

		// whoever created the case the sample is associated with or is assigned to it
		// is allowed to access it
		Join<Case,Case> casePath = samplePath.join(Sample.ASSOCIATED_CASE);

		Predicate caseFilter = caseService.createUserFilter(cb, cq, casePath, user);
		filter = or(cb, filter, caseFilter);

		return filter;
	}

	@SuppressWarnings("rawtypes")
	public Predicate createUserFilterWithoutCase(CriteriaBuilder cb, CriteriaQuery cq, From<Sample,Sample> samplePath, User user) {
		Join<Sample, Case> caze = samplePath.join(Sample.ASSOCIATED_CASE, JoinType.LEFT);

		Predicate filter = null;
		// user that reported it is not able to access it. Otherwise they would also need to access the case
		//filter = cb.equal(samplePath.get(Sample.REPORTING_USER), user);

		// lab users can see samples assigned to their laboratory
		if (user.getUserRoles().contains(UserRole.LAB_USER) || user.getUserRoles().contains(UserRole.EXTERNAL_LAB_USER)) {
			if(user.getLaboratory() != null) {
				filter = or(cb, filter, cb.equal(samplePath.get(Sample.LAB), user.getLaboratory()));			}
		}

		// only show samples of a specific disease if a limited disease is set
		if (filter != null && user.getLimitedDisease() != null) {
			filter = and(cb, filter, cb.equal(caze.get(Case.DISEASE), user.getLimitedDisease()));
		}

		return filter;
	}

	public Predicate buildCriteriaFilter(SampleCriteria criteria, CriteriaBuilder cb, Root<Sample> from) {
		Join<Sample, Case> caze = from.join(Sample.ASSOCIATED_CASE, JoinType.LEFT);

		Predicate filter = null;
		if (criteria.getRegion() != null) {
			filter = and(cb, filter, cb.equal(caze.join(Case.REGION, JoinType.LEFT).get(Region.UUID), criteria.getRegion().getUuid()));
		}
		if (criteria.getDistrict() != null) {
			filter = and(cb, filter, cb.equal(caze.join(Case.DISTRICT, JoinType.LEFT).get(District.UUID), criteria.getDistrict().getUuid()));
		}
		if (criteria.getLaboratory() != null) {
			filter = and(cb, filter, cb.equal(from.join(Sample.LAB, JoinType.LEFT).get(Facility.UUID), criteria.getLaboratory().getUuid()));
		}
		if (criteria.getShipped() != null) {
			filter = and(cb, filter, cb.equal(from.get(Sample.SHIPPED), criteria.getShipped()));
		}
		if (criteria.getReceived() != null) {
			filter = and(cb, filter, cb.equal(from.get(Sample.RECEIVED), criteria.getReceived()));
		}
		if (criteria.getReferred() != null) {
			if (criteria.getReferred().equals(Boolean.TRUE)) {
				filter = and(cb, filter, cb.isNotNull(from.get(Sample.REFERRED_TO)));
			} else {
				filter = and(cb, filter, cb.isNull(from.get(Sample.REFERRED_TO)));
			}
		}
		if (criteria.getPathogenTestResult() != null) {
			filter = and(cb, filter, cb.equal(from.get(Sample.PATHOGEN_TEST_RESULT), criteria.getPathogenTestResult()));
		}
		if (criteria.getCaseClassification() != null) {
			filter = and(cb, filter, cb.equal(caze.get(Case.CASE_CLASSIFICATION), criteria.getCaseClassification()));
		}		
		if (criteria.getDisease() != null) {
			filter = and(cb, filter, cb.equal(caze.get(Case.DISEASE), criteria.getDisease()));
		}
		if (criteria.getCaze() != null) {
			filter = and(cb, filter, cb.equal(caze.get(Case.UUID), criteria.getCaze().getUuid()));
		}
		if (criteria.getSpecimenCondition() != null) {
			filter = and(cb, filter, cb.equal(from.get(Sample.SPECIMEN_CONDITION), criteria.getSpecimenCondition()));
		}
		if (criteria.getRelevanceStatus() != null) {
			if (criteria.getRelevanceStatus() == EntityRelevanceStatus.ACTIVE) {
				filter = and(cb, filter, cb.or(
							cb.equal(caze.get(Case.ARCHIVED), false),
							cb.isNull(caze.get(Case.ARCHIVED))));
			} else if (criteria.getRelevanceStatus() == EntityRelevanceStatus.ARCHIVED) {
				filter = and(cb, filter, cb.equal(caze.get(Case.ARCHIVED), true));
			}
		}
		if (criteria.getDeleted() != null) {
			filter = and(cb, filter, cb.equal(from.get(Sample.DELETED), criteria.getDeleted()));
		}

		if (criteria.getCaseCodeIdLike() != null) {
			Join<Case, Person> casePerson = caze.join(Case.PERSON, JoinType.LEFT);
			Join<Sample, Facility> lab = from.join(Sample.LAB, JoinType.LEFT);
			String[] textFilters = criteria.getCaseCodeIdLike().split("\\s+");
			for (int i=0; i<textFilters.length; i++)
			{
				String textFilter = "%" + textFilters[i].toLowerCase() + "%";
				if (!DataHelper.isNullOrEmpty(textFilter)) {
					Predicate likeFilters = cb.or(
							cb.like(cb.lower(caze.get(Case.UUID)), textFilter),
							cb.like(cb.lower(casePerson.get(Person.FIRST_NAME)), textFilter),
							cb.like(cb.lower(casePerson.get(Person.LAST_NAME)), textFilter),
							cb.like(cb.lower(from.get(Sample.LAB_SAMPLE_ID)), textFilter),
							cb.like(cb.lower(caze.get(Case.EPID_NUMBER)), textFilter),
							cb.like(cb.lower(lab.get(Facility.NAME)), textFilter));
					filter = and(cb, filter, likeFilters);
				}
			}
		}

		return filter;	
	}

	@Override
	public void delete(Sample sample) {
		// Mark all pathogen tests of this sample as deleted
		for (PathogenTest pathogenTest : sample.getSampleTests()) {
			pathogenTestService.delete(pathogenTest);
		}

		// Delete all additional tests of this sample
		for (AdditionalTest additionalTest : sample.getAdditionalTests()) {
			additionalTestService.delete(additionalTest);
		}

		// Remove the reference from another sample to this sample if existing
		Sample referralSample = getReferredFrom(sample.getUuid());
		if (referralSample != null) {
			referralSample.setReferredTo(null);
			ensurePersisted(referralSample);
		}

		super.delete(sample);
	}

	/**
	 * Creates a filter that excludes all samples that are either {@link CoreAdo#deleted} or associated with
	 * cases that are {@link Case#archived}.
	 */
	public Predicate createActiveSamplesFilter(CriteriaBuilder cb, Root<Sample> root) {
		Join<Sample, Case> caze = root.join(Sample.ASSOCIATED_CASE, JoinType.LEFT);
		return cb.and(
				cb.isFalse(caze.get(Case.ARCHIVED)),
				cb.isFalse(root.get(Case.DELETED)));
	}

	/**
	 * Creates a default filter that should be used as the basis of queries that do not use {@link SampleCriteria}.
	 * This essentially removes {@link CoreAdo#deleted} samples from the queries.
	 */
	public Predicate createDefaultFilter(CriteriaBuilder cb, Root<Sample> root) {
		return cb.isFalse(root.get(Sample.DELETED));
	}

}
