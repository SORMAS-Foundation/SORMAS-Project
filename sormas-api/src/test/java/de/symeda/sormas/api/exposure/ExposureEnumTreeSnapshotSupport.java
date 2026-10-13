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

package de.symeda.sormas.api.exposure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class ExposureEnumTreeSnapshotSupport {

	static final int SCHEMA_VERSION = 1;
	static final String SNAPSHOT_CLASSPATH = "de/symeda/sormas/api/exposure/exposure-enum-tree.json";
	static final String SNAPSHOT_FILE = "src/test/resources/" + SNAPSHOT_CLASSPATH;

	private ExposureEnumTreeSnapshotSupport() {
	}

	static Map<String, Object> buildTreeFromEnums() {
		Map<String, Object> root = new LinkedHashMap<>();
		root.put("schemaVersion", SCHEMA_VERSION);

		List<Map<String, Object>> categories = new ArrayList<>();
		for (ExposureCategory category : ExposureCategory.values()) {
			categories.add(buildCategoryNode(category));
		}
		root.put("categories", categories);

		return root;
	}

	private static Map<String, Object> buildCategoryNode(ExposureCategory category) {
		Map<String, Object> categoryNode = new LinkedHashMap<>();
		categoryNode.put("name", category.name());
		categoryNode.put("deprecated", category.isDeprecated());
		categoryNode.put("hasNoSetting", category.hasNoSetting());
		categoryNode.put("hasNoSubSetting", category.hasNoSubSetting());

		List<Map<String, Object>> settings = new ArrayList<>();
		for (ExposureSetting setting : ExposureSetting.getValues(category, true)) {
			settings.add(buildSettingNode(category, setting));
		}
		categoryNode.put("settings", settings);

		Map<String, Object> categoryOnlyNode = new LinkedHashMap<>();
		categoryOnlyNode.put("subSettings", toLeafList(ExposureSubSetting.getValuesForCategoryOnly(category, true)));
		categoryOnlyNode.put("contactFactors", toLeafList(ExposureContactFactor.getValues(category, null, true)));
		categoryOnlyNode.put("protectiveMeasures", toLeafList(ExposureProtectiveMeasure.getValues(category, null, true)));
		categoryNode.put("categoryOnly", categoryOnlyNode);

		return categoryNode;
	}

	private static Map<String, Object> buildSettingNode(ExposureCategory category, ExposureSetting setting) {
		Map<String, Object> settingNode = new LinkedHashMap<>();
		settingNode.put("name", setting.name());
		settingNode.put("deprecated", setting.isDeprecated());
		settingNode.put("subSettings", toLeafList(ExposureSubSetting.getValues(category, setting, true)));
		settingNode.put("contactFactors", toLeafList(ExposureContactFactor.getValues(category, setting, true)));
		settingNode.put("protectiveMeasures", toLeafList(ExposureProtectiveMeasure.getValues(category, setting, true)));
		return settingNode;
	}

	private static List<Map<String, Object>> toLeafList(Collection<?> values) {
		List<Map<String, Object>> result = new ArrayList<>();
		for (Object value : values) {
			if (value instanceof ExposureSubSetting) {
				ExposureSubSetting enumValue = (ExposureSubSetting) value;
				result.add(leaf(enumValue.name(), enumValue.isDeprecated()));
			} else if (value instanceof ExposureContactFactor) {
				ExposureContactFactor enumValue = (ExposureContactFactor) value;
				result.add(leaf(enumValue.name(), enumValue.isDeprecated()));
			} else if (value instanceof ExposureProtectiveMeasure) {
				ExposureProtectiveMeasure enumValue = (ExposureProtectiveMeasure) value;
				result.add(leaf(enumValue.name(), enumValue.isDeprecated()));
			} else if (value instanceof ExposureSetting) {
				ExposureSetting enumValue = (ExposureSetting) value;
				result.add(leaf(enumValue.name(), enumValue.isDeprecated()));
			} else if (value instanceof ExposureCategory) {
				ExposureCategory enumValue = (ExposureCategory) value;
				result.add(leaf(enumValue.name(), enumValue.isDeprecated()));
			} else {
				throw new IllegalArgumentException("Unsupported enum value type: " + value.getClass());
			}
		}
		return result;
	}

	private static Map<String, Object> leaf(String name, boolean deprecated) {
		Map<String, Object> leafNode = new LinkedHashMap<>();
		leafNode.put("name", name);
		leafNode.put("deprecated", deprecated);
		return leafNode;
	}
}
