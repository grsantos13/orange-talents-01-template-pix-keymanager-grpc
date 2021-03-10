package br.com.zup.pix.consulta

import br.com.zup.ConsultaChavePixRequest
import br.com.zup.ConsultaChavePixResponse
import br.com.zup.KeymanagerConsultaChavePixGrpcServiceGrpc
import br.com.zup.pix.ChavePix
import br.com.zup.shared.exception.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@Singleton
@ErrorHandler
class ConsultaChavePixEndpoint(
    private val service: ConsultaChavePixService
) : KeymanagerConsultaChavePixGrpcServiceGrpc.KeymanagerConsultaChavePixGrpcServiceImplBase() {

    override fun consultar(
        request: ConsultaChavePixRequest,
        responseObserver: StreamObserver<ConsultaChavePixResponse>
    ) {

        val chavePix = service.consultar(request)

    }
}