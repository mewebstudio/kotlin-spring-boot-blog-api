package com.mewebstudio.blogapi.entity.specification.criteria

data class PaginationCriteria(
    var page: Int? = null,
    var size: Int? = null,
    var sortBy: String? = null,
    var sort: String? = null,
    var columns: Array<String>? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        other as PaginationCriteria
        return page == other.page &&
            size == other.size &&
            sortBy == other.sortBy &&
            sort == other.sort &&
            columns?.contentEquals(other.columns) == true
    }

    override fun hashCode(): Int {
        return listOf(page, size, sortBy, sort, columns?.contentHashCode()).hashCode()
    }
}
