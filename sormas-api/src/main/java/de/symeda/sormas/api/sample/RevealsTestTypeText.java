/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package de.symeda.sormas.api.sample;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.symeda.sormas.api.Disease;

/**
 * Marks a {@link PathogenTestType} value as one whose selection should reveal the
 * pathogen-test "test type text" free-text companion field.
 *
 * <p>
 * Without any {@code diseases} listed, the value reveals the text field for every disease that
 * sees it. When a non-empty {@code diseases} array is given, the value reveals the text field
 * <em>only</em> for cases of one of those diseases — useful for disease-specific typing tests
 * (e.g. {@code SEROTYPING} reveals text only for Salmonellosis).
 *
 * <p>
 * Read at runtime by {@link PathogenTestType#revealsTestTypeText(PathogenTestType, Disease)}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RevealsTestTypeText {

	Disease[] diseases() default {};
}
