#include "pdvtax/nfce.hpp"

#include "pdvtax/strings.hpp"
#include "pdvtax/uf.hpp"
#include "pdvtax/validador.hpp"

#include <sstream>
#include <iomanip>

namespace pdvtax {
namespace {

std::string xml_escape(const std::string& s) {
  std::string out;
  out.reserve(s.size());
  for (char c : s) {
    switch (c) {
      case '&':
        out += "&amp;";
        break;
      case '<':
        out += "&lt;";
        break;
      case '>':
        out += "&gt;";
        break;
      case '"':
        out += "&quot;";
        break;
      case '\'':
        out += "&apos;";
        break;
      default:
        out.push_back(c);
    }
  }
  return out;
}

std::string money(double v) {
  std::ostringstream os;
  os << std::fixed << std::setprecision(2) << arredonda(v);
  return os.str();
}

std::string qty(double v) {
  std::ostringstream os;
  os << std::fixed << std::setprecision(4) << v;
  return os.str();
}

int parse_ano_mes(const std::string& dh_emi, int& ano, int& mes) {
  // Expect YYYY-MM-DD...
  if (dh_emi.size() < 7) {
    return -1;
  }
  ano = std::stoi(dh_emi.substr(0, 4));
  mes = std::stoi(dh_emi.substr(5, 2));
  return 0;
}

}  // namespace

void preparar_nfce(NfceDocumento& doc) {
  Crt crt = Crt::SimplesNacional;
  if (doc.emit.crt == "3") {
    crt = Crt::RegimeNormal;
  } else if (doc.emit.crt == "2") {
    crt = Crt::SimplesExcesso;
  } else if (doc.emit.crt == "4") {
    crt = Crt::Mei;
  }

  for (auto& prod : doc.itens) {
    prod.v_prod = arredonda(prod.q_com * prod.v_un_com);
    prod.trib.valor_produto = prod.v_prod;
    prod.trib.valor_desconto = prod.v_desc;
    calcular_item(prod.trib, crt);
  }

  if (!doc.itens.empty()) {
    std::vector<ItemTributavel> tmp;
    tmp.reserve(doc.itens.size());
    for (const auto& p : doc.itens) {
      tmp.push_back(p.trib);
    }
    doc.totais = somar_totais(tmp.data(), tmp.size());
  }

  int ano = 2026;
  int mes = 1;
  if (!doc.dh_emi.empty()) {
    parse_ano_mes(doc.dh_emi, ano, mes);
  }

  ChaveAcessoParams p;
  p.codigo_uf = doc.c_uf;
  p.ano = ano;
  p.mes = mes;
  p.cnpj_cpf = doc.emit.cnpj;
  p.modelo = kModeloNFCe;
  p.serie = doc.serie;
  p.numero = doc.numero;
  p.tp_emis = doc.tp_emis;
  p.codigo_numerico = doc.c_nf;
  doc.chave = gerar_chave_acesso(p);
}

std::string gerar_xml_nfce(const NfceDocumento& doc) {
  std::ostringstream xml;
  const std::string id = "NFe" + doc.chave;
  xml << "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
  xml << "<NFe xmlns=\"http://www.portalfiscal.inf.br/nfe\">";
  xml << "<infNFe Id=\"" << id << "\" versao=\"4.00\">";

  xml << "<ide>";
  xml << "<cUF>" << doc.c_uf << "</cUF>";
  xml << "<cNF>" << poem_zeros(doc.c_nf > 0 ? doc.c_nf : gerar_codigo_dfe(doc.numero), 8)
      << "</cNF>";
  xml << "<natOp>VENDA</natOp>";
  xml << "<mod>65</mod>";
  xml << "<serie>" << doc.serie << "</serie>";
  xml << "<nNF>" << doc.numero << "</nNF>";
  xml << "<dhEmi>" << xml_escape(doc.dh_emi) << "</dhEmi>";
  xml << "<tpNF>1</tpNF>";
  xml << "<idDest>1</idDest>";
  xml << "<cMunFG>" << xml_escape(doc.emit.c_mun) << "</cMunFG>";
  xml << "<tpImp>4</tpImp>";
  xml << "<tpEmis>" << doc.tp_emis << "</tpEmis>";
  if (!doc.chave.empty()) {
    xml << "<cDV>" << doc.chave.back() << "</cDV>";
  }
  xml << "<tpAmb>" << doc.tp_amb << "</tpAmb>";
  xml << "<finNFe>1</finNFe>";
  xml << "<indFinal>1</indFinal>";
  xml << "<indPres>1</indPres>";
  xml << "<procEmi>0</procEmi>";
  xml << "<verProc>PDVTax-" << "1.0.0" << "</verProc>";
  xml << "</ide>";

  xml << "<emit>";
  xml << "<CNPJ>" << only_number(doc.emit.cnpj) << "</CNPJ>";
  xml << "<xNome>" << xml_escape(doc.emit.x_nome) << "</xNome>";
  if (!doc.emit.x_fant.empty()) {
    xml << "<xFant>" << xml_escape(doc.emit.x_fant) << "</xFant>";
  }
  xml << "<enderEmit>";
  xml << "<xLgr>" << xml_escape(doc.emit.x_lgr) << "</xLgr>";
  xml << "<nro>" << xml_escape(doc.emit.nro) << "</nro>";
  xml << "<xBairro>" << xml_escape(doc.emit.x_bairro) << "</xBairro>";
  xml << "<cMun>" << xml_escape(doc.emit.c_mun) << "</cMun>";
  xml << "<xMun>" << xml_escape(doc.emit.x_mun) << "</xMun>";
  xml << "<UF>" << xml_escape(doc.emit.uf) << "</UF>";
  xml << "<CEP>" << only_number(doc.emit.cep) << "</CEP>";
  xml << "<cPais>1058</cPais><xPais>BRASIL</xPais>";
  xml << "</enderEmit>";
  xml << "<IE>" << xml_escape(doc.emit.ie) << "</IE>";
  xml << "<CRT>" << xml_escape(doc.emit.crt) << "</CRT>";
  xml << "</emit>";

  if (!doc.dest.cpf_cnpj.empty()) {
    xml << "<dest>";
    const std::string docid = only_alpha_num(doc.dest.cpf_cnpj);
    if (docid.size() > 11) {
      xml << "<CNPJ>" << docid << "</CNPJ>";
    } else {
      xml << "<CPF>" << pad_left(docid, 11, '0') << "</CPF>";
    }
    if (!doc.dest.x_nome.empty()) {
      xml << "<xNome>" << xml_escape(doc.dest.x_nome) << "</xNome>";
    }
    xml << "<indIEDest>9</indIEDest>";
    xml << "</dest>";
  }

  int n = 1;
  for (const auto& it : doc.itens) {
    xml << "<det nItem=\"" << (it.n_item > 0 ? it.n_item : n) << "\">";
    xml << "<prod>";
    xml << "<cProd>" << xml_escape(it.c_prod) << "</cProd>";
    xml << "<cEAN>" << xml_escape(it.c_ean) << "</cEAN>";
    xml << "<xProd>" << xml_escape(it.x_prod) << "</xProd>";
    xml << "<NCM>" << xml_escape(it.ncm) << "</NCM>";
    xml << "<CFOP>" << xml_escape(it.cfop) << "</CFOP>";
    xml << "<uCom>" << xml_escape(it.u_com) << "</uCom>";
    xml << "<qCom>" << qty(it.q_com) << "</qCom>";
    xml << "<vUnCom>" << money(it.v_un_com) << "</vUnCom>";
    xml << "<vProd>" << money(it.v_prod) << "</vProd>";
    xml << "<cEANTrib>" << xml_escape(it.c_ean) << "</cEANTrib>";
    xml << "<uTrib>" << xml_escape(it.u_com) << "</uTrib>";
    xml << "<qTrib>" << qty(it.q_com) << "</qTrib>";
    xml << "<vUnTrib>" << money(it.v_un_com) << "</vUnTrib>";
    if (it.v_desc > 0.0) {
      xml << "<vDesc>" << money(it.v_desc) << "</vDesc>";
    }
    xml << "<indTot>1</indTot>";
    xml << "</prod>";
    xml << "<imposto>";
    xml << "<vTotTrib>" << money(calcular_ibpt(it.trib).total_valor) << "</vTotTrib>";
    xml << "<ICMS><ICMSSN102>";
    xml << "<orig>" << static_cast<int>(it.trib.icms.origem) << "</orig>";
    xml << "<CSOSN>" << xml_escape(it.trib.icms.cst_ou_csosn) << "</CSOSN>";
    xml << "</ICMSSN102></ICMS>";
    xml << "<PIS><PISOutr>";
    xml << "<CST>" << xml_escape(it.trib.pis.cst) << "</CST>";
    xml << "<vBC>" << money(it.trib.pis.base_calculo) << "</vBC>";
    xml << "<pPIS>" << money(it.trib.pis.aliquota) << "</pPIS>";
    xml << "<vPIS>" << money(it.trib.pis.valor) << "</vPIS>";
    xml << "</PISOutr></PIS>";
    xml << "<COFINS><COFINSOutr>";
    xml << "<CST>" << xml_escape(it.trib.cofins.cst) << "</CST>";
    xml << "<vBC>" << money(it.trib.cofins.base_calculo) << "</vBC>";
    xml << "<pCOFINS>" << money(it.trib.cofins.aliquota) << "</pCOFINS>";
    xml << "<vCOFINS>" << money(it.trib.cofins.valor) << "</vCOFINS>";
    xml << "</COFINSOutr></COFINS>";
    xml << "</imposto>";
    xml << "</det>";
    ++n;
  }

  xml << "<total><ICMSTot>";
  xml << "<vBC>" << money(doc.totais.v_bc_icms) << "</vBC>";
  xml << "<vICMS>" << money(doc.totais.v_icms) << "</vICMS>";
  xml << "<vICMSDeson>0.00</vICMSDeson>";
  xml << "<vFCP>0.00</vFCP>";
  xml << "<vBCST>0.00</vBCST><vST>0.00</vST><vFCPST>0.00</vFCPST><vFCPSTRet>0.00</vFCPSTRet>";
  xml << "<vProd>" << money(doc.totais.v_prod) << "</vProd>";
  xml << "<vFrete>" << money(doc.totais.v_frete) << "</vFrete>";
  xml << "<vSeg>" << money(doc.totais.v_seg) << "</vSeg>";
  xml << "<vDesc>" << money(doc.totais.v_desc) << "</vDesc>";
  xml << "<vII>0.00</vII><vIPI>0.00</vIPI><vIPIDevol>0.00</vIPIDevol>";
  xml << "<vPIS>" << money(doc.totais.v_pis) << "</vPIS>";
  xml << "<vCOFINS>" << money(doc.totais.v_cofins) << "</vCOFINS>";
  xml << "<vOutro>" << money(doc.totais.v_outro) << "</vOutro>";
  xml << "<vNF>" << money(doc.totais.v_nf) << "</vNF>";
  xml << "<vTotTrib>" << money(doc.totais.v_tot_trib) << "</vTotTrib>";
  xml << "</ICMSTot></total>";

  xml << "<transp><modFrete>9</modFrete></transp>";

  xml << "<pag>";
  for (const auto& p : doc.pagamentos) {
    xml << "<detPag>";
    xml << "<tPag>" << xml_escape(p.t_pag) << "</tPag>";
    xml << "<vPag>" << money(p.v_pag) << "</vPag>";
    xml << "</detPag>";
  }
  if (doc.pagamentos.empty()) {
    xml << "<detPag><tPag>01</tPag><vPag>" << money(doc.totais.v_nf)
        << "</vPag></detPag>";
  }
  xml << "</pag>";

  if (!doc.inf_cpl.empty()) {
    xml << "<infAdic><infCpl>" << xml_escape(doc.inf_cpl) << "</infCpl></infAdic>";
  }

  xml << "</infNFe></NFe>";
  return xml.str();
}

std::string qrcode_host(const std::string& uf, int tp_amb) {
  const std::string u = to_upper_ascii(uf);
  // Representative public hosts (homologacao/production patterns)
  if (u == "SP") {
    return tp_amb == 1
               ? "https://www.nfce.fazenda.sp.gov.br/qrcode"
               : "https://www.homologacao.nfce.fazenda.sp.gov.br/qrcode";
  }
  if (u == "MG") {
    return tp_amb == 1 ? "https://portalsped.fazenda.mg.gov.br/portalnfce/sistema/qrcode.xhtml"
                       : "https://portalsped.fazenda.mg.gov.br/portalnfce/sistema/qrcode.xhtml";
  }
  // Generic fallback used by several UFs
  return tp_amb == 1 ? "https://www.fazenda.gov.br/nfce/qrcode"
                     : "https://homologacao.fazenda.gov.br/nfce/qrcode";
}

std::string gerar_url_qrcode_nfce(const NfceDocumento& doc) {
  const std::string host = qrcode_host(doc.emit.uf, doc.tp_amb);
  std::ostringstream url;
  url << host << "?p=" << doc.chave << "|"
      << "2"  // versao QR
      << "|" << doc.tp_amb << "|"
      << (doc.id_token.empty() ? "1" : doc.id_token);
  // CSC hash would be appended after signing with CSC; demo leaves placeholder
  if (!doc.csc.empty()) {
    url << "|CSCDEMO";
  }
  return url.str();
}

}  // namespace pdvtax
