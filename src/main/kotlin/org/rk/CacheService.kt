package org.rk

import io.quarkus.redis.datasource.RedisDataSource
import io.quarkus.redis.datasource.keys.KeyCommands
import io.quarkus.redis.datasource.value.ValueCommands
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.config.inject.ConfigProperty


@ApplicationScoped
class CacheService(ds: RedisDataSource, reactive: RedisDataSource) {

    private val keyCommands: KeyCommands<String> = reactive.key()

    private var secretCommands: ValueCommands<String, String> = ds.value(String::class.java)

    private val EXPIRATION_SECONDS = 345600L // 345600 seconds is 4 days

    @field:ConfigProperty(name = "encryption.key")
    private lateinit var encryptionKey: String


    fun get(key: String): String =
        secretCommands.get(key)
            ?.let { value -> Encryption.decrypt(value, encryptionKey) }
            ?.also { keyCommands.del(key) }
            ?: "Secret does not exist."

    fun set(key: String, value: String) =
        secretCommands.setex(key, EXPIRATION_SECONDS, Encryption.encrypt(value, encryptionKey))
}