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

package de.symeda.sormas.backend.therapy;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.therapy.DrugSusceptibilityDto;
import de.symeda.sormas.backend.util.DtoHelper;

@LocalBean
@Stateless(name = "DrugSusceptibilityMapper")
public class DrugSusceptibilityMapper {

	public static DrugSusceptibilityDto toDto(DrugSusceptibility source) {

		if (source == null) {
			return null;
		}

		DrugSusceptibilityDto target = new DrugSusceptibilityDto();
		DtoHelper.fillDto(target, source);

		target.setAmikacinMic(source.getAmikacinMic());
		target.setAmikacinSusceptibility(source.getAmikacinSusceptibility());
		target.setBedaquilineMic(source.getBedaquilineMic());
		target.setBedaquilineSusceptibility(source.getBedaquilineSusceptibility());
		target.setCapreomycinMic(source.getCapreomycinMic());
		target.setCapreomycinSusceptibility(source.getCapreomycinSusceptibility());
		target.setCiprofloxacinMic(source.getCiprofloxacinMic());
		target.setCiprofloxacinSusceptibility(source.getCiprofloxacinSusceptibility());
		target.setDelamanidMic(source.getDelamanidMic());
		target.setDelamanidSusceptibility(source.getDelamanidSusceptibility());
		target.setEthambutolMic(source.getEthambutolMic());
		target.setEthambutolSusceptibility(source.getEthambutolSusceptibility());
		target.setGatifloxacinMic(source.getGatifloxacinMic());
		target.setGatifloxacinSusceptibility(source.getGatifloxacinSusceptibility());
		target.setIsoniazidMic(source.getIsoniazidMic());
		target.setIsoniazidSusceptibility(source.getIsoniazidSusceptibility());
		target.setKanamycinMic(source.getKanamycinMic());
		target.setKanamycinSusceptibility(source.getKanamycinSusceptibility());
		target.setLevofloxacinMic(source.getLevofloxacinMic());
		target.setLevofloxacinSusceptibility(source.getLevofloxacinSusceptibility());
		target.setMoxifloxacinMic(source.getMoxifloxacinMic());
		target.setMoxifloxacinSusceptibility(source.getMoxifloxacinSusceptibility());
		target.setOfloxacinMic(source.getOfloxacinMic());
		target.setOfloxacinSusceptibility(source.getOfloxacinSusceptibility());
		target.setRifampicinMic(source.getRifampicinMic());
		target.setRifampicinSusceptibility(source.getRifampicinSusceptibility());
		target.setStreptomycinMic(source.getStreptomycinMic());
		target.setStreptomycinSusceptibility(source.getStreptomycinSusceptibility());

		return target;
	}

	public DrugSusceptibility fillOrBuildEntity(@NotNull DrugSusceptibilityDto source, DrugSusceptibility target, boolean checkChangeDate) {
		if (source == null) {
			return null;
		}

		target = DtoHelper.fillOrBuildEntity(source, target, DrugSusceptibility::new, checkChangeDate);

		target.setAmikacinMic(source.getAmikacinMic());
		target.setAmikacinSusceptibility(source.getAmikacinSusceptibility());
		target.setBedaquilineMic(source.getBedaquilineMic());
		target.setBedaquilineSusceptibility(source.getBedaquilineSusceptibility());
		target.setCapreomycinMic(source.getCapreomycinMic());
		target.setCapreomycinSusceptibility(source.getCapreomycinSusceptibility());
		target.setCiprofloxacinMic(source.getCiprofloxacinMic());
		target.setCiprofloxacinSusceptibility(source.getCiprofloxacinSusceptibility());
		target.setDelamanidMic(source.getDelamanidMic());
		target.setDelamanidSusceptibility(source.getDelamanidSusceptibility());
		target.setEthambutolMic(source.getEthambutolMic());
		target.setEthambutolSusceptibility(source.getEthambutolSusceptibility());
		target.setGatifloxacinMic(source.getGatifloxacinMic());
		target.setGatifloxacinSusceptibility(source.getGatifloxacinSusceptibility());
		target.setIsoniazidMic(source.getIsoniazidMic());
		target.setIsoniazidSusceptibility(source.getIsoniazidSusceptibility());
		target.setKanamycinMic(source.getKanamycinMic());
		target.setKanamycinSusceptibility(source.getKanamycinSusceptibility());
		target.setLevofloxacinMic(source.getLevofloxacinMic());
		target.setLevofloxacinSusceptibility(source.getLevofloxacinSusceptibility());
		target.setMoxifloxacinMic(source.getMoxifloxacinMic());
		target.setMoxifloxacinSusceptibility(source.getMoxifloxacinSusceptibility());
		target.setOfloxacinMic(source.getOfloxacinMic());
		target.setOfloxacinSusceptibility(source.getOfloxacinSusceptibility());
		target.setRifampicinMic(source.getRifampicinMic());
		target.setRifampicinSusceptibility(source.getRifampicinSusceptibility());
		target.setStreptomycinMic(source.getStreptomycinMic());
		target.setStreptomycinSusceptibility(source.getStreptomycinSusceptibility());

		return target;
	}
}
