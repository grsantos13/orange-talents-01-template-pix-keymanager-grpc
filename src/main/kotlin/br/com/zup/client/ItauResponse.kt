package br.com.zup.client

import java.util.*

data class ContaResponse(
    val tipo: String,
    val instituicao: InstituicaoResponse,
    val agencia: String,
    val numero: String,
    val titular: TitularResponse
)

data class TitularResponse(
    val id: UUID,
    val nome: String,
    val cpf: String
)

data class InstituicaoResponse(
    val nome: String,
    val ispb: String
)
