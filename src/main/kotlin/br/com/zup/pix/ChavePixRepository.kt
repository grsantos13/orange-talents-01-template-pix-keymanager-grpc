package br.com.zup.pix

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChavePixRepository : JpaRepository<ChavePix, UUID> {
    fun existsByChave(chave: String): Boolean
    fun findByIdAndIdCliente(id: UUID, idCliente: UUID): Optional<ChavePix>
    fun findByChave(chave: String): Optional<ChavePix>
    fun findByIdCliente(idCliente: UUID): List<ChavePix>

}