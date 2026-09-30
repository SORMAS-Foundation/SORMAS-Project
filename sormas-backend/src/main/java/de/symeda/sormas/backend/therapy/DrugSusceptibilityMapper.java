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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.therapy.DrugSusceptibilityDto;
import de.symeda.sormas.backend.util.DtoHelper;

@LocalBean
@Stateless(name = "DrugSusceptibilityMapper")
public class DrugSusceptibilityMapper {

	private static final List<Field> DRUG_RESULT_FIELDS = Arrays.stream(DrugSusceptibilityDto.class.getDeclaredFields())
		.filter(field -> !field.isSynthetic() && !Modifier.isStatic(field.getModifiers()))
		.map(DrugSusceptibilityMapper::accessible)
		.collect(Collectors.toList());

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
		target.setCefiximeMic(source.getCefiximeMic());
		target.setCefiximeSusceptibility(source.getCefiximeSusceptibility());
		target.setCefiximeMethod(source.getCefiximeMethod());
		target.setTetracyclineMic(source.getTetracyclineMic());
		target.setTetracyclineSusceptibility(source.getTetracyclineSusceptibility());
		target.setTetracyclineMethod(source.getTetracyclineMethod());
		target.setGentamicinMic(source.getGentamicinMic());
		target.setGentamicinSusceptibility(source.getGentamicinSusceptibility());
		target.setGentamicinMethod(source.getGentamicinMethod());
		target.setSpectinomycinMic(source.getSpectinomycinMic());
		target.setSpectinomycinSusceptibility(source.getSpectinomycinSusceptibility());
		target.setSpectinomycinMethod(source.getSpectinomycinMethod());

		target.setClindamycinMethod(source.getClindamycinMethod());
		target.setClindamycinMic(source.getClindamycinMic());
		target.setClindamycinSusceptibility(source.getClindamycinSusceptibility());

		target.setLinezolidMethod(source.getLinezolidMethod());
		target.setLinezolidMic(source.getLinezolidMic());
		target.setLinezolidSusceptibility(source.getLinezolidSusceptibility());

		target.setMeropenemMethod(source.getMeropenemMethod());
		target.setMeropenemMic(source.getMeropenemMic());
		target.setMeropenemSusceptibility(source.getMeropenemSusceptibility());

		target.setTetracyclinesMethod(source.getTetracyclinesMethod());
		target.setTetracyclinesMic(source.getTetracyclinesMic());
		target.setTetracyclinesSusceptibility(source.getTetracyclinesSusceptibility());

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
		target.setCefiximeMic(source.getCefiximeMic());
		target.setCefiximeSusceptibility(source.getCefiximeSusceptibility());
		target.setCefiximeMethod(source.getCefiximeMethod());
		target.setTetracyclineMic(source.getTetracyclineMic());
		target.setTetracyclineSusceptibility(source.getTetracyclineSusceptibility());
		target.setTetracyclineMethod(source.getTetracyclineMethod());
		target.setGentamicinMic(source.getGentamicinMic());
		target.setGentamicinSusceptibility(source.getGentamicinSusceptibility());
		target.setGentamicinMethod(source.getGentamicinMethod());
		target.setSpectinomycinMic(source.getSpectinomycinMic());
		target.setSpectinomycinSusceptibility(source.getSpectinomycinSusceptibility());
		target.setSpectinomycinMethod(source.getSpectinomycinMethod());

		target.setClindamycinMethod(source.getClindamycinMethod());
		target.setClindamycinMic(source.getClindamycinMic());
		target.setClindamycinSusceptibility(source.getClindamycinSusceptibility());

		target.setLinezolidMethod(source.getLinezolidMethod());
		target.setLinezolidMic(source.getLinezolidMic());
		target.setLinezolidSusceptibility(source.getLinezolidSusceptibility());

		target.setMeropenemMethod(source.getMeropenemMethod());
		target.setMeropenemMic(source.getMeropenemMic());
		target.setMeropenemSusceptibility(source.getMeropenemSusceptibility());

		target.setTetracyclinesMethod(source.getTetracyclinesMethod());
		target.setTetracyclinesMic(source.getTetracyclinesMic());
		target.setTetracyclinesSusceptibility(source.getTetracyclinesSusceptibility());

		return target;
	}

	public static boolean hasData(DrugSusceptibilityDto dto) {
		return dto != null && DRUG_RESULT_FIELDS.stream().anyMatch(field -> readField(field, dto) != null);
	}

	private static Field accessible(Field field) {
		field.setAccessible(true);
		return field;
	}

	private static Object readField(Field field, DrugSusceptibilityDto dto) {
		try {
			return field.get(dto);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}

}
