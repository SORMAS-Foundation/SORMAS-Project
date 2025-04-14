package de.symeda.sormas.ui.caze.notifier;

public class TreatmentOption {

    private String value;
    private String caption;

    public TreatmentOption(String value, String caption) {
        this.value = value;
        this.caption = caption;
    }

    public String getValue() {
        return value;
    }

    public String getCaption() {
        return caption;
    }

    @Override
    public String toString() {
        return caption; // Use caption as the default display
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        TreatmentOption that = (TreatmentOption) obj;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
