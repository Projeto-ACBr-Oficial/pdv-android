#include <jni.h>

#include <string>

#include "pdvtax/c_api.h"

extern "C" {

JNIEXPORT jstring JNICALL
Java_com_mjtech_acbrlib_pdvtax_PdvTaxNative_nativeName(JNIEnv* env, jclass /*clazz*/) {
    return env->NewStringUTF(PDVTAX_Nome());
}

JNIEXPORT jstring JNICALL
Java_com_mjtech_acbrlib_pdvtax_PdvTaxNative_nativeVersion(JNIEnv* env, jclass /*clazz*/) {
    return env->NewStringUTF(PDVTAX_Versao());
}

JNIEXPORT jboolean JNICALL
Java_com_mjtech_acbrlib_pdvtax_PdvTaxNative_nativeValidarCpf(JNIEnv* env, jclass /*clazz*/,
                                                             jstring cpf) {
    if (cpf == nullptr) {
        return JNI_FALSE;
    }
    const char* chars = env->GetStringUTFChars(cpf, nullptr);
    const int ok = PDVTAX_ValidarCPF(chars);
    env->ReleaseStringUTFChars(cpf, chars);
    return ok ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_com_mjtech_acbrlib_pdvtax_PdvTaxNative_nativeValidarCnpj(JNIEnv* env, jclass /*clazz*/,
                                                              jstring cnpj) {
    if (cnpj == nullptr) {
        return JNI_FALSE;
    }
    const char* chars = env->GetStringUTFChars(cnpj, nullptr);
    const int ok = PDVTAX_ValidarCNPJ(chars);
    env->ReleaseStringUTFChars(cnpj, chars);
    return ok ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_com_mjtech_acbrlib_pdvtax_PdvTaxNative_nativeValidarUf(JNIEnv* env, jclass /*clazz*/,
                                                            jstring uf) {
    if (uf == nullptr) {
        return JNI_FALSE;
    }
    const char* chars = env->GetStringUTFChars(uf, nullptr);
    const int ok = PDVTAX_ValidarUF(chars);
    env->ReleaseStringUTFChars(uf, chars);
    return ok ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jstring JNICALL
Java_com_mjtech_acbrlib_pdvtax_PdvTaxNative_nativeGerarChave(
    JNIEnv* env, jclass /*clazz*/, jint codigoUf, jint ano, jint mes, jstring cnpj, jint modelo,
    jint serie, jlong numero, jint tpEmis, jint codigoNumerico) {
    if (cnpj == nullptr) {
        return nullptr;
    }
    const char* cnpjChars = env->GetStringUTFChars(cnpj, nullptr);
    char out[64];
    const int rc = PDVTAX_GerarChave(codigoUf, ano, mes, cnpjChars, modelo, serie, numero, tpEmis,
                                     codigoNumerico, out, static_cast<int>(sizeof(out)));
    env->ReleaseStringUTFChars(cnpj, cnpjChars);
    if (rc != PDVTAX_OK) {
        return nullptr;
    }
    return env->NewStringUTF(out);
}

JNIEXPORT jdouble JNICALL
Java_com_mjtech_acbrlib_pdvtax_PdvTaxNative_nativeCalcularIcms(JNIEnv* /*env*/, jclass /*clazz*/,
                                                               jdouble base,
                                                               jdouble aliquotaPercent) {
    double valor = 0.0;
    if (PDVTAX_CalcularICMS(base, aliquotaPercent, &valor) != PDVTAX_OK) {
        return 0.0;
    }
    return valor;
}

JNIEXPORT jdouble JNICALL
Java_com_mjtech_acbrlib_pdvtax_PdvTaxNative_nativeCalcularIbpt(
    JNIEnv* /*env*/, jclass /*clazz*/, jdouble vProd, jdouble pNac, jdouble pImp, jdouble pEst,
    jdouble pMun, jboolean importado) {
    double total = 0.0;
    if (PDVTAX_CalcularIBPT(vProd, pNac, pImp, pEst, pMun, importado ? 1 : 0, &total) !=
        PDVTAX_OK) {
        return 0.0;
    }
    return total;
}

}  // extern "C"
