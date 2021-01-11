/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.util;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.core.FirebaseParameter;
import de.symeda.sormas.app.rest.RetroProvider;

public class ErrorReportingHelper {

	/**
	 * Sends an exception report to Firebase Analytics.
	 */
	public static void sendCaughtException(Exception e) {
		FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
		crashlytics.setCustomKey(FirebaseParameter.CONNECTION_ID, String.valueOf(RetroProvider.getLastConnectionId()));
		crashlytics.setCustomKey(FirebaseParameter.SERVER_URL, ConfigProvider.getServerRestUrl());
		crashlytics.recordException(e);
	}

	/**
	 * Sends an exception report to Firebase Analytics.
	 * 
	 * @param entity
	 *            The entity object (e.g. a case or contact) if this error is associated with one
	 */
	public static void sendCaughtException(Exception e, AbstractDomainObject entity) {
		if (entity != null) {
			FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
			crashlytics.setCustomKey(FirebaseParameter.ENTITY_TYPE, entity.getClass().getSimpleName());
			crashlytics.setCustomKey(FirebaseParameter.ENTITY_UUID, entity.getUuid());
		}
		sendCaughtException(e);
	}
}
