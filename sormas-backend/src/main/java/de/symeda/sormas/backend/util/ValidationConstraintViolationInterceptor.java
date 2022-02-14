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

package de.symeda.sormas.backend.util;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.validation.ConstraintViolationException;

import de.symeda.sormas.api.utils.ConstrainValidationHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidationConstraintViolationInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(ValidationConstraintViolationInterceptor.class);

	@AroundInvoke
	public Object handleValidationConstraintViolation(InvocationContext context) throws Exception {
		try {
			return context.proceed();
		} catch (ConstraintViolationException e) {
			logger.error(String.valueOf(e));
			throw new ValidationRuntimeException(ConstrainValidationHelper.getPropertyErrors(e.getConstraintViolations()));
		}
	}
}
