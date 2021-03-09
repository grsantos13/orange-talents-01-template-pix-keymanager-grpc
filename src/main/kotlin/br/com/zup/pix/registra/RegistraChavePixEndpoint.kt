package br.com.zup.pix.registra

import br.com.zup.KeymanagerRegistraChavePixGrpcServiceGrpc
import br.com.zup.RegistraChavePixRequest
import br.com.zup.RegistraChavePixResponse
import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@Singleton
class RegistraChavePixEndpoint(
    private val service: RegistraChavePixService
) :
    KeymanagerRegistraChavePixGrpcServiceGrpc.KeymanagerRegistraChavePixGrpcServiceImplBase() {

    override fun registrar(
        request: RegistraChavePixRequest,
        responseObserver: StreamObserver<RegistraChavePixResponse>
    ) {
        val chavePixRequest = request.toRequestModel()
        val chavePix = service.registrar(chavePixRequest)
        val pixResponse = RegistraChavePixResponse.newBuilder()
            .setIdCliente(chavePix.idCliente.toString())
            .setIdPix(chavePix.id.toString())
            .build()
        responseObserver.onNext(pixResponse)
        responseObserver.onCompleted()
    }
}