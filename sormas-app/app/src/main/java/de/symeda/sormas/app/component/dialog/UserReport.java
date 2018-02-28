package de.symeda.sormas.app.component.dialog;

/**
 * Created by Orson on 04/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class UserReport {

    private String viewName;
    private String uuid;
    private String message;

    public UserReport(String viewName, String uuid) {
        this.viewName = viewName;
        this.uuid = uuid;
    }

    public String getViewName() {

        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
