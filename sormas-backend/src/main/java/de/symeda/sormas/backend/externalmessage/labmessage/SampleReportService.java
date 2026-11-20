/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.externalmessage.labmessage;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.backend.common.BaseAdoService;
import de.symeda.sormas.backend.common.DeletableAdo;
import de.symeda.sormas.backend.sample.Sample;

@Stateless
@LocalBean
public class SampleReportService extends BaseAdoService<SampleReport> {

	@EJB
	private TestReportService testReportService;

	public SampleReportService() {
		super(SampleReport.class);
	}

	/**
	 * Creates a default filter that should be used as the basis of queries in this service.
	 * This essentially removes {@link DeletableAdo#deleted} test reports from the queries.
	 */
	public Predicate createDefaultFilter(CriteriaBuilder cb, Root<TestReport> root) {
		return cb.isFalse(root.join(SampleReport.LAB_MESSAGE, JoinType.LEFT).get(DeletableAdo.DELETED));
	}

	@Override
	public boolean deletePermanent(SampleReport sampleReport) {

		Optional.ofNullable(sampleReport.getTestReports()).ifPresent(testReports -> testReports.forEach(t -> testReportService.deletePermanent(t)));

		super.deletePermanent(sampleReport);
		return false;
	}

	public void unlinkSample(Sample sample) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<SampleReport> cu = cb.createCriteriaUpdate(SampleReport.class);
		Root<SampleReport> root = cu.from(SampleReport.class);

		cu.set(SampleReport.CHANGE_DATE, Timestamp.from(Instant.now()));
		cu.set(root.get(SampleReport.SAMPLE), (Sample) null);
		cu.where(cb.equal(root.get(SampleReport.SAMPLE), sample));

		em.createQuery(cu).executeUpdate();
	}
}
