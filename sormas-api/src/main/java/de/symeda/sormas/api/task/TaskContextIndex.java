/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 *******************************************************************************/

package de.symeda.sormas.api.task;


import de.symeda.sormas.api.audit.AuditIncludeProperty;
import de.symeda.sormas.api.audit.AuditedClass;

import java.io.Serializable;

@AuditedClass
public class TaskContextIndex implements Serializable{

    @AuditIncludeProperty
    private TaskContext taskContext;
    @AuditIncludeProperty
    private String uuid;

    public TaskContextIndex() {
    }

    public TaskContextIndex(TaskContext taskContext) {
        this.taskContext = taskContext;
    }



    public TaskContext getTaskContext() {
        return taskContext;
    }

    public String getUuid() {
        return uuid;
    }

    // Copy constructor
    public TaskContextIndex(TaskContextIndex other) {
        this.taskContext = other.taskContext; // Enum instances can be directly assigned
        this.uuid = other.uuid;
    }
    // Copy factory method
    public static TaskContextIndex copyOf(TaskContextIndex other) {
        return new TaskContextIndex(other);
    }

}

