package com.mewebstudio.blogapi.util

import java.nio.charset.StandardCharsets
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object AESCipher {
    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/ECB/PKCS5Padding"

    /**
     * Encrypts plain text using AES algorithm.
     *
     * @param plainText String - The text that will be encrypted.
     * @param secretKey String - The key (256 bit) that will be used for encryption.
     * @return String - Base64 encoded string of the encrypted text.
     */
    @Throws(Exception::class)
    fun encrypt(plainText: String, secretKey: String): String {
        val keySpec = generateKey(secretKey)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)
        val encryptedBytes = cipher.doFinal(plainText.toByteArray(StandardCharsets.UTF_8))

        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    /**
     * Decrypts encrypted text using AES algorithm.
     *
     * @param encryptedText String - The text that will be decrypted.
     * @param secretKey String - The key (256 bit) that will be used for decryption.
     * @return String  - Decrypted text.
     */
    @Throws(Exception::class)
    fun decrypt(encryptedText: String, secretKey: String): String {
        val keySpec = generateKey(secretKey)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, keySpec)
        val encryptedBytes = Base64.getDecoder().decode(encryptedText)
        val decryptedBytes = cipher.doFinal(encryptedBytes)

        return String(decryptedBytes, StandardCharsets.UTF_8)
    }

    /**
     * Generates a secret key to be used for encryption and decryption.
     *
     * @param secretKey String - The key that will be used for encryption and decryption.
     * @return SecretKeySpec - The secret key spec.
     */
    private fun generateKey(secretKey: String): SecretKeySpec {
        val keyBytes = secretKey.toByteArray(StandardCharsets.UTF_8)
        return SecretKeySpec(keyBytes, ALGORITHM)
    }
}
