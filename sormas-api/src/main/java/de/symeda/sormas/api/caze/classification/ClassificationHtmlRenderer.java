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
		return "<div class='v-slot v-slot-background-rounded-corners v-slot-background-"
				+ criteriaType.toString() 
				+ "-criteria v-slot-vspace-3' style='display: inline;' width='100%;'>"
				+ "<div class='v-verticallayout v-layout v-vertical v-widget background-rounded-corners "
				+ "v-verticallayout-background-rounded-corners background-" + criteriaType.toString() + "-criteria "
				+ "v-verticallayout-background-" + criteriaType.toString() + "-criteria "
				+ (marginBottom ? "vspace-3 v-verticallayout-vspace-3 " : "")
				+ "v-has-width' style='width: 100%;'>"
				+ content
				+ "</div></div>";
	}

	/**
	 * Creates a div containing the headline of a whole criteria.
	 */
	private static String createHeadlineDiv(String headline) {
		return "<div class='v-slot v-slot-bold'>"
				+ "<div class='v-label v-widget bold v-label-bold v-has-width' style='width: 100%;'>"
				+ headline + "</div></div>";
	}

	/**
	 * Creates a div containing an info text.
	 */
	private static String createInfoDiv() {
		return "<div class='v-slot'>"
				+ "<div class='v-label v-widget v-has-width' style='width: 100%;'>"
				+ "... when the case meets <b>ALL</b> of the following requirements:"
				+ "</div></div>";
	}

	/**
	 * Creates the surrounding div of a single part of the criteria.
	 */
	private static String createCriteriaSurroundingDiv(String content) {
		return "<div class='v-slot v-slot-background-rounded-corners "
				+ "v-slot-background-criteria v-slot-vspace-top-4'>"
				+ "<div class='v-verticallayout v-layout v-vertical v-widget background-rounded-corners "
				+ "v-verticallayout-background-rounded-corners background-criteria v-verticallayout-background-criteria "
				+ "vspace-top-4 v-verticallayout-vspace-top-4 v-has-width' style='width: 100%;'>"
				+ content
				+ "</div></div>";
	}

	/**
	 * Creates the surrounding div of a single sub criteria (with a slightly darker background).
	 */
	private static String createSubCriteriaSurroundingDiv(String content) {
		return "<div class='v-slot v-slot-background-rounded-corners v-slot-background-sub-criteria "
				+ "v-slot-vspace-top-4 v-slot-vspace-4 v-slot-hspace-right-3 v-align-right v-align-middle'>"
				+ "<div class='v-verticallayout v-layout v-vertical v-widget background-rounded-corners "
				+ "v-verticallayout-background-rounded-corners background-sub-criteria v-verticallayout-background-sub-criteria "
				+ "vspace-top-4 v-verticallayout-vspace-top-4 vspace-4 v-verticallayout-vspace-4 hspace-right-3 "
				+ "v-verticallayout-hspace-right-3 v-has-width' style='width: 95%;'>"
				+ content
				+ "</div></div>";
	}

	/**
	 * Creates the div for an actual criteria containing its description.
	 */
	private static String createCriteriaItemDiv(String text) {
		return "<div class='v-slot'>"
				+ "<div class='v-label v-widget v-has-width' style='width: 100%;'>"
				+ text
				+ "</div></div>";
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
