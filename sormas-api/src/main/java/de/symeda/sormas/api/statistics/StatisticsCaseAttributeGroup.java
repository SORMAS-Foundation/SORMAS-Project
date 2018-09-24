package de.symeda.sormas.api.statistics;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.I18nProperties;

public enum StatisticsCaseAttributeGroup {
	
	TIME,
	PLACE,
	PERSON,
	CASE;
	
	private List<StatisticsCaseAttribute> attributes;
	
	public List<StatisticsCaseAttribute> getAttributes() {
		if (attributes == null) {
			attributes = new ArrayList<>();
			for (StatisticsCaseAttribute attribute : StatisticsCaseAttribute.values()) {
				if (attribute.getAttributeGroup() == this) {
					attributes.add(attribute);
				}
			}
		}

		return attributes;
	}
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
	
}
