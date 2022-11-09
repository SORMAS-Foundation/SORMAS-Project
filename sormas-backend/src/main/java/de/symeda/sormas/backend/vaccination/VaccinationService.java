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

package de.symeda.sormas.backend.vaccination;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import de.symeda.sormas.backend.common.BaseAdoService;

@Stateless
@LocalBean
public class VaccinationService extends BaseAdoService<Vaccination> {

	public VaccinationService() {
		super(Vaccination.class);
	}

	public Map<String, String> getLastVaccinationType() {
		Map<String, String> result = new HashMap<>();
		String queryString =
			"select v.immunization_id, vaccinetype from vaccination v inner join (select immunization_id, max(vaccinationdate) maxdate from vaccination group by immunization_id) maxdates on v.immunization_id=maxdates.immunization_id and v.vaccinationdate=maxdates.maxdate";
		Query query = em.createNativeQuery(queryString);
		((Stream<Object[]>) query.getResultStream()).forEach(item -> result.put(((Number) item[0]).toString(), (String) item[1]));
		return result;
	}
}
