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

    @field:ConfigProperty(name = "encryption.key")
    private lateinit var encryptionKey: String


    fun get(key: String): String =
        secretCommands.get(key)
            ?.let { value -> Encryption.decrypt(value, encryptionKey) }
            ?.also { keyCommands.del(key) }
            ?: "Secret has been deleted."

    fun set(key: String, value: String) =
        secretCommands.set(key, Encryption.encrypt(value, encryptionKey))
}