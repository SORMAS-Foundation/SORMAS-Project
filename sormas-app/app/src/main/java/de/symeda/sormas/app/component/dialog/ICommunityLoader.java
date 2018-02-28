package de.symeda.sormas.app.component.dialog;

import java.util.List;

import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;

/**
 * Created by Orson on 08/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public interface ICommunityLoader {
    List<Community> load(District district);
}
