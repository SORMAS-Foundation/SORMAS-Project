package de.symeda.sormas.app.searchstrategy;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.shared.ShipmentStatus;

public class SampleSearchByStatusStrategy implements ISearchStrategy<Sample> {

    private ShipmentStatus status;

    public SampleSearchByStatusStrategy(ShipmentStatus status) {
        this.status = status;
    }

    @Override
    public List<Sample> search() {
        List<Sample> result;

        if (status == ShipmentStatus.NOT_SHIPPED) {
            result = DatabaseHelper.getSampleDao().queryForEq(Sample.SHIPPED, false);
            List<Sample> samplesToRemove = new ArrayList<>();
            for (Sample sample : result) {
                if (sample.isReceived() || sample.getReferredToUuid() != null) {
                    samplesToRemove.add(sample);
                }
            }
            result.removeAll(samplesToRemove);
        } else if (status == ShipmentStatus.SHIPPED) {
            result = DatabaseHelper.getSampleDao().queryForEq(Sample.SHIPPED, true);
            List<Sample> samplesToRemove = new ArrayList<>();
            for (Sample sample : result) {
                if (sample.isReceived() || sample.getReferredToUuid() != null) {
                    samplesToRemove.add(sample);
                }
            }
            result.removeAll(samplesToRemove);
        } else if (status == ShipmentStatus.RECEIVED) {
            result = DatabaseHelper.getSampleDao().queryForEq(Sample.RECEIVED, true);
            List<Sample> samplesToRemove = new ArrayList<>();
            for (Sample sample : result) {
                if (sample.getReferredToUuid() != null) {
                    samplesToRemove.add(sample);
                }
            }
            result.removeAll(samplesToRemove);
        } else {
            result = DatabaseHelper.getSampleDao().queryForNotNull(Sample.REFERRED_TO_UUID);
        }

        return result;
    }
}
