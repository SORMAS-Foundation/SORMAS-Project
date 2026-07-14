package de.symeda.sormas.ui.externalmessage.surveyresponse;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.externalmessage.survey.PatchField;
import de.symeda.sormas.api.patch.partial_retrieval.DisplayableFieldInfo;
import de.symeda.sormas.api.patch.partial_retrieval.DisplayablePartialRetrievalResponse;

/**
 * To avoid duplicating display logic for survey response part.
 */
public class SurveyResponseDisplayUtils {

	private SurveyResponseDisplayUtils() {
	}

	public static String resolveFieldName(PatchField patchField, DisplayablePartialRetrievalResponse displayData) {
		String groupNumberPrefix = patchField.getGroupIndexAsOptional().map(integer -> integer + ": ").orElse("");
		String fieldPath = patchField.getField();

		DisplayableFieldInfo info = displayData.getFieldInfoDictionary().get(fieldPath);
		String aliasPath = FacadeProvider.getPathAliasFacade().fetchAliasPath(fieldPath);
		if (info != null) {
			String translatedFieldName = info.getTranslatedFieldName();
			if (translatedFieldName != null) {
				return String.format("%s%s (%s)", groupNumberPrefix, translatedFieldName, aliasPath);
			}
		}
		return groupNumberPrefix + aliasPath;
	}

	public static String resolveCurrentValue(PatchField patchField, DisplayablePartialRetrievalResponse displayData) {
		String fieldPath = patchField.getField();
		DisplayableFieldInfo info = displayData.getFieldInfoDictionary().get(fieldPath);
		if (info != null && info.getTranslatedFieldValue() != null) {
			return info.getTranslatedFieldValue();
		}
		return "";
	}
}
