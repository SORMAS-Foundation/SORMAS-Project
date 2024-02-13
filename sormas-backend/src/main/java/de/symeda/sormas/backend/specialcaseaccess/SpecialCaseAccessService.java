/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.specialcaseaccess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.IsCase;
import de.symeda.sormas.api.immunization.IsImmunization;
import de.symeda.sormas.api.manualmessagelog.ManualMessageLogIndexDto;
import de.symeda.sormas.api.sample.IsSample;
import de.symeda.sormas.api.task.IsTask;
import de.symeda.sormas.api.travelentry.IsTravelEntry;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.uuid.HasUuid;
import de.symeda.sormas.api.visit.IsVisit;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.surveillancereport.SurveillanceReport;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.BaseAdoService;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.manualmessagelog.ManualMessageLog;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.sample.PathogenTest;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.task.Task;
import de.symeda.sormas.backend.travelentry.TravelEntry;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.IterableHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.vaccination.Vaccination;
import de.symeda.sormas.backend.visit.Visit;

@Stateless
@LocalBean
public class SpecialCaseAccessService extends BaseAdoService<SpecialCaseAccess> {

	public SpecialCaseAccessService() {
		super(SpecialCaseAccess.class);
	}

	public Collection<SpecialCaseAccess> getAllActiveByCase(CaseReferenceDto caze) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SpecialCaseAccess> cq = cb.createQuery(getElementClass());
		Root<SpecialCaseAccess> from = cq.from(getElementClass());

		cq.where(
			cb.equal(from.join(SpecialCaseAccess.CAZE, JoinType.LEFT).get(Case.UUID), caze.getUuid()),
			cb.greaterThanOrEqualTo(from.get(SpecialCaseAccess.END_DATE_TIME), new Date()));
		cq.orderBy(cb.desc(from.get(SpecialCaseAccess.END_DATE_TIME)));

		return em.createQuery(cq).getResultList();
	}

	public boolean isAnyAssignedToUser(List<CaseReferenceDto> cases, UserReferenceDto user) {
		return IterableHelper.anyBatch(
			cases,
			ModelConstants.PARAMETER_LIMIT,
			batchedCases -> exists(
				(cb, from, cq) -> cb.and(
					from.join(SpecialCaseAccess.CAZE, JoinType.LEFT)
						.get(Case.UUID)
						.in(batchedCases.stream().map(CaseReferenceDto::getUuid).collect(Collectors.toList())),
					cb.equal(from.join(SpecialCaseAccess.ASSIGNED_TO, JoinType.LEFT).get(User.UUID), user.getUuid()),
					cb.greaterThanOrEqualTo(from.get(SpecialCaseAccess.END_DATE_TIME), new Date()))));
	}

	public void deleteByCaseAndAssignee(CaseReferenceDto caze, UserReferenceDto assignedTo) {
		getByPredicate(
			(cb, from, cq) -> cb.and(
				cb.equal(from.join(SpecialCaseAccess.CAZE, JoinType.LEFT).get(Case.UUID), caze.getUuid()),
				cb.equal(from.join(SpecialCaseAccess.ASSIGNED_TO, JoinType.LEFT).get(User.UUID), assignedTo.getUuid())))
			.forEach(this::deletePermanent);
	}

	public <T extends IsCase> List<String> getCaseUuidsWithSpecialAccess(Collection<T> cases) {
		return getUuidsWithSpecialAccess(Case.class, r -> r, cases);
	}

	public List<String> getSurveillanceReportUuidsWithSpecialAccess(Collection<SurveillanceReport> cases) {
		return getUuidsWithSpecialAccess(SurveillanceReport.class, r -> r.join(SurveillanceReport.CAZE), cases);
	}

	public List<String> getImmunizationUuidsWithSpecialAccess(Collection<? extends IsImmunization> immunizations) {
		return getUuidsWithSpecialAccess(Immunization.class, r -> r.join(Immunization.PERSON).join(Person.CASES), immunizations);
	}

	public List<String> getSampleUuidsWithSpecialAccess(Collection<? extends IsSample> samples) {
		return getUuidsWithSpecialAccess(Sample.class, r -> r.join(Sample.ASSOCIATED_CASE), samples);
	}

	public List<String> getPathogenTestUuidsWithSpecialAccess(Collection<? extends PathogenTest> tests) {
		return getUuidsWithSpecialAccess(PathogenTest.class, r -> r.join(PathogenTest.SAMPLE).join(Sample.ASSOCIATED_CASE), tests);
	}

	public List<String> getTaskUuidsWithSpecialAccess(Collection<? extends IsTask> tasks) {
		return getUuidsWithSpecialAccess(Task.class, r -> r.join(Task.CAZE), tasks);
	}

	public List<String> getTravelEntryUuidsWithSpecialAccess(Collection<? extends IsTravelEntry> entries) {
		return getUuidsWithSpecialAccess(TravelEntry.class, r -> r.join(TravelEntry.PERSON).join(Person.CASES), entries);
	}

	public List<String> getVaccinationUuidsWithSpecialAccess(List<Vaccination> vaccinations) {
		return getUuidsWithSpecialAccess(
			Vaccination.class,
			r -> r.join(Vaccination.IMMUNIZATION).join(Immunization.PERSON).join(Person.CASES),
			vaccinations);
	}

	public List<String> getVisitUuidsWithSpecialAccess(Collection<? extends IsVisit> visits) {
		return getUuidsWithSpecialAccess(Visit.class, r -> r.join(Visit.CAZE), visits);
	}

	public List<String> getManualMessageLogUuidsWithSpecialAccess(Collection<ManualMessageLogIndexDto> manualMessageLogs) {
		return getUuidsWithSpecialAccess(
			ManualMessageLog.class,
			r -> r.join(ManualMessageLog.RECIPIENT_PERSON).join(Person.CASES),
			manualMessageLogs);
	}

	private <T extends AbstractDomainObject> List<String> getUuidsWithSpecialAccess(
		Class<T> entityType,
		Function<Root<T>, From<?, Case>> joinCase,
		Collection<? extends HasUuid> entities) {

		if (entities.isEmpty()) {
			return Collections.emptyList();
		}

		List<String> result = new ArrayList<>(entities.size());

		IterableHelper.executeBatched(entities, ModelConstants.PARAMETER_LIMIT, batch -> {
			final CriteriaBuilder cb = em.getCriteriaBuilder();
			final CriteriaQuery<String> cq = cb.createQuery(String.class);
			final Root<T> from = cq.from(entityType);

			Join<Case, SpecialCaseAccess> specialCaseAccess = joinCase.apply(from).join(Case.SPECIAL_CASE_ACCESSES, JoinType.LEFT);

			cq.select(from.get(AbstractDomainObject.UUID));
			cq.where(
				cb.and(
					from.get(AbstractDomainObject.UUID).in(batch.stream().map(HasUuid::getUuid).collect(Collectors.toList())),
					createSpecialCaseAccessFilter(cb, specialCaseAccess)));

			result.addAll(em.createQuery(cq).getResultList());
		});

		return result;
	}

	public Predicate createSpecialCaseAccessFilter(CriteriaBuilder cb, From<?, SpecialCaseAccess> from) {
		return cb.and(
			cb.equal(from.get(SpecialCaseAccess.ASSIGNED_TO), getCurrentUser()),
			cb.greaterThanOrEqualTo(from.get(SpecialCaseAccess.END_DATE_TIME), new Date()));
	}
}
