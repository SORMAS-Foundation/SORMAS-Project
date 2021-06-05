package de.symeda.sormas.ui.contact.components.linelisting.multidayselector;

import java.time.LocalDate;

import com.vaadin.data.Binder;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.utils.CssStyles;

public class MultiDaySelectorField extends CustomField<MultiDaySelectorDto> {

	private final Binder<MultiDaySelectorDto> binder = new Binder<>(MultiDaySelectorDto.class);

	private final CheckBox multiDaySelect;
	private final DateField firstDate;
	private final DateField lastDate;

	public MultiDaySelectorField() {
		multiDaySelect = new CheckBox();
		firstDate = new DateField();
		lastDate = new DateField();
	}

	@Override
	protected Component initContent() {
		setValue(new MultiDaySelectorDto());

		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(false);
		layout.setMargin(false);

		HorizontalLayout selectorLayout = new HorizontalLayout();

		binder.forField(multiDaySelect).bind(MultiDaySelectorDto.MULTI_DAY);
		multiDaySelect.addValueChangeListener(e -> {
			getValue().setMultiDay(e.getValue());
			firstDate.setVisible(e.getValue());
			getValue().setFirstDate(null);
		});
		selectorLayout.addComponent(multiDaySelect);

		HorizontalLayout datesLayout = new HorizontalLayout();

		firstDate.setWidth(150, Unit.PIXELS);
		binder.forField(firstDate).bind(MultiDaySelectorDto.FIRST_DATE);
		firstDate.setRangeEnd(LocalDate.now());
		firstDate.setVisible(getValue().isMultiDay());

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
		multiDaySelect.setCaption(I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.MULTI_DAY_CONTACT));
		multiDaySelect.removeStyleName(CssStyles.CAPTION_HIDDEN);
		firstDate.setCaption(I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.FIRST_CONTACT_DATE));
		firstDate.removeStyleName(CssStyles.CAPTION_HIDDEN);
		lastDate.setCaption(I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.LAST_CONTACT_DATE));
		lastDate.removeStyleName(CssStyles.CAPTION_HIDDEN);
	}
}
