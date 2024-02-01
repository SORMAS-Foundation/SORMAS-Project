/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.user;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.backend.common.QueryJoins;

public class UserRoleJoins extends QueryJoins<UserRole>  {
    private Join<UserRole, UserRight> userRights;

    public UserRoleJoins(From<?, UserRole> root) {
        super(root);
    }

    public Join<UserRole, UserRight> getUserRights() {
        return getOrCreate(userRights, UserRole.USER_RIGHTS, JoinType.LEFT, this::setUserRightsJoin);
    }

    private void setUserRightsJoin(Join<UserRole, UserRight> userRightsJoin) {
        this.userRights = userRightsJoin;
    }
}
