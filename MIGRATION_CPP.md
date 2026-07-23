# Migração dos módulos fiscais ACBr → C++ (PDV Android)

## Contexto

O repositório **pdv-android** integra as AARs do ACBrLib (NFe, BAL, PIXCD). O motor fiscal completo do ACBr é Object Pascal (~2.100 units). Esta entrega porta o **núcleo fiscal de PDV** para **C++17**, com paridade de comportamento verificada por testes, e o expõe ao app via JNI.

## Escopo entregue

| Caminho | Conteúdo |
|---------|----------|
| `native/pdv-tax-cpp/` | Biblioteca C++17, demo, 176 testes, research notes |
| `acbrlib/pdvtax/` | Módulo Android (CMake/NDK + Kotlin `PdvTaxEngine`) |
| `docs/` | Apresentação de meeting (GitHub Pages) |

## O que NÃO foi reescrito

- Webservices SEFAZ / XML-DSig (recomendado: OpenSSL 3.x na próxima onda)
- AARs oficiais ACBrLib (continuam como caminho de produção até o C++ cobrir Assinar/Enviar)
- Hardware SAT / impressoras fiscais legadas

## Compatibilidade de bibliotecas

| Camada | Escolha |
|--------|---------|
| Core C++ | STL apenas (zero deps runtime) |
| Android NDK | C++17, `c++_shared`, ABIs arm/x86 |
| Assinar/HTTP (futuro) | OpenSSL **3.3+** (evitar 1.1.1 do tree ACBr) + libcurl 8.x |
| AGP / JNA (AAR legado) | AGP 8.11 · JNA 5.18.x nos AARs oficiais |

## Verificação

```bash
# Host
cmake -S native/pdv-tax-cpp -B native/pdv-tax-cpp/build
cmake --build native/pdv-tax-cpp/build -j
./native/pdv-tax-cpp/build/pdvtax_tests   # 176 checks, 0 failures
./native/pdv-tax-cpp/build/pdvtax_demo

# Android (com NDK)
./gradlew :acbrlib:pdvtax:assembleDebug
./gradlew :acbrlib:pdvtax:test
```

## Apresentação

- Resumo: `docs/index.html`
- Slides: `docs/presentation.html`
