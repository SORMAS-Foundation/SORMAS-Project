package de.symeda.sormas.ui.utils;

import static de.symeda.sormas.ui.utils.LayoutUtil.div;
import static de.symeda.sormas.ui.utils.LayoutUtil.filterLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.locCss;

import java.util.Collections;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.AbstractField;
import com.vaadin.v7.ui.AbstractTextField;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.PopupDateField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.components.FormActionButtonsComponent;

public abstract class AbstractFilterForm<T> extends AbstractForm<T> {

	private static final long serialVersionUID = -692949260096914243L;

	public static final String FILTER_ITEM_STYLE = "filter-item";

	private static final String ACTION_BUTTONS_ID = "actionButtons";
	private static final String MORE_FILTERS_ID = "moreFilters";

	private CustomLayout moreFiltersLayout;
	private boolean skipChangeEvents;
	private boolean hasFilter;

	protected FormActionButtonsComponent formActionButtonsComponent;

	private JurisdictionFieldConfig jurisdictionFieldConfig;

	protected ComboBox regionFilter;
	protected ComboBox districtFilter;
	protected ComboBox communityFilter;

	protected AbstractFilterForm(Class<T> type, String propertyI18nPrefix, JurisdictionFieldConfig jurisdictionFieldConfig) {
		this(type, propertyI18nPrefix, null, jurisdictionFieldConfig);
	}

	protected AbstractFilterForm(Class<T> type, String propertyI18nPrefix, JurisdictionFieldConfig jurisdictionFieldConfig, boolean addFields) {
		this(type, propertyI18nPrefix, null, Captions.actionApplyFilters, Captions.actionResetFilters, jurisdictionFieldConfig, addFields);
	}

	protected AbstractFilterForm(
		Class<T> type,
		String propertyI18nPrefix,
		FieldVisibilityCheckers fieldVisibilityCheckers,
		JurisdictionFieldConfig jurisdictionFieldConfig) {
		this(
			type,
			propertyI18nPrefix,
			fieldVisibilityCheckers,
			Captions.actionApplyFilters,
			Captions.actionResetFilters,
			jurisdictionFieldConfig,
			true);
	}

	protected AbstractFilterForm(
		Class<T> type,
		String propertyI18nPrefix,
		FieldVisibilityCheckers fieldVisibilityCheckers,
		String applyCaptionTag,
		String resetCaptionTag,
		JurisdictionFieldConfig jurisdictionFieldConfig,
		boolean addFields) {

		super(type, propertyI18nPrefix, new SormasFieldGroupFieldFactory(fieldVisibilityCheckers, null), addFields);
		this.jurisdictionFieldConfig = jurisdictionFieldConfig;

		String moreFiltersHtmlLayout = createMoreFiltersHtmlLayout();
		boolean hasMoreFilters = moreFiltersHtmlLayout != null && moreFiltersHtmlLayout.length() > 0;

		if (hasMoreFilters) {
			moreFiltersLayout = new CustomLayout();
			moreFiltersLayout.setTemplateContents(moreFiltersHtmlLayout);
			moreFiltersLayout.setVisible(false);
			getContent().addComponent(moreFiltersLayout, MORE_FILTERS_ID);
		}

		formActionButtonsComponent = new FormActionButtonsComponent(applyCaptionTag, resetCaptionTag, moreFiltersLayout);
		getContent().addComponent(formActionButtonsComponent, ACTION_BUTTONS_ID);

		if (hasMoreFilters) {
			addMoreFilters(moreFiltersLayout);
		}

		this.addValueChangeListener(e -> {
			onChange();
		});

		addStyleName(CssStyles.FILTER_FORM);

		if (addFields) {
			initJurisdictionFields(jurisdictionFieldConfig);
		}
	}

	protected void initJurisdictionFields(JurisdictionFieldConfig jurisdictionFieldConfig) {
		if (jurisdictionFieldConfig != null) {
			regionFilter = jurisdictionFieldConfig.region != null ? getField(jurisdictionFieldConfig.region) : null;
			districtFilter = jurisdictionFieldConfig.district != null ? getField(jurisdictionFieldConfig.district) : null;
			communityFilter = jurisdictionFieldConfig.community != null ? getField(jurisdictionFieldConfig.community) : null;
		} else {
			regionFilter = null;
			districtFilter = null;
			communityFilter = null;
		}
	}

	public void onChange() {
		hasFilter = streamFieldsForEmptyCheck(getContent()).anyMatch(f -> !f.isEmpty());
	}

	@Override
	protected String createHtmlLayout() {
		return div(filterLocs(ArrayUtils.addAll(getMainFilterLocators(), ACTION_BUTTONS_ID)) + locCss(CssStyles.VSPACE_TOP_3, MORE_FILTERS_ID));

	}

