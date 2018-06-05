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

public interface IAnimalContactValueLoader {
    List<AnimalContact> unloaded();
    List<AnimalContact> loadState(EpiData record);
}
