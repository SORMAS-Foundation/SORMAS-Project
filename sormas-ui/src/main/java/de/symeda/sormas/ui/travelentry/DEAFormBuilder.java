package de.symeda.sormas.ui.travelentry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.server.Page;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.travelentry.DeaContentEntry;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.SormasFieldGroupFieldFactory;

public class DEAFormBuilder {

	private List<DeaContentEntry> deaContentEntries;
	private GridLayout gridLayout;
	private Boolean isCreate;
	private Map<String, Field<?>> fields;

	public DEAFormBuilder(List<DeaContentEntry> deaContentEntries, Boolean isCreate) {
		this.deaContentEntries = deaContentEntries;
		this.gridLayout = new GridLayout(2, (int) deaContentEntries.size() / 2);
		this.isCreate = isCreate;
		this.fields = new HashMap<>();
	}

	public List<DeaContentEntry> getDeaContentEntries() {
		ArrayList<DeaContentEntry> deaContentValueEntries = new ArrayList<>(this.deaContentEntries);
		deaContentValueEntries.forEach(deaContentEntry -> {
			Field<?> field = fields.get(deaContentEntry.getCaption());
			deaContentEntry.setValue((String) field.getValue());
		});
		return deaContentValueEntries;
	}

	public void buildForm() {
		SormasFieldGroupFieldFactory fieldFactory = new SormasFieldGroupFieldFactory(new FieldVisibilityCheckers(), UiFieldAccessCheckers.getNoop());

		for (DeaContentEntry deaContentEntry : deaContentEntries) {
			TextField textField = fieldFactory.createField(String.class, TextField.class);

			CssStyles.style(textField, CssStyles.TEXTFIELD_ROW);
			Page.Styles styles = Page.getCurrent().getStyles();
			styles.add("#" + textField.getCaption() + " { width: " + "100% !important; }");
			textField.setCaption(deaContentEntry.getCaption());
			if (!isCreate) {
				textField.setValue(deaContentEntry.getValue());
			}
			gridLayout.addComponent(textField);
			fields.put(deaContentEntry.getCaption(), textField);
		}
	}

	public Component getLayout() {
		return gridLayout;
	}
}
