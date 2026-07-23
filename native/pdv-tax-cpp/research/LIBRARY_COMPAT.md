# Library Compatibility Analysis

## ACBr Android stack (upstream)

| Component | Version in ACBr tree | Notes |
|-----------|----------------------|-------|
| Android min SDK | 24 (Nougat) | README of ACBrLib Android |
| Android Gradle Plugin | 8.6.0+ | Kotlin DSL samples |
| JNA | **5.18.1** (`@aar`) | JNI bridge to `.so` |
| OpenSSL (Android DLLs) | **1.1.1w** / 1.1.1d | Legacy 1.1.x in `DLLs/Android/OpenSSL` |
| LibXML2 | **2.9.10** | `DLLs/Android/LibXML2` |
| LibXSLT | 1.1.34 | Companion to LibXML2 |
| Memory pages | 16 KB aligned | Android 15 requirement |

## PDVTax C++ choices (this migration)

| Need | Choice | Why |
|------|--------|-----|
| Language | **C++17** | Wide NDK / desktop support; no C++20 requirement |
| Build | **CMake ≥ 3.16** (tested with **CMake 4.3.4**) | Standard, NDK toolchain files ready |
| Compiler | Apple Clang 21 / GCC / MSVC | `-Wall -Wextra -Wpedantic -Werror` clean |
| XML (generation) | **Hand-built strings** | Zero deps for demo; schema writer is deterministic |
| Crypto / XML-DSig | **Not bundled yet** | Recommend **OpenSSL 3.3+** or **3.4** (not 1.1.1 — EOL) when adding Assinar |
| HTTP SEFAZ | **Not bundled yet** | Recommend **libcurl 8.x** with OpenSSL backend |
| JSON (optional) | n/a | Not required for fiscal XML core |
| Unit tests | **Header-free custom runner** | No gtest download in offline CI; 176 checks |
| Coverage | gcov / `--coverage` | Measured **100% line coverage** on `src/*.cpp` executable lines |
| Android interop | **C API** (`c_api.h`) | Same pattern as ACBrLib for JNI/`dlopen` |

## Latest-version recommendations (next integration wave)

| Library | Avoid (ACBr legacy) | Prefer (2026) |
|---------|---------------------|---------------|
| OpenSSL | 1.1.1.x | **3.3 / 3.4 LTS** |
| LibXML2 | 2.9.10 | **2.12+** or **libxml2 2.13** with security backports |
| JNA (if keeping Java shell) | — | **5.18.x** is already current-line |
| Android NDK | — | **r27 / r28** with 16 KB page size |
| CMake | — | **3.28+** or 4.x |

## ABI notes

- `PDVTAX_*` C functions are `extern "C"` and safe for NDK.
- No exceptions cross the C boundary.
- UTF-8 strings; callers own buffers (ACBrLib-style).

## Decision

Ship the tax **core with zero third-party runtime dependencies** so it can be linked into Android NDK, iOS, Linux POS, or desktop PDV. Add OpenSSL 3.x + libcurl only when implementing **Assinar / Enviar** parity with ACBrLibNFe.
