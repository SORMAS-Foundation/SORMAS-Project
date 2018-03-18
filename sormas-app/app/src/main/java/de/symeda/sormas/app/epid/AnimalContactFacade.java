package de.symeda.sormas.app.epid;

import java.util.List;

import de.symeda.sormas.app.backend.epidata.EpiData;

/**
 * Created by Orson on 20/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class AnimalContactFacade {

    public static List<AnimalContact> loadState(List<AnimalContact> list, EpiData record) {
        if (record == null)
            return list;

        for (AnimalContact animalContact : list) {
            if (animalContact.equals(AnimalContact.RODENT)) {
                animalContact.setState(record.getRodents());
                animalContact.getLayout().setDetailOrSpecify("");
                animalContact.getLayout().setLastExposureDate(null);
                animalContact.getLayout().setLastExposurePlace("");
            } else if (animalContact.equals(AnimalContact.BAT)) {
                animalContact.setState(record.getBats());
                animalContact.getLayout().setDetailOrSpecify("");
                animalContact.getLayout().setLastExposureDate(null);
                animalContact.getLayout().setLastExposurePlace("");
            } else if (animalContact.equals(AnimalContact.PRIMATE)) {
                animalContact.setState(record.getPrimates());
                animalContact.getLayout().setDetailOrSpecify("");
                animalContact.getLayout().setLastExposureDate(null);
                animalContact.getLayout().setLastExposurePlace("");
            } else if (animalContact.equals(AnimalContact.SWINE)) {
                animalContact.setState(record.getSwine());
                animalContact.getLayout().setDetailOrSpecify("");
                animalContact.getLayout().setLastExposureDate(null);
                animalContact.getLayout().setLastExposurePlace("");
            } else if (animalContact.equals(AnimalContact.BIRD)) {
                animalContact.setState(record.getBirds());
                animalContact.getLayout().setDetailOrSpecify("");
                animalContact.getLayout().setLastExposureDate(null);
                animalContact.getLayout().setLastExposurePlace("");
            } else if (animalContact.equals(AnimalContact.EAT_RAW_UNDERCOOKED_BIRD)) {
                animalContact.setState(record.getPoultryEat());
                animalContact.getLayout().setDetailOrSpecify("");
                animalContact.getLayout().setLastExposureDate(null);
                animalContact.getLayout().setLastExposurePlace("");
            } else if (animalContact.equals(AnimalContact.EXPOSURE_TO_DOMESTICATED_BIRD)) {
                animalContact.setState(record.getPoultry());
                animalContact.getLayout().setDetailOrSpecify(record.getPoultryDetails());
                animalContact.getLayout().setLastExposureDate(null);
                animalContact.getLayout().setLastExposurePlace("");
            } else if (animalContact.equals(AnimalContact.EXPOSURE_TO_SICK_BIRD)) {
                animalContact.setState(record.getPoultrySick());
                animalContact.getLayout().setDetailOrSpecify(record.getPoultrySickDetails());
                animalContact.getLayout().setLastExposureDate(record.getPoultryDate());
                animalContact.getLayout().setLastExposurePlace(record.getPoultryLocation());
            } else if (animalContact.equals(AnimalContact.CATTLE)) {
                animalContact.setState(record.getCattle());
                animalContact.getLayout().setDetailOrSpecify("");
                animalContact.getLayout().setLastExposureDate(null);
                animalContact.getLayout().setLastExposurePlace("");
            } else if (animalContact.equals(AnimalContact.WILD_ANIMAL)) {
                animalContact.setState(record.getWildbirds());
                animalContact.getLayout().setDetailOrSpecify(record.getWildbirdsDetails());
                animalContact.getLayout().setLastExposureDate(record.getWildbirdsDate());
                animalContact.getLayout().setLastExposurePlace(record.getWildbirdsLocation());
            } else if (animalContact.equals(AnimalContact.OTHER_ANIMAL)) {
                animalContact.setState(record.getOtherAnimals());
                animalContact.getLayout().setDetailOrSpecify(record.getOtherAnimalsDetails());
                animalContact.getLayout().setLastExposureDate(null);
                animalContact.getLayout().setLastExposurePlace("");
            } else if (animalContact.equals(AnimalContact.CONTACT_WITH_BODY_OF_WATER)) {
                animalContact.setState(record.getWaterBody());
                animalContact.getLayout().setDetailOrSpecify(record.getWaterBodyDetails());
                animalContact.getLayout().setLastExposureDate(null);
                animalContact.getLayout().setLastExposurePlace("");
            } else if (animalContact.equals(AnimalContact.TICK_BITE)) {
                animalContact.setState(record.getTickBite());
                animalContact.getLayout().setDetailOrSpecify("");
                animalContact.getLayout().setLastExposureDate(null);
                animalContact.getLayout().setLastExposurePlace("");
            } else if (animalContact.equals(AnimalContact.FLEA_BITE)) {
                animalContact.setState(record.getFleaBite());
                animalContact.getLayout().setDetailOrSpecify("");
                animalContact.getLayout().setLastExposureDate(null);
                animalContact.getLayout().setLastExposurePlace("");
            }
        }

        return list;
    }
}
