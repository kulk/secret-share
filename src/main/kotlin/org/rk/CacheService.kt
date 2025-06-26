package org.rk

import io.quarkus.redis.datasource.RedisDataSource
import io.quarkus.redis.datasource.keys.KeyCommands
import io.quarkus.redis.datasource.value.ValueCommands
import jakarta.enterprise.context.ApplicationScoped
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.time.ZoneId
import java.time.ZonedDateTime


@ApplicationScoped
class CacheService(ds: RedisDataSource, reactive: RedisDataSource) {

    private val keyCommands: KeyCommands<String> = reactive.key()

    private var secretCommands: ValueCommands<String, String> = ds.value(String::class.java)

    private val zone = ZoneId.of("Europe/Amsterdam")

    @field:ConfigProperty(name = "encryption.key")
    private lateinit var encryptionKey: String


    fun get(key: String): String = secretCommands.get(key)
            ?.let { json -> Json.decodeFromString<Secret>(json) }
            ?.let { secret -> Encryption.decrypt(secret.value, encryptionKey) }
            ?.also { keyCommands.del(key) }
            ?: "Secret does not exist."

    fun set(key: String, value: String) = Encryption.encrypt(value, encryptionKey)
        .let { encrypted -> Secret(encrypted, ZonedDateTime.now(zone)) }
        .let { secret -> Json.encodeToString(secret) }
        .also { json -> secretCommands.set(key, json) }

}

@Serializable
data class Secret(
    val value: String,
    @Serializable(with = ZonedDateTimeSerializer::class)
    val createdAt: ZonedDateTime
)