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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.epidata;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections.CollectionUtils;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.exposure.Exposure;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.util.IterableHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless
@LocalBean
public class EpiDataService extends AbstractAdoService<EpiData> {

	public EpiDataService() {
		super(EpiData.class);
	}

	public EpiData createEpiData() {

		EpiData epiData = new EpiData();
		epiData.setUuid(DataHelper.createUuid());
		return epiData;
	}

	public Map<String, String> getExposureSourceCaseNames(List<String> exposureUuids) {
		if (CollectionUtils.isEmpty(exposureUuids)) {
			return Collections.emptyMap();
		}

		Map<String, String> sourceCaseNameMap = new HashMap<>();

		IterableHelper.executeBatched(exposureUuids, ModelConstants.PARAMETER_LIMIT, batchedExposureUuids -> {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
			Root<Exposure> root = cq.from(Exposure.class);
			Join<Exposure, Contact> contactJoin = root.join(Exposure.CONTACT_TO_CASE);
			Join<Contact, Case> caseJoin = contactJoin.join(Contact.CAZE);
			Join<Case, Person> casePersonJoin = caseJoin.join(Case.PERSON);

			cq.where(root.get(AbstractDomainObject.UUID).in(batchedExposureUuids));
			cq.multiselect(root.get(AbstractDomainObject.UUID), casePersonJoin.get(Person.FIRST_NAME), casePersonJoin.get(Person.LAST_NAME));

			List<Object[]> resultList = em.createQuery(cq).getResultList();

			for (Object[] result : resultList) {
				sourceCaseNameMap.put((String) result[0], DataHelper.toStringNullable(result[1]) + " " + DataHelper.toStringNullable(result[2]));
			}
		});

		return sourceCaseNameMap;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, EpiData> from) {
		// A user should not directly query for this
		throw new UnsupportedOperationException();
	}

	@Override
	public Predicate createChangeDateFilter(CriteriaBuilder cb, From<?, EpiData> epiData, Timestamp date) {
		Predicate dateFilter = greaterThanAndNotNull(cb, epiData.get(AbstractDomainObject.CHANGE_DATE), date);

		Join<EpiData, Exposure> exposures = epiData.join(EpiData.EXPOSURES, JoinType.LEFT);
		dateFilter = cb.or(dateFilter, greaterThanAndNotNull(cb, exposures.get(AbstractDomainObject.CHANGE_DATE), date));
		dateFilter = cb
			.or(dateFilter, greaterThanAndNotNull(cb, exposures.join(Exposure.LOCATION, JoinType.LEFT).get(AbstractDomainObject.CHANGE_DATE), date));

		return dateFilter;
	}
}
