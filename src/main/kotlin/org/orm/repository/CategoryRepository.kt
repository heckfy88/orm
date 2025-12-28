package org.orm.repository

import org.orm.domain.course.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CategoryRepository : JpaRepository<Category, UUID> {
    fun findByName(name: String): Category?
    fun existsByName(name: String): Boolean
}