/*
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
			return DrugSusceptibilityDto.build();
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
		target.setCeftriaxoneMic(source.getCeftriaxoneMic());
		target.setCeftriaxoneSusceptibility(source.getCeftriaxoneSusceptibility());
		target.setPenicillinMic(source.getPenicillinMic());
		target.setPenicillinSusceptibility(source.getPenicillinSusceptibility());
		target.setErythromycinMic(source.getErythromycinMic());
		target.setErythromycinSusceptibility(source.getErythromycinSusceptibility());
		target.setAmikacinMethod(source.getAmikacinMethod());
		target.setBedaquilineMethod(source.getBedaquilineMethod());
		target.setCapreomycinMethod(source.getCapreomycinMethod());
		target.setCiprofloxacinMethod(source.getCiprofloxacinMethod());
		target.setDelamanidMethod(source.getDelamanidMethod());
		target.setEthambutolMethod(source.getEthambutolMethod());
		target.setGatifloxacinMethod(source.getGatifloxacinMethod());
		target.setIsoniazidMethod(source.getIsoniazidMethod());
		target.setKanamycinMethod(source.getKanamycinMethod());
		target.setLevofloxacinMethod(source.getLevofloxacinMethod());
		target.setMoxifloxacinMethod(source.getMoxifloxacinMethod());
		target.setOfloxacinMethod(source.getOfloxacinMethod());
		target.setRifampicinMethod(source.getRifampicinMethod());
		target.setStreptomycinMethod(source.getStreptomycinMethod());
		target.setCeftriaxoneMethod(source.getCeftriaxoneMethod());
		target.setPenicillinMethod(source.getPenicillinMethod());
		target.setErythromycinMethod(source.getErythromycinMethod());
		target.setAzithromycinMic(source.getAzithromycinMic());
		target.setAzithromycinSusceptibility(source.getAzithromycinSusceptibility());
		target.setCeftazidimeMic(source.getCeftazidimeMic());
		target.setCeftazidimeSusceptibility(source.getCeftazidimeSusceptibility());
		target.setCefotaximeMic(source.getCefotaximeMic());
		target.setCefotaximeSusceptibility(source.getCefotaximeSusceptibility());
		target.setAmpicillinMic(source.getAmpicillinMic());
		target.setAmpicillinSusceptibility(source.getAmpicillinSusceptibility());
		target.setTrimethoprimSulfamethoxazoleMic(source.getTrimethoprimSulfamethoxazoleMic());
		target.setTrimethoprimSulfamethoxazoleSusceptibility(source.getTrimethoprimSulfamethoxazoleSusceptibility());
		target.setAzithromycinMethod(source.getAzithromycinMethod());
		target.setAmpicillinMethod(source.getAmpicillinMethod());
		target.setCeftazidimeMethod(source.getCeftazidimeMethod());
		target.setCefotaximeMethod(source.getCefotaximeMethod());
		target.setTrimethoprimSulfamethoxazoleMethod(source.getTrimethoprimSulfamethoxazoleMethod());

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
		target.setCeftriaxoneMic(source.getCeftriaxoneMic());
		target.setCeftriaxoneSusceptibility(source.getCeftriaxoneSusceptibility());
		target.setPenicillinMic(source.getPenicillinMic());
		target.setPenicillinSusceptibility(source.getPenicillinSusceptibility());
		target.setErythromycinMic(source.getErythromycinMic());
		target.setErythromycinSusceptibility(source.getErythromycinSusceptibility());
		target.setAmikacinMethod(source.getAmikacinMethod());
		target.setBedaquilineMethod(source.getBedaquilineMethod());
		target.setCapreomycinMethod(source.getCapreomycinMethod());
		target.setCiprofloxacinMethod(source.getCiprofloxacinMethod());
		target.setDelamanidMethod(source.getDelamanidMethod());
		target.setEthambutolMethod(source.getEthambutolMethod());
		target.setGatifloxacinMethod(source.getGatifloxacinMethod());
		target.setIsoniazidMethod(source.getIsoniazidMethod());
		target.setKanamycinMethod(source.getKanamycinMethod());
		target.setLevofloxacinMethod(source.getLevofloxacinMethod());
		target.setMoxifloxacinMethod(source.getMoxifloxacinMethod());
		target.setOfloxacinMethod(source.getOfloxacinMethod());
		target.setRifampicinMethod(source.getRifampicinMethod());
		target.setStreptomycinMethod(source.getStreptomycinMethod());
		target.setCeftriaxoneMethod(source.getCeftriaxoneMethod());
		target.setPenicillinMethod(source.getPenicillinMethod());
		target.setErythromycinMethod(source.getErythromycinMethod());
		target.setAzithromycinMic(source.getAzithromycinMic());
		target.setAzithromycinSusceptibility(source.getAzithromycinSusceptibility());
		target.setCeftazidimeMic(source.getCeftazidimeMic());
		target.setCeftazidimeSusceptibility(source.getCeftazidimeSusceptibility());
		target.setCefotaximeMic(source.getCefotaximeMic());
		target.setCefotaximeSusceptibility(source.getCefotaximeSusceptibility());
		target.setAmpicillinMic(source.getAmpicillinMic());
		target.setAmpicillinSusceptibility(source.getAmpicillinSusceptibility());
		target.setTrimethoprimSulfamethoxazoleMic(source.getTrimethoprimSulfamethoxazoleMic());
		target.setTrimethoprimSulfamethoxazoleSusceptibility(source.getTrimethoprimSulfamethoxazoleSusceptibility());
		target.setAzithromycinMethod(source.getAzithromycinMethod());
		target.setAmpicillinMethod(source.getAmpicillinMethod());
		target.setCeftazidimeMethod(source.getCeftazidimeMethod());
		target.setCefotaximeMethod(source.getCefotaximeMethod());
		target.setTrimethoprimSulfamethoxazoleMethod(source.getTrimethoprimSulfamethoxazoleMethod());

		return target;
	}

	public static boolean hasData(DrugSusceptibilityDto dto) {
		if (dto == null) {
			return false;
		}

		return dto.getAmikacinMic() != null
			|| dto.getAmikacinSusceptibility() != null
			|| dto.getBedaquilineMic() != null
			|| dto.getBedaquilineSusceptibility() != null
			|| dto.getCapreomycinMic() != null
			|| dto.getCapreomycinSusceptibility() != null
			|| dto.getCiprofloxacinMic() != null
			|| dto.getCiprofloxacinSusceptibility() != null
			|| dto.getDelamanidMic() != null
			|| dto.getDelamanidSusceptibility() != null
			|| dto.getEthambutolMic() != null
			|| dto.getEthambutolSusceptibility() != null
			|| dto.getGatifloxacinMic() != null
			|| dto.getGatifloxacinSusceptibility() != null
			|| dto.getIsoniazidMic() != null
			|| dto.getIsoniazidSusceptibility() != null
			|| dto.getKanamycinMic() != null
			|| dto.getKanamycinSusceptibility() != null
			|| dto.getLevofloxacinMic() != null
			|| dto.getLevofloxacinSusceptibility() != null
			|| dto.getMoxifloxacinMic() != null
			|| dto.getMoxifloxacinSusceptibility() != null
			|| dto.getOfloxacinMic() != null
			|| dto.getOfloxacinSusceptibility() != null
			|| dto.getRifampicinMic() != null
			|| dto.getRifampicinSusceptibility() != null
			|| dto.getStreptomycinMic() != null
			|| dto.getStreptomycinSusceptibility() != null
			|| dto.getCeftriaxoneMic() != null
			|| dto.getCeftriaxoneSusceptibility() != null
			|| dto.getPenicillinMic() != null
			|| dto.getPenicillinSusceptibility() != null
			|| dto.getErythromycinMic() != null
			|| dto.getErythromycinSusceptibility() != null
			|| dto.getAmikacinMethod() != null
			|| dto.getBedaquilineMethod() != null
			|| dto.getCapreomycinMethod() != null
			|| dto.getCiprofloxacinMethod() != null
			|| dto.getDelamanidMethod() != null
			|| dto.getEthambutolMethod() != null
			|| dto.getGatifloxacinMethod() != null
			|| dto.getIsoniazidMethod() != null
			|| dto.getKanamycinMethod() != null
			|| dto.getLevofloxacinMethod() != null
			|| dto.getMoxifloxacinMethod() != null
			|| dto.getOfloxacinMethod() != null
			|| dto.getRifampicinMethod() != null
			|| dto.getStreptomycinMethod() != null
			|| dto.getCeftriaxoneMethod() != null
			|| dto.getPenicillinMethod() != null
			|| dto.getErythromycinMethod() != null
			|| dto.getErythromycinSusceptibility() != null
			|| dto.getAzithromycinMic() != null
			|| dto.getAzithromycinSusceptibility() != null
			|| dto.getAzithromycinMethod() != null
			|| dto.getCeftazidimeMic() != null
			|| dto.getCeftazidimeSusceptibility() != null
			|| dto.getCeftazidimeMethod() != null
			|| dto.getCefotaximeMic() != null
			|| dto.getCefotaximeSusceptibility() != null
			|| dto.getCefotaximeMethod() != null
			|| dto.getAmpicillinMic() != null
			|| dto.getAmpicillinSusceptibility() != null
			|| dto.getAmpicillinMethod() != null
			|| dto.getTrimethoprimSulfamethoxazoleMic() != null
			|| dto.getTrimethoprimSulfamethoxazoleSusceptibility() != null
			|| dto.getTrimethoprimSulfamethoxazoleMethod() != null;
	}
}
