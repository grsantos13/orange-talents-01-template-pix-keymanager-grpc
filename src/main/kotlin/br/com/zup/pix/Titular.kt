package br.com.zup.pix

import javax.persistence.Embeddable

@Embeddable
class Titular(
    val nome: String,
    val cpf: String
)