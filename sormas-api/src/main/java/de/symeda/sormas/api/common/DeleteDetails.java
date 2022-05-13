package de.symeda.sormas.api.common;

import java.io.Serializable;

public class DeleteDetails implements Serializable {

    private DeleteReason deleteReason;
    private String otherDeleteReason;

    public DeleteDetails() {
    }

    public DeleteDetails(DeleteReason deleteReason, String otherDeleteReason) {
        this.deleteReason = deleteReason;
        this.otherDeleteReason = otherDeleteReason;
    }

    public DeleteReason getDeleteReason() {
        return deleteReason;
    }

    public void setDeleteReason(DeleteReason deleteReason) {
        this.deleteReason = deleteReason;
    }

    public String getOtherDeleteReason() {
        return otherDeleteReason;
    }

    public void setOtherDeleteReason(String otherDeleteReason) {
        this.otherDeleteReason = otherDeleteReason;
    }
}
