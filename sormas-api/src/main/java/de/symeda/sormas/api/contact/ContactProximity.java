/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.contact;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.I18nProperties;

public enum ContactProximity {
	
	TOUCHED_FLUID,		
	PHYSICAL_CONTACT,
	CLOTHES_OR_OTHER,
	CLOSE_CONTACT,		
	FACE_TO_FACE_LONG,	
	MEDICAL_UNSAVE,		
	SAME_ROOM,
	AIRPLANE,
	FACE_TO_FACE_SHORT,
	MEDICAL_SAVE;
	
	public boolean hasFollowUp() {
		switch (this) {
		case AIRPLANE:
		case CLOSE_CONTACT:
		case CLOTHES_OR_OTHER:
		case FACE_TO_FACE_LONG:
		case MEDICAL_UNSAVE:
		case PHYSICAL_CONTACT:
		case SAME_ROOM:
		case TOUCHED_FLUID:
			return true;

		case FACE_TO_FACE_SHORT:
		case MEDICAL_SAVE:
			return false;
			
		default:
			throw new IllegalArgumentException(this.name());
		}
	}
	
	/**
	 * TODO Replace locale with customizable solution or whatever is needed based on #1503
	 */
	public static ContactProximity[] getValues(Disease disease, String serverLocale) {
		if (disease != null && serverLocale != null && serverLocale.startsWith("de")) {
			switch (disease) {
			case CORONAVIRUS:
				return new ContactProximity[] { TOUCHED_FLUID, FACE_TO_FACE_LONG, MEDICAL_UNSAVE, AIRPLANE, FACE_TO_FACE_SHORT, MEDICAL_SAVE };
			default:
				break;
			}
		}
		return new ContactProximity[] { TOUCHED_FLUID, PHYSICAL_CONTACT, CLOTHES_OR_OTHER, CLOSE_CONTACT, SAME_ROOM };
	}
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
