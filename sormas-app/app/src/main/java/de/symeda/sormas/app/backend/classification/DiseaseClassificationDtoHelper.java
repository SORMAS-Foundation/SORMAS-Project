/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.backend.classification;

import java.sql.SQLException;
import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.caze.classification.ClassificationHtmlRenderer;
import de.symeda.sormas.api.caze.classification.DiseaseClassificationCriteriaDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class DiseaseClassificationDtoHelper extends AdoDtoHelper<DiseaseClassificationCriteria, DiseaseClassificationCriteriaDto> {

	@Override
	protected Class<DiseaseClassificationCriteria> getAdoClass() {
		return DiseaseClassificationCriteria.class;
	}

	@Override
	protected Class<DiseaseClassificationCriteriaDto> getDtoClass() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Call<List<DiseaseClassificationCriteriaDto>> pullAllSince(long since) throws NoConnectionException {
		return RetroProvider.getClassificationFacade().pullAllSince(since);
	}

	@Override
	protected Call<List<DiseaseClassificationCriteriaDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is read-only");
	}

	@Override
	protected Call<List<PushResult>> pushAll(List<DiseaseClassificationCriteriaDto> dtos) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is read-only");
	}

	@Override
	public void fillInnerFromDto(DiseaseClassificationCriteria target, DiseaseClassificationCriteriaDto source) {
		target.setDisease(source.getDisease());
		target.setSuspectCriteria(ClassificationHtmlRenderer.createSuspectHtmlString(source));
		target.setProbableCriteria(ClassificationHtmlRenderer.createProbableHtmlString(source));
		target.setConfirmedCriteria(ClassificationHtmlRenderer.createConfirmedHtmlString(source));
	}

	@Override
	protected DiseaseClassificationCriteria handlePulledDto(AbstractAdoDao<DiseaseClassificationCriteria> dao, DiseaseClassificationCriteriaDto dto)
		throws DaoException, SQLException {

		// check if there already is an entry for this disease - the primary key is the disease, not the UUID
		List<DiseaseClassificationCriteria> existingCriteria = dao.queryForEq(DiseaseClassificationCriteria.DISEASE, dto.getDisease());
		if (!existingCriteria.isEmpty()) {
			dto.setUuid(existingCriteria.get(0).getUuid());
		} else {
			dto.setUuid(DataHelper.createUuid());
		}

		DiseaseClassificationCriteria source = fillOrCreateFromDto(null, dto);
		return dao.mergeOrCreate(source);
	}

	@Override
	public void fillInnerFromAdo(DiseaseClassificationCriteriaDto target, DiseaseClassificationCriteria source) {
		throw new UnsupportedOperationException("Entity is read-only");
	}
}
