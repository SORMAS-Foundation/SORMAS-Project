package de.symeda.sormas.ui.caze;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class MergeInstructionsLayout extends VerticalLayout {

	public MergeInstructionsLayout() {
		setMargin(true);
		setSpacing(false);
		
		Label lblIntroduction = new Label(I18nProperties.getString(Strings.infoMergingIntroduction));
		CssStyles.style(lblIntroduction, CssStyles.VSPACE_3);
		lblIntroduction.setWidth(100, Unit.PERCENTAGE);
		addComponent(lblIntroduction);
		
		Label lblActionsDescription = new Label(I18nProperties.getString(Strings.infoMergingActionsDescription));
		CssStyles.style(lblActionsDescription, CssStyles.VSPACE_4);
		lblActionsDescription.setWidth(100, Unit.PERCENTAGE);
		addComponent(lblActionsDescription);
		
		Label lblHeadingMerge = new Label(I18nProperties.getCaption(Captions.actionMerge));
		CssStyles.style(lblHeadingMerge, CssStyles.H3);
		addComponent(lblHeadingMerge);
		Label lblMergeDescription = new Label(I18nProperties.getString(Strings.infoMergingMergeDescription));
		CssStyles.style(lblMergeDescription, CssStyles.VSPACE_4);
		lblMergeDescription.setWidth(100, Unit.PERCENTAGE);
		addComponent(lblMergeDescription);

		Label lblHeadingPick = new Label(I18nProperties.getCaption(Captions.actionPick));
		CssStyles.style(lblHeadingPick, CssStyles.H3);
		addComponent(lblHeadingPick);
		Label lblPickDescription = new Label(I18nProperties.getString(Strings.infoMergingPickDescription));
		CssStyles.style(lblPickDescription, CssStyles.VSPACE_4);
		lblPickDescription.setWidth(100, Unit.PERCENTAGE);
		addComponent(lblPickDescription);

		Label lblHeadingDismiss = new Label(I18nProperties.getCaption(Captions.actionDismiss));
		CssStyles.style(lblHeadingDismiss, CssStyles.H3);
		addComponent(lblHeadingDismiss);
		Label lblDismissDescription = new Label(I18nProperties.getString(Strings.infoMergingDismissDescription));
		lblDismissDescription.setWidth(100, Unit.PERCENTAGE);
		addComponent(lblDismissDescription);
	}
	
}