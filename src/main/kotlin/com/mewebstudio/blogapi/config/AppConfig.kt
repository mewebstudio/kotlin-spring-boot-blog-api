package com.mewebstudio.blogapi.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.mewebstudio.blogapi.dto.util.TrimmingStringDeserializer
import com.mewebstudio.blogapi.util.Constants.SECURITY_SCHEME_NAME
import io.swagger.v3.core.jackson.ModelResolver
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver
import org.thymeleaf.spring6.SpringTemplateEngine
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver
import org.thymeleaf.templatemode.TemplateMode
import java.nio.charset.StandardCharsets
import java.util.*


@Configuration
class AppConfig {
    /**
     * Locale resolver bean.
     *
     * @param defaultLocale String
     * @return LocaleResolver
     */
    @Bean
    fun localeResolver(
        @Value("\${spring.application.default-locale:en}") defaultLocale: String?,
        @Value("\${spring.application.default-timezone:UTC}") defaultTimezone: String?
    ): LocaleResolver {
        val localResolver = AcceptHeaderLocaleResolver()
        localResolver.setDefaultLocale(Locale.Builder().setLanguage(defaultLocale).build())
        TimeZone.setDefault(TimeZone.getTimeZone(defaultTimezone))

        return localResolver
    }

    /**
     * Model resolver bean.
     *
     * @param objectMapper ObjectMapper
     * @return ModelResolver
     */
    @Bean
    fun modelResolver(objectMapper: ObjectMapper): ModelResolver {
        return ModelResolver(objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE))
    }

    /**
     * Password encoder bean.
     *
     * @return PasswordEncoder
     */
    @Bean
    fun delegatingPasswordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    /**
     * Spring template engine bean.
     *
     * @return SpringTemplateEngine
     */
    @Bean
    fun springTemplateEngine(): SpringTemplateEngine {
        val templateEngine = SpringTemplateEngine()
        templateEngine.addTemplateResolver(htmlTemplateResolver())

        return templateEngine
    }

    /**
     * Spring resource template resolver bean.
     *
     * @return SpringResourceTemplateResolver
     */
    @Bean
    fun htmlTemplateResolver(): SpringResourceTemplateResolver {
        val emailTemplateResolver = SpringResourceTemplateResolver()
        emailTemplateResolver.prefix = "classpath:/templates/"
        emailTemplateResolver.suffix = ".html"
        emailTemplateResolver.templateMode = TemplateMode.HTML
        emailTemplateResolver.characterEncoding = StandardCharsets.UTF_8.name()

        return emailTemplateResolver
    }

    /**
     * Object mapper bean.
     *
     * @return ObjectMapper
     */
    @Bean
    fun objectMapper(): ObjectMapper {
        val module = SimpleModule().apply {
            addDeserializer(String::class.java, TrimmingStringDeserializer())
        }

        return ObjectMapper().registerModule(KotlinModule.Builder().build()).apply {
            registerModule(module)
            enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            registerModule(JavaTimeModule())
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
            propertyNamingStrategy = PropertyNamingStrategies.LOWER_CAMEL_CASE
        }
    }

    /**
     * OpenAPI bean.
     *
     * @param title       String
     * @param description String
     * @return OpenAPI
     */
    @Bean
    fun customOpenAPI(
        @Value("\${spring.application.name}") title: String?,
        @Value("\${spring.application.description}") description: String?,
        @Value("\${spring.application.version}") version: String?,
        @Value("\${spring.application.url}") url: String?
    ): OpenAPI {
        return OpenAPI()
            .components(
                Components()
                    .addSecuritySchemes(
                        SECURITY_SCHEME_NAME, SecurityScheme()
                            .name(SECURITY_SCHEME_NAME)
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                    )
            )
            .info(
                Info().title(title).description(description).version(version)
                    .termsOfService(url)
                    .license(License().name("Apache 2.0").url("https://springdoc.org"))
            )
    }
}
