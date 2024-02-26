package de.symeda.sormas.ui.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import org.jetbrains.annotations.NotNull;

import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.CustomField;

public class CheckBoxTreeUpdated<ENUM extends Enum<?>> extends CustomField<Map<ENUM, Boolean>> {

	private static final String[] INDENTATION_STYLES = new String[] {
		CssStyles.INDENT_LEFT_1,
		CssStyles.INDENT_LEFT_2,
		CssStyles.INDENT_LEFT_3 };

	private Map<ENUM, CheckBox> enumToggles = new HashMap<>();
	private VerticalLayout checkBoxTreeLayout;
	private HorizontalLayout checkBoxTreeLayoutForGroups;
	private Class<ENUM> enumType;
	private boolean addVerticalSpaces;
	private UnaryOperator<ENUM> parentProvider;
	private Function<ENUM, ? extends Enum> groupProvider;
	private boolean settingToggles = false;
	private int columns;

	public CheckBoxTreeUpdated() {

	}

	public Map<ENUM, CheckBox> getEnumToggles() {
		return enumToggles;
	}

	@Override
	protected Component initContent() {

		if (enumType != null) {
			if (groupProvider == null) {
				checkBoxTreeLayout = new VerticalLayout();
				checkBoxTreeLayout.setMargin(false);
				checkBoxTreeLayout.setSpacing(false);
				checkBoxTreeLayout.setWidth(100, Sizeable.Unit.PERCENTAGE);
				addCheckBoxes();
				final MarginInfo marginInfo = new MarginInfo(false, false, true, false);
				checkBoxTreeLayout.setMargin(marginInfo);
			} else {
				checkBoxTreeLayoutForGroups = new HorizontalLayout();
				checkBoxTreeLayoutForGroups.setMargin(false);
				checkBoxTreeLayoutForGroups.setWidth(100, Unit.PERCENTAGE);
				addGroups();
			}

			settingToggles = true;
			setToggleValues(getValue());
			settingToggles = false;
		}

		addValueChangeListener(valueChangeEvent -> {
			Map<ENUM, Boolean> value = (Map<ENUM, Boolean>) valueChangeEvent.getProperty().getValue();
			final Map<ENUM, Boolean> nullSafeValue = value == null ? new HashMap<>() : value;
			settingToggles = true;
			enumToggles.forEach(((anEnum, checkBox) -> {
				final Boolean orDefault = nullSafeValue.getOrDefault(anEnum, false);
				if (!Objects.equals(checkBox.getValue(), orDefault)) {
					checkBox.setValue(orDefault);
				}
			}));
			settingToggles = false;
		});

		if (checkBoxTreeLayout != null) {
			return checkBoxTreeLayout;
		}

		return checkBoxTreeLayoutForGroups;
	}

	private void addGroups() {
		final ENUM[] enumElements = enumType.getEnumConstants();

		Map<Enum, List<ENUM>> enumGroupMap = new HashMap<>();

		for (ENUM enumElement : enumElements) {
			final Enum groupElement = groupProvider.apply(enumElement);
			if (enumGroupMap.containsKey(groupElement)) {
				enumGroupMap.get(groupElement).add(enumElement);
			} else {
				enumGroupMap.put(groupElement, new ArrayList<>());
				enumGroupMap.get(groupElement).add(enumElement);
			}
		}

		List<VerticalLayout> columnList = new ArrayList<>();

		for (int i = 0; i < columns; i++) {
			final VerticalLayout e = new VerticalLayout();
			e.setMargin(false);
			e.setSpacing(false);
			columnList.add(e);
		}

		AtomicInteger currentColumn = new AtomicInteger();
		currentColumn.set(0);
		enumGroupMap.entrySet().stream().forEach(enumListEntry -> {
			Label heading = new Label(enumListEntry.getKey().toString());
			CssStyles.style(heading, CssStyles.H4);
			columnList.get(currentColumn.get()).addComponent(heading);

			enumListEntry.getValue().stream().forEach(anEnum -> {
				CheckBox checkbox = createCheckbox(0, anEnum, null);
				enumToggles.put(anEnum, checkbox);
				columnList.get(currentColumn.get()).addComponent(checkbox);
			});

			if (currentColumn.get() == columns - 1) {
				currentColumn.set(0);
			} else {
				currentColumn.getAndIncrement();
			}
		});

		for (int i = 0; i < columns; i++) {
			checkBoxTreeLayoutForGroups.addComponent(columnList.get(i));
		}

		final MarginInfo marginInfo = new MarginInfo(false, false, true, false);
		checkBoxTreeLayoutForGroups.setMargin(marginInfo);

		settingToggles = true;
		setToggleValues(getValue());
		settingToggles = false;
	}

