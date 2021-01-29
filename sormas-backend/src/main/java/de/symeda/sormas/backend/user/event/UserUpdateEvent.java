/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.user.event;

import java.util.function.Consumer;

import de.symeda.sormas.backend.user.User;

/**
 * Event fired when an existing user is updated.
 *
 * @author Alex Vidrean
 * @since 27-Aug-20
 */
public class UserUpdateEvent {

	private final User oldUser;

	private final User newUser;

	private Consumer<String> exceptionCallback;

	/**
	 * Used when there are not actual updates on the entity, but the Authentication Provider has to do a sync.
	 */
	public UserUpdateEvent(User user) {
		this.oldUser = user;
		this.newUser = user;
	}

	public UserUpdateEvent(User oldUser, User newUser) {
		this.oldUser = oldUser;
		this.newUser = newUser;
	}

	public User getOldUser() {
		return oldUser;
	}

	public User getNewUser() {
		return newUser;
	}

	public Consumer<String> getExceptionCallback() {
		return exceptionCallback;
	}

	public void setExceptionCallback(Consumer<String> exceptionCallback) {
		this.exceptionCallback = exceptionCallback;
	}
}
