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

package de.symeda.sormas.backend.infrastructure.formbuilder;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.infrastructure.forms.FormBuilderCriteria;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.infrastructure.AbstractInfrastructureAdoService;
import de.symeda.sormas.backend.user.UserService;

@Stateless
@LocalBean
public class FormBuilderService extends AbstractInfrastructureAdoService<FormBuilder, FormBuilderCriteria> {

	@EJB
	private UserService userService;

	public FormBuilderService() {
		super(FormBuilder.class);
	}

	@Override
	public List<FormBuilder> getByExternalId(String externalId, boolean includeArchived) {
		return null;
	}

	@Override
	public Predicate buildCriteriaFilter(FormBuilderCriteria criteria, CriteriaBuilder cb, Root<FormBuilder> from) {
		Predicate filter = null;

		if (criteria.getFormType() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(FormBuilder.FORM_TYPE), criteria.getFormType()));
		}

		if (criteria.getDisease() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(FormBuilder.DISEASE), criteria.getDisease()));
		}

		if (criteria.getActive() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(FormBuilder.ACTIVE), criteria.getActive()));
		}

		return filter;
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, FormBuilder> from) {
		// no filter by user needed
		return null;
	}

	public List<FormBuilder> getByFormTypeAndDisease(FormBuilderCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<FormBuilder> cq = cb.createQuery(FormBuilder.class);
		Root<FormBuilder> from = cq.from(FormBuilder.class);

		Predicate filter = buildCriteriaFilter(criteria, cb, from);
		if (filter != null) {
			cq.where(filter);
		}

		cq.orderBy(cb.asc(from.get(FormBuilder.FORM_TYPE)), cb.asc(from.get(FormBuilder.DISEASE)));

		return em.createQuery(cq).getResultList();
	}
}

