#include "pdvtax/modulo.hpp"

#include "pdvtax/strings.hpp"

namespace pdvtax {

void CalcDigito::set_documento(std::string doc) { documento_ = std::move(doc); }

void CalcDigito::set_multiplicador(int ini, int fim) {
  mult_ini_ = ini;
  mult_fim_ = fim;
}

void CalcDigito::set_formula(DigitoFormula formula) { formula_ = formula; }

void CalcDigito::calculo_padrao() {
  mult_ini_ = 2;
  mult_fim_ = 9;
  mult_atu_ = 0;
  formula_ = DigitoFormula::Modulo11;
}

void CalcDigito::calcular() {
  soma_digitos_ = 0;
  digito_final_ = 0;
  modulo_final_ = 0;

  int base = 0;
  if (mult_atu_ >= mult_ini_ && mult_atu_ <= mult_fim_) {
    base = mult_atu_;
  } else {
    base = mult_ini_;
  }

  const int tamanho = static_cast<int>(documento_.size());
  for (int a = 1; a <= tamanho; ++a) {
    const char ch = documento_[static_cast<std::size_t>(tamanho - a)];
    const int n = char_weight_value(ch);
    int valor_calc = n * base;

    if (formula_ == DigitoFormula::Modulo10 && valor_calc > 9) {
      const int tens = valor_calc / 10;
      const int ones = valor_calc % 10;
      valor_calc = tens + ones;
    }

    soma_digitos_ += valor_calc;

    if (mult_ini_ > mult_fim_) {
      --base;
      if (base < mult_fim_) {
        base = mult_ini_;
      }
    } else {
      ++base;
      if (base > mult_fim_) {
        base = mult_ini_;
      }
    }
  }

  switch (formula_) {
    case DigitoFormula::Modulo11:
      modulo_final_ = soma_digitos_ % 11;
      if (modulo_final_ < 2) {
        digito_final_ = 0;
      } else {
        digito_final_ = 11 - modulo_final_;
      }
      break;
    case DigitoFormula::Modulo10Pis:
      modulo_final_ = soma_digitos_ % 11;
      digito_final_ = 11 - modulo_final_;
      if (digito_final_ >= 10) {
        digito_final_ = 0;
      }
      break;
    case DigitoFormula::Modulo10:
      modulo_final_ = soma_digitos_ % 10;
      digito_final_ = 10 - modulo_final_;
      if (digito_final_ >= 10) {
        digito_final_ = 0;
      }
      break;
  }
}

std::string modulo11(const std::string& documento, int peso, int base) {
  CalcDigito calc;
  calc.set_documento(documento);
  calc.set_multiplicador(peso, base);
  calc.set_formula(DigitoFormula::Modulo11);
  calc.calcular();
  return std::to_string(calc.digito_final());
}

}  // namespace pdvtax
