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

    public DeletionReason getDeletionReason() {
        return deletionReason;
    }

    public void setDeletionReason(DeletionReason deletionReason) {
        this.deletionReason = deletionReason;
    }

    public String getOtherDeletionReason() {
        return otherDeleteReason;
    }

    public void setOtherDeletionReason(String otherDeleteReason) {
        this.otherDeleteReason = otherDeleteReason;
    }
}
