package org.rk

import io.quarkus.qute.Location
import io.quarkus.qute.Template
import io.quarkus.qute.TemplateInstance
import io.smallrye.common.annotation.Blocking
import jakarta.enterprise.inject.Default
import jakarta.inject.Inject
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.MultivaluedMap
import java.util.UUID


@Path("/")
class SecretResource {

    @field:Inject
    @field:Default
    private lateinit var service: CacheService

    @field:Location("secretForm")
    private lateinit var secretForm: Template

    @field:Location("secretSuccess")
    private lateinit var secretSuccess: Template

    @GET
    @Produces(MediaType.TEXT_HTML)
    fun showForm(): TemplateInstance {
        return secretForm.instance()
    }

    @GET
    @Path("/{key}")
    fun get(key: String) = service.get(key)

    @POST
    @Blocking
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    fun handleSecret(formData: MultivaluedMap<String, String>): TemplateInstance {
        val secret: String? = formData.getFirst("secret")
        if (secret == null || secret.trim { it <= ' ' }.isEmpty()) {
            return secretForm.data("error", "Secret is required")
                .data("secret", secret)
        }
        val link = createLink(secret)

        return secretSuccess.data("link", link)
    }

    private fun createLink(value: String) = UUID.randomUUID().toString()
        .also { uuid -> service.set(uuid, value) }
        .let { uuid -> "/$uuid" }
}