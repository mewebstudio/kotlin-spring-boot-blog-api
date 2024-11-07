package com.mewebstudio.blogapi.security

import com.mewebstudio.blogapi.entity.User
import com.mewebstudio.blogapi.service.MessageSourceService
import com.mewebstudio.blogapi.service.UserService
import com.mewebstudio.blogapi.util.logger
import jakarta.transaction.Transactional
import org.slf4j.Logger
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.util.*

@Component
class CustomAuthenticationManager(
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder,
    private val messageSourceService: MessageSourceService
) : AuthenticationManager {
    private val log: Logger by logger()

    /**
     * Authenticate user.
     *
     * @param authentication Authentication
     */
    @Transactional
    @Throws(AuthenticationException::class)
    override fun authenticate(authentication: Authentication): Authentication = run {
        val user: User = userService.findByEmail(authentication.name)

        if (Objects.nonNull(authentication.credentials)) {
            val matches = passwordEncoder.matches(authentication.credentials.toString(), user.password)
            if (!matches) {
                log.error("AuthenticationCredentialsNotFoundException occurred for {}", authentication.name)
                throw AuthenticationCredentialsNotFoundException(messageSourceService.get("bad_credentials"))
            }
        }

        val authorities: List<SimpleGrantedAuthority> = user.roles.stream()
            .map { role -> SimpleGrantedAuthority(role) }
            .toList()
        val userDetails: UserDetails = userService.loadUserByEmail(authentication.name)
        val auth = UsernamePasswordAuthenticationToken(userDetails, user.password, authorities)
        SecurityContextHolder.getContext().authentication = auth

        auth
    }
}
