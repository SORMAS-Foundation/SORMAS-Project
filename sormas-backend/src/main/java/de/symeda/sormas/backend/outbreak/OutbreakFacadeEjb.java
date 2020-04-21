/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.outbreak;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.outbreak.OutbreakCriteria;
import de.symeda.sormas.api.outbreak.OutbreakDto;
import de.symeda.sormas.api.outbreak.OutbreakFacade;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.backend.region.DistrictFacadeEjb;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "OutbreakFacade")
public class OutbreakFacadeEjb implements OutbreakFacade {

	@EJB
	private OutbreakService outbreakService;
	@EJB
	private DistrictService districtService;
	@EJB
	private UserService userService;

	@Override
	public List<OutbreakDto> getActiveAfter(Date date) {
		List<Outbreak> result = outbreakService.queryByCriteria(
				new OutbreakCriteria().active(true).changeDateAfter(date), null, Outbreak.DISEASE, true);

		return result.stream().map(OutbreakFacadeEjb::toDto).collect(Collectors.toList());
	}

	@Override
	public List<String> getActiveUuidsAfter(Date date) {
		List<String> result = outbreakService
				.queryUuidByCriteria(new OutbreakCriteria().active(true).changeDateAfter(date), null, null, true);

		return result;
	}

	@Override
	public List<String> getInactiveUuidsAfter(Date date) {
		List<String> result = outbreakService
				.queryUuidByCriteria(new OutbreakCriteria().active(false).changeDateAfter(date), null, null, true);

		return result;
	}

	@Override
	public List<OutbreakDto> getActive(OutbreakCriteria criteria) {
		List<Outbreak> result = outbreakService.queryByCriteria(criteria, null, Outbreak.DISEASE, true);

		return result.stream().map(OutbreakFacadeEjb::toDto).collect(Collectors.toList());
	}

	@Override
	public List<OutbreakDto> getActiveByRegionAndDisease(RegionReferenceDto regionRef, Disease disease) {

		List<Outbreak> result = outbreakService.queryByCriteria(
				new OutbreakCriteria().region(regionRef).disease(disease).active(true), null,
				Outbreak.DISTRICT, true);

		return result.stream().map(OutbreakFacadeEjb::toDto).collect(Collectors.toList());
	}

	@Override
	public OutbreakDto getActiveByDistrictAndDisease(DistrictReferenceDto districtRef, Disease disease) {
		List<Outbreak> result = outbreakService.queryByCriteria(
				new OutbreakCriteria().district(districtRef).disease(disease).active(true), null,
				Outbreak.DISTRICT, true);

		return result.stream().map(OutbreakFacadeEjb::toDto).findFirst().orElse(null);
	}

	@Override
	public boolean hasOutbreak(DistrictReferenceDto district, Disease disease) {
		Long count = outbreakService.countByCriteria(
				new OutbreakCriteria().district(district).disease(disease).active(true), null);
		return count > 0;
	}

	@Override
	public OutbreakDto startOutbreak(DistrictReferenceDto district, Disease disease) {
		OutbreakDto outbreak = getActiveByDistrictAndDisease(district, disease);
		if (outbreak != null) {
			// there is already an active outbreak - return that one
			return outbreak;
		}

		UserReferenceDto reportingUser = UserFacadeEjb.toReferenceDto(userService.getCurrentUser());
		outbreak = OutbreakDto.build(district, disease, reportingUser);
		outbreak.setStartDate(new Date());
		return saveOutbreak(outbreak);
	}

	@Override
	public OutbreakDto endOutbreak(DistrictReferenceDto district, Disease disease) {
		OutbreakDto outbreak = getActiveByDistrictAndDisease(district, disease);
		if (outbreak != null) {
			outbreak.setEndDate(new Date());
			return saveOutbreak(outbreak);
		}
		return null;
	}

	@Override
	public OutbreakDto saveOutbreak(OutbreakDto outbreakDto) {
		Outbreak outbreak = fromDto(outbreakDto);
		outbreakService.ensurePersisted(outbreak);
		return toDto(outbreak);
	}

	@Override
	public void deleteOutbreak(OutbreakDto outbreakDto) {
		Outbreak outbreak = outbreakService.getByUuid(outbreakDto.getUuid());
		outbreakService.delete(outbreak);
	}

	public Outbreak fromDto(OutbreakDto source) {
		if (source == null) {
			return null;
		}

		Outbreak target = outbreakService.getByUuid(source.getUuid());
		if (target == null) {
			target = new Outbreak();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		DtoHelper.validateDto(source, target);

		target.setDistrict(districtService.getByReferenceDto(source.getDistrict()));
		target.setDisease(source.getDisease());
		target.setStartDate(source.getStartDate());
		target.setEndDate(source.getEndDate());
		target.setReportingUser(userService.getByReferenceDto(source.getReportingUser()));
		target.setReportDate(source.getReportDate());

		return target;
	}

	public static OutbreakDto toDto(Outbreak source) {
		if (source == null) {
			return null;
		}
		OutbreakDto target = new OutbreakDto();
		DtoHelper.fillDto(target, source);

		target.setDistrict(DistrictFacadeEjb.toReferenceDto(source.getDistrict()));
		target.setDisease(source.getDisease());
		target.setStartDate(source.getStartDate());
		target.setEndDate(source.getEndDate());
		target.setReportingUser(UserFacadeEjb.toReferenceDto(source.getReportingUser()));
		target.setReportDate(source.getReportDate());

		return target;
	}
	
	public Map<Disease, Long> getOutbreakDistrictCountByDisease (OutbreakCriteria criteria) {
		User user = userService.getCurrentUser();

		return outbreakService.getOutbreakDistrictCountByDisease(criteria, user);
	}
	
	@Override
	public Long getOutbreakDistrictCount (OutbreakCriteria criteria) {
		User user = userService.getCurrentUser();

		return outbreakService.getOutbreakDistrictCount(criteria, user);
	}
	
	
	@LocalBean
	@Stateless
	public static class OutbreakFacadeEjbLocal extends OutbreakFacadeEjb {
	}
}
