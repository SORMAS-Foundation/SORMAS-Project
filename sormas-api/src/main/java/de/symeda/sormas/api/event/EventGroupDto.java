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

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.Required;

public class EventGroupDto extends EntityDto {

    private static final long serialVersionUID = -5916206611384281510L;

    public static final String I18N_PREFIX = "EventGroup";

    public static final String NAME = "name";

    @Required
    private String name;

    public static EventGroupDto build() {
        EventGroupDto eventGroup = new EventGroupDto();
        eventGroup.setUuid(DataHelper.createUuid());
        return eventGroup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EventGroupReferenceDto toReference() {
        return new EventGroupReferenceDto(getUuid(), getName());
    }
}
