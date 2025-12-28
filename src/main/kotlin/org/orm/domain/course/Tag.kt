package org.orm.domain.course

import jakarta.persistence.*
import org.orm.domain.base.BaseEntity

@Entity
@Table(name = "tag", schema = "learning")
class Tag(

    @Column(nullable = false, unique = true) var name: String

) : BaseEntity() {

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    var courses: MutableSet<Course> = mutableSetOf()
}