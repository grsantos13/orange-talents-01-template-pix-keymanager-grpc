package br.com.zup.pix

import br.com.zup.pix.TipoDeChave.ALEATORIA
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Column
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.EnumType.STRING
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table
import javax.persistence.UniqueConstraint
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(name = "chave_pix_uk", columnNames = ["chave"])
    ]
)
class ChavePix(
    @field:NotNull
    @Column(nullable = false)
    val idCliente: UUID,

    @field:NotBlank
    @field:Size(max = 77)
    @Column(nullable = false, unique = true)
    var chave: String,

    @field:NotNull
    @Enumerated(STRING)
    @Column(nullable = false)
    val tipo: TipoDeChave,

    @field:Valid
    @Embedded
    val conta: Conta
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null

    @field:NotNull
    @Column(nullable = false)
    val registradaEm: LocalDateTime = LocalDateTime.now()

    fun atualizar(novaChave: String) {
        if (tipo == ALEATORIA)
            chave = novaChave
    }

}
