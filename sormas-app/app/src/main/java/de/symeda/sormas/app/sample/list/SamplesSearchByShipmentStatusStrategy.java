package de.symeda.sormas.app.sample.list;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.sample.ShipmentStatus;

/**
 * Created by Orson on 10/12/2017.
 */

public class SamplesSearchByShipmentStatusStrategy implements ISamplesSearchStrategy {

    public static final String ARG_FILTER_STATUS = "filterStatus";

    private ShipmentStatus status = null;

    public SamplesSearchByShipmentStatusStrategy(ShipmentStatus status) {
        this.status = status;
    }

    @Override
    public List<Sample> search() {
        List<Sample> list;

        //TODO: Make changes here
        if (status == null) {
            //return DatabaseHelper.getContactDao().queryForAll(Contact.REPORT_DATE_TIME, false);
            return new ArrayList<>();
        }

        if (status == ShipmentStatus.NOT_SHIPPED) {
            list = DatabaseHelper.getSampleDao().queryForEq(Sample.SHIPPED, false);
            List<Sample> samplesToRemove = new ArrayList<>();
            for (Sample sample : list) {
                if (sample.isReceived() || sample.getReferredTo() != null) {
                    samplesToRemove.add(sample);
                }
            }
            list.removeAll(samplesToRemove);
            //list = MemoryDatabaseHelper.SAMPLE.getNotShippedSamples(20);
        } else if (status == ShipmentStatus.SHIPPED) {
            list = DatabaseHelper.getSampleDao().queryForEq(Sample.SHIPPED, true);
            List<Sample> samplesToRemove = new ArrayList<>();
            for (Sample sample : list) {
                if (sample.isReceived() || sample.getReferredTo() != null) {
                    samplesToRemove.add(sample);
                }
            }
            list.removeAll(samplesToRemove);
            //list = MemoryDatabaseHelper.SAMPLE.getShippedSamples(20);
        } else if (status == ShipmentStatus.RECEIVED) {
            list = DatabaseHelper.getSampleDao().queryForEq(Sample.RECEIVED, true);
            List<Sample> samplesToRemove = new ArrayList<>();
            for (Sample sample : list) {
                if (sample.getReferredTo() != null) {
                    samplesToRemove.add(sample);
                }
            }
            list.removeAll(samplesToRemove);
            //list = MemoryDatabaseHelper.SAMPLE.getReceivedSamples(20);
        } else {
            list = DatabaseHelper.getSampleDao().queryForNotNull(Sample.REFERRED_TO + "_id");
            //list = MemoryDatabaseHelper.SAMPLE.getReferredSamples(20);
        }

        return list;
    }
}
