package br.com.zup.pix.registra

import br.com.zup.KeymanagerRegistraChavePixGrpcServiceGrpc
import br.com.zup.RegistraChavePixRequest
import br.com.zup.TipoDeChave
import br.com.zup.TipoDeConta
import br.com.zup.client.ContaResponse
import br.com.zup.client.InstituicaoResponse
import br.com.zup.client.TitularResponse
import br.com.zup.client.bcb.BcbClient
import br.com.zup.client.itau.ItauClient
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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.util.*

@MicronautTest(transactional = false)
internal class RegistraChavePixEndpointTest(
    val repository: ChavePixRepository,
    val grpcClient: KeymanagerRegistraChavePixGrpcServiceGrpc.KeymanagerRegistraChavePixGrpcServiceBlockingStub,
    val itauClient: ItauClient,
    val bcbClient: BcbClient
) {

    companion object {
        val ID_CLIENTE = UUID.randomUUID()
    }

    @BeforeEach
    fun setup() {
        repository.deleteAll()
    }

    @Test
    fun `deve criar nova chave pix`() {
        Mockito.`when`(itauClient.buscaContaPorClienteETipoDeConta(ID_CLIENTE.toString(), "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(criarContaResponse()))

        Mockito.`when`(bcbClient.registrar(TestData.criarCreatePixKeyRequest()))
            .thenReturn(HttpResponse.created(TestData.criarCreatePixKeyResponse()))

        val response = grpcClient.registrar(
            RegistraChavePixRequest.newBuilder()
                .setIdCliente(ID_CLIENTE.toString())
                .setTipoDeChave(TipoDeChave.CELULAR)
                .setChave("+5519999999999")
                .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                .build()
        )

        assertEquals(ID_CLIENTE.toString(), response.idCliente)
        assertNotNull(response.idPix)
    }

    @Test
    fun `nao deve criar chave ja existente`() {
        repository.save(TestData.criarChave(ID_CLIENTE, "+5519999999999", CELULAR))

        val exception = assertThrows<StatusRuntimeException> {
            grpcClient.registrar(
                RegistraChavePixRequest.newBuilder()
                    .setIdCliente(ID_CLIENTE.toString())
                    .setTipoDeChave(TipoDeChave.CELULAR)
                    .setChave("+5519999999999")
                    .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                    .build()
            )
        }

        assertEquals(Status.ALREADY_EXISTS.code, exception.status.code)
        assertEquals("A chave +5519999999999 já existe.", exception.status.description)
    }

    @Test
    fun `nao deve criar chave caso ocorra um erro no bacen`() {
        Mockito.`when`(itauClient.buscaContaPorClienteETipoDeConta(ID_CLIENTE.toString(), "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(criarContaResponse()))

        Mockito.`when`(bcbClient.registrar(TestData.criarCreatePixKeyRequest()))
            .thenReturn(HttpResponse.serverError())

        val exception = assertThrows<StatusRuntimeException> {
            grpcClient.registrar(
                RegistraChavePixRequest.newBuilder()
                    .setIdCliente(ID_CLIENTE.toString())
                    .setTipoDeChave(TipoDeChave.CELULAR)
                    .setChave("+5519999999999")
                    .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                    .build()
            )
        }

        assertEquals(Status.FAILED_PRECONDITION.code, exception.status.code)
        assertEquals("Erro ao registrar a chave +5519999999999 no BACEN", exception.status.description)
    }

    @Test
    fun `nao deve criar chave caso ocorra um erro no itau (cliente nao encontrado)`() {
        Mockito.`when`(itauClient.buscaContaPorClienteETipoDeConta(ID_CLIENTE.toString(), "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.notFound())

        val exception = assertThrows<StatusRuntimeException> {
            grpcClient.registrar(
                RegistraChavePixRequest.newBuilder()
                    .setIdCliente(ID_CLIENTE.toString())
                    .setTipoDeChave(TipoDeChave.CELULAR)
                    .setChave("+5519999999999")
                    .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                    .build()
            )
        }

        assertEquals(Status.FAILED_PRECONDITION.code, exception.status.code)
        assertEquals("Cliente não encontrado no sistema do Itaú", exception.status.description)

    }

    @Test
    fun `erro ao passar chave unknown`() {
        Mockito.`when`(itauClient.buscaContaPorClienteETipoDeConta(ID_CLIENTE.toString(), "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.notFound())

        val exception = assertThrows<StatusRuntimeException> {
            grpcClient.registrar(
                RegistraChavePixRequest.newBuilder()
                    .setIdCliente(ID_CLIENTE.toString())
                    .setTipoDeChave(TipoDeChave.UNKNOWN_TIPO_DE_CHAVE)
                    .setChave("+5519999999999")
                    .setTipoDeConta(TipoDeConta.UNKNOWN_TIPO_DE_CONTA)
                    .build()
            )
        }

        assertEquals(Status.INVALID_ARGUMENT.code, exception.status.code)
        assertEquals("Erro de validação dos argumentos", exception.status.description)

    }

    private fun criarContaResponse(): ContaResponse {
        return ContaResponse(
            tipo = "CONTA_CORRENTE",
            instituicao = InstituicaoResponse("ITAÚ UNIBANCO S.A.", Conta.ISPB),
            agencia = "0001",
            numero = "000001",
            titular = TitularResponse(ID_CLIENTE, "Gustavo Santos", "20783911076")
        )
    }

    @MockBean(BcbClient::class)
    fun bcbClient(): BcbClient? {
        return Mockito.mock(BcbClient::class.java)
    }

    @MockBean(ItauClient::class)
    fun itauClient(): ItauClient? {
        return Mockito.mock(ItauClient::class.java)
    }

    @Factory
    class Clients {
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeymanagerRegistraChavePixGrpcServiceGrpc.KeymanagerRegistraChavePixGrpcServiceBlockingStub? {
            return KeymanagerRegistraChavePixGrpcServiceGrpc.newBlockingStub(channel)
        }
    }
}