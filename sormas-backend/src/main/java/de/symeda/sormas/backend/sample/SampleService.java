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
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.DashboardSampleDto;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sample.SampleTestResultType;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class SampleService extends AbstractAdoService<Sample> {

	@EJB
	private CaseService caseService;	
	@EJB
	private SampleTestService sampleTestService;

	public SampleService() {
		super(Sample.class);
	}

	@Override
	public void delete(Sample sample) {
		sample.setMainSampleTest(null);
		for (SampleTest sampleTest : sample.getSampleTests()) {
			sampleTestService.delete(sampleTest);
		}

		// Remove the reference from another sample to this sample, if existing
		Sample referralSample = getReferredFrom(sample.getUuid());
		if (referralSample != null) {
			referralSample.setReferredTo(null);
		}

		super.delete(sample);
	}

	public List<Sample> getAllActiveSamplesAfter(Date date, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Sample> cq = cb.createQuery(getElementClass());
		Root<Sample> from = cq.from(getElementClass());
		Join<Sample, Case> caze = from.join(Sample.ASSOCIATED_CASE, JoinType.LEFT);

		Predicate filter = cb.or(
				cb.equal(caze.get(Case.ARCHIVED), false),
				cb.isNull(caze.get(Case.ARCHIVED)));

		if (user != null) {
			Predicate userFilter = createUserFilter(cb, cq, from, user);
			filter = cb.and(filter, userFilter);
		}

		if (date != null) {
			Predicate dateFilter = createChangeDateFilter(cb, from, date);
			filter = cb.and(filter, dateFilter);		
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
		Join<Sample, Case> caze = from.join(Sample.ASSOCIATED_CASE, JoinType.LEFT);

		Predicate filter = cb.or(
				cb.equal(caze.get(Case.ARCHIVED), false),
				cb.isNull(caze.get(Case.ARCHIVED)));

		if (user != null) {
			Predicate userFilter = createUserFilter(cb, cq, from, user);
			filter = cb.and(filter, userFilter);
		}

		cq.where(filter);
		cq.select(from.get(Sample.UUID));

		return em.createQuery(cq).getResultList();
	}

	public List<Sample> getAllByCase(Case caze) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Sample> cq = cb.createQuery(getElementClass());
		Root<Sample> from = cq.from(getElementClass());

		if(caze != null) {
			cq.where(cb.equal(from.get(Sample.ASSOCIATED_CASE), caze));
		}
		cq.orderBy(cb.desc(from.get(Sample.REPORT_DATE_TIME)));

		List<Sample> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}

	public int getReceivedSampleCountByCase(Case caze) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Sample> from = cq.from(getElementClass());

		cq.select(cb.count(from));
		cq.where(cb.and(
				cb.equal(from.get(Sample.ASSOCIATED_CASE), caze),
				cb.equal(from.get(Sample.RECEIVED), true)));

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

	public void updateMainSampleTest(Sample sample) {
		if (sample.getSampleTests().isEmpty()) {
			sample.setMainSampleTest(null);
		} else {
			sample.setMainSampleTest(sample.getSampleTests().stream()
					.sorted(Comparator.comparing(SampleTest::getTestDateTime, Comparator.nullsLast(Comparator.reverseOrder())))
					.findFirst().get());
		}
	}

	public List<DashboardSampleDto> getNewSamplesForDashboard(Region region, District district, Disease disease, Date from, Date to, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DashboardSampleDto> cq = cb.createQuery(DashboardSampleDto.class);
		Root<Sample> sample = cq.from(getElementClass());
		Join<Sample, Case> caze = sample.join(Sample.ASSOCIATED_CASE, JoinType.LEFT);

		Predicate filter = createUserFilter(cb, cq, sample, user);
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

	public List<Date> getSampleDatesForCase(long caseId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Date> cq = cb.createQuery(Date.class);
		Root<Sample> sample = cq.from(getElementClass());
		cq.where(cb.equal(sample.get(Sample.ASSOCIATED_CASE).get(Case.ID), caseId));
		cq.select(sample.get(Sample.SAMPLE_DATE_TIME));
		List<Date> result = em.createQuery(cq).getResultList();
		return result;
	}

	/**
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<Sample,Sample> samplePath, User user) {

		Predicate filter = createUserFilterWithoutCase(cb, cq, samplePath, user);

		// whoever created the case the sample is associated with or is assigned to it
		// is allowed to access it
		Path<Case> casePath = samplePath.get(Sample.ASSOCIATED_CASE);

		Predicate caseFilter = caseService.createUserFilter(cb, cq, (From<Case,Case>)casePath, user);
		filter = or(cb, filter, caseFilter);

		return filter;
	}

	@SuppressWarnings("rawtypes")
	public Predicate createUserFilterWithoutCase(CriteriaBuilder cb, CriteriaQuery cq, From<Sample,Sample> samplePath, User user) {

		Predicate filter = null;
		// user that reported it is not able to access it. Otherwise they would also need to access the case
		//filter = cb.equal(samplePath.get(Sample.REPORTING_USER), user);

		// lab users can see samples assigned to their laboratory
		if (user.getUserRoles().contains(UserRole.LAB_USER) || user.getUserRoles().contains(UserRole.EXTERNAL_LAB_USER)) {
			if(user.getLaboratory() != null) {
				filter = or(cb, filter, cb.equal(samplePath.get(Sample.LAB), user.getLaboratory()));			}
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
		if (criteria.getTestResult() != null) {
			Predicate subFilter = cb.equal(from.join(Sample.MAIN_SAMPLE_TEST, JoinType.LEFT).get(SampleTest.TEST_RESULT), criteria.getTestResult());
			if (criteria.getTestResult() == SampleTestResultType.PENDING) {
				subFilter = or(cb, subFilter, cb.isNull(from.join(Sample.MAIN_SAMPLE_TEST, JoinType.LEFT).get(SampleTest.TEST_RESULT)));
			}
			filter = and(cb, filter, subFilter);
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
		if (Boolean.TRUE.equals(criteria.getArchived())) {
			filter = and(cb, filter, cb.equal(caze.get(Case.ARCHIVED), true));
		} else {
			filter = and(cb, filter, cb.or(cb.equal(caze.get(Case.ARCHIVED), false), cb.isNull(caze.get(Case.ARCHIVED))));
		}

		if (criteria.getCaseCodeIdLike() != null) {
			Join<Case, Person> casePerson = caze.join(Case.PERSON, JoinType.LEFT);
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
							cb.like(cb.lower(from.get(Sample.SAMPLE_CODE)), textFilter));
					filter = and(cb, filter, likeFilters);
				}
			}
		}

		return filter;
	}
}
