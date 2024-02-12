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

import de.symeda.sormas.api.utils.EmbeddedPersonalData;
import de.symeda.sormas.api.utils.EmbeddedSensitiveData;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.fieldaccess.FieldAccessChecker;

public final class PseudonymizedFieldAccessChecker<T> implements FieldAccessChecker<T> {

	private final WrappedFieldAccessChecker wrapped;

	public PseudonymizedFieldAccessChecker(
		Class<? extends Annotation> annotation,
		Class<? extends Annotation> embeddedAnnotation,
		boolean isPseudonymized) {
		this.wrapped = new WrappedFieldAccessChecker(annotation, embeddedAnnotation, isPseudonymized);
	}

	@Override
	public boolean isConfiguredForCheck(Field field, boolean withMandatory) {
		return wrapped.isConfiguredForCheck(field, withMandatory);
	}

	@Override
	public boolean isEmbedded(Field field) {
		return wrapped.isEmbedded(field);
	}

	public boolean hasRight() {
		return wrapped.hasRight(null);
	}

	@Override
	public boolean hasRight(T object) {
		throw new UnsupportedOperationException();
	}

	private final class WrappedFieldAccessChecker extends AnnotationBasedFieldAccessChecker<T> {

		private WrappedFieldAccessChecker(
			Class<? extends Annotation> annotation,
			Class<? extends Annotation> embeddedAnnotation,
			boolean isPseudonymized) {
			super(annotation, embeddedAnnotation, !isPseudonymized, t -> false);
		}

		@Override
		protected boolean isAnnotatedFieldMandatory(Field annotatedField) {
			return false;
		}
	}

	public static <T> PseudonymizedFieldAccessChecker<T> forPersonalData(boolean isPseudonymized) {
		return new PseudonymizedFieldAccessChecker<>(PersonalData.class, EmbeddedPersonalData.class, isPseudonymized);
	}

	public static <T> PseudonymizedFieldAccessChecker<T> forSensitiveData(boolean isPseudonymized) {
		return new PseudonymizedFieldAccessChecker<>(SensitiveData.class, EmbeddedSensitiveData.class, isPseudonymized);
	}
}
