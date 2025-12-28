package org.orm.domain.quiz

import jakarta.persistence.*
import org.orm.domain.base.BaseEntity
import org.orm.domain.content.Module

@Entity
@Table(name = "quiz", schema = "learning")
class Quiz(

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id") var module: Module? = null,

    @Column(nullable = false) var title: String,

    var timeLimit: Int? = null

) : BaseEntity() {

    @OneToMany(mappedBy = "quiz", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var questions: MutableList<Question> = mutableListOf()
}