/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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
package de.symeda.sormas.ui.survey;

import java.util.List;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.survey.SurveyTokenReferenceDto;
import de.symeda.sormas.api.survey.external.views.ExternalSurveyView;
import de.symeda.sormas.api.survey.external.views.QuestionAnswersView;

/**
 * Modal window displaying a survey questionnaire as a structured HTML view.
 * Shows questions, their answers and nested subquestions.
 */
public class SurveyQuestionnaireWindow {

	public SurveyQuestionnaireWindow(SurveyTokenReferenceDto surveyTokenRef) {
		ExternalSurveyView surveyView;
		try {
			surveyView = FacadeProvider.getSurveyTokenFacade().getExternalSurveyView(surveyTokenRef.getUuid());
		} catch (Exception e) {
			surveyView = null;
		}

		Window window = new Window(surveyTokenRef.getCaption());
		window.setModal(true);
		window.setResizable(true);
		window.setWidth(700, com.vaadin.server.Sizeable.Unit.PIXELS);
		window.setHeight(80, com.vaadin.server.Sizeable.Unit.PERCENTAGE);

		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setMargin(true);
		mainLayout.setSpacing(false);

		if (surveyView == null || surveyView.getQuestionAnswersViews() == null || surveyView.getQuestionAnswersViews().isEmpty()) {
			mainLayout.addComponent(new Label("No questionnaire data available."));
		} else {
			Label htmlContent = new Label(buildHtml(surveyView.getQuestionAnswersViews()), ContentMode.HTML);
			htmlContent.setSizeFull();
			mainLayout.addComponent(htmlContent);
		}

		Panel scrollPanel = new Panel(mainLayout);
		scrollPanel.setSizeFull();

		window.setContent(scrollPanel);
		UI.getCurrent().addWindow(window);
	}

	private String buildHtml(List<QuestionAnswersView> questions) {
		StringBuilder sb = new StringBuilder();
		sb.append("<table style=\"width:100%;border-collapse:collapse;font-family:sans-serif;font-size:13px;\">");
		sb.append("<thead><tr style=\"background:#f0f0f0;\">")
			.append("<th style=\"text-align:left;padding:6px 8px;border-bottom:2px solid #ccc;\">Question</th>")
			.append("<th style=\"text-align:left;padding:6px 8px;border-bottom:2px solid #ccc;\">Answer</th>")
			.append("</tr></thead>");
		sb.append("<tbody>");

		for (QuestionAnswersView q : questions) {
			appendQuestion(sb, q, 0);
		}

		sb.append("</tbody></table>");
		return sb.toString();
	}

	private void appendQuestion(StringBuilder sb, QuestionAnswersView q, int depth) {
		boolean hasSubquestions = q.getSubquestions() != null && !q.getSubquestions().isEmpty();
		String indent = depth > 0 ? "padding-left:" + (depth * 20) + "px;" : "";
		String rowStyle = depth > 0 ? "background:#fafafa;" : "background:#fff;";
		String questionStyle = hasSubquestions && q.getAnswer() == null ? "font-weight:bold;color:#333;" : "color:#333;";

		sb.append("<tr style=\"")
			.append(rowStyle)
			.append("border-bottom:1px solid #eee;\">")
			.append("<td style=\"padding:5px 8px;")
			.append(indent)
			.append(questionStyle)
			.append("\">")
			.append(escapeHtml(q.getQuestion()))
			.append("</td>")
			.append("<td style=\"padding:5px 8px;color:#555;\">")
			.append(resolveAnswer(q))
			.append("</td>")
			.append("</tr>");

		if (hasSubquestions) {
			for (QuestionAnswersView sub : q.getSubquestions()) {
				appendQuestion(sb, sub, depth + 1);
			}
		}
	}

	private String resolveAnswer(QuestionAnswersView q) {
		// Prefer human-readable answerText, fall back to raw answer
		if (q.getAnswerText() != null && !q.getAnswerText().isEmpty()) {
			return escapeHtml(q.getAnswerText());
		}
		if (q.getAnswer() != null && !q.getAnswer().isEmpty()) {
			return escapeHtml(q.getAnswer());
		}
		return "<span style=\"color:#bbb;\">—</span>";
	}

	private String escapeHtml(String text) {
		if (text == null) {
			return "";
		}
		return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
	}
}
