package com.mjtech.acbrlib.pdvtax

/**
 * High-level Kotlin API for the C++ PDV tax engine.
 *
 * When the native library is not packaged (unit tests on JVM without NDK), methods throw
 * [IllegalStateException] so callers can fall back to ACBrLib AAR flows.
 */
class PdvTaxEngine {

    val isNativeAvailable: Boolean
        get() = PdvTaxNative.loaded

    fun name(): String {
        ensureNative()
        return PdvTaxNative.nativeName()
    }

    fun version(): String {
        ensureNative()
        return PdvTaxNative.nativeVersion()
    }

    fun validarCpf(cpf: String): Boolean {
        ensureNative()
        return PdvTaxNative.nativeValidarCpf(cpf)
    }

    fun validarCnpj(cnpj: String): Boolean {
        ensureNative()
        return PdvTaxNative.nativeValidarCnpj(cnpj)
    }

    fun validarUf(uf: String): Boolean {
        ensureNative()
        return PdvTaxNative.nativeValidarUf(uf)
    }

    /**
     * @param modelo 55 = NF-e, 65 = NFC-e
     */
    fun gerarChaveAcesso(
        codigoUf: Int,
        ano: Int,
        mes: Int,
        cnpj: String,
        modelo: Int = 65,
        serie: Int = 1,
        numero: Long,
        tpEmis: Int = 1,
        codigoNumerico: Int = 0
    ): String {
        ensureNative()
        return PdvTaxNative.nativeGerarChave(
            codigoUf, ano, mes, cnpj, modelo, serie, numero, tpEmis, codigoNumerico
        ) ?: error("Failed to generate access key")
    }

    fun calcularIcms(base: Double, aliquotaPercent: Double): Double {
        ensureNative()
        return PdvTaxNative.nativeCalcularIcms(base, aliquotaPercent)
    }

    fun calcularIbpt(
        vProd: Double,
        pNacional: Double,
        pImportado: Double = 0.0,
        pEstadual: Double,
        pMunicipal: Double = 0.0,
        importado: Boolean = false
    ): Double {
        ensureNative()
        return PdvTaxNative.nativeCalcularIbpt(
            vProd, pNacional, pImportado, pEstadual, pMunicipal, importado
        )
    }

    private fun ensureNative() {
        check(PdvTaxNative.loaded) {
            "libpdvtax not loaded. Build with Android NDK or use host CMake under native/pdv-tax-cpp."
        }
    }
}
