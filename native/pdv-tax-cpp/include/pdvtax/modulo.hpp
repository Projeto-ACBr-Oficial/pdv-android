#pragma once

#include <string>

namespace pdvtax {

// Port of TACBrCalcDigito / Modulo11 from ACBrValidador.pas
enum class DigitoFormula {
  Modulo11,     // frModulo11
  Modulo10Pis,  // frModulo10PIS
  Modulo10      // frModulo10
};

class CalcDigito {
 public:
  void set_documento(std::string doc);
  void set_multiplicador(int ini, int fim);
  void set_formula(DigitoFormula formula);
  void calculo_padrao();  // 2..9, Modulo11
  void calcular();

  int digito_final() const { return digito_final_; }
  int soma_digitos() const { return soma_digitos_; }
  int modulo_final() const { return modulo_final_; }

 private:
  std::string documento_;
  int mult_ini_ = 2;
  int mult_fim_ = 9;
  int mult_atu_ = 0;
  DigitoFormula formula_ = DigitoFormula::Modulo11;
  int digito_final_ = 0;
  int soma_digitos_ = 0;
  int modulo_final_ = 0;
};

// Default peso=2, base=9 (ACBr Validador.Modulo11)
std::string modulo11(const std::string& documento, int peso = 2, int base = 9);

}  // namespace pdvtax
