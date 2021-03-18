package br.com.zup.pix.consulta

import br.com.zup.ConsultaChavePixRequest
import br.com.zup.ConsultaChavePixRequest.ConsultaPorClienteEIdPix
import br.com.zup.KeymanagerConsultaChavePixGrpcServiceGrpc
import br.com.zup.client.bcb.AccountType
import br.com.zup.client.bcb.BankAccount
import br.com.zup.client.bcb.BcbClient
import br.com.zup.client.bcb.KeyType
import br.com.zup.client.bcb.Owner
import br.com.zup.client.bcb.OwnerType
import br.com.zup.client.bcb.PixKeyDetailsResponse
import br.com.zup.pix.ChavePix
import br.com.zup.pix.ChavePixRepository
import br.com.zup.pix.Conta
import br.com.zup.pix.TipoDeChave
import br.com.zup.pix.TipoDeChave.EMAIL
import br.com.zup.pix.TipoDeConta.CONTA_CORRENTE
import br.com.zup.pix.Titular
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
internal class ConsultaChavePixEndpointTest(
    val repository: ChavePixRepository,
    val grpcClient: KeymanagerConsultaChavePixGrpcServiceGrpc.KeymanagerConsultaChavePixGrpcServiceBlockingStub,
    val bcbClient: BcbClient
) {

    companion object {
        val ID_CLIENTE = UUID.randomUUID()
    }

    @BeforeEach
    fun setup() {
        repository.save(criarChave(ID_CLIENTE, "gsantos@email.com", EMAIL))
    }

    @AfterEach
    fun after() {
        repository.deleteAll()
    }

    @Test
    fun `consulta chave pelo id do pix e do cliente`() {
        val chavePix = repository.findByChave("gsantos@email.com")
            .get()

        val grpcResponse = grpcClient.consultar(
            ConsultaChavePixRequest.newBuilder()
                .setIdPixEIdCliente(
                    ConsultaPorClienteEIdPix.newBuilder()
                        .setIdPix(chavePix.id.toString())
                        .setIdCliente(chavePix.idCliente.toString())
                        .build()
                ).build()
        )

        assertEquals(chavePix.idCliente.toString(), grpcResponse.idCliente)
        assertEquals(chavePix.tipo.name, grpcResponse.tipoDeChave.name)
        assertEquals(chavePix.chave, grpcResponse.chave)
        assertEquals(chavePix.id.toString(), grpcResponse.idPix)
    }

    @Test
    fun `nao deve carregar por idPix e idCliente quando chave nao existir`() {
        val idCliente = UUID.randomUUID().toString()
        val idPix = UUID.randomUUID().toString()

        val exception = assertThrows<StatusRuntimeException> {
            grpcClient.consultar(
                ConsultaChavePixRequest.newBuilder()
                    .setIdPixEIdCliente(
                        ConsultaPorClienteEIdPix.newBuilder()
                            .setIdPix(idPix)
                            .setIdCliente(idCliente)
                            .build()
                    ).build()
            )
        }

        assertEquals(Status.NOT_FOUND.code, exception.status.code)
        assertEquals(
            "Não existe chave de propriedade do cliente $idCliente com id $idPix",
            exception.status.description
        )
    }

    @Test
    fun `consulta chave pelo valor da chave localmente`() {
        val chavePix = repository.findByChave("gsantos@email.com")
            .get()

        val grpcResponse = grpcClient.consultar(
            ConsultaChavePixRequest.newBuilder()
                .setChave("gsantos@email.com")
                .build()
        )

        assertEquals(chavePix.idCliente.toString(), grpcResponse.idCliente)
        assertEquals(chavePix.tipo.name, grpcResponse.tipoDeChave.name)
        assertEquals(chavePix.chave, grpcResponse.chave)
        assertEquals(chavePix.id.toString(), grpcResponse.idPix)
    }

    @Test
    fun `consulta chave pelo valor da chave no bcb (nao existe localmente)`() {
        val clientResponse = criarPixKeyDetailsResponse()
        Mockito.`when`(bcbClient.consultar(clientResponse.key))
            .thenReturn(HttpResponse.ok(clientResponse))

        val grpcResponse = grpcClient.consultar(
            ConsultaChavePixRequest.newBuilder()
                .setChave(clientResponse.key)
                .build()
        )

        assertEquals("null", grpcResponse.idCliente)
        assertEquals(clientResponse.keyType.name, grpcResponse.tipoDeChave.name)
        assertEquals(clientResponse.key, grpcResponse.chave)
        assertEquals("null", grpcResponse.idPix)
    }

    @Test
    fun `nao deve retornar com sucesso a consulta por chave quando nao houver chave localmente ou no bacen`() {
        Mockito.`when`(bcbClient.consultar(key = "email@chaveinexistente.com"))
            .thenReturn(HttpResponse.notFound())

        val exception = assertThrows<StatusRuntimeException> {
            grpcClient.consultar(
                ConsultaChavePixRequest.newBuilder()
                    .setChave("email@chaveinexistente.com")
                    .build()
            )
        }

        assertEquals(Status.NOT_FOUND.code, exception.status.code)
        assertEquals("Chave não encontrada com o valor email@chaveinexistente.com", exception.status.description)
    }

    @Test
    fun `nao deve carregar chave por valor da chave ou por idPix e idCliente quando filtro invalido`() {
        val exceptionPorIdClienteEIdPix = assertThrows<StatusRuntimeException> {
            grpcClient.consultar(
                ConsultaChavePixRequest.newBuilder()
                    .setIdPixEIdCliente(
                        ConsultaPorClienteEIdPix.newBuilder()
                            .setIdPix("")
                            .setIdCliente("")
                            .build()
                    ).build()
            )
        }

        val exceptionPorChave = assertThrows<StatusRuntimeException> {
            grpcClient.consultar(ConsultaChavePixRequest.newBuilder().setChave("").build())
        }

        assertEquals(Status.INVALID_ARGUMENT.code, exceptionPorChave.status.code)
        assertEquals("Erro de validação dos argumentos", exceptionPorChave.status.description)
        assertEquals(Status.INVALID_ARGUMENT.code, exceptionPorIdClienteEIdPix.status.code)
        assertEquals("Erro de validação dos argumentos", exceptionPorIdClienteEIdPix.status.description)
    }

    private fun criarChave(idCliente: UUID, chave: String?, tipo: TipoDeChave): ChavePix {
        return ChavePix(
            idCliente = idCliente,
            chave = chave ?: UUID.randomUUID().toString(),
            tipo = tipo,
            conta = Conta(
                instituicao = "ITAÚ UNIBANCO S.A.",
                titular = Titular(nome = "Gustavo Santos", cpf = "12332112233"),
                agencia = "1234",
                numeroDaConta = "987657",
                tipoDeConta = CONTA_CORRENTE
            )
        )
    }

    private fun criarPixKeyDetailsResponse(): PixKeyDetailsResponse {
        return PixKeyDetailsResponse(
            keyType = KeyType.EMAIL,
            key = "email@email.com",
            bankAccount = BankAccount(
                participant = "60746948",
                branch = "0001",
                accountNumber = "000001",
                accountType = AccountType.CACC
            ),
            owner = Owner(
                type = OwnerType.NATURAL_PERSON,
                name = "Usuário anônimo",
                taxIdNumber = "13579808642"
            ),
            createdAt = LocalDateTime.now()
        )
    }

    @MockBean(BcbClient::class)
    fun bcbClient(): BcbClient? {
        return Mockito.mock(BcbClient::class.java)
    }

    @Factory
    class Client {
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeymanagerConsultaChavePixGrpcServiceGrpc.KeymanagerConsultaChavePixGrpcServiceBlockingStub? {
            return KeymanagerConsultaChavePixGrpcServiceGrpc.newBlockingStub(channel)
        }
    }
}