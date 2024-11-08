package com.mewebstudio.blogapi.service

import com.mewebstudio.blogapi.dto.request.auth.LoginRequest
import com.mewebstudio.blogapi.dto.response.auth.TokenResponse
import com.mewebstudio.blogapi.entity.JwtToken
import com.mewebstudio.blogapi.entity.User
import com.mewebstudio.blogapi.exception.NotFoundException
import com.mewebstudio.blogapi.security.JwtTokenProvider
import com.mewebstudio.blogapi.security.JwtUserDetails
import com.mewebstudio.blogapi.util.Enums
import jakarta.servlet.http.HttpServletRequest
import org.instancio.Instancio
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.function.Executable
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.lenient
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import java.time.LocalDateTime
import java.util.*

@Tag("unit")
@ExtendWith(MockitoExtension::class)
@DisplayName("Unit tests for AuthService")
class AuthServiceTest {
    @InjectMocks
    private lateinit var authService: AuthService

    @Mock
    private lateinit var userService: UserService

    @Mock
    private lateinit var messageSourceService: MessageSourceService

    @Mock
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @Mock
    private lateinit var jwtTokenService: JwtTokenService

    @Mock
    private lateinit var authenticationManager: AuthenticationManager

    @Mock
    private var securityContext: SecurityContext? = null

    @Mock
    private var authentication: Authentication? = null

    @Mock
    private lateinit var httpServletRequest: HttpServletRequest

    private var user = Instancio.create(User::class.java)

    private lateinit var jwtUserDetails: JwtUserDetails

    private var jwtToken = Instancio.create(JwtToken::class.java)

    private var tokenResponse = Instancio.create(TokenResponse::class.java)

    private var bearerToken = "Bearer token"

    private var token = "token"

    @BeforeEach
    fun setUp() {
        lenient().`when`(jwtTokenProvider.getTokenExpiresIn()).thenReturn(1L)
        lenient().`when`(jwtTokenProvider.generateJwt(anyString())).thenReturn(tokenResponse.token)
        lenient().`when`(jwtTokenProvider.generateRefresh(anyString())).thenReturn(tokenResponse.refreshToken)
        lenient().`when`(httpServletRequest.remoteAddr).thenReturn("0.0.0.0")
        lenient().`when`(httpServletRequest.getHeader("User-Agent")).thenReturn("Mock-Agent")
        lenient().`when`(messageSourceService.get("bad_credentials")).thenReturn("Bad credentials")

        val authoritiesList = mutableListOf<GrantedAuthority>()
        authoritiesList.add(SimpleGrantedAuthority(Enums.RoleEnum.ADMIN.name))
        jwtUserDetails = JwtUserDetails(user.id.toString(), user.email!!, user.password!!, authoritiesList)

        SecurityContextHolder.setContext(securityContext)
        lenient().`when`(securityContext!!.authentication).thenReturn(authentication)
        lenient().`when`(authentication!!.principal).thenReturn(jwtUserDetails)

        user.roles = listOf(Enums.RoleEnum.ADMIN.value)
    }

