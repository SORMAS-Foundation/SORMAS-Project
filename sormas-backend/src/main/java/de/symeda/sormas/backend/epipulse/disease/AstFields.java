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

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseLaboratoryMapper;
import de.symeda.sormas.api.epipulse.EpipulseVariable;
import de.symeda.sormas.backend.epipulse.fieldmodel.ValueDef;

/**
 * AST CSV field builders, one antibiotic at a time.
 *
 * <p>
 * AST is shared across several diseases but not universal, so it stays outside
 * {@link CommonFields}. MIC and SIR are split intentionally: some diseases declare only
 * {@code SIR_*}, while {@code MICSign_*}/{@code MICValueAST_*} apply only to specific codes.
 */
final class AstFields {

	private AstFields() {
	}

	/**
	 * Adds one drug's MIC sign/value columns plus their shared raw MIC source.
	 * Raw text is parsed once into {@link EpipulseLaboratoryMapper.MicReading}.
	 */
	static void addMic(
		List<ValueDef> fields,
		String key,
		String micExpression,
		EpipulseVariable micSignField,
		EpipulseVariable micValueField,
		Function<EpipulseDiseaseExportEntryDto, String> micSignCsv,
		Function<EpipulseDiseaseExportEntryDto, String> micValueCsv,
		BiConsumer<EpipulseDiseaseExportEntryDto, EpipulseLaboratoryMapper.MicReading> micSetter) {

		String micAlias = "mic_" + key;

		fields.add(
			ValueDef.sqlOnly(
				micAlias,
				micExpression,
				(dto, row) -> micSetter.accept(dto, EpipulseLaboratoryMapper.parseMic((String) row.get(micAlias)))));
		fields.add(ValueDef.calculated(micSignField, micSignCsv));
		fields.add(ValueDef.calculated(micValueField, micValueCsv));
	}

	/**
	 * Adds one drug's {@code SIR_*} interpretation column.
	 */
	static void addSir(
		List<ValueDef> fields,
		String key,
		String sirExpression,
		EpipulseVariable sirField,
		Function<EpipulseDiseaseExportEntryDto, String> sirCsv,
		BiConsumer<EpipulseDiseaseExportEntryDto, String> sirSetter) {

		String sirAlias = "sir_" + key;

		fields.add(ValueDef.direct(sirAlias, sirExpression, (dto, row) -> {
			String raw = (String) row.get(sirAlias);
			if (!StringUtils.isBlank(raw)) {
				sirSetter.accept(dto, raw);
			}
		}, sirField, sirCsv));
	}
}
