/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package de.symeda.sormas.ui.events.sevenoneseven;

import java.io.Serializable;
import java.util.Date;

/**
 * Values of one 7-1-7 early response action while it is edited in a dialog. The assessment itself stores the actions as
 * separate properties.
 */
public class Event717EarlyResponseActionEntry implements Serializable {

	private static final long serialVersionUID = 6437108214387654520L;

	public static final String DATE = "date";
	public static final String NOT_APPLICABLE = "notApplicable";
	public static final String NARRATIVE = "narrative";

	private Date date;
	private Boolean notApplicable;
	private String narrative;

	public Event717EarlyResponseActionEntry() {
	}

	public Event717EarlyResponseActionEntry(Date date, Boolean notApplicable, String narrative) {
		this.date = date;
		this.notApplicable = notApplicable;
		this.narrative = narrative;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Boolean getNotApplicable() {
		return notApplicable;
	}

	public void setNotApplicable(Boolean notApplicable) {
		this.notApplicable = notApplicable;
	}

	public String getNarrative() {
		return narrative;
	}

	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}
}
