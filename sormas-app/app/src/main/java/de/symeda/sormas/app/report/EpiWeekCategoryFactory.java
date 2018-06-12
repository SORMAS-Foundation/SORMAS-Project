package de.symeda.sormas.app.report;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.utils.EpiWeek;

/**
 * Created by Orson on 24/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public class EpiWeekCategoryFactory {

    private final List<BaseEpiWeekCategory> mEpiWeekCategoryList;

    public EpiWeekCategoryFactory() {
        mEpiWeekCategoryList = new ArrayList<BaseEpiWeekCategory>() {{
            add(new LastEpiWeek());
            add(new CurrentEpiWeek());
            add(new OtherEpiWeek());
        }};
    }

    public BaseEpiWeekCategory getEpiWeekCategory(EpiWeek epiWeek) {
        for(BaseEpiWeekCategory e: mEpiWeekCategoryList) {
            if (e.isMatch(epiWeek))
                return e;
        }

        throw new IllegalArgumentException("Invalid EpiWeek argument; cannot find Epi Week Category");
    }

}
