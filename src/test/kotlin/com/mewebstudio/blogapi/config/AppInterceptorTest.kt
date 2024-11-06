package com.mewebstudio.blogapi.config

import com.mewebstudio.blogapi.TestController
import com.mewebstudio.blogapi.service.AuthService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.web.method.HandlerMethod
import java.util.*

@Tag("unit")
@ExtendWith(MockitoExtension::class)
@DisplayName("Unit tests for AppInterceptor")
class AppInterceptorTest {
    @InjectMocks
    private lateinit var appInterceptor: AppInterceptor

    @Mock
    private lateinit var authService: AuthService

    @Mock
    private lateinit var request: HttpServletRequest

    @Mock
    private lateinit var response: HttpServletResponse

    @Mock
    private lateinit var handlerMethod: HandlerMethod

    @BeforeEach
    fun setup() {
        val controller = TestController::class.java.getMethod("validMethodName")
        handlerMethod = HandlerMethod(controller.declaringClass, controller)
    }

    @Test
    fun `preHandle should allow access when user is authenticated and authorized`() {
        // Given
        val parameterNames = listOf<String>().iterator()
        `when`(request.parameterNames).thenReturn(Collections.enumeration(parameterNames.asSequence().toList()))
        // When
        val result = appInterceptor.preHandle(request, response, handlerMethod)
        // Then
        assert(result) { "Expected preHandle to return true" }
    }
}
