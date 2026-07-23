#include "pdvtax/chave_acesso.hpp"

#include "pdvtax/modulo.hpp"
#include "pdvtax/strings.hpp"
#include "pdvtax/uf.hpp"

namespace pdvtax {

int gerar_codigo_dfe(std::int64_t numero) {
  // Deterministic stand-in for ACBr GerarCodigoDFe when random is not desired
  // Uses last 8 digits derived from nNF (stable for tests)
  if (numero < 0) {
    numero = 0;
  }
  const std::int64_t code = (numero * 7919 + 1234567) % 100000000LL;
  return static_cast<int>(code);
}

int digito_chave_acesso(const std::string& chave43_sem_dv) {
  return std::stoi(modulo11(chave43_sem_dv, 2, 9));
}

std::string gerar_chave_acesso(const ChaveAcessoParams& p) {
  int codigo = p.codigo_numerico;
  if (codigo == -1) {
    codigo = 0;
  }
  if (codigo == 0) {
    codigo = gerar_codigo_dfe(p.numero);
  }
  if (codigo <= -2) {
    codigo = 0;
  }

  int ano2 = p.ano;
  if (ano2 >= 100) {
    ano2 = ano2 % 100;
  }
  const std::string v_uf = poem_zeros(p.codigo_uf, 2);
  const std::string v_data =
      poem_zeros(ano2, 2) + poem_zeros(p.mes, 2);
  std::string cnpj = only_alpha_num(p.cnpj_cpf);
  cnpj = pad_left(cnpj, 14, '0');
  const std::string v_modelo = poem_zeros(p.modelo, 2);
  const std::string v_serie = poem_zeros(p.serie, 3);
  const std::string v_numero = poem_zeros(p.numero, 9);
  const std::string v_tp = poem_zeros(p.tp_emis, 1);

  std::string body;
  if (p.site_autorizador == -1) {
    body = v_uf + v_data + cnpj + v_modelo + v_serie + v_numero + v_tp +
           poem_zeros(codigo, 8);
  } else {
    body = v_uf + v_data + cnpj + v_modelo + v_serie + v_numero + v_tp +
           poem_zeros(p.site_autorizador, 1) + poem_zeros(codigo, 7);
  }

  return body + modulo11(body, 2, 9);
}

std::string formatar_chave_acesso(const std::string& chave) {
  std::string digits = only_number(chave);
  std::string out;
  for (std::size_t i = 0; i < digits.size(); i += 4) {
    if (!out.empty()) {
      out.push_back(' ');
    }
    out += digits.substr(i, 4);
  }
  return out;
}

bool validar_chave_acesso(const std::string& chave) {
  const std::string d = only_number(chave);
  if (d.size() != 44 || !str_is_number(d)) {
    return false;
  }
  const int uf = std::stoi(d.substr(0, 2));
  if (!validar_codigo_uf(uf)) {
    return false;
  }
  const int modelo = std::stoi(d.substr(20, 2));
  if (modelo != 55 && modelo != 65) {
    return false;
  }
  const int expected = digito_chave_acesso(d.substr(0, 43));
  return expected == (d[43] - '0');
}

}  // namespace pdvtax
