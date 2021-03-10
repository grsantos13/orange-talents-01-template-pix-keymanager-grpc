package br.com.zup.client.bcb

import br.com.zup.pix.Conta
import java.time.LocalDateTime

data class DeletePixKeyRequest(
    val key: String,
    val participant: String = Conta.ISPB
)

data class DeletePixKeyResponse(
    val key: String,
    val participant: String,
    val deletedAt: LocalDateTime
)