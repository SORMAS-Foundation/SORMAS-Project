package de.symeda.sormas.app.sample.list;

import de.symeda.sormas.app.util.MemoryDatabaseHelper;

import java.util.List;

import de.symeda.sormas.app.backend.sample.Sample;

/**
 * Created by Orson on 10/12/2017.
 */

public class SamplesSearchStrategy implements ISamplesSearchStrategy {
    @Override
    public List<Sample> search() {
        //return DatabaseHelper.getSampleDao().queryForAll();
        return MemoryDatabaseHelper.SAMPLE.getSamples(20);
    }
}
