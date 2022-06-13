/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.sormas.backend.user;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Create a mapping between the username provided by the session context and the actual DB entry to save round trips.
 */
public final class UserCache {

	private static UserCache instance;

	ConcurrentHashMap<String, User> cache;

	private UserCache() {
		cache = new ConcurrentHashMap<>();
	}

	public static synchronized UserCache getInstance() {
		if (instance == null) {
			instance = new UserCache();
		}
		return instance;
	}

	public void flush() {
		cache.clear();
	}

	public void put(String name, User user) {
		cache.put(name.toLowerCase(), user);
	}

	public User get(String name) {
		return cache.get(name.toLowerCase());
	}

	public void remove(String name) {
		cache.remove(name.toLowerCase());
	}

}
