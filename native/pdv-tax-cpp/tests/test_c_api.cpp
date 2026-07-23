#include "pdvtax/c_api.h"

#include <cstring>
#include <iostream>
#include <string>

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

void test_c_api() {
  REQUIRE(std::strlen(PDVTAX_Nome()) > 0);
  REQUIRE(std::string(PDVTAX_Versao()) == "1.0.0");

  REQUIRE_EQ(PDVTAX_ValidarCPF("12345678909"), 1);
  REQUIRE_EQ(PDVTAX_ValidarCPF("12345678901"), 0);
  REQUIRE_EQ(PDVTAX_ValidarCPF(nullptr), 0);
  REQUIRE_EQ(PDVTAX_ValidarCNPJ("12345678000195"), 1);
  REQUIRE_EQ(PDVTAX_ValidarCNPJ(nullptr), 0);
  REQUIRE_EQ(PDVTAX_ValidarUF("SP"), 1);
  REQUIRE_EQ(PDVTAX_ValidarUF("XX"), 0);
  REQUIRE_EQ(PDVTAX_ValidarUF(nullptr), 0);

  char chave[64];
  REQUIRE_EQ(PDVTAX_GerarChave(35, 2026, 7, "12345678000195", 65, 1, 10, 1,
                               12345678, chave, 64),
             PDVTAX_OK);
  REQUIRE_EQ(static_cast<int>(std::strlen(chave)), 44);
  REQUIRE_EQ(PDVTAX_GerarChave(35, 2026, 7, "12345678000195", 65, 1, 10, 1,
                               12345678, chave, 10),
             PDVTAX_ERR_BUFFER);
  REQUIRE_EQ(PDVTAX_GerarChave(35, 2026, 7, nullptr, 65, 1, 10, 1, 1, chave, 64),
             PDVTAX_ERR_INVALID_ARG);

  char fmt[80];
  REQUIRE_EQ(PDVTAX_FormatarChave(chave, fmt, 80), PDVTAX_OK);
  REQUIRE(std::string(fmt).find(' ') != std::string::npos);
  REQUIRE_EQ(PDVTAX_FormatarChave(chave, fmt, 5), PDVTAX_ERR_BUFFER);
  REQUIRE_EQ(PDVTAX_FormatarChave(nullptr, fmt, 80), PDVTAX_ERR_INVALID_ARG);

  double v = 0.0;
  REQUIRE_EQ(PDVTAX_CalcularICMS(100.0, 18.0, &v), PDVTAX_OK);
  REQUIRE_EQ(v, 18.0);
  REQUIRE_EQ(PDVTAX_CalcularICMS(100.0, 18.0, nullptr), PDVTAX_ERR_INVALID_ARG);

  REQUIRE_EQ(PDVTAX_CalcularIBPT(100.0, 10.0, 20.0, 18.0, 0.0, 0, &v),
             PDVTAX_OK);
  REQUIRE_EQ(v, 28.0);
  REQUIRE_EQ(PDVTAX_CalcularIBPT(100.0, 10.0, 20.0, 18.0, 0.0, 1, &v),
             PDVTAX_OK);
  REQUIRE_EQ(v, 38.0);
  REQUIRE_EQ(PDVTAX_CalcularIBPT(100.0, 10.0, 20.0, 18.0, 0.0, 0, nullptr),
             PDVTAX_ERR_INVALID_ARG);
}
