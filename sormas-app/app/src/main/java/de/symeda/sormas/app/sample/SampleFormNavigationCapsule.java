package de.symeda.sormas.app.sample;

import android.content.Context;

import de.symeda.sormas.app.core.BaseFormNavigationCapsule;

/**
 * Created by Orson on 09/01/2018.
 */

public class SampleFormNavigationCapsule extends BaseFormNavigationCapsule {

    public SampleFormNavigationCapsule(Context context, String recordUuid, ShipmentStatus pageStatus) {
        super(context, recordUuid, pageStatus);
    }

    /*public SampleFormNavigationCapsule setSampleMaterial(String sampleMaterial) {
        setSampleMaterial(sampleMaterial);

        return this;
    }

    public SampleFormNavigationCapsule setCaseUuid(String caseUuid) {
        setSampleMaterial(caseUuid);

        return this;
    }*/
}