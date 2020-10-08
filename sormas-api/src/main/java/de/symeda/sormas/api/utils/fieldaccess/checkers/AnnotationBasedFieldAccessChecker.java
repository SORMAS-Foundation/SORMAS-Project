/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.utils.fieldaccess.checkers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import de.symeda.sormas.api.utils.fieldaccess.FieldAccessChecker;

public abstract class AnnotationBasedFieldAccessChecker implements FieldAccessChecker {

	private Class<? extends Annotation> fieldAnnotation;
	private Class<? extends Annotation> embeddedAnnotation;
	private final boolean hasRight;

	protected AnnotationBasedFieldAccessChecker(
		Class<? extends Annotation> fieldAnnotation,
		Class<? extends Annotation> embeddedAnnotation,
		boolean hasRight) {
		this.fieldAnnotation = fieldAnnotation;
		this.embeddedAnnotation = embeddedAnnotation;
		this.hasRight = hasRight;
	}

	@Override
	public boolean isConfiguredForCheck(Field field, boolean withMandatory) {
		boolean annotationPresent = field.isAnnotationPresent(fieldAnnotation);

		if (!annotationPresent || withMandatory) {
			return annotationPresent;
		}

		return !isAnnotatedFieldMandatory(field);
	}

	protected abstract boolean isAnnotatedFieldMandatory(Field annotatedField);

	@Override
	public boolean isEmbedded(Field field) {
		return field.isAnnotationPresent(embeddedAnnotation);
	}

	@Override
	public boolean hasRight() {
		return hasRight;
	}

	public interface RightCheck {

		boolean check(boolean inJurisdiction);
	}
}
