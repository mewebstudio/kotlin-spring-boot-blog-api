package com.mewebstudio.blogapi.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig(private val interceptor: AppInterceptor) : WebMvcConfigurer {
    @Value("\${springdoc.swagger-ui.path}")
    private lateinit var swaggerUiPath: String

    override fun addViewControllers(registry: ViewControllerRegistry) {
        registry.addRedirectViewController("/", swaggerUiPath)
    }

    /**
     * Add interceptors to the registry.
     *
     * @param registry InterceptorRegistry -- A registry of MappedInterceptor instances.
     */
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(interceptor).addPathPatterns("/**")
    }
}
