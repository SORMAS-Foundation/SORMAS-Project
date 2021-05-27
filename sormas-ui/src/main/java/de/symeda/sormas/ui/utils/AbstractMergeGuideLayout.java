/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.utils;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;

public abstract class AbstractMergeGuideLayout extends VerticalLayout {

	private static final long serialVersionUID = -4739282529871338153L;

	public AbstractMergeGuideLayout() {

		setMargin(true);
		setSpacing(false);

		Label lblHeadingIntroduction = new Label(I18nProperties.getString(Strings.headingIntroduction));
		CssStyles.style(lblHeadingIntroduction, CssStyles.H2);
		addComponent(lblHeadingIntroduction);
		Label lblIntroduction = new Label(I18nProperties.getString(getInfoMergingExplanationMessage()));
		CssStyles.style(lblIntroduction, CssStyles.VSPACE_4);
		lblIntroduction.setContentMode(ContentMode.HTML);
		lblIntroduction.setWidth(100, Unit.PERCENTAGE);
		addComponent(lblIntroduction);

		Label lblHeadingHowTo = new Label(I18nProperties.getString(getHeadingHowToMergeMessage()));
		CssStyles.style(lblHeadingHowTo, CssStyles.H2);
		addComponent(lblHeadingHowTo);
		Label lblHowTo = new Label(I18nProperties.getString(getInfoHowToMergeMessage()));
		addComponent(lblHowTo);

		Label lblHeadingMerge = new Label(I18nProperties.getCaption(Captions.actionMerge));
		CssStyles.style(lblHeadingMerge, CssStyles.H3);
		addComponent(lblHeadingMerge);
		Label lblMergeDescription = new Label(I18nProperties.getString(getInfoMergingMergeDescriptionMessage()));
		lblMergeDescription.setContentMode(ContentMode.HTML);
		lblMergeDescription.setWidth(100, Unit.PERCENTAGE);
		addComponent(lblMergeDescription);

		Label lblHeadingPick = new Label(I18nProperties.getCaption(Captions.actionPick));
		CssStyles.style(lblHeadingPick, CssStyles.H3);
		addComponent(lblHeadingPick);
		Label lblPickDescription = new Label(I18nProperties.getString(getInfoMergingPickDescriptionMessage()));
		CssStyles.style(lblPickDescription, CssStyles.VSPACE_4);
		lblPickDescription.setWidth(100, Unit.PERCENTAGE);
		addComponent(lblPickDescription);

		Label lblHeadingHide = new Label(I18nProperties.getCaption(Captions.actionHide));
		CssStyles.style(lblHeadingHide, CssStyles.H3);
		addComponent(lblHeadingHide);
		Label lblHideDescription = new Label(I18nProperties.getString(getInfoMergingHideDescriptionMessage()));
		CssStyles.style(lblHideDescription, CssStyles.VSPACE_4);
		lblHideDescription.setWidth(100, Unit.PERCENTAGE);
		lblHideDescription.setContentMode(ContentMode.HTML);
		addComponent(lblHideDescription);

		Label lblHeadingTermsDefinition = new Label(I18nProperties.getString(Strings.headingExplanationOfTerms));
		CssStyles.style(lblHeadingTermsDefinition, CssStyles.H2);
		addComponent(lblHeadingTermsDefinition);

		Label lblHeadingCompleteness = new Label(I18nProperties.getString(Strings.headingCompleteness));
		CssStyles.style(lblHeadingCompleteness, CssStyles.H3, CssStyles.VSPACE_TOP_5);
		addComponent(lblHeadingCompleteness);
		Label lblCompletenessDescription = new Label(
			I18nProperties.getString(getInfoCompletenessMessage()) + I18nProperties.getString(getInfoCompletenessMergeMessage()) + "</br></br>"
				+ I18nProperties.getString(getInfoCalculateCompletenessMessage()));
		lblCompletenessDescription.setContentMode(ContentMode.HTML);
		lblCompletenessDescription.setWidth(100, Unit.PERCENTAGE);
		addComponent(lblCompletenessDescription);

		Label lblHeadingIgnoreRegion = new Label(I18nProperties.getCaption(Captions.caseFilterWithDifferentRegion));
		CssStyles.style(lblHeadingIgnoreRegion, CssStyles.H3);
		addComponent(lblHeadingIgnoreRegion);
		Label lblIgnoreRegionDescription = new Label(I18nProperties.getString(getInfoMergeIgnoreRegionMessage()));
		lblIgnoreRegionDescription.setContentMode(ContentMode.HTML);
		lblIgnoreRegionDescription.setWidth(100, Unit.PERCENTAGE);
		addComponent(lblIgnoreRegionDescription);
	}

	protected abstract String getInfoMergingExplanationMessage();

	protected abstract String getHeadingHowToMergeMessage();

	protected abstract String getInfoHowToMergeMessage();

	protected abstract String getInfoMergingMergeDescriptionMessage();

	protected abstract String getInfoMergingPickDescriptionMessage();

	protected abstract String getInfoMergingHideDescriptionMessage();

	protected abstract String getInfoCompletenessMessage();

	protected abstract String getInfoCompletenessMergeMessage();

	protected abstract String getInfoCalculateCompletenessMessage();

	protected abstract String getInfoMergeIgnoreRegionMessage();
}
