#pragma once

#include <optional>
#include <string>
#include <vector>

namespace pdvtax {

struct UfInfo {
  std::string sigla;
  int codigo_ibge = 0;
  std::string nome;
};

// Valid IBGE UF codes used by ACBrDFeUtil.ValidarCodigoUF
bool validar_codigo_uf(int codigo);

std::optional<UfInfo> uf_por_sigla(const std::string& sigla);
std::optional<UfInfo> uf_por_codigo(int codigo);
std::vector<UfInfo> todas_ufs();

// Modelo DF: 55=NFe, 65=NFCe
constexpr int kModeloNFe = 55;
constexpr int kModeloNFCe = 65;

}  // namespace pdvtax
