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
package de.symeda.sormas.api.statistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.user.UserRole;
//import de.symeda.sormas.ui.statistics.StatisticsFilterElement.TokenizableValue;

@SuppressWarnings("hiding")
public class StatisticsAttribute {

	@SuppressWarnings("rawtypes")
	private final Enum _enum;	
	StatisticsAttributeEnum baseEnum;
	StatisticsGroupingKey[] groupingKeys;
	private IValuesGetter valuesGetter;
	
	private final boolean sortByCaption;
	private final boolean unknownValueAllowed;
	private List<StatisticsSubAttribute> subAttributes = new ArrayList<StatisticsSubAttribute>();
	
	
	@SuppressWarnings("rawtypes")
	public StatisticsAttribute (Enum _enum, StatisticsAttributeEnum baseEnum, boolean sortByCaption, boolean unknownValueAllowed, List<StatisticsSubAttribute> subAttributes, StatisticsGroupingKey[] groupingKeys) {
		this._enum = _enum;
		this.baseEnum = baseEnum;
		this.groupingKeys = groupingKeys;

		this.sortByCaption = sortByCaption;
		this.unknownValueAllowed = unknownValueAllowed;
		this.subAttributes = subAttributes;
	}	
	
	@SuppressWarnings("rawtypes")
	public StatisticsAttribute (Enum _enum, StatisticsAttributeEnum baseEnum, boolean sortByCaption, boolean unknownValueAllowed, List<StatisticsSubAttribute> subAttributes, IValuesGetter valuesGetter) {
		this._enum = _enum;
		this.baseEnum = baseEnum;
		this.valuesGetter = valuesGetter;

		this.sortByCaption = sortByCaption;
		this.unknownValueAllowed = unknownValueAllowed;
		this.subAttributes = subAttributes;
	}	

	public boolean isSortByCaption() {
		return sortByCaption;
	}

	public boolean isUnknownValueAllowed() {
		return unknownValueAllowed;
	}
	
	public List<StatisticsSubAttribute> getSubAttributes() {
		return subAttributes;
	}
	
	public String toString() {
		return I18nProperties.getEnumCaption(_enum != null ? _enum : baseEnum);
	}
	
	public boolean isAgeGroup() {
		return baseEnum == StatisticsAttributeEnum.AGE_INTERVAL_1_YEAR 
				|| baseEnum == StatisticsAttributeEnum.AGE_INTERVAL_5_YEARS 
				|| baseEnum == StatisticsAttributeEnum.AGE_INTERVAL_BASIC 
				|| baseEnum == StatisticsAttributeEnum.AGE_INTERVAL_CHILDREN_COARSE
				|| baseEnum == StatisticsAttributeEnum.AGE_INTERVAL_CHILDREN_FINE 
				|| baseEnum == StatisticsAttributeEnum.AGE_INTERVAL_CHILDREN_MEDIUM;
	}
	
	public boolean isPopulationData() {
		return baseEnum == StatisticsAttributeEnum.REGION_DISTRICT 
				|| baseEnum == StatisticsAttributeEnum.SEX 
				|| isAgeGroup();
	}
	
	public StatisticsAttributeEnum getBaseEnum () {
		return baseEnum;
	}
	
	@SuppressWarnings("rawtypes")
	public static Enum getEnum (StatisticsAttribute attribute) {
		return attribute == null ? null : attribute._enum;
	}	
	
	public static StatisticsAttributeEnum getBaseEnum (StatisticsAttribute attribute) {
		return attribute == null ? null : attribute.baseEnum;
	}
	
	public Collection<? extends StatisticsGroupingKey> getValues () {
		if (valuesGetter != null)
			return valuesGetter.get(this);
		
		return Arrays.asList(groupingKeys);
	}

	public interface IValuesGetter {
		Collection<? extends StatisticsGroupingKey> get(StatisticsAttribute attribute);
	}
}
