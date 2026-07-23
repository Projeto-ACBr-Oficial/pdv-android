# PDVTax C++ — ACBr PDV Tax Modules Migration

Behavioral C++17 port of the **Brazilian PDV tax core** derived from [Projeto ACBr](https://github.com/MirrorProjetoACBr/ACBr) (Object Pascal), focused on the modules required for **NFC-e / PDV Android** workflows.

> This is **not** a full rewrite of the entire ACBr monorepo. It ports the tax algorithms and PDV document helpers with **tests anchored on ACBr fixtures**.

## Features

- CPF / CNPJ (incl. alphanumeric weights) / UF / CEP / e-mail / GTIN / IE validation  
- NFC-e / NF-e **access key** generation & validation (Modulo 11)  
- ICMS / PIS / COFINS / IBPT / IBS-CBS calculation helpers  
- NFC-e prepare → totals → **layout 4.00 XML** (unsigned demo) + QR URL  
- **C API** for Android NDK / FFI (`include/pdvtax/c_api.h`)  
- Demo showcase + unit tests (**176 checks**, **0 failures**)  
- Builds clean with **`-Wall -Wextra -Wpedantic -Werror`**

## Quick start

```bash
cmake -S . -B build
cmake --build build -j
./build/pdvtax_tests
./build/pdvtax_demo
```

Coverage:

```bash
cmake -S . -B build-cov -DPDVTAX_COVERAGE=ON -DPDVTAX_BUILD_DEMO=OFF
cmake --build build-cov -j && ./build-cov/pdvtax_tests
```

## Layout

```
include/pdvtax/   Public headers
src/              Implementation (ACBr behavioral port)
tests/            Unit tests (ACBr golden fixtures)
demo/             End-to-end NFC-e showcase
docs/             GitHub Pages meeting presentation
research/         Phase 1 findings, plan, library matrix
```

## Research

- [Phase 1 findings](research/PHASE1_FINDINGS.md)
- [Migration plan](research/MIGRATION_PLAN.md)
- [Library compatibility](research/LIBRARY_COMPAT.md)
- [Meeting presentation (GitHub Pages)](docs/index.html)

## License note

ACBr is subject to its own license/community terms. This port reimplements algorithms for interoperability research; review ACBr licensing before commercial redistribution.
