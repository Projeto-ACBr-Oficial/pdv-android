#pragma once

#include <cmath>
#include <string>

namespace pdvtax {

// Money helpers — Brazilian fiscal rounding (2 decimals, half-away-from-zero / commercial)
double arredonda(double value, int casas = 2);

enum class Crt {
  SimplesNacional = 1,
  SimplesExcesso = 2,
  RegimeNormal = 3,
  Mei = 4
};

enum class OrigemMercadoria {
  Nacional = 0,
  EstrangeiraImportacaoDireta = 1,
  EstrangeiraAdquiridaMercadoInterno = 2,
  NacionalConteudoImportacaoSuperior40 = 3,
  NacionalProcessosBasicos = 4,
  NacionalConteudoImportacaoInferiorIgual40 = 5,
  EstrangeiraImportacaoDiretaSemSimilar = 6,
  EstrangeiraAdquiridaMercadoInternoSemSimilar = 7,
  NacionalConteudoImportacaoSuperior70 = 8
};

// Common retail CSOSN (Simples) / CST ICMS used in PDV NFC-e
struct IcmsItem {
  OrigemMercadoria origem = OrigemMercadoria::Nacional;
  std::string cst_ou_csosn = "102";  // e.g. 00, 20, 40, 60, 102, 500
  double base_calculo = 0.0;
  double aliquota = 0.0;             // percent
  double valor = 0.0;
  double reducao_bc_percent = 0.0;
};

struct PisCofinsItem {
  std::string cst = "49";  // common SN outlet
  double base_calculo = 0.0;
  double aliquota = 0.0;
  double valor = 0.0;
};

struct IbptResult {
  double nacional_federal = 0.0;
  double importados_federal = 0.0;
  double estadual = 0.0;
  double municipal = 0.0;
  double total_percent = 0.0;
  double total_valor = 0.0;
  std::string fonte = "IBPT";
  std::string chave = "";
};

struct ItemTributavel {
  double valor_produto = 0.0;
  double valor_desconto = 0.0;
  double valor_frete = 0.0;
  double valor_seguro = 0.0;
  double valor_outro = 0.0;
  IcmsItem icms;
  PisCofinsItem pis;
  PisCofinsItem cofins;
  // IBPT percentages (from table) for lei da transparência
  double ibpt_nacional = 0.0;
  double ibpt_importado = 0.0;
  double ibpt_estadual = 0.0;
  double ibpt_municipal = 0.0;
  bool produto_importado = false;
};

struct TotaisTributarios {
  double v_prod = 0.0;
  double v_desc = 0.0;
  double v_frete = 0.0;
  double v_seg = 0.0;
  double v_outro = 0.0;
  double v_bc_icms = 0.0;
  double v_icms = 0.0;
  double v_pis = 0.0;
  double v_cofins = 0.0;
  double v_nf = 0.0;
  double v_tot_trib = 0.0;  // IBPT approx
};

// Compute ICMS value for an item (mutates item.icms.valor / base)
void calcular_icms(IcmsItem& item, Crt crt);

// PIS/COFINS ad valorem
void calcular_pis_cofins(PisCofinsItem& item);

// IBPT (lei 12.741/2012) approximate tax burden on item
IbptResult calcular_ibpt(const ItemTributavel& item);

// Full line tax pipeline used by PDV before mounting NFC-e
void calcular_item(ItemTributavel& item, Crt crt);

// Aggregate totals
TotaisTributarios somar_totais(const ItemTributavel* items, std::size_t count);

// IBS/CBS placeholders for reforma tributária fields (NT 2025+) — structure only
struct IbsCbsItem {
  std::string cst = "";
  std::string c_class_trib = "";
  double v_bc = 0.0;
  double p_ibs_uf = 0.0;
  double v_ibs_uf = 0.0;
  double p_ibs_mun = 0.0;
  double v_ibs_mun = 0.0;
  double p_cbs = 0.0;
  double v_cbs = 0.0;
};

void calcular_ibs_cbs(IbsCbsItem& item);

}  // namespace pdvtax
