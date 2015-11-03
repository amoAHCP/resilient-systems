package ch.trivadis.rest;

import ch.trivadis.model.Product;
import ch.trivadis.service.ProductService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;

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
    public void create(Product entity, @Suspended final AsyncResponse asyncResponse) {
        productService.
                create(entity).
                subscribe(
                        createResponse -> asyncResponse.resume(createResponse),
                        error -> onErrorResponse(asyncResponse, error)
                );
    }



    @DELETE
    @Path("/{id:[0-9][0-9]*}")
    public void deleteById(@PathParam("id") Long id, @Suspended final AsyncResponse asyncResponse) {
        productService.
                deleteById(id).
                subscribe(
                        deleteResponse -> asyncResponse.resume(deleteResponse),
                        error -> onErrorResponse(asyncResponse, error)
                );
    }

    @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces("application/json")
    public void findById(@PathParam("id") Long id, @Suspended final AsyncResponse asyncResponse) {
        productService.
                findById(id).
                subscribe(
                        productResponse -> asyncResponse.resume(productResponse),
                        error -> onErrorResponse(asyncResponse, error)
                );

    }

    @GET
    @Produces("application/json")
    public void listAll(@QueryParam("start") Integer startPosition,
                        @QueryParam("max") Integer maxResult, @Suspended final AsyncResponse asyncResponse) {
        productService.
                listAll(startPosition, maxResult).
                subscribe(
                        productResponse -> asyncResponse.resume(productResponse),
                        error -> onErrorResponse(asyncResponse, error)
                );

    }

    @PUT
    @Path("/{id:[0-9][0-9]*}")
    @Consumes("application/json")
    public void update(@PathParam("id") Long id, Product entity, @Suspended final AsyncResponse asyncResponse) {
        productService.
                update(id, entity).
                subscribe(
                        updateResponse -> asyncResponse.resume(updateResponse),
                        error -> asyncResponse.resume(Response.status(Response.Status.NOT_MODIFIED).entity(error.getCause()).build())
                );
    }

    private boolean onErrorResponse(AsyncResponse asyncResponse, Throwable error) {
        return asyncResponse.resume(Response.status(Response.Status.BAD_REQUEST).entity(error.getCause()).build());
    }
}
