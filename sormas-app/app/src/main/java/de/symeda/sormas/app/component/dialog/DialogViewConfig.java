package de.symeda.sormas.app.component.dialog;

public class DialogViewConfig {

    private String heading;
    private String subHeading;
    private String positiveButtonText;
    private String negativeButtonText;
    private String deleteButtonText;
    private String cancelButtonText;
    private String createButtonText;

    public DialogViewConfig(String heading) {
        this.heading = heading;
    }

    public DialogViewConfig(String heading, String subHeading, String positiveButtonText, String negativeButtonText, String deleteButtonText, String cancelButtonText, String createButtonText) {
        this.heading = heading;
        this.subHeading = subHeading;
        this.positiveButtonText = positiveButtonText;
        this.negativeButtonText = negativeButtonText;
        this.deleteButtonText = deleteButtonText;
        this.cancelButtonText = cancelButtonText;
        this.createButtonText = createButtonText;
    }

    public String getHeading() {
        return heading;
    }

    public String getSubHeading() {
        return subHeading;
    }

    public void setSubHeading(String subHeading) {
        this.subHeading = subHeading;
    }

    public String getPositiveButtonText() {
        return positiveButtonText;
    }

    public void setPositiveButtonText(String positiveButtonText) {
        this.positiveButtonText = positiveButtonText;
    }

    public String getNegativeButtonText() {
        return negativeButtonText;
    }

    public void setNegativeButtonText(String negativeButtonText) {
        this.negativeButtonText = negativeButtonText;
    }

    public String getDeleteButtonText() {
        return deleteButtonText;
    }

    public String getCancelButtonText() {
        return cancelButtonText;
    }

    public String getCreateButtonText() {
        return createButtonText;
    }

}
