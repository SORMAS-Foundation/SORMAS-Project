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

package de.symeda.sormas.backend.selfdeclaration;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.backend.common.AbstractCoreAdoService;

@Stateless
@LocalBean
public class SelfDeclarationService extends AbstractCoreAdoService<SelfDeclaration, SelfDeclarationJoins> {

	public SelfDeclarationService() {
		super(SelfDeclaration.class, DeletableEntityType.SELF_DECLARATION);
	}

	@Override
	protected Predicate createUserFilterInternal(CriteriaBuilder cb, CriteriaQuery cq, From<?, SelfDeclaration> from) {
		return null;
	}

	@Override
	protected SelfDeclarationJoins toJoins(From<?, SelfDeclaration> adoPath) {
		return new SelfDeclarationJoins(adoPath);
	}

	@Override
	public Predicate inJurisdictionOrOwned(CriteriaBuilder cb, CriteriaQuery<?> query, From<?, SelfDeclaration> from) {
		return cb.conjunction();
	}
}
