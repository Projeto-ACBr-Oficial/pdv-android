#pragma once

#include <cstdint>
#include <string>

namespace pdvtax {

// Port of ACBrDFeUtil.GerarChaveAcesso / FormatarChaveAcesso
// Layout (44 digits + DV handled inside):
// cUF(2) + AAMM(4) + CNPJ(14) + mod(2) + serie(3) + nNF(9) + tpEmis(1) + cNF(8) + cDV(1)

struct ChaveAcessoParams {
  int codigo_uf = 0;           // e.g. 35 SP
  int ano = 0;                 // full year or 2-digit
  int mes = 0;                 // 1-12
  std::string cnpj_cpf;        // 11 or 14 digits (padded to 14)
  int modelo = 65;             // 55 NFe / 65 NFCe
  int serie = 1;
  std::int64_t numero = 1;
  int tp_emis = 1;             // 1=Normal
  int codigo_numerico = 0;     // 0 => derived from numero (GerarCodigoDFe style)
  int site_autorizador = -1;   // -1 => classic 8-digit cNF
};

std::string gerar_chave_acesso(const ChaveAcessoParams& p);
std::string formatar_chave_acesso(const std::string& chave);
bool validar_chave_acesso(const std::string& chave);
int digito_chave_acesso(const std::string& chave43_sem_dv);

// ACBr GerarCodigoDFe-like: deterministic numeric code from nNF when cNF not given
int gerar_codigo_dfe(std::int64_t numero);

}  // namespace pdvtax
