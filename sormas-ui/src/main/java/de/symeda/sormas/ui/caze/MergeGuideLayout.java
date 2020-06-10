package de.symeda.sormas.ui.caze;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.CssStyles;

public class MergeGuideLayout extends VerticalLayout {

	private static final long serialVersionUID = -4739282529871338153L;

	public MergeGuideLayout() {

		setMargin(true);
		setSpacing(false);

		Label lblHeadingIntroduction = new Label(I18nProperties.getString(Strings.headingIntroduction));
		CssStyles.style(lblHeadingIntroduction, CssStyles.H2);
		addComponent(lblHeadingIntroduction);
		Label lblIntroduction = new Label(I18nProperties.getString(Strings.infoMergingExplanation));
		CssStyles.style(lblIntroduction, CssStyles.VSPACE_4);
		lblIntroduction.setContentMode(ContentMode.HTML);
		lblIntroduction.setWidth(100, Unit.PERCENTAGE);
		addComponent(lblIntroduction);

		Label lblHeadingHowTo = new Label(I18nProperties.getString(Strings.headingHowToMergeCases));
		CssStyles.style(lblHeadingHowTo, CssStyles.H2);
		addComponent(lblHeadingHowTo);
		Label lblHowTo = new Label(I18nProperties.getString(Strings.infoHowToMergeCases));
		addComponent(lblHowTo);

		Label lblHeadingMerge = new Label(I18nProperties.getCaption(Captions.actionMerge));
		CssStyles.style(lblHeadingMerge, CssStyles.H3);
		addComponent(lblHeadingMerge);
		Label lblMergeDescription = new Label(I18nProperties.getString(Strings.infoMergingMergeDescription));
		lblMergeDescription.setContentMode(ContentMode.HTML);
		lblMergeDescription.setWidth(100, Unit.PERCENTAGE);
		addComponent(lblMergeDescription);

		Label lblHeadingPick = new Label(I18nProperties.getCaption(Captions.actionPick));
		CssStyles.style(lblHeadingPick, CssStyles.H3);
		addComponent(lblHeadingPick);
		Label lblPickDescription = new Label(I18nProperties.getString(Strings.infoMergingPickDescription));
		CssStyles.style(lblPickDescription, CssStyles.VSPACE_4);
		lblPickDescription.setWidth(100, Unit.PERCENTAGE);
		addComponent(lblPickDescription);

		Label lblHeadingHide = new Label(I18nProperties.getCaption(Captions.actionHide));
		CssStyles.style(lblHeadingHide, CssStyles.H3);
		addComponent(lblHeadingHide);
		Label lblHideDescription = new Label(I18nProperties.getString(Strings.infoMergingHideDescription));
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
			I18nProperties.getString(Strings.infoCaseCompleteness) + I18nProperties.getString(Strings.infoCompletenessMerge) + "</br></br>"
				+ I18nProperties.getString(Strings.infoCalculateCompleteness));
		lblCompletenessDescription.setContentMode(ContentMode.HTML);
		lblCompletenessDescription.setWidth(100, Unit.PERCENTAGE);
		addComponent(lblCompletenessDescription);

		Label lblHeadingIgnoreRegion = new Label(I18nProperties.getCaption(Captions.caseFilterWithDifferentRegion));
		CssStyles.style(lblHeadingIgnoreRegion, CssStyles.H3);
		addComponent(lblHeadingIgnoreRegion);
		Label lblIgnoreRegionDescription = new Label(I18nProperties.getString(Strings.infoMergeIgnoreRegion));
		lblIgnoreRegionDescription.setContentMode(ContentMode.HTML);
		lblIgnoreRegionDescription.setWidth(100, Unit.PERCENTAGE);
		addComponent(lblIgnoreRegionDescription);
	}
}
