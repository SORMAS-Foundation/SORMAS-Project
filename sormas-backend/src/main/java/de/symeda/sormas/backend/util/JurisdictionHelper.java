/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.sormas.backend.util;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.infrastructure.InfrastructureAdo;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;
import de.symeda.sormas.backend.infrastructure.region.Region;

public class JurisdictionHelper {

	private JurisdictionHelper() {
	}

	public static List<Region> getCaseRegions(Case caze) {
		List<Region> regions = new ArrayList<>();
		regions.add(caze.getResponsibleRegion());

		if (caze.getRegion() != null) {
			regions.add(caze.getRegion());
		}

		return regions;
	}

	public static List<Region> getContactRegions(Contact contact) {
		Case contactCase = contact.getCaze();

		List<Region> regions = new ArrayList<>();
		if (contact.getRegion() != null) {
			regions.add(contact.getRegion());
		} else {
			regions.addAll(getCaseRegions(contactCase));
		}

		return regions;
	}

	public static Expression<Boolean> booleanSelector(CriteriaBuilder cb, Predicate jurisdictionPredicate) {
		return cb.<Boolean> selectCase().when(jurisdictionPredicate, cb.literal(true)).otherwise(cb.literal(false));
	}

	public static InfrastructureAdo getParentInfrastructure(InfrastructureAdo infrastructure, JurisdictionLevel parentJurisdictionLevel) {

		if (infrastructure instanceof Facility) {
			if (parentJurisdictionLevel == JurisdictionLevel.COMMUNITY) {
				return ((Facility) infrastructure).getCommunity();
			} else {
				return ((Facility) infrastructure).getDistrict();
			}
		} else if (infrastructure instanceof PointOfEntry) {
			return ((PointOfEntry) infrastructure).getDistrict();
		} else if (infrastructure instanceof Community) {
			return ((Community) infrastructure).getDistrict();
		} else if (infrastructure instanceof District) {
			return ((District) infrastructure).getRegion();
		} else {
			throw new IllegalArgumentException("Infrastructure must be on district level or below to have a parent infrastructure");
		}
	}
}
