package com.mewebstudio.blogapi.service

import com.mewebstudio.blogapi.exception.CipherException
import com.mewebstudio.blogapi.util.AESCipher
import com.mewebstudio.blogapi.util.logger
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class CipherService {
    private val log: Logger by logger()

    @Value("\${spring.application.secret}")
    private lateinit var appSecret: String

    /**
     * Encrypt plain text with secret key.
     *
     * @param plainText String
     * @param secretKey String (256 bit)
     * @return String
     * @throws CipherException - Encrypting exception
     */
    fun encrypt(plainText: String, secretKey: String): String {
        try {
            return AESCipher.encrypt(plainText, secretKey)
        } catch (e: Exception) {
            log.error("Encrypting error", e)
            throw CipherException(e)
        }
    }

    /**
     * Encrypt plain text with app secret.
     *
     * @param plainText String
     * @return String
     * @throws CipherException - Encrypting exception
     */
    fun encrypt(plainText: String): String {
        return encrypt(plainText, appSecret)
    }

    /**
     * Decrypt cipher text with secret key.
     *
     * @param encryptedText String
     * @param secretKey     String (256 bit)
     * @return String
     * @throws CipherException - Decrypting exception
     */
    fun decrypt(encryptedText: String, secretKey: String): String {
        try {
            return AESCipher.decrypt(encryptedText, secretKey)
        } catch (e: java.lang.Exception) {
            log.error("Decrypting error", e)
            throw CipherException(e)
        }
    }

    /**
     * Decrypt cipher text with app secret.
     *
     * @param encryptedText String
     * @return String
     * @throws CipherException - Decrypting exception
     */
    fun decrypt(encryptedText: String): String {
        return decrypt(encryptedText, appSecret)
    }
}
