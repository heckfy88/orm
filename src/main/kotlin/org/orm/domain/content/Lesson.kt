package org.orm.domain.content

import jakarta.persistence.*

import org.orm.domain.base.BaseEntity

@Entity
@Table(name = "lesson", schema = "learning")
class Lesson(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false) var module: Module,

    @Column(nullable = false) var title: String,

    var content: String? = null,
    var videoUrl: String? = null

) : BaseEntity() {

    @OneToMany(mappedBy = "lesson", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var assignments: MutableList<Assignment> = mutableListOf()
}
