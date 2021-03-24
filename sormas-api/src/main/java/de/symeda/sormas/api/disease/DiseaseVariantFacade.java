/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 */

package de.symeda.sormas.api.disease;

import de.symeda.sormas.api.Disease;

import javax.ejb.Remote;
import java.util.Date;
import java.util.List;

@Remote
public interface DiseaseVariantFacade {

    List<DiseaseVariantDto> getAllAfter(Date date);

    List<DiseaseVariantDto> getByUuids(List<String> uuids);

    List<String> getAllUuids();

    List<DiseaseVariantReferenceDto> getAll();

    List<DiseaseVariantReferenceDto> getAllByDisease(Disease disease);

    List<DiseaseVariantReferenceDto> getByName(String name, Disease disease);
}
