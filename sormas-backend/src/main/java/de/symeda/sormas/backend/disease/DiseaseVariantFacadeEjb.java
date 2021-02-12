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

package de.symeda.sormas.backend.disease;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.disease.DiseaseVariantDto;
import de.symeda.sormas.api.disease.DiseaseVariantFacade;
import de.symeda.sormas.api.disease.DiseaseVariantReferenceDto;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "DiseaseVariantFacade")
public class DiseaseVariantFacadeEjb implements DiseaseVariantFacade {

	@EJB
	private DiseaseVariantService diseaseVariantService;

	@Override
	public List<DiseaseVariantDto> getAllAfter(Date date) {
		return diseaseVariantService.getAllAfter(date, null).stream().map(d -> toDto(d)).collect(Collectors.toList());
	}

	@Override
	public List<DiseaseVariantDto> getByUuids(List<String> uuids) {
		return diseaseVariantService.getByUuids(uuids).stream().map(d -> toDto(d)).collect(Collectors.toList());
	}

	@Override
	public List<String> getAllUuids() {
		return diseaseVariantService.getAllUuids();
	}

	@Override
	public List<DiseaseVariantReferenceDto> getAll() {
		return diseaseVariantService.getAll(DiseaseVariant.NAME, true)
			.stream()
			.map(DiseaseVariantFacadeEjb::toReferenceDto)
			.collect(Collectors.toList());
	}

	@Override
	public List<DiseaseVariantReferenceDto> getAllByDisease(Disease disease) {
		return diseaseVariantService.getAllByDisease(disease).stream().map(DiseaseVariantFacadeEjb::toReferenceDto).collect(Collectors.toList());
	}

	public static DiseaseVariantDto toDto(DiseaseVariant source) {

		if (source == null) {
			return null;
		}

		DiseaseVariantDto target = new DiseaseVariantDto();
		DtoHelper.fillDto(target, source);

		target.setDisease(source.getDisease());
		target.setName(source.getName());

		return target;
	}

	public static DiseaseVariantReferenceDto toReferenceDto(DiseaseVariant source) {

		if (source == null) {
			return null;
		}

		return new DiseaseVariantReferenceDto(source.getUuid(), source.getName());
	}

	public DiseaseVariant fromDto(@NotNull DiseaseVariantDto source, boolean checkChangeDate) {

		DiseaseVariant target = diseaseVariantService.getByUuid(source.getUuid());
		if (target == null) {
			target = new DiseaseVariant();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		DtoHelper.validateDto(source, target, checkChangeDate);

		target.setDisease(source.getDisease());
		target.setName(source.getName());

		return target;
	}

	@LocalBean
	@Stateless
	public static class DiseaseVariantFacadeEjbLocal extends DiseaseVariantFacadeEjb {

	}
}
