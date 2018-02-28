package de.symeda.sormas.app.sample.list;

import java.util.List;

import de.symeda.sormas.app.backend.sample.Sample;

/**
 * Created by Orson on 10/12/2017.
 */

public interface ISamplesSearchStrategy {

    List<Sample> search();
}
