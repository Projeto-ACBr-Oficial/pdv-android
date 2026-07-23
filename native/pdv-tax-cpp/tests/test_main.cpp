#include <cstdlib>
#include <iostream>

// Minimal test runner — each test file registers via static constructors.
// Returns number of failed assertions.

namespace pdvtax_test {

struct Stats {
  int checks = 0;
  int failures = 0;
};

Stats& stats() {
  static Stats s;
  return s;
}

void check(bool cond, const char* expr, const char* file, int line) {
  ++stats().checks;
  if (!cond) {
    ++stats().failures;
    std::cerr << "FAIL " << file << ":" << line << "  " << expr << "\n";
  }
}

}  // namespace pdvtax_test

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

// Declarations of suite runners
void test_strings();
void test_modulo();
void test_validador();
void test_chave_acesso();
void test_impostos();
void test_nfce();
void test_c_api();

int main() {
  test_strings();
  test_modulo();
  test_validador();
  test_chave_acesso();
  test_impostos();
  test_nfce();
  test_c_api();

  const auto& s = pdvtax_test::stats();
  std::cout << "pdvtax tests: " << s.checks << " checks, " << s.failures
            << " failures\n";
  return s.failures == 0 ? EXIT_SUCCESS : EXIT_FAILURE;
}
