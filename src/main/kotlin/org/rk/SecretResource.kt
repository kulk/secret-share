package org.rk

import jakarta.enterprise.inject.Default
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import java.util.*


@Path("/secrets")
class SecretResource {

    @field:Inject
    @field:Default
    private lateinit var service: CacheService


    @POST
    fun create(value: String) = UUID.randomUUID().toString()
        .also { uuid -> service.set(uuid, value) }
        .let { uuid -> "http://localhost:8080/secrets/$uuid" }

    @GET
    @Path("/{key}")
    fun get(key: String) = service.get(key)

}