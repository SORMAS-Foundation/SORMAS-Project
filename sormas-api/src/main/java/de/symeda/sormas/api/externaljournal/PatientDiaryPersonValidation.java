package de.symeda.sormas.api.externaljournal;

import java.io.Serializable;

public class PatientDiaryPersonValidation implements Serializable {

    private boolean valid;
    private String message;

    public PatientDiaryPersonValidation(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
