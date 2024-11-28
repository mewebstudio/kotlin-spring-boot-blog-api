package com.mewebstudio.blogapi.service

import com.mewebstudio.blogapi.dto.request.auth.ChangePasswordRequest
import com.mewebstudio.blogapi.dto.request.auth.PasswordRequest
import com.mewebstudio.blogapi.dto.request.user.CreateUserRequest
import com.mewebstudio.blogapi.dto.request.user.IUserRequest
import com.mewebstudio.blogapi.dto.request.user.RegisterUserRequest
import com.mewebstudio.blogapi.dto.request.user.UpdateProfileRequest
import com.mewebstudio.blogapi.dto.request.user.UpdateUserRequest
import com.mewebstudio.blogapi.dto.request.user.UserFilterRequest
import com.mewebstudio.blogapi.entity.User
import com.mewebstudio.blogapi.entity.specification.UserFilterSpecification
import com.mewebstudio.blogapi.entity.specification.criteria.UserCriteria
import com.mewebstudio.blogapi.exception.NotFoundException
import com.mewebstudio.blogapi.repository.UserRepository
import com.mewebstudio.blogapi.security.JwtUserDetails
import com.mewebstudio.blogapi.util.Enums
import com.mewebstudio.blogapi.util.Helpers
import com.mewebstudio.blogapi.util.PageRequestBuilder
import com.mewebstudio.blogapi.util.logger
import jakarta.transaction.Transactional
import org.slf4j.Logger
import org.springframework.data.domain.Page
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.BindException
import org.springframework.validation.FieldError
import java.time.LocalDateTime
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val emailVerificationTokenService: EmailVerificationTokenService,
    private val passwordResetTokenService: PasswordResetTokenService,
    private val messageSourceService: MessageSourceService
) {
    private val log: Logger by logger()

    /**
     * Get authentication.
     *
     * @return Authentication
     */
    fun getAuthentication(): Authentication = SecurityContextHolder.getContext().authentication

    /**
     * Count all users.
     *
     * @return Long
     */
    fun count(): Long = userRepository.count()

    /**
     * Find all users with pagination.
     *
     * @param request UserFilterRequest
     * @return Page<User>
     */
    fun findAll(request: UserFilterRequest): Page<User> =
        userRepository.findAll(
            UserFilterSpecification(
                UserCriteria(
                    roles = request.roles?.map { Helpers.searchEnum(Enums.RoleEnum::class.java, it)!! },
                    genders = request.genders?.map { Helpers.searchEnum(Enums.GenderEnum::class.java, it)!! },
                    createdUsers = request.createdUsers?.map { it },
                    updatedUsers = request.updatedUsers?.map { it },
                    createdAtStart = request.createdAtStart,
                    createdAtEnd = request.createdAtEnd,
                    isBlocked = request.isBlocked,
                    q = request.q
                )
            ),
            PageRequestBuilder.build(request.page, request.size, request.sortBy, request.sort)
        )

    /**
     * Find user by email.
     *
     * @param id UUID
     * @return User
     * @throws NotFoundException
     */
    fun findById(id: UUID): User = userRepository.findById(id).orElseThrow {
        NotFoundException(
            messageSourceService.get(
                "not_found_with_param",
                arrayOf(messageSourceService.get("user"))
            )
        )
    }

    /**
     * Find user by email.
     *
     * @param id String
     * @return User
     * @throws NotFoundException
     */
    fun findById(id: String): User = findById(UUID.fromString(id))

    /**
     * Find a user by email.
     *
     * @param email String.
     * @return User
     */
    fun findByEmail(email: String): User = userRepository.findByEmail(email.lowercase()) ?: throw NotFoundException(
        messageSourceService.get("not_found_with_param", arrayOf(messageSourceService.get("user")))
    )

    /**
     * Load user details by username.
     *
     * @param email String
     * @return UserDetails
     * @throws UsernameNotFoundException email not found exception.
     */
    fun loadUserByEmail(email: String): UserDetails = run {
        val user: User = userRepository.findByEmail(email.lowercase()) ?: throw NotFoundException(
            messageSourceService.get("not_found_with_param", arrayOf(messageSourceService.get("user")))
        )

        JwtUserDetails.create(user)
    }

    /**
     * Loads user details by UUID.
     *
     * @param id UUID
     * @return UserDetails
     * @throws NotFoundException
     */
    fun loadUserById(id: UUID): UserDetails = run {
        val user = userRepository.findById(id)
            .orElseThrow {
                NotFoundException(
                    messageSourceService.get(
                        "not_found_with_param",
                        arrayOf(messageSourceService.get("user"))
                    )
                )
            }

        JwtUserDetails.create(user)
    }

    /**
     * Loads user details by UUID string.
     *
     * @param id String
     * @return UserDetails
     * @throws NotFoundException
     */
    fun loadUserById(id: String): UserDetails = loadUserById(UUID.fromString(id))

    /**
     * Get UserDetails from security context.
     *
     * @param authentication Wrapper for security context
     * @return the Principal being authenticated or the authenticated principal after authentication.
     */
    fun getPrincipal(authentication: Authentication): JwtUserDetails = authentication.principal as JwtUserDetails

    /**
     * Return the authenticated user.
     *
     * @return user User
     */
    fun getUser(): User = run {
        val authentication = getAuthentication()
        if (!authentication.isAuthenticated) {
            log.warn("[JWT] User not authenticated!")
            throw BadCredentialsException(messageSourceService.get("bad_credentials"))
        }

        try {
            findById(getPrincipal(authentication).id)
        } catch (e: Exception) {
            log.warn("[JWT] User details not found: ${e.message}")
            throw BadCredentialsException(messageSourceService.get("bad_credentials"))
        }
    }

    /**
     * Register a new user.
     *
     * @param request RegisterUserRequest
     * @return User
     */
    @Transactional
    @Throws(BindException::class)
    fun register(request: RegisterUserRequest): User = run {
        val user = createEqualFields(request)
        user.roles = listOf(Enums.RoleEnum.USER.name)

        userRepository.save(user)
        log.info("[Register user] User registered: ${user.email} - ${user.id}")

        create(user)
    }

    /**
     * Create a new user.
     *
     * @param request CreateUserRequest
     * @return User
     */
    @Transactional
    @Throws(BindException::class)
    fun create(request: CreateUserRequest): User = run {
        val user = createEqualFields(request)

        user.roles = request.roles!!.map { it.uppercase() }
        user.blockedAt = request.isBlocked?.let { if (it) LocalDateTime.now() else null }
        user.emailVerifiedAt = request.isEmailVerified?.let { if (it) LocalDateTime.now() else null }
        user.createdUser = getUser()

        create(user)
    }

    /**
     * Create user.
     *
     * @param user User
     * @return User
     */
    @Transactional
    fun create(user: User): User = run {
        if (user.emailVerifiedAt == null) {
            user.emailVerificationToken = emailVerificationTokenService.create(user)
        }

        userRepository.save(user).also { log.info("[Create user] User created: $user") }
    }

    /**
     * Verify email.
     *
     * @param token String
     */
    @Transactional
    fun verifyEmail(token: String) {
        val user = emailVerificationTokenService.getUserByToken(token)
        user.emailVerifiedAt = LocalDateTime.now()
        user.emailVerificationToken = null
        userRepository.save(user).also { log.info("Email verified: $user") }
    }

    /**
     * Create password reset.
     *
     * @param request PasswordRequest
     */
    @Transactional
    fun createPasswordReset(request: PasswordRequest) {
        val user = findByEmail(request.email!!)
        user.passwordResetToken = passwordResetTokenService.create(user)
        userRepository.save(user).also { log.info("Password reset created: $user") }
    }

    /**
     * Change password.
     *
     * @param token String
     * @param request ChangePasswordRequest
     */
    fun changePassword(token: String, request: ChangePasswordRequest) {
        val user = passwordResetTokenService.getUserByToken(token)
        user.password = passwordEncoder.encode(request.password)
        user.passwordResetToken = null
        userRepository.save(user).also { log.info("Password changed: $user") }
    }

    /**
     * Update user.
     *
     * @param id UUID
     * @param request UpdateUserRequest
     * @return User
     */
    fun update(id: String, request: UpdateUserRequest): User = run {
        val user = findById(id)
        updateEqualFields(request, user)

        request.roles?.takeIf { it.isNotEmpty() }?.let {
            user.roles = it.map { role -> role.uppercase() }
        }

        request.isBlocked?.let {
            user.blockedAt = if (it) LocalDateTime.now() else null
        }

        user.updatedUser = getUser()

        userRepository.save(user).also { log.info("User updated: $user") }
    }

    /**
     * Update user profile.
     *
     * @param request UpdateProfileRequest
     * @return User
     */
    fun updateProfile(request: UpdateProfileRequest): User = run {
        val user = getUser()
        updateEqualFields(request, user)

        userRepository.save(user).also { log.info("Profile updated: $user") }
    }

    /**
     * Delete user.
     *
     * @param id UUID
     */
    fun delete(id: UUID) {
        userRepository.delete(findById(id)).also { log.info("User deleted: $id") }
    }

    /**
     * Delete user.
     *
     * @param id String
     */
    fun delete(id: String) {
        delete(UUID.fromString(id))
    }

    /**
     * Create equal fields.
     *
     * @param request IUserRequest
     * @return User
     */
    private fun createEqualFields(request: IUserRequest): User = run {
        val bindingResult = BeanPropertyBindingResult(request, "request")
        userRepository.findByEmail(request.email!!.lowercase())?.let {
            log.error("[Create user] User with email: ${request.email} already exists")
            bindingResult.addError(
                FieldError(
                    bindingResult.objectName, "email",
                    messageSourceService.get(
                        "already_exists_with_param",
                        arrayOf(messageSourceService.get("email"))
                    )
                )
            )
        }

        if (bindingResult.hasErrors()) {
            throw BindException(bindingResult)
        }

        val user = User().apply {
            firstname = request.firstname!!
            lastname = request.lastname!!
            gender = request.gender.let { Helpers.searchEnum(Enums.GenderEnum::class.java, request.gender!!)!! }
            email = request.email!!.lowercase()
            password = passwordEncoder.encode(request.password!!)
        }

        user
    }

    /**
     * Update equal fields.
     *
     * @param request IUserRequest
     * @param user User
     */
    private fun updateEqualFields(request: IUserRequest, user: User) {
        val bindingResult = BeanPropertyBindingResult(request, "request")
        request.email?.takeIf { it.isNotEmpty() && !it.equals(user.email, ignoreCase = true) }?.let {
            userRepository.findByEmailAndIdNot(it.lowercase(), user.id!!)?.let { existingUser ->
                log.error("User with email: $existingUser already exists")
                bindingResult.addError(
                    FieldError(
                        bindingResult.objectName, "email",
                        messageSourceService.get(
                            "already_exists_with_param",
                            arrayOf(messageSourceService.get("email"))
                        )
                    )
                )
            }
        }

        if (bindingResult.hasErrors()) {
            throw BindException(bindingResult)
        }

        user.email = request.email?.lowercase() ?: user.email
        user.firstname = request.firstname ?: user.firstname
        user.lastname = request.lastname ?: user.lastname
        user.gender = request.gender?.let {
            Helpers.searchEnum(Enums.GenderEnum::class.java, request.gender!!)!!
        } ?: user.gender

        if (request.password != null) {
            user.password = passwordEncoder.encode(request.password)
        }
    }
}
