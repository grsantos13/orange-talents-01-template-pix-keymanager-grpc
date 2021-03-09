package br.com.zup.shared.validation

import br.com.zup.pix.registra.NovaChavePixRequest
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.TYPE
import kotlin.reflect.KClass

@MustBeDocumented
@Target(CLASS, TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = [ChavePixValidaValidator::class])
annotation class ChavePixValida(
    val message: String = "Chave Pix inválida",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = []
)

@Singleton
class ChavePixValidaValidator : ConstraintValidator<ChavePixValida, NovaChavePixRequest> {

    override fun isValid(
        value: NovaChavePixRequest?,
        annotationMetadata: AnnotationValue<ChavePixValida>,
        context: ConstraintValidatorContext
    ): Boolean {
        if (value?.tipo == null) return false
        return value.tipo.validar(value.chave)
    }
}
