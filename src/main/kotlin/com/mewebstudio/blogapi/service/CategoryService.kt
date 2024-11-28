package com.mewebstudio.blogapi.service

import com.mewebstudio.blogapi.dto.request.category.CategoryFilterRequest
import com.mewebstudio.blogapi.dto.request.category.CreateCategoryRequest
import com.mewebstudio.blogapi.dto.request.category.UpdateCategoryRequest
import com.mewebstudio.blogapi.entity.Category
import com.mewebstudio.blogapi.entity.specification.CategoryFilterSpecification
import com.mewebstudio.blogapi.entity.specification.criteria.CategoryCriteria
import com.mewebstudio.blogapi.exception.NotFoundException
import com.mewebstudio.blogapi.repository.CategoryRepository
import com.mewebstudio.blogapi.util.Helpers
import com.mewebstudio.blogapi.util.PageRequestBuilder
import com.mewebstudio.blogapi.util.logger
import org.slf4j.Logger
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.BindException
import org.springframework.validation.FieldError
import java.util.UUID

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository,
    private val userService: UserService,
    private val messageSourceService: MessageSourceService
) {
    private val log: Logger by logger()

    /**
     * Find all categories with pagination.
     *
     * @param request CategoryFilterRequest
     * @return Page<Category>
     */
    fun findAll(request: CategoryFilterRequest): Page<Category> =
        categoryRepository.findAll(
            CategoryFilterSpecification(
                CategoryCriteria(
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
     * Find a category by ID.
     *
     * @param id UUID
     */
    fun findById(id: UUID): Category = categoryRepository.findById(id).orElseThrow {
        NotFoundException(
            messageSourceService.get(
                "not_found_with_param",
                arrayOf(messageSourceService.get("category"))
            )
        )
    }

    /**
     * Find a category by ID or slug.
     *
     * @param idOrSlug String - The ID or slug of the category.
     * @return Category - The found category.
     * @throws NotFoundException - If the category is not found.
     */
    fun findByIdOrSlug(idOrSlug: String): Category = run {
        try {
            findById(UUID.fromString(idOrSlug))
        } catch (e: IllegalArgumentException) {
            log.debug("ID is not a valid UUID, trying to find by slug: ${e.message}")
            categoryRepository.findBySlug(idOrSlug) ?: throw NotFoundException(
                messageSourceService.get(
                    "not_found_with_param",
                    arrayOf(messageSourceService.get("category"))
                )
            )
        }
    }

    /**
     * Create a new category.
     *
     * @param request CreateCategoryRequest - The request object.
     * @return Category - The created category.
     */
    @Throws(BindException::class)
    fun create(request: CreateCategoryRequest): Category = run {
        val bindingResult = BeanPropertyBindingResult(request, "request")
        categoryRepository.existsByTitle(request.title!!, null).takeIf { it }?.let {
            log.error("[Create category] Category already exists with title: ${request.title}")
            bindingResult.addError(
                FieldError(
                    bindingResult.objectName, "title",
                    messageSourceService.get(
                        "already_exists_with_param",
                        arrayOf(messageSourceService.get("category"))
                    )
                )
            )
        }

        if (bindingResult.hasErrors()) {
            throw BindException(bindingResult)
        }

        categoryRepository.save(
            Category(
                title = request.title,
                description = request.description,
                createdUser = userService.getUser()
            ).apply { setSlug(this) }
        ).also { log.info("Category created: $it") }
    }

    /**
     * Update a category.
     *
     * @param id UUID - The ID of the category.
     * @param request UpdateCategoryRequest - The request object.
     * @return Category - The updated category.
     */
    @Throws(BindException::class)
    fun update(id: UUID, request: UpdateCategoryRequest): Category = run {
        val category = categoryRepository.findById(id).orElseThrow {
            NotFoundException(
                messageSourceService.get(
                    "not_found_with_param",
                    arrayOf(messageSourceService.get("category"))
                )
            )
        }

        val bindingResult = BeanPropertyBindingResult(request, "request")
        categoryRepository.existsByTitle(request.title!!, category.id).takeIf { it }?.let {
            log.error("[Update category] Category already exists with title and id: ${request.title}, ${category.id}")
            bindingResult.addError(
                FieldError(
                    bindingResult.objectName, "title",
                    messageSourceService.get(
                        "already_exists_with_param",
                        arrayOf(messageSourceService.get("category"))
                    )
                )
            )
        }

        if (bindingResult.hasErrors()) {
            throw BindException(bindingResult)
        }

        request.title?.takeIf { it.isNotEmpty() }?.let { category.title = it }
        request.description?.takeIf { it.isNotEmpty() }?.let { category.description = it }
        category.apply {
            updatedUser = userService.getUser()
            setSlug(this)
        }

        categoryRepository.save(category).also { log.info("Category updated: $category") }
    }

    /**
     * Update a category.
     *
     * @param id String - The ID of the category.
     * @param request UpdateCategoryRequest - The request object.
     * @return Category - The updated category.
     */
    @Throws(BindException::class)
    fun update(id: String, request: UpdateCategoryRequest): Category = update(UUID.fromString(id), request)

    /**
     * Delete user.
     *
     * @param id UUID
     */
    fun delete(id: UUID) {
        categoryRepository.delete(findById(id)).also { log.info("User deleted: $id") }
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
     * @param category Category
     */
    private fun setSlug(category: Category) {
        val baseSlug = Helpers.generateSlug(category.title)
        val count = categoryRepository.countSlugsStartingWith(baseSlug, category.id)

        category.slug = if (count > 0) {
            "$baseSlug-${count + 1}"
        } else {
            baseSlug
        }
    }
}
