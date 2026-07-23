#pragma once

#include "pdvtax/chave_acesso.hpp"
#include "pdvtax/impostos.hpp"

#include <cstdint>
#include <string>
#include <vector>

namespace pdvtax {

struct Emitente {
  std::string cnpj;
  std::string x_nome;
  std::string x_fant;
  std::string ie;
  std::string crt = "1";  // 1=SN
  std::string c_mun = "3550308";
  std::string x_mun = "SAO PAULO";
  std::string uf = "SP";
  std::string cep = "01001000";
  std::string x_lgr = "RUA EXEMPLO";
  std::string nro = "100";
  std::string x_bairro = "CENTRO";
};

struct Destinatario {
  std::string cpf_cnpj;  // optional for NFC-e
  std::string x_nome;
};

struct ProdutoNfce {
  int n_item = 1;
  std::string c_prod;
  std::string c_ean = "SEM GTIN";
  std::string x_prod;
  std::string ncm = "00000000";
  std::string cfop = "5102";
  std::string u_com = "UN";
  double q_com = 1.0;
  double v_un_com = 0.0;
  double v_prod = 0.0;
  double v_desc = 0.0;
  ItemTributavel trib;
};

struct Pagamento {
  std::string t_pag = "01";  // 01=Dinheiro
  double v_pag = 0.0;
  std::string x_pag;
};

struct NfceDocumento {
  int serie = 1;
  std::int64_t numero = 1;
  int tp_emis = 1;
  int tp_amb = 2;  // 2=Homologacao
  int c_uf = 35;
  std::string dh_emi;  // ISO-8601 local, e.g. 2026-07-23T10:00:00-03:00
  int c_nf = 0;
  Emitente emit;
  Destinatario dest;
  std::vector<ProdutoNfce> itens;
  std::vector<Pagamento> pagamentos;
  std::string inf_cpl;
  std::string id_token;     // CSC id
  std::string csc;          // CSC token for QR
  std::string chave;        // filled by build
  TotaisTributarios totais;
};

// Build access key into doc.chave and compute taxes/totals
void preparar_nfce(NfceDocumento& doc);

// Minimal NFC-e layout 4.00 XML (unsigned) for demo / unit tests
std::string gerar_xml_nfce(const NfceDocumento& doc);

// QR Code URL (NT 2015.002+) — query string without digital signature of SEFAZ online
// For offline demo we produce the base URL + chNFe + params (CSC hash optional placeholder)
std::string gerar_url_qrcode_nfce(const NfceDocumento& doc);

// SEFAZ QR host by UF (homologacao common endpoints subset)
std::string qrcode_host(const std::string& uf, int tp_amb);

}  // namespace pdvtax