	private void addCheckBoxes() {

		final ENUM[] enumElements = enumType.getEnumConstants();

		for (ENUM enumElement : enumElements) {
			final ENUM parentElement = parentProvider.apply(enumElement);
			int level = getEnumElementLevel(enumElement, 0);
			CheckBox elementCheckbox = createCheckbox(level, enumElement, parentElement);

			enumToggles.put(enumElement, elementCheckbox);
			checkBoxTreeLayout.addComponent(elementCheckbox);
		}
	}

	private int getEnumElementLevel(ENUM enumElement, int level) {
		ENUM parentElement = parentProvider.apply(enumElement);
		if (parentElement != null) {
			level++;
			return getEnumElementLevel(parentElement, level);
		} else {
			return level;
		}
	}

	@NotNull
	private CheckBox createCheckbox(int level, ENUM element, ENUM parentElement) {
		CheckBox elementCheckbox = new CheckBox(element.toString());
		final int minLevel = Math.min(level - 1, INDENTATION_STYLES.length - 1);
		if (level > 0) {
			elementCheckbox.addStyleName(INDENTATION_STYLES[minLevel]);
		}
		if (addVerticalSpaces) {
			CssStyles.style(elementCheckbox, CssStyles.VSPACE_4);
		}

		elementCheckbox.setWidth(100, Unit.PERCENTAGE);
		elementCheckbox.addValueChangeListener(e -> {
			final Boolean value = (Boolean) e.getProperty().getValue();
			Map<ENUM, Boolean> newValue = new HashMap<>();
			if (getValue() != null) {
				newValue = new HashMap<>(getValue());
			}
			newValue.put(element, value);
			setValue(newValue);
		});

		if (parentElement != null) {
			final CheckBox parentCheckBox = enumToggles.get(parentElement);
			elementCheckbox.setVisible(parentCheckBox.getValue() != null && parentCheckBox.getValue());
			parentCheckBox.addValueChangeListener(parentChangeEvent -> {
				if (!settingToggles) {
					elementCheckbox.setValue(false);
				}

				elementCheckbox.setVisible((Boolean) parentChangeEvent.getProperty().getValue());
			});
		}
		return elementCheckbox;
	}

	public void setEnumType(Class<ENUM> enumType, UnaryOperator<ENUM> parentProvider) {
		this.enumType = enumType;
		this.parentProvider = parentProvider;
	}

	public void setEnumType(Class<ENUM> enumType, Function<ENUM, ? extends Enum<?>> groupProvider, int columns) {
		this.enumType = enumType;
		this.groupProvider = groupProvider;
		this.columns = columns;
	}

	@Override
	public void setValue(Map<ENUM, Boolean> newFieldValue) throws ReadOnlyException, Converter.ConversionException {
		super.setValue(newFieldValue);

		if (!this.enumToggles.isEmpty()) {
			setToggleValues(newFieldValue);
		}
	}

	private void setToggleValues(Map<ENUM, Boolean> newFieldValue) {
		enumToggles.forEach((anEnum, checkBox) -> {
			checkBox.setValue(newFieldValue != null && (newFieldValue.get(anEnum) != null && newFieldValue.get(anEnum)));
			System.out.println("test");
		});
	}

	@Override
	public Class<? extends Map<ENUM, Boolean>> getType() {
		return (Class<? extends Map<ENUM, Boolean>>) new HashMap<>().getClass();
	}
}
