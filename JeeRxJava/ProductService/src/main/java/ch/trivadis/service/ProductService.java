package ch.trivadis.service;

import ch.trivadis.error.ProductServiceException;
import ch.trivadis.model.Product;
import rx.Observable;

import javax.ejb.Stateless;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static ch.trivadis.util.ServiceUtil.*;

/**
 * Created by Andy Moncsek on 25.09.15.
 */
@Stateless
public class ProductService {

    private String warehouse = "warehouseservice";
    private String warehousePort = System.getenv("JEERXJAVA_WAREHOUSESERVICE_1_PORT_8080_TCP_PORT");
    private String amazon = "amazonservice";
    private String amazonPort = System.getenv("JEERXJAVA_AMAZONSERVICE_1_PORT_8080_TCP_PORT");
    private String wareHouseBaseURL = "http://" + warehouse + ":8080" + "/WarehouseService/rest/products";
    private String amazonBaseURL = "http://" + amazon + ":8080" + "/AmazonService/rest/products";


    /**
     * create a Product entity
     *
     * @param entity the new Product to persist
     * @return an observable with the correct status OK/ERROR
     */
    public Observable<Response> create(Product entity) {
        return Observable.<Response>create(sub ->
                onBadRequestCatch(() -> {
                    Response value = ClientBuilder.newClient().
                            target(wareHouseBaseURL + "/").
                            request(MediaType.APPLICATION_JSON_TYPE).post(Entity.entity(entity, MediaType.APPLICATION_JSON_TYPE));
                    response(value, sub);
                }, sub));
    }

    /**
     * delete a product by id
     *
     * @param id the id of the product to delete
     * @return an observable with the correct status OK/ERROR
     */
    public Observable<Response> deleteById(Long id) {
        return Observable.<Response>create(sub ->
                onBadRequestCatch(() -> {
                    Response value = ClientBuilder.newClient().
                            target(wareHouseBaseURL + "/" + id).
                            request(MediaType.APPLICATION_JSON_TYPE).delete();
                    response(value, sub);
                }, sub));
    }

    /**
     * find a product by id
     *
     * @param id the id of the Product to find
     * @return an Observable with either the correct Response-object containing the Product or an Error
     */
    public Observable<Response> findById(Long id) {
        return findInWarehouseById(id).
                timeout(1, TimeUnit.SECONDS, Observable.error(new ProductServiceException(Response.Status.SERVICE_UNAVAILABLE, "timeout "))).
                retry(1).
                doOnError(e -> System.err.println("FALLING BACK TO AMAZON SERVICE findById")).
                onErrorResumeNext(next -> findOnAmazonById(id)).
                map(product -> Response.ok(product).build());

    }

    /**
     * List all Products
     *
     * @param startPosition start position for paging
     * @param maxResult     max length of resulting list
     * @return an Observable with either the correct Response-object containing the Product-list or an Error
     */
    public Observable<List<Product>> listAll(Integer startPosition,
                                             Integer maxResult) {
        return findInWarehouseListAll(startPosition, maxResult).
                timeout(1, TimeUnit.SECONDS, Observable.error(new ProductServiceException(Response.Status.SERVICE_UNAVAILABLE, "timeout "))).
                retry(1).
                doOnError(e -> System.err.println("FALLING BACK TO AMAZON SERVICE")).
                onErrorResumeNext(next -> findOnAmazonListAll(startPosition, maxResult));
    }

    /**
     * Update a given product definition by id
     *
     * @param id     the id of the Product to update
     * @param entity the given entity
     * @return OK/NOT ok
     */
    public Observable<Response> update(Long id, Product entity) {
        return Observable.<Response>create(sub ->
                onBadRequestCatch(() -> {
                    Response value = ClientBuilder.newClient().
                            target(wareHouseBaseURL + "/" + id).
                            request(MediaType.APPLICATION_JSON_TYPE).put(Entity.entity(entity, MediaType.APPLICATION_JSON_TYPE));
                    response(value, sub);
                }, sub));
    }


    /**
     * access warehouse service to find Product by id
     *
     * @param id the Product id to look for
     * @return the Observable containing the Product
     */
    private Observable<Product> findInWarehouseById(Long id) {
        return Observable.<Product>create(sub ->
                handleClientExceptions(() ->
                        response(getProductById(wareHouseBaseURL, id), sub), sub, "findInWarehouseById"));

    }

    /**
     * access amazon service fo find the Product by id
     *
     * @param id the Product id to look for
     * @return the Observable containing the Product
     */
    private Observable<Product> findOnAmazonById(Long id) {
        return Observable.<Product>create(sub ->
                handleClientExceptions(() ->
                        response(getProductById(amazonBaseURL, id), sub), sub, "findOnAmazonById"));

    }

    /**
     * Generic method to find a Product by ID either by warehouse url or amazon url
     *
     * @param baseURL the URL to request
     * @param id      the Product id to request
     * @return the requested Product
     */
    private Product getProductById(String baseURL, Long id) {
        System.err.println("getProductById product: " + baseURL);
        return ClientBuilder.newClient().
                target(baseURL + "/" + id).
                request(MediaType.APPLICATION_JSON_TYPE).
                get(Product.class);
    }

    /**
     * Find all Products in warehouse service
     *
     * @param startPosition paging start position
     * @param maxResult     max size of returning list
     * @return an Observable containing a List of Products
     */
    private Observable<List<Product>> findInWarehouseListAll(Integer startPosition,
                                                             Integer maxResult) {
        return Observable.<List<Product>>create(sub ->
                handleClientExceptions(() -> {
                    final String postfix = getServicePostfix(startPosition, maxResult);
                    final List<Product> value = getAllProducts(wareHouseBaseURL, postfix);
                    response(value, sub);
                }, sub, "findInWarehouseListAll"));
    }

    /**
     * Find all Products in Amazon service
     *
     * @param startPosition paging start position
     * @param maxResult     max size of returning list
     * @return an Observable containing a List of Products
     */
    private Observable<List<Product>> findOnAmazonListAll(Integer startPosition,
                                                          Integer maxResult) {
        return Observable.<List<Product>>create(sub ->
                handleClientExceptions(() -> {
                    final String postfix = getServicePostfix(startPosition, maxResult);
                    final List<Product> value = getAllProducts(amazonBaseURL, postfix);
                    response(value, sub);
                }, sub, "findOnAmazonListAll"));

    }

    /**
     * Generic method to find all products
     *
     * @param baseUrl the url to request
     * @param postfix the postfix of the url
     * @return a List of Products
     */
    private List<Product> getAllProducts(String baseUrl, String postfix) {
        return ClientBuilder.newClient().
                target(baseUrl + postfix).
                request(MediaType.APPLICATION_JSON_TYPE).get(new GenericType<List<Product>>() {
        });
    }


}

