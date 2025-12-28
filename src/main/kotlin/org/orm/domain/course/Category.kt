package org.orm.domain.course

import jakarta.persistence.*
import org.orm.domain.base.BaseEntity

@Entity
@Table(name = "category", schema = "learning")
class Category(

    @Column(nullable = false, unique = true) var name: String

) : BaseEntity() {

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    var courses: MutableList<Course> = mutableListOf()
}