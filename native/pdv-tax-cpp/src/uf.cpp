#include "pdvtax/uf.hpp"

#include "pdvtax/strings.hpp"

namespace pdvtax {
namespace {

const std::vector<UfInfo>& tabela() {
  static const std::vector<UfInfo> kUfs = {
      {"AC", 12, "Acre"},
      {"AL", 27, "Alagoas"},
      {"AP", 16, "Amapa"},
      {"AM", 13, "Amazonas"},
      {"BA", 29, "Bahia"},
      {"CE", 23, "Ceara"},
      {"DF", 53, "Distrito Federal"},
      {"ES", 32, "Espirito Santo"},
      {"GO", 52, "Goias"},
      {"MA", 21, "Maranhao"},
      {"MT", 51, "Mato Grosso"},
      {"MS", 50, "Mato Grosso do Sul"},
      {"MG", 31, "Minas Gerais"},
      {"PA", 15, "Para"},
      {"PB", 25, "Paraiba"},
      {"PR", 41, "Parana"},
      {"PE", 26, "Pernambuco"},
      {"PI", 22, "Piaui"},
      {"RJ", 33, "Rio de Janeiro"},
      {"RN", 24, "Rio Grande do Norte"},
      {"RS", 43, "Rio Grande do Sul"},
      {"RO", 11, "Rondonia"},
      {"RR", 14, "Roraima"},
      {"SC", 42, "Santa Catarina"},
      {"SP", 35, "Sao Paulo"},
      {"SE", 28, "Sergipe"},
      {"TO", 17, "Tocantins"},
  };
  return kUfs;
}

}  // namespace

bool validar_codigo_uf(int codigo) {
  // ACBrDFeUtil.ValidarCodigoUF codes + EX
  static const int kCodes[] = {12, 27, 16, 13, 29, 23, 53, 32, 52, 21, 51, 50,
                               31, 15, 25, 41, 26, 22, 33, 24, 43, 11, 14, 42,
                               35, 28, 17, 90, 91};
  for (int c : kCodes) {
    if (c == codigo) {
      return true;
    }
  }
  return false;
}

std::optional<UfInfo> uf_por_sigla(const std::string& sigla) {
  const std::string s = to_upper_ascii(trim_copy(sigla));
  for (const auto& u : tabela()) {
    if (u.sigla == s) {
      return u;
    }
  }
  return std::nullopt;
}

std::optional<UfInfo> uf_por_codigo(int codigo) {
  for (const auto& u : tabela()) {
    if (u.codigo_ibge == codigo) {
      return u;
    }
  }
  return std::nullopt;
}

std::vector<UfInfo> todas_ufs() { return tabela(); }

}  // namespace pdvtax
