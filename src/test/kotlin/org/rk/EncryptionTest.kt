package org.rk

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class EncryptionTest {

    private val secretKey = "superSecretKey123"
    private val wrongKey = "wrongSecretKey456"

    @Test
    fun `encrypt and decrypt should return original string`() {
        val originalText = "Hello, World!"
        val encrypted = Encryption.encrypt(originalText, secretKey)
        val decrypted = Encryption.decrypt(encrypted, secretKey)

        assertEquals(originalText, decrypted)
    }

    @Test
    fun `decryption with wrong key should fail`() {
        val originalText = "Sensitive data"
        val encrypted = Encryption.encrypt(originalText, secretKey)

        assertThrows<Exception> {
            Encryption.decrypt(encrypted, wrongKey)
        }
    }

    @Test
    fun `encryption should produce different outputs due to random IV`() {
        val text = "Repeatable content"
        val encrypted1 = Encryption.encrypt(text, secretKey)
        val encrypted2 = Encryption.encrypt(text, secretKey)

        assertNotEquals(encrypted1, encrypted2)
    }

    @Test
    fun `decrypting tampered data should throw exception`() {
        val text = "Tamper check"
        val encrypted = Encryption.encrypt(text, secretKey)

        // Tamper with the data
        val tampered = encrypted.replaceRange(10..12, "xyz")

        assertThrows<SecurityException> {
            Encryption.decrypt(tampered, secretKey)
        }
    }


    @Test
    fun `encrypt and decrypt empty string`() {
        val encrypted = Encryption.encrypt("", secretKey)
        val decrypted = Encryption.decrypt(encrypted, secretKey)

        assertEquals("", decrypted)
    }

    @Test
    fun `output of encryption should be valid Base64`() {
        val encrypted = Encryption.encrypt("test", secretKey)

        assertDoesNotThrow {
            Base64.getDecoder().decode(encrypted)
        }
    }
}
