#include "pdvtax/modulo.hpp"

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

void test_modulo() {
  using namespace pdvtax;

  // CPF body 123456789 -> DV 0 and 9 (known ACBr fixture 12345678909)
  CalcDigito m;
  m.set_multiplicador(2, 11);
  m.set_formula(DigitoFormula::Modulo11);
  m.set_documento("123456789");
  m.calcular();
  REQUIRE_EQ(m.digito_final(), 0);
  m.set_documento("1234567890");
  m.calcular();
  REQUIRE_EQ(m.digito_final(), 9);

  // CNPJ body 123456780001 -> 95
  m.calculo_padrao();
  m.set_documento("123456780001");
  m.calcular();
  REQUIRE_EQ(m.digito_final(), 9);
  m.set_documento("1234567800019");
  m.calcular();
  REQUIRE_EQ(m.digito_final(), 5);

  // Default Modulo11 weights 2..9 (not CPF's 2..11)
  REQUIRE_EQ(modulo11("123456789", 2, 9), std::string("7"));
  // CPF-style weights 2..11 on body yields first DV 0
  REQUIRE_EQ(modulo11("123456789", 2, 11), std::string("0"));

  // Modulo10
  CalcDigito m10;
  m10.set_documento("123");
  m10.set_multiplicador(2, 1);
  m10.set_formula(DigitoFormula::Modulo10);
  m10.calcular();
  REQUIRE(m10.digito_final() >= 0 && m10.digito_final() <= 9);

  // Modulo10PIS
  CalcDigito mp;
  mp.set_documento("1234567890");
  mp.calculo_padrao();
  mp.set_formula(DigitoFormula::Modulo10Pis);
  mp.calcular();
  REQUIRE(mp.digito_final() >= 0 && mp.digito_final() <= 9);

  REQUIRE_EQ(m.soma_digitos() > 0, true);
}
