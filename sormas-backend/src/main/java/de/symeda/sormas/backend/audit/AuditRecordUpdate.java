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

package de.symeda.sormas.backend.audit;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.interceptor.InterceptorBinding;

/**
 * Interceptor binding annotation for auditing entity updates.
 * 
 * This annotation marks methods or classes that should have their entity update operations
 * audited. When applied, it triggers interceptor logic that captures and tracks changes
 * to entities.
 * 
 * <p>
 * The annotation can be applied at:
 * <ul>
 * <li><strong>Method level</strong> - to audit specific method calls that modify entities</li>
 * <li><strong>Class level</strong> - to audit all methods within a class that modify entities</li>
 * </ul>
 * 
 * <p>
 * Usage example:
 * 
 * <pre>
 * {@code @AuditRecordUpdate
 * public void updateUser(User user) {
 *     // Method implementation that modifies user entity
 * }
 * }
 * </pre>
 * 
 * @see javax.interceptor.InterceptorBinding
 */
@InterceptorBinding
@Target({
    METHOD,
    TYPE })
@Retention(RUNTIME)
public @interface AuditRecordUpdate {

}
