package de.symeda.sormas.api.common;

import java.io.Serializable;

public class DeletionDetails implements Serializable {

    private DeletionReason deletionReason;
    private String otherDeleteReason;

    public DeletionDetails() {
    }

    public DeletionDetails(DeletionReason deletionReason, String otherDeleteReason) {
        this.deletionReason = deletionReason;
        this.otherDeleteReason = otherDeleteReason;
    }

    public DeletionReason getDeleteReason() {
        return deletionReason;
    }

    public void setDeleteReason(DeletionReason deletionReason) {
        this.deletionReason = deletionReason;
    }

    public String getOtherDeleteReason() {
        return otherDeleteReason;
    }

    public void setOtherDeleteReason(String otherDeleteReason) {
        this.otherDeleteReason = otherDeleteReason;
    }
}
