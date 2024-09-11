package de.symeda.sormas.ui.news;

import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.news.NewsCriteria;
import de.symeda.sormas.api.news.NewsIndexDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.FieldConfiguration;
import de.symeda.sormas.ui.utils.InfrastructureFieldsHelper;

public class NewsFilterForm extends AbstractFilterForm<NewsCriteria> {

	protected NewsFilterForm() {
		super(
			NewsCriteria.class,
			NewsIndexDto.I18N_PREFIX,
			FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale()),
			null);
	}

	@Override
	protected String[] getMainFilterLocators() {
		return new String[] {
			NewsCriteria.NEWS_LIKE,
			NewsCriteria.REGION,
			NewsCriteria.DISTRICT,
			NewsCriteria.COMMUNITY,
			NewsCriteria.RISK_LEVE,
			NewsCriteria.STATUS,
			NewsCriteria.START_DATE,
			NewsCriteria.END_DATE,
			NewsCriteria.IS_USER_LEVEL_FILER };
	}

	@Override
	protected void addFields() {
		addField(FieldConfiguration.withCaptionAndPixelSized(NewsCriteria.START_DATE, I18nProperties.getString(Strings.newsStartDate), 140));
		addField(FieldConfiguration.withCaptionAndPixelSized(NewsCriteria.END_DATE, I18nProperties.getString(Strings.newsEndDate), 140));
		addField(FieldConfiguration.withCaptionAndPixelSized(NewsCriteria.NEWS_LIKE, I18nProperties.getString(Strings.newsFilterText), 140));
		ComboBox region = addField(NewsCriteria.REGION, ComboBox.class);
		ComboBox district = addField(NewsCriteria.DISTRICT, ComboBox.class);
		ComboBox community = addField(NewsCriteria.COMMUNITY, ComboBox.class);

		InfrastructureFieldsHelper.initInfrastructureFields(region, district, community);

		if (UserProvider.getCurrent().getJurisdictionLevel() != JurisdictionLevel.NATION) {
			addField(NewsCriteria.IS_USER_LEVEL_FILER, CheckBox.class);
		}
		if (UserProvider.getCurrent().hasUserRight(UserRight.EDIT_NEWS)) {
			addFields(NewsCriteria.STATUS);
		}
	}
}
