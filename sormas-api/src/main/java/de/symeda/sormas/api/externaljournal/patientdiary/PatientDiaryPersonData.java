package de.symeda.sormas.api.externaljournal.patientdiary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientDiaryPersonData implements Serializable {

    private static final long serialVersionUID = -1036432520752506284L;

    private String _id;
    private PatientDiaryIdatId idatId;
    // Other returned fields are ignored. Add as needed

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public PatientDiaryIdatId getIdatId() {
        return idatId;
    }

    public void setIdatId(PatientDiaryIdatId idatId) {
        this.idatId = idatId;
    }
}
