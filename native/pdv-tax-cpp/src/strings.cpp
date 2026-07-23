#include "pdvtax/strings.hpp"

#include <algorithm>
#include <cctype>

namespace pdvtax {

std::string only_number(const std::string& value) {
  std::string out;
  out.reserve(value.size());
  for (char ch : value) {
    const auto c = static_cast<unsigned char>(ch);
    if (std::isdigit(c) != 0) {
      out.push_back(ch);
    }
  }
  return out;
}

std::string only_alpha_num(const std::string& value) {
  std::string out;
  out.reserve(value.size());
  for (char ch : value) {
    const auto c = static_cast<unsigned char>(ch);
    if (std::isalnum(c) != 0) {
      out.push_back(static_cast<char>(std::toupper(c)));
    }
  }
  return out;
}

std::string pad_left(const std::string& value, std::size_t len, char ch) {
  // ACBr PadLeft: if longer than nLen, keep leftmost characters (LeftStr)
  if (value.size() >= len) {
    return value.substr(0, len);
  }
  return std::string(len - value.size(), ch) + value;
}

std::string poem_zeros(std::int64_t num, std::size_t len) {
  return pad_left(std::to_string(num < 0 ? 0 : num), len, '0');
}

std::string poem_zeros(const std::string& text, std::size_t len) {
  return pad_left(trim_copy(text), len, '0');
}

std::string remove_zeros_esquerda(const std::string& value) {
  std::size_t i = 0;
  while (i < value.size() && value[i] == '0') {
    ++i;
  }
  if (i == value.size()) {
    return value.empty() ? "" : "0";
  }
  return value.substr(i);
}

bool str_is_number(const std::string& value) {
  if (value.empty()) {
    return false;
  }
  return std::all_of(value.begin(), value.end(),
                     [](unsigned char c) { return std::isdigit(c) != 0; });
}

bool str_is_alpha_num(const std::string& value) {
  if (value.empty()) {
    return false;
  }
  return std::all_of(value.begin(), value.end(),
                     [](unsigned char c) { return std::isalnum(c) != 0; });
}

std::string to_upper_ascii(const std::string& value) {
  std::string out = value;
  for (char& c : out) {
    c = static_cast<char>(std::toupper(static_cast<unsigned char>(c)));
  }
  return out;
}

std::string trim_copy(const std::string& value) {
  std::size_t start = 0;
  while (start < value.size() &&
         std::isspace(static_cast<unsigned char>(value[start])) != 0) {
    ++start;
  }
  std::size_t end = value.size();
  while (end > start &&
         std::isspace(static_cast<unsigned char>(value[end - 1])) != 0) {
    --end;
  }
  return value.substr(start, end - start);
}

int char_weight_value(char c) {
  return static_cast<unsigned char>(c) - static_cast<unsigned char>('0');
}

}  // namespace pdvtax
