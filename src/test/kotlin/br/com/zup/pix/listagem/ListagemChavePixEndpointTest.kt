package br.com.zup.pix.listagem

import br.com.zup.KeymanagerListaChavesPixGrpcServiceGrpc
import br.com.zup.ListaChavesPixRequest
import br.com.zup.ListaChavesPixResponse
import br.com.zup.data.TestData
import br.com.zup.pix.ChavePixRepository
import br.com.zup.pix.TipoDeChave.EMAIL
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

@MicronautTest(transactional = false)
internal class ListagemChavePixEndpointTest(
    val grpcClient: KeymanagerListaChavesPixGrpcServiceGrpc.KeymanagerListaChavesPixGrpcServiceBlockingStub,
    val repository: ChavePixRepository
) {

    companion object {
        val ID_CLIENTE = UUID.randomUUID()
    }

    @BeforeEach
    fun setup() {
        repository.save(TestData.criarChave(ID_CLIENTE, "email@email.com", EMAIL))
    }

    @AfterEach
    fun after() {
        repository.deleteAll()
    }

    @Test
    fun `deve listar as chaves de um cliente com sucesso`() {
        val chavePixList = repository.findByIdCliente(ID_CLIENTE)

        val listaChavesPixResponse = ListaChavesPixResponse.newBuilder()
            .addAllChaves(TestData.mapToChavePixGrpcResponse(chavePixList))
            .setIdCliente(ID_CLIENTE.toString())

        val grpcResponse = grpcClient.listar(
            ListaChavesPixRequest.newBuilder()
                .setIdCliente(ID_CLIENTE.toString())
                .build()
        )

        assertEquals(1, grpcResponse.chavesList.size)
        assertEquals(listaChavesPixResponse.idCliente, grpcResponse.idCliente)
    }

    @Test
    fun `deve retornar array vazio por nao haver dados no banco`() {
        repository.deleteAll()

        val chavePixList = repository.findByIdCliente(ID_CLIENTE)
        val listaChavesPixResponse = ListaChavesPixResponse.newBuilder()
            .addAllChaves(TestData.mapToChavePixGrpcResponse(chavePixList))
            .setIdCliente(ID_CLIENTE.toString())

        val grpcResponse = grpcClient.listar(
            ListaChavesPixRequest.newBuilder()
                .setIdCliente(ID_CLIENTE.toString())
                .build()
        )

        assertEquals(0, grpcResponse.chavesList.size)
        assertEquals(listaChavesPixResponse.idCliente, grpcResponse.idCliente)
    }

    @Test
    fun `deve retornar erro por passar parametro invalido`() {

        val exception = assertThrows<StatusRuntimeException> {
            grpcClient.listar(
                ListaChavesPixRequest.newBuilder()
                    .setIdCliente("")
                    .build()
            )
        }

        assertEquals(Status.INVALID_ARGUMENT.code, exception.status.code)
        assertEquals("O id do cliente não pode ser vazio ou nulo", exception.status.description)
    }

    @Factory
    class Client {
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeymanagerListaChavesPixGrpcServiceGrpc.KeymanagerListaChavesPixGrpcServiceBlockingStub? {
            return KeymanagerListaChavesPixGrpcServiceGrpc.newBlockingStub(channel)
        }
    }
}