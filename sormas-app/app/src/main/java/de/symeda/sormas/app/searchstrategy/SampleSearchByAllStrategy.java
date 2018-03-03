package de.symeda.sormas.app.searchstrategy;

import java.util.List;

import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.Sample;

/**
 * Created by Orson on 03/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class SampleSearchByAllStrategy implements ISearchStrategy<Sample> {
    @Override
    public List<Sample> search() {
        return DatabaseHelper.getSampleDao().queryForAll();
    }
}