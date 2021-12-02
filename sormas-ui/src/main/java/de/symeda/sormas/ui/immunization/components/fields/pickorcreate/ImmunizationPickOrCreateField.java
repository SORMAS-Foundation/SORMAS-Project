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
import de.symeda.sormas.ui.immunization.components.fields.info.ImmunizationInfo;
import de.symeda.sormas.ui.immunization.components.fields.info.InfoLayout;
import de.symeda.sormas.ui.utils.CssStyles;

public class ImmunizationPickOrCreateField extends CustomField<String> {

	public static final String KEEP_IMMUNIZATION = "keepImmunization";
	public static final String OVERWRITE_IMMUNIZATION = "overwriteImmunization";
	public static final String CREATE_NEW_IMMUNIZATION = "createNewImmunization";

	private final ImmunizationDto newImmunization;
	private final ImmunizationDto similarImmunization;

	private String selectedImmunizationUuid;

	private Consumer<Boolean> selectionChangeCallback;

	public ImmunizationPickOrCreateField(ImmunizationDto newImmunization, List<ImmunizationDto> similarImmunizations) {
		this.newImmunization = newImmunization;
		this.similarImmunization = similarImmunizations.isEmpty() ? null : similarImmunizations.get(0);
	}

	@SuppressWarnings("deprecation")
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

		ImmunizationInfo existingImmunization = new ImmunizationInfo(I18nProperties.getString(Strings.infoPickOrCreateImmunizationExisting));
		existingImmunization.addComponent(new ImmunizationInfoLayout(similarImmunization));
		mainLayout.addComponent(existingImmunization);

		ImmunizationPickOrCreateOption keepImmunization =
			new ImmunizationPickOrCreateOption(KEEP_IMMUNIZATION, I18nProperties.getCaption(Captions.immunizationKeepImmunization));
		mainLayout.addComponent(keepImmunization);

		ImmunizationInfo newImmunizationInfo = new ImmunizationInfo(I18nProperties.getString(Strings.infoPickOrCreateImmunizationNew));
		newImmunizationInfo.addComponent(new ImmunizationInfoLayout(newImmunization));
		mainLayout.addComponent(newImmunizationInfo);

		ImmunizationPickOrCreateOption overwriteImmunization =
			new ImmunizationPickOrCreateOption(OVERWRITE_IMMUNIZATION, I18nProperties.getCaption(Captions.immunizationOverwriteImmunization));
		mainLayout.addComponent(overwriteImmunization);

		ImmunizationPickOrCreateOption createNewImmunization =
			new ImmunizationPickOrCreateOption(CREATE_NEW_IMMUNIZATION, I18nProperties.getCaption(Captions.immunizationCreateNewImmunization));
		mainLayout.addComponent(createNewImmunization);

		keepImmunization.addValueChangeListener(e -> {
			if (e.getProperty().getValue() != null) {
				overwriteImmunization.setValue(null);
				createNewImmunization.setValue(null);
				doSetValue(null);
				if (selectionChangeCallback != null) {
					selectionChangeCallback.accept(true);
				}
			}
		});

		overwriteImmunization.addValueChangeListener(e -> {
			if (e.getProperty().getValue() != null) {
				keepImmunization.setValue(null);
				createNewImmunization.setValue(null);
				doSetValue(similarImmunization.getUuid());
				if (selectionChangeCallback != null) {
					selectionChangeCallback.accept(true);
				}
			}
		});

		createNewImmunization.addValueChangeListener(e -> {
			if (e.getProperty().getValue() != null) {
				overwriteImmunization.setValue(null);
				keepImmunization.setValue(null);
				doSetValue(newImmunization.getUuid());
				if (selectionChangeCallback != null) {
					selectionChangeCallback.accept(true);
				}
			}
		});

		return mainLayout;
	}

	@Override
	protected void doSetValue(String selectedImmunizationUuid) {
		this.selectedImmunizationUuid = selectedImmunizationUuid;
	}

	@Override
	public String getValue() {
		return this.selectedImmunizationUuid;
	}

	public void setSelectionChangeCallback(Consumer<Boolean> callback) {
		this.selectionChangeCallback = callback;
	}
}
