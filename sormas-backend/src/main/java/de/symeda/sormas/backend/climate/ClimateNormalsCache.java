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

package de.symeda.sormas.backend.climate;

import java.util.function.Supplier;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * Normal ranges computed on request (decision 25), kept in memory. The key names the scope's locations, the reference
 * years and the history settings, so a new year or a changed setting simply uses another key. Each entry carries the
 * fingerprint of the rows it was computed from (decision 67); an entry whose rows have since been added to, changed or
 * deleted by any route is computed again.
 */
@Singleton
@LocalBean
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class ClimateNormalsCache {

	/** Enough for every district and its region, weekly and daily, in a large deployment. */
	private static final int MAX_ENTRIES = 5000;

	private final Cache<String, Entry> cache = CacheBuilder.newBuilder().maximumSize(MAX_ENTRIES).build();

	/**
	 * @return the cached value for the key when it was computed from rows with the same fingerprint, else the newly
	 *         computed value, which is then cached
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String key, String fingerprint, Supplier<T> compute) {

		Entry entry = cache.getIfPresent(key);
		if (entry != null && entry.fingerprint.equals(fingerprint)) {
			return (T) entry.value;
		}
		T value = compute.get();
		cache.put(key, new Entry(fingerprint, value));
		return value;
	}

	public void clear() {
		cache.invalidateAll();
	}

	private static final class Entry {

		private final String fingerprint;
		private final Object value;

		private Entry(String fingerprint, Object value) {
			this.fingerprint = fingerprint;
			this.value = value;
		}
	}
}
