package com.mewebstudio.blogapi.repository

import com.mewebstudio.blogapi.entity.Tag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface TagRepository: JpaRepository<Tag, UUID>, JpaSpecificationExecutor<Tag> {
    fun findBySlug(slug: String): Tag?

    @Query("SELECT COUNT(t) FROM Tag t WHERE t.slug LIKE CONCAT(:prefix, '%') AND (:id IS NULL OR t.id != :id)")
    fun countSlugsStartingWith(@Param("prefix") prefix: String, @Param("id") id: UUID?): Long
}
