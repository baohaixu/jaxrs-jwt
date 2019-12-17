package com.baohaixu.controller;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.baohaixu.model.Product;
import com.baohaixu.util.Secured;

@Path("products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Stateless
public class ProductController {
	@PersistenceContext(unitName = "primary")
    private EntityManager em;

    @GET
    @Secured
    public Response getAllProducts() {
        TypedQuery<Product> query = em.createQuery(Product.QUERY_ALL, Product.class);
        List<Product> Products = query.getResultList();
        return Response.ok(Products).build();
    }

    @GET
    @Path("{id}")
    public Response getOneProduct(@PathParam("id") long id) {
        Product product = this.em.find(Product.class, id);
        return Response.ok(product).build();
    }

    @POST
    public Response createOneProduct(Product product) {
        em.persist(product);
        return Response.ok(product).build();
    }

    @PUT
    public Response updateOneProduct(Product product) {
        Product productManaged = em.merge(product);
        return Response.ok(productManaged).build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteOneProduct(@PathParam("id") long id) {
        Product product = em.find(Product.class, id);
        em.remove(product);
        return Response.ok(product).build();
    }
}
