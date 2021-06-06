package de.symeda.sormas.ui.utils.components.multidayselector;

import java.time.LocalDate;
import java.util.Properties;

import com.vaadin.data.Binder;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.utils.CssStyles;

public class MultiDaySelectorField extends CustomField<MultiDaySelectorDto> {

	private final Binder<MultiDaySelectorDto> binder = new Binder<>(MultiDaySelectorDto.class);

	private final CheckBox multiDaySelect;
	private final DateField firstDate;
	private final DateField lastDate;

	protected Properties properties;

	public MultiDaySelectorField() {
		multiDaySelect = new CheckBox();
		firstDate = new DateField();
		lastDate = new DateField();

		this.properties = new Properties();
	}

	@Override
	protected Component initContent() {
		setValue(new MultiDaySelectorDto());

		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(false);
		layout.setMargin(false);

		HorizontalLayout selectorLayout = new HorizontalLayout();

		multiDaySelect.setId("multiDaySelect");
		binder.forField(multiDaySelect).bind(MultiDaySelectorDto.MULTI_DAY);
		multiDaySelect.addValueChangeListener(e -> {
			getValue().setMultiDay(e.getValue());
			firstDate.setVisible(e.getValue());
			getValue().setFirstDate(null);
		});
		selectorLayout.addComponent(multiDaySelect);

		HorizontalLayout datesLayout = new HorizontalLayout();

		firstDate.setId("firstDate");
		firstDate.setWidth(150, Unit.PIXELS);
		binder.forField(firstDate).bind(MultiDaySelectorDto.FIRST_DATE);
		firstDate.setRangeEnd(LocalDate.now());
		firstDate.setVisible(getValue().isMultiDay());

		lastDate.setId("lastDate");
		lastDate.setWidth(150, Unit.PIXELS);
		binder.forField(lastDate).bind(MultiDaySelectorDto.LAST_DATE);
		lastDate.setRangeEnd(LocalDate.now());

		datesLayout.addComponents(firstDate, lastDate);

		layout.addComponents(selectorLayout, datesLayout);

		return layout;
	}

	@Override
	protected void doSetValue(MultiDaySelectorDto multiDaySelectorDto) {
		binder.setBean(multiDaySelectorDto);
	}

	@Override
	public MultiDaySelectorDto getValue() {
		return binder.getBean();
	}

	public void showCaptions() {
		String prefix = properties.getProperty("prefix");
		multiDaySelect.setCaption(I18nProperties.getPrefixCaption(prefix, properties.getProperty("multiDay")));
		multiDaySelect.removeStyleName(CssStyles.CAPTION_HIDDEN);
		firstDate.setCaption(I18nProperties.getPrefixCaption(prefix, properties.getProperty("firstDate")));
		firstDate.removeStyleName(CssStyles.CAPTION_HIDDEN);
		lastDate.setCaption(I18nProperties.getPrefixCaption(prefix, properties.getProperty("lastDate")));
		lastDate.removeStyleName(CssStyles.CAPTION_HIDDEN);
	}
}
