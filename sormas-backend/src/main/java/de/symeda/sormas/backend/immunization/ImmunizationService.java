/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.immunization;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.immunization.ImmunizationListEntryDto;
import de.symeda.sormas.api.immunization.ImmunizationManagementStatus;
import de.symeda.sormas.api.immunization.ImmunizationSimilarityCriteria;
import de.symeda.sormas.api.immunization.ImmunizationStatus;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.common.AbstractCoreAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.ChangeDateFilterBuilder;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.immunization.entity.DirectoryImmunization;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.immunization.joins.ImmunizationJoins;
import de.symeda.sormas.backend.immunization.transformers.ImmunizationListEntryDtoTransformer;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.JurisdictionHelper;
import de.symeda.sormas.backend.util.QueryHelper;
import de.symeda.sormas.backend.vaccination.LastVaccinationDate;
import de.symeda.sormas.backend.vaccination.Vaccination;

@Stateless
@LocalBean
public class ImmunizationService extends AbstractCoreAdoService<Immunization> {

	@EJB
	private UserService userService;

	public ImmunizationService() {
		super(Immunization.class);
	}

	public List<ImmunizationListEntryDto> getEntriesList(Long personId, Disease disease, Integer first, Integer max) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		final Root<Immunization> immunization = cq.from(Immunization.class);

		ImmunizationQueryContext<Immunization> immunizationQueryContext = new ImmunizationQueryContext<>(cb, cq, immunization);

		cq.multiselect(
			immunization.get(Immunization.UUID),
			immunization.get(Immunization.DISEASE),
			immunization.get(Immunization.MEANS_OF_IMMUNIZATION),
			immunization.get(Immunization.IMMUNIZATION_STATUS),
			immunization.get(Immunization.IMMUNIZATION_MANAGEMENT_STATUS),
			immunization.get(Immunization.START_DATE),
			immunization.get(Immunization.END_DATE),
			immunization.get(Immunization.CHANGE_DATE),
			JurisdictionHelper.booleanSelector(cb, createUserFilter(immunizationQueryContext)));

		final Predicate criteriaFilter = buildCriteriaFilter(personId, disease, immunizationQueryContext);
		if (criteriaFilter != null) {
			cq.where(criteriaFilter);
		}

		cq.orderBy(cb.desc(immunization.get(Immunization.CHANGE_DATE)));

		cq.distinct(true);

