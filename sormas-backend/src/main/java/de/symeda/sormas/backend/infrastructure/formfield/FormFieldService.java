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

package de.symeda.sormas.backend.infrastructure.formfield;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.FormType;
import de.symeda.sormas.api.infrastructure.fields.FormFieldsCriteria;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.infrastructure.AbstractInfrastructureAdoService;
import de.symeda.sormas.backend.user.UserService;

@Stateless
@LocalBean
public class FormFieldService extends AbstractInfrastructureAdoService<FormField, FormFieldsCriteria> {

	@EJB
	private UserService userService;

	public FormFieldService() {
		super(FormField.class);
	}

	@Override
	public Predicate buildCriteriaFilter(FormFieldsCriteria criteria, CriteriaBuilder cb, Root<FormField> from) {
		Predicate filter = null;

		if (criteria.getFormType() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(FormField.FORM_TYPE), criteria.getFormType()));
		}

		if (criteria.getRelevanceStatus() != null) {
			if (criteria.getRelevanceStatus() == EntityRelevanceStatus.ACTIVE) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.or(
					cb.equal(from.get(FormField.ARCHIVED), false),
					cb.isNull(from.get(FormField.ARCHIVED))));
			} else if (criteria.getRelevanceStatus() == EntityRelevanceStatus.ARCHIVED) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(FormField.ARCHIVED), true));
			}
		}

		return filter;
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, FormField> from) {
		// no filter by user needed
		return null;
	}

	@Override
	public List<FormField> getByExternalId(String externalId, boolean includeArchived) {
		return null;
	}
}

