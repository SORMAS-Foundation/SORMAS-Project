package de.symeda.sormas.app.component.dialog;

import java.util.List;

import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.component.Item;

/**
 * Created by Orson on 18/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public interface IFacilityLoader {

    List<Item> load(Community community, boolean includeStaticFacilities);

    List<Item> load(District district, boolean includeStaticFacilities);
}
