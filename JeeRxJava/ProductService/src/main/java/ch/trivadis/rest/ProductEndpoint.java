package ch.trivadis.rest;

import ch.trivadis.model.Product;
import ch.trivadis.service.ProductService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 *
 */
@Stateless
@Path("/products")
public class ProductEndpoint {
    private Client client = ClientBuilder.newClient();

    private String warehouse = System.getenv("JEERXJAVA_WAREHOUSESERVICE_1_PORT_8080_TCP_ADDR");
    private String warehousePort = System.getenv("JEERXJAVA_WAREHOUSESERVICE_1_PORT_8080_TCP_PORT");
    private String amazon = System.getenv("JEERXJAVA_AMAZONSERVICE_1_PORT_8080_TCP_ADDR");
    private String amazonPort = System.getenv("JEERXJAVA_AMAZONSERVICE_1_PORT_8080_TCP_PORT");

    @Inject
    private ProductService productService;

    @POST
    @Consumes("application/json")
    // public Response create(Product entity,@Suspended final AsyncResponse asyncResponse) {
    public Response create(Product entity) {
        return productService.create(entity);
    }

    @DELETE
    @Path("/{id:[0-9][0-9]*}")
    public Response deleteById(@PathParam("id") Long id) {
        return productService.deleteById(id);
    }

    @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces("application/json")
    public Response findById(@PathParam("id") Long id) {
        return productService.findById(id);

    }

    @GET
    @Produces("application/json")
    public List<Product> listAll(@QueryParam("start") Integer startPosition,
                                 @QueryParam("max") Integer maxResult) {
       return productService.listAll(startPosition,maxResult);

    }

    @PUT
    @Path("/{id:[0-9][0-9]*}")
    @Consumes("application/json")
    public Response update(@PathParam("id") Long id, Product entity) {
        return productService.update(id,entity);
    }
}
