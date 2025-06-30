package org.rk

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.junit.jupiter.api.Test

@QuarkusTest
class SecretResourceTest {

    @Test
    fun testSecretEndpoint() {
        given()
          .`when`().get("/")
          .then()
             .statusCode(200)
    }

    @Test
    fun testSecretFormEndpoint() {
        given()
            .contentType("application/x-www-form-urlencoded")
            .formParam("value", "mySecret")
            .`when`().post("/")
            .then()
            .statusCode(200)
    }
}


