package de.symeda.sormas.ui;

import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Buffered;
import com.vaadin.data.Validator;
import com.vaadin.server.AbstractErrorMessage.ContentMode;
import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.ErrorEvent;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.ErrorMessage.ErrorLevel;
import com.vaadin.server.SystemError;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Notification;

public class SormasErrorHandler implements ErrorHandler {

	private static final long serialVersionUID = -8550777561547915589L;
	
	private static final Logger logger = LoggerFactory.getLogger(SormasErrorHandler.class);
	
	private static final SormasErrorHandler instance = new SormasErrorHandler();
	
	public static SormasErrorHandler get() {
		return instance;
	}
	
	@Override
	public void error(ErrorEvent event) {
		handleError(event);
	}
	
	public static void handleError(ErrorEvent event) {
        final Throwable t = event.getThrowable();
        if (t instanceof SocketException) {
            // Most likely client browser closed socket
        	logger.info(
                    "SocketException in CommunicationManager."
                            + " Most likely client (browser) closed socket.");
            return;
        }
    	
        // log the error
		logger.error(t.getMessage(), t);
		
	    // finds the original source of the error/exception
	    AbstractComponent component = DefaultErrorHandler.findAbstractComponent(event);
	    if (component != null) {
	        // Shows the error in AbstractComponent
	        ErrorMessage errorMessage = getErrorMessageForException(t);
	        
	        if (errorMessage instanceof SystemError) {
        		Notification.show("An error has occurred", "The error was automatically reported to support.", Notification.Type.ERROR_MESSAGE);
	        } else {
	        	
	        	// to prevent the original message from appearing, if necessary
	        	if (component instanceof AbstractField<?>) {
	        		((AbstractField<?>)component).setCurrentBufferedSourceException(null);
	        	}
	        	
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
        	UserErrorImpl error = new UserErrorImpl(
                    ((Validator.InvalidValueException) t).getHtmlMessage(),
                    ContentMode.HTML, ErrorLevel.ERROR);
            for (Validator.InvalidValueException nestedException : ((Validator.InvalidValueException) t)
                    .getCauses()) {
                error.addCause(getErrorMessageForException(nestedException));
            }
            return error;
        } else if (t instanceof Buffered.SourceException) {
            // no message, only the causes to be painted
        	UserErrorImpl error = new UserErrorImpl(null);
            // in practice, this was always ERROR in Vaadin 6 unless tweaked in
            // custom exceptions implementing ErrorMessage
            error.setErrorLevel(ErrorLevel.ERROR);
            // causes
            for (Throwable nestedException : ((Buffered.SourceException) t)
                    .getCauses()) {
                error.addCause(getErrorMessageForException(nestedException));
            }
            return error;
        } else {
            String message = t.getMessage();
            if (message == null) {
            	message = "An error has occurred"; 
            }
			return new SystemError(message);
        }
    }
    
    private static final class UserErrorImpl extends UserError {
		private static final long serialVersionUID = 1L;
		public UserErrorImpl(String textErrorMessage) {
            super(textErrorMessage);
        }

        public UserErrorImpl(String message, ContentMode contentMode,
                ErrorLevel errorLevel) {
            super(message, contentMode, errorLevel);
        }
    	@Override
    	protected void addCause(ErrorMessage cause) {
    		super.addCause(cause);
    	}
    	@Override
    	protected void setErrorLevel(ErrorLevel level) {
    		super.setErrorLevel(level);
    	}
    }

}