    @Nested
    @DisplayName("Tests for login")
    inner class LoginTest {
        @Mock
        private var authentication: Authentication? = null

        private var request = Instancio.create(LoginRequest::class.java)

        private val jwtUserDetails = JwtUserDetails.create(user)

        @BeforeEach
        fun setUp() {
            request.rememberMe = true
            user.blockedAt = null
            lenient().`when`(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken::class.java)))
                .thenReturn(authentication)
            lenient().`when`(jwtTokenProvider.getPrincipal(authentication!!)).thenReturn(jwtUserDetails)
            lenient().doNothing()!!.`when`(jwtTokenProvider).setRememberMe()
        }

        @Test
        @DisplayName("Happy path: login should return token response")
        fun `login should return token response`() {
            // Given
            lenient().`when`(userService.findByEmail(anyString())).thenReturn(user)
            lenient().`when`(jwtTokenProvider.getPrincipal(authentication!!)).thenReturn(jwtUserDetails)
            // When
            val result = authService.login(request)
            // Then
            assertNotNull(result)
            assertEquals(tokenResponse.token, result.token)
            assertEquals(tokenResponse.refreshToken, result.refreshToken)
        }

        @Test
        @DisplayName("User is blocked: login should throw exception")
        fun `login should throw exception when user is blocked`() {
            // Given
            user.blockedAt = LocalDateTime.now()
            `when`(userService.findByEmail(anyString())).thenReturn(user)
            // When
            try {
                authService.login(request)
            } catch (e: Exception) {
                // Then
                assert(e is AuthenticationCredentialsNotFoundException)
                assertEquals("Bad credentials", e.message)
            }
        }

        @Test
        @DisplayName("User not found: login should throw exception")
        fun `login should throw exception when user is not found`() {
            // Given
            `when`(userService.findByEmail(anyString())).thenThrow(NotFoundException("User not found"))
            // When
            try {
                authService.login(request)
            } catch (e: Exception) {
                // Then
                assert(e is AuthenticationCredentialsNotFoundException)
            }
        }
    }

    @Nested
    @DisplayName("Tests for isAuthorized")
    inner class IsAuthorized {
        @Test
        @DisplayName("Happy path: isAuthorized should return true")
        fun `isAuthorized should return true`() {
            // Given
            val roles = arrayOf(Enums.RoleEnum.ADMIN.name)
            // When
            val result = authService.isAuthorized(*roles)
            // Then
            assertTrue(result)
        }

        @Test
        @DisplayName("User not found: isAuthorized should throw exception")
        fun `isAuthorized should throw exception when user is not found`() {
            // Given
            // When
            try {
                authService.isAuthorized(Enums.RoleEnum.ADMIN.name)
            } catch (e: Exception) {
                // Then
                assert(e is AuthenticationCredentialsNotFoundException)
                assertEquals("Access denied", e.message)
            }
        }
    }

    @Nested
    @DisplayName("Tests for getPrincipal")
    inner class GetPrincipal {
        @Test
        @DisplayName("Happy path: getPrincipal should return JwtUserDetails")
        fun `getPrincipal should return JwtUserDetails`() {
            // When
            val result = authService.getPrincipal()
            // Then
            assertNotNull(result)
            assertEquals(jwtUserDetails, result)
        }

        @Test
        @DisplayName("Exception path: getPrincipal should return null on ClassCastException")
        fun `getPrincipal should return null on ClassCastException`() {
            // Given
            `when`(authentication!!.principal).thenReturn("Not a JwtUserDetails")
            // When
            val result = authService.getPrincipal()
            // Then
            assertNull(result)
        }
    }

    @Nested
    @DisplayName("Tests for logout")
    inner class Logout {
        @BeforeEach
        fun setUp() {
            jwtToken.userId = user.id!!

            lenient().`when`(jwtTokenService.findByTokenOrRefreshToken(anyString())).thenReturn(jwtToken)
            lenient().doNothing().`when`(jwtTokenService).delete(jwtToken)
        }

        @Test
        @DisplayName("Happy path: logout with params should return nothing")
        fun `logout with params should return nothing`() {
            // Given
            // When
            authService.logout(user, token)
            // Then
            verify(jwtTokenService).delete(jwtToken)
        }

        @Test
        @DisplayName("Logout with params should throw AuthenticationCredentialsException")
        fun `logout with params should throw AuthenticationCredentialsException`() {
            // Given
            jwtToken.userId = UUID.randomUUID()
            // When
            val executable = Executable { authService.logout(user, token) }
            // Then
            assertThrows(AuthenticationCredentialsNotFoundException::class.java, executable)
        }

        @Test
        @DisplayName("Happy path: logout should return nothing")
        fun `logout should return nothing`() {
            // Given
            `when`(httpServletRequest.getHeader(anyString())).thenReturn(bearerToken)
            `when`(userService.getUser()).thenReturn(user)
            `when`(jwtTokenProvider.extractJwtFromBearerString(anyString())).thenReturn(token)
            // When
            authService.logout()
            // Then
            verify(jwtTokenService).delete(jwtToken)
        }

        @Test
        @DisplayName("Happy path: logout should throw AuthenticationCredentialsException")
        fun `logout should throw AuthenticationCredentialsException`() {
            // Given
            `when`(httpServletRequest.getHeader(anyString())).thenReturn(null)
            // When
            val executable = Executable { authService.logout() }
            // Then
            assertThrows(AuthenticationCredentialsNotFoundException::class.java, executable)
        }
    }
}
