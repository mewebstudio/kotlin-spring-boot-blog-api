package com.mewebstudio.blogapi.entity.specification

import com.mewebstudio.blogapi.entity.Category
import com.mewebstudio.blogapi.entity.specification.criteria.CategoryCriteria
import com.mewebstudio.blogapi.entity.specification.criteria.SpecificationHelper
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification
import java.util.*

class CategoryFilterSpecification(private val criteria: CategoryCriteria) : Specification<Category> {
    override fun toPredicate(
        root: Root<Category>,
        query: CriteriaQuery<*>?,
        criteriaBuilder: CriteriaBuilder
    ): Predicate? {
        val predicates = mutableListOf<Predicate>()

        criteria.users.let { users ->
            if (users.isNotEmpty()) {
                val userPredicate = root.get<UUID>("user").`in`(users)
                predicates.add(userPredicate)
            }
        }

        SpecificationHelper.addDateRangePredicates(criteria, root, criteriaBuilder, predicates)

        criteria.q?.let { q ->
            val qPattern = "%${q.lowercase(Locale.getDefault())}%"
            predicates.add(
                SpecificationHelper.addSearchPredicate(
                    root, criteriaBuilder, qPattern, listOf("id", "title", "slug", "description")
                )
            )
        }

        query?.where(*predicates.toTypedArray())?.distinct(true)
        return query?.restriction
    }
}
