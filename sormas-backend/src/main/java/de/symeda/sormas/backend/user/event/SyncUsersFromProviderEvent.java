/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.user.event;

import java.util.List;
import java.util.function.BiConsumer;

import de.symeda.sormas.backend.user.User;

public class SyncUsersFromProviderEvent {

	private final List<User> existingUsers;

	private final BiConsumer<List<User>, List<User>> callback;

	public SyncUsersFromProviderEvent(List<User> existingUsers, BiConsumer<List<User>, List<User>> callback) {
		this.existingUsers = existingUsers;
		this.callback = callback;
	}

	public List<User> getExistingUsers() {
		return existingUsers;
	}

	public BiConsumer<List<User>, List<User>> getCallback() {
		return callback;
	}
}
