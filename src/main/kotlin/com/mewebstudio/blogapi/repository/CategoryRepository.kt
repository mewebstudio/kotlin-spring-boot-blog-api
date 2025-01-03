package com.mewebstudio.blogapi.repository

import com.mewebstudio.blogapi.entity.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface CategoryRepository : JpaRepository<Category, UUID>, JpaSpecificationExecutor<Category> {
    fun findBySlug(slug: String): Category?

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END FROM Category c " +
        "WHERE LOWER(c.title) = LOWER(:title) AND (:id IS NULL OR c.id != :id)")
    fun existsByTitle(@Param("title") title: String, @Param("id") id: UUID?): Boolean

    @Query("SELECT COUNT(c) FROM Category c WHERE c.slug LIKE CONCAT(:prefix, '%') AND (:id IS NULL OR c.id != :id)")
    fun countSlugsStartingWith(@Param("prefix") prefix: String, @Param("id") id: UUID?): Long
}
