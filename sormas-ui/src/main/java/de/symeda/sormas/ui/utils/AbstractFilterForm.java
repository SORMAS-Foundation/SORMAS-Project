package de.symeda.sormas.ui.utils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.*;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import org.apache.commons.lang3.ArrayUtils;

import java.util.stream.Stream;

import static de.symeda.sormas.ui.utils.LayoutUtil.*;

public abstract class AbstractFilterForm<T> extends AbstractForm<T> {
	public static final String FILTER_ITEM_STYLE = "filter-item";

	private static final String RESET_BUTTON_ID = "reset";
	private static final String EXPAND_COLLAPSE_ID = "expandCollapse";
	private static final String MORE_FILTERS_ID = "moreFilters";

	private CustomLayout moreFiltersLayout;
	private boolean skipChangeEvents;

	protected AbstractFilterForm(Class<T> type, String propertyI18nPrefix) {
		super(type, propertyI18nPrefix, null,
				new SormasFieldGroupFieldFactory(null, null, null), true);

		String moreFiltersHtmlLayout = createMoreFiltersHtmlLayout();
		boolean hasMoreFilters = moreFiltersHtmlLayout != null && moreFiltersHtmlLayout.length() > 0;

		if (hasMoreFilters) {
			moreFiltersLayout = new CustomLayout();
			moreFiltersLayout.setTemplateContents(moreFiltersHtmlLayout);
			moreFiltersLayout.setVisible(false);
			getContent().addComponent(moreFiltersLayout, MORE_FILTERS_ID);

			addMoreFilters(moreFiltersLayout);
		}

		addDefaultButtons();
	}

	@Override
	protected String createHtmlLayout() {
		return div(filterLocs(ArrayUtils.addAll(getMainFilterLocators(), EXPAND_COLLAPSE_ID, RESET_BUTTON_ID)) +
				loc(MORE_FILTERS_ID));
	}

	protected abstract String[] getMainFilterLocators();

	protected String createMoreFiltersHtmlLayout() {
		return "";
	}

	public void addMoreFilters(CustomLayout moreFiltersContainer) {

	}

	protected void addDefaultButtons() {
		Button resetButton = ButtonHelper.createButton(Captions.actionResetFilters, null, FILTER_ITEM_STYLE);

		getContent().addComponent(resetButton, RESET_BUTTON_ID);

		if (moreFiltersLayout != null) {
			String showMoreCaption = I18nProperties.getCaption(Captions.actionShowMoreFilters);
			Button showHideMoreButton = ButtonHelper.createIconButtonWithCaption("showHideMoreFilters", showMoreCaption, VaadinIcons.CHEVRON_DOWN, e -> {
				Button showHideButton = e.getButton();
				boolean isShowMore = showHideButton.getCaption().equals(showMoreCaption);
				showHideButton.setCaption(isShowMore ? I18nProperties.getCaption(Captions.actionShowLessFilters) : showMoreCaption);
				showHideButton.setIcon(isShowMore ? VaadinIcons.CHEVRON_UP : VaadinIcons.CHEVRON_DOWN);

				if (isShowMore) {
					getContent().getComponent(MORE_FILTERS_ID).setVisible(true);
				} else {
					getContent().getComponent(MORE_FILTERS_ID).setVisible(false);
				}
			}, ValoTheme.BUTTON_BORDERLESS, CssStyles.VSPACE_TOP_NONE, CssStyles.LABEL_PRIMARY, RESET_BUTTON_ID);

			getContent().addComponent(showHideMoreButton, EXPAND_COLLAPSE_ID);
		}
	}

	protected CustomLayout getMoreFiltersContainer() {
		return (CustomLayout) getContent().getComponent(MORE_FILTERS_ID);
	}

	@Override
	protected <T1 extends Field> void formatField(T1 field, String propertyId) {
		super.formatField(field, propertyId);

		field.addStyleName(FILTER_ITEM_STYLE);

		String caption = I18nProperties.getPrefixCaption(propertyI18nPrefix, propertyId, field.getCaption());
		setFieldCaption(field, caption);

		if (TextField.class.isAssignableFrom(field.getClass())) {
			((TextField) field).addTextChangeListener(e -> field.setValue(e.getText()));
		}

		field.addValueChangeListener(e -> {
			onFieldValueChange(propertyId, e);
		});
	}

	public void addResetHandler(Button.ClickListener resetHandler) {
		((Button) getContent().getComponent(RESET_BUTTON_ID)).addClickListener(resetHandler);
	}

	@Override
	protected void applyFieldConfiguration(FieldConfiguration configuration, Field field) {
		super.applyFieldConfiguration(configuration, field);

		if (configuration.getCaption() != null) {
			setFieldCaption(field, configuration.getCaption());
		}
	}

	@Override
	public void setValue(T newFieldValue) throws ReadOnlyException, Converter.ConversionException {
		doWithoutChangeHandler(() -> {
			super.setValue(newFieldValue);

			applyDependenciesOnNewValue(newFieldValue);

			updateResetButtonState();
			if (moreFiltersLayout != null) {
				boolean hasExpandedFilter = streamFieldsForEmptyCheck(moreFiltersLayout)
						.anyMatch(f -> !f.isEmpty());
				moreFiltersLayout.setVisible(hasExpandedFilter);
			}
		});
	}

	public void updateResetButtonState() {
		boolean hasFilter = streamFieldsForEmptyCheck(getContent())
				.anyMatch(f -> !f.isEmpty());

		getContent().getComponent(RESET_BUTTON_ID).setVisible(hasFilter);
	}

	protected Stream<Field> streamFieldsForEmptyCheck(CustomLayout layout) {
		return FieldHelper.streamFields(layout);
	}

	protected void applyDependenciesOnNewValue(T newValue) {

	}

	private void onFieldValueChange(String propertyId, Property.ValueChangeEvent event) {
		if (!skipChangeEvents) {
			try {
				doWithoutChangeHandler(() -> applyDependenciesOnFieldChange(propertyId, event));

				this.getFieldGroup().commit();
				this.fireValueChange(true);
			} catch (FieldGroup.CommitException ex) {
				ex.printStackTrace();
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

	interface Callable {
		void call();
	}
}
