package ch.trivadis.rest;

import ch.trivadis.model.Product;
import ch.trivadis.service.ProductService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
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
	public Response create(Product entity) {
		productService.create(entity);
		return Response.created(
				UriBuilder.fromResource(ProductEndpoint.class)
						.path(String.valueOf(entity.getId())).build()).build();
	}

	@DELETE
	@Path("/{id:[0-9][0-9]*}")
	public Response deleteById(@PathParam("id") Long id) {
		return  Response.status(productService.deleteById(id)).build();
	}

	@GET
	@Path("/{id:[0-9][0-9]*}")
	@Produces("application/json")
	public Response findById(@PathParam("id") Long id) {
		Product entity = productService.findById(id);
		if (entity == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok(entity).build();
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
		return Response.status(productService.update(id,entity)).build();
	}
}
