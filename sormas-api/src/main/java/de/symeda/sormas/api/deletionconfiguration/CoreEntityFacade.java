package de.symeda.sormas.api.deletionconfiguration;


import java.util.Date;

public interface CoreEntityFacade {

    void executeAutomaticDeletion (DeletionReference deletionReference, Date referenceDeletionDate);

}
