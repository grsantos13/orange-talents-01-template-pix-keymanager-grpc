package br.com.zup.pix.listagem

import br.com.zup.KeymanagerListaChavesPixGrpcServiceGrpc
import br.com.zup.ListaChavesPixRequest
import br.com.zup.ListaChavesPixResponse
import br.com.zup.TipoDeChave
import br.com.zup.TipoDeConta
import br.com.zup.pix.ChavePix
import br.com.zup.pix.ChavePixRepository
import br.com.zup.shared.exception.ErrorHandler
import com.google.protobuf.Timestamp
import io.grpc.stub.StreamObserver
import java.time.ZoneId
import java.util.*
import javax.inject.Singleton

@ErrorHandler
@Singleton
class ListagemChavePixEndpoint(
    private val repository: ChavePixRepository
) : KeymanagerListaChavesPixGrpcServiceGrpc.KeymanagerListaChavesPixGrpcServiceImplBase() {

    override fun listar(
        request: ListaChavesPixRequest,
        responseObserver: StreamObserver<ListaChavesPixResponse>
    ) {
        if (request.idCliente.isNullOrBlank())
            throw IllegalArgumentException("O id do cliente não pode ser vazio ou nulo")

        val chavePixList = repository.findByIdCliente(UUID.fromString(request.idCliente))
        val pixGrpcResponse = mapToChavePixGrpcResponse(chavePixList)

        responseObserver.onNext(ListaChavesPixResponse.newBuilder()
            .setIdCliente(request.idCliente)
            .addAllChaves(pixGrpcResponse)
            .build())
        responseObserver.onCompleted()
    }

    private fun mapToChavePixGrpcResponse(chaves: List<ChavePix>): List<ListaChavesPixResponse.ChavePix> {
        return chaves.map {
            val registradaEm = it.registradaEm.atZone(ZoneId.of("UTC")).toInstant()
            ListaChavesPixResponse.ChavePix.newBuilder()
                .setChave(it.chave)
                .setIdPix(it.id.toString())
                .setTipoDaChave(TipoDeChave.valueOf(it.tipo.name))
                .setTipoDaConta(TipoDeConta.valueOf(it.conta.tipoDeConta.name))
                .setRegistradaEm(
                    Timestamp.newBuilder()
                        .setSeconds(registradaEm.epochSecond)
                        .setNanos(registradaEm.nano)
                        .build()
                )
                .build()
        }
    }
}