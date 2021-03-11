package br.com.zup.pix

import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.Embedded
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Embeddable
class Conta(
    @field:NotBlank
    @Column(nullable = false)
    val instituicao: String,

    @field:NotBlank
    @field:Size(max = 4)
    @Column(nullable = false, length = 4)
    val agencia: String,

    @field:NotBlank
    @field:Size(max = 6)
    @Column(nullable = false, length = 6)
    val numeroDaConta: String,

    @Embedded
    @field:Valid
    @field:NotNull
    val titular: Titular,

    @field:NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val tipoDeConta: TipoDeConta
) {
    companion object {
        const val ISPB = "60701190"
    }

    override fun toString(): String {
        return "Conta(instituicao='$instituicao', agencia='$agencia', numeroDaConta='$numeroDaConta', titular=$titular, tipoDeConta=$tipoDeConta)"
    }
}