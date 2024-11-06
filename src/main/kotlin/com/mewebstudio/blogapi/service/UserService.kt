package com.mewebstudio.blogapi.service

import com.mewebstudio.blogapi.dto.request.user.CreateUserRequest
import com.mewebstudio.blogapi.dto.request.user.IUpdateUserRequest
import com.mewebstudio.blogapi.dto.request.user.UpdateProfileRequest
import com.mewebstudio.blogapi.dto.request.user.UpdateUserRequest
import com.mewebstudio.blogapi.entity.User
import com.mewebstudio.blogapi.entity.specification.UserFilterSpecification
import com.mewebstudio.blogapi.entity.specification.criteria.PaginationCriteria
import com.mewebstudio.blogapi.entity.specification.criteria.UserCriteria
import com.mewebstudio.blogapi.exception.NotFoundException
import com.mewebstudio.blogapi.repository.UserRepository
import com.mewebstudio.blogapi.security.JwtUserDetails
import com.mewebstudio.blogapi.util.Enums
import com.mewebstudio.blogapi.util.Helpers
import com.mewebstudio.blogapi.util.PageRequestBuilder
import com.mewebstudio.blogapi.util.logger
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
    private val messageSourceService: MessageSourceService
) {
    private val log: Logger by logger()

    /**
     * Get authentication.
     *
     * @return Authentication
     */
    fun getAuthentication(): Authentication {
        return SecurityContextHolder.getContext().authentication
    }

    /**
     * Count all users.
     *
     * @return Long
     */
    fun count(): Long {
        return userRepository.count()
    }

    /**
     * Find all users with pagination.
     *
     * @param criteria UserCriteria
     * @param paginationCriteria PaginationCriteria
     * @return Page<User>
     */
    fun findAll(criteria: UserCriteria, paginationCriteria: PaginationCriteria): Page<User> {
        return userRepository.findAll(UserFilterSpecification(criteria), PageRequestBuilder.build(paginationCriteria))
    }

    /**
     * Find user by email.
     *
     * @param id UUID
     * @return User
     * @throws NotFoundException
     */
    fun findById(id: UUID): User {
        return userRepository.findById(id)
            .orElseThrow {
                NotFoundException(
                    messageSourceService.get(
                        "not_found_with_param",
                        arrayOf(messageSourceService.get("user"))
                    )
                )
            }
    }

    /**
     * Find user by email.
     *
     * @param id String
     * @return User
     * @throws NotFoundException
     */
    fun findById(id: String): User {
        return findById(UUID.fromString(id))
    }

    /**
     * Find a user by email.
     *
     * @param email String.
     * @return User
     */
    fun findByEmail(email: String): User {
        return userRepository.findByEmail(email.lowercase()) ?: throw NotFoundException(
            messageSourceService.get("not_found_with_param", arrayOf(messageSourceService.get("user")))
        )
    }

    /**
     * Load user details by username.
     *
     * @param email String
     * @return UserDetails
     * @throws UsernameNotFoundException email not found exception.
     */
    fun loadUserByEmail(email: String): UserDetails {
        val user: User = userRepository.findByEmail(email.lowercase()) ?: throw NotFoundException(
            messageSourceService.get("not_found_with_param", arrayOf(messageSourceService.get("user")))
        )

        return JwtUserDetails.create(user)
    }

    /**
     * Loads user details by UUID.
     *
     * @param id UUID
     * @return UserDetails
     * @throws NotFoundException
     */
    fun loadUserById(id: UUID): UserDetails {
        val user = userRepository.findById(id)
            .orElseThrow {
                NotFoundException(
                    messageSourceService.get(
                        "not_found_with_param",
                        arrayOf(messageSourceService.get("user"))
                    )
                )
            }

        return JwtUserDetails.create(user)
    }

    /**
     * Loads user details by UUID string.
     *
     * @param id String
     * @return UserDetails
     * @throws NotFoundException
     */
    fun loadUserById(id: String): UserDetails {
        return loadUserById(UUID.fromString(id))
    }

    /**
     * Get UserDetails from security context.
     *
     * @param authentication Wrapper for security context
     * @return the Principal being authenticated or the authenticated principal after authentication.
     */
    fun getPrincipal(authentication: Authentication): JwtUserDetails {
        return authentication.principal as JwtUserDetails
    }

    /**
     * Return the authenticated user.
     *
     * @return user User
     */
    fun getUser(): User {
        val authentication = getAuthentication()

        if (!authentication.isAuthenticated) {
            log.warn("[JWT] User not authenticated!")
            throw BadCredentialsException(messageSourceService.get("bad_credentials"))
        }

        return try {
            findById(getPrincipal(authentication).id)
        } catch (e: Exception) {
            log.warn("[JWT] User details not found: ${e.message}")
            throw BadCredentialsException(messageSourceService.get("bad_credentials"))
        }
    }

    /**
     * Create a new user.
     *
     * @param request CreateUserRequest
     * @return User
     */
    @Throws(BindException::class)
    fun create(request: CreateUserRequest): User {
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
            gender = Helpers.searchEnum(Enums.GenderEnum::class.java, request.gender)!!
            email = request.email!!.lowercase()
            password = passwordEncoder.encode(request.password!!)
            roles = request.roles!!.map { it.uppercase() }
            blockedAt = request.isBlocked?.let { if (it) LocalDateTime.now() else null }
        }

        userRepository.save(user)
        log.info("[Create user] User created: ${user.email} - ${user.id}")

        return user
    }

    /**
     * Update user.
     *
     * @param id UUID
     * @param request UpdateUserRequest
     * @return User
     */
    fun update(id: String, request: UpdateUserRequest): User {
        val user = findById(id)
        updateEqualFields(request, user)

        if (request.roles.isNullOrEmpty().not()) {
            user.roles = request.roles!!.map { it.uppercase() }
        }

        if (request.isBlocked != null) {
            user.blockedAt = request.isBlocked?.let { if (it) LocalDateTime.now() else null }
        }

        userRepository.save(user)
        log.info("[Update user] ID: $id - Request: $request")

        return user
    }

    /**
     * Update user profile.
     *
     * @param request UpdateProfileRequest
     * @return User
     */
    fun updateProfile(request: UpdateProfileRequest): User {
        val user = getUser()
        updateEqualFields(request, user)

        userRepository.save(user)
        log.info("[Update profile] Profile updated: ${user.email} - ${user.id}")

        return user
    }

    /**
     * Delete user.
     *
     * @param id UUID
     */
    fun delete(id: UUID) {
        userRepository.delete(findById(id))
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
     * Update equal fields.
     *
     * @param request IUpdateUserRequest
     * @param user User
     */
    private fun updateEqualFields(
        request: IUpdateUserRequest,
        user: User
    ) {
        val bindingResult = BeanPropertyBindingResult(request, "request")
        if (request.email.isNullOrEmpty().not() && request.email.equals(user.email, ignoreCase = true).not()) {
            userRepository.findByEmailAndIdNot(request.email!!.lowercase(), user.id!!)?.let {
                log.error("[Update profile] User with email: ${request.email} already exists")
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
