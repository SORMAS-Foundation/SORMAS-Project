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

package de.symeda.sormas.ui.events.groups;

import com.vaadin.data.ValueProvider;

import de.symeda.sormas.api.event.EventGroupsIndexDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.HtmlHelper;

public class EventGroupsValueProvider implements ValueProvider<EventGroupsIndexDto, String> {

    @Override
    public String apply(EventGroupsIndexDto eventGroups) {
        if (eventGroups == null || eventGroups.getEventGroup() == null) {
            return "";
        }

        String html = HtmlHelper.buildHyperlinkTitle(eventGroups.getEventGroup().getCaption(), DataHelper.getShortUuid(eventGroups.getEventGroup().getUuid()));
        if (eventGroups.getCount() != null && eventGroups.getCount() > 1L) {
			html = html
				+ "<span class=\"hspace-left-5\">"
				+ HtmlHelper.buildTitle(String.format(I18nProperties.getCaption(Captions.eventGroupsMultiple), eventGroups.getCount()), "(" + eventGroups.getCount() + ")")
				+ "</span>";
        }
        return html;
    }
}
