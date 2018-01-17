package de.symeda.sormas.backend.outbreak;

import java.util.Date;
import java.util.List;
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
import de.symeda.sormas.backend.region.DistrictFacadeEjb;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "OutbreakFacade")
public class OutbreakFacadeEjb implements OutbreakFacade {

	@EJB
	private OutbreakService outbreakService;
	@EJB
	private RegionService regionService;
	@EJB
	private UserService userService;

	@Override
	public List<OutbreakDto> getAllAfter(Date date) {
		return outbreakService.getAllAfter(date, null).stream()
			.map(OutbreakFacadeEjb::toDto)
			.collect(Collectors.toList());
	}
	
	@Override
	public List<OutbreakDto> getAllByRegionAndDisease(RegionReferenceDto regionRef, Disease disease) {
		
		List<Outbreak> result = outbreakService.queryByCriteria(new OutbreakCriteria()
				.districtIsInRegion(regionRef).diseaseEquals(disease), null, Outbreak.DISTRICT, true);
		
		return result.stream()
				.map(OutbreakFacadeEjb::toDto)
				.collect(Collectors.toList());
	}
	
	@Override
	public boolean hasOutbreak(DistrictReferenceDto district, Disease disease) {
//		Long count = outbreakService.countByCriteria(new OutbreakCriteria()
//				.districtEquals(district).diseaseEquals(disease), null);
//		return count > 0;
		return true;
	}
	
	public static OutbreakDto toDto(Outbreak source) {
		if (source == null) {
			return null;
		}
		OutbreakDto target = new OutbreakDto();
		DtoHelper.fillDto(target, source);
		
		target.setDistrict(DistrictFacadeEjb.toReferenceDto(source.getDistrict()));
		target.setDisease(source.getDisease());
		target.setReportingUser(UserFacadeEjb.toReferenceDto(source.getReportingUser()));
		target.setReportDate(source.getReportDate());

		return target;
	}	
	
	@LocalBean
	@Stateless
	public static class OutbreakFacadeEjbLocal extends OutbreakFacadeEjb	 {
	}
}
