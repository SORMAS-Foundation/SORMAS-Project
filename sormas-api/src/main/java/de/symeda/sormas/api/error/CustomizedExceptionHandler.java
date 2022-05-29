package de.symeda.sormas.api.error;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import de.symeda.sormas.api.error.implementations.CustomizedException;

public abstract class CustomizedExceptionHandler
    implements ExceptionMapper<CustomizedException>{

    @Override
    public Response toResponse(CustomizedException exception) {

        ErrorDetails errorDetails = new ErrorDetailsBuilder()
                        .setMessageId(exception.getMessageId())
                        .setArgumentsList( exception.getArgumentsList() )
                        .setMessage( exception.getMessage() )
                        .setEntity( exception.getEntity() )
                        .build();

        return Response.status(exception.getStatus().getStatusCode())
                .header("Conflict-Reason", errorDetails.toString())
                .type(MediaType.APPLICATION_JSON)
                .entity(errorDetails)
                .build();
    }
}
