package de.symeda.sormas.ui.immunization.components.fields.pickorcreate;

import java.util.List;
import java.util.function.Consumer;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.ui.utils.CssStyles;

public class ImmunizationPickOrCreateField extends CustomField<ImmunizationDto> {

	public static final String KEEP_IMMUNIZATION = "keepImmunization";
	public static final String OVERWRITE_IMMUNIZATION = "overwriteImmunization";

	private final ImmunizationDto newImmunization;
	private final ImmunizationDto similarImmunization;

	private ImmunizationDto selectedValue;

	private Consumer<Boolean> selectionChangeCallback;

	public ImmunizationPickOrCreateField(ImmunizationDto newImmunization, List<ImmunizationDto> similarImmunizations) {
		this.newImmunization = newImmunization;
		this.similarImmunization = similarImmunizations.isEmpty() ? null : similarImmunizations.get(0);
	}

	@Override
	protected Component initContent() {

		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setSpacing(true);
		mainLayout.setSizeUndefined();
		mainLayout.setWidth(100, Unit.PERCENTAGE);

		String infoText = String.format(I18nProperties.getString(Strings.infoPickOrCreateImmunization), newImmunization.getDisease().toString());
		InfoLayout infoLayout = new InfoLayout(infoText);
		mainLayout.addComponent(infoLayout);
		CssStyles.style(infoLayout, CssStyles.VSPACE_3);

		ImmunizationInfo existingImmunization =
			new ImmunizationInfo(similarImmunization, I18nProperties.getString(Strings.getInfoPickOrCreateImmunizationExisting));
		mainLayout.addComponent(existingImmunization);

		ImmunizationPickOrCreateOption keepImmunization =
			new ImmunizationPickOrCreateOption(KEEP_IMMUNIZATION, I18nProperties.getCaption(Captions.immunizationKeepImmunization));
		mainLayout.addComponent(keepImmunization);

		ImmunizationInfo immunizationInfo =
			new ImmunizationInfo(newImmunization, I18nProperties.getString(Strings.getInfoPickOrCreateImmunizationNew));
		mainLayout.addComponent(immunizationInfo);

		ImmunizationPickOrCreateOption overwriteImmunization =
			new ImmunizationPickOrCreateOption(OVERWRITE_IMMUNIZATION, I18nProperties.getCaption(Captions.immunizationOverwriteImmunization));
		mainLayout.addComponent(overwriteImmunization);

		keepImmunization.addValueChangeListener(e -> {
			if (e.getProperty().getValue() != null) {
				overwriteImmunization.setValue(null);
				doSetValue(similarImmunization);
				if (selectionChangeCallback != null) {
					selectionChangeCallback.accept(true);
				}
			}
		});

		overwriteImmunization.addValueChangeListener(e -> {
			if (e.getProperty().getValue() != null) {
				keepImmunization.setValue(null);
				doSetValue(newImmunization);
				if (selectionChangeCallback != null) {
					selectionChangeCallback.accept(true);
				}
			}
		});

		return mainLayout;
	}

	@Override
	protected void doSetValue(ImmunizationDto immunizationDto) {
		this.selectedValue = immunizationDto;
	}

	@Override
	public ImmunizationDto getValue() {
		return this.selectedValue;
	}

	public void setSelectionChangeCallback(Consumer<Boolean> callback) {
		this.selectionChangeCallback = callback;
	}
}