		return createQuery(cq, first, max).unwrap(org.hibernate.query.Query.class)
			.setResultTransformer(new ImmunizationListEntryDtoTransformer())
			.getResultList();
	}

	public boolean inJurisdictionOrOwned(Immunization immunization) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Boolean> cq = cb.createQuery(Boolean.class);
		Root<Immunization> root = cq.from(Immunization.class);
		cq.multiselect(JurisdictionHelper.booleanSelector(cb, createUserFilter(new ImmunizationQueryContext<>(cb, cq, root))));
		cq.where(cb.equal(root.get(Immunization.UUID), immunization.getUuid()));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, Immunization> immunizationPath) {
		return createUserFilter(new ImmunizationQueryContext(cb, cq, immunizationPath));
	}

	public Predicate createActiveImmunizationsFilter(CriteriaBuilder cb, From<?, Immunization> root) {
		return cb.and(cb.isFalse(root.get(Immunization.ARCHIVED)), cb.isFalse(root.get(Immunization.DELETED)));
	}

	public Predicate createDefaultFilter(CriteriaBuilder cb, From<?, Immunization> root) {
		return cb.isFalse(root.get(Immunization.DELETED));
	}

	@Override
	public Predicate createChangeDateFilter(CriteriaBuilder cb, From<?, Immunization> immunization, Timestamp date) {
		Join<Immunization, Vaccination> vaccinations = immunization.join(Immunization.VACCINATIONS, JoinType.LEFT);

		return new ChangeDateFilterBuilder(cb, date).add(immunization).add(vaccinations).build();
	}

	public List<Immunization> getAllActiveAfter(Date date) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Immunization> cq = cb.createQuery(getElementClass());
		Root<Immunization> from = cq.from(getElementClass());

		Predicate filter = createDefaultFilter(cb, from);

		if (getCurrentUser() != null) {
			Predicate userFilter = createUserFilter(cb, cq, from);
			if (userFilter != null) {
				filter = cb.and(filter, userFilter);
			}
		}

		if (date != null) {
			Predicate dateFilter = createChangeDateFilter(cb, from, DateHelper.toTimestampUpper(date));
			if (dateFilter != null) {
				filter = cb.and(filter, dateFilter);
			}
		}

		cq.where(filter);
		cq.orderBy(cb.desc(from.get(Immunization.CHANGE_DATE)));
		cq.distinct(true);

		return em.createQuery(cq).getResultList();
	}

	public boolean isArchived(String uuid) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Immunization> from = cq.from(Immunization.class);

		cq.where(cb.and(cb.equal(from.get(Immunization.ARCHIVED), true), cb.equal(from.get(AbstractDomainObject.UUID), uuid)));
		cq.select(cb.count(from));
		long count = em.createQuery(cq).getSingleResult();
		return count > 0;
	}

	public List<String> getArchivedUuidsSince(Date since) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Immunization> from = cq.from(getElementClass());

		Predicate filter = createUserFilter(cb, cq, from);
		if (since != null) {
			Predicate dateFilter = cb.greaterThanOrEqualTo(from.get(Immunization.CHANGE_DATE), since);
			if (filter != null) {
				filter = cb.and(filter, dateFilter);
			} else {
				filter = dateFilter;
			}
		}

		Predicate archivedFilter = cb.equal(from.get(Immunization.ARCHIVED), true);
		if (filter != null) {
			filter = cb.and(filter, archivedFilter);
		} else {
			filter = archivedFilter;
		}

		cq.where(filter);
		cq.select(from.get(Immunization.UUID));

		return em.createQuery(cq).getResultList();
	}

	public List<String> getDeletedUuidsSince(Date since) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Immunization> from = cq.from(getElementClass());

		Predicate filter = createUserFilter(cb, cq, from);
		if (since != null) {
			Predicate dateFilter = cb.greaterThanOrEqualTo(from.get(Immunization.CHANGE_DATE), since);
			if (filter != null) {
				filter = cb.and(filter, dateFilter);
			} else {
				filter = dateFilter;
			}
		}

		Predicate deletedFilter = cb.equal(from.get(Immunization.DELETED), true);
		if (filter != null) {
			filter = cb.and(filter, deletedFilter);
		} else {
			filter = deletedFilter;
		}

		cq.where(filter);
		cq.select(from.get(Immunization.UUID));

		return em.createQuery(cq).getResultList();
	}

	public List<Object[]> getSimilarImmunizations(ImmunizationSimilarityCriteria criteria) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		final Root<Immunization> from = cq.from(Immunization.class);

		ImmunizationQueryContext<Immunization> immunizationQueryContext = new ImmunizationQueryContext<>(cb, cq, from);
		ImmunizationJoins<Immunization> joins = (ImmunizationJoins<Immunization>) immunizationQueryContext.getJoins();

		cq.multiselect(
			from.get(Immunization.UUID),
			from.get(Immunization.MEANS_OF_IMMUNIZATION),
			from.get(Immunization.IMMUNIZATION_MANAGEMENT_STATUS),
			from.get(Immunization.IMMUNIZATION_STATUS),
			from.get(Immunization.START_DATE),
			from.get(Immunization.END_DATE),
			from.get(Immunization.RECOVERY_DATE),
			from.get(Immunization.CREATION_DATE),
			JurisdictionHelper.booleanSelector(cb, createUserFilter(immunizationQueryContext)));

		Predicate filter = createUserFilter(immunizationQueryContext);

		Predicate immunizationFilter =
			criteria.getImmunizationUuid() != null ? cb.notEqual(from.get(Immunization.UUID), criteria.getImmunizationUuid()) : null;

		Predicate diseaseFilter = criteria.getDisease() != null ? cb.equal(from.get(Immunization.DISEASE), criteria.getDisease()) : null;

		Predicate dateFilter = createDateFilter(cb, from, criteria);

		Predicate personSimilarityFilter =
			criteria.getPersonUuid() != null ? cb.equal(joins.getPerson().get(Person.UUID), criteria.getPersonUuid()) : null;

		Predicate meansOfImmunizationFilter = criteria.getMeansOfImmunization() != null
			? cb.equal(from.get(Immunization.MEANS_OF_IMMUNIZATION), criteria.getMeansOfImmunization())
			: null;

		Predicate notDeletedFilter = cb.isFalse(from.get(Immunization.DELETED));

		filter = CriteriaBuilderHelper.and(cb, filter, immunizationFilter);
		filter = CriteriaBuilderHelper.and(cb, filter, diseaseFilter);
		filter = CriteriaBuilderHelper.and(cb, filter, dateFilter);
		filter = CriteriaBuilderHelper.and(cb, filter, personSimilarityFilter);
		filter = CriteriaBuilderHelper.and(cb, filter, meansOfImmunizationFilter);
		filter = CriteriaBuilderHelper.and(cb, filter, notDeletedFilter);

		cq.where(filter);

		cq.orderBy(cb.desc(from.get(Immunization.CREATION_DATE)));

		cq.distinct(true);

		return em.createQuery(cq).getResultList();
	}

	/**
	 * Retrieves the date of the last vaccination of any immunization associated with the person defined by personUuid
	 * and of the given disease that lies before the given referenceDate.
	 */
	public Date getLastVaccinationDateBefore(String personUuid, Disease disease, Date referenceDate) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Date> cq = cb.createQuery(Date.class);
		Root<DirectoryImmunization> root = cq.from(DirectoryImmunization.class);
		Path lastVaccinationPath = root.join(DirectoryImmunization.LAST_VACCINATION_DATE, JoinType.LEFT).get(LastVaccinationDate.VACCINATION_DATE);

		cq.where(
			cb.and(
				cb.equal(root.get(Immunization.PERSON).get(Person.UUID), personUuid),
				cb.equal(root.get(Immunization.DISEASE), disease),
				cb.lessThanOrEqualTo(lastVaccinationPath, referenceDate)));

		cq.select(lastVaccinationPath);
		cq.orderBy(cb.desc(lastVaccinationPath));
		cq.distinct(true);

		return QueryHelper.getFirstResult(em, cq);
	}

	public void updateImmunizationStatusBasedOnVaccinations(Immunization immunization) {
		ImmunizationManagementStatus immunizationManagementStatus = immunization.getImmunizationManagementStatus();
		if (immunizationManagementStatus == ImmunizationManagementStatus.SCHEDULED
			|| immunizationManagementStatus == ImmunizationManagementStatus.ONGOING) {
			final Integer numberOfDoses = immunization.getNumberOfDoses();
			final int vaccinationCount = immunization.getVaccinations().size();

			if (numberOfDoses != null) {
				final Date startDate = immunization.getStartDate();
				if (System.currentTimeMillis() > startDate.getTime() && vaccinationCount >= 1 && vaccinationCount < numberOfDoses) {
					immunization.setImmunizationManagementStatus(ImmunizationManagementStatus.ONGOING);
					immunization.setImmunizationStatus(ImmunizationStatus.PENDING);
				} else if (vaccinationCount >= numberOfDoses) {
					immunization.setImmunizationManagementStatus(ImmunizationManagementStatus.COMPLETED);
					immunization.setImmunizationStatus(ImmunizationStatus.ACQUIRED);
				}
			}
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void updateImmunizationStatuses() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<Immunization> cu = cb.createCriteriaUpdate(Immunization.class);
		Root<Immunization> root = cu.from(Immunization.class);

		cu.set(Immunization.CHANGE_DATE, Timestamp.from(Instant.now()));
		cu.set(root.get(Immunization.IMMUNIZATION_STATUS), ImmunizationStatus.EXPIRED);

		cu.where(
			cb.and(
				cb.equal(root.get(Immunization.IMMUNIZATION_STATUS), ImmunizationStatus.ACQUIRED),
				cb.lessThanOrEqualTo(root.get(Immunization.VALID_UNTIL), new Date())));

		em.createQuery(cu).executeUpdate();
	}

	private Predicate createUserFilter(ImmunizationQueryContext<Immunization> qc) {
		final User currentUser = userService.getCurrentUser();
		return ImmunizationJurisdictionPredicateValidator.of(qc, currentUser).inJurisdictionOrOwned();
	}

	private Predicate createDateFilter(CriteriaBuilder cb, Root<Immunization> from, ImmunizationSimilarityCriteria criteria) {
		final Date startDate = criteria.getStartDate();
		final Date endDate = criteria.getEndDate();

		if (startDate != null && endDate != null) {
			return cb.or(
				cb.and(
					cb.isNull(from.get(Immunization.END_DATE)),
					cb.isNotNull(from.get(Immunization.START_DATE)),
					cb.between(from.get(Immunization.START_DATE), startDate, endDate)),
				cb.and(
					cb.isNull(from.get(Immunization.START_DATE)),
					cb.isNotNull(from.get(Immunization.END_DATE)),
					cb.between(from.get(Immunization.END_DATE), startDate, endDate)),
				cb.and(cb.isNull(from.get(Immunization.START_DATE)), cb.isNull(from.get(Immunization.END_DATE))),
				cb.and(
					cb.or(cb.isNull(from.get(Immunization.END_DATE)), cb.greaterThanOrEqualTo(from.get(Immunization.END_DATE), startDate)),
					cb.or(cb.isNull(from.get(Immunization.START_DATE)), cb.lessThanOrEqualTo(from.get(Immunization.START_DATE), endDate))));
		} else if (startDate != null) {
			return cb.or(
				cb.isNull(from.get(Immunization.END_DATE)),
				cb.and(cb.isNotNull(from.get(Immunization.END_DATE)), cb.greaterThanOrEqualTo(from.get(Immunization.END_DATE), startDate)),
				cb.and(cb.isNull(from.get(Immunization.START_DATE)), cb.isNull(from.get(Immunization.END_DATE))));
		} else if (endDate != null) {
			return cb.or(
				cb.isNull(from.get(Immunization.START_DATE)),
				cb.and(cb.isNotNull(from.get(Immunization.START_DATE)), cb.lessThanOrEqualTo(from.get(Immunization.START_DATE), endDate)),
				cb.and(cb.isNull(from.get(Immunization.START_DATE)), cb.isNull(from.get(Immunization.END_DATE))));
		} else {
			return cb.conjunction();
		}
	}

	private Predicate buildCriteriaFilter(Long personId, Disease disease, ImmunizationQueryContext<Immunization> immunizationQueryContext) {
		final CriteriaBuilder cb = immunizationQueryContext.getCriteriaBuilder();
		final From<?, ?> from = immunizationQueryContext.getRoot();

		Predicate filter = cb.equal(from.get(Immunization.PERSON_ID), personId);
		if (disease != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Immunization.DISEASE), disease));
		}

		filter = CriteriaBuilderHelper.and(cb, filter, cb.isFalse(from.get(Immunization.DELETED)));

		return filter;
	}
}
