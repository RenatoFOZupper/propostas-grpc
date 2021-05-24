package br.com.zup.edu.createproposal

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.PositiveOrZero

@Entity
class Proposal(
    @field: NotBlank
    @Column(unique = false)
    val name: String,

    @field: NotBlank
    @Column(unique = false)
    val document: String,

    @field: Email
    @field: NotBlank
    @Column(unique = false)
    val email: String,

    @field: NotBlank
    @Column(unique = false)
    val address: String,

    @field: PositiveOrZero
    @field: NotBlank
    @Column(unique = false)
    val salary: BigDecimal
) {
    @Id
    @GeneratedValue
    val id: UUID? = null

    val createdAt: LocalDateTime = LocalDateTime.now()

}
