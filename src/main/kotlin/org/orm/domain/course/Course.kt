package org.orm.domain.course

import jakarta.persistence.*
import org.orm.domain.base.BaseEntity
import org.orm.domain.content.Module
import org.orm.domain.user.User
import java.time.Instant

@Entity
@Table(name = "course", schema = "learning")
class Course(
    var title: String,
    var description: String? = null,

    var duration: Int? = null,

    @Column(name = "start_date")
    var startDate: Instant? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    var category: Category? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    var teacher: User? = null
) : BaseEntity() {

    @OneToMany(
        mappedBy = "course",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    var modules: MutableList<Module> = mutableListOf()

    @ManyToMany
    @JoinTable(
        name = "course_tag",
        schema = "learning",
        joinColumns = [JoinColumn(name = "course_id")],
        inverseJoinColumns = [JoinColumn(name = "tag_id")]
    )
    var tags: MutableSet<Tag> = mutableSetOf()
}