package br.com.zup.pix.remove

import br.com.zup.KeymanagerRemoveChavePixGrpcServiceGrpc
import br.com.zup.RemoveChavePixRequest
import br.com.zup.RemoveChavePixResponse
import br.com.zup.shared.exception.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@ErrorHandler
@Singleton
class RemoveChavePixEndpoint(
    private val service: RemoveChavePixService
) : KeymanagerRemoveChavePixGrpcServiceGrpc.KeymanagerRemoveChavePixGrpcServiceImplBase() {

    override fun remover(request: RemoveChavePixRequest, responseObserver: StreamObserver<RemoveChavePixResponse>) {
        service.remover(request.idCliente, request.idPix)

        val pixResponse = RemoveChavePixResponse.newBuilder()
            .setIdCliente(request.idCliente)
            .setIdPix(request.idPix)
            .build()

        responseObserver.onNext(pixResponse)
        responseObserver.onCompleted()
    }
}