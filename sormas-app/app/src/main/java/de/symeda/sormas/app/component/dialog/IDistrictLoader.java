package de.symeda.sormas.app.component.dialog;

import java.util.List;

import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.component.Item;

/**
 * Created by Orson on 08/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public interface IDistrictLoader {
    List<Item> load(Region region);
}
