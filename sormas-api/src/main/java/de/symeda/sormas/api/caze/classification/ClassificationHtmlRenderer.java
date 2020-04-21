/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.caze.classification;

import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.Language;
import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.classification.ClassificationXOfCriteriaDto.ClassificationXOfSubCriteriaDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.InfoProvider;

/**
 * Provides methods that create HTML Strings to visualize the automatic classification rules.
 */
public class ClassificationHtmlRenderer {

	private ClassificationHtmlRenderer() { }

	public static String createSuspectHtmlString(DiseaseClassificationCriteriaDto criteria) {
		StringBuilder sb = new StringBuilder();
		ClassificationCriteriaDto suspectCriteria = criteria.getSuspectCriteria();
		if (suspectCriteria != null) {
			StringBuilder suspectSb = new StringBuilder();
			suspectSb.append(createHeadlineDiv(I18nProperties.getString(Strings.classificationSuspect)));
			if (suspectCriteria instanceof ClassificationXOfCriteriaDto) {
				suspectSb.append(createInfoDiv(((ClassificationXOfCriteriaDto) suspectCriteria).getRequiredAmount()));
			} else {
				suspectSb.append(createInfoDiv());
			}
			suspectSb.append(buildCriteriaDiv(suspectCriteria));
			sb.append(createSurroundingDiv(ClassificationCriteriaType.SUSPECT, suspectSb.toString(), true));
		}

		return sb.toString();
	}

	public static String createProbableHtmlString(DiseaseClassificationCriteriaDto criteria) {
		StringBuilder sb = new StringBuilder();
		ClassificationCriteriaDto probableCriteria = criteria.getProbableCriteria();
		if (probableCriteria != null) {
			StringBuilder probableSb = new StringBuilder();
			probableSb.append(createHeadlineDiv(I18nProperties.getString(Strings.classificationProbable)));
			if (probableCriteria instanceof ClassificationXOfCriteriaDto) {
				probableSb.append(createInfoDiv(((ClassificationXOfCriteriaDto) probableCriteria).getRequiredAmount()));
			} else {
				probableSb.append(createInfoDiv());
			}
			probableSb.append(buildCriteriaDiv(probableCriteria));
			sb.append(createSurroundingDiv(ClassificationCriteriaType.PROBABLE, probableSb.toString(), true));
		}

		return sb.toString();
	}

	public static String createConfirmedHtmlString(DiseaseClassificationCriteriaDto criteria) {
		StringBuilder sb = new StringBuilder();
		ClassificationCriteriaDto confirmedCriteria = criteria.getConfirmedCriteria();
		if (confirmedCriteria != null) {
			StringBuilder confirmedSb = new StringBuilder();
			confirmedSb.append(createHeadlineDiv(I18nProperties.getString(Strings.classificationConfirmed)));
			if (confirmedCriteria instanceof ClassificationXOfCriteriaDto) {
				confirmedSb.append(createInfoDiv(((ClassificationXOfCriteriaDto) confirmedCriteria).getRequiredAmount()));
			} else {
				confirmedSb.append(createInfoDiv());
			}
			confirmedSb.append(buildCriteriaDiv(confirmedCriteria));
			sb.append(createSurroundingDiv(ClassificationCriteriaType.CONFIRMED, confirmedSb.toString(), false));
		}

		return sb.toString();
	}
	
	public static String createNotACaseHtmlString(DiseaseClassificationCriteriaDto criteria) {
		StringBuilder sb = new StringBuilder();
		ClassificationCriteriaDto notACaseCriteria = criteria.getNotACaseCriteria();
		if (notACaseCriteria != null) {
			StringBuilder notACaseSb = new StringBuilder();
			notACaseSb.append(createHeadlineDiv(I18nProperties.getString(Strings.classificationNotACase)));
			if (notACaseCriteria instanceof ClassificationXOfCriteriaDto) {
				notACaseSb.append(createInfoDiv(((ClassificationXOfCriteriaDto) notACaseCriteria).getRequiredAmount()));
			} else {
				notACaseSb.append(createInfoDiv());
			}
			notACaseSb.append(buildCriteriaDiv(notACaseCriteria));
			sb.append(createSurroundingDiv(ClassificationCriteriaType.NOT_A_CASE, notACaseSb.toString(), false));
		}

		return sb.toString();
	}

