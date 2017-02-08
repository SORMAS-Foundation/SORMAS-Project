package de.symeda.sormas.app.sample;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.sample.SampleDto;

/**
 * Created by Mate Strysewske on 07.02.2017.
 */

public enum SampleEditTabs {
    SAMPLE_DATA
    ;

    public String toString() {
        return I18nProperties.getFieldCaption(SampleDto.I18N_PREFIX+"."+this.name());
    };
}
