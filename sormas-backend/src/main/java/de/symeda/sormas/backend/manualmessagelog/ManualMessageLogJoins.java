/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 */

package de.symeda.sormas.backend.manualmessagelog;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import de.symeda.sormas.backend.common.QueryJoins;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserJoins;

public class ManualMessageLogJoins extends QueryJoins<ManualMessageLog> {

    private Join<ManualMessageLog, User> sendingUser;
    private UserJoins sendingUserJoins;

    public ManualMessageLogJoins(From<?, ManualMessageLog> root) {
        super(root);
    }

    public Join<ManualMessageLog, User> getSendingUser() {
        return getOrCreate(sendingUser, ManualMessageLog.SENDING_USER, JoinType.LEFT, this::setSendingUser);
    }

    private void setSendingUser(Join<ManualMessageLog, User> sendingUser) {
        this.sendingUser = sendingUser;
    }

    public UserJoins getSendungUserJoins() {
        return getOrCreate(sendingUserJoins, () -> new UserJoins(getSendingUser()), this::setSendingUserJoins);
    }

    private void setSendingUserJoins(UserJoins sendingUserJoins) {
        this.sendingUserJoins = sendingUserJoins;
    }
}
