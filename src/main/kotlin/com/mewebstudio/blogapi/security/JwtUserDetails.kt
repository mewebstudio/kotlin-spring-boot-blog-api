package com.mewebstudio.blogapi.security

import com.mewebstudio.blogapi.entity.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.stream.Collectors

class JwtUserDetails(
    var id: String,
    private var username: String,
    private var password: String,
    private var authorities: Collection<GrantedAuthority>
) : UserDetails {

    companion object {
        /**
         * Create JwtUserDetails from User.
         *
         * @param user User
         * @return JwtUserDetails
         */
        fun create(user: User): JwtUserDetails = run {
            val authorities: List<GrantedAuthority> = user.roles.stream()
                .map { role -> SimpleGrantedAuthority(role) }
                .collect(Collectors.toList())

            JwtUserDetails(user.id.toString(), user.email, user.password, authorities)
        }
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = authorities.toMutableList()

    override fun getPassword(): String = "{noop}$password"

    override fun getUsername(): String = username

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}
