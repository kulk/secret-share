package org.rk

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object Encryption {

    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"
    private const val HMAC_ALGORITHM = "HmacSHA256"
    private const val IV_SIZE = 16
    private const val HMAC_SIZE = 32 // bytes

    fun encrypt(plainText: String, key: String): String {
        val secretKey = createAesKey(key)
        val hmacKey = createHmacKey(key)
        val iv = generateRandomIv()

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(iv))
        val cipherText = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

        val combined = iv + cipherText
        val hmac = computeHmac(hmacKey, combined)

        val finalPayload = combined + hmac
        return Base64.getEncoder().encodeToString(finalPayload)
    }

    fun decrypt(encryptedText: String, key: String): String {
        val decoded = Base64.getDecoder().decode(encryptedText)
        if (decoded.size < IV_SIZE + HMAC_SIZE) throw IllegalArgumentException("Invalid payload")

        val iv = decoded.sliceArray(0 until IV_SIZE)
        val cipherText = decoded.sliceArray(IV_SIZE until decoded.size - HMAC_SIZE)
        val hmac = decoded.sliceArray(decoded.size - HMAC_SIZE until decoded.size)

        val combined = iv + cipherText
        val hmacKey = createHmacKey(key)
        val expectedHmac = computeHmac(hmacKey, combined)

        if (!hmac.contentEquals(expectedHmac)) throw SecurityException("HMAC verification failed")

        val secretKey = createAesKey(key)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))
        val plainBytes = cipher.doFinal(cipherText)
        return String(plainBytes, Charsets.UTF_8)
    }

    private fun createAesKey(key: String): SecretKeySpec {
        val hashed = MessageDigest.getInstance("SHA-256")
            .digest(key.toByteArray(Charsets.UTF_8))
        return SecretKeySpec(hashed, ALGORITHM)
    }

    private fun createHmacKey(key: String): SecretKeySpec {
        val hmacSeed = (key + ":hmac").toByteArray(Charsets.UTF_8)
        val hashed = MessageDigest.getInstance("SHA-256").digest(hmacSeed)
        return SecretKeySpec(hashed, HMAC_ALGORITHM)
    }

    private fun computeHmac(key: SecretKeySpec, data: ByteArray): ByteArray {
        val mac = Mac.getInstance(HMAC_ALGORITHM)
        mac.init(key)
        return mac.doFinal(data)
    }

    private fun generateRandomIv(): ByteArray =
        ByteArray(IV_SIZE).apply { SecureRandom().nextBytes(this) }
}
