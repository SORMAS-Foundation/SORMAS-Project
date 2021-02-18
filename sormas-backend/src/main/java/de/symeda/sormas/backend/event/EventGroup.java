/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.event;

import de.symeda.auditlog.api.Audited;
import de.symeda.auditlog.api.AuditedIgnore;
import de.symeda.sormas.backend.common.CoreAdo;
import de.symeda.sormas.backend.visit.Visit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import java.util.List;

import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_DEFAULT;

@Entity(name = "event_groups")
@Audited
public class EventGroup extends CoreAdo {

    private static final long serialVersionUID = -6609939162115335854L;

    public static final String TABLE_NAME = "event_groups";

    public static final String NAME = "name";
    public static final String EVENTS = "events";
    public static final String ARCHIVED = "archived";

    private String name;
    private List<Event> events;

    private boolean archived;

    @Column(length = COLUMN_LENGTH_DEFAULT, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @AuditedIgnore
    @ManyToMany(mappedBy = Event.EVENT_GROUPS, fetch = FetchType.LAZY)
    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    @Column
    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}
