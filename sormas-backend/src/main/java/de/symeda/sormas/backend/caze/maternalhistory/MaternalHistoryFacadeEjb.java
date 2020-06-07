package de.symeda.sormas.backend.caze.maternalhistory;

import java.sql.Timestamp;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.caze.maternalhistory.MaternalHistoryDto;
import de.symeda.sormas.api.caze.maternalhistory.MaternalHistoryFacade;
import de.symeda.sormas.backend.region.CommunityFacadeEjb;
import de.symeda.sormas.backend.region.CommunityService;
import de.symeda.sormas.backend.region.DistrictFacadeEjb;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.RegionFacadeEjb;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "MaternalHistoryFacade")
public class MaternalHistoryFacadeEjb implements MaternalHistoryFacade {

	@EJB
	private MaternalHistoryService service;
	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private CommunityService communityService;

	public static MaternalHistoryDto toDto(MaternalHistory source) {
		if (source == null) {
			return null;
		}

		MaternalHistoryDto target = new MaternalHistoryDto();
		DtoHelper.fillDto(target, source);

		target.setAgeAtBirth(source.getAgeAtBirth());
		target.setArthralgiaArthritis(source.getArthralgiaArthritis());
		target.setArthralgiaArthritisOnset(source.getArthralgiaArthritisOnset());
		target.setArthralgiaArthritisMonth(source.getArthralgiaArthritisMonth());
		target.setChildrenNumber(source.getChildrenNumber());
		target.setConjunctivitis(source.getConjunctivitis());
		target.setConjunctivitisOnset(source.getConjunctivitisOnset());
		target.setConjunctivitisMonth(source.getConjunctivitisMonth());
		target.setMaculopapularRash(source.getMaculopapularRash());
		target.setMaculopapularRashOnset(source.getMaculopapularRashOnset());
		target.setMaculopapularRashMonth(source.getMaculopapularRashMonth());
		target.setRubella(source.getRubella());
		target.setRubellaOnset(source.getRubellaOnset());
		target.setSwollenLymphs(source.getSwollenLymphs());
		target.setSwollenLymphsOnset(source.getSwollenLymphsOnset());
		target.setSwollenLymphsMonth(source.getSwollenLymphsMonth());
		target.setRashExposure(source.getRashExposure());
		target.setRashExposureDate(source.getRashExposureDate());
		target.setRashExposureMonth(source.getRashExposureMonth());
		target.setRashExposureRegion(RegionFacadeEjb.toReferenceDto(source.getRashExposureRegion()));
		target.setRashExposureDistrict(DistrictFacadeEjb.toReferenceDto(source.getRashExposureDistrict()));
		target.setRashExposureCommunity(CommunityFacadeEjb.toReferenceDto(source.getRashExposureCommunity()));
		target.setOtherComplications(source.getOtherComplications());
		target.setOtherComplicationsOnset(source.getOtherComplicationsOnset());
		target.setOtherComplicationsMonth(source.getOtherComplicationsMonth());
		target.setOtherComplicationsDetails(source.getOtherComplicationsDetails());

		return target;
	}

	public MaternalHistory fromDto(@NotNull MaternalHistoryDto source) {
		MaternalHistory target = service.getByUuid(source.getUuid());

		if (target == null) {
			target = new MaternalHistory();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}

		DtoHelper.validateDto(source, target);

		target.setAgeAtBirth(source.getAgeAtBirth());
		target.setArthralgiaArthritis(source.getArthralgiaArthritis());
		target.setArthralgiaArthritisOnset(source.getArthralgiaArthritisOnset());
		target.setArthralgiaArthritisMonth(source.getArthralgiaArthritisMonth());
		target.setChildrenNumber(source.getChildrenNumber());
		target.setConjunctivitis(source.getConjunctivitis());
		target.setConjunctivitisOnset(source.getConjunctivitisOnset());
		target.setConjunctivitisMonth(source.getConjunctivitisMonth());
		target.setMaculopapularRash(source.getMaculopapularRash());
		target.setMaculopapularRashOnset(source.getMaculopapularRashOnset());
		target.setMaculopapularRashMonth(source.getMaculopapularRashMonth());
		target.setRubella(source.getRubella());
		target.setRubellaOnset(source.getRubellaOnset());
		target.setSwollenLymphs(source.getSwollenLymphs());
		target.setSwollenLymphsOnset(source.getSwollenLymphsOnset());
		target.setSwollenLymphsMonth(source.getSwollenLymphsMonth());
		target.setRashExposure(source.getRashExposure());
		target.setRashExposureDate(source.getRashExposureDate());
		target.setRashExposureMonth(source.getRashExposureMonth());
		target.setRashExposureRegion(regionService.getByReferenceDto(source.getRashExposureRegion()));
		target.setRashExposureDistrict(districtService.getByReferenceDto(source.getRashExposureDistrict()));
		target.setRashExposureCommunity(communityService.getByReferenceDto(source.getRashExposureCommunity()));
		target.setOtherComplications(source.getOtherComplications());
		target.setOtherComplicationsOnset(source.getOtherComplicationsOnset());
		target.setOtherComplicationsMonth(source.getOtherComplicationsMonth());
		target.setOtherComplicationsDetails(source.getOtherComplicationsDetails());

		return target;
	}

	@LocalBean
	@Stateless
	public static class MaternalHistoryFacadeEjbLocal extends MaternalHistoryFacadeEjb {

	}
}
