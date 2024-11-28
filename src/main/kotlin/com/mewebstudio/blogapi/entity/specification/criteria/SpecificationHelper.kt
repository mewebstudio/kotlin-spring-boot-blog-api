package com.mewebstudio.blogapi.entity.specification.criteria

import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root

object SpecificationHelper {
    /**
     * Add date range predicates for createdAt and updatedAt
     */
    fun addDateRangePredicates(
        criteria: ICriteria,
        root: Root<*>,
        criteriaBuilder: CriteriaBuilder,
        predicates: MutableList<Predicate>
    ) {
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
    }

    /**
     * Add search predicate for text-based search
     */
    fun addSearchPredicate(
        root: Root<*>,
        criteriaBuilder: CriteriaBuilder,
        pattern: String,
        fields: List<String>
    ): Predicate {
        return criteriaBuilder.or(*fields.map { field ->
            criteriaBuilder.like(criteriaBuilder.lower(criteriaBuilder.toString(root.get(field))), pattern)
        }.toTypedArray())
    }
}
