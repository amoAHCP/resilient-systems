package ch.trivadis.error;

import javax.ws.rs.core.Response;

/**
 * Created by Andy Moncsek on 19.10.15.
 */
public class ProductServiceException extends RuntimeException  {

    private final Response.Status status;

    public ProductServiceException(Response.Status status, String message) {
        this(status,message,null);
    }

    public ProductServiceException(Response.Status status, String message, Throwable cause) {
        super(message,cause);
        this.status = status;

    }

    public Response.Status getStatus() {
        return status;
    }
}
