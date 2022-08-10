package de.symeda.sormas.ui.utils;

/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.Position;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.v7.data.Validator.InvalidValueException;
import com.vaadin.v7.data.validator.AbstractValidator;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.ui.campaign.expressions.Texttest;

public class NumberNumericValueValidator extends AbstractValidator<String> {

	private BigDecimal minValue;
	private BigDecimal maxValue;
	private boolean decimalAllowed;
	private boolean onError;

	public NumberNumericValueValidator(String errorMessage) {
		this(errorMessage, null, null, true);
	}

	public NumberNumericValueValidator(String errorMessage, Number minValue, Number maxValue) {
		this(errorMessage, minValue, maxValue, true);
	}

	public NumberNumericValueValidator(String errorMessage, Number minValue, Number maxValue, boolean decimalAllowed) {
		super(errorMessage);

		if (minValue != null) {
			this.minValue = new BigDecimal(minValue.toString());
		}

		if (maxValue != null) {
			this.maxValue = new BigDecimal(maxValue.toString());
		}

		this.decimalAllowed = decimalAllowed;
	}

	
	public NumberNumericValueValidator(String errorMessage, Number minValue, Number maxValue, boolean decimalAllowed, boolean inn) {
		super(errorMessage);
		
		
		System.out.println("delteme meeeeeee"+errorMessage);
		if (minValue != null) {
			this.minValue = new BigDecimal(minValue.toString());
		}

		if (maxValue != null) {
			this.maxValue = new BigDecimal(maxValue.toString());
		}

		this.decimalAllowed = decimalAllowed;
		this.onError = inn;
	}
	
	
	@Override
	protected boolean isValidValue(String number) {
		if (StringUtils.isBlank(number)) {
			return true;
		}

		Number parsedNumber;
		try {
			parsedNumber = Integer.valueOf(number);
		} catch (NumberFormatException ie) {
			try {
				parsedNumber = Long.valueOf(number);
			} catch (NumberFormatException le) {
				if (!decimalAllowed) {
					return false;
				}
				try {
					parsedNumber = Float.valueOf(number);
				} catch (NumberFormatException fe) {
					try {
						parsedNumber = Double.valueOf(number);
					} catch (NumberFormatException de) {
						return false;
					}
				}
			}
		}

		return validateRange(parsedNumber);
	}

	@Override
	public Class<String> getType() {
		return String.class;
	}

	@SuppressWarnings("deprecation")
	private boolean validateRange(Number number) {

		BigDecimal decimalNumber = new BigDecimal(number.toString());
		//onError = true;
		if (onError) {
		
			Window window = VaadinUiUtil.createPopupWindow();
			window.setCaption(I18nProperties.getString(Strings.warningHead));

			VerticalLayout vl = new VerticalLayout();

			// vl.addComponent(new
			// Label(I18nProperties.getString(Strings.warningNotInRange)));

			window.setClosable(false);

			// window.setWidth(vl.getWidth() + 64 + 20, Unit.PIXELS);

			if (minValue != null && minValue.compareTo(decimalNumber) > 0) {
			//	System.out.println("MINIMULLLLL" + getErrorMessage());

				vl.addComponent(new Label(getErrorMessage()));
				Button ok = new Button(I18nProperties.getString(Strings.okayCont));
				ok.addClickListener(new ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						// TODO Auto-generated method stub
						window.close();
					}
				});
				vl.addComponent(ok);
				window.setContent(vl);

				UI.getCurrent().addWindow(window);
				// Notification.show("WARNING! ", getErrorMessage(),
				// Type.ERROR_MESSAGE.TRAY_NOTIFICATION);
				
				 
				return onError;
			}

			if (maxValue != null && maxValue.compareTo(decimalNumber) < 0) {

				vl.addComponent(new Label(getErrorMessage()));
				Button ok = new Button(I18nProperties.getString(Strings.okayCont));
				ok.addClickListener(new ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						// TODO Auto-generated method stub
						window.close();
					}
				});
				vl.addComponent(ok);
				window.setContent(vl);

				UI.getCurrent().addWindow(window);

				return onError;
			}

		} else {
			
		//	System.out.println("ddddddddddddddddddddddddddddddddddddddddddddddddd");
			if (minValue != null && minValue.compareTo(decimalNumber) > 0) {
				Notification.show("WARNING! ", getErrorMessage(), Type.ERROR_MESSAGE);
				return onError;
			}

			if (maxValue != null && maxValue.compareTo(decimalNumber) < 0) {
				Notification.show("WARNING! ", getErrorMessage(), Type.ERROR_MESSAGE);

				return onError;
			}
			return true;
			
		}

		return true;
	}

	@Override
	protected boolean isValidType(Object value) {
		return super.isValidType(value) || Number.class.isAssignableFrom(value.getClass());
	}
}