	protected abstract String[] getMainFilterLocators();

	protected UserDto currentUserDto() {
		return UiUtil.getUser();
	}

	protected String createMoreFiltersHtmlLayout() {
		return "";
	}

	public void addMoreFilters(CustomLayout moreFiltersContainer) {

	}

	protected CustomLayout getMoreFiltersContainer() {
		return moreFiltersLayout;
	}

	@Override
	@SuppressWarnings({
		"rawtypes",
		"unchecked" })
	protected <T1 extends Field> void formatField(T1 field, String propertyId) {

		super.formatField(field, propertyId);

		field.addStyleName(FILTER_ITEM_STYLE);

		String caption = I18nProperties.getPrefixCaption(propertyI18nPrefix, propertyId, field.getCaption());
		setFieldCaption(field, caption);

		field.addValueChangeListener(e -> {
			onFieldValueChange(propertyId, e);
		});
	}

	public void addResetHandler(Button.ClickListener resetHandler) {
		formActionButtonsComponent.addResetHandler(resetHandler);
	}

	public void addApplyHandler(Button.ClickListener applyHandler) {
		formActionButtonsComponent.addApplyHandler(applyHandler);
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected void applyFieldConfiguration(FieldConfiguration configuration, Field field) {
		super.applyFieldConfiguration(configuration, field);

		if (configuration.getCaption() != null) {
			setFieldCaption(field, configuration.getCaption());
		}

		// set description tooltip for fields without caption and description
		if (field instanceof AbstractField) {
			AbstractField withDescription = (AbstractField) field;
			if (StringUtils.isBlank(withDescription.getCaption()) && StringUtils.isBlank(withDescription.getDescription())) {
				withDescription.setDescription(getFieldCaption(field));
			}
		}
	}

	@Override
	public void setValue(T newFieldValue) throws ReadOnlyException, Converter.ConversionException {

		doWithoutChangeHandler(() -> {
			super.setValue(newFieldValue);

			applyDependenciesOnNewValue(newFieldValue);

			if (moreFiltersLayout != null) {
				boolean hasExpandedFilter = streamFieldsForEmptyCheck(moreFiltersLayout).anyMatch(f -> !f.isEmpty() && f.isVisible());
				formActionButtonsComponent.toggleMoreFilters(hasExpandedFilter);
			}
		});

		if (newFieldValue != null && getJurisdictionFields().anyMatch(Field::isVisible) && UiUtil.enabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			hideAndFillJurisdictionFilters();
		}
	}

	protected Stream<ComboBox> getJurisdictionFields() {
		return Stream.of(regionFilter, districtFilter, communityFilter).filter(Objects::nonNull);
	}

	private void hideAndFillJurisdictionFilters() {
		hideAndFillJurisdictionField(regionFilter, () -> FacadeProvider.getRegionFacade().getDefaultInfrastructureReference());
		hideAndFillJurisdictionField(districtFilter, () -> FacadeProvider.getDistrictFacade().getDefaultInfrastructureReference());
		hideAndFillJurisdictionField(communityFilter, () -> FacadeProvider.getCommunityFacade().getDefaultInfrastructureReference());
	}

	private void hideAndFillJurisdictionField(@Nullable ComboBox field, Supplier<ReferenceDto> defaultValueGetter) {
		if (field != null) {
			field.setVisible(false);
			if (jurisdictionFieldConfig.prefillOnHide) {
				field.setValue(defaultValueGetter.get());
			}
		}
	}

	@SuppressWarnings("rawtypes")
	protected Stream<Field> streamFieldsForEmptyCheck(CustomLayout layout) {
		return FieldHelper.streamFields(layout);
	}

	protected void applyDependenciesOnNewValue(T newValue) {

	}

	protected void applyRegionFilterDependency(RegionReferenceDto region, String districtFieldId) {
		final UserDto user = UiUtil.getUser();
		final ComboBox districtField = getField(districtFieldId);
		DistrictReferenceDto userDistrict = user.getDistrict();

		if (user.getRegion() != null && userDistrict == null) {
			FieldHelper.updateItems(districtField, FacadeProvider.getDistrictFacade().getAllActiveByRegion(user.getRegion().getUuid()));
			districtField.setEnabled(true);
		} else if (region != null) {
			FieldHelper.updateItems(districtField, FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()));
			if (userDistrict == null) {
				districtField.setEnabled(true);
			}
		} else {
			districtField.setEnabled(false);
			FieldHelper.updateItems(districtField, Collections.singletonList(userDistrict));
			districtField.setValue(userDistrict);
		}
	}

