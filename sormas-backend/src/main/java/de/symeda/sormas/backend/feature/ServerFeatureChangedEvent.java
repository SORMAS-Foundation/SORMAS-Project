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

package de.symeda.sormas.backend.feature;

import de.symeda.sormas.api.feature.FeatureType;

/**
 * Fired when a server feature is switched on or off (only when its value actually changes), so other parts of the backend
 * can react, for example by preparing data or regenerating files that depend on the active features.
 */
public class ServerFeatureChangedEvent {

	private final FeatureType featureType;
	private final boolean enabled;

	public ServerFeatureChangedEvent(FeatureType featureType, boolean enabled) {
		this.featureType = featureType;
		this.enabled = enabled;
	}

	public FeatureType getFeatureType() {
		return featureType;
	}

	public boolean isEnabled() {
		return enabled;
	}
}
