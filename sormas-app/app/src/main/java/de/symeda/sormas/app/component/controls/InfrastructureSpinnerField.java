package de.symeda.sormas.app.component.controls;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.app.backend.infrastructure.InfrastructureAdo;
import de.symeda.sormas.app.component.Item;

public class InfrastructureSpinnerField extends ControlSpinnerField {

	public InfrastructureSpinnerField(Context context) {
		super(context);
	}

	public InfrastructureSpinnerField(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public InfrastructureSpinnerField(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void setSpinnerData(List<Item> items, Object selectedValue) {
		List<Item> processedItems = null;
		if (items != null) {
			processedItems = new ArrayList<>(items.size());
			for (Item i : items) {
				if (i.getValue() != null && ((InfrastructureAdo) i.getValue()).isArchived()) {
					processedItems.add(new Item(i.getKey() + " (" + I18nProperties.getString(Strings.inactive) + ")", i.getValue()));
				} else {
					processedItems.add(i);
				}
			}
		}

		super.setSpinnerData(processedItems, selectedValue);
	}
}
