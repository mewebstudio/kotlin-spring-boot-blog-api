package com.mewebstudio.blogapi.service

import com.mewebstudio.blogapi.util.logger
import org.slf4j.Logger
import org.springframework.context.MessageSource
import org.springframework.context.NoSuchMessageException
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import java.util.*

@Service
class MessageSourceService(private val messageSource: MessageSource?) {
    private val log: Logger by logger()

    /**
     * Get message from message source by key.
     *
     * @param code   String
     * @param params Object[]
     * @param locale Locale
     * @return message String
     */
    fun get(code: String, params: Array<String>?, locale: Locale?): String = run {
        try {
            messageSource!!.getMessage(code, params, locale!!)
        } catch (e: NoSuchMessageException) {
            log.warn("Translation message not found ($locale): $code / ${e.message}")
            return code
        }
    }

    /**
     * Get message from message source by key.
     *
     * @param code   String
     * @param params Object[]
     * @return message String
     */
    fun get(code: String, params: Array<String>): String = get(code, params, LocaleContextHolder.getLocale())

    /**
     * Get message from message source by key.
     *
     * @param code   String
     * @param locale Locale
     * @return message String
     */
    fun get(code: String, locale: Locale?): String = get(code, null, locale)

    /**
     * Get message from message source by key.
     *
     * @param code String
     * @return message String
     */
    fun get(code: String): String = get(code, null, LocaleContextHolder.getLocale())
}
