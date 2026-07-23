# Migration Plan — ACBr PDV Tax Modules → C++

## Goal

Migrate the **tax modules required by a Brazilian PDV (especially Android)** from ACBr Object Pascal to **modern C++17**, preserving algorithm behavior, with a demo, ≥90% automated test coverage, and documentation.

## Non-goals (explicit)

- Full rewrite of all 2,100+ Pascal units
- Pixel-perfect DANFE/SAT hardware drivers
- Live SEFAZ authorization in CI (requires certificates + credentials)
- Drop-in binary replacement of proprietary ACBrLib `.so` builds

## Target modules (Phase 2 scope)

| C++ module | ACBr source of truth | Status |
|------------|----------------------|--------|
| `strings` | `ACBrUtil.Strings` | Done |
| `modulo` | `TACBrCalcDigito` / `Modulo11` | Done |
| `validador` | `ACBrValidador` | Done (IE soft-check for minor UFs) |
| `uf` | IBGE table + `ValidarCodigoUF` | Done |
| `chave_acesso` | `ACBrDFeUtil.GerarChaveAcesso` | Done |
| `impostos` | PDV ICMS/PIS/COFINS/IBPT/IBS-CBS shell | Done |
| `nfce` | NFC-e prepare + minimal XML 4.00 + QR URL | Done |
| `c_api` | ACBrLib-style C exports for NDK/JNI | Done |

## Compatibility principles

1. **Golden fixtures** from ACBr FPCUnit tests must pass identically where algorithms are fully ported.
2. **No silent semantic drift** on DV / chave / money rounding for covered paths.
3. Public C++ API is idiomatic; **C API** mirrors ACBrLib naming style for interop.
4. XML generated is **structurally valid demo NFC-e** (unsigned). Signing remains a separate integration step (OpenSSL).

## Library strategy

See [LIBRARY_COMPAT.md](LIBRARY_COMPAT.md).

## Phased roadmap beyond this delivery

1. **XML-DSig + A1 certificate** (OpenSSL 3.x)
2. **SEFAZ HTTP client** (libcurl) + status/consulta/autorização
3. **Complete IE matrix** for all 27 UFs (full ACBr weight tables)
4. **SAT C++ client** (dynamic load vendor DLL/SO)
5. **Android NDK AAR** wrapping `libpdvtax.so`
6. **Differential tests** against ACBrLib outputs when Pascal toolchain available

## Risk register

| Risk | Mitigation |
|------|------------|
| Full ACBr too large | Scope PDV tax core; document remaining work |
| SEFAZ schema drift | Version pin layout 4.00; add schema tests later |
| Money FP errors | `arredonda()` with long double + epsilon; future integer cents |
| IE state complexity | Port remaining UF rules incrementally with ACBr fixtures |
