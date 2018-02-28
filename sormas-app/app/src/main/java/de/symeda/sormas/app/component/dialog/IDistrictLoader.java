package de.symeda.sormas.app.component.dialog;

import java.util.List;

import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;

/**
 * Created by Orson on 08/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public interface IDistrictLoader {
    List<District> load(Region region);
}
