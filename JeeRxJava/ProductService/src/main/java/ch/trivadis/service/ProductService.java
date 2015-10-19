package ch.trivadis.service;

import ch.trivadis.model.Product;

import javax.ejb.Stateless;
import javax.ws.rs.PathParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
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
    private String wareHouseBaseURL="http://" + warehouse + ":8080" + "/WarehouseService/rest/products";


    public Response create(Product entity) {
        client.
                target(wareHouseBaseURL +"/").
                request("application/json").post(Entity.entity(entity, "application/json"));
        return Response.ok().build();
    }

    public Response deleteById(@PathParam("id") Long id) {
        return client.
                target(wareHouseBaseURL+ "/" + id).
                request("application/json").delete();
    }

    public Response findById(Long id) {
        final Product p = client.
                target(wareHouseBaseURL+ "/" + id).
                request("application/json").
                get(Product.class);
        if (p != null) {
            return Response.ok(p).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    public List<Product> listAll(Integer startPosition,
                                 Integer maxResult) {
        String postfix = "";
        if (startPosition != null && maxResult != null) {
            postfix = "?start=" + startPosition + "&max=" + maxResult;
        } else if (startPosition != null) {
            postfix = "?start=" + startPosition;
        } else if (maxResult != null) {
            postfix = "?max=" + maxResult;
        }
        return client.
                target(wareHouseBaseURL + postfix).
                request("application/json").get(new GenericType<List<Product>>() {
        });
    }

    public Response update(@PathParam("id") Long id, Product entity) {
        return client.
                target(wareHouseBaseURL+ "/" + id).
                request("application/json").put(javax.ws.rs.client.Entity.entity(entity, "application/json"));
    }
}

