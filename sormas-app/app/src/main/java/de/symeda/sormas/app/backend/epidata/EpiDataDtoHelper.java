package de.symeda.sormas.app.backend.epidata;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.epidata.EpiDataBurialDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.epidata.EpiDataGatheringDto;
import de.symeda.sormas.api.epidata.EpiDataTravelDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;

/**
 * Created by Mate Strysewske on 08.03.2017.
 */

public class EpiDataDtoHelper extends AdoDtoHelper<EpiData, EpiDataDto> {

    private EpiDataBurialDtoHelper burialDtoHelper;
    private EpiDataGatheringDtoHelper gatheringDtoHelper;
    private EpiDataTravelDtoHelper travelDtoHelper;

    public EpiDataDtoHelper() {
        burialDtoHelper = new EpiDataBurialDtoHelper(this);
        gatheringDtoHelper = new EpiDataGatheringDtoHelper(this);
        travelDtoHelper = new EpiDataTravelDtoHelper(this);
    }

    @Override
    public EpiData create() {
        return new EpiData();
    }

    @Override
    public EpiDataDto createDto() {
        return new EpiDataDto();
    }

    @Override
    public void fillInnerFromDto(EpiData a, EpiDataDto b) {
        a.setBurialAttended(b.getBurialAttended());
        a.setGatheringAttended(b.getGatheringAttended());
        a.setTraveled(b.getTraveled());
        a.setRodents(b.getRodents());
        a.setBats(b.getBats());
        a.setPrimates(b.getPrimates());
        a.setSwine(b.getSwine());
        a.setBirds(b.getBirds());
        a.setPoultryEat(b.getPoultryEat());
        a.setPoultry(b.getPoultry());
        a.setPoultrySick(b.getPoultrySick());
        a.setPoultrySickDetails(b.getPoultrySickDetails());
        a.setPoultryDate(b.getPoultryDate());
        a.setPoultryLocation(b.getPoultryLocation());
        a.setCattle(b.getCattle());
        a.setOtherAnimals(b.getOtherAnimals());
        a.setOtherAnimalsDetails(b.getOtherAnimalsDetails());
        a.setWildbirds(b.getWildbirds());
        a.setWildbirdsDetails(b.getWildbirdsDetails());
        a.setWildbirdsDate(b.getWildbirdsDate());
        a.setWildbirdsLocation(b.getWildbirdsLocation());
        a.setWaterSource(b.getWaterSource());
        a.setWaterSourceOther(b.getWaterSourceOther());
        a.setWaterBody(b.getWaterBody());
        a.setWaterBodyDetails(b.getWaterBodyDetails());
        a.setTickBite(b.getTickBite());

        List<EpiDataBurial> burials = new ArrayList<>();
        if (!b.getBurials().isEmpty()) {
            for (EpiDataBurialDto burialDto : b.getBurials()) {
                EpiDataBurial burial = burialDtoHelper.fillOrCreateFromDto(null, burialDto);
                burial.setEpiData(a);
                burials.add(burial);
            }
        }
        a.setBurials(burials);

        List<EpiDataGathering> gatherings = new ArrayList<>();
        if (!b.getGatherings().isEmpty()) {
            for (EpiDataGatheringDto gatheringDto : b.getGatherings()) {
                EpiDataGathering gathering = gatheringDtoHelper.fillOrCreateFromDto(null, gatheringDto);
                gathering.setEpiData(a);
                gatherings.add(gathering);
            }
        }
        a.setGatherings(gatherings);

        List<EpiDataTravel> travels = new ArrayList<>();
        if (!b.getTravels().isEmpty()) {
            for (EpiDataTravelDto travelDto : b.getTravels()) {
                EpiDataTravel travel = travelDtoHelper.fillOrCreateFromDto(null, travelDto);
                travel.setEpiData(a);
                travels.add(travel);
            }
        }
        a.setTravels(travels);
    }

    @Override
    public void fillInnerFromAdo(EpiDataDto a, EpiData b) {
        a.setBurialAttended(b.getBurialAttended());
        a.setGatheringAttended(b.getGatheringAttended());
        a.setTraveled(b.getTraveled());
        a.setRodents(b.getRodents());
        a.setBats(b.getBats());
        a.setPrimates(b.getPrimates());
        a.setSwine(b.getSwine());
        a.setBirds(b.getBirds());
        a.setPoultryEat(b.getPoultryEat());
        a.setPoultry(b.getPoultry());
        a.setPoultrySick(b.getPoultrySick());
        a.setPoultrySickDetails(b.getPoultrySickDetails());
        a.setPoultryDate(b.getPoultryDate());
        a.setPoultryLocation(b.getPoultryLocation());
        a.setCattle(b.getCattle());
        a.setOtherAnimals(b.getOtherAnimals());
        a.setOtherAnimalsDetails(b.getOtherAnimalsDetails());
        a.setWildbirds(b.getWildbirds());
        a.setWildbirdsDetails(b.getWildbirdsDetails());
        a.setWildbirdsDate(b.getWildbirdsDate());
        a.setWildbirdsLocation(b.getWildbirdsLocation());
        a.setWaterSource(b.getWaterSource());
        a.setWaterSourceOther(b.getWaterSourceOther());
        a.setWaterBody(b.getWaterBody());
        a.setWaterBodyDetails(b.getWaterBodyDetails());
        a.setTickBite(b.getTickBite());

        List<EpiDataBurialDto> burialDtos = new ArrayList<>();
        if (!b.getBurials().isEmpty()) {
            for (EpiDataBurial burial : b.getBurials()) {
                EpiDataBurialDto burialDto = burialDtoHelper.createDto();
                burialDtoHelper.fillInnerFromAdo(burialDto, burial);
                burialDto.setEpiData(a);
                burialDtos.add(burialDto);
            }
        }
        a.setBurials(burialDtos);

        List<EpiDataGatheringDto> gatheringDtos = new ArrayList<>();
        if (!b.getGatherings().isEmpty()) {
            for (EpiDataGathering gathering : b.getGatherings()) {
                EpiDataGatheringDto gatheringDto = gatheringDtoHelper.createDto();
                gatheringDtoHelper.fillInnerFromAdo(gatheringDto, gathering);
                gatheringDto.setEpiData(a);
                gatheringDtos.add(gatheringDto);
            }
        }
        a.setGatherings(gatheringDtos);

        List<EpiDataTravelDto> travelDtos = new ArrayList<>();
        if (!b.getTravels().isEmpty()) {
            for (EpiDataTravel travel : b.getTravels()) {
                EpiDataTravelDto travelDto = travelDtoHelper.createDto();
                travelDtoHelper.fillInnerFromAdo(travelDto, travel);
                travelDto.setEpiData(a);
                travelDtos.add(travelDto);
            }
        }
        a.setTravels(travelDtos);
    }
}
