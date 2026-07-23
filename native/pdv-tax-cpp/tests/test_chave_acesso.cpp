#include "pdvtax/chave_acesso.hpp"

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

void test_chave_acesso() {
  using namespace pdvtax;

  ChaveAcessoParams p;
  p.codigo_uf = 35;
  p.ano = 2026;
  p.mes = 7;
  p.cnpj_cpf = "12345678000195";
  p.modelo = 65;
  p.serie = 1;
  p.numero = 123;
  p.tp_emis = 1;
  p.codigo_numerico = 12345678;

  const std::string chave = gerar_chave_acesso(p);
  REQUIRE_EQ(static_cast<int>(chave.size()), 44);
  REQUIRE(validar_chave_acesso(chave));
  REQUIRE_EQ(chave.substr(0, 2), std::string("35"));
  REQUIRE_EQ(chave.substr(2, 4), std::string("2607"));
  REQUIRE_EQ(chave.substr(6, 14), std::string("12345678000195"));
  REQUIRE_EQ(chave.substr(20, 2), std::string("65"));
  REQUIRE_EQ(chave.substr(22, 3), std::string("001"));
  REQUIRE_EQ(chave.substr(25, 9), std::string("000000123"));
  REQUIRE_EQ(chave.substr(34, 1), std::string("1"));
  REQUIRE_EQ(chave.substr(35, 8), std::string("12345678"));

  const int dv = digito_chave_acesso(chave.substr(0, 43));
  REQUIRE_EQ(dv, chave[43] - '0');

  const std::string fmt = formatar_chave_acesso(chave);
  REQUIRE(fmt.find(' ') != std::string::npos);
  REQUIRE_EQ(static_cast<int>(fmt.size()), 54);  // 44 digits + 10 spaces

  // Invalid key
  REQUIRE(!validar_chave_acesso("123"));
  std::string bad = chave;
  bad[43] = (bad[43] == '0') ? '1' : '0';
  REQUIRE(!validar_chave_acesso(bad));

  // Auto cNF
  p.codigo_numerico = 0;
  const std::string chave2 = gerar_chave_acesso(p);
  REQUIRE(validar_chave_acesso(chave2));
  REQUIRE(gerar_codigo_dfe(123) >= 0);

  // NFe model 55
  p.modelo = 55;
  p.codigo_numerico = 1;
  REQUIRE(validar_chave_acesso(gerar_chave_acesso(p)));
}