	public static String createHtmlForDownload(String sormasServerUrl, List<Disease> diseases, Language language) {
		StringBuilder html = new StringBuilder();
		html.append("<html><header><style>");

		// Add style definitions
		html.append("body {\r\n" +
				" font-family: verdana;\r\n" +
				"}\r\n" +
				".classification-rules .main-criteria {\r\n" + 
				"  font-size: 0.8em;\r\n" +
				"  width: 75%;\r\n" + 
				"  border-radius: 8px;\r\n" + 
				"  margin: auto;\r\n" +
				"  padding: 8px;\r\n" + 
				"}\r\n" + 
				".classification-rules .main-criteria.main-criteria-suspect {\r\n" + 
				"  background: rgba(255, 215, 0, 0.6);\r\n" + 
				"  margin-bottom: 16px;\r\n" + 
				"}\r\n" + 
				".classification-rules .main-criteria.main-criteria-probable {\r\n" + 
				"  background: rgba(255, 140, 0, 0.6);\r\n" + 
				"  margin-bottom: 16px;\r\n" + 
				"}\r\n" + 
				".classification-rules .main-criteria.main-criteria-confirmed {\r\n" + 
				"  background: rgba(255, 0, 0, 0.6);\r\n" + 
				"}\r\n" +
				".classification-rules .main-criteria.main-criteria-not_a_case {\r\n" + 
				"  background: rgba(201, 201, 201, 0.6);\r\n" + 
				"}\r\n" + 
				".classification-rules .headline {\r\n" + 
				"  font-weight: bold;\r\n" + 
				"}\r\n" + 
				".classification-rules .criteria {\r\n" + 
				"  width: calc(100% - 16px);\r\n" + 
				"  border-radius: 8px;\r\n" + 
				"  padding: 8px;\r\n" + 
				"  margin-top: 6px;\r\n" + 
				"  background: rgba(244, 244, 244, 0.8);\r\n" + 
				"  display: inline-block;\r\n" + 
				"}\r\n" + 
				".classification-rules .sub-criteria {\r\n" + 
				"  width: 95%;\r\n" + 
				"  margin-right: 10px;\r\n" + 
				"  margin-left: auto;\r\n" + 
				"  margin-top: 6px;\r\n" + 
				"  margin-bottom: 6px;\r\n" + 
				"}\r\n" + 
				".classification-rules .sub-criteria .sub-criteria-content {\r\n" + 
				"  width: calc(100% - 8px);\r\n" + 
				"  border-radius: 8px;\r\n" + 
				"  padding: 8px;\r\n" + 
				"  background: rgba(244, 244, 244, 0.7);\r\n" + 
				"  display: inline-block;\r\n" + 
				"}</style></header><body>");

		html.append("<h1 style=\"text-align: center; color: #005A9C;\">").append(I18nProperties.getString(Strings.classificationClassificationRules)).append("</h1>");
		html.append("<h4 style=\"text-align: center;\">")
				.append(I18nProperties.getString(Strings.classificationGeneratedFor))
				.append(" ").append(InfoProvider.get().getVersion())
				.append(StringUtils.wrap(I18nProperties.getString(Strings.on), " "))
				.append(sormasServerUrl).append(StringUtils.wrap(I18nProperties.getString(Strings.at), " "))
				.append(DateHelper.formatLocalDateTime(new Date(), language)).append("</h4>");

		for (Disease disease : diseases) {
			DiseaseClassificationCriteriaDto diseaseCriteria = FacadeProvider.getCaseClassificationFacade().getByDisease(disease);
			if (diseaseCriteria != null && diseaseCriteria.hasAnyCriteria()) {
				html.append("<h2 style=\"text-align: center; color: #005A9C;\">" + disease.toString() + "</h2>");
				html.append(createSuspectHtmlString(diseaseCriteria));
				html.append(createProbableHtmlString(diseaseCriteria));
				html.append(createConfirmedHtmlString(diseaseCriteria));
			}
		}
		html.append("</body></html>");

		return html.toString();
	}

	private static String buildCriteriaDiv(ClassificationCriteriaDto criteria) {
		StringBuilder sb = new StringBuilder();

		if (!(criteria instanceof ClassificationCollectiveCriteria)) {
			// Create a single div if the criteria is not collective (i.e. has no sub criteria)
			String itemDiv = createCriteriaItemDiv(criteria.buildDescription());
			sb.append(createCriteriaSurroundingDiv(itemDiv));
		} else {
			// Otherwise, create a div and fill it by iterating over the sub criteria
			for (ClassificationCriteriaDto subCriteria : ((ClassificationCollectiveCriteria) criteria).getSubCriteria()) {
				if (subCriteria instanceof ClassificationAllOfCriteriaDto && !((ClassificationAllOfCriteriaDto) subCriteria).isDrawSubCriteriaTogether()) {
					// If the sub criteria is an AllOfCriteria, every one of its sub criteria needs its own div
					for (ClassificationCriteriaDto subSubCriteria : ((ClassificationCollectiveCriteria) subCriteria).getSubCriteria()) {
						sb.append(createCriteriaSurroundingDiv(buildSubCriteriaDiv(new StringBuilder(), subSubCriteria, subCriteria)));
					}
				} else {
					sb.append(createCriteriaSurroundingDiv(buildSubCriteriaDiv(new StringBuilder(), subCriteria, criteria)));
				}
			}
		}

		return sb.toString();
	}

