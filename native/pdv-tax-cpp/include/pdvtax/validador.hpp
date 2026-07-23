#pragma once

#include <string>

namespace pdvtax {

// Validation API mirrors ACBrValidador free functions:
// empty string = valid; non-empty = error message.

std::string validar_cpf(const std::string& documento, bool ajustar_tamanho = false);
std::string validar_cnpj(const std::string& documento, bool ajustar_tamanho = false);
std::string validar_cnpj_ou_cpf(const std::string& documento);
std::string validar_uf(const std::string& uf);
std::string validar_cep(const std::string& cep, const std::string& uf = "");
std::string validar_email(const std::string& email);
std::string validar_gtin(const std::string& gtin);
std::string validar_ie(const std::string& ie, const std::string& uf);

// Formatters (ACBr Formatar*)
std::string formatar_cpf(const std::string& value);
std::string formatar_cnpj(const std::string& value);
std::string formatar_cep(const std::string& value);
std::string formatar_fone(const std::string& value, const std::string& ddd_padrao = "");

bool is_cpf_valido(const std::string& documento);
bool is_cnpj_valido(const std::string& documento);

}  // namespace pdvtax
