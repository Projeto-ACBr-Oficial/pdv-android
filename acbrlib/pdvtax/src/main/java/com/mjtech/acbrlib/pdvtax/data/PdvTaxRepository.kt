package com.mjtech.acbrlib.pdvtax.data

import com.mjtech.acbrlib.pdvtax.PdvTaxEngine

/**
 * Repository façade used by the PDV app layer for fiscal helpers implemented in C++.
 */
class PdvTaxRepository(
    private val engine: PdvTaxEngine = PdvTaxEngine()
) {
    fun isAvailable(): Boolean = engine.isNativeAvailable

    fun engineInfo(): String? =
        if (engine.isNativeAvailable) "${engine.name()} v${engine.version()}" else null

    fun validarDocumentoEmitente(cnpj: String): Boolean = engine.validarCnpj(cnpj)

    fun validarCpfConsumidor(cpf: String): Boolean = engine.validarCpf(cpf)

    fun gerarChaveNfce(
        codigoUf: Int,
        ano: Int,
        mes: Int,
        cnpj: String,
        serie: Int,
        numero: Long,
        cNf: Int = 0
    ): String = engine.gerarChaveAcesso(
        codigoUf = codigoUf,
        ano = ano,
        mes = mes,
        cnpj = cnpj,
        modelo = 65,
        serie = serie,
        numero = numero,
        codigoNumerico = cNf
    )
}
