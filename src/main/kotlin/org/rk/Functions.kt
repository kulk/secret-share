package org.rk

/**
 * INPUT
 * - receive Secret (String)
 * - encrypt secret
 * - generate hash
 * - store secret
 * - return link (endpoint + hash)
 *
 * OUTPUT
 * - Retrieve secret based on HASH from redis
 * - Send secret in response
 * - Delete entry from redis
 *
 * SCHEDULER
 * - Run a clean up 1x per day. Delete everything from redis which is 4 days old.
 */