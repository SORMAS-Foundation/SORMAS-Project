package de.symeda.sormas.api.visit;

import java.io.Serializable;

public class VisitResultDto implements Serializable {

    public VisitResultDto() {}

    public VisitResultDto(boolean external, VisitResult result) {
        this.external = external;
        this.result = result;
    }

    private boolean external;
    private VisitResult result;

    public boolean isExternal() {
        return external;
    }

    public void setExternal(boolean external) {
        this.external = external;
    }

    public VisitResult getResult() {
        return result;
    }

    public void setResult(VisitResult result) {
        this.result = result;
    }

    public String toString() {
        return result.toString();
    }
}
