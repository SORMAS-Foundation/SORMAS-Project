package de.symeda.sormas.api.externalmessage.processing;

import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;

public class EventValidationResult {

    private EventDto event;

    private EventParticipantReferenceDto eventParticipant;

    private boolean eventSelectionCanceled;

    public EventDto getEvent() {
        return event;
    }

    public void setEvent(EventDto event) {
        this.event = event;
    }

    public boolean isEventSelected() {
        return event != null;
    }

    public EventParticipantReferenceDto getEventParticipant() {
        return eventParticipant;
    }

    public void setEventParticipant(EventParticipantReferenceDto eventParticipant) {
        this.eventParticipant = eventParticipant;
    }

    public boolean isEventParticipantSelected() {
        return eventParticipant != null;
    }

    public boolean isEventSelectionCanceled() {
        return eventSelectionCanceled;
    }

    public void setEventSelectionCanceled(boolean eventSelectionCanceled) {
        this.eventSelectionCanceled = eventSelectionCanceled;
    }
}
