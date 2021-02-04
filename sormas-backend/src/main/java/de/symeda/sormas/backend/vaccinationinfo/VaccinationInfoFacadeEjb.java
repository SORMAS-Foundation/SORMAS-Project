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

package de.symeda.sormas.backend.vaccinationinfo;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.vaccinationinfo.VaccinationInfoDto;
import de.symeda.sormas.api.vaccinationinfo.VaccinationInfoFacade;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "VaccinationInfoFacade")
public class VaccinationInfoFacadeEjb implements VaccinationInfoFacade {

	@EJB
	private VaccinationInfoService service;

	public static VaccinationInfoDto toDto(VaccinationInfo source) {
		if (source == null) {
			return VaccinationInfoDto.build();
		}

		VaccinationInfoDto target = new VaccinationInfoDto();

		DtoHelper.fillDto(target, source);

		target.setVaccination(source.getVaccination());
		target.setVaccinationDoses(source.getVaccinationDoses());
		target.setVaccinationInfoSource(source.getVaccinationInfoSource());
		target.setFirstVaccinationDate(source.getFirstVaccinationDate());
		target.setLastVaccinationDate(source.getLastVaccinationDate());
		target.setVaccineName(source.getVaccineName());
		target.setOtherVaccineName(source.getOtherVaccineName());
		target.setVaccineManufacturer(source.getVaccineManufacturer());
		target.setOtherVaccineManufacturer(source.getOtherVaccineManufacturer());
		target.setVaccineInn(source.getVaccineInn());
		target.setVaccineBatchNumber(source.getVaccineBatchNumber());
		target.setVaccineUniiCode(source.getVaccineUniiCode());
		target.setVaccineAtcCode(source.getVaccineAtcCode());

		return target;
	}

	public VaccinationInfo fromDto(VaccinationInfoDto source, boolean checkChangeDate) {
		if (source == null) {
			return null;
		}

		String uuid = source.getUuid();
		VaccinationInfo target =
			DtoHelper.fillOrBuildEntity(source, uuid != null ? service.getByUuid(uuid) : null, VaccinationInfo::new, checkChangeDate);

		target.setVaccination(source.getVaccination());
		target.setVaccinationDoses(source.getVaccinationDoses());
		target.setVaccinationInfoSource(source.getVaccinationInfoSource());
		target.setFirstVaccinationDate(source.getFirstVaccinationDate());
		target.setLastVaccinationDate(source.getLastVaccinationDate());
		target.setVaccineName(source.getVaccineName());
		target.setOtherVaccineName(source.getOtherVaccineName());
		target.setVaccineManufacturer(source.getVaccineManufacturer());
		target.setOtherVaccineManufacturer(source.getOtherVaccineManufacturer());
		target.setVaccineInn(source.getVaccineInn());
		target.setVaccineBatchNumber(source.getVaccineBatchNumber());
		target.setVaccineUniiCode(source.getVaccineUniiCode());
		target.setVaccineAtcCode(source.getVaccineAtcCode());

		return target;
	}

	@LocalBean
	@Stateless
	public static class VaccinationInfoFacadeEjbLocal extends VaccinationInfoFacadeEjb {

	}
}
