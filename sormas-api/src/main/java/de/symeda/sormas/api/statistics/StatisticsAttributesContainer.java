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

import java.util.List;
import java.util.stream.Stream;
import java.util.function.Function;
import java.util.function.Predicate;

public class StatisticsAttributesContainer {
	
	private final List<StatisticsAttributeGroup> groups;
	
	public StatisticsAttributesContainer (List<StatisticsAttributeGroup> groups) {
		this.groups = groups;
	}
	
	public List<StatisticsAttributeGroup> values () {
		return groups;
	}
	
	public StatisticsAttribute get (final StatisticsAttributeEnum _enum) {
		return groups.stream()
					.flatMap(new Function<StatisticsAttributeGroup, Stream<StatisticsAttribute>>() {
					    @Override
					    public Stream<StatisticsAttribute> apply(StatisticsAttributeGroup n) {
					        return n.getAttributes().stream();
					    }
					})
					.filter(new Predicate<StatisticsAttribute>() {
					    @Override
					    public boolean test(StatisticsAttribute n) {
					        return n.getBaseEnum() == _enum;
					    }
					})
					.findFirst().orElse(null);
	}
	
	public StatisticsSubAttribute get (final StatisticsSubAttributeEnum _enum) {
		return groups.stream()
					.flatMap(new Function<StatisticsAttributeGroup, Stream<StatisticsAttribute>>() {
					    @Override
					    public Stream<StatisticsAttribute> apply(StatisticsAttributeGroup n) {
					        return n.getAttributes().stream();
					    }
					})
					.flatMap(new Function<StatisticsAttribute, Stream<StatisticsSubAttribute>>() {
					    @Override
					    public Stream<StatisticsSubAttribute> apply(StatisticsAttribute n) {
					        return n.getSubAttributes().stream();
					    }
					})
					.filter(new Predicate<StatisticsSubAttribute>() {
					    @Override
					    public boolean test(StatisticsSubAttribute n) {
					        return n.getBaseEnum() == _enum;
					    }
					})
					.findFirst().orElse(null);
	}
}
