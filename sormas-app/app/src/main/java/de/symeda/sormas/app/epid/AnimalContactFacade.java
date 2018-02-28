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
        for (AnimalContact animalContact : list) {
            if (animalContact == AnimalContact.EXPOSURE_TO_SICK_BIRD) {
                animalContact.getLayout().setDetailOrSpecify("Test expo to sick bird"); // record.getFever());
                animalContact.getLayout().setLastExposureDate(record.getWildbirdsDate());
                animalContact.getLayout().setLastExposurePlace(record.getPoultryLocation());
            }
        }

        return list;
    }
}
