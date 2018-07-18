package de.symeda.sormas.app.core.enumeration;

import android.content.Context;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.EventType;
import de.symeda.sormas.api.sample.SampleTestResultType;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.shared.ShipmentStatus;

public class StatusElaboratorFactory {

    public static IStatusElaborator getElaborator(Context c, Enum e) {
        if (e == null)
            throw new NullPointerException("Enum arugment for StatusElaboratorFactory cannot be null");

        IStatusElaborator result;

        if (e instanceof EventStatus) {
            result = new EventStatusElaborator((EventStatus) e);
        } else if (e instanceof EventType) {
            result = new EventTypeElaborator((EventType) e);
        } else if (e instanceof FollowUpStatus) {
            result = new FollowUpStatusElaborator((FollowUpStatus) e);
        } else if (e instanceof InvestigationStatus) {
            result = new InvestigationStatusElaborator((InvestigationStatus) e);
        } else if (e instanceof ShipmentStatus) {
            result = new ShipmentStatusElaborator((ShipmentStatus) e);
        } else if (e instanceof TaskStatus) {
            result = new TaskStatusElaborator((TaskStatus) e);
        } else if (e instanceof VisitStatus) {
            result = new VisitStatusElaborator((VisitStatus) e);
        } else if (e instanceof CaseClassification) {
            result = new CaseClassificationElaborator((CaseClassification) e);
        } else if (e instanceof ContactClassification) {
            result = new ContactClassificationElaborator((ContactClassification) e);
        } else if (e instanceof SampleTestResultType) {
            result = new SampleTestResultTypeElaborator((SampleTestResultType) e);
        } else {
            throw new IllegalArgumentException(e.getDeclaringClass().getName());
        }

        return result;
    }
}
