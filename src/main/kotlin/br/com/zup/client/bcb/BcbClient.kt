package br.com.zup.client.bcb

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client

@Client("\${bcb.url}")
interface BcbClient {
    @Post(
        "/api/v1/pix/keys",
        consumes = [MediaType.APPLICATION_XML],
        produces = [MediaType.APPLICATION_XML]
    )
    fun registrar(@Body request: CreatePixKeyRequest): HttpResponse<CreatePixKeyResponse>
}

