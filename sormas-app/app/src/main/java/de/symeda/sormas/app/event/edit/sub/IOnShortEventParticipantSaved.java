package de.symeda.sormas.app.event.edit.sub;

import de.symeda.sormas.app.backend.event.EventParticipant; /**
 * Created by Orson on 30/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public interface IOnShortEventParticipantSaved {
    void onSaved(EventParticipant eventParticipantToSave);
}
