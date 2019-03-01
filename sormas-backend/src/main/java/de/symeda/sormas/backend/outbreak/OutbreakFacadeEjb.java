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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.outbreak.DashboardOutbreakDto;
import de.symeda.sormas.api.outbreak.OutbreakCriteria;
import de.symeda.sormas.api.outbreak.OutbreakDto;
import de.symeda.sormas.api.outbreak.OutbreakFacade;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.DistrictFacadeEjb;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "OutbreakFacade")
public class OutbreakFacadeEjb implements OutbreakFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	protected EntityManager em;
	@EJB
	private OutbreakService outbreakService;
	@EJB
	private RegionService regionService;
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
	public List<OutbreakDto> getActive() {

		List<Outbreak> result = outbreakService.queryByCriteria(new OutbreakCriteria().active(true), null,
				Outbreak.DISEASE, true);

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
	
	@Override
	public List<DashboardOutbreakDto> getOutbreaksForDashboard(
			RegionReferenceDto regionRef,
			DistrictReferenceDto districtRef, 
			Disease disease,
			Date activeLower, 
			Date activeUpper, 
			String userUuid) {
		
		OutbreakCriteria outbreakCriteria = new OutbreakCriteria();
		outbreakCriteria.disease(disease).region(regionRef).district(districtRef)
			.active(true, activeLower, activeUpper);
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DashboardOutbreakDto> cq = cb.createQuery(DashboardOutbreakDto.class);

		Root<Outbreak> from = cq.from(Outbreak.class);
		Join<Outbreak, District> outbreakDistrict = from.join(Outbreak.DISTRICT, JoinType.LEFT);
		
		Predicate filter = outbreakService.buildCriteriaFilter(outbreakCriteria, cb, from);
		if (filter != null) {
			cq.where(filter);
		}
		
		cq.multiselect(from.get(Outbreak.DISEASE), outbreakDistrict.get(District.UUID));		
		return em.createQuery(cq).getResultList();
	}
	
	public Map<Disease, Long> getOutbreakCountByDisease (RegionReferenceDto regionRef, DistrictReferenceDto districtRef, Date from, Date to) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Outbreak> outbreak = cq.from(Outbreak.class);
		cq.multiselect(outbreak.get(Outbreak.DISEASE), cb.countDistinct(outbreak.get(Outbreak.DISTRICT)));
		cq.groupBy(outbreak.get(Outbreak.DISEASE));
		
		Predicate filter = null;
		if (from != null || to != null) {
			filter = AbstractAdoService.and(cb, filter, cb.between(outbreak.get(Outbreak.REPORT_DATE), from, to));
		}
		if (districtRef != null) {
			filter = AbstractAdoService.and(cb, filter, cb.equal(outbreak.join(Outbreak.DISTRICT, JoinType.LEFT).get(District.UUID), districtRef.getUuid()));
		}
		else if (regionRef != null) {
			filter = AbstractAdoService.and(cb, filter, cb.equal(outbreak.join(Outbreak.DISTRICT, JoinType.LEFT).join(District.REGION, JoinType.LEFT).get(Region.UUID), regionRef.getUuid()));
		}
		if (filter != null) {
			cq.where(filter);
		}
		
		List<Object[]> results = em.createQuery(cq).getResultList();
		
		Map<Disease, Long> outbreaks = results.stream().collect(
				Collectors.toMap(e -> (Disease) e[0], e -> (Long) e[1]));
		
		return outbreaks;
	}
	
	
	@LocalBean
	@Stateless
	public static class OutbreakFacadeEjbLocal extends OutbreakFacadeEjb {
	}
}
