package de.symeda.sormas.backend.importexport.hierarchical.engine;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.importexport.hierarchical.FieldChangeDto;
import de.symeda.sormas.api.utils.DateHelper;

public final class FieldDiff {

	private FieldDiff() {
	}

	public static List<FieldChangeDto> diff(ImportNode node, List<ColumnSpec> columns, Map<String, Object> before, Map<String, Object> after) {
		List<FieldChangeDto> changes = new ArrayList<>();
		for (ColumnSpec column : columns) {
			if (!column.isProperty() || node.cell(column.header()).isEmpty()) {
				continue;
			}
			Object oldValue = PropertyPaths.read(before.get(column.target()), column.propertyPath());
			Object newValue = PropertyPaths.read(after.get(column.target()), column.propertyPath());
			if (!sameValue(oldValue, newValue)) {
				changes.add(new FieldChangeDto(column.header(), display(oldValue), display(newValue)));
			}
		}
		return changes;
	}

	public static boolean sameValue(Object oldValue, Object newValue) {
		if (oldValue instanceof Date && newValue instanceof Date) {
			return ((Date) oldValue).getTime() == ((Date) newValue).getTime();
		}
		return Objects.equals(oldValue, newValue);
	}

	public static String display(Object value) {
		if (value == null) {
			return "";
		}
		if (value instanceof Date) {
			return DateHelper.formatLocalDate((Date) value, I18nProperties.getUserLanguage());
		}
		return value.toString();
	}
}
