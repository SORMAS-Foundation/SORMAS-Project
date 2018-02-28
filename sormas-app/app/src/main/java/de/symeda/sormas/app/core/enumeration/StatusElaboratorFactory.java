package de.symeda.sormas.app.core.enumeration;

import android.content.Context;
import android.content.res.Resources;

import de.symeda.sormas.app.sample.ShipmentStatus;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.EventType;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.visit.VisitStatus;

/**
 * Created by Orson on 06/01/2018.
 */

public class StatusElaboratorFactory {

    public static IStatusElaborator getElaborator(Context c, Enum e) {
        if (e == null)
            throw new NullPointerException("Enum arugment for StatusElaboratorFactory cannot be null");

        Resources resoures = c.getResources();
        IStatusElaborator result;

        if (e instanceof EventStatus) {
            result = new EventStatusElaborator((EventStatus)e, resoures);
        } else if (e instanceof EventType) {
            result = new EventTypeElaborator((EventType) e, resoures);
        } else if (e instanceof FollowUpStatus) {
            result = new FollowUpStatusElaborator((FollowUpStatus)e, resoures);
        } else if (e instanceof InvestigationStatus) {
            result = new InvestigationStatusElaborator((InvestigationStatus)e, resoures);
        } else if (e instanceof ShipmentStatus) {
            result = new ShipmentStatusElaborator((ShipmentStatus)e, resoures);
        } else if (e instanceof TaskStatus) {
            result = new TaskStatusElaborator((TaskStatus)e, resoures);
        } else if (e instanceof VisitStatus) {
            result = new VisitStatusElaborator((VisitStatus)e, resoures);
        } else if (e instanceof CaseClassification) {
            result = new CaseClassificationElaborator((CaseClassification)e, resoures);
        } else if (e instanceof ContactClassification) {
            result = new ContactClassificationElaborator((ContactClassification)e, resoures);
        } else {
            throw new IllegalArgumentException(e.getDeclaringClass().getName());
        }

        return result;
    }


}
