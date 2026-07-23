#include "pdvtax/strings.hpp"

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

void test_strings() {
  using namespace pdvtax;
  REQUIRE_EQ(only_number("12.345.678/0001-95"), std::string("12345678000195"));
  REQUIRE_EQ(only_alpha_num("12.345.678/0001-95"), std::string("12345678000195"));
  REQUIRE_EQ(pad_left("91", 11, '0'), std::string("00000000091"));
  REQUIRE_EQ(poem_zeros(35, 2), std::string("35"));
  REQUIRE_EQ(poem_zeros(7, 3), std::string("007"));
  REQUIRE_EQ(remove_zeros_esquerda("000123"), std::string("123"));
  REQUIRE(str_is_number("12345"));
  REQUIRE(!str_is_number("12a45"));
  REQUIRE(str_is_alpha_num("AB12"));
  REQUIRE_EQ(to_upper_ascii("sp"), std::string("SP"));
  REQUIRE_EQ(trim_copy("  hi  "), std::string("hi"));
  REQUIRE_EQ(char_weight_value('0'), 0);
  REQUIRE_EQ(char_weight_value('9'), 9);
  REQUIRE_EQ(char_weight_value('A'), 17);  // alphanumeric CNPJ
  REQUIRE_EQ(poem_zeros(std::string("42"), 5), std::string("00042"));
  REQUIRE_EQ(pad_left("12345", 3, '0'), std::string("123"));  // ACBr LeftStr
  REQUIRE_EQ(remove_zeros_esquerda("0000"), std::string("0"));
  REQUIRE_EQ(only_number(""), std::string(""));
  REQUIRE(!str_is_number(""));
  REQUIRE(!str_is_alpha_num(""));
}
