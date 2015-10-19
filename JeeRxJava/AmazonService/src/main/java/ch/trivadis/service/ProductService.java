package ch.trivadis.service;

import ch.trivadis.model.Product;

import javax.ejb.Stateless;
import javax.persistence.*;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by Andy Moncsek on 25.09.15.
 */
@Stateless
public class ProductService {

    @PersistenceContext(unitName = "ProductService-persistence-unit")
    private EntityManager em;

    public void create(Product entity) {
        em.persist(entity);
    }

    public Response.Status deleteById(@PathParam("id") Long id) {
        Product entity = em.find(Product.class, id);
        if (entity == null) {
            return Response.Status.NOT_FOUND;
        }
        em.remove(entity);
        return Response.Status.OK;
    }

    public Product findById(@PathParam("id") Long id) {
        TypedQuery<Product> findByIdQuery = em
                .createQuery(
                        "SELECT DISTINCT p FROM Product p WHERE p.id = :entityId ORDER BY p.id",
                        Product.class);
        findByIdQuery.setParameter("entityId", id);
        Product entity;
        try {
            entity = findByIdQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        if (entity == null) {
            return null;
        }
        return entity;
    }

    public List<Product> listAll(Integer startPosition,
                                 Integer maxResult) {
        TypedQuery<Product> findAllQuery = em
                .createQuery("SELECT DISTINCT p FROM Product p ORDER BY p.id",
                        Product.class);
        if (startPosition != null) {
            findAllQuery.setFirstResult(startPosition);
        }
        if (maxResult != null) {
            findAllQuery.setMaxResults(maxResult);
        }
        final List<Product> results = findAllQuery.getResultList();
        return results;
    }

    public Response.Status update(@PathParam("id") Long id, Product entity) {
        if (entity == null) {
            return Response.Status.BAD_REQUEST;
        }
        if (id == null) {
            return Response.Status.BAD_REQUEST;
        }
        if (!id.equals(entity.getId())) {
            return Response.Status.CONFLICT;
        }
        if (em.find(Product.class, id) == null) {
            return Response.Status.NOT_FOUND;
        }
        try {
            entity = em.merge(entity);
        } catch (OptimisticLockException e) {
            return Response.Status.CONFLICT;
        }

        return Response.Status.OK;
    }
}