	protected void applyRegionAndDistrictFilterDependency(
		RegionReferenceDto region,
		String districtFieldId,
		DistrictReferenceDto district,
		String communityFieldId) {
		applyRegionFilterDependency(region, districtFieldId);
		applyDistrictDependency(district, communityFieldId);
	}

	protected void applyDistrictDependency(DistrictReferenceDto district, String communityFieldId) {
		final UserDto user = UiUtil.getUser();
		final ComboBox communityField = getField(communityFieldId);
		if (user.getDistrict() != null && user.getCommunity() == null) {
			FieldHelper.updateItems(communityField, FacadeProvider.getCommunityFacade().getAllActiveByDistrict(user.getDistrict().getUuid()));
			communityField.setEnabled(true);
		} else {
			if (district != null) {
				FieldHelper.updateItems(communityField, FacadeProvider.getCommunityFacade().getAllActiveByDistrict(district.getUuid()));
				communityField.setEnabled(true);
			} else {
				communityField.setEnabled(false);
			}
		}
	}

	private void onFieldValueChange(String propertyId, Property.ValueChangeEvent event) {
		if (!skipChangeEvents) {
			try {
				doWithoutChangeHandler(() -> applyDependenciesOnFieldChange(propertyId, event));

				this.getFieldGroup().commit();
				this.fireValueChange(true);
			} catch (FieldGroup.CommitException ex) {
				// do nothing
			}
		}
	}

	protected void applyDependenciesOnFieldChange(String propertyId, Property.ValueChangeEvent event) {
	}

	private void doWithoutChangeHandler(Callable callback) {
		this.skipChangeEvents = true;
		try {
			callback.call();
		} finally {
			this.skipChangeEvents = false;
		}
	}

	@SuppressWarnings("rawtypes")
	private <T1 extends Field> void setFieldCaption(T1 field, String caption) {
		field.setCaption(null);
		if (field instanceof ComboBox) {
			((ComboBox) field).setInputPrompt(caption);
		} else if (field instanceof AbstractTextField) {
			((AbstractTextField) field).setInputPrompt(caption);
		} else if (field instanceof PopupDateField) {
			((PopupDateField) field).setInputPrompt(caption);
		} else {
			field.setCaption(caption);
		}
	}

	private <T1 extends Field> String getFieldCaption(T1 field) {
		if (field instanceof ComboBox) {
			return ((ComboBox) field).getInputPrompt();
		} else if (field instanceof AbstractTextField) {
			return ((AbstractTextField) field).getInputPrompt();
		} else if (field instanceof PopupDateField) {
			return ((PopupDateField) field).getInputPrompt();
		} else {
			return field.getCaption();
		}
	}

	public boolean hasFilter() {
		return hasFilter;
	}

	protected void clearAndDisableFields(Field... fields) {
		for (Field field : fields) {
			if (field != null) {
				field.setValue(null);
				field.setEnabled(false);
			}
		}
	}

	protected void clearAndDisableFields(String... propertyIds) {
		for (String propertyId : propertyIds) {
			Field<?> field = getField(propertyId);

			field.setValue(null);
			field.setEnabled(false);
		}
	}

	protected void enableFields(String... propertyIds) {
		updateFieldsEnabling(propertyIds, true);
	}

	protected void disableFields(String... propertyIds) {
		updateFieldsEnabling(propertyIds, false);
	}

	private void updateFieldsEnabling(String[] propertyIds, boolean enabled) {
		for (String propertyId : propertyIds) {
			getField(propertyId).setEnabled(enabled);
		}
	}

	protected void enableFields(Field... fields) {
		updateFieldsEnabling(fields, true);
	}

	protected void disableFields(Field... fields) {
		updateFieldsEnabling(fields, false);
	}

	private void updateFieldsEnabling(Field[] fields, boolean enabled) {
		for (Field field : fields) {
			if (field != null) {
				field.setEnabled(enabled);
			}
		}
	}

	interface Callable {

		void call();
	}

	protected static class JurisdictionFieldConfig {

		private final String region;
		private final String district;
		private final String community;

		private boolean prefillOnHide;

		private JurisdictionFieldConfig(String region, String district, String community) {
			this.region = region;
			this.district = district;
			this.community = community;
		}

		public static JurisdictionFieldConfig of(String region, String district, String community) {
			JurisdictionFieldConfig names = new JurisdictionFieldConfig(region, district, community);
			names.prefillOnHide = true;
			return names;
		}

		public static JurisdictionFieldConfig withNoPrefillOnHide(String region, String district, String community) {
			JurisdictionFieldConfig names = new JurisdictionFieldConfig(region, district, community);
			names.prefillOnHide = false;
			return names;
		}

	}
}
