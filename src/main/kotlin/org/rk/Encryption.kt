package org.rk

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object Encryption {

    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"
    private const val IV_SIZE = 16

    /**
     * Encrypts a string using AES encryption with the provided key
     * @param plainText The string to encrypt
     * @param key The encryption key (will be hashed to ensure proper length)
     * @return Base64 encoded encrypted string with IV prepended
     */
    fun encrypt(plainText: String, key: String): String =
        createSecretKey(key).let { secretKey ->
            generateRandomIv().let { iv ->
                Cipher.getInstance(TRANSFORMATION)
                    .apply { init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(iv)) }
                    .doFinal(plainText.toByteArray(Charsets.UTF_8))
                    .let { encryptedBytes -> iv + encryptedBytes }
                    .let(Base64.getEncoder()::encodeToString)
            }
        }

    /**
     * Decrypts a string using AES decryption with the provided key
     * @param encryptedText Base64 encoded encrypted string with IV prepended
     * @param key The decryption key (same as used for encryption)
     * @return The decrypted plain text string
     */
    fun decrypt(encryptedText: String, key: String): String =
        Base64.getDecoder().decode(encryptedText).let { encryptedWithIv ->
            val iv = encryptedWithIv.sliceArray(0 until IV_SIZE)
            val encryptedBytes = encryptedWithIv.sliceArray(IV_SIZE until encryptedWithIv.size)

            createSecretKey(key).let { secretKey ->
                Cipher.getInstance(TRANSFORMATION)
                    .apply { init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv)) }
                    .doFinal(encryptedBytes)
                    .let { String(it, Charsets.UTF_8) }
            }
        }

    /**
     * Creates a SecretKeySpec from a string key by hashing it with SHA-256
     * This ensures the key is always the correct length (32 bytes for AES-256)
     */
    private fun createSecretKey(key: String): SecretKeySpec =
        MessageDigest.getInstance("SHA-256")
            .digest(key.toByteArray(Charsets.UTF_8))
            .let { SecretKeySpec(it, ALGORITHM) }

    /**
     * Generates a random IV for encryption
     */
    private fun generateRandomIv() =
        ByteArray(IV_SIZE).apply { SecureRandom().nextBytes(this) }

}