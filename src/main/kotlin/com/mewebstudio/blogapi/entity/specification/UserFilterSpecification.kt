package com.mewebstudio.blogapi.entity.specification

import com.mewebstudio.blogapi.entity.User
import com.mewebstudio.blogapi.entity.specification.criteria.SpecificationHelper
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

        SpecificationHelper.addCreatedAndUpdatedUserPredicates(criteria, root, criteriaBuilder, predicates)
        SpecificationHelper.addDateRangePredicates(criteria, root, criteriaBuilder, predicates)

        criteria.roles?.takeIf { it.isNotEmpty() }?.let {
            predicates.add(buildRolePredicate(root, criteriaBuilder, it))
        }

        criteria.genders?.takeIf { it.isNotEmpty() }?.let {
            predicates.add(root.get<Enums.GenderEnum>("gender").`in`(it))
        }

        criteria.isBlocked?.let {
            predicates.add(criteriaBuilder.equal(root.get<Boolean>("blockedAt").isNotNull(), it))
        }

        criteria.q?.let { q ->
            val qPattern = "%${q.lowercase(Locale.getDefault())}%"
            predicates.add(
                SpecificationHelper.addSearchPredicate(
                    root, criteriaBuilder, qPattern, listOf("id", "email", "firstname", "lastname")
                )
            )
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
    ): Predicate = criteriaBuilder.or(*roles.map {
        criteriaBuilder.isTrue(
            criteriaBuilder.function(
                "jsonb_contains", Boolean::class.java,
                root.get<String>("roles"),
                criteriaBuilder.literal("[\"$it\"]")
            )
        )
    }.toTypedArray())
}
