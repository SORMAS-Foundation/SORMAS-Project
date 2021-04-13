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

package de.symeda.sormas.api.event;

import java.io.Serializable;

/*
  An event may be related to several event groups.
  This class contains only one reference on an event group (= the latest), the one that will be shown to the user.
  It also contains an information about the total count of event groups related to an Event.
  It will be used in Events directory for the Event groups column.
 */
public class EventGroupsIndexDto implements Serializable {

    private static final long serialVersionUID = 769275615231297865L;

    private EventGroupReferenceDto eventGroup;
    private Long count;

    public EventGroupsIndexDto(EventGroupReferenceDto eventGroup, Long count) {
        this.eventGroup = eventGroup;
        this.count = count;
    }

    public EventGroupReferenceDto getEventGroup() {
        return eventGroup;
    }

    public void setEventGroup(EventGroupReferenceDto eventGroup) {
        this.eventGroup = eventGroup;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    @Override
    public String toString() {
        if (count <= 1) {
            return eventGroup.getUuid();
        }
        return eventGroup.getUuid() + " (" + count + ")";
    }
}
