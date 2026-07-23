#include "pdvtax/c_api.h"

#include "pdvtax/chave_acesso.hpp"
#include "pdvtax/impostos.hpp"
#include "pdvtax/pdvtax.hpp"
#include "pdvtax/validador.hpp"

#include <cstring>

extern "C" {

const char* PDVTAX_Nome(void) { return pdvtax::name(); }

const char* PDVTAX_Versao(void) { return pdvtax::version(); }

int PDVTAX_ValidarCPF(const char* cpf) {
  if (cpf == nullptr) {
    return 0;
  }
  return pdvtax::is_cpf_valido(cpf) ? 1 : 0;
}

int PDVTAX_ValidarCNPJ(const char* cnpj) {
  if (cnpj == nullptr) {
    return 0;
  }
  return pdvtax::is_cnpj_valido(cnpj) ? 1 : 0;
}

int PDVTAX_ValidarUF(const char* uf) {
  if (uf == nullptr) {
    return 0;
  }
  return pdvtax::validar_uf(uf).empty() ? 1 : 0;
}

int PDVTAX_GerarChave(int codigo_uf, int ano, int mes, const char* cnpj,
                      int modelo, int serie, long long numero, int tp_emis,
                      int codigo_numerico, char* out, int out_len) {
  if (cnpj == nullptr || out == nullptr) {
    return PDVTAX_ERR_INVALID_ARG;
  }
  if (out_len < 45) {
    return PDVTAX_ERR_BUFFER;
  }
  pdvtax::ChaveAcessoParams p;
  p.codigo_uf = codigo_uf;
  p.ano = ano;
  p.mes = mes;
  p.cnpj_cpf = cnpj;
  p.modelo = modelo;
  p.serie = serie;
  p.numero = numero;
  p.tp_emis = tp_emis;
  p.codigo_numerico = codigo_numerico;
  const std::string chave = pdvtax::gerar_chave_acesso(p);
  if (static_cast<int>(chave.size()) + 1 > out_len) {
    return PDVTAX_ERR_BUFFER;
  }
  std::memcpy(out, chave.c_str(), chave.size() + 1);
  return PDVTAX_OK;
}

int PDVTAX_FormatarChave(const char* chave, char* out, int out_len) {
  if (chave == nullptr || out == nullptr || out_len < 1) {
    return PDVTAX_ERR_INVALID_ARG;
  }
  const std::string f = pdvtax::formatar_chave_acesso(chave);
  if (static_cast<int>(f.size()) + 1 > out_len) {
    return PDVTAX_ERR_BUFFER;
  }
  std::memcpy(out, f.c_str(), f.size() + 1);
  return PDVTAX_OK;
}

int PDVTAX_CalcularICMS(double base, double aliquota_percent, double* valor_out) {
  if (valor_out == nullptr) {
    return PDVTAX_ERR_INVALID_ARG;
  }
  pdvtax::IcmsItem item;
  item.base_calculo = base;
  item.aliquota = aliquota_percent;
  item.cst_ou_csosn = "00";
  pdvtax::calcular_icms(item, pdvtax::Crt::RegimeNormal);
  *valor_out = item.valor;
  return PDVTAX_OK;
}

int PDVTAX_CalcularIBPT(double v_prod, double p_nac, double p_imp, double p_est,
                        double p_mun, int importado, double* total_out) {
  if (total_out == nullptr) {
    return PDVTAX_ERR_INVALID_ARG;
  }
  pdvtax::ItemTributavel it;
  it.valor_produto = v_prod;
  it.ibpt_nacional = p_nac;
  it.ibpt_importado = p_imp;
  it.ibpt_estadual = p_est;
  it.ibpt_municipal = p_mun;
  it.produto_importado = importado != 0;
  *total_out = pdvtax::calcular_ibpt(it).total_valor;
  return PDVTAX_OK;
}

}  // extern "C"
