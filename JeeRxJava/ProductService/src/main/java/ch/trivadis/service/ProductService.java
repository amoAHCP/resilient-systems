package ch.trivadis.service;

import ch.trivadis.error.ProductServiceException;
import ch.trivadis.model.Product;
import rx.Observable;
import rx.Subscriber;

import javax.ejb.Stateless;
import javax.ws.rs.PathParam;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by Andy Moncsek on 25.09.15.
 */
@Stateless
public class ProductService {

    private Client client = ClientBuilder.newClient();

    private String warehouse = "warehouseservice";
    private String warehousePort = System.getenv("JEERXJAVA_WAREHOUSESERVICE_1_PORT_8080_TCP_PORT");
    private String amazon = "amazonservice";
    private String amazonPort = System.getenv("JEERXJAVA_AMAZONSERVICE_1_PORT_8080_TCP_PORT");
    private String wareHouseBaseURL = "http://" + warehouse + ":8080" + "/WarehouseService/rest/products";
    private String amazonBaseURL = "http://" + amazon + ":8080" + "/AmazonService/rest/products";


    public Observable<Response> create(Product entity) {
        return Observable.<Response>create(sub ->
                onBadRequestCatch(() -> {
                    Response value = client.
                            target(wareHouseBaseURL + "/").
                            request(MediaType.APPLICATION_JSON_TYPE).post(Entity.entity(entity, MediaType.APPLICATION_JSON_TYPE));
                    response(value, sub);
                }, sub));
    }

    public Observable<Response> deleteById(@PathParam("id") Long id) {
        return Observable.<Response>create(sub ->
                onBadRequestCatch(() -> {
                    Response value = client.
                            target(wareHouseBaseURL + "/" + id).
                            request(MediaType.APPLICATION_JSON_TYPE).delete();
                    response(value, sub);
                }, sub));
    }

    public Observable<Response> findById(Long id) {

        return findInWarehouseById(id).
                doOnError(e -> System.out.println("FALLING BACK TO AMAZON SERVICE")).
                onErrorResumeNext(next -> findOnAmazonById(id)).
                map(product -> Response.ok(product).build());

    }

    private Observable<Product> findInWarehouseById(Long id) {
        return Observable.<Product>create(sub ->
                handleClientExceptions(() ->
                        response(getProductById(wareHouseBaseURL,id), sub), sub, "findOnAmazonById"));

    }

    private Observable<Product> findOnAmazonById(Long id) {
        return Observable.<Product>create(sub ->
                handleClientExceptions(() ->
                        response(getProductById(amazonBaseURL,id), sub), sub, "findOnAmazonById"));

    }

    private Product getProductById(String baseURL,Long id) {
        return client.
                target(baseURL + "/" + id).
                request(MediaType.APPLICATION_JSON_TYPE).
                get(Product.class);
    }


    public Observable<List<Product>> listAll(Integer startPosition,
                                             Integer maxResult) {
        return findInWarehouseListAll(startPosition, maxResult).
                doOnError(e -> System.out.println("FALLING BACK TO AMAZON SERVICE")).
                onErrorResumeNext(next -> findOnAmazonListAll(startPosition, maxResult));
    }

    private Observable<List<Product>> findInWarehouseListAll(Integer startPosition,
                                                             Integer maxResult) {
        return Observable.<List<Product>>create(sub ->
                handleClientExceptions(() -> {
                    final String postfix = getServicePostfix(startPosition, maxResult);
                    final List<Product> value = getAllProducts(wareHouseBaseURL,postfix);
                    response(value, sub);
                }, sub, "findInWarehouseListAll"));
    }


    private Observable<List<Product>> findOnAmazonListAll(Integer startPosition,
                                                          Integer maxResult) {
        return Observable.<List<Product>>create(sub ->
                handleClientExceptions(() -> {
                    final String postfix = getServicePostfix(startPosition, maxResult);
                    final List<Product> value = getAllProducts(amazonBaseURL,postfix);
                    response(value, sub);
                }, sub, "findOnAmazonListAll"));

    }

    private List<Product> getAllProducts(String baseUrl, String postfix) {
        return client.
                target(baseUrl + postfix).
                request(MediaType.APPLICATION_JSON_TYPE).get(new GenericType<List<Product>>() {
        });
    }

    public Observable<Response> update(@PathParam("id") Long id, Product entity) {
        return Observable.<Response>create(sub ->
                onBadRequestCatch(() -> {
                    Response value = client.
                            target(wareHouseBaseURL + "/" + id).
                            request(MediaType.APPLICATION_JSON_TYPE).put(Entity.entity(entity, MediaType.APPLICATION_JSON_TYPE));
                    response(value, sub);
                }, sub));
    }

    private <T> void response(T value, Subscriber<T> sub) {
        if (value != null) {
            sub.onNext(value);
            sub.onCompleted();
        } else {
            sub.onError(new ProductServiceException(Response.Status.NOT_FOUND, "", null));
        }
    }

    private void handleClientExceptions(Runnable r, Subscriber<?> sub, final String name) {
        try {
            r.run();
        } catch (WebApplicationException e) {
            sub.onError(new ProductServiceException(Response.Status.NOT_FOUND, "--> WebApplicationException -- " + name, null));
        } catch (ProcessingException e) {
            sub.onError(new ProductServiceException(Response.Status.NOT_FOUND, "--> ProcessingException  -- " + name, null));
        }
    }

    private void onBadRequestCatch(Runnable r, Subscriber<?> sub) {
        try {
            r.run();
        } catch (Exception e) {
            sub.onError(new ProductServiceException(Response.Status.BAD_REQUEST, "", e));
        }
    }

    private String getServicePostfix(Integer startPosition, Integer maxResult) {
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

