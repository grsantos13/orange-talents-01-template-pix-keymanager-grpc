package br.com.zup.registra

import br.com.zup.KeymanagerRegistraChavePixGrpcServiceGrpc
import br.com.zup.RegistraChavePixRequest
import br.com.zup.RegistraChavePixResponse
import io.grpc.stub.StreamObserver

class RegistraChavePixService :
    KeymanagerRegistraChavePixGrpcServiceGrpc.KeymanagerRegistraChavePixGrpcServiceImplBase() {

    override fun registrar(
        request: RegistraChavePixRequest,
        responseObserver: StreamObserver<RegistraChavePixResponse>
    ) {
        request.toRequestModel()
    }
}