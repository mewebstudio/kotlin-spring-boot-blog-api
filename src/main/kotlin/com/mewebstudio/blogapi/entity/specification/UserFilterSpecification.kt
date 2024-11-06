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
        val predicates: MutableList<Predicate> = mutableListOf()

        if (criteria.roles.isNullOrEmpty().not()) {
            val rolePredicates = criteria.roles!!.map { role ->
                criteriaBuilder.isTrue(
                    criteriaBuilder.function(
                        "jsonb_contains", Boolean::class.java,
                        root.get<String>("roles"),
                        criteriaBuilder.literal("[\"$role\"]")
                    )
                )
            }

            if (rolePredicates.isNotEmpty()) {
                predicates.add(criteriaBuilder.or(*rolePredicates.toTypedArray()))
            }
        }

        if (criteria.genders.isNullOrEmpty().not()) {
            val genderPath = root.get<Enums.GenderEnum>("gender")
            val genderPredicate = genderPath.`in`(criteria.genders)
            predicates.add(genderPredicate)
        }

        if (criteria.createdAtStart != null) {
            val createdAtStartPredicate = criteriaBuilder.greaterThanOrEqualTo(
                root.get("createdAt"),
                criteria.createdAtStart
            )
            predicates.add(createdAtStartPredicate)
        }

        if (criteria.createdAtEnd != null) {
            val createdAtEndPredicate = criteriaBuilder.lessThanOrEqualTo(
                root.get("createdAt"),
                criteria.createdAtEnd
            )
            predicates.add(createdAtEndPredicate)
        }

        if (criteria.isBlocked != null) {
            val isBlockedPredicate = criteriaBuilder.equal(
                root.get<Boolean>("blockedAt").isNotNull(),
                criteria.isBlocked
            )
            predicates.add(isBlockedPredicate)
        }

        if (criteria.q != null) {
            val q = "%${criteria.q!!.lowercase(Locale.getDefault())}%"
            val qPredicate = criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), q),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("firstname")), q),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("lastname")), q)
            )
            predicates.add(qPredicate)
        }

        if (predicates.isNotEmpty()) {
            query!!.where(*predicates.toTypedArray())
        }

        return query!!.distinct(true).restriction
    }
}
