package br.com.zup.edu.createproposal

import br.com.zup.edu.shared.CpfOrCnpj
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.PositiveOrZero

@Entity
class Proposal(
    @field: NotBlank
    @Column(unique = false)
    val name: String,

    @CpfOrCnpj
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
    @field: NotNull
    @Column(unique = false)
    val salary: BigDecimal
) {
    @Id
    @GeneratedValue
    val id: UUID? = null

    @Column(unique = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()

}
