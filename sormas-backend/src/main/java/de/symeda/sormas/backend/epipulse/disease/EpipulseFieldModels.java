/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.backend.epipulse.disease;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.backend.epipulse.fieldmodel.FieldModel;

/**
 * Registry from subject code to field model.
 *
 * <p>
 * Dedicated models are listed once in {@link #DEDICATED}; all other codes use
 * {@link DefaultFieldModel}. Built models are cached and reused across exports.
 */
public final class EpipulseFieldModels {

	private static final Map<EpipulseSubjectCode, Supplier<FieldModel>> DEDICATED;

	static {
		Map<EpipulseSubjectCode, Supplier<FieldModel>> dedicated = new EnumMap<>(EpipulseSubjectCode.class);
		dedicated.put(EpipulseSubjectCode.PERT, PertussisFieldModel::create);
		dedicated.put(EpipulseSubjectCode.MEAS, MeasFieldModel::create);
		dedicated.put(EpipulseSubjectCode.PNEU, PneuFieldModel::create);
		dedicated.put(EpipulseSubjectCode.MENI, MeniFieldModel::create);
		dedicated.put(EpipulseSubjectCode.SYPH, SyphFieldModel::create);
		dedicated.put(EpipulseSubjectCode.CONSYPH, ConSyphFieldModel::create);
		dedicated.put(EpipulseSubjectCode.SALM, SalmFieldModel::create);
		dedicated.put(EpipulseSubjectCode.MALA, MalaFieldModel::create);
		dedicated.put(EpipulseSubjectCode.DENGUE, DengueFieldModel::create);
		dedicated.put(EpipulseSubjectCode.GONO, GonoFieldModel::create);
		dedicated.put(EpipulseSubjectCode.RUBE, RubeFieldModel::create);
		dedicated.put(EpipulseSubjectCode.SHIG, ShigFieldModel::create);
		DEDICATED = Collections.unmodifiableMap(dedicated);
	}

	private static final Map<EpipulseSubjectCode, FieldModel> CACHE = new ConcurrentHashMap<>();

	private EpipulseFieldModels() {
	}

	public static FieldModel forSubjectCode(EpipulseSubjectCode subjectCode) {
		return CACHE.computeIfAbsent(subjectCode, code -> DEDICATED.containsKey(code) ? DEDICATED.get(code).get() : DefaultFieldModel.create(code));
	}

	/**
	 * @return whether this subject code has a field model of its own rather than the shared default
	 */
	public static boolean hasDedicatedModel(EpipulseSubjectCode subjectCode) {
		return DEDICATED.containsKey(subjectCode);
	}
}
