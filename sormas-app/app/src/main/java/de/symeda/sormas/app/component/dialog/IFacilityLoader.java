package de.symeda.sormas.app.component.dialog;

import java.util.List;

import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.region.Community;

/**
 * Created by Orson on 18/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public interface IFacilityLoader {

    List<Facility> load(Community community);
}
