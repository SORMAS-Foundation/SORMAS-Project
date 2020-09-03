/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.backend.classification;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.app.backend.common.DatabaseHelper;

public class DiseaseClassificationAppHelper {

	public static String buildDiseaseClassificationHtml(Disease disease) {
		StringBuilder html = new StringBuilder();
		html.append("<html><header><style>");

		// Add style definitions
		html.append(
			"body {\r\n" + " font-family: verdana;\r\n" + "}\r\n" + ".classification-rules .main-criteria {\r\n" + "  width: 95%;\r\n"
				+ "  border-radius: 8px;\r\n" + "  margin: auto;\r\n" + "  padding: 8px;\r\n" + "}\r\n"
				+ ".classification-rules .main-criteria.main-criteria-suspect {\r\n" + "  background: rgba(255, 215, 0, 0.6);\r\n"
				+ "  margin-bottom: 16px;\r\n" + "}\r\n" + ".classification-rules .main-criteria.main-criteria-probable {\r\n"
				+ "  background: rgba(255, 140, 0, 0.6);\r\n" + "  margin-bottom: 16px;\r\n" + "}\r\n"
				+ ".classification-rules .main-criteria.main-criteria-confirmed {\r\n" + "  background: rgba(255, 0, 0, 0.6);\r\n" + "}\r\n"
				+ ".classification-rules .headline {\r\n" + "  font-weight: bold;\r\n" + "}\r\n" + ".classification-rules .criteria {\r\n"
				+ "  width: calc(100% - 16px);\r\n" + "  border-radius: 8px;\r\n" + "  padding: 8px;\r\n" + "  margin-top: 6px;\r\n"
				+ "  background: rgba(244, 244, 244, 0.8);\r\n" + "  display: inline-block;\r\n" + "}\r\n"
				+ ".classification-rules .sub-criteria {\r\n" + "  width: 95%;\r\n" + "  margin-right: 10px;\r\n" + "  margin-left: auto;\r\n"
				+ "  margin-top: 6px;\r\n" + "  margin-bottom: 6px;\r\n" + "}\r\n" + ".classification-rules .sub-criteria .sub-criteria-content {\r\n"
				+ "  width: calc(100% - 8px);\r\n" + "  border-radius: 8px;\r\n" + "  padding: 8px;\r\n"
				+ "  background: rgba(244, 244, 244, 0.7);\r\n" + "  display: inline-block;\r\n" + "}</style></header><body>");

		DiseaseClassificationCriteria diseaseClassificationCriteria = DatabaseHelper.getDiseaseClassificationCriteriaDao().getByDisease(disease);
		if (diseaseClassificationCriteria.hasAnyCriteria()) {
			html.append(diseaseClassificationCriteria.getSuspectCriteria());
			html.append(diseaseClassificationCriteria.getProbableCriteria());
			html.append(diseaseClassificationCriteria.getConfirmedCriteria());
		}

		html.append("</body></html>");
		return html.toString();
	}
}
