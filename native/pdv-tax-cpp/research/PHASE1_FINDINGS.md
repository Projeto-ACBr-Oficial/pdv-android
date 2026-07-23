# Phase 1 — Research Findings

## Source repository

- Cloned: https://github.com/MirrorProjetoACBr/ACBr (shallow clone)
- Nature: Unofficial mirror of **Projeto ACBr** (Automação Comercial Brasil)
- Primary language: **Object Pascal** (Delphi / Free Pascal / Lazarus)
- Scale: ~14,000 files, **~2,100 `.pas` units**, of which **~1,074** under `Fontes/ACBrDFe`

## What “pdv-android” means in this codebase

There is no single repository named `pdv-android`. PDV-on-Android support is composed of:

| Layer | Path | Role |
|-------|------|------|
| Native tax engine | `Fontes/ACBrDFe`, `Fontes/ACBrSAT`, `Fontes/ACBrDiversos` | NFC-e/NFe, SAT, validators, IBPT/NCM |
| ACBrLib C ABI | `Projetos/ACBrLib/Fontes/*` | Exported DLL/SO API (`NFE_*`, …) |
| Android AAR + JNA | `Projetos/ACBrLib/Android/{NFe,Comum,CEP,PIXCD,BAL,ConsultaCNPJ}` | Java wrappers over `.so` via JNA 5.18.1 |
| Android demos | `Projetos/ACBrLib/Demos/Android/{Java,Flutter,ReactNative,ReactExpo}` | Sample apps |
| Existing C++ demos | `Projetos/ACBrLib/Demos/C++/{NFe,Boleto}` | **Dynamic loaders** of prebuilt ACBrLib, not a C++ reimplementation |

PDV fiscal flow on Android for retail is dominated by **NFC-e (model 65)** via `ACBrLibNFe`, with optional **PIX**, **CEP**, **BAL**, and **SAT** depending on UF.

## Architecture (how it works)

```
PDV App (Java/Kotlin/Flutter)
    → ACBrLibNFe.aar (JNA)
        → libACBrLibNFe.so (compiled Free Pascal)
            → ACBrNFe / PCNNFe / ACBrDFeUtil / ACBrValidador
                → OpenSSL + LibXML2 (Android DLLs in tree)
                    → SEFAZ webservices (TLS + XML-DSig)
```

Typical NFC-e lifecycle exposed by the Android bridge:

1. `NFE_Inicializar` / config (`ConfigGravarValor`)
2. `CarregarINI` or build note in-memory
3. `NFE_GerarChave` · `Assinar` · `Validar` · `ValidarRegrasdeNegocios`
4. `NFE_Enviar` (sync) / contingency
5. DANFE NFC-e print + QR Code URL
6. Events: cancel, inutilização, e-mail

## Tax modules relevant to PDV

| Module | Pascal origin | PDV relevance |
|--------|---------------|---------------|
| Validators | `ACBrValidador.pas` | CPF/CNPJ/IE/CEP/UF/GTIN |
| Access key | `ACBrDFeUtil.GerarChaveAcesso` | 44-digit chave + DV |
| ICMS / PIS / COFINS | PCNNFe + configs | Item taxes |
| IBPT | `ACBrIBPTax.pas` | Lei da transparência |
| NFC-e XML | `pcnNFeW` / XmlWriter | Layout 4.00 |
| QR Code | NFC-e NT | CSC + URL |
| SAT | `ACBrSAT/*` | UF-specific hardware/software |
| Cert / SSL | OpenSSL / Capicom | Signing (not pure logic) |
| SEFAZ WS | `ACBrNFeWebServices.pas` (~4k lines) | Network I/O |

## Demo / test execution status (this environment)

| Attempt | Result |
|---------|--------|
| Free Pascal / Lazarus demos | **Not runnable** — `fpc` / `lazbuild` not installed |
| Android AAR demos | **Not runnable** — no prebuilt `libACBrLibNFe.so` in tree; needs Android SDK/NDK + official binaries |
| ACBr unit tests (`Testes/FPCUnit`) | **Not runnable** without FPC |
| Algorithm fixtures from `acbrvalidadortest.pas` | **Extracted and reused** as golden values for C++ tests |

## Behavioral anchors used for migration

From ACBr tests / sources:

- CPF `12345678909`, CNPJ `12345678000195`
- IE SP `110042490114`, SC `251040852`, RS `2243658792`, RJ `12123124`
- CEP ranges per UF (ACBr `ValidarCEP`)
- `Modulo11` / `TACBrCalcDigito` (weights 2..9 or 2..11)
- `GerarChaveAcesso` layout + DV
- `PadLeft` keeps **left** when truncating

## Conclusion of Phase 1

A **line-by-line port of the entire ACBr tree to C++** is not a single-sprint deliverable (thousands of units, GUI, printers, boleto, eSocial, multi-UF webservice matrices, certificate stores).

A **behavior-preserving C++ port of the PDV tax core** (validators, chave, impostos, NFC-e XML/QR scaffolding, C ABI for NDK) **is** feasible and was selected as the migration scope for Phase 2.
