#include "pdvtax/c_api.h"
#include "pdvtax/pdvtax.hpp"

#include <iostream>
#include <iomanip>

int main() {
  using namespace pdvtax;

  std::cout << "=== " << name() << " v" << version() << " ===\n\n";

  std::cout << "[1] Document validation (ACBr-compatible)\n";
  const char* cpf = "123.456.789-09";
  const char* cnpj = "12.345.678/0001-95";
  std::cout << "  CPF  " << cpf << " -> "
            << (is_cpf_valido(cpf) ? "VALID" : "INVALID") << " ("
            << formatar_cpf(cpf) << ")\n";
  std::cout << "  CNPJ " << cnpj << " -> "
            << (is_cnpj_valido(cnpj) ? "VALID" : "INVALID") << " ("
            << formatar_cnpj(cnpj) << ")\n";
  std::cout << "  UF SP -> " << (validar_uf("SP").empty() ? "VALID" : "INVALID")
            << "\n";
  std::cout << "  CEP 01001-000/SP -> "
            << (validar_cep("01001000", "SP").empty() ? "VALID" : "INVALID")
            << "\n\n";

  std::cout << "[2] Access key generation (NFC-e model 65)\n";
  ChaveAcessoParams kp;
  kp.codigo_uf = 35;
  kp.ano = 2026;
  kp.mes = 7;
  kp.cnpj_cpf = "12345678000195";
  kp.modelo = kModeloNFCe;
  kp.serie = 1;
  kp.numero = 1001;
  kp.tp_emis = 1;
  kp.codigo_numerico = 11223344;
  const std::string chave = gerar_chave_acesso(kp);
  std::cout << "  Chave : " << chave << "\n";
  std::cout << "  Format: " << formatar_chave_acesso(chave) << "\n";
  std::cout << "  Valid : " << (validar_chave_acesso(chave) ? "yes" : "no")
            << "\n\n";

  std::cout << "[3] Tax calculation (Simples Nacional PDV item)\n";
  ItemTributavel item;
  item.valor_produto = 19.90;
  item.icms.cst_ou_csosn = "102";
  item.pis.cst = "49";
  item.cofins.cst = "49";
  item.ibpt_nacional = 13.45;
  item.ibpt_estadual = 18.00;
  calcular_item(item, Crt::SimplesNacional);
  const auto ibpt = calcular_ibpt(item);
  std::cout << std::fixed << std::setprecision(2);
  std::cout << "  vProd=" << item.valor_produto << " ICMS=" << item.icms.valor
            << " IBPT~=" << ibpt.total_valor << " (" << ibpt.total_percent
            << "%)\n\n";

  std::cout << "[4] Full NFC-e document prepare + XML showcase\n";
  NfceDocumento nfce;
  nfce.serie = 1;
  nfce.numero = 1001;
  nfce.tp_amb = 2;
  nfce.c_uf = 35;
  nfce.c_nf = 11223344;
  nfce.dh_emi = "2026-07-23T15:00:00-03:00";
  nfce.emit.cnpj = "12345678000195";
  nfce.emit.x_nome = "ACME COMERCIO LTDA";
  nfce.emit.x_fant = "ACME PDV";
  nfce.emit.ie = "110042490114";
  nfce.emit.crt = "1";
  nfce.emit.uf = "SP";
  nfce.emit.c_mun = "3550308";
  nfce.emit.x_mun = "SAO PAULO";
  nfce.emit.cep = "01001000";
  nfce.dest.cpf_cnpj = "12345678909";
  nfce.id_token = "000001";
  nfce.csc = "DEMO";

  ProdutoNfce prod;
  prod.c_prod = "001";
  prod.x_prod = "Refrigerante 350ml";
  prod.ncm = "22021000";
  prod.cfop = "5102";
  prod.q_com = 2;
  prod.v_un_com = 4.99;
  prod.trib.icms.cst_ou_csosn = "102";
  prod.trib.pis.cst = "49";
  prod.trib.cofins.cst = "49";
  prod.trib.ibpt_nacional = 13.45;
  prod.trib.ibpt_estadual = 18.0;
  nfce.itens.push_back(prod);

  Pagamento pag;
  pag.t_pag = "17";  // PIX
  pag.v_pag = 9.98;
  nfce.pagamentos.push_back(pag);
  nfce.inf_cpl = "Documento de demonstracao PDVTax C++ — homologacao";

  preparar_nfce(nfce);
  const std::string xml = gerar_xml_nfce(nfce);
  const std::string qr = gerar_url_qrcode_nfce(nfce);

  std::cout << "  Chave NFC-e: " << nfce.chave << "\n";
  std::cout << "  vNF: " << nfce.totais.v_nf
            << "  vTotTrib: " << nfce.totais.v_tot_trib << "\n";
  std::cout << "  QR URL: " << qr << "\n";
  std::cout << "  XML size: " << xml.size() << " bytes\n";
  std::cout << "  XML preview (first 280 chars):\n    "
            << xml.substr(0, 280) << "...\n\n";

  std::cout << "[5] C API smoke\n";
  std::cout << "  PDVTAX_Nome=" << PDVTAX_Nome() << "\n";
  std::cout << "  PDVTAX_ValidarCPF=" << PDVTAX_ValidarCPF("12345678909")
            << "\n";

  std::cout << "\nDemo completed successfully.\n";
  return 0;
}
