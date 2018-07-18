package de.symeda.sormas.app.core.enumeration;

/**
 * Created by Orson on 25/12/2017.
 */
public interface IStatusElaborator {

    String getFriendlyName();
    int getColorIndicatorResource();
    String getStatekey();
    Enum getValue();

    String ARG_EVENT_TYPE = "argEventType";
    String ARG_EVENT_STATUS = "argEventStatus";
    String ARG_FOLLOW_UP_STATUS = "argFollowUpStatus";
    String ARG_INVESTIGATION_STATUS = "argInvestigationStatus";
    String ARG_SHIPMENT_STATUS = "argShipmentStatus";
    String ARG_TASK_STATUS = "argTaskStatus";
    String ARG_VISIT_STATUS = "argVisitStatus";
    String ARG_CASE_CLASSIFICATION_STATUS = "argCaseClassificationStatus";
    String ARG_CONTACT_CLASSIFICATION_STATUS = "argContactClassificationStatus";
    String ARG_SAMPLE_TEST_RESULT_TYPE = "argSampleTestResultType";

}
