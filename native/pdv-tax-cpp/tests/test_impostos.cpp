#include "pdvtax/impostos.hpp"

#include <cmath>
#include <iostream>

#define REQUIRE(expr) \
  ::pdvtax_test::check(static_cast<bool>(expr), #expr, __FILE__, __LINE__)
#define REQUIRE_EQ(a, b)                                                     \
  do {                                                                       \
    const auto _va = (a);                                                    \
    const auto _vb = (b);                                                    \
    const bool _ok = (_va == _vb);                                           \
    ::pdvtax_test::check(_ok, #a " == " #b, __FILE__, __LINE__);             \
    if (!_ok) {                                                              \
      std::cerr << "       left = [" << _va << "] right = [" << _vb << "]\n"; \
    }                                                                        \
  } while (0)

namespace pdvtax_test {
void check(bool cond, const char* expr, const char* file, int line);
}

void test_impostos() {
  using namespace pdvtax;

  REQUIRE_EQ(arredonda(1.005, 2), 1.01);
  REQUIRE_EQ(arredonda(1.004, 2), 1.0);
  REQUIRE_EQ(arredonda(-1.005, 2), -1.01);

  IcmsItem icms;
  icms.cst_ou_csosn = "00";
  icms.base_calculo = 100.0;
  icms.aliquota = 18.0;
  calcular_icms(icms, Crt::RegimeNormal);
  REQUIRE_EQ(icms.valor, 18.0);

  icms.reducao_bc_percent = 10.0;
  icms.base_calculo = 100.0;
  calcular_icms(icms, Crt::RegimeNormal);
  REQUIRE_EQ(icms.base_calculo, 90.0);
  REQUIRE_EQ(icms.valor, 16.2);

  icms.cst_ou_csosn = "102";
  icms.base_calculo = 50.0;
  icms.aliquota = 18.0;
  calcular_icms(icms, Crt::SimplesNacional);
  REQUIRE_EQ(icms.valor, 0.0);

  icms.cst_ou_csosn = "500";
  calcular_icms(icms, Crt::SimplesNacional);
  REQUIRE_EQ(icms.valor, 0.0);

  PisCofinsItem pis;
  pis.cst = "01";
  pis.base_calculo = 100.0;
  pis.aliquota = 1.65;
  calcular_pis_cofins(pis);
  REQUIRE_EQ(pis.valor, 1.65);

  pis.cst = "49";
  pis.aliquota = 0.0;
  calcular_pis_cofins(pis);
  REQUIRE_EQ(pis.valor, 0.0);

  ItemTributavel item;
  item.valor_produto = 100.0;
  item.ibpt_nacional = 13.45;
  item.ibpt_estadual = 18.0;
  item.ibpt_municipal = 0.0;
  auto ibpt = calcular_ibpt(item);
  REQUIRE_EQ(ibpt.total_percent, 31.45);
  REQUIRE_EQ(ibpt.total_valor, 31.45);

  item.produto_importado = true;
  item.ibpt_importado = 20.0;
  ibpt = calcular_ibpt(item);
  REQUIRE_EQ(ibpt.total_percent, 38.0);

  item.valor_produto = 100.0;
  item.valor_desconto = 10.0;
  item.icms.cst_ou_csosn = "102";
  item.pis.cst = "49";
  item.cofins.cst = "49";
  calcular_item(item, Crt::SimplesNacional);
  REQUIRE_EQ(item.icms.valor, 0.0);

  ItemTributavel items[2];
  items[0] = item;
  items[1].valor_produto = 50.0;
  items[1].icms.cst_ou_csosn = "102";
  items[1].pis.cst = "49";
  items[1].cofins.cst = "49";
  items[1].ibpt_nacional = 10.0;
  calcular_item(items[1], Crt::SimplesNacional);
  auto tot = somar_totais(items, 2);
  REQUIRE_EQ(tot.v_prod, 150.0);
  REQUIRE(tot.v_nf > 0.0);

  IbsCbsItem ibs;
  ibs.v_bc = 100.0;
  ibs.p_ibs_uf = 0.1;
  ibs.p_ibs_mun = 0.05;
  ibs.p_cbs = 0.9;
  calcular_ibs_cbs(ibs);
  REQUIRE_EQ(ibs.v_ibs_uf, 0.1);
  REQUIRE_EQ(ibs.v_ibs_mun, 0.05);
  REQUIRE_EQ(ibs.v_cbs, 0.9);
}
