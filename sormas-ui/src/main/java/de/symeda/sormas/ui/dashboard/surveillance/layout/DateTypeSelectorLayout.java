package de.symeda.sormas.ui.dashboard.surveillance.layout;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.HorizontalLayout;

import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.components.datetypeselector.DateTypeSelectorComponent;

public class DateTypeSelectorLayout extends HorizontalLayout {

	private final DateTypeSelectorComponent dateTypeSelectorComponent;

	public DateTypeSelectorLayout() {
		dateTypeSelectorComponent =
			new DateTypeSelectorComponent.Builder<>(NewCaseDateType.class).dateTypePrompt(I18nProperties.getString(Strings.promptNewCaseDateType))
				.build();
		addComponent(dateTypeSelectorComponent);

		setSpacing(true);
		setSizeUndefined();
		setMargin(new MarginInfo(false, true, false, true));
	}

	public void addValueChangeListener(Property.ValueChangeListener valueChangeListener) {
		dateTypeSelectorComponent.addValueChangeListener(valueChangeListener);
	}
}
