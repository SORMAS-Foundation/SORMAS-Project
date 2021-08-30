package de.symeda.sormas.ui.immunization.components.fields.popup;

import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.ui.immunization.components.fields.info.ImmunizationInfo;
import de.symeda.sormas.ui.immunization.components.fields.info.InfoLayout;
import de.symeda.sormas.ui.utils.CssStyles;

public class SimilarImmunizationPopup extends VerticalLayout {

	public SimilarImmunizationPopup(ImmunizationDto immunization, ImmunizationDto similarImmunization) {

		setSpacing(true);
		setSizeUndefined();
		setWidth(100, Unit.PERCENTAGE);

		String infoText = String.format(I18nProperties.getString(Strings.infoSimilarImmunization), immunization.getDisease().toString());
		InfoLayout infoLayout = new InfoLayout(infoText);
		addComponent(infoLayout);
		CssStyles.style(infoLayout, CssStyles.VSPACE_3);

		ImmunizationInfo immunizationInfo = new ImmunizationInfo(I18nProperties.getString(Strings.infoImmunizationPeriod));
		immunizationInfo.addComponent(new ImmunizationInfoLayout(immunization));
		addComponent(immunizationInfo);

		ImmunizationInfo existingImmunization = new ImmunizationInfo(I18nProperties.getString(Strings.infoExistingImmunizationPeriod));
		existingImmunization.addComponent(new ImmunizationInfoLayout(similarImmunization));
		addComponent(existingImmunization);
	}
}
