# pdv-android

Simulação de PDV Android que se integra com as libs do **Projeto ACBr**.

## Módulos

| Módulo | Descrição |
|--------|-----------|
| `:app` | UI do PDV (produtos, carrinho, checkout, balança) |
| `:domain` / `:data:*` | Camada de domínio e mocks |
| `:acbrlib:bal` | Integração ACBrLibBAL (AAR) |
| `:acbrlib:nfe` | Integração ACBrLibNFe (AAR) |
| `:acbrlib:pixcd` | Integração ACBrLibPIXCD (AAR) |
| **`:acbrlib:pdvtax`** | **Núcleo fiscal em C++17** (port comportamental ACBr) + JNI |
| `:printer:sunmi` | Impressão Sunmi |

## Novidade: módulos fiscais em C++ (`native/pdv-tax-cpp`)

Esta branch adiciona um **port comportamental em C++17** do núcleo tributário de PDV derivado do ACBr:

- Validadores (CPF/CNPJ/UF/CEP/GTIN/IE)
- Geração/validação de **chave de acesso** NFC-e/NF-e
- Cálculos ICMS / PIS / COFINS / IBPT
- Montagem demo de XML NFC-e 4.00 + URL de QR Code
- **C API + JNI** para o app Android (`PdvTaxEngine`)

Documentação completa da migração: [MIGRATION_CPP.md](MIGRATION_CPP.md)  
Apresentação (GitHub Pages): [docs/index.html](docs/index.html)

### Build do núcleo C++ (host / CI)

```bash
cmake -S native/pdv-tax-cpp -B native/pdv-tax-cpp/build
cmake --build native/pdv-tax-cpp/build -j
./native/pdv-tax-cpp/build/pdvtax_tests   # 176 checks
./native/pdv-tax-cpp/build/pdvtax_demo
```

### Build Android

Requer Android SDK + **NDK** (AGP 8.11 / compileSdk 36). O módulo `:acbrlib:pdvtax` compila `libpdvtax.so` via CMake e expõe:

```kotlin
val tax = PdvTaxEngine()
if (tax.isNativeAvailable) {
    tax.validarCnpj("12.345.678/0001-95")
    tax.gerarChaveAcesso(codigoUf = 35, ano = 2026, mes = 7, cnpj = "12345678000195", numero = 1001)
}
```

AARs oficiais do ACBrLib (NFe/BAL/PIX) continuam sendo colocados em `acbrlib/*/libs/` conforme os READMEs de cada módulo.

## Requisitos

- Android Studio Ladybug+ / AGP 8.11
- minSdk 24 · target/compileSdk 36
- NDK r26+ para o módulo C++
