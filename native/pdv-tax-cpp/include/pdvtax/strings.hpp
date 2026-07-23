#pragma once

#include <cstdint>
#include <string>

namespace pdvtax {

// Mirrors ACBrUtil.Strings helpers used by tax modules.
std::string only_number(const std::string& value);
std::string only_alpha_num(const std::string& value);
std::string pad_left(const std::string& value, std::size_t len, char ch = '0');
std::string poem_zeros(std::int64_t num, std::size_t len);
std::string poem_zeros(const std::string& text, std::size_t len);
std::string remove_zeros_esquerda(const std::string& value);
bool str_is_number(const std::string& value);
bool str_is_alpha_num(const std::string& value);
std::string to_upper_ascii(const std::string& value);
std::string trim_copy(const std::string& value);

// Character value for modulo calc (digit or letter): Ord(c) - Ord('0')
// Supports alphanumeric CNPJ (Receita Federal / ACBr).
int char_weight_value(char c);

}  // namespace pdvtax
