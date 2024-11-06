package com.mewebstudio.blogapi.entity.specification

import com.mewebstudio.blogapi.entity.User
import com.mewebstudio.blogapi.entity.specification.criteria.UserCriteria
import com.mewebstudio.blogapi.util.logger
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.slf4j.Logger
import org.springframework.data.jpa.domain.Specification

class UserFilterSpecification(private val criteria: UserCriteria) : Specification<User> {
    private val log: Logger by logger()

    override fun toPredicate(root: Root<User>, query: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder): Predicate? {
        val predicates: List<Predicate> = ArrayList()

        if (predicates.isNotEmpty()) {
            query!!.where(*predicates.toTypedArray<Predicate>())
        }

        if (criteria.roles.isNullOrEmpty().not()) {
            log.info("Filtering by roles: ${criteria.roles}")
        }

        return query!!.distinct(true).restriction
    }
}
