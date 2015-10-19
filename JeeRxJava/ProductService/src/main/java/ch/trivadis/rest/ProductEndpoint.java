package ch.trivadis.rest;

import ch.trivadis.model.Product;
import ch.trivadis.service.ProductService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 *
 */
@Stateless
@Path("/products")
public class ProductEndpoint {


    @Inject
    private ProductService productService;

    @POST
    @Consumes("application/json")
    // public Response create(Product entity,@Suspended final AsyncResponse asyncResponse) {
    public Response create(Product entity) {
        return productService.create(entity).single().toBlocking().single();
    }

    @DELETE
    @Path("/{id:[0-9][0-9]*}")
    public Response deleteById(@PathParam("id") Long id) {
        return productService.deleteById(id).single().toBlocking().single();
    }

    @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces("application/json")
    public Response findById(@PathParam("id") Long id) {
        return productService.findById(id).single().toBlocking().single();

    }

    @GET
    @Produces("application/json")
    public List<Product> listAll(@QueryParam("start") Integer startPosition,
                                 @QueryParam("max") Integer maxResult) {
       return productService.listAll(startPosition,maxResult).single().toBlocking().single();

    }

    @PUT
    @Path("/{id:[0-9][0-9]*}")
    @Consumes("application/json")
    public Response update(@PathParam("id") Long id, Product entity) {
        return productService.update(id,entity).single().toBlocking().single();
    }
}
