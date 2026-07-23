#pragma once

#ifdef __cplusplus
extern "C" {
#endif

/* C ABI for Android NDK / JNI / FFI — mirrors ACBrLib style return codes:
 * 0 = OK, negative = error
 */

#define PDVTAX_OK 0
#define PDVTAX_ERR_INVALID_ARG -1
#define PDVTAX_ERR_BUFFER -2
#define PDVTAX_ERR_VALIDATION -3

const char* PDVTAX_Nome(void);
const char* PDVTAX_Versao(void);

/* Returns 1 if valid, 0 if invalid */
int PDVTAX_ValidarCPF(const char* cpf);
int PDVTAX_ValidarCNPJ(const char* cnpj);
int PDVTAX_ValidarUF(const char* uf);

/* Writes 44-digit key into out (must be >= 45 bytes). Returns PDVTAX_OK. */
int PDVTAX_GerarChave(int codigo_uf, int ano, int mes, const char* cnpj,
                      int modelo, int serie, long long numero, int tp_emis,
                      int codigo_numerico, char* out, int out_len);

/* Format key with spaces every 4 digits. out >= 55. */
int PDVTAX_FormatarChave(const char* chave, char* out, int out_len);

/* Compute ICMS: base * aliq/100 into *valor_out */
int PDVTAX_CalcularICMS(double base, double aliquota_percent, double* valor_out);

/* IBPT total value given product value and percentages */
int PDVTAX_CalcularIBPT(double v_prod, double p_nac, double p_imp, double p_est,
                        double p_mun, int importado, double* total_out);

#ifdef __cplusplus
}
#endif
