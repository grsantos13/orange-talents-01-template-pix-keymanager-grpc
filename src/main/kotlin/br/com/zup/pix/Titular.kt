package br.com.zup.pix

import javax.persistence.Column
import javax.persistence.Embeddable
import javax.validation.constraints.NotBlank

@Embeddable
class Titular(
    @field:NotBlank
    @Column(nullable = false)
    val nome: String,

    @field:NotBlank
    @Column(nullable = false, length = 11)
    val cpf: String
)