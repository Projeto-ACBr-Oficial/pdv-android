#pragma once

#include "pdvtax/version.hpp"
#include "pdvtax/strings.hpp"
#include "pdvtax/modulo.hpp"
#include "pdvtax/uf.hpp"
#include "pdvtax/validador.hpp"
#include "pdvtax/chave_acesso.hpp"
#include "pdvtax/impostos.hpp"
#include "pdvtax/nfce.hpp"

namespace pdvtax {

inline const char* version() { return PDVTAX_VERSION_STRING; }
inline const char* name() { return "PDVTax C++ (ACBr behavioral port)"; }

}  // namespace pdvtax
