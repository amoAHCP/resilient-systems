package ch.trivadis.util;

import ch.trivadis.error.ProductServiceException;
import rx.Subscriber;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Created by Andy Moncsek on 31.10.15.
 * Simple util class.
 */
public class ServiceUtil {
    /**
     * set the client response to subscriber, depending on existing value. If null an error will be created
     *
     * @param value the value to set
     * @param sub   the subscriber to write to
     * @param <T>   The type of response
     */
    public static <T> void response(T value, Subscriber<T> sub) {
        if (value != null) {
            sub.onNext(value);
            sub.onCompleted();
        } else {
            sub.onError(new ProductServiceException(Response.Status.NOT_FOUND, "", null));
        }
    }

    /**
     * Wraps an rest client call and catches all exceptions correctly
     *
     * @param r    the request to execute
     * @param sub  the subscriber to put the error to
     * @param name service name is only for logging
     */
    public static void handleClientExceptions(Runnable r, Subscriber<?> sub, final String name) {
        try {
            r.run();
        } catch (WebApplicationException e) {
            sub.onError(new ProductServiceException(Response.Status.NOT_FOUND, "--> WebApplicationException -- " + name, e));
        } catch (ProcessingException e) {
            sub.onError(new ProductServiceException(Response.Status.NOT_FOUND, "--> ProcessingException  -- " + name, e));
        } catch (Exception e) {
            sub.onError(new ProductServiceException(Response.Status.NOT_FOUND, "--> Exception  -- " + name, e));
        }
    }

    /**
     * Runnable.run request and catch Exception if needed.
     *
     * @param r   the request to execute
     * @param sub the subscriber to put the error to
     */
    public static void onBadRequestCatch(Runnable r, Subscriber<?> sub) {
        try {
            r.run();
        } catch (Exception e) {
            sub.onError(new ProductServiceException(Response.Status.BAD_REQUEST, "", e));
        }
    }

    /**
     * Create service postfix depending on start - max values
     *
     * @param startPosition the start position
     * @param maxResult     the maximum amout of result values
     * @return the correct postfix for service call
     */
    public static String getServicePostfix(Integer startPosition, Integer maxResult) {
        String postfix = "";
        if (startPosition != null && maxResult != null) {
            postfix = "?start=" + startPosition + "&max=" + maxResult;
        } else if (startPosition != null) {
            postfix = "?start=" + startPosition;
        } else if (maxResult != null) {
            postfix = "?max=" + maxResult;
        }
        return postfix;
    }
}
