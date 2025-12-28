package org.orm.repository

import org.orm.domain.content.Module
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ModuleRepository : JpaRepository<Module, UUID> {
    fun findByCourse_Id(courseId: UUID): List<Module>
}