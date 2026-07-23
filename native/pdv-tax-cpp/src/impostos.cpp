#include "pdvtax/impostos.hpp"

#include <cmath>

namespace pdvtax {

double arredonda(double value, int casas) {
  // Commercial half-away-from-zero with long double + epsilon to counter binary FP
  const long double f = std::pow(10.0L, static_cast<long double>(casas));
  long double scaled = static_cast<long double>(value) * f;
  constexpr long double eps = 1e-10L;
  if (scaled >= 0.0L) {
    scaled = std::floor(scaled + 0.5L + eps);
  } else {
    scaled = std::ceil(scaled - 0.5L - eps);
  }
  return static_cast<double>(scaled / f);
}

void calcular_icms(IcmsItem& item, Crt crt) {
  const std::string& c = item.cst_ou_csosn;

  // Isentos / não tributados / cobrado anteriormente / SN sem crédito
  if (c == "40" || c == "41" || c == "50" || c == "60" || c == "102" ||
      c == "103" || c == "300" || c == "400" || c == "500" || c == "900") {
    if (c == "60" || c == "500") {
      // ST cobrado antes — vICMS = 0 on this document
      item.base_calculo = 0.0;
      item.valor = 0.0;
      return;
    }
    if (crt == Crt::SimplesNacional || crt == Crt::Mei) {
      item.base_calculo = 0.0;
      item.valor = 0.0;
      return;
    }
    if (c == "40" || c == "41" || c == "50") {
      item.base_calculo = 0.0;
      item.valor = 0.0;
      return;
    }
  }

  double bc = item.base_calculo;
  if (item.reducao_bc_percent > 0.0) {
    bc = arredonda(bc * (1.0 - item.reducao_bc_percent / 100.0));
  }
  item.base_calculo = bc;
  item.valor = arredonda(bc * item.aliquota / 100.0);
}

void calcular_pis_cofins(PisCofinsItem& item) {
  // CSTs without calculation
  if (item.cst == "04" || item.cst == "05" || item.cst == "06" ||
      item.cst == "07" || item.cst == "08" || item.cst == "09" ||
      item.cst == "49" || item.cst == "99") {
    if (item.aliquota <= 0.0) {
      item.valor = 0.0;
      return;
    }
  }
  item.valor = arredonda(item.base_calculo * item.aliquota / 100.0);
}

IbptResult calcular_ibpt(const ItemTributavel& item) {
  IbptResult r;
  const double base = arredonda(item.valor_produto - item.valor_desconto +
                                item.valor_frete + item.valor_seguro +
                                item.valor_outro);
  r.nacional_federal = item.ibpt_nacional;
  r.importados_federal = item.ibpt_importado;
  r.estadual = item.ibpt_estadual;
  r.municipal = item.ibpt_municipal;
  const double p_fed =
      item.produto_importado ? item.ibpt_importado : item.ibpt_nacional;
  r.total_percent = p_fed + item.ibpt_estadual + item.ibpt_municipal;
  r.total_valor = arredonda(base * r.total_percent / 100.0);
  r.fonte = "IBPT";
  return r;
}

void calcular_item(ItemTributavel& item, Crt crt) {
  const double liquido = arredonda(item.valor_produto - item.valor_desconto +
                                   item.valor_frete + item.valor_seguro +
                                   item.valor_outro);
  if (item.icms.base_calculo <= 0.0) {
    item.icms.base_calculo = liquido;
  }
  calcular_icms(item.icms, crt);

  if (item.pis.base_calculo <= 0.0) {
    item.pis.base_calculo = liquido;
  }
  if (item.cofins.base_calculo <= 0.0) {
    item.cofins.base_calculo = liquido;
  }
  calcular_pis_cofins(item.pis);
  calcular_pis_cofins(item.cofins);
}

TotaisTributarios somar_totais(const ItemTributavel* items, std::size_t count) {
  TotaisTributarios t{};
  for (std::size_t i = 0; i < count; ++i) {
    const auto& it = items[i];
    t.v_prod = arredonda(t.v_prod + it.valor_produto);
    t.v_desc = arredonda(t.v_desc + it.valor_desconto);
    t.v_frete = arredonda(t.v_frete + it.valor_frete);
    t.v_seg = arredonda(t.v_seg + it.valor_seguro);
    t.v_outro = arredonda(t.v_outro + it.valor_outro);
    t.v_bc_icms = arredonda(t.v_bc_icms + it.icms.base_calculo);
    t.v_icms = arredonda(t.v_icms + it.icms.valor);
    t.v_pis = arredonda(t.v_pis + it.pis.valor);
    t.v_cofins = arredonda(t.v_cofins + it.cofins.valor);
    t.v_tot_trib =
        arredonda(t.v_tot_trib + calcular_ibpt(it).total_valor);
  }
  t.v_nf = arredonda(t.v_prod - t.v_desc + t.v_frete + t.v_seg + t.v_outro);
  return t;
}

void calcular_ibs_cbs(IbsCbsItem& item) {
  item.v_ibs_uf = arredonda(item.v_bc * item.p_ibs_uf / 100.0);
  item.v_ibs_mun = arredonda(item.v_bc * item.p_ibs_mun / 100.0);
  item.v_cbs = arredonda(item.v_bc * item.p_cbs / 100.0);
}

}  // namespace pdvtax
