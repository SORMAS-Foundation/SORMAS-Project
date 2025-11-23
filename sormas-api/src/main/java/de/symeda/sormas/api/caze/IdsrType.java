package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum IdsrType {
    ANTHRAX,
    ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION,
    ANIMAL_BITE,
    CHOLERA,
    DIARRHEA_WITH_BLOOD,
    MATERNAL_DEATH,
    NEONATAL_DEATH,
    UNEXPLAINED_CLUSTER_OF_EVENTS,
    UNEXPLAINED_CLUSTER_OF_DEATHS,
    SCHISTOSOMIASIS,
    ACUTE_VIRAL_HAEMORRHAGIC_FEVERS,
    MONKEY_POX,
    SNAKE_BITES,
    LYMPHATIC_FILARIASIS,
    ONCHOCERCIASIS,
    TRACHOMA,
    OTHER;

    @Override
    public String toString() {
        return I18nProperties.getEnumCaption(this);
    }
}
