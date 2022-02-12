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

package de.symeda.sormas.backend.common;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import de.symeda.auditlog.api.Audited;

/**
 * An extension of the {@link AbstractDomainObject} that defines core data that is essential to the system.
 * The integral definition of core data is that it is the working representation of a real-world event that is archived once the work is
 * complete.
 */
@MappedSuperclass
@Audited
public class CoreAdo extends DeletableAdo {

    public static final String ARCHIVED = "archived";

    private boolean archived;

    @Column(nullable = false)
    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}
