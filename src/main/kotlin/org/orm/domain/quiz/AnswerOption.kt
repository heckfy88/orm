package org.orm.domain.quiz

import jakarta.persistence.*
import org.orm.domain.base.BaseEntity

@Entity
@Table(name = "answer_option", schema = "learning")
class AnswerOption(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false) var question: Question,

    @Column(nullable = false) var text: String,

    @Column(name = "is_correct") var isCorrect: Boolean = false

) : BaseEntity()