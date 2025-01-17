package br.com.zup.shared.validation

import javax.validation.Constraint
import javax.validation.Payload
import javax.validation.ReportAsSingleViolation
import javax.validation.constraints.Pattern
import javax.validation.constraints.Pattern.Flag.CASE_INSENSITIVE
import kotlin.reflect.KClass

@ReportAsSingleViolation
@Constraint(validatedBy = [])
@Pattern(
    regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$",
    flags = [CASE_INSENSITIVE]
)
annotation class UUIDValido(
    val message: String = "O formato do id não é válido.",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = []
)
