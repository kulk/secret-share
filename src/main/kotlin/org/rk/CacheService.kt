package org.rk

import io.quarkus.redis.datasource.ReactiveRedisDataSource
import io.quarkus.redis.datasource.RedisDataSource
import io.quarkus.redis.datasource.keys.ReactiveKeyCommands
import io.quarkus.redis.datasource.value.ValueCommands
import jakarta.enterprise.context.ApplicationScoped


@ApplicationScoped
class CacheService(ds: RedisDataSource, reactive: ReactiveRedisDataSource) {

    private val keyCommands: ReactiveKeyCommands<String> = reactive.key()
    private var secretCommands: ValueCommands<String, Long> = ds.value(Long::class.java)








}