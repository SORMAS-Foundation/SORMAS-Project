package de.symeda.sormas.backend.patch.customizablefield;

import static de.symeda.sormas.backend.patch.customizablefield.CustomizableFieldContextPatchMapping.I18N_DICTIONARY;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import de.symeda.sormas.api.externalmessage.survey.PatchField;
import de.symeda.sormas.backend.patch.PatchFieldHelper;

@ApplicationScoped
public class CustomizableFieldHelper {

	public Optional<CustomizablePatchField> from(PatchField patchField) {
		String field = patchField.getField();

		if (!field.contains(PatchFieldHelper.CUSTOM_PREFIX)) {
			return Optional.empty();
		}

		String[] splittedField = field.split("\\.");

		if (splittedField.length != 3) {
			return Optional.empty();
		}

		return Optional.ofNullable(I18N_DICTIONARY.get(splittedField[1]))
			.map(
				context -> new CustomizablePatchField().setContext(context)
					.setLeafFieldName(splittedField[2])
					.setGroupIndex(patchField.getGroupIndex()));
	}
}
