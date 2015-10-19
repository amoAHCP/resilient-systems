package ch.trivadis.service;

import ch.trivadis.error.ProductServiceException;
import ch.trivadis.model.Product;
import rx.Observable;
import rx.Subscriber;

import javax.ejb.Stateless;
import javax.ws.rs.PathParam;
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

    private String warehouse = System.getenv("JEERXJAVA_WAREHOUSESERVICE_1_PORT_8080_TCP_ADDR");
    private String warehousePort = System.getenv("JEERXJAVA_WAREHOUSESERVICE_1_PORT_8080_TCP_PORT");
    private String amazon = System.getenv("JEERXJAVA_AMAZONSERVICE_1_PORT_8080_TCP_ADDR");
    private String amazonPort = System.getenv("JEERXJAVA_AMAZONSERVICE_1_PORT_8080_TCP_PORT");
    private String wareHouseBaseURL = "http://" + warehouse + ":8080" + "/WarehouseService/rest/products";


    public Observable<Response> create(Product entity) {
        return Observable.<Response>create(sub ->
                onBadRequestCatch(() -> {
                    Response value =  client.
                            target(wareHouseBaseURL + "/").
                            request(MediaType.APPLICATION_JSON_TYPE).post(Entity.entity(entity, MediaType.APPLICATION_JSON_TYPE));
                    response(value,sub);
                }, sub));
    }

    public Observable<Response> deleteById(@PathParam("id") Long id) {
        return Observable.<Response>create(sub ->
                onBadRequestCatch(() -> {
                    Response value = client.
                            target(wareHouseBaseURL + "/" + id).
                            request(MediaType.APPLICATION_JSON_TYPE).delete();
                    response(value,sub);
                }, sub));
    }

    public Observable<Response> findById(Long id) {
        return Observable.<Response>create(sub ->
                onBadRequestCatch(() -> {
                    Product p = client.
                            target(wareHouseBaseURL + "/" + id).
                            request(MediaType.APPLICATION_JSON_TYPE).
                            get(Product.class);
                    response(p!=null?Response.ok(p).build():null,sub);
                }, sub));
    }


    public Observable<List<Product>> listAll(Integer startPosition,
                                             Integer maxResult) {
        final String postfix = getServicePostfix(startPosition, maxResult);
        return Observable.<List<Product>>create(sub ->
                onBadRequestCatch(() -> {
                    final List<Product> value = client.
                            target(wareHouseBaseURL + postfix).
                            request(MediaType.APPLICATION_JSON_TYPE).get(new GenericType<List<Product>>() {
                    });

                    response(value,sub);
                }, sub));
    }




    public Observable<Response> update(@PathParam("id") Long id, Product entity) {
        return Observable.<Response>create(sub ->
                onBadRequestCatch(() -> {
                    Response value = client.
                            target(wareHouseBaseURL + "/" + id).
                            request(MediaType.APPLICATION_JSON_TYPE).put(Entity.entity(entity, MediaType.APPLICATION_JSON_TYPE));
                    response(value,sub);
                }, sub));
    }

    private <T> void response(T value,Subscriber<T> sub) {
        if (value != null) {
            sub.onNext(value);
            sub.onCompleted();
        } else {
            sub.onError(new ProductServiceException(Response.Status.NOT_FOUND, "", null));
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

