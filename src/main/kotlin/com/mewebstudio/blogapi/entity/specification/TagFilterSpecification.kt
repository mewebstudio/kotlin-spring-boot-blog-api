package com.mewebstudio.blogapi.entity.specification

import com.mewebstudio.blogapi.entity.Tag
import com.mewebstudio.blogapi.entity.specification.criteria.TagCriteria
import com.mewebstudio.blogapi.entity.specification.criteria.SpecificationHelper
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification
import java.util.*

class TagFilterSpecification(private val criteria: TagCriteria) : Specification<Tag> {
    override fun toPredicate(
        root: Root<Tag>,
        query: CriteriaQuery<*>?,
        criteriaBuilder: CriteriaBuilder
    ): Predicate? {
        val predicates = mutableListOf<Predicate>()

        SpecificationHelper.addCreatedAndUpdatedUserPredicates(criteria, root, criteriaBuilder, predicates)
        SpecificationHelper.addDateRangePredicates(criteria, root, criteriaBuilder, predicates)

        criteria.q?.let { q ->
            val qPattern = "%${q.lowercase(Locale.getDefault())}%"
            predicates.add(
                SpecificationHelper.addSearchPredicate(
                    root, criteriaBuilder, qPattern, listOf("id", "title", "slug")
                )
            )
        }

        query?.where(*predicates.toTypedArray())?.distinct(true)
        return query?.restriction
    }
}
