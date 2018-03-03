package de.symeda.sormas.app.util;

import android.content.res.Resources;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.SearchBy;

/**
 * Created by Orson on 03/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class SubheadingHelper {

    public static String getSubHeading(Resources resources, SearchBy searchBy, Enum filterStatus, String page) {
        String subHeading = page + " Records";
        if (searchBy == SearchBy.BY_CASE_ID) {
            subHeading = String.format(resources.getString(R.string.heading_list_by_case), page + "s");
        } else if (searchBy == SearchBy.BY_CONTACT_ID) {
            subHeading = String.format(resources.getString(R.string.heading_list_by_contact), page + "s");
        } else if (searchBy == SearchBy.BY_EVENT_ID) {
            subHeading = String.format(resources.getString(R.string.heading_list_by_event), page + "s");
        } else if (searchBy == SearchBy.BY_FILTER_STATUS) {
            subHeading = filterStatus != null ? filterStatus.toString() : subHeading;
        }

        return subHeading;
    }
}
