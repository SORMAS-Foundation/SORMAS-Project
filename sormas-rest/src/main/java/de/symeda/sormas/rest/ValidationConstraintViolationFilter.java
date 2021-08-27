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

package de.symeda.sormas.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.validation.ConstraintViolationException;
import javax.validation.ElementKind;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;

@WebFilter("/*")
public class ValidationConstraintViolationFilter implements Filter {

	public ValidationConstraintViolationFilter() {
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
		throws IOException, ServletException {
		try {
			filterChain.doFilter(servletRequest, servletResponse);
		} catch (ConstraintViolationException e) {
			throw new ValidationRuntimeException(getPropertyErrors(e));
		}
	}

	@Override
	public void destroy() {

	}

	private Map<List<String>, String> getPropertyErrors(ConstraintViolationException e) {
		return e.getConstraintViolations().stream().map(v -> {
			List<String> path = new ArrayList<>();
			v.getPropertyPath().forEach(p -> {
				if (p.getKind() == ElementKind.PROPERTY) {
					if (p.getIndex() != null && path.size() > 0) {
						int pathSize = path.size();
						path.set(pathSize - 1, path.get(pathSize - 1) + "[" + p.getIndex() + "]");
					}

					path.add(p.getName());
				}
			});

			return new DataHelper.Pair<>(path, I18nProperties.getValidationError(v.getMessage(), v.getConstraintDescriptor().getAttributes()));
		}).collect(Collectors.toMap(DataHelper.Pair::getElement0, DataHelper.Pair::getElement1));
	}
}
