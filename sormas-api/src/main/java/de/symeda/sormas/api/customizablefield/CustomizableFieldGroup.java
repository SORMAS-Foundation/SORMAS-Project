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

package de.symeda.sormas.api.customizablefield;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines UI groups for customizable fields, scoped to a specific {@link CustomizableFieldContext}.
 * <p>
 * Each group has a stable string {@link #key} that is used as:
 * <ul>
 * <li>the value stored in the database;</li>
 * <li>the Vaadin layout location ID when embedding a {@code CustomizableFieldsGroup} component.</li>
 * </ul>
 * <p>
 * To add a new group, append an enum value with the owning context and a unique, stable key.
 * The key must not be changed after data has been stored against it.
 */
public enum CustomizableFieldGroup {

	// ---- CASE groups --------------------------------------------------------
	CASE_TEST_GROUP_1(CustomizableFieldContext.CASE, "caseTestGroup1"),
	CASE_TEST_GROUP_2(CustomizableFieldContext.CASE, "caseTestGroup2"),

	// ---- EPIDATA groups -----------------------------------------------------
	EPIDATA_EXPOSURE_INVESTIGATION(CustomizableFieldContext.EPIDATA, "exposureInvestigation"),
	EPIDATA_ACTIVITY_AS_CASE(CustomizableFieldContext.EPIDATA, "activityAsCase"),
	EPIDATA_CONTACT_WITH_SOURCE_CASE(CustomizableFieldContext.EPIDATA, "contactWithSourceCase");

	private final CustomizableFieldContext context;
	/**
	 * Stable key stored in the database and used as the Vaadin layout location ID.
	 */
	private final String key;

	CustomizableFieldGroup(CustomizableFieldContext context, String key) {
		this.context = context;
		this.key = key;
	}

	/**
	 * The {@link CustomizableFieldContext} this group belongs to.
	 */
	public CustomizableFieldContext getContext() {
		return context;
	}

	/**
	 * Stable string key used as the database-stored value and as the Vaadin layout location ID.
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Returns all groups that belong to the given context.
	 */
	public static List<CustomizableFieldGroup> getGroupsForContext(CustomizableFieldContext context) {
		List<CustomizableFieldGroup> result = new ArrayList<>();
		for (CustomizableFieldGroup group : values()) {
			if (group.context == context) {
				result.add(group);
			}
		}
		return result;
	}

	/**
	 * Looks up a group by its stable {@link #key}, returning {@code null} if not found.
	 */
	public static CustomizableFieldGroup fromKey(String key) {
		if (key == null) {
			return null;
		}
		for (CustomizableFieldGroup group : values()) {
			if (group.key.equals(key)) {
				return group;
			}
		}
		return null;
	}
}
