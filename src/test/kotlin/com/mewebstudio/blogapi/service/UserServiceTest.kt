package com.mewebstudio.blogapi.service

import com.mewebstudio.blogapi.dto.request.user.CreateUserRequest
import com.mewebstudio.blogapi.entity.User
import com.mewebstudio.blogapi.exception.NotFoundException
import com.mewebstudio.blogapi.repository.UserRepository
import com.mewebstudio.blogapi.security.JwtUserDetails
import com.mewebstudio.blogapi.util.Enums
import org.instancio.Instancio
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.function.Executable
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.lenient
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.validation.BindException
import java.util.*

@Tag("unit")
@ExtendWith(MockitoExtension::class)
@DisplayName("Unit tests for UserService")
class UserServiceTest {
    @InjectMocks
    private lateinit var userService: UserService

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @Mock
    private lateinit var messageSourceService: MessageSourceService

    @Mock
    private var authentication: Authentication? = null

    @Mock
    private var securityContext: SecurityContext? = null

    private var user = Instancio.create(User::class.java)

    private var jwtUserDetails = JwtUserDetails.create(user)

    @BeforeEach
    fun setUp() {
        SecurityContextHolder.setContext(securityContext)
        lenient().`when`(securityContext!!.authentication).thenReturn(authentication)
        lenient().`when`(authentication!!.principal).thenReturn(jwtUserDetails)
        lenient().`when`(authentication!!.isAuthenticated).thenReturn(true)

        lenient().`when`(messageSourceService.get("user")).thenReturn("User")
        lenient().`when`(
            messageSourceService.get(
                "not_found_with_param",
                arrayOf("User")
            )
        ).thenReturn("User not found")

        user.gender = Enums.GenderEnum.MALE
    }

    @Test
    @DisplayName("Test count all users")
    fun `should return count users`() {
        // Given
        `when`(userRepository.count()).thenReturn(1L)
        // When
        val result = userService.count()
        // Then
        assert(result == 1L)
    }

    @Nested
    @DisplayName("Test create scenarios")
    inner class CreateTest {
        private val request: CreateUserRequest = Instancio.create(CreateUserRequest::class.java)

        @BeforeEach
        fun setUp() {
            request.email = user.email
            request.gender = Enums.GenderEnum.MALE.value
        }

        @Test
        @DisplayName("Happy path")
        fun `should create user`() {
            // Given
            `when`(passwordEncoder.encode(request.password)).thenReturn("encodedPassword")
            `when`(userRepository.save(any(User::class.java))).thenReturn(user)
            // When
            val result = userService.create(request)
            // Then
            assertNotNull(result)
            assertEquals(user.email.lowercase(), result.email.lowercase())
            assertEquals("encodedPassword", result.password)
            verify(userRepository).save(any(User::class.java))
        }

        @Test
        @DisplayName("Should throw BindException when email already exists")
        fun `should throw BindException when email already exists`() {
            // Given
            request.email = "invalid-email"
            `when`(userRepository.findByEmail(request.email!!)).thenReturn(user)
            // When
            val executable = Executable { userService.create(request) }
            // Then
            assertThrows(BindException::class.java, executable)
            verify(userRepository).findByEmail(request.email!!)
        }
    }

    @Nested
    @DisplayName("Test findById scenarios")
    inner class FindByIdTest {
        @Test
        @DisplayName("Test find user by id")
        fun `should find user by id`() {
            // Given
            `when`(userRepository.findById(user.id!!)).thenReturn(Optional.of(user))
            // When
            val result: User = userService.findById(user.id.toString())
            // Then
            assertNotNull(result)
            assertEquals(user.email, result.email)
            verify(userRepository).findById(user.id!!)
        }

        @Test
        @DisplayName("Test find user by id throws NotFoundException")
        fun `should throw NotFoundException when user not found by id`() {
            // Given
            `when`(userRepository.findById(user.id!!)).thenReturn(Optional.empty())
            // When
            val executable = Executable { userService.findById(user.id!!) }
            // Then
            assertThrows(NotFoundException::class.java, executable)
            verify(userRepository).findById(user.id!!)
        }
    }

    @Nested
    @DisplayName("Test load user by id scenarios")
    inner class LoadUserByIdTest {
        @Test
        @DisplayName("Happy path load user by id")
        fun `should load user by id`() {
            // Given
            `when`(userRepository.findById(user.id!!)).thenReturn(Optional.of(user))
            // When
            val userDetails = userService.loadUserById(user.id.toString())
            // Then
            assertNotNull(userDetails)
            assertEquals(user.email, (userDetails as JwtUserDetails).username)
            verify(userRepository).findById(user.id!!)
        }

        @Test
        @DisplayName("Test load user by id throws NotFoundException")
        fun `should throw UsernameNotFoundException when user not found by id`() {
            // Given
            `when`(userRepository.findById(user.id!!)).thenReturn(Optional.empty())
            // When
            val executable = Executable { userService.loadUserById(user.id!!) }
            // Then
            assertThrows(NotFoundException::class.java, executable)
            verify(userRepository).findById(user.id!!)
        }
    }

    @Test
    @DisplayName("Test for getPrincipal")
    fun `should get principal`() {
        // When
        val result = userService.getPrincipal(authentication!!)
        // Then
        assertEquals(result, jwtUserDetails)
    }

    @Nested
    @DisplayName("Test getUser scenarios")
    inner class GetUserTest {
        @Test
        @DisplayName("Should return user when authenticated")
        fun `should return user when authenticated`() {
            // Given
            `when`(authentication?.isAuthenticated).thenReturn(true)
            `when`(authentication?.principal).thenReturn(jwtUserDetails)
            `when`(userRepository.findById(user.id!!)).thenReturn(Optional.of(user))
            // When
            val result = userService.getUser()
            // Then
            assertNotNull(result)
            assertEquals(user.email, result.email)
            verify(authentication)?.isAuthenticated
            verify(userRepository).findById(user.id!!)
        }

        @Test
        @DisplayName("Should throw BadCredentialsException when not authenticated")
        fun `should throw BadCredentialsException when not authenticated`() {
            // Given
            `when`(authentication?.isAuthenticated).thenReturn(false)
            // When
            val executable = Executable { userService.getUser() }
            // Then
            assertThrows(BadCredentialsException::class.java, executable)
            verify(authentication)?.isAuthenticated
        }

        @Test
        @DisplayName("Should throw BadCredentialsException when user details not found")
        fun `should throw BadCredentialsException when user details not found`() {
            // Given
            `when`(authentication?.isAuthenticated).thenReturn(true)
            `when`(authentication?.principal).thenReturn(jwtUserDetails)
            `when`(userRepository.findById(user.id!!)).thenThrow(RuntimeException("User not found"))
            // When
            val executable = Executable { userService.getUser() }
            // Then
            assertThrows(BadCredentialsException::class.java, executable)
            verify(authentication)?.isAuthenticated
            verify(userRepository).findById(user.id!!)
        }
    }
}
