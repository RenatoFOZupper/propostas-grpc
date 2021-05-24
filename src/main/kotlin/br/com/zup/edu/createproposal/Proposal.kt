package br.com.zup.edu.createproposal

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Proposal(
    val name: String,
    val document: String,
    val email: String,
    val address: String,
    val salary: BigDecimal
) {
    @Id
    @GeneratedValue
    val id: UUID? = null

    val createdAt: LocalDateTime = LocalDateTime.now()

}
