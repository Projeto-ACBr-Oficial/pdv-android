#include "pdvtax/nfce.hpp"

#include <iostream>

#define REQUIRE(expr) \
  ::pdvtax_test::check(static_cast<bool>(expr), #expr, __FILE__, __LINE__)
#define REQUIRE_EQ(a, b)                                                     \
  do {                                                                       \
    const auto _va = (a);                                                    \
    const auto _vb = (b);                                                    \
    const bool _ok = (_va == _vb);                                           \
    ::pdvtax_test::check(_ok, #a " == " #b, __FILE__, __LINE__);             \
    if (!_ok) {                                                              \
      std::cerr << "       left = [" << _va << "] right = [" << _vb << "]\n"; \
    }                                                                        \
  } while (0)

namespace pdvtax_test {
void check(bool cond, const char* expr, const char* file, int line);
}

void test_nfce() {
  using namespace pdvtax;

  NfceDocumento doc;
  doc.serie = 1;
  doc.numero = 1001;
  doc.tp_emis = 1;
  doc.tp_amb = 2;
  doc.c_uf = 35;
  doc.c_nf = 11223344;
  doc.dh_emi = "2026-07-23T14:30:00-03:00";
  doc.emit.cnpj = "12345678000195";
  doc.emit.x_nome = "EMPRESA DEMO PDV LTDA";
  doc.emit.x_fant = "DEMO PDV";
  doc.emit.ie = "110042490114";
  doc.emit.crt = "1";
  doc.emit.uf = "SP";
  doc.emit.c_mun = "3550308";
  doc.emit.cep = "01001000";
  doc.dest.cpf_cnpj = "12345678909";
  doc.dest.x_nome = "CONSUMIDOR";
  doc.id_token = "000001";
  doc.csc = "DEMO-CSC-TOKEN";

  ProdutoNfce p;
  p.n_item = 1;
  p.c_prod = "SKU1";
  p.x_prod = "Produto Teste & Cafe";
  p.ncm = "22021000";
  p.cfop = "5102";
  p.q_com = 2.0;
  p.v_un_com = 5.50;
  p.trib.icms.cst_ou_csosn = "102";
  p.trib.pis.cst = "49";
  p.trib.cofins.cst = "49";
  p.trib.ibpt_nacional = 13.45;
  p.trib.ibpt_estadual = 18.0;
  doc.itens.push_back(p);

  Pagamento pag;
  pag.t_pag = "01";
  pag.v_pag = 11.0;
  doc.pagamentos.push_back(pag);
  doc.inf_cpl = "Tributos aproximados conforme IBPT";

  preparar_nfce(doc);
  REQUIRE_EQ(static_cast<int>(doc.chave.size()), 44);
  REQUIRE(validar_chave_acesso(doc.chave));
  REQUIRE_EQ(doc.totais.v_prod, 11.0);
  REQUIRE_EQ(doc.totais.v_nf, 11.0);

  const std::string xml = gerar_xml_nfce(doc);
  REQUIRE(xml.find("<?xml") == 0);
  REQUIRE(xml.find("versao=\"4.00\"") != std::string::npos);
  REQUIRE(xml.find("<mod>65</mod>") != std::string::npos);
  REQUIRE(xml.find("NFe" + doc.chave) != std::string::npos);
  REQUIRE(xml.find("&amp;") != std::string::npos);  // escaped &
  REQUIRE(xml.find("<vNF>11.00</vNF>") != std::string::npos);
  REQUIRE(xml.find("12345678909") != std::string::npos);

  const std::string qr = gerar_url_qrcode_nfce(doc);
  REQUIRE(qr.find(doc.chave) != std::string::npos);
  REQUIRE(qr.find("homologacao") != std::string::npos ||
          qr.find("qrcode") != std::string::npos);

  REQUIRE(qrcode_host("SP", 1).find("nfce") != std::string::npos);
  REQUIRE(qrcode_host("MG", 2).find("http") == 0);
  REQUIRE(qrcode_host("ZZ", 2).find("http") == 0);

  // empty payments path
  NfceDocumento doc2 = doc;
  doc2.pagamentos.clear();
  const std::string xml2 = gerar_xml_nfce(doc2);
  REQUIRE(xml2.find("<tPag>01</tPag>") != std::string::npos);
}