	private static String buildSubCriteriaDiv(StringBuilder subCriteriaSb, ClassificationCriteriaDto criteria, ClassificationCriteriaDto parentCriteria) {
		// For non-collective criteria, only a simple div needs to be added
		if (!(criteria instanceof ClassificationCollectiveCriteria)) {
			subCriteriaSb.append(createCriteriaItemDiv(criteria.buildDescription()));
			return subCriteriaSb.toString();
		}

		// Add the criteria name to the div (e.g. "ONE OF")
		if (!(criteria instanceof ClassificationAllOfCriteriaDto) || ((ClassificationAllOfCriteriaDto) criteria).isDrawSubCriteriaTogether()) {
			subCriteriaSb.append(createCriteriaItemDiv(((ClassificationCollectiveCriteria) criteria).getCriteriaName()));
		}

		for (ClassificationCriteriaDto subCriteria : ((ClassificationCollectiveCriteria) criteria).getSubCriteria()) {
			if (!(subCriteria instanceof ClassificationCollectiveCriteria) || subCriteria instanceof ClassificationCompactCriteria) {
				// For non-collective or compact collective criteria, add the description as a list item
				subCriteriaSb.append("- " + subCriteria.buildDescription()+ "</br>");
			} else if (subCriteria instanceof ClassificationCollectiveCriteria && !(subCriteria instanceof ClassificationAllOfCriteriaDto)
					&& !(subCriteria.getClass() == ClassificationXOfCriteriaDto.class)) {
				// For collective criteria, but not ClassificationAllOfCriteria, add a sub div with a slightly different color to make clear
				// that it belongs to the criteria listed before
				String itemDiv = null;
				if (subCriteria instanceof ClassificationXOfSubCriteriaDto && !((ClassificationXOfSubCriteriaDto) subCriteria).isAddition()) {
					itemDiv = createCriteriaItemDiv(subCriteria.buildDescription());
				} else {
					itemDiv = createCriteriaItemDiv("<b>" + I18nProperties.getString(Strings.and).toUpperCase() + "</b>" + subCriteria.buildDescription());
				}
				subCriteriaSb.append(createSubCriteriaSurroundingDiv(itemDiv));
			} else {
				// For everything else, recursively call this method to determine how to display the sub criteria
				buildSubCriteriaDiv(subCriteriaSb, subCriteria, criteria instanceof ClassificationAllOfCriteriaDto ? parentCriteria : criteria);
			}
		}

		return subCriteriaSb.toString();

	}

	/**
	 * Creates the surrounding div of a whole (suspect, probable or confirmed) criteria definition.
	 */
	private static String createSurroundingDiv(ClassificationCriteriaType criteriaType, String content, boolean marginBottom) {
		return "<div class='classification-rules'>"
				+ "<div class='main-criteria main-criteria-"
				+ criteriaType.toString()
				+ "'>"
				+ content
				+ "</div></div>";
	}

	/**
	 * Creates a div containing the headline of a whole criteria.
	 */
	private static String createHeadlineDiv(String headline) {
		return "<div class='headline'>"
				+ headline 
				+ "</div>";
	}

	/**
	 * Creates a div containing an info text.
	 */
	private static String createInfoDiv() {
		return I18nProperties.getString(Strings.classificationInfoText);
	}

	private static String createInfoDiv(int requirementsNumber) {
		return String.format(I18nProperties.getString(Strings.classificationInfoNumberText), DataHelper.parseNumberToString(requirementsNumber));
	}

	/**
	 * Creates the surrounding div of a single part of the criteria.
	 */
	private static String createCriteriaSurroundingDiv(String content) {
		return "<div class='criteria'>"
				+ content
				+ "</div>";
	}

	/**
	 * Creates the surrounding div of a single sub criteria (with a slightly darker background).
	 */
	private static String createSubCriteriaSurroundingDiv(String content) {
		return "<div class='sub-criteria'><div class='sub-criteria-content'>"
				+ content
				+ "</div></div>";
	}

	/**
	 * Creates the div for an actual criteria containing its description.
	 */
	private static String createCriteriaItemDiv(String text) {
		return text + "<br/>";
	}

	private enum ClassificationCriteriaType {
		SUSPECT,
		PROBABLE,
		CONFIRMED,
		NOT_A_CASE;

		@Override
		public String toString() {
			return name().toLowerCase();
		}
	}

}
