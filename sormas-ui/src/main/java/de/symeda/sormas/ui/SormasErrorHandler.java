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
package de.symeda.sormas.ui;

import java.net.SocketException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.AbstractErrorMessage.ContentMode;
import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.ErrorEvent;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.Page;
import com.vaadin.server.SystemError;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.ErrorLevel;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Notification;
import com.vaadin.v7.data.Buffered;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.ui.AbstractField;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.OutdatedEntityException;
import de.symeda.sormas.api.utils.ValidationRuntimeException;

public class SormasErrorHandler implements ErrorHandler {

	private static final long serialVersionUID = -8550777561547915589L;

	private static final SormasErrorHandler INSTANCE = new SormasErrorHandler();

	public static SormasErrorHandler get() {
		return INSTANCE;
	}

	@Override
	public void error(ErrorEvent event) {
		handleError(event);
	}

	public static void handleError(ErrorEvent event) {

		Logger logger = LoggerFactory.getLogger(SormasErrorHandler.class);
		final Throwable t = event.getThrowable();
		if (t instanceof SocketException) {
			// Most likely client browser closed socket
			logger.info("SocketException in CommunicationManager." + " Most likely client (browser) closed socket.");
			return;
		}

		ErrorMessage errorMessage = getErrorMessageForException(t);

		if (t != null) {
			// log the error or warning
			if (errorMessage instanceof SystemError) {
				logger.error(t.getMessage(), t);
			} else {
				logger.warn(t.getMessage(), t);
			}
		}

		// finds the original source of the error/exception
		AbstractComponent component = DefaultErrorHandler.findAbstractComponent(event);
		if (errorMessage != null && component != null) {
			// Shows the error in AbstractComponent
			if (errorMessage instanceof SystemError) {
				Notification.show(
					I18nProperties.getString(Strings.errorOccurred, I18nProperties.getString(Strings.errorOccurred)),
					I18nProperties.getString(Strings.errorWasReported),
					Notification.Type.ERROR_MESSAGE);
			} else {

				// to prevent the original message from appearing, if necessary
				if (component instanceof AbstractField<?>) {
					((AbstractField<?>) component).setCurrentBufferedSourceException(null);
				}

				Notification notification = new Notification(
					I18nProperties.getString(Strings.errorProblemOccurred, I18nProperties.getString(Strings.errorProblemOccurred)),
					errorMessage.getFormattedHtmlMessage(),
					Notification.Type.WARNING_MESSAGE,
					true);
				notification.setDelayMsec(-1);
				notification.show(Page.getCurrent());
				component.setComponentError(errorMessage);
			}
		}
	}

	/**
	 * Taken and cleaned from AbstractErrorMessage
	 */
	public static ErrorMessage getErrorMessageForException(Throwable t) {

		//return AbstractErrorMessage.getErrorMessageForException(t)
		if (null == t) {
			return null;
		} else if (t instanceof ErrorMessage) {
			// legacy case for custom error messages
			return (ErrorMessage) t;
		} else if (t instanceof Validator.InvalidValueException) {
			LocalUserError error = new LocalUserError(((Validator.InvalidValueException) t).getHtmlMessage(), ContentMode.HTML, ErrorLevel.ERROR);
			for (Validator.InvalidValueException nestedException : ((Validator.InvalidValueException) t).getCauses()) {
				error.addCause(getErrorMessageForException(nestedException));
			}
			return error;
		} else if (t instanceof Buffered.SourceException) {
			// no message, only the causes to be painted
			LocalUserError error = new LocalUserError(null);
			// in practice, this was always ERROR in Vaadin 6 unless tweaked in
			// custom exceptions implementing ErrorMessage
			error.setErrorLevel(ErrorLevel.ERROR);
			// causes
			for (Throwable nestedException : ((Buffered.SourceException) t).getCauses()) {
				error.addCause(getErrorMessageForException(nestedException));
			}
			return error;
		} else {
			Throwable rootCause = ExceptionUtils.getRootCause(t);
			if (rootCause instanceof ValidationRuntimeException) {
				LocalUserError error;
				if (rootCause instanceof OutdatedEntityException) {
					error = new LocalUserError(I18nProperties.getString(Strings.errorEntityOutdated), ContentMode.HTML, ErrorLevel.WARNING);
				} else {
					error = new LocalUserError(rootCause.getMessage(), ContentMode.HTML, ErrorLevel.WARNING);
				}
				return error;
			} else {
				String message = t.getMessage();
				if (message == null) {
					message = I18nProperties.getString(Strings.errorOccurred);
				}
				return new SystemError(message);
			}
		}
	}

	private static final class LocalUserError extends UserError {

		private static final long serialVersionUID = 1L;

		public LocalUserError(String textErrorMessage) {
			super(textErrorMessage);
		}

		public LocalUserError(String message, ContentMode contentMode, ErrorLevel errorLevel) {
			super(message, contentMode, errorLevel);
		}

		@Override
		public void addCause(ErrorMessage cause) {
			super.addCause(cause);
		}

		@Override
		public void setErrorLevel(ErrorLevel level) {
			super.setErrorLevel(level);
		}
	}
}
