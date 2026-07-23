cmake_minimum_required(VERSION 3.16)
project(pdv-tax-cpp VERSION 1.0.0 LANGUAGES CXX C)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)
set(CMAKE_EXPORT_COMPILE_COMMANDS ON)

option(PDVTAX_BUILD_TESTS "Build unit tests" ON)
option(PDVTAX_BUILD_DEMO "Build demo showcase" ON)
option(PDVTAX_WARNINGS_AS_ERRORS "Treat warnings as errors" ON)

add_library(pdvtax
  src/strings.cpp
  src/modulo.cpp
  src/uf.cpp
  src/validador.cpp
  src/chave_acesso.cpp
  src/impostos.cpp
  src/nfce.cpp
  src/c_api.cpp
)

add_library(pdvtax::pdvtax ALIAS pdvtax)

target_include_directories(pdvtax
  PUBLIC
    $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/include>
    $<INSTALL_INTERFACE:include>
)

if(MSVC)
  target_compile_options(pdvtax PRIVATE /W4)
  if(PDVTAX_WARNINGS_AS_ERRORS)
    target_compile_options(pdvtax PRIVATE /WX)
  endif()
else()
  target_compile_options(pdvtax PRIVATE -Wall -Wextra -Wpedantic -Wconversion -Wshadow)
  if(PDVTAX_WARNINGS_AS_ERRORS)
    target_compile_options(pdvtax PRIVATE -Werror)
  endif()
endif()

if(PDVTAX_BUILD_DEMO)
  add_executable(pdvtax_demo demo/main.cpp)
  target_link_libraries(pdvtax_demo PRIVATE pdvtax)
  if(NOT MSVC)
    target_compile_options(pdvtax_demo PRIVATE -Wall -Wextra -Wpedantic)
    if(PDVTAX_WARNINGS_AS_ERRORS)
      target_compile_options(pdvtax_demo PRIVATE -Werror)
    endif()
  endif()
endif()

if(PDVTAX_BUILD_TESTS)
  enable_testing()
  add_executable(pdvtax_tests
    tests/test_main.cpp
    tests/test_strings.cpp
    tests/test_modulo.cpp
    tests/test_validador.cpp
    tests/test_chave_acesso.cpp
    tests/test_impostos.cpp
    tests/test_nfce.cpp
    tests/test_c_api.cpp
  )
  target_link_libraries(pdvtax_tests PRIVATE pdvtax)
  if(NOT MSVC)
    target_compile_options(pdvtax_tests PRIVATE -Wall -Wextra -Wpedantic)
    if(PDVTAX_WARNINGS_AS_ERRORS)
      target_compile_options(pdvtax_tests PRIVATE -Werror)
    endif()
  endif()
  add_test(NAME pdvtax_tests COMMAND pdvtax_tests)

  # Optional coverage flags for clang/gcc
  option(PDVTAX_COVERAGE "Enable coverage flags" OFF)
  if(PDVTAX_COVERAGE AND NOT MSVC)
    foreach(tgt pdvtax pdvtax_tests)
      target_compile_options(${tgt} PRIVATE -fprofile-arcs -ftest-coverage -O0 -g)
      target_link_options(${tgt} PRIVATE -fprofile-arcs -ftest-coverage)
    endforeach()
    if(TARGET pdvtax_demo)
      target_compile_options(pdvtax_demo PRIVATE -fprofile-arcs -ftest-coverage -O0 -g)
      target_link_options(pdvtax_demo PRIVATE -fprofile-arcs -ftest-coverage)
    endif()
  endif()
endif()

install(TARGETS pdvtax ARCHIVE DESTINATION lib LIBRARY DESTINATION lib)
install(DIRECTORY include/pdvtax DESTINATION include)
