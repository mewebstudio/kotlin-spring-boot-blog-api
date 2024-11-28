package com.mewebstudio.blogapi.service

import com.mewebstudio.blogapi.dto.request.tag.TagFilterRequest
import com.mewebstudio.blogapi.dto.request.tag.CreateTagRequest
import com.mewebstudio.blogapi.dto.request.tag.UpdateTagRequest
import com.mewebstudio.blogapi.entity.Tag
import com.mewebstudio.blogapi.entity.specification.TagFilterSpecification
import com.mewebstudio.blogapi.entity.specification.criteria.TagCriteria
import com.mewebstudio.blogapi.exception.NotFoundException
import com.mewebstudio.blogapi.repository.TagRepository
import com.mewebstudio.blogapi.util.Helpers
import com.mewebstudio.blogapi.util.PageRequestBuilder
import com.mewebstudio.blogapi.util.logger
import org.slf4j.Logger
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class TagService(
    private val tagRepository: TagRepository,
    private val userService: UserService,
    private val messageSourceService: MessageSourceService
) {
    private val log: Logger by logger()

    /**
     * Find all categories with pagination.
     *
     * @param request TagFilterRequest
     * @return Page<Tag>
     */
    fun findAll(request: TagFilterRequest): Page<Tag> =
        tagRepository.findAll(
            TagFilterSpecification(
                TagCriteria(
                    createdUsers = request.createdUsers?.map { it },
                    updatedUsers = request.updatedUsers?.map { it },
                    createdAtStart = request.createdAtStart,
                    createdAtEnd = request.createdAtEnd,
                    q = request.q
                )
            ),
            PageRequestBuilder.build(request.page, request.size, request.sortBy, request.sort)
        )

    /**
     * Find a tag by ID.
     *
     * @param id UUID
     */
    fun findById(id: UUID): Tag = tagRepository.findById(id).orElseThrow {
        NotFoundException(
            messageSourceService.get(
                "not_found_with_param",
                arrayOf(messageSourceService.get("tag"))
            )
        )
    }

    /**
     * Find a tag by ID or slug.
     *
     * @param idOrSlug String - The ID or slug of the tag.
     * @return Tag - The found tag.
     * @throws NotFoundException - If the tag is not found.
     */
    fun findByIdOrSlug(idOrSlug: String): Tag = run {
        try {
            findById(UUID.fromString(idOrSlug))
        } catch (e: IllegalArgumentException) {
            log.debug("ID is not a valid UUID, trying to find by slug: ${e.message}")
            tagRepository.findBySlug(idOrSlug) ?: throw NotFoundException(
                messageSourceService.get(
                    "not_found_with_param",
                    arrayOf(messageSourceService.get("tag"))
                )
            )
        }
    }

    /**
     * Create a new tag.
     *
     * @param request CreateTagRequest - The request object.
     * @return Tag - The created tag.
     */
    fun create(request: CreateTagRequest): Tag = tagRepository.save(
        Tag(title = request.title, createdUser = userService.getUser()).apply { setSlug(this) }
    ).also { log.info("Tag created: $it") }

    /**
     * Update a tag.
     *
     * @param id UUID - The ID of the tag.
     * @param request UpdateTagRequest - The request object.
     * @return Tag - The updated tag.
     */
    fun update(id: UUID, request: UpdateTagRequest): Tag = run {
        val tag = tagRepository.findById(id).orElseThrow {
            NotFoundException(
                messageSourceService.get(
                    "not_found_with_param",
                    arrayOf(messageSourceService.get("tag"))
                )
            )
        }

        request.title?.takeIf { it.isNotEmpty() }?.let { tag.title = it }
        tag.apply {
            updatedUser = userService.getUser()
            setSlug(this)
        }

        tagRepository.save(tag).also { log.info("Tag updated: $tag") }
    }

    /**
     * Update a tag.
     *
     * @param id String - The ID of the tag.
     * @param request UpdateTagRequest - The request object.
     * @return Tag - The updated tag.
     */
    fun update(id: String, request: UpdateTagRequest): Tag = update(UUID.fromString(id), request)

    /**
     * Delete user.
     *
     * @param id UUID
     */
    fun delete(id: UUID) {
        tagRepository.delete(findById(id)).also { log.info("User deleted: $id") }
    }

    /**
     * Delete user.
     *
     * @param id String
     */
    fun delete(id: String) {
        delete(UUID.fromString(id))
    }

    /**
     * Set slug
     *
     * @param tag Tag
     */
    private fun setSlug(tag: Tag) {
        val baseSlug = Helpers.generateSlug(tag.title)
        val count = tagRepository.countSlugsStartingWith(baseSlug, tag.id)

        tag.slug = if (count > 0) {
            "$baseSlug-${count + 1}"
        } else {
            baseSlug
        }
    }
}
