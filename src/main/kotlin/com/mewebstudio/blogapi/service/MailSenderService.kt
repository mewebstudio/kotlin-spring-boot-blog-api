package com.mewebstudio.blogapi.service

import com.mewebstudio.blogapi.entity.EmailVerificationToken
import com.mewebstudio.blogapi.util.logger
import jakarta.mail.MessagingException
import jakarta.mail.internet.InternetAddress
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine

@Service
class MailSenderService(
    private val mailSender: JavaMailSender,
    private val templateEngine: SpringTemplateEngine,
    private val messageSourceService: MessageSourceService
) {
    private val log: Logger by logger()

    @Value("\${spring.application.url}")
    private lateinit var applicationUrl: String

    @Value("\${spring.application.frontend-url}")
    private lateinit var frontendUrl: String

    @Value("\${spring.mail.username}")
    private lateinit var senderAddress: String

    @Value("\${spring.mail.sender}")
    private lateinit var senderName: String

    /**
     * Send user email verification link.
     *
     * @param emailVerificationToken EmailVerificationToken
     */
    fun sendUserEmailVerification(emailVerificationToken: EmailVerificationToken) {
        try {
            val user = emailVerificationToken.user
            if (user == null) {
                log.error("[EmailService] User not found: $emailVerificationToken")
                return
            }

            log.info("[EmailService] Sending verification e-mail: ${user.id} - ${user.email}")

            val url = "$frontendUrl/auth/email-verification/${emailVerificationToken.token}"
            val ctx = createContext()
            ctx.setVariable("firstname", user.firstname)
            ctx.setVariable("lastname", user.lastname)
            ctx.setVariable("fullName", user.fullName)
            ctx.setVariable("url", url)

            send(
                from = InternetAddress(senderAddress, senderName),
                to = InternetAddress(user.email, user.fullName),
                subject = messageSourceService.get("email_verification"),
                content = templateEngine.process("mail/user-email-verification", ctx)
            )

            log.info("[EmailService] Sent verification e-mail: ${user.id} - ${user.email}")
        } catch (e: Exception) {
            log.error("[EmailService] Failed to send verification e-mail: ${e.message}")
        }
    }

    /**
     * Create context for template engine.
     *
     * @return Context
     */
    private fun createContext(): Context = run {
        val ctx = Context(LocaleContextHolder.getLocale())
        ctx.setVariable("senderAddress", senderAddress)
        ctx.setVariable("senderName", senderName)
        ctx.setVariable("applicationUrl", applicationUrl)
        ctx.setVariable("frontendUrl", frontendUrl)

        ctx
    }

    /**
     * Send an e-mail to the specified address.
     *
     * @param from    Address who sent
     * @param to      Address who receive
     * @param subject String subject
     * @param content    String message
     * @throws MessagingException when sending fails
     */
    @Throws(MessagingException::class)
    private fun send(from: InternetAddress, to: InternetAddress, subject: String, content: String) {
        val mimeMessage = mailSender.createMimeMessage()
        val mimeMessageHelper = MimeMessageHelper(mimeMessage, true)
        mimeMessageHelper.setFrom(from)
        mimeMessageHelper.setTo(to)
        mimeMessageHelper.setSubject(subject)
        mimeMessageHelper.setText(content, true)

        mailSender.send(mimeMessage)
    }
}