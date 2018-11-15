/*
 * This file is part of SORMAS®.
 *
 * SORMAS® is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SORMAS® is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SORMAS®.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.rest;

import java.util.List;

import de.symeda.sormas.api.caze.classification.DiseaseClassificationCriteriaDto;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ClassificationFacadeRetro {

    @GET("classification/all")
    Call<List<DiseaseClassificationCriteriaDto>> pullAllClassificationCriteria();

}
