package de.symeda.sormas.api.externaljournal.patientdiary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientDiaryIdatId implements Serializable {
    private static final long serialVersionUID = -2785744892381447672L;

    private PatientDiaryPersonDto idat;

    public PatientDiaryPersonDto getIdat() {
        return idat;
    }

    public void setIdat(PatientDiaryPersonDto idat) {
        this.idat = idat;
    }
}
