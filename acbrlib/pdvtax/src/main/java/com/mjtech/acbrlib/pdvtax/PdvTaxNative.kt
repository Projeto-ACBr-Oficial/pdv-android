package com.mjtech.acbrlib.pdvtax

/**
 * JNI bridge to [native/pdv-tax-cpp] libpdvtax (C++ behavioral port of ACBr PDV tax core).
 */
internal object PdvTaxNative {
    @Volatile
    var loaded: Boolean = false
        private set

    init {
        loaded = try {
            System.loadLibrary("pdvtax")
            true
        } catch (_: UnsatisfiedLinkError) {
            false
        }
    }

    @JvmStatic external fun nativeName(): String
    @JvmStatic external fun nativeVersion(): String
    @JvmStatic external fun nativeValidarCpf(cpf: String): Boolean
    @JvmStatic external fun nativeValidarCnpj(cnpj: String): Boolean
    @JvmStatic external fun nativeValidarUf(uf: String): Boolean
    @JvmStatic external fun nativeGerarChave(
        codigoUf: Int,
        ano: Int,
        mes: Int,
        cnpj: String,
        modelo: Int,
        serie: Int,
        numero: Long,
        tpEmis: Int,
        codigoNumerico: Int
    ): String?

    @JvmStatic external fun nativeCalcularIcms(base: Double, aliquotaPercent: Double): Double
    @JvmStatic external fun nativeCalcularIbpt(
        vProd: Double,
        pNac: Double,
        pImp: Double,
        pEst: Double,
        pMun: Double,
        importado: Boolean
    ): Double
}
