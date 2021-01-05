/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.exposure;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.backend.common.BaseAdoService;

@Stateless
@LocalBean
public class ExposureService extends BaseAdoService<Exposure> {

	public ExposureService() {
		super(Exposure.class);
	}

	public void removeContactFromExposures(Long contactId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<Exposure> cu = cb.createCriteriaUpdate(getElementClass());
		Root<Exposure> root = cu.from(getElementClass());

		cu.where(cb.equal(root.get(Exposure.CONTACT_TO_CASE), contactId));
		cu.set(Exposure.CONTACT_TO_CASE, null);

		em.createQuery(cu).executeUpdate();
	}

}
