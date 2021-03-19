package br.com.zup.pix

import br.com.zup.data.TestData
import br.com.zup.pix.TipoDeChave.ALEATORIA
import br.com.zup.pix.TipoDeChave.EMAIL
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

internal class ChavePixTest {
    @Test
    fun `deve atualizar quando chave for aleatoria`() {
        val CHAVE_INICIAL = UUID.randomUUID().toString()
        val CHAVE_FINAL = UUID.randomUUID().toString()

        val chave = TestData.criarChave(UUID.randomUUID(), CHAVE_INICIAL, ALEATORIA)
        chave.atualizar(CHAVE_FINAL)
        assertEquals(CHAVE_FINAL, chave.chave)
    }

    @Test
    fun `nao deve atualizar quando chave nao for aleatoria`() {
        val chave = TestData.criarChave(UUID.randomUUID(), "email@email.com", EMAIL)
        chave.atualizar("email2@email.com")
        assertEquals("email@email.com", chave.chave)
    }
}