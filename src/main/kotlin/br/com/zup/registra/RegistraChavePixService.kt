package br.com.zup.registra

import br.com.zup.KeymanagerRegistraChavePixGrpcServiceGrpc
import br.com.zup.RegistraChavePixRequest
import br.com.zup.RegistraChavePixResponse
import br.com.zup.client.ItauClient
import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@Singleton
class RegistraChavePixService(
    private val itauClient: ItauClient
) :
    KeymanagerRegistraChavePixGrpcServiceGrpc.KeymanagerRegistraChavePixGrpcServiceImplBase() {

    override fun registrar(
        request: RegistraChavePixRequest,
        responseObserver: StreamObserver<RegistraChavePixResponse>
    ) {
        val chavePixRequest = request.toRequestModel()
        val response =
            itauClient.buscaPorClienteETipoDeConta(chavePixRequest.idCliente, chavePixRequest.tipoDeConta.name)
        responseObserver.onCompleted()
    }
}