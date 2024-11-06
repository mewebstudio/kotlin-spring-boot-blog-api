package com.mewebstudio.blogapi.config

import com.mewebstudio.blogapi.security.CheckRole
import com.mewebstudio.blogapi.service.AuthService
import com.mewebstudio.blogapi.util.logger
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.Logger
import org.springframework.context.annotation.Profile
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import java.security.InvalidParameterException
import java.util.*

@Component
@Profile("!mvcIT")
class AppInterceptor(private val authService: AuthService) : HandlerInterceptor {
    private val log: Logger by logger()

    /**
     * Interception point before the execution of a handler.
     *
     * @param request HttpServletRequest -- Request information for HTTP servlets.
     * @param response HttpServletRequest -- Response information for HTTP servlets.
     * @param handler Any -- Class Object is the root of the class hierarchy.
     * @return Boolean -- true or false or AccessDeniedException
     */
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val handlerMethod = handler as? HandlerMethod ?: return true
        validateQueryParams(request, handlerMethod)
        log.info("q: ${request.getParameter("q")}")
        val authorizeAnnotation = getAuthorizeAnnotation(handlerMethod)
        if (authorizeAnnotation != null && !hasCheckRoleAnnotation(authorizeAnnotation)) {
            log.error("AccessDeniedException: Role is not valid for this API.")
            throw AccessDeniedException("You are not allowed to perform this operation.")
        }

        return true
    }

    /**
     * Validation of the request params to check unhandled ones.
     *
     * @param request HttpServletRequest -- Request information for HTTP servlets.
     * @param handler HandlerMethod -- Encapsulates information about a handler method consisting of a method
     */
    private fun validateQueryParams(request: HttpServletRequest, handler: HandlerMethod) {
        val queryParams = Collections.list(request.parameterNames).toMutableList()
        val expectedParams = mutableListOf<String>()
        handler.methodParameters.forEach { methodParameter ->
            methodParameter.parameter.getAnnotation(RequestParam::class.java)?.let {
                expectedParams.add(element = it.name)
            }
        }

        val hasModelAttribute = handler.methodParameters.any {
            it.getParameterAnnotation(ModelAttribute::class.java) != null
        }

        queryParams.removeAll(expectedParams)
        if (queryParams.isNotEmpty() && !hasModelAttribute) {
            log.error("Unexpected parameters: $queryParams")
            throw InvalidParameterException("Unexpected parameter: $queryParams")
        }
    }

    /**
     * Get infos for CheckRole annotation that defined for class or method.
     *
     * @param handlerMethod HandlerMethod -- RequestMapping method that reached to server
     * @return T -- Authorize annotation or null
     */
    private fun getAuthorizeAnnotation(handlerMethod: HandlerMethod): CheckRole? {
        return when {
            handlerMethod.method.declaringClass.isAnnotationPresent(CheckRole::class.java) ->
                handlerMethod.method.declaringClass.getAnnotation(CheckRole::class.java)

            handlerMethod.method.isAnnotationPresent(CheckRole::class.java) ->
                handlerMethod.method.getAnnotation(CheckRole::class.java)

            handlerMethod.method.javaClass.isAnnotationPresent(CheckRole::class.java) ->
                handlerMethod.method.javaClass.getAnnotation(CheckRole::class.java)

            else -> null
        }
    }

    /**
     * Checks the roles of user for defined CheckRole annotation.
     *
     * @param checkRole - parameter that has roles
     * @return -- false if not authorized
     * @throws BadCredentialsException -- throws BadCredentialsException
     * @throws AccessDeniedException   -- throws AccessDeniedException
     */
    @Throws(BadCredentialsException::class, AccessDeniedException::class)
    private fun hasCheckRoleAnnotation(checkRole: CheckRole): Boolean {
        authService.getPrincipal()
            ?: throw BadCredentialsException("You have to be authenticated to perform this operation").also {
                log.error("You have to be authenticated to perform this operation")
            }

        val roles = checkRole.roles.map { it.value }.toTypedArray()
        return try {
            if (!authService.isAuthorized(*roles)) {
                log.error("Authorization is failed.")
                false
            } else {
                true
            }
        } catch (e: Exception) {
            log.trace("Exception occurred while authorizing: ${ExceptionUtils.getStackTrace(e)}")
            false
        }
    }
}
