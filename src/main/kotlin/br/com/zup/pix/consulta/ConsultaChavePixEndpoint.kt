package br.com.zup.pix.consulta

import br.com.zup.ConsultaChavePixRequest
import br.com.zup.ConsultaChavePixResponse
import br.com.zup.Conta
import br.com.zup.KeymanagerConsultaChavePixGrpcServiceGrpc
import br.com.zup.TipoDeChave
import br.com.zup.TipoDeConta
import br.com.zup.Titular
import br.com.zup.shared.exception.ErrorHandler
import com.google.protobuf.Timestamp
import io.grpc.stub.StreamObserver
import java.time.ZoneId
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
        val response = converterParaResponse(chavePix)

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

    private fun converterParaResponse(
        chavePix: ChavePixResponse,
    ): ConsultaChavePixResponse {

        val instant = chavePix.registradaEm.atZone(ZoneId.of("UTC")).toInstant()
        return ConsultaChavePixResponse.newBuilder()
            .setChave(chavePix.chave)
            .setIdCliente(chavePix.IdCliente.toString())
            .setIdPix(chavePix.IdPix.toString())
            .setTipoDeChave(TipoDeChave.valueOf(chavePix.tipo.name))
            .setConta(
                Conta.newBuilder()
                    .setTipoDeConta(TipoDeConta.valueOf(chavePix.conta.tipoDeConta.name))
                    .setInstituicao(chavePix.conta.instituicao)
                    .setAgencia(chavePix.conta.agencia)
                    .setNumero(chavePix.conta.numeroDaConta)
                    .setTitular(
                        Titular.newBuilder()
                            .setCpf(chavePix.conta.titular.cpf)
                            .setNome(chavePix.conta.titular.nome)
                            .build()
                    )
                    .build()
            )
            .setCriadaEm(
                Timestamp.newBuilder()
                    .setNanos(instant.nano)
                    .setSeconds(instant.epochSecond)
                    .build()
            ).build()
    }
}