package de.symeda.sormas.app.shared;

import android.content.Context;

import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.core.BaseFormNavigationCapsule;

public class SampleFormNavigationCapsule extends BaseFormNavigationCapsule<Sample, SampleFormNavigationCapsule> {

    public SampleFormNavigationCapsule(Context context, ShipmentStatus pageStatus) {
        super(context, null, pageStatus);
    }

    public SampleFormNavigationCapsule(Context context, String recordUuid, ShipmentStatus pageStatus) {
        super(context, recordUuid, pageStatus);
    }
}