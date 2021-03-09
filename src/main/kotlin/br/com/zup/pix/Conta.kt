package br.com.zup.pix

import javax.persistence.Embeddable
import javax.persistence.Embedded

@Embeddable
class Conta(
    val instituicao: String,
    val agencia: String,
    val numeroDaConta: String,
    @Embedded
    val titular: Titular
)