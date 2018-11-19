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
package de.symeda.sormas.ui.samples;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.sample.SampleTestDto;
import de.symeda.sormas.api.sample.SampleTestResultType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class SampleTestListEntry extends HorizontalLayout {

	private final SampleTestDto sampleTest;
	private Button editButton;

	public SampleTestListEntry(SampleTestDto sampleTest) {

		setSpacing(true);
		setWidth(100, Unit.PERCENTAGE);
		addStyleName(CssStyles.SORMAS_LIST_ENTRY);
		this.sampleTest = sampleTest;

		VerticalLayout labelLayout = new VerticalLayout();
		labelLayout.setWidth(100, Unit.PERCENTAGE);
		addComponent(labelLayout);
		setExpandRatio(labelLayout, 1);

		// very hacky: clean up when needed elsewhere!
		HorizontalLayout topLabelLayout = new HorizontalLayout();
		topLabelLayout.setWidth(100, Unit.PERCENTAGE);
		labelLayout.addComponent(topLabelLayout);
		String htmlTop = LayoutUtil.divCss(CssStyles.LABEL_BOLD + " " + CssStyles.LABEL_UPPERCASE,
				DataHelper.toStringNullable(sampleTest.getTestType()))
				+ LayoutUtil.div(DataHelper.toStringNullable(sampleTest.getTestResultText()));
		Label labelTopLeft = new Label(htmlTop, ContentMode.HTML);
		topLabelLayout.addComponent(labelTopLeft);

		if (sampleTest.isTestResultVerified()) {
			Label labelTopRight = new Label(FontAwesome.CHECK_CIRCLE.getHtml(), ContentMode.HTML);
			labelTopRight.setSizeUndefined();
			labelTopRight.addStyleName(CssStyles.LABEL_LARGE);
			labelTopRight.setDescription(I18nProperties.getPrefixFieldCaption(SampleTestDto.I18N_PREFIX,
					SampleTestDto.TEST_RESULT_VERIFIED));
			topLabelLayout.addComponent(labelTopRight);
			topLabelLayout.setComponentAlignment(labelTopRight, Alignment.TOP_RIGHT);
		}

		HorizontalLayout bottomLabelLayout = new HorizontalLayout();
		bottomLabelLayout.setWidth(100, Unit.PERCENTAGE);
		labelLayout.addComponent(bottomLabelLayout);
		String htmlLeft = LayoutUtil.divCss(CssStyles.LABEL_BOLD + " " + CssStyles.LABEL_UPPERCASE
				+ " " + (sampleTest.getTestResult() == SampleTestResultType.POSITIVE ? CssStyles.LABEL_CRITICAL : 
					(sampleTest.getTestResult() == SampleTestResultType.INDETERMINATE ? CssStyles.LABEL_WARNING : "")),
				DataHelper.toStringNullable(sampleTest.getTestResult()));
		Label labelLeft = new Label(htmlLeft, ContentMode.HTML);
		bottomLabelLayout.addComponent(labelLeft);

		String htmlRight = LayoutUtil.div(DateHelper.formatLocalShortDateTime(sampleTest.getTestDateTime()));
		Label labelRight = new Label(htmlRight, ContentMode.HTML);
		labelRight.addStyleName(CssStyles.ALIGN_RIGHT);
		bottomLabelLayout.addComponent(labelRight);
		bottomLabelLayout.setComponentAlignment(labelRight, Alignment.TOP_RIGHT);
	}

	public void addEditListener(ClickListener editClickListener) {
		if (editButton == null) {
			editButton = new Button(FontAwesome.PENCIL);
			CssStyles.style(editButton, ValoTheme.BUTTON_LINK, CssStyles.BUTTON_NO_PADDING);
			addComponent(editButton);
			setComponentAlignment(editButton, Alignment.MIDDLE_RIGHT);
			setExpandRatio(editButton, 0);
		}
		editButton.addClickListener(editClickListener);
	}

	public SampleTestDto getSampleTest() {
		return sampleTest;
	}
}
