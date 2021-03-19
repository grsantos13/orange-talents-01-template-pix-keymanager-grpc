package br.com.zup.pix

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class TipoDeChaveTest {

    @Nested
    inner class CPF {

        @Test
        fun `deve retornar true com cpf valido`() {
            assertTrue(TipoDeChave.CPF.validar("20783911076"))
        }

        @Test
        fun `deve retornar false quando o valor passado for nulo ou branco`() {
            with(TipoDeChave.CPF) {
                assertFalse(validar(""))
                assertFalse(validar(null))
            }
        }

        @Test
        fun `deve retornar false quando o valor passado possuirem pontos e virgulas`() {
            assertFalse(TipoDeChave.CPF.validar("207.839.110-76"))
        }

        @Test
        fun `deve retornar false quando o cpf for invalido`() {
            assertFalse(TipoDeChave.CPF.validar("207.839.110-75"))
        }
    }

    @Nested
    inner class CELULAR {

        @Test
        fun `deve retornar true para celular valido`(){
            assertTrue(TipoDeChave.CELULAR.validar("+5519999999999"))
        }

        @Test
        fun `deve retornar false para celular em nulo ou em branco`(){
            with(TipoDeChave.CELULAR){
                assertFalse(validar(""))
                assertFalse(validar(null))
            }
        }

        @Test
        fun `deve retornar true para celular invalido`(){
            with(TipoDeChave.CELULAR){
                assertFalse(validar("19999999999"))
                assertFalse(validar("+5519999999999999999"))
            }
        }
    }

    @Nested
    inner class EMAIL {

        @Test
        fun `deve retornar true para email valido`(){
            assertTrue(TipoDeChave.EMAIL.validar("email@email.com"))
        }

        @Test
        fun `deve retornar false para email em nulo ou em branco`(){
            with(TipoDeChave.EMAIL){
                assertFalse(validar(""))
                assertFalse(validar(null))
            }
        }

        @Test
        fun `deve retornar true para email invalido`(){
            with(TipoDeChave.CELULAR){
                assertFalse(validar("email"))
                assertFalse(validar("@email"))
                assertFalse(validar("email@email"))
            }
        }
    }

    @Nested
    inner class ALEATORIA {

        @Test
        fun `deve retornar true para chaves nulas ou vazias`() {
            with(TipoDeChave.ALEATORIA) {
                assertTrue(validar(""))
                assertTrue(validar(null))
            }
        }

        @Test
        fun `deve retornar false para chaves com texto`() {
            assertFalse(TipoDeChave.ALEATORIA.validar("test"))
        }

    }
}