#include "pdvtax/validador.hpp"
#include "pdvtax/uf.hpp"

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

void test_validador() {
  using namespace pdvtax;

  // --- CPF (ACBr fixtures) ---
  REQUIRE(validar_cpf("12345678909").empty());
  REQUIRE(validar_cpf("123.456.789-09").empty());
  REQUIRE(is_cpf_valido("12345678909"));
  REQUIRE(!validar_cpf("12345678901").empty());
  REQUIRE(!validar_cpf("11111111111").empty());
  REQUIRE(!validar_cpf("123456789").empty());
  REQUIRE(!validar_cpf("1234567890123").empty());
  REQUIRE(!validar_cpf("123456789AB").empty());
  REQUIRE(validar_cpf("191", true).empty());  // pad -> 00000000191
  REQUIRE_EQ(formatar_cpf("12345678909"), std::string("123.456.789-09"));
  REQUIRE_EQ(formatar_cpf("191"), std::string("000.000.001-91"));

  // --- CNPJ (ACBr fixtures) ---
  REQUIRE(validar_cnpj("12345678000195").empty());
  REQUIRE(validar_cnpj("12.345.678/0001-95").empty());
  REQUIRE(is_cnpj_valido("12345678000195"));
  REQUIRE(!validar_cnpj("12345678000100").empty());
  REQUIRE(!validar_cnpj("00000000000000").empty());
  REQUIRE(validar_cnpj("191", true).empty());
  REQUIRE_EQ(formatar_cnpj("12345678000195"), std::string("12.345.678/0001-95"));

  REQUIRE(validar_cnpj_ou_cpf("12345678909").empty());
  REQUIRE(validar_cnpj_ou_cpf("12345678000195").empty());
  REQUIRE(!validar_cnpj_ou_cpf("123").empty());

  // --- UF ---
  REQUIRE(validar_uf("SP").empty());
  REQUIRE(validar_uf("sp").empty());
  REQUIRE(!validar_uf("XX").empty());
  REQUIRE(validar_codigo_uf(35));
  REQUIRE(validar_codigo_uf(53));
  REQUIRE(!validar_codigo_uf(99));
  REQUIRE(uf_por_sigla("RJ").has_value());
  REQUIRE(uf_por_codigo(35)->sigla == "SP");
  REQUIRE_EQ(static_cast<int>(todas_ufs().size()), 27);

  // --- CEP ---
  REQUIRE(validar_cep("02260001", "SP").empty());
  REQUIRE(validar_cep("02260-001", "SP").empty());
  REQUIRE(!validar_cep("92260001", "SP").empty());
  REQUIRE(!validar_cep("ABCDEFGHIJ", "SP").empty());
  REQUIRE_EQ(formatar_cep("02260001"), std::string("02260-001"));
  REQUIRE_EQ(formatar_cep("2260"), std::string("02260-000"));
  REQUIRE_EQ(formatar_cep(""), std::string("00000-000"));
  REQUIRE_EQ(formatar_cep("18270"), std::string("18270-000"));
  REQUIRE_EQ(formatar_cep("123456789"), std::string("12345-678"));

  // --- Email ---
  REQUIRE(validar_email("user@example.com").empty());
  REQUIRE(!validar_email("").empty());
  REQUIRE(!validar_email("a@b.c").empty());  // < 8 chars
  REQUIRE(!validar_email("user@@example.com").empty());
  REQUIRE(!validar_email("user@.com").empty());
  REQUIRE(!validar_email(".user@example.com").empty());
  REQUIRE(!validar_email("user@example.com.").empty());

  // --- Phone format ---
  REQUIRE_EQ(formatar_fone(""), std::string(""));
  REQUIRE_EQ(formatar_fone("0000000000"), std::string(""));
  REQUIRE_EQ(formatar_fone("33220000", ""), std::string("3322-0000"));
  REQUIRE_EQ(formatar_fone("1533220000", ""), std::string("(15)3322-0000"));
  REQUIRE_EQ(formatar_fone("33220000", "15"), std::string("(15)3322-0000"));
  REQUIRE_EQ(formatar_fone("997012345", ""), std::string("99701-2345"));
  REQUIRE_EQ(formatar_fone("15997012346", ""), std::string("(15)99701-2346"));
  REQUIRE_EQ(formatar_fone("03001234567", ""), std::string("0300-123-4567"));
  REQUIRE_EQ(formatar_fone("551133220000", ""), std::string("+55(11)3322-0000"));

  // --- GTIN ---
  // EAN-13 example 7891234567895 (compute may vary — use known good)
  // 4006381333931 is a common valid EAN-13
  REQUIRE(validar_gtin("4006381333931").empty());
  REQUIRE(!validar_gtin("123").empty());
  REQUIRE(!validar_gtin("4006381333932").empty());

  // --- IE (ACBr fixtures where algorithms match) ---
  REQUIRE(validar_ie("ISENTO", "SP").empty());
  REQUIRE(!validar_ie("123", "").empty());
  REQUIRE(validar_ie("110042490114", "SP").empty());
  REQUIRE(!validar_ie("123123123123", "SP").empty());
  REQUIRE(validar_ie("P011004243002", "SP").empty());
  REQUIRE(validar_ie("251040852", "SC").empty());
  REQUIRE(validar_ie("2243658792", "RS").empty());

  // RJ 12.123.12-4
  REQUIRE(validar_ie("12123124", "RJ").empty());
  REQUIRE(!validar_ie("12123129", "RJ").empty());
}
