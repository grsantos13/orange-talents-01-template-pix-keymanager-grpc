package br.com.zup.shared.exception.handlers

import br.com.zup.shared.exception.ExceptionHandler
import br.com.zup.shared.exception.ExceptionHandler.StatusWrapper
import com.google.protobuf.Any
import com.google.rpc.BadRequest
import com.google.rpc.Code
import com.google.rpc.Status
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
class ConstraintViolationExceptionHandler : ExceptionHandler<ConstraintViolationException> {

    override fun handle(e: ConstraintViolationException): StatusWrapper {
        val details = BadRequest.newBuilder()
            .addAllFieldViolations(e.constraintViolations.map {
                BadRequest.FieldViolation.newBuilder()
                    .setField(it.propertyPath.last().name ?: "")
                    .setDescription(it.message)
                    .build()
            }).build()

        val status = Status.newBuilder()
            .setCode(Code.INVALID_ARGUMENT_VALUE)
            .setMessage("Erro de validação dos argumentos")
            .addDetails(Any.pack(details))
            .build()

        return StatusWrapper(status)
    }

    override fun supports(e: Exception): Boolean {
        return e is ConstraintViolationException
    }
}