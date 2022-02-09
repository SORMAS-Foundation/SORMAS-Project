package de.symeda.sormas.ui.utils.components.automaticdeletion;

import java.util.Date;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.deletionconfiguration.AutomaticDeletionInfoDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;

public class AutomaticDeletionLabel extends HorizontalLayout {

	public AutomaticDeletionLabel(AutomaticDeletionInfoDto automaticDeletionInfoDto) {
		setMargin(false);
		setSpacing(false);

		String infoIconDesciption = String.format(
			I18nProperties.getString(Strings.infoAutomaticDeletionTooltip),
			DateFormatHelper.formatDate(automaticDeletionInfoDto.getDeletionDate()),
			DateFormatHelper.formatDate(automaticDeletionInfoDto.getEndOfProcessing()),
			formatDeletionPeriod(automaticDeletionInfoDto.getDeletionPeriod()));
		Label infoIcon = new Label(VaadinIcons.INFO_CIRCLE.getHtml(), ContentMode.HTML);
		infoIcon.setDescription(infoIconDesciption, ContentMode.HTML);
		infoIcon.addStyleNames(CssStyles.VSPACE_TOP_4, CssStyles.HSPACE_RIGHT_4);
		addComponent(infoIcon);

		String infoText = String
			.format(I18nProperties.getString(Strings.infoAutomaticDeletion), DateFormatHelper.formatDate(automaticDeletionInfoDto.getDeletionDate()));
		Label infoTextLabel = new Label(infoText, ContentMode.HTML);
		infoTextLabel.addStyleName(CssStyles.VSPACE_TOP_4);
		addComponent(infoTextLabel);

		if (DateHelper.getDaysBetween(new Date(), automaticDeletionInfoDto.getDeletionDate()) < 181) {
			infoIcon.addStyleName(CssStyles.LABEL_CRITICAL);
			infoTextLabel.addStyleName(CssStyles.LABEL_CRITICAL);
		} else {
			infoIcon.addStyleName(CssStyles.LABEL_MINOR);
			infoTextLabel.addStyleName(CssStyles.LABEL_MINOR);
		}
	}

	private String formatDeletionPeriod(int deletionPeriod) {
		if (deletionPeriod < 31) {
			return String.format(I18nProperties.getString(Strings.infoAutomaticDeletionTooltipDays), deletionPeriod);
		} else if (deletionPeriod < 366) {
			return String.format(I18nProperties.getString(Strings.infoAutomaticDeletionTooltipMonths), deletionPeriod / 30);
		} else {
			return String.format(I18nProperties.getString(Strings.infoAutomaticDeletionTooltipYears), deletionPeriod / 365);
		}
	}
}
