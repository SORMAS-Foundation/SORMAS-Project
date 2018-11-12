package de.symeda.sormas.api.caze.classification;

import de.symeda.sormas.api.I18nProperties;

/**
 * Provides methods that create HTML Strings to visualize the automatic classification rules.
 */
public class ClassificationHtmlRenderer {

	private ClassificationHtmlRenderer() { }

	public static String createSuspectHtmlString(DiseaseClassificationCriteria criteria) {
		StringBuilder sb = new StringBuilder();
		ClassificationCriteria suspectCriteria = criteria.getSuspectCriteria();
		if (suspectCriteria != null) {
			StringBuilder suspectSb = new StringBuilder();
			suspectSb.append(createHeadlineDiv("Suspect Classification"));
			suspectSb.append(createInfoDiv());
			suspectSb.append(buildCriteriaDiv(suspectCriteria));
			sb.append(createSurroundingDiv(ClassificationCriteriaType.SUSPECT, suspectSb.toString(), true));
		}
		
		return sb.toString();
	}
	
	public static String createProbableHtmlString(DiseaseClassificationCriteria criteria) {
		StringBuilder sb = new StringBuilder();
		ClassificationCriteria probableCriteria = criteria.getProbableCriteria();
		if (probableCriteria != null) {
			StringBuilder probableSb = new StringBuilder();
			probableSb.append(createHeadlineDiv("Probable Classification"));
			probableSb.append(createInfoDiv());
			probableSb.append(buildCriteriaDiv(probableCriteria));
			sb.append(createSurroundingDiv(ClassificationCriteriaType.PROBABLE, probableSb.toString(), true));
		}
		
		return sb.toString();
	}

	public static String createConfirmedHtmlString(DiseaseClassificationCriteria criteria) {
		StringBuilder sb = new StringBuilder();
		ClassificationCriteria confirmedCriteria = criteria.getConfirmedCriteria();
		if (confirmedCriteria != null) {
			StringBuilder confirmedSb = new StringBuilder();
			confirmedSb.append(createHeadlineDiv("Confirmed Classification"));
			confirmedSb.append(createInfoDiv());
			confirmedSb.append(buildCriteriaDiv(confirmedCriteria));
			sb.append(createSurroundingDiv(ClassificationCriteriaType.CONFIRMED, confirmedSb.toString(), false));
		}
		
		return sb.toString();
	}

	private static String buildCriteriaDiv(ClassificationCriteria criteria) {
		StringBuilder sb = new StringBuilder();

		if (!(criteria instanceof ClassificationCollectiveCriteria)) {
			// Create a single div if the criteria is not collective (i.e. has no sub criteria)
			String itemDiv = createCriteriaItemDiv(criteria.buildDescription());
			sb.append(createCriteriaSurroundingDiv(itemDiv));
		} else {
			// Otherwise, create a div and fill it by iterating over the sub criteria
			for (ClassificationCriteria subCriteria : ((ClassificationCollectiveCriteria) criteria).getSubCriteria()) {
				if (subCriteria instanceof ClassificationAllOfCriteria) {
					// If the sub criteria is an AllOfCriteria, every one of its sub criteria needs its own div
					for (ClassificationCriteria subSubCriteria : ((ClassificationCollectiveCriteria) subCriteria).getSubCriteria()) {
						sb.append(createCriteriaSurroundingDiv(buildSubCriteriaDiv(new StringBuilder(), subSubCriteria, subCriteria)));
					}
				} else {
					sb.append(createCriteriaSurroundingDiv(buildSubCriteriaDiv(new StringBuilder(), subCriteria, criteria)));
				}
			}
		}

		return sb.toString();
	}

	private static String buildSubCriteriaDiv(StringBuilder subCriteriaSb, ClassificationCriteria criteria, ClassificationCriteria parentCriteria) {
		// For non-collective criteria, only a simple div needs to be added
		if (!(criteria instanceof ClassificationCollectiveCriteria)) {
			subCriteriaSb.append(createCriteriaItemDiv(criteria.buildDescription()));
			return subCriteriaSb.toString();
		}

		// Add the criteria name to the div (e.g. "ONE OF")
		if (!(criteria instanceof ClassificationAllOfCriteria)) {
			subCriteriaSb.append(createCriteriaItemDiv(((ClassificationCollectiveCriteria) criteria).getCriteriaName()));
		}

		for (ClassificationCriteria subCriteria : ((ClassificationCollectiveCriteria) criteria).getSubCriteria()) {
			if (!(subCriteria instanceof ClassificationCollectiveCriteria) || subCriteria instanceof ClassificationCompactCriteria) {
				// For non-collective or compact collective criteria, add the description as a list item
				subCriteriaSb.append("- " + subCriteria.buildDescription()+ "</br>");
			} else if (parentCriteria instanceof ClassificationCollectiveCriteria && !(parentCriteria instanceof ClassificationAllOfCriteria)) {
				// For collective criteria, but not ClassificationAllOfCriteria, add a sub div with a slightly different color to make clear
				// that it belongs to the criteria listed before
				String itemDiv = createCriteriaItemDiv("<b>" + I18nProperties.getText("and").toUpperCase() + "</b>" + subCriteria.buildDescription());
				subCriteriaSb.append(createSubCriteriaSurroundingDiv(itemDiv));
			} else {
				// For everything else, recursively call this method to determine how to display the sub criteria
				buildSubCriteriaDiv(subCriteriaSb, subCriteria, criteria instanceof ClassificationAllOfCriteria ? parentCriteria : criteria);
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
		return "... when the case meets <b>ALL</b> of the following requirements:<br/>";
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
		CONFIRMED;

		@Override
		public String toString() {
			return name().toLowerCase();
		}
	}

}
