package com.mewebstudio.blogapi.entity.specification

import com.mewebstudio.blogapi.entity.User
import com.mewebstudio.blogapi.entity.specification.criteria.UserCriteria
import com.mewebstudio.blogapi.util.Enums
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification
import java.util.*

class UserFilterSpecification(private val criteria: UserCriteria) : Specification<User> {
    override fun toPredicate(root: Root<User>, query: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder): Predicate? {
        val predicates = mutableListOf<Predicate>()

        criteria.roles?.takeIf { it.isNotEmpty() }?.let {
            predicates.add(buildRolePredicate(root, criteriaBuilder, it))
        }

        criteria.genders?.takeIf { it.isNotEmpty() }?.let {
            predicates.add(root.get<Enums.GenderEnum>("gender").`in`(it))
        }

        criteria.createdAtStart?.let {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), it))
        }

        criteria.createdAtEnd?.let {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), it))
        }

        criteria.updatedAtStart?.let {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("updatedAt"), it))
        }

        criteria.updatedAtEnd?.let {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("updatedAt"), it))
        }

        criteria.isBlocked?.let {
            predicates.add(criteriaBuilder.equal(root.get<Boolean>("blockedAt").isNotNull(), it))
        }

        criteria.q?.let { q ->
            val qPattern = "%${q.lowercase(Locale.getDefault())}%"
            predicates.add(buildSearchPredicate(root, criteriaBuilder, qPattern))
        }

        query?.where(*predicates.toTypedArray())?.distinct(true)
        return query?.restriction
    }

    /**
     * Build role predicate
     */
    private fun buildRolePredicate(
        root: Root<User>,
        criteriaBuilder: CriteriaBuilder,
        roles: List<Enums.RoleEnum>
    ): Predicate = run {
        val rolePredicates = roles.map { role ->
            criteriaBuilder.isTrue(
                criteriaBuilder.function(
                    "jsonb_contains", Boolean::class.java,
                    root.get<String>("roles"),
                    criteriaBuilder.literal("[\"$role\"]")
                )
            )
        }

        criteriaBuilder.or(*rolePredicates.toTypedArray())
    }

    /**
     * Build search predicate.
     */
    private fun buildSearchPredicate(
        root: Root<User>,
        criteriaBuilder: CriteriaBuilder,
        pattern: String
    ): Predicate = criteriaBuilder.or(
        criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), pattern),
        criteriaBuilder.like(criteriaBuilder.lower(root.get("firstname")), pattern),
        criteriaBuilder.like(criteriaBuilder.lower(root.get("lastname")), pattern)
    )
}
