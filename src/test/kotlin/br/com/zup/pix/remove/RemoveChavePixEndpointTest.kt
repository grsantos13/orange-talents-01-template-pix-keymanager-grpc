package br.com.zup.pix.remove

import br.com.zup.KeymanagerRemoveChavePixGrpcServiceGrpc
import br.com.zup.RemoveChavePixRequest
import br.com.zup.client.bcb.BcbClient
import br.com.zup.client.bcb.DeletePixKeyRequest
import br.com.zup.client.bcb.DeletePixKeyResponse
import br.com.zup.data.TestData
import br.com.zup.pix.ChavePixRepository
import br.com.zup.pix.Conta
import br.com.zup.pix.TipoDeChave.CELULAR
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.time.LocalDateTime
import java.util.*

@MicronautTest(transactional = false)
internal class RemoveChavePixEndpointTest(
    val bcbClient: BcbClient,
    val grpcClient: KeymanagerRemoveChavePixGrpcServiceGrpc.KeymanagerRemoveChavePixGrpcServiceBlockingStub,
    val repository: ChavePixRepository
) {

    companion object {
        val ID_CLIENTE = UUID.randomUUID()
        var PIX = TestData.criarChave(ID_CLIENTE, "+5519999999999", CELULAR)
    }

    @BeforeEach
    fun setup() {
        repository.save(PIX)
    }

    @AfterEach
    fun after() {
        repository.deleteAll()
        PIX = TestData.criarChave(ID_CLIENTE, "+5519999999999", CELULAR)
    }

    @Test
    fun `deve remover chave com sucesso`() {

        Mockito.`when`(bcbClient.remover(PIX.chave, DeletePixKeyRequest(PIX.chave)))
            .thenReturn(HttpResponse.ok(DeletePixKeyResponse(PIX.chave, Conta.ISPB, LocalDateTime.now())))

        val response = grpcClient.remover(
            RemoveChavePixRequest.newBuilder()
                .setIdCliente(ID_CLIENTE.toString())
                .setIdPix(PIX.id.toString())
                .build()
        )

        assertEquals(ID_CLIENTE.toString(), response.idCliente)
        assertEquals(PIX.id.toString(), response.idPix)
    }

    @Test
    fun `deve retornar erro ao passar parametros invalidos`() {

        val exception = assertThrows<StatusRuntimeException> {
            grpcClient.remover(
                RemoveChavePixRequest.newBuilder()
                    .setIdCliente("")
                    .setIdPix("")
                    .build()
            )
        }

        assertEquals(Status.INVALID_ARGUMENT.code, exception.status.code)
        assertEquals("Erro de validação dos argumentos", exception.status.description)
    }

    @Test
    fun `erro ao nao encontrar chave pelo cliente ou id pix`() {

        val anyRandomUUID = UUID.randomUUID()

        val exceptionIdCliente = assertThrows<StatusRuntimeException> {
            grpcClient.remover(
                RemoveChavePixRequest.newBuilder()
                    .setIdCliente(anyRandomUUID.toString())
                    .setIdPix(PIX.id.toString())
                    .build()
            )
        }

        val exceptionIdPix = assertThrows<StatusRuntimeException> {
            grpcClient.remover(
                RemoveChavePixRequest.newBuilder()
                    .setIdCliente(ID_CLIENTE.toString())
                    .setIdPix(anyRandomUUID.toString())
                    .build()
            )
        }

        assertEquals(Status.NOT_FOUND.code, exceptionIdPix.status.code)
        assertEquals("Chave de id $anyRandomUUID não encontrada para o cliente $ID_CLIENTE", exceptionIdPix.status.description)
        assertEquals(Status.NOT_FOUND.code, exceptionIdCliente.status.code)
        assertEquals("Chave de id ${PIX.id} não encontrada para o cliente $anyRandomUUID", exceptionIdCliente.status.description)
    }

    @Test
    fun `nao deve remover quando houver erro no bacen`() {

        Mockito.`when`(bcbClient.remover(PIX.chave, DeletePixKeyRequest(PIX.chave)))
            .thenReturn(HttpResponse.serverError())

        val exception = assertThrows<StatusRuntimeException> {
            grpcClient.remover(
                RemoveChavePixRequest.newBuilder()
                    .setIdCliente(ID_CLIENTE.toString())
                    .setIdPix(PIX.id.toString())
                    .build()
            )
        }

        assertEquals(Status.FAILED_PRECONDITION.code, exception.status.code)
        assertEquals("Erro ao remover a chave no BACEN", exception.status.description)
    }

    @MockBean(BcbClient::class)
    fun bcbClient(): BcbClient {
        return Mockito.mock(BcbClient::class.java)
    }

    @Factory
    class Client {
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeymanagerRemoveChavePixGrpcServiceGrpc.KeymanagerRemoveChavePixGrpcServiceBlockingStub? {
            return KeymanagerRemoveChavePixGrpcServiceGrpc.newBlockingStub(channel)
        }
    }
}