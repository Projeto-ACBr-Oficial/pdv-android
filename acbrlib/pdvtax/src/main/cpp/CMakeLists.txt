cmake_minimum_required(VERSION 3.22.1)
project(pdvtax_android LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

# Shared C++ tax core living at native/pdv-tax-cpp (repo root relative from this file:
# acbrlib/pdvtax/src/main/cpp -> ../../../../../native/pdv-tax-cpp)
set(PDVTAX_ROOT "${CMAKE_CURRENT_SOURCE_DIR}/../../../../../native/pdv-tax-cpp")

add_library(pdvtax SHARED
    pdvtax_jni.cpp
    ${PDVTAX_ROOT}/src/strings.cpp
    ${PDVTAX_ROOT}/src/modulo.cpp
    ${PDVTAX_ROOT}/src/uf.cpp
    ${PDVTAX_ROOT}/src/validador.cpp
    ${PDVTAX_ROOT}/src/chave_acesso.cpp
    ${PDVTAX_ROOT}/src/impostos.cpp
    ${PDVTAX_ROOT}/src/nfce.cpp
    ${PDVTAX_ROOT}/src/c_api.cpp
)

target_include_directories(pdvtax
    PRIVATE
        ${PDVTAX_ROOT}/include
)

find_library(log-lib log)

target_compile_options(pdvtax PRIVATE -Wall -Wextra -Wpedantic)
target_link_libraries(pdvtax ${log-lib})
