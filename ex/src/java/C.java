package br.com.tecnicon.relatorio;

import br.com.tecnicon.enviaemail.EmailWebUtil;
import br.com.tecnicon.enviaemail.TEnviarEmail;
import br.com.tecnicon.geral.Utils;
import br.com.tecnicon.server.context.TClassLoader;
import br.com.tecnicon.server.context.TecniconLookup;
import br.com.tecnicon.server.dataset.TClientDataSet;
import br.com.tecnicon.server.dataset.TSQLDataSet;
import br.com.tecnicon.server.dataset.TSQLDataSetEmp;
import br.com.tecnicon.server.dataset.TSQLDataSetTec;
import br.com.tecnicon.server.execoes.ExcecaoTecnicon;
import br.com.tecnicon.server.interfaces.ParametrosForm;
import br.com.tecnicon.server.sessao.TVariavelSessao;
import br.com.tecnicon.server.sessao.VariavelSessao;
import br.com.tecnicon.server.tecniproc.LogoJPG;
import br.com.tecnicon.server.util.funcoes.Funcoes;
import br.com.tecnicon.utils.file.FileUtils;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ejb.Stateless;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author jose.spohr
 */
@Stateless
public class RelatorioDANFE
{

    public TSQLDataSet retornaRelatorioNFCE3(VariavelSessao vs) throws ExcecaoTecnicon, ParseException
    {
        return (vs.getParameter("VERXMLENTRADA") != null && "S".equals(vs.getParameter("VERXMLENTRADA")) && !"".equals(vs.getParameter("SXMLENTRADA")) ? retornaRelatorioNFCE3_XML(vs) : retornaDANFE(vs));
    }

    public TSQLDataSet retornaRelatorioNFCE3_XML(VariavelSessao vs) throws ExcecaoTecnicon, ParseException
    {
        String modelo = "772";

        //dataSet principal com todos campos do
        TSQLDataSetEmp dataSet = TSQLDataSetEmp.create(vs);
        dataSet.fieldDefs().clear();
        dataSet.fieldDefs().add("AREAMENSAGEMFISCAL", "FTSTRING", 100, true);
        dataSet.fieldDefs().add("EMITNOME", "FTSTRING", 60, true);
        dataSet.fieldDefs().add("EMITENDERECO", "FTSTRING", 180, true);
        dataSet.fieldDefs().add("EMITENDERECONFCE", "FTSTRING", 180, true);
        dataSet.fieldDefs().add("EMITBAIRRO", "FTSTRING", 20, true);
        dataSet.fieldDefs().add("EMITCEP", "FTSTRING", 10, true);
        dataSet.fieldDefs().add("EMITCIDADE", "FTSTRING", 30, true);
        dataSet.fieldDefs().add("EMITUF", "FTSTRING", 2, true);
        dataSet.fieldDefs().add("EMITFONE", "FTSTRING", 17, true);
        dataSet.fieldDefs().add("NNF", "FTSTRING", 11, true);
        dataSet.fieldDefs().add("SERIE", "FTSTRING", 3, true);
        dataSet.fieldDefs().add("NATOP", "FTSTRING", 60, true);
        dataSet.fieldDefs().add("TPNF", "FTSTRING", 1, true);
        dataSet.fieldDefs().add("EMITIE", "FTSTRING", 14, true);
        dataSet.fieldDefs().add("EMITIEST", "FTSTRING", 20, true);
        dataSet.fieldDefs().add("EMITCNPJ", "FTSTRING", 20, true);
        dataSet.fieldDefs().add("DESTXNOME", "FTSTRING", 60, true);
        dataSet.fieldDefs().add("DESTINFO", "FTSTRING", 1000, true);
        dataSet.fieldDefs().add("DESTINFONFCE", "FTSTRING", 1000, true);
        dataSet.fieldDefs().add("DESTCNPJ", "FTSTRING", 20, true);
        dataSet.fieldDefs().add("DESTENDERECO", "FTSTRING", 180, true);
        dataSet.fieldDefs().add("DESTBAIRRO", "FTSTRING", 60, true);
        dataSet.fieldDefs().add("DESTCIDADE", "FTSTRING", 60, true);
        dataSet.fieldDefs().add("DESTCEP", "FTSTRING", 13, true);
        dataSet.fieldDefs().add("DESTFONE", "FTSTRING", 11, true);
        dataSet.fieldDefs().add("DESTUF", "FTSTRING", 2, true);
        dataSet.fieldDefs().add("DESTIE", "FTSTRING", 14, true);
        dataSet.fieldDefs().add("DEMI", "FTSTRING", 10, true);
        dataSet.fieldDefs().add("HEMI", "FTSTRING", 10, true);
        dataSet.fieldDefs().add("DSAIDA", "FTSTRING", 10, true);
        dataSet.fieldDefs().add("HSAIDA", "FTSTRING", 10, true);
        dataSet.fieldDefs().add("ENTRCNPJ", "FTSTRING", 20, true);
        dataSet.fieldDefs().add("ENTREND", "FTSTRING", 180, true);
        dataSet.fieldDefs().add("ENTRBAIRRO", "FTSTRING", 60, true);
        dataSet.fieldDefs().add("ENTRCIDADE", "FTSTRING", 60, true);
        dataSet.fieldDefs().add("ENTRUF", "FTSTRING", 2, true);
        dataSet.fieldDefs().add("NFATUCOBR", "FTMEMO", 0, true);
        dataSet.fieldDefs().add("VBCICMSTOT", "FTDOUBLE", 10, true, 2);
        dataSet.fieldDefs().add("VICMSICMSTOT", "FTDOUBLE", 10, true, 2);
        dataSet.fieldDefs().add("VBCSTICMSTOT", "FTDOUBLE", 10, true, 2);
        dataSet.fieldDefs().add("VSTICMSTOT", "FTDOUBLE", 10, true, 2);
        dataSet.fieldDefs().add("VPRODICMSTOT", "FTDOUBLE", 10, true, 2);
        dataSet.fieldDefs().add("VFRETEICMSTOT", "FTDOUBLE", 10, true, 2);
        dataSet.fieldDefs().add("VSEGCMSTOT", "FTDOUBLE", 10, true, 2);
        dataSet.fieldDefs().add("DESCCMSTOT", "FTDOUBLE", 10, true, 2);
        dataSet.fieldDefs().add("VOUTROICMSTOT", "FTDOUBLE", 10, true, 2);
        dataSet.fieldDefs().add("VIPI", "FTDOUBLE", 15, true, 2);
        dataSet.fieldDefs().add("VLRTOTALITENS", "FTDOUBLE", 13, true, 2);
        dataSet.fieldDefs().add("VNFICMSTOT", "FTDOUBLE", 10, true, 2);
        dataSet.fieldDefs().add("TRANSPNOME", "FTSTRING", 60, true);
        dataSet.fieldDefs().add("MODFRETE", "FTSTRING", 30, true);
        dataSet.fieldDefs().add("VEICANTT", "FTSTRING", 10, true);
        dataSet.fieldDefs().add("VEICPLACA", "FTSTRING", 8, true);
        dataSet.fieldDefs().add("VEICPLACAUF", "FTSTRING", 2, true);
        dataSet.fieldDefs().add("VEICCGC", "FTSTRING", 20, true);
        dataSet.fieldDefs().add("TRANSPENDER", "FTSTRING", 180, true);
        dataSet.fieldDefs().add("TRANSPCIDADE", "FTSTRING", 60, true);
        dataSet.fieldDefs().add("TRANSPUF", "FTSTRING", 2, true);
        dataSet.fieldDefs().add("TRANSPIE", "FTSTRING", 14, true);
        dataSet.fieldDefs().add("TRANSPQVOL", "FTDOUBLE", 15, true, 2);
        dataSet.fieldDefs().add("TRANSPESP", "FTSTRING", 60, true);
        dataSet.fieldDefs().add("TRANSPMARCA", "FTSTRING", 60, true);
        dataSet.fieldDefs().add("TRANSPNUMERACAO", "FTSTRING", 10, true);
        dataSet.fieldDefs().add("TRANSPPESOB", "FTDOUBLE", 15, true, 3);
        dataSet.fieldDefs().add("TRANSPPESOL", "FTDOUBLE", 15, true, 3);
        dataSet.fieldDefs().add("EMITIM", "FTSTRING", 15, true);
        dataSet.fieldDefs().add("VBCISSQN", "FTDOUBLE", 15, true, 2);
        dataSet.fieldDefs().add("VLISSQN", "FTDOUBLE", 15, true, 2);
        dataSet.fieldDefs().add("VTOTTRIB", "FTDOUBLE", 15, true, 2);
        dataSet.fieldDefs().add("VTOTISSQN", "FTDOUBLE", 10, true, 2);
        dataSet.fieldDefs().add("CHAVE", "FTSTRING", 70, true);
        dataSet.fieldDefs().add("CHAVEBARRA", "FTSTRING", 70, true);
        dataSet.fieldDefs().add("DISPLVIA", "FTSTRING", 1, true);
        dataSet.fieldDefs().add("NUMVIA", "FTSTRING", 20, true);
        dataSet.fieldDefs().add("NUMPROTOCOLO", "FTSTRING", 160, true);
        dataSet.fieldDefs().add("STATUS", "FTSTRING", 3, true);
        dataSet.fieldDefs().add("OBSCONT", "FTSTRING", 600, true);
        dataSet.fieldDefs().add("PROTOCOLOAUTORIZA", "FTSTRING", 600, true);
        dataSet.fieldDefs().add("PROTOCOLOAUTORIZANFCE", "FTSTRING", 600, true);
        dataSet.fieldDefs().add("QRCODE", "FTSTRING", 6000, false);
        dataSet.fieldDefs().add("NOMECSTCSOSN", "FTSTRING", 10, true);
        dataSet.fieldDefs().add("IMPRIMELOGO", "FTSTRING", 1, false);
        dataSet.fieldDefs().add("IMPRIMEMARCADAGUA", "FTSTRING", 1, false);
        dataSet.fieldDefs().add("IMPRIMIRINFOEMITENTE", "FTSTRING", 3, false);
        dataSet.fieldDefs().add("TAMANHOLOGO", "FTSTRING", 30, false);
        dataSet.fieldDefs().add("INFOFISCO", "FTSTRING", 5000, false);
        dataSet.fieldDefs().add("INFOFISCOPAGDOIS", "FTSTRING", 5000, false);
        dataSet.fieldDefs().add("IMPINFOPAGDOIS", "FTSTRING", 5, false);
        dataSet.fieldDefs().add("EMAIL", "FTSTRING", 50, false);
        dataSet.fieldDefs().add("PAGE", "FTSTRING", 50, false);
        dataSet.fieldDefs().add("EXIBEEMAIL", "FTSTRING", 15, false);
        dataSet.fieldDefs().add("FAX", "FTSTRING", 15, false);
        dataSet.fieldDefs().add("EXIBEFAX", "FTSTRING", 15, false);
        dataSet.fieldDefs().add("IMPRIMECOMPROVANTEVENDAPRAZO", "FTSTRING", 1, false);
        dataSet.fieldDefs().add("ITENSNOCOMPROVANTEVENDAPRAZO", "FTSTRING", 1, false);
        dataSet.fieldDefs().add("VALORPRIMEIRAPARCELA", "FTDOUBLE", 15, false, 2);
        dataSet.fieldDefs().add("VITENSTOT", "FTDOUBLE", 10, true, 2);
        dataSet.fieldDefs().add("VDESCTOT", "FTDOUBLE", 10, true, 2);
        dataSet.fieldDefs().add("VACRETOT", "FTDOUBLE", 10, true, 2);
        dataSet.fieldDefs().add("TROCO", "FTDOUBLE", 10, true, 2);
        dataSet.fieldDefs().add("IMPITENS", "FTSTRING", 25, false);
        dataSet.fieldDefs().add("NFCESTYLEQR", "FTSTRING", 55, false);
        dataSet.fieldDefs().add("NFCESTYLEPROTDIR", "FTSTRING", 55, false);
        dataSet.fieldDefs().add("NFCESTYLEPROTCIMA", "FTSTRING", 55, false);
        dataSet.fieldDefs().add("NFS", "I", 10, false);
        dataSet.fieldDefs().add("NFE", "I", 10, false);
        dataSet.fieldDefs().add("TPVIA", "V", 20, false);
        dataSet.fieldDefs().add("CCLIFOR", "I", 10, false);
        dataSet.fieldDefs().add("PLACAINFO", "FTSTRING", 30, false);
        dataSet.fieldDefs().add("KMINFO", "FTSTRING", 30, false);
        dataSet.fieldDefs().add("URLCONSULTACHAVE", "FTSTRING", 100, false);
        dataSet.fieldDefs().add("IMPCONTINGENCIA", "FTSTRING", 2, false);
        TClientDataSet xmlEntrada = TClientDataSet.create(vs, "XMLENTRADA");
        xmlEntrada.createDataSet();
        xmlEntrada.condicao("WHERE XMLENTRADA.SXMLENTRADA = " + vs.getParameter("SXMLENTRADA"));
        xmlEntrada.open();

        xmlEntrada.first();
        while (!xmlEntrada.eof())
        {
            Document document;
            try
            {
                document = Utils.strToDoc(vs, xmlEntrada.fieldByName("XML").asString());
            } catch (ParserConfigurationException | SAXException | IOException ex)
            {
                throw new ExcecaoTecnicon(vs, "Falha na leitura do XML, verifique se o xml esta valido."
                        + "\nEfetue o download do xml no SEFAZ para realizar a manutenção\nerro:" + ex.getMessage());
            }
            Node aux = document.getElementsByTagName("infNFe").item(0);
            if (aux != null && aux.getAttributes() != null && aux.getAttributes().getNamedItem("versao") != null)
            {
                if (aux.getAttributes().getNamedItem("versao").getTextContent().equals("4.00"))
                {
                    return (TSQLDataSet) TClassLoader.execMethod("TecniconNFe4/RelatorioDANFE", "retornaRelatorioNFCE4", vs);
                } else if (!aux.getAttributes().getNamedItem("versao").getTextContent().equals("3.10") && !aux.getAttributes().getNamedItem("versao").getTextContent().equals("4.00"))
                {
                    return (TSQLDataSet) TClassLoader.execMethod("TecniconNFe2/RetornaRelatorioDANFE", "obterSQLDataSet", vs);
                }
            }

            dataSet.insert();

            ParametrosForm pf = (ParametrosForm) TecniconLookup.lookup("TecniconParametrosForm", "ParametrosFormImpl");

            if (pf.retornaRegraNegocio(vs, vs.getValor("filial"), 1172).equals("S"))
            {
                TSQLDataSetEmp cdsNFS = TSQLDataSetEmp.create(vs);
                cdsNFS.commandText("SELECT FILIAL.EMAIL, FILIAL.HPAGE"
                        + " FROM FILIAL"
                        + " WHERE FILIAL.CFILIAL=" + vs.getValor("filial"));
                cdsNFS.open();
                dataSet.fieldByName("EMAIL").asString(cdsNFS.fieldByName("EMAIL").asString());
                dataSet.fieldByName("PAGE").asString(cdsNFS.fieldByName("HPAGE").asString());
                dataSet.fieldByName("EXIBEEMAIL").asString("display:block");
            } else
            {
                dataSet.fieldByName("EMAIL").asString("");
                dataSet.fieldByName("PAGE").asString("");
                dataSet.fieldByName("EXIBEEMAIL").asString("display:none");
            }
            if (pf.retornaRegraNegocio(vs, vs.getValor("filial"), 1245).equals("S"))
            {
                TSQLDataSetEmp cdsNFS = TSQLDataSetEmp.create(vs);
                cdsNFS.commandText("SELECT FILIAL.FAX"
                        + " FROM FILIAL"
                        + " WHERE FILIAL.CFILIAL=" + vs.getValor("filial"));
                cdsNFS.open();
                dataSet.fieldByName("FAX").asString(cdsNFS.fieldByName("FAX").asString());
                dataSet.fieldByName("EXIBEFAX").asString("display:block");
            } else
            {
                dataSet.fieldByName("FAX").asString("");
                dataSet.fieldByName("EXIBEFAX").asString("display:none");
            }

            if (vs.getParameter("TPVIA") != null && !vs.getParameter("TPVIA").isEmpty())
            {
                dataSet.fieldByName("TPVIA").asString(vs.getParameter("TPVIA"));
            } else
            {
                dataSet.fieldByName("TPVIA").asString("Consumidor");
            }
            dataSet.fieldByName("CHAVEBARRA").asString(xmlEntrada.fieldByName("CHAVE").asString());
            dataSet.fieldByName("CHAVE").asString(Funcoes.formatMaskText(vs, "9999 9999 9999 9999 9999 9999 9999 9999 9999 9999 9999", xmlEntrada.fieldByName("CHAVE").asString()));

            String imprimirLogo = pf.retornaRegraNegocio(vs, vs.getValor("filial"), 81);
            dataSet.fieldByName("IMPRIMELOGO").asString(imprimirLogo);
            //se retornar vazio para LOGODANFE, deve imprimir as informações do emitente
            //se retornar com alguma informação, é pq as informações se encontram na LOGO e não precisam ser impressas
            if (LogoJPG.RETORNA_LOGO_JPG(vs.getValor("empresa"), vs.getValor("filial"), "", "LOGODANFE", ".JPG").isEmpty())
            {
                dataSet.fieldByName("IMPRIMIRINFOEMITENTE").asString("sim");
                if (modelo.equals("868"))
                {
                    dataSet.fieldByName("TAMANHOLOGO").asString("height=\"50px\"");
                } else
                {
                    dataSet.fieldByName("TAMANHOLOGO").asString("width=\"65px\"");
                }
            } else
            {
                dataSet.fieldByName("IMPRIMIRINFOEMITENTE").asString("nao");
                if (modelo.equals("868"))
                {
                    dataSet.fieldByName("TAMANHOLOGO").asString("height=\"90px\"");
                } else
                {
                    dataSet.fieldByName("TAMANHOLOGO").asString("height=\"80px\"");
                }
            }

            if (vs.getParameter("MODELOECONOMICONFCE") != null && vs.getParameter("MODELOECONOMICONFCE").equals("S"))
            {
                dataSet.fieldByName("IMPITENS").asString("display: none;");
            }

            int numVia = 1;
            dataSet.fieldByName("NUMVIA").asString(numVia + "ª VIA");

            if (vs.getParameter("NFETERCEIROS") != null && vs.getParameter("NFETERCEIROS").equals("TRUE"))
            {
                dataSet.fieldByName("IMPRIMEMARCADAGUA").asString("N");
                dataSet.fieldByName("IMPRIMELOGO").asString("N");
                dataSet.fieldByName("IMPRIMIRINFOEMITENTE").asString("sim");
                dataSet.fieldByName("TAMANHOLOGO").asString("width=\"5px\"");
            }
            if (("S").equals(dataSet.fieldByName("IMPRIMELOGO").asString()))
            {
                if (xmlEntrada.fieldByName("NFE").asString() != null)
                {
                    TSQLDataSetEmp nfOrigem = TSQLDataSetEmp.create(vs);
                    nfOrigem.commandText("SELECT NFE "
                            + " FROM NFENTRADA"
                            + " INNER JOIN MODELONF ON MODELONF.CMODELONF = NFENTRADA.CMODELONF"
                            + " WHERE MODELONF.EMITENTE = 'T'"
                            + " AND NFENTRADA.NFE = " + xmlEntrada.fieldByName("NFE").asInteger());
                    nfOrigem.open();
                    if (!nfOrigem.isEmpty())
                    {
                        dataSet.fieldByName("IMPRIMELOGO").asString("N");
                    }
                }
            }
            boolean impProtocoloLadoQR = pf.retornaRegraNegocio(vs, vs.getValor("filial"), 1517).equals("S");
            if (impProtocoloLadoQR)
            {
                dataSet.fieldByName("NFCESTYLEQR").asString("padding-left: 30px;");
                dataSet.fieldByName("NFCESTYLEPROTDIR").asString("text-align:right; padding-right: 80px;");
                dataSet.fieldByName("NFCESTYLEPROTCIMA").asString("display: none;");
            } else
            {
                dataSet.fieldByName("NFCESTYLEPROTDIR").asString("display: none;");
                dataSet.fieldByName("NFCESTYLEPROTCIMA").asString("");
            }
            //variaveis utilizadas para gerar o qrcode e para controle
            String tpEmi = "", protAutoriza = "", datahoraEmi;
            //Identificação da NF-e

            String mod = "";
            NodeList ide = document.getElementsByTagName("ide").item(0).getChildNodes();
            for (int i = 0; i < ide.getLength(); i++)
            {
                if (ide.item(i).getNodeName().equals("serie"))
                {
                    dataSet.fieldByName("SERIE").asString(ide.item(i).getTextContent());
                }
                if ("tpNF".equals(ide.item(i).getNodeName()))
                {
                    dataSet.fieldByName("TPNF").asString(ide.item(i).getTextContent());
                }
                if ("mod".equals(ide.item(i).getNodeName()))
                {
                    mod = ide.item(i).getTextContent();
                }

                //date e hora de emissão
                if ("dhEmi".equals(ide.item(i).getNodeName()))
                {
                    datahoraEmi = ide.item(i).getTextContent();
                    String[] td = datahoraEmi.substring(0, 10).split("-");
                    dataSet.fieldByName("DEMI").asString(td[2].concat("/").concat(td[1]).concat("/").concat(td[0]));
                    dataSet.fieldByName("HEMI").asString(datahoraEmi.substring(11, 19));
                }
                if ("dhSaiEnt".equals(ide.item(i).getNodeName()))
                {
                    String datahoraSai = ide.item(i).getTextContent();
                    String[] tds = datahoraSai.substring(0, 10).split("-");
                    dataSet.fieldByName("DSAIDA").asString(tds[2].concat("/").concat(tds[1]).concat("/").concat(tds[0]));
                    dataSet.fieldByName("HSAIDA").asString(datahoraSai.substring(11, 19));
                    continue;
                }
                if ("nNF".equals(ide.item(i).getNodeName()))
                {
                    String nf = Funcoes.car(ide.item(i).getTextContent(), 9, "D", "0");
                    nf = Funcoes.formatMaskText(vs, "999.999.999;0", nf);
                    dataSet.fieldByName("NNF").asString(nf);
                }
                if ("tpEmis".equals(ide.item(i).getNodeName()))
                {
                    tpEmi = ide.item(i).getTextContent();
                }
                if ("natOp".equals(ide.item(i).getNodeName()))
                {
                    dataSet.fieldByName("NATOP").asString(ide.item(i).getTextContent());
                }

                if ("xJust".equals(ide.item(i).getNodeName()) && ide.item(i).getTextContent() != null && !"".equals(ide.item(i).getTextContent()))
                {
                    dataSet.fieldByName("IMPCONTINGENCIA").asString("S");

                } else
                {
                    dataSet.fieldByName("IMPCONTINGENCIA").asString("N");
                }
            }

            //Identificação do emitente da NF-e
            NodeList emit = document.getElementsByTagName("emit").item(0).getChildNodes();
            if (emit != null)
            {
                for (int i = 0; i < emit.getLength(); i++)
                {
                    if (emit.item(i).getNodeName().equals("CPF"))
                    {
                        dataSet.fieldByName("EMITCNPJ").asString(Funcoes.formatMaskText(vs, "999.999.999-99;0", emit.item(i).getTextContent()));
                    }
                    if (emit.item(i).getNodeName().equals("CNPJ"))
                    {
                        dataSet.fieldByName("EMITCNPJ").asString(Funcoes.formatMaskText(vs, "99.999.999/9999-99;0", emit.item(i).getTextContent()));
                    }
                    if (emit.item(i).getNodeName().equals("xNome"))
                    {
                        dataSet.fieldByName("EMITNOME").asString(emit.item(i).getTextContent());
                    }
                    if (emit.item(i).getNodeName().equals("xFant"))
                    {
                    }
                    if (emit.item(i).getNodeName().equals("IE"))
                    {
                        dataSet.fieldByName("EMITIE").asString(emit.item(i).getTextContent());
                    }
                    if (emit.item(i).getNodeName().equals("IEST"))
                    {
                        dataSet.fieldByName("EMITIEST").asString(emit.item(i).getTextContent());
                    }
                    if (emit.item(i).getNodeName().equals("IM"))
                    {
                        dataSet.fieldByName("EMITIM").asString(emit.item(i).getTextContent());
                    }
                }
            }
            //Endereço do emitente
            String ufEmitente = "";
            NodeList enderEmit = document.getElementsByTagName("enderEmit").item(0).getChildNodes();
            if (enderEmit != null)
            {
                for (int i = 0; i < enderEmit.getLength(); i++)
                {
                    if (enderEmit.item(i).getNodeName().equals("xLgr"))
                    {
                        dataSet.fieldByName("EMITENDERECO").asString(enderEmit.item(i).getTextContent());
                        dataSet.fieldByName("EMITENDERECONFCE").asString(enderEmit.item(i).getTextContent());
                    }
                    if (enderEmit.item(i).getNodeName().equals("nro"))
                    {
                        dataSet.fieldByName("EMITENDERECO").asString(dataSet.fieldByName("EMITENDERECO").asString().concat(" - ")
                                .concat(enderEmit.item(i).getTextContent()));
                        dataSet.fieldByName("EMITENDERECONFCE").asString(dataSet.fieldByName("EMITENDERECONFCE").asString().concat(" - ")
                                .concat(enderEmit.item(i).getTextContent()));
                    }
                    if (enderEmit.item(i).getNodeName().equals("xCpl"))
                    {
                        if (!enderEmit.item(i).getTextContent().isEmpty())
                        {
                            dataSet.fieldByName("EMITENDERECO").asString(dataSet.fieldByName("EMITENDERECO").asString().concat(" ")
                                    .concat(enderEmit.item(i).getTextContent()));
                        }
                    }
                    if (enderEmit.item(i).getNodeName().equals("xBairro"))
                    {
                        dataSet.fieldByName("EMITENDERECO").asString(dataSet.fieldByName("EMITENDERECO").asString().concat(" ")
                                .concat(enderEmit.item(i).getTextContent()));
                        dataSet.fieldByName("EMITENDERECONFCE").asString(dataSet.fieldByName("EMITENDERECONFCE").asString().concat(" ")
                                .concat(enderEmit.item(i).getTextContent()));
                    }
                    if (enderEmit.item(i).getNodeName().equals("cMun"))
                    {
                    }
                    if (enderEmit.item(i).getNodeName().equals("xMun"))
                    {
                        dataSet.fieldByName("EMITENDERECO").asString(dataSet.fieldByName("EMITENDERECO").asString().concat(", ")
                                .concat(enderEmit.item(i).getTextContent()));
                        dataSet.fieldByName("EMITENDERECONFCE").asString(dataSet.fieldByName("EMITENDERECONFCE").asString().concat("\n")
                                .concat(enderEmit.item(i).getTextContent()));
                    }
                    if (enderEmit.item(i).getNodeName().equals("UF"))
                    {
                        dataSet.fieldByName("EMITENDERECO").asString(dataSet.fieldByName("EMITENDERECO").asString().concat(" - ")
                                .concat(enderEmit.item(i).getTextContent()));
                        dataSet.fieldByName("EMITENDERECONFCE").asString(dataSet.fieldByName("EMITENDERECONFCE").asString().concat(" - ")
                                .concat(enderEmit.item(i).getTextContent()));
                    }
                    if (enderEmit.item(i).getNodeName().equals("CEP"))
                    {
                        dataSet.fieldByName("EMITCEP").asString(enderEmit.item(i).getTextContent());
                    }

                    if ("xMun".equals(enderEmit.item(i).getNodeName()))
                    {
                        dataSet.fieldByName("EMITCIDADE").asString(enderEmit.item(i).getTextContent());
                    }
                    if (enderEmit.item(i).getNodeName().equals("fone"))
                    {
                        dataSet.fieldByName("EMITFONE").asString(enderEmit.item(i).getTextContent());
                    }

                    if (enderEmit.item(i).getNodeName().equals("UF"))
                    {
                        ufEmitente = enderEmit.item(i).getTextContent();
                    }
                    if ("fone".equals(enderEmit.item(i).getNodeName()))
                    {
                        dataSet.fieldByName("EMITFONE").asString(enderEmit.item(i).getTextContent());
                    }
                }
            }
            switch (ufEmitente)
            {
                case "AL":
                    dataSet.fieldByName("URLCONSULTACHAVE").asString("http://nfce.sefaz.al.gov.br/consultaNFCe.htm");
                    break;
                default:
                    dataSet.fieldByName("URLCONSULTACHAVE").asString("www.nfe.fazenda.gov.br/portal");
            }

            //Identificação do Destinatário da NF-e
            Node tagdest = document.getElementsByTagName("dest").item(0);
            StringBuilder auxDestCli = new StringBuilder("CONSUMIDOR");
            StringBuilder auxDestNFCe = new StringBuilder("<ce>CONSUMIDOR</ce>");
            if (tagdest != null)
            {
                NodeList dest = tagdest.getChildNodes();
                auxDestCli.append("\r\n");
                auxDestNFCe.append("\n");
                for (int i = 0; i < dest.getLength(); i++)
                {
                    if (dest.item(i).getNodeName().equals("CNPJ"))
                    {
                        auxDestCli.append("CNPJ: ".concat(Funcoes.formatMaskText(vs, "99.999.999/9999-99;0", dest.item(i).getTextContent())));
                        auxDestNFCe.append("<ce>CNPJ: ".concat(Funcoes.formatMaskText(vs, "99.999.999/9999-99;0", dest.item(i).getTextContent())));
                        dataSet.fieldByName("DESTCNPJ").asString(Funcoes.formatMaskText(vs, "99.999.999/9999-99;0", dest.item(i).getTextContent()));
                    }
                    if (dest.item(i).getNodeName().equals("CPF"))
                    {
                        auxDestCli.append("CPF: ".concat(Funcoes.formatMaskText(vs, "999.999.999-99;0", dest.item(i).getTextContent())));
                        auxDestNFCe.append("<ce>CPF: ".concat(Funcoes.formatMaskText(vs, "999.999.999-99;0", dest.item(i).getTextContent())));
                        dataSet.fieldByName("DESTCNPJ").asString(Funcoes.formatMaskText(vs, "999.999.999-99;0", dest.item(i).getTextContent()));
                    }
                    if (dest.item(i).getNodeName().equals("idEstrangeiro"))
                    {
                        auxDestCli.append("ID Estrangeiro: ".concat(dest.item(i).getTextContent()));
                        auxDestNFCe.append("<ce>ID Estrangeiro: ".concat(dest.item(i).getTextContent()));
                        dataSet.fieldByName("DESTCNPJ").asString(dest.item(i).getTextContent());
                    }
                    if (dest.item(i).getNodeName().equals("xNome"))
                    {
                        auxDestCli.append(" ").append(dest.item(i).getTextContent()).append("</ce>\r\n");
                        dataSet.fieldByName("DESTXNOME").asString(dest.item(i).getTextContent());
                    }
                    if ("IE".equals(dest.item(i).getNodeName()))
                    {
                        dataSet.fieldByName("DESTIE").asString(dest.item(i).getTextContent());
                    }
                }
            }
            //Endereço do Destinatário da NF-e
//        NodeList enderDest = (document.getElementsByTagName("enderDest") == null ? null : document.getElementsByTagName("enderDest").item(0).getChildNodes());
            StringBuilder auxDestEnde = new StringBuilder();
            if (document.getElementsByTagName("enderDest") != null && document.getElementsByTagName("enderDest").item(0) != null)
            {
                NodeList enderDest = document.getElementsByTagName("enderDest").item(0).getChildNodes();

                auxDestNFCe.append("\n");
                for (int i = 0; i < enderDest.getLength(); i++)
                {
                    if (enderDest.item(i).getNodeName().equals("xLgr"))
                    {
                        auxDestEnde.append(enderDest.item(i).getTextContent());
                        auxDestNFCe.append("<ce>").append(enderDest.item(i).getTextContent());
                        dataSet.fieldByName("DESTENDERECO").asString(enderDest.item(i).getTextContent());
                    }
                    if (enderDest.item(i).getNodeName().equals("nro"))
                    {
                        auxDestEnde.append(", ").append(enderDest.item(i).getTextContent());
                        auxDestNFCe.append(", ").append(enderDest.item(i).getTextContent());
                        dataSet.fieldByName("DESTENDERECO").asString(dataSet.fieldByName("DESTENDERECO").asString() + ", " + enderDest.item(i).getTextContent());
                    }
                    if ("xCpl".equals(enderDest.item(i).getNodeName()))
                    {
                        dataSet.fieldByName("DESTENDERECO").asString(dataSet.fieldByName("DESTENDERECO").asString() + " " + enderDest.item(i).getTextContent());
                        continue;
                    }
                    if (enderDest.item(i).getNodeName().equals("xBairro"))
                    {
                        auxDestEnde.append(", ").append(enderDest.item(i).getTextContent());
                        auxDestNFCe.append(", ").append(enderDest.item(i).getTextContent());
                        dataSet.fieldByName("DESTBAIRRO").asString(enderDest.item(i).getTextContent());
                    }
                    if (enderDest.item(i).getNodeName().equals("xMun"))
                    {
                        auxDestEnde.append(", ").append(enderDest.item(i).getTextContent());
                        auxDestNFCe.append(", ").append(enderDest.item(i).getTextContent()).append("</ce>");
                        dataSet.fieldByName("DESTCIDADE").asString(enderDest.item(i).getTextContent());
                    }
                    if (enderDest.item(i).getNodeName().equals("UF"))
                    {
                        dataSet.fieldByName("DESTUF").asString(enderDest.item(i).getTextContent());
                    }
                    if (enderDest.item(i).getNodeName().equals("CEP"))
                    {
                        dataSet.fieldByName("DESTCEP").asString(Funcoes.formatMaskText(vs, "99999-999", enderDest.item(i).getTextContent()));
                    }
                    if (enderDest.item(i).getNodeName().equals("fone"))
                    {
                        dataSet.fieldByName("DESTFONE").asString(enderDest.item(i).getTextContent());
                    }
                }
            }

            if (mod.equals("65") && document.getElementsByTagName("entrega") != null && document.getElementsByTagName("entrega").item(0) != null)
            {
                auxDestEnde.setLength(0);
                auxDestEnde.append("\n");
                NodeList enderEntrega = document.getElementsByTagName("entrega").item(0).getChildNodes();
                for (int i = 0; i < enderEntrega.getLength(); i++)
                {
                    if (enderEntrega.item(i).getNodeName().equals("xLgr"))
                    {
                        auxDestEnde.append(enderEntrega.item(i).getTextContent());
                        dataSet.fieldByName("DESTENDERECO").asString(enderEntrega.item(i).getTextContent());
                    }
                    if (enderEntrega.item(i).getNodeName().equals("nro"))
                    {
                        auxDestEnde.append(", ").append(enderEntrega.item(i).getTextContent());
                        dataSet.fieldByName("DESTENDERECO").asString(dataSet.fieldByName("DESTENDERECO").asString() + ", " + enderEntrega.item(i).getTextContent());
                    }
                    if ("xCpl".equals(enderEntrega.item(i).getNodeName()))
                    {
                        auxDestEnde.append(", ").append(enderEntrega.item(i).getTextContent());
                        dataSet.fieldByName("DESTENDERECO").asString(dataSet.fieldByName("DESTENDERECO").asString() + " " + enderEntrega.item(i).getTextContent());
                        continue;
                    }
                    if (enderEntrega.item(i).getNodeName().equals("xBairro"))
                    {
                        auxDestEnde.append(", ").append(enderEntrega.item(i).getTextContent());
                        dataSet.fieldByName("DESTBAIRRO").asString(enderEntrega.item(i).getTextContent());
                    }
                    if (enderEntrega.item(i).getNodeName().equals("xMun"))
                    {
                        auxDestEnde.append("\n").append(enderEntrega.item(i).getTextContent());
                        dataSet.fieldByName("DESTCIDADE").asString(enderEntrega.item(i).getTextContent());
                    }
                    if (enderEntrega.item(i).getNodeName().equals("UF"))
                    {
                        auxDestEnde.append(" - ").append(enderEntrega.item(i).getTextContent());
                        dataSet.fieldByName("DESTUF").asString(enderEntrega.item(i).getTextContent());
                    }
                }
            }

            aux = document.getElementsByTagName("total").item(0);
            if (aux != null)
            {
                NodeList total = aux.getChildNodes();
                for (int j = 0; j < total.getLength(); j++)
                {
                    if ("ICMSTot".equals(total.item(j).getNodeName()))
                    {
                        NodeList toticms = total.item(j).getChildNodes();
                        for (int k = 0; k < toticms.getLength(); k++)
                        {
                            if ("vBC".equals(toticms.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("VBCICMSTOT").asString(toticms.item(k).getTextContent());
                                continue;
                            }
                            if ("vICMS".equals(toticms.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("VICMSICMSTOT").asString(toticms.item(k).getTextContent());
                                continue;
                            }
                            if ("vBCST".equals(toticms.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("VBCSTICMSTOT").asString(toticms.item(k).getTextContent());
                                continue;
                            }
                            if ("vST".equals(toticms.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("VSTICMSTOT").asString(toticms.item(k).getTextContent());
                                continue;
                            }
                            if ("vProd".equals(toticms.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("VPRODICMSTOT").asString(toticms.item(k).getTextContent());
                                continue;
                            }
                            if ("vFrete".equals(toticms.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("VFRETEICMSTOT").asString(toticms.item(k).getTextContent());
                                continue;
                            }
                            if ("vSeg".equals(toticms.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("VSEGCMSTOT").asString(toticms.item(k).getTextContent());
                                continue;
                            }
                            if ("vDesc".equals(toticms.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("DESCCMSTOT").asString(toticms.item(k).getTextContent());
                                continue;
                            }
                            if ("vIPI".equals(toticms.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("VIPI").asString(toticms.item(k).getTextContent());
                                continue;
                            }
                            if ("vOutro".equals(toticms.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("VOUTROICMSTOT").asString(toticms.item(k).getTextContent());
                            }
                        }
                    }
                    if ("ISSQNtot".equals(total.item(j).getNodeName()))
                    {
                        NodeList totissqn = total.item(j).getChildNodes();
                        for (int k = 0; k < totissqn.getLength(); k++)
                        {
                            if ("vBC".equals(totissqn.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("VBCISSQN").asString(totissqn.item(k).getTextContent());
                                continue;
                            }
                            if ("vISS".equals(totissqn.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("VTOTISSQN").asString(totissqn.item(k).getTextContent());
                                continue;
                            }
                            if ("vServ".equals(totissqn.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("VLISSQN").asString(totissqn.item(k).getTextContent());
                            }
                        }
                    }
                }
            }
            aux = document.getElementsByTagName("transp").item(0);
            if (aux != null)
            {
                NodeList transp = aux.getChildNodes();
                for (int j = 0; j < transp.getLength(); j++)
                {
                    if ("modFrete".equals(transp.item(j).getNodeName()))
                    {
                        dataSet.fieldByName("MODFRETE").asString(transp.item(j).getTextContent());
                        switch (dataSet.fieldByName("MODFRETE").asString())
                        {
                            case "0": //CIF
                                dataSet.fieldByName("MODFRETE").asString(dataSet.fieldByName("MODFRETE").asString() + " - EMITENTE");
                                break;
                            case "1": //FOB
                                dataSet.fieldByName("MODFRETE").asString(dataSet.fieldByName("MODFRETE").asString() + " - DESTINATÁRIO/REMETENTE");
                                break;
                            case "2": //TERCEIROS
                                dataSet.fieldByName("MODFRETE").asString(dataSet.fieldByName("MODFRETE").asString() + " - TERCEIROS");
                                break;
                            case "9": //SEM FRETE
                                dataSet.fieldByName("MODFRETE").asString(dataSet.fieldByName("MODFRETE").asString() + " - SEM FRETE");
                                break;
                            default: //DEFAULT - FOB
                                dataSet.fieldByName("MODFRETE").asString(dataSet.fieldByName("MODFRETE").asString() + " - DESTINATÁRIO/REMETENTE");
                                break;
                        }
                        continue;
                    }
                    if ("transporta".equals(transp.item(j).getNodeName()))
                    {
                        NodeList transporta = transp.item(j).getChildNodes();
                        for (int k = 0; k < transporta.getLength(); k++)
                        {
                            if ("CPF".equals(transporta.item(k).getNodeName()) || "CNPJ".equals(transporta.item(k).getNodeName()))
                            {
                                if ("CPF".equals(transporta.item(k).getNodeName()))
                                {
                                    dataSet.fieldByName("VEICCGC").asString(Funcoes.formatMaskText(vs, "999.999.999-99;0", transporta.item(k).getTextContent()));
                                } else
                                {
                                    dataSet.fieldByName("VEICCGC").asString(Funcoes.formatMaskText(vs, "99.999.999/9999-99;0", transporta.item(k).getTextContent()));
                                }
                                continue;
                            }
                            if ("xNome".equals(transporta.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("TRANSPNOME").asString(transporta.item(k).getTextContent());
                                continue;
                            }
                            if ("xEnder".equals(transporta.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("TRANSPENDER").asString(transporta.item(k).getTextContent());
                                continue;
                            }
                            if ("xMun".equals(transporta.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("TRANSPCIDADE").asString(transporta.item(k).getTextContent());
                                continue;
                            }
                            if ("UF".equals(transporta.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("TRANSPUF").asString(transporta.item(k).getTextContent());
                                continue;
                            }
                            if ("IE".equals(transporta.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("TRANSPIE").asString(transporta.item(k).getTextContent());
                            }
                        }
                    }

                    if ("vol".equals(transp.item(j).getNodeName()))
                    {
                        NodeList vol = transp.item(j).getChildNodes();
                        for (int k = 0; k < vol.getLength(); k++)
                        {
                            if ("qVol".equals(vol.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("TRANSPQVOL").asString(vol.item(k).getTextContent());
                                continue;
                            }
                            if ("nVol".equals(vol.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("TRANSPNUMERACAO").asString(vol.item(k).getTextContent());
                                continue;
                            }
                            if ("marca".equals(vol.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("TRANSPMARCA").asString(vol.item(k).getTextContent());
                                continue;
                            }
                            if ("esp".equals(vol.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("TRANSPESP").asString(vol.item(k).getTextContent());
                                continue;
                            }
                            if ("nVol".equals(vol.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("TRANSPNUMERACAO").asString(vol.item(k).getTextContent());
                                continue;
                            }
                            if ("pesoL".equals(vol.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("TRANSPPESOL").asString(vol.item(k).getTextContent());
                                continue;
                            }
                            if ("pesoB".equals(vol.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("TRANSPPESOB").asString(vol.item(k).getTextContent());
                            }
                        }
                    }
                    if ("veicTransp".equals(transp.item(j).getNodeName()))
                    {
                        NodeList VeicTransp = transp.item(j).getChildNodes();
                        for (int k = 0; k < VeicTransp.getLength(); k++)
                        {
                            if ("RNTC".equals(VeicTransp.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("VEICANTT").asString(VeicTransp.item(k).getTextContent());
                                continue;
                            }
                            if ("placa".equals(VeicTransp.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("VEICPLACA").asString(VeicTransp.item(k).getTextContent());
                                continue;
                            }
                            if ("UF".equals(VeicTransp.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("VEICPLACAUF").asString(VeicTransp.item(k).getTextContent());
                            }
                        }
                    }
                }
            }
            String nfat = "";
            aux = document.getElementsByTagName("cobr").item(0);
            if (aux != null)
            {
                NodeList cobr = aux.getChildNodes();
                int cont = 0;
                for (int j = 0; j < cobr.getLength(); j++)
                {
                    if ("fat".equals(cobr.item(j).getNodeName()))
                    {
                        NodeList fat = cobr.item(j).getChildNodes();
                        for (int k = 0; k < fat.getLength(); k++)
                        {
                            if ("nFat".equals(fat.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("NFATUCOBR").asString("");
                                nfat = fat.item(k).getTextContent();
                            }
                        }
                    }
                    if ("dup".equals(cobr.item(j).getNodeName()))
                    {
                        NodeList dup = cobr.item(j).getChildNodes();
                        for (int k = 0; k < dup.getLength(); k++)
                        {
                            if ("nDup".equals(dup.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("NFATUCOBR").asString(dataSet.fieldByName("NFATUCOBR").asString() + nfat + "/" + dup.item(k).getTextContent());
                                continue;
                            }
                            if ("dVenc".equals(dup.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("NFATUCOBR").asString(dataSet.fieldByName("NFATUCOBR").asString() + " " + dup.item(k).getTextContent().substring(8, 10) + "/"
                                        + dup.item(k).getTextContent().substring(5, 7) + "/" + dup.item(k).getTextContent().substring(0, 4));
                                continue;
                            }
                            if ("vDup".equals(dup.item(k).getNodeName()))
                            {
                                String valor = dup.item(k).getTextContent();
                                double dbValor = Funcoes.strToDouble(valor);
                                dataSet.fieldByName("NFATUCOBR").asString(dataSet.fieldByName("NFATUCOBR").asString() + " R$ " + Funcoes.formatFloat(",##0.00", dbValor) + " &nbsp; &nbsp; ");
                                cont++;
                                if (cont == 4)
                                {
                                    cont = 0;
                                    dataSet.fieldByName("NFATUCOBR").asString(dataSet.fieldByName("NFATUCOBR").asString() + "\r\n");
                                }
                            }
                        }
                    }
                }
            }
            aux = document.getElementsByTagName("infAdic").item(0);
            if (aux != null && "infAdic".equals(aux.getNodeName()))
            {
                NodeList InfAdic = aux.getChildNodes();
                for (int j = 0; j < InfAdic.getLength(); j++)
                {
                    if ("infCpl".equals(InfAdic.item(j).getNodeName()))
                    {
//                        if (xmlEntrada.fieldByName("NFESTATUS").asString().equals("C") || xmlEntrada.fieldByName("NFESTATUS").asString().equals("D"))
//                        {
//                            dataSet.fieldByName("INFOFISCO").asString(InfAdic.item(j).getTextContent() + "CHAVE DE ACESSO:" + dataSet.fieldByName("CHAVE").asString());
//                        } else
//                        {
                        dataSet.fieldByName("INFOFISCO").asString(InfAdic.item(j).getTextContent());
//                        }
                    }
                    if ("infAdFisco".equals(InfAdic.item(j).getNodeName()))
                    {
                        dataSet.fieldByName("AREAMENSAGEMFISCAL").asString(InfAdic.item(j).getTextContent().replace("$$", "\n").trim());
                        if (InfAdic.item(j).getTextContent().contains("|"))
                        {
                            dataSet.fieldByName("INFOFISCO").asString(dataSet.fieldByName("INFOFISCO").asString() + "\n" + InfAdic.item(j).getTextContent().replace("|", "\n").trim());
                        } else
                        {
                            dataSet.fieldByName("INFOFISCO").asString(dataSet.fieldByName("INFOFISCO").asString() + "\n" + InfAdic.item(j).getTextContent().replace("&&", "\n").trim());
                        }
                    }
                    if ("obsCont".equals(InfAdic.item(j).getNodeName()))
                    {
                        String obsCont = addEspacoStr(InfAdic.item(j).getTextContent(), " ", 48);
                        dataSet.fieldByName("OBSCONT").asString(dataSet.fieldByName("OBSCONT").asString().concat(obsCont.concat("\n\r")));
                    }
                }
            }

            dataSet.fieldByName("NOMECSTCSOSN").asString("CST");
            aux = document.getElementsByTagName("det").item(0);
            if (aux != null)
            {
                NodeList det = aux.getChildNodes();
                for (int i = 0; i < det.getLength(); i++)
                {
                    if (det.item(i).getNodeName().equals("imposto"))
                    {
                        NodeList imp = det.item(i).getChildNodes();
                        for (int ae = 0; ae < imp.getLength(); ae++)
                        {
                            if (imp.item(ae).getNodeName().startsWith("ICMS"))
                            {
                                NodeList imp1 = imp.item(ae).getChildNodes();
                                for (int j = 0; j < imp1.getLength(); j++)
                                {
                                    if (imp1.item(j).getNodeName().startsWith("ICMS"))
                                    {
                                        NodeList imp2 = imp1.item(j).getChildNodes();
                                        for (int k = 0; k < imp2.getLength(); k++)
                                        {
                                            if (imp2.item(k).getNodeName().contains("CSOSN"))
                                            {
                                                dataSet.fieldByName("NOMECSTCSOSN").asString("CSOSN");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (auxDestCli.toString().equals("CONSUMIDOR"))
            {
                auxDestEnde.append(" NÃO IDENTIFICADO");
                auxDestNFCe.append(" NÃO IDENTIFICADO");
            }

            dataSet.fieldByName("DESTINFO").asString(auxDestCli.toString() + auxDestEnde.toString());
            dataSet.fieldByName("DESTINFONFCE").asString(auxDestNFCe.toString());

            aux = document.getElementsByTagName("total").item(0);
            String vnf = "", vicms = "", vprod = "", vdesc = "";
            double vacres = 0.0;
            if (aux != null)
            {
                NodeList total = aux.getChildNodes();
                for (int i = 0; i < total.getLength(); i++)
                {

                    if (total.item(i).getNodeName().equals("ICMSTot"))
                    {
                        NodeList vTrib = total.item(i).getChildNodes();
                        for (int j = 0; j < vTrib.getLength(); j++)
                        {
                            if (vTrib.item(j).getNodeName().equals("vTotTrib"))
                            {
                                dataSet.fieldByName("VTOTTRIB").asDouble(Funcoes.strToDouble(vTrib.item(j).getTextContent()));
                            }
                            if (vTrib.item(j).getNodeName().equals("vProd"))
                            {
                                dataSet.fieldByName("VLRTOTALITENS").asDouble(Funcoes.strToDouble(vTrib.item(j).getTextContent()));
                            }
                            if ("vNF".equals(vTrib.item(j).getNodeName()))
                            {
                                vnf = vTrib.item(j).getTextContent();
                            }
                            if ("vICMS".equals(vTrib.item(j).getNodeName()))
                            {
                                vicms = vTrib.item(j).getTextContent();
                            }
                            if ("vProd".equals(vTrib.item(j).getNodeName()))
                            {
                                vprod = vTrib.item(j).getTextContent();
                            }
                            if ("vDesc".equals(vTrib.item(j).getNodeName()))
                            {
                                vdesc = vTrib.item(j).getTextContent();
                            }
                            if ("vFrete".equals(vTrib.item(j).getNodeName()) && !vTrib.item(j).getTextContent().isEmpty())
                            {
                                vacres += Double.parseDouble(vTrib.item(j).getTextContent());
                            }
                            if ("vSeg".equals(vTrib.item(j).getNodeName()) && !vTrib.item(j).getTextContent().isEmpty())
                            {
                                vacres += Double.parseDouble(vTrib.item(j).getTextContent());
                            }
                            if ("vOutro".equals(vTrib.item(j).getNodeName()) && !vTrib.item(j).getTextContent().isEmpty())
                            {
                                vacres += Double.parseDouble(vTrib.item(j).getTextContent());
                            }
                        }
                    }
                }
            }

            aux = document.getElementsByTagName("entrega").item(0);
            if (aux != null)
            {
                NodeList entr = aux.getChildNodes();
                for (int j = 0; j < entr.getLength(); j++)
                {
                    if ("CPF".equals(entr.item(j).getNodeName()) || "CNPJ".equals(entr.item(j).getNodeName()))
                    {
                        dataSet.fieldByName("ENTRCNPJ").asString(entr.item(j).getTextContent());
                        if ("CPF".equals(entr.item(j).getNodeName()))
                        {
                            dataSet.fieldByName("ENTRCNPJ").asString(Funcoes.formatMaskText(vs, "999.999.999-99;0", entr.item(j).getTextContent()));
                        } else
                        {
                            dataSet.fieldByName("ENTRCNPJ").asString(Funcoes.formatMaskText(vs, "99.999.999/9999-99;0", entr.item(j).getTextContent()));
                        }
                        continue;
                    }
                    if ("xLgr".equals(entr.item(j).getNodeName()))
                    {
                        dataSet.fieldByName("ENTREND").asString(entr.item(j).getTextContent());
                        continue;
                    }
                    if ("nro".equals(entr.item(j).getNodeName()))
                    {
                        dataSet.fieldByName("ENTREND").asString(dataSet.fieldByName("ENTREND").asString() + ", " + entr.item(j).getTextContent());
                        continue;
                    }
                    if ("xCpl".equals(entr.item(j).getNodeName()))
                    {
                        dataSet.fieldByName("ENTREND").asString(dataSet.fieldByName("ENTREND").asString() + " " + entr.item(j).getTextContent());
                        continue;
                    }
                    if ("xBairro".equals(entr.item(j).getNodeName()))
                    {
                        dataSet.fieldByName("ENTRBAIRRO").asString(entr.item(j).getTextContent());
                        continue;
                    }
                    if ("xMun".equals(entr.item(j).getNodeName()))
                    {
                        dataSet.fieldByName("ENTRCIDADE").asString(entr.item(j).getTextContent());
                        continue;
                    }
                    if ("UF".equals(entr.item(j).getNodeName()))
                    {
                        dataSet.fieldByName("ENTRUF").asString(entr.item(j).getTextContent());
                    }
                }
            }
            if ("".equals(dataSet.fieldByName("ENTRCNPJ").asString()))
            {
                dataSet.fieldByName("ENTRCNPJ").asString(dataSet.fieldByName("DESTCNPJ").asString());
            }
            if ("".equals(dataSet.fieldByName("ENTREND").asString()))
            {
                dataSet.fieldByName("ENTREND").asString(dataSet.fieldByName("DESTENDERECO").asString());
            }
            if ("".equals(dataSet.fieldByName("ENTRBAIRRO").asString()))
            {
                dataSet.fieldByName("ENTRBAIRRO").asString(dataSet.fieldByName("DESTBAIRRO").asString());
            }
            if ("".equals(dataSet.fieldByName("ENTRCIDADE").asString()))
            {
                dataSet.fieldByName("ENTRCIDADE").asString(dataSet.fieldByName("DESTCIDADE").asString());
            }
            if ("".equals(dataSet.fieldByName("ENTRUF").asString()))
            {
                dataSet.fieldByName("ENTRUF").asString(dataSet.fieldByName("DESTUF").asString());
            }
            dataSet.fieldByName("DISPLVIA").asString(vs.getParameter("DISPLVIA"));
            //Informação do Protocolo de Autorização
            aux = document.getElementsByTagName("infProt").item(0);
            if (aux != null)
            {
                NodeList infProt = aux.getChildNodes();
                for (int i = 0; i < infProt.getLength(); i++)
                {
                    if ("nProt".equals(infProt.item(i).getNodeName()))
                    {
                        protAutoriza = infProt.item(i).getTextContent();
                    }
                }
            }
            String dtRec = "", hRec = "";
            aux = document.getElementsByTagName("dhRecbto").item(0);
            if (aux != null)
            {
                String dthrec = aux.getTextContent();
                String[] td = dthrec.substring(0, 10).split("-");
                dtRec = td[2].concat("/").concat(td[1]).concat("/").concat(td[0]);
                hRec = dthrec.substring(11, 19);
            }

            dataSet.fieldByName("NUMPROTOCOLO").asString(protAutoriza + (!dtRec.isEmpty() ? " - " + dtRec + " " + hRec : ""));

            NodeList tagNfRef = document.getElementsByTagName("NFref");
            String nfReferenciados = "";
            for (int it = 0; it < tagNfRef.getLength(); it++)
            {
                aux = document.getElementsByTagName("NFref").item(it);
                if (aux != null)
                {
                    NodeList infNf = aux.getChildNodes();

                    for (int i = 0; i < infNf.getLength(); i++)
                    {
                        if ("refNFe".equals(infNf.item(i).getNodeName()))
                        {
                            String chaveNFe = infNf.item(i).getTextContent();
//                            TSQLDataSetEmp sdsNota = TSQLDataSetEmp.create(vs);
//                            if (seq_nfe != null && !seq_nfe.isEmpty())
//                            {
//                                sdsNota.commandText("SELECT NFSAIDA.NF FROM CHAVENFE "
//                                        + "  INNER JOIN NFSAIDA ON (NFSAIDA.NFS = CHAVENFE.NFS)"
//                                        + "  WHERE CHAVENFE.CHAVE = '" + chaveNFe + "'");
//                            } else
//                            {
//                                sdsNota.commandText("SELECT NFENTRADA.NF FROM CHAVENFET"
//                                        + " INNER JOIN NFENTRADA ON (NFENTRADA.NFE = CHAVENFET.NFE)"
//                                        + " WHERE CHAVENFET.CHAVE = '" + chaveNFe + "'");
//                            }
//                            sdsNota.open();
//                            nfReferenciados = nfReferenciados.concat("$$Ref. NF-e ").concat(chaveNFe);
//                            if (!sdsNota.isEmpty())
//                            {
//                                nfReferenciados = nfReferenciados.concat(" NF ").concat(sdsNota.fieldByName("NF").asString());
//                            }
                        } else if ("refNF".equals(infNf.item(i).getNodeName()))
                        {
                            String sequenciaInfo = "{NUMNF}{AAMM}";
                            NodeList infRefNf = infNf.item(i).getChildNodes();
                            for (int j = 0; j < infRefNf.getLength(); j++)
                            {
                                if ("nNF".equals(infRefNf.item(j).getNodeName()))
                                {
                                    sequenciaInfo = sequenciaInfo.replace("{NUMNF}", "$$Ref. NF " + infRefNf.item(j).getTextContent());
                                }
                                if ("AAMM".equals(infRefNf.item(j).getNodeName()))
                                {
                                    sequenciaInfo = sequenciaInfo.replace("{AAMM}", " de " + identificaAno(vs, infRefNf.item(j).getTextContent()) + "" + infRefNf.item(j).getTextContent());
                                    //COLOCADO O IDENTIFICAANO AO MENOS PARA NAO FICAR FIXO, E CONSEGUIR AO MENOS RETROCEDER NOTAS PARA ANO 19
                                }
                            }
                            nfReferenciados += sequenciaInfo.replace("{NUMNF}", "").replace("{AAMM}", "");
                        } else if ("refNFP".equals(infNf.item(i).getNodeName()))
                        {
                            NodeList infRefNf = infNf.item(i).getChildNodes();
                            String sequenciaInfo = "{NUMNF}{SERIE}{AAMM}";
                            for (int j = 0; j < infRefNf.getLength(); j++)
                            {
                                if ("nNF".equals(infRefNf.item(j).getNodeName()))
                                {
                                    sequenciaInfo = sequenciaInfo.replace("{NUMNF}", "$$Ref. NF " + infRefNf.item(j).getTextContent());
                                }
                                if ("serie".equals(infRefNf.item(j).getNodeName()))
                                {
                                    sequenciaInfo = sequenciaInfo.replace("{SERIE}", " Serie " + infRefNf.item(j).getTextContent());
                                }
                                if ("AAMM".equals(infRefNf.item(j).getNodeName()))
                                {
                                    sequenciaInfo = sequenciaInfo.replace("{AAMM}", " de Produtor de " + identificaAno(vs, infRefNf.item(j).getTextContent()) + "" + infRefNf.item(j).getTextContent());
                                }
                            }
                            nfReferenciados += sequenciaInfo.replace("{NUMNF}", "").replace("{SERIE}", "").replace("{AAMM}", "");
                        }
                    }
                }
            }
            if (!nfReferenciados.isEmpty())
            {
                dataSet.fieldByName("INFOFISCO").asString(dataSet.fieldByName("INFOFISCO").asString() + nfReferenciados);
            }
            //necessário manter o limitador de caracteres para funcionar também o limitador de linhas.

            String infoFiscoA = addEspacoStr(dataSet.fieldByName("INFOFISCO").asString().replace("$$$$", "$$").replace("$$", "\n"), "\n", 100, 10).trim().replace("$$", "\n").replace("\r\n", "\n");
            String[] linhasInfoFisco = infoFiscoA.split("\n");
            String[] infos, infoPlaca, infoKM = null;
            String strInfoFisco = "";

            for (int pos = 0; pos < linhasInfoFisco.length; pos++)
            {
                if (!strInfoFisco.contains(linhasInfoFisco[pos]))
                {
                    strInfoFisco += "\n" + linhasInfoFisco[pos];
                }
            }
            dataSet.fieldByName("INFOFISCO").asString(strInfoFisco.replaceFirst("\n", ""));
            for (String s : linhasInfoFisco)
            {
                if (s.contains("PLACA:"))
                {
                    infos = s.split(" ");
                    infoPlaca = infos[0].split("\\:");

                    if (infos.length > 1)
                    {
                        infoKM = infos[1].split("\\:");
                    }

                    if (infoPlaca != null && infoPlaca.length > 1)
                    {
                        dataSet.fieldByName("PLACAINFO").asString("PLACA: " + infoPlaca[1]);
                    } else
                    {
                        dataSet.fieldByName("PLACAINFO").asString(" ");
                    }

                    if (infoKM != null && infoKM.length > 1)
                    {
                        dataSet.fieldByName("KMINFO").asString("KM: " + infoKM[1]);
                    } else
                    {
                        dataSet.fieldByName("KMINFO").asString(" ");
                    }
                }
            }
            if (modelo.equals("email") || modelo.equals("862") || modelo.equals("877") || modelo.equals("1900") || modelo.equals("2025"))
            {
                String URLqr = gerarQRCode(vs, xmlEntrada.fieldByName("XML").asString(), xmlEntrada.fieldByName("CHAVE").asString());
                dataSet.fieldByName("QRCODE").asString(URLqr);
            }
            dataSet.fieldByName("IMPINFOPAGDOIS").asString("nao");
            if (!dataSet.fieldByName("INFOFISCO").asString().isEmpty())
            {
                StringBuilder novaInfo = new StringBuilder();
                int maxLinhas = 10;

                String[] linhas = dataSet.fieldByName("INFOFISCO").asString().split("\n");
                int qtdeLinhas = linhas.length;

                if (qtdeLinhas > maxLinhas)
                {

                    for (int i = 0; i < maxLinhas; i++)
                    {
                        novaInfo.append(linhas[i]).append("\n");
                    }
                    dataSet.fieldByName("INFOFISCO").asString(novaInfo.toString());
                    dataSet.jsonData();
                    novaInfo.setLength(0);
                    novaInfo.trimToSize();

                    for (int i = maxLinhas; i < qtdeLinhas; i++)
                    {
                        novaInfo.append(linhas[i]).append("\n");
                    }
                    dataSet.fieldByName("INFOFISCOPAGDOIS").asString(addEspacoStr(novaInfo.toString(), "\n", 150, 9999));
                    dataSet.fieldByName("IMPINFOPAGDOIS").asString("sim");
                }
            }

            dataSet.fieldByName("VNFICMSTOT").asString(vnf);
            dataSet.fieldByName("VITENSTOT").asString(vprod);
            dataSet.fieldByName("VDESCTOT").asString(vdesc);
            dataSet.fieldByName("VACRETOT").asDouble(vacres);

            dataSet.fieldByName("STATUS").asString("sim");

            if (tpEmi.equals("1"))
            {
                String quebra = impProtocoloLadoQR ? "\n" : " ";
                dataSet.fieldByName("PROTOCOLOAUTORIZA").asString("Protocolo de Autorização:".concat(quebra)
                        .concat(protAutoriza).concat(quebra).concat(impProtocoloLadoQR ? "Data de Autorização\n" : "").concat(dtRec).concat(" ").concat(hRec));
                dataSet.fieldByName("PROTOCOLOAUTORIZANFCE").asString("<ce>Protocolo de Autorizacao: </ce>\n<ce>".concat(protAutoriza).concat(" ").concat(dtRec).concat(" ").concat(hRec).concat("</ce>\n"));
            }
            dataSet.fieldByName("IMPRIMECOMPROVANTEVENDAPRAZO").asString("N");

            boolean imprimeComprovanteVendaPrazo = pf.retornaRegraNegocio(vs, vs.getValor("filial"), 1424).equals("S") || pf.retornaRegraNegocio(vs, vs.getValor("filial"), 1424).equals("COMITEM");

            if (imprimeComprovanteVendaPrazo
                    && (vs.getParameter("IMPRIMECOMPROVANTEVENDAPRAZO") == null || !vs.getParameter("IMPRIMECOMPROVANTEVENDAPRAZO").equals("FALSE")))
            {
                TSQLDataSet dupl = retornaValoresDuplicata(vs);
                if (!dupl.isEmpty())
                {
                    dupl.first();
                    dataSet.fieldByName("IMPRIMECOMPROVANTEVENDAPRAZO").asString("S");
                    dataSet.fieldByName("VALORPRIMEIRAPARCELA").asDouble(dupl.fieldByName("VALOR").asDouble());
                    if (pf.retornaRegraNegocio(vs, vs.getValor("filial"), 1424).equals("COMITEM"))
                    {
                        dataSet.fieldByName("ITENSNOCOMPROVANTEVENDAPRAZO").asString("S");
                    } else
                    {
                        dataSet.fieldByName("ITENSNOCOMPROVANTEVENDAPRAZO").asString("N");
                    }
                    if (!xmlEntrada.fieldByName("NFS").isNull())
                    {
                        TSQLDataSetEmp cdsNFS = TSQLDataSetEmp.create(vs);
                        cdsNFS.commandText("SELECT NFSAIDA.CCLIFOR"
                                + " FROM NFSAIDA"
                                + " WHERE NFSAIDA.NFS=" + xmlEntrada.fieldByName("NFS").asString());
                        cdsNFS.open();
                        dataSet.fieldByName("CCLIFOR").asString(cdsNFS.fieldByName("CCLIFOR").asString());
                    }
                }
            }

            dataSet.post();
            xmlEntrada.next();
        }
        return dataSet;
    }

    public TSQLDataSet retornaDANFE(VariavelSessao vs) throws ExcecaoTecnicon, ParseException
    {
        String seq_nfs = vs.getParameter("NFS");
        String seq_nfe = vs.getParameter("NFE");
        String modelo = validaNFCE3(vs);

        //dataSet principal com todos campos do
        TSQLDataSetEmp dataSet = TSQLDataSetEmp.create(vs);
        dataSet.fieldDefs().clear();
        dataSet.fieldDefs().add("AREAMENSAGEMFISCAL", "FTSTRING", 100, true);
        dataSet.fieldDefs().add("EMITNOME", "FTSTRING", 60, true);
        dataSet.fieldDefs().add("EMITENDERECO", "FTSTRING", 180, true);
        dataSet.fieldDefs().add("EMITENDERECONFCE", "FTSTRING", 180, true);
        dataSet.fieldDefs().add("EMITBAIRRO", "FTSTRING", 20, true);
        dataSet.fieldDefs().add("EMITCEP", "FTSTRING", 10, true);
        dataSet.fieldDefs().add("EMITCIDADE", "FTSTRING", 30, true);
        dataSet.fieldDefs().add("EMITUF", "FTSTRING", 2, true);
        dataSet.fieldDefs().add("EMITFONE", "FTSTRING", 17, true);
        dataSet.fieldDefs().add("NNF", "FTSTRING", 11, true);
        dataSet.fieldDefs().add("SERIE", "FTSTRING", 3, true);
        dataSet.fieldDefs().add("NATOP", "FTSTRING", 60, true);
        dataSet.fieldDefs().add("TPNF", "FTSTRING", 1, true);
        dataSet.fieldDefs().add("EMITIE", "FTSTRING", 14, true);
        dataSet.fieldDefs().add("EMITIEST", "FTSTRING", 20, true);
        dataSet.fieldDefs().add("EMITCNPJ", "FTSTRING", 20, true);
        dataSet.fieldDefs().add("DESTXNOME", "FTSTRING", 60, true);
        dataSet.fieldDefs().add("DESTINFO", "FTSTRING", 1000, true);
        dataSet.fieldDefs().add("DESTINFONFCE", "FTSTRING", 1000, true);
        dataSet.fieldDefs().add("DESTCNPJ", "FTSTRING", 20, true);
        dataSet.fieldDefs().add("DESTENDERECO", "FTSTRING", 180, true);
        dataSet.fieldDefs().add("DESTBAIRRO", "FTSTRING", 60, true);
        dataSet.fieldDefs().add("DESTCIDADE", "FTSTRING", 60, true);
        dataSet.fieldDefs().add("DESTCEP", "FTSTRING", 13, true);
        dataSet.fieldDefs().add("DESTFONE", "FTSTRING", 11, true);
        dataSet.fieldDefs().add("DESTUF", "FTSTRING", 2, true);
        dataSet.fieldDefs().add("DESTIE", "FTSTRING", 14, true);
        dataSet.fieldDefs().add("DEMI", "FTSTRING", 10, true);
        dataSet.fieldDefs().add("HEMI", "FTSTRING", 10, true);
        dataSet.fieldDefs().add("DSAIDA", "FTSTRING", 10, true);
        dataSet.fieldDefs().add("HSAIDA", "FTSTRING", 10, true);
        dataSet.fieldDefs().add("ENTRCNPJ", "FTSTRING", 20, true);
        dataSet.fieldDefs().add("ENTREND", "FTSTRING", 180, true);
        dataSet.fieldDefs().add("ENTRBAIRRO", "FTSTRING", 60, true);
        dataSet.fieldDefs().add("ENTRCIDADE", "FTSTRING", 60, true);
        dataSet.fieldDefs().add("ENTRUF", "FTSTRING", 2, true);
        dataSet.fieldDefs().add("NFATUCOBR", "FTMEMO", 0, true);
        dataSet.fieldDefs().add("VBCICMSTOT", "FTDOUBLE", 10, true, 2);
        dataSet.fieldDefs().add("VICMSICMSTOT", "FTDOUBLE", 10, true, 2);
        dataSet.fieldDefs().add("VBCSTICMSTOT", "FTDOUBLE", 10, true, 2);
        dataSet.fieldDefs().add("VSTICMSTOT", "FTDOUBLE", 10, true, 2);
        dataSet.fieldDefs().add("VPRODICMSTOT", "FTDOUBLE", 10, true, 2);
        dataSet.fieldDefs().add("VFRETEICMSTOT", "FTDOUBLE", 10, true, 2);
        dataSet.fieldDefs().add("VSEGCMSTOT", "FTDOUBLE", 10, true, 2);
        dataSet.fieldDefs().add("DESCCMSTOT", "FTDOUBLE", 10, true, 2);
        dataSet.fieldDefs().add("VOUTROICMSTOT", "FTDOUBLE", 10, true, 2);
        dataSet.fieldDefs().add("VIPI", "FTDOUBLE", 15, true, 2);
        dataSet.fieldDefs().add("VLRTOTALITENS", "FTDOUBLE", 13, true, 2);
        dataSet.fieldDefs().add("VNFICMSTOT", "FTDOUBLE", 10, true, 2);
        dataSet.fieldDefs().add("TRANSPNOME", "FTSTRING", 60, true);
        dataSet.fieldDefs().add("MODFRETE", "FTSTRING", 30, true);
        dataSet.fieldDefs().add("VEICANTT", "FTSTRING", 10, true);
        dataSet.fieldDefs().add("VEICPLACA", "FTSTRING", 8, true);
        dataSet.fieldDefs().add("VEICPLACAUF", "FTSTRING", 2, true);
        dataSet.fieldDefs().add("VEICCGC", "FTSTRING", 20, true);
        dataSet.fieldDefs().add("TRANSPENDER", "FTSTRING", 180, true);
        dataSet.fieldDefs().add("TRANSPCIDADE", "FTSTRING", 60, true);
        dataSet.fieldDefs().add("TRANSPUF", "FTSTRING", 2, true);
        dataSet.fieldDefs().add("TRANSPIE", "FTSTRING", 14, true);
        dataSet.fieldDefs().add("TRANSPQVOL", "FTDOUBLE", 15, true, 2);
        dataSet.fieldDefs().add("TRANSPESP", "FTSTRING", 60, true);
        dataSet.fieldDefs().add("TRANSPMARCA", "FTSTRING", 60, true);
        dataSet.fieldDefs().add("TRANSPNUMERACAO", "FTSTRING", 10, true);
        dataSet.fieldDefs().add("TRANSPPESOB", "FTDOUBLE", 15, true, 3);
        dataSet.fieldDefs().add("TRANSPPESOL", "FTDOUBLE", 15, true, 3);
        dataSet.fieldDefs().add("EMITIM", "FTSTRING", 15, true);
        dataSet.fieldDefs().add("VBCISSQN", "FTDOUBLE", 15, true, 2);
        dataSet.fieldDefs().add("VLISSQN", "FTDOUBLE", 15, true, 2);
        dataSet.fieldDefs().add("VTOTTRIB", "FTDOUBLE", 15, true, 2);
        dataSet.fieldDefs().add("VTOTISSQN", "FTDOUBLE", 10, true, 2);
        dataSet.fieldDefs().add("CHAVE", "FTSTRING", 70, true);
        dataSet.fieldDefs().add("CHAVEBARRA", "FTSTRING", 70, true);
        dataSet.fieldDefs().add("DISPLVIA", "FTSTRING", 1, true);
        dataSet.fieldDefs().add("NUMVIA", "FTSTRING", 20, true);
        dataSet.fieldDefs().add("NUMPROTOCOLO", "FTSTRING", 160, true);
        dataSet.fieldDefs().add("STATUS", "FTSTRING", 3, true);
        dataSet.fieldDefs().add("OBSCONT", "FTSTRING", 600, true);
        dataSet.fieldDefs().add("PROTOCOLOAUTORIZA", "FTSTRING", 600, true);
        dataSet.fieldDefs().add("PROTOCOLOAUTORIZANFCE", "FTSTRING", 600, true);
        dataSet.fieldDefs().add("QRCODE", "FTSTRING", 6000, false);
        dataSet.fieldDefs().add("NOMECSTCSOSN", "FTSTRING", 10, true);
        dataSet.fieldDefs().add("IMPRIMELOGO", "FTSTRING", 1, false);
        dataSet.fieldDefs().add("IMPRIMEMARCADAGUA", "FTSTRING", 1, false);
        dataSet.fieldDefs().add("IMPRIMIRINFOEMITENTE", "FTSTRING", 3, false);
        dataSet.fieldDefs().add("TAMANHOLOGO", "FTSTRING", 30, false);
        dataSet.fieldDefs().add("INFOFISCO", "FTSTRING", 5000, false);
        dataSet.fieldDefs().add("INFOFISCOPAGDOIS", "FTSTRING", 5000, false);
        dataSet.fieldDefs().add("IMPINFOPAGDOIS", "FTSTRING", 5, false);
        dataSet.fieldDefs().add("EMAIL", "FTSTRING", 50, false);
        dataSet.fieldDefs().add("PAGE", "FTSTRING", 50, false);
        dataSet.fieldDefs().add("EXIBEEMAIL", "FTSTRING", 15, false);
        dataSet.fieldDefs().add("FAX", "FTSTRING", 15, false);
        dataSet.fieldDefs().add("EXIBEFAX", "FTSTRING", 15, false);
        dataSet.fieldDefs().add("IMPRIMECOMPROVANTEVENDAPRAZO", "FTSTRING", 1, false);
        dataSet.fieldDefs().add("ITENSNOCOMPROVANTEVENDAPRAZO", "FTSTRING", 1, false);
        dataSet.fieldDefs().add("VALORPRIMEIRAPARCELA", "FTDOUBLE", 15, false, 2);
        dataSet.fieldDefs().add("VITENSTOT", "FTDOUBLE", 10, true, 2);
        dataSet.fieldDefs().add("VDESCTOT", "FTDOUBLE", 10, true, 2);
        dataSet.fieldDefs().add("VACRETOT", "FTDOUBLE", 10, true, 2);
        dataSet.fieldDefs().add("TROCO", "FTDOUBLE", 10, true, 2);
        dataSet.fieldDefs().add("IMPITENS", "FTSTRING", 25, false);
        dataSet.fieldDefs().add("NFCESTYLEQR", "FTSTRING", 55, false);
        dataSet.fieldDefs().add("NFCESTYLEPROTDIR", "FTSTRING", 55, false);
        dataSet.fieldDefs().add("NFCESTYLEPROTCIMA", "FTSTRING", 55, false);
        dataSet.fieldDefs().add("NFS", "I", 10, false);
        dataSet.fieldDefs().add("NFE", "I", 10, false);
        dataSet.fieldDefs().add("TPVIA", "V", 20, false);
        dataSet.fieldDefs().add("CCLIFOR", "I", 10, false);
        dataSet.fieldDefs().add("PLACAINFO", "FTSTRING", 30, false);
        dataSet.fieldDefs().add("KMINFO", "FTSTRING", 30, false);
        dataSet.fieldDefs().add("URLCONSULTACHAVE", "FTSTRING", 100, false);
        dataSet.fieldDefs().add("IMPCONTINGENCIA", "FTSTRING", 2, false);

        TClientDataSet dsXml = retornaCdsXML(vs, seq_nfs, seq_nfe);
        if ("".equals(dsXml.fieldByName("NFEXML").asString()))
        {
            throw new ExcecaoTecnicon(vs, "Nota Fiscal não possui xml");
        }
        dsXml.first();
        while (!dsXml.eof())
        {
            Document document;
            try
            {
                document = Utils.strToDoc(vs, dsXml.fieldByName("NFEXML").asString());
            } catch (ParserConfigurationException | SAXException | IOException ex)
            {
                throw new ExcecaoTecnicon(vs, "Falha na leitura do XML, verifique se o xml esta valido."
                        + "\nEfetue o download do xml no SEFAZ para realizar a manutenção\nerro:" + ex.getMessage());
            }
            Node aux = document.getElementsByTagName("infNFe").item(0);
            if (aux != null && aux.getAttributes() != null && aux.getAttributes().getNamedItem("versao") != null)
            {
                if (aux.getAttributes().getNamedItem("versao").getTextContent().equals("4.00"))
                {
                    return (TSQLDataSet) TClassLoader.execMethod("TecniconNFe4/RelatorioDANFE", "retornaRelatorioNFCE4", vs);
                } else if (!aux.getAttributes().getNamedItem("versao").getTextContent().equals("3.10") && !aux.getAttributes().getNamedItem("versao").getTextContent().equals("4.00"))
                {
                    return (TSQLDataSet) TClassLoader.execMethod("TecniconNFe2/RetornaRelatorioDANFE", "obterSQLDataSet", vs);
                }
            }

            dataSet.insert();
            dataSet.fieldByName("NFS").asInteger(dsXml.fieldByName("NFS").asInteger());
            dataSet.fieldByName("NFE").asInteger(dsXml.fieldByName("NFE").asInteger());

            ParametrosForm pf = (ParametrosForm) TecniconLookup.lookup("TecniconParametrosForm", "ParametrosFormImpl");

            if (pf.retornaRegraNegocio(vs, vs.getValor("filial"), 1172).equals("S") && !dsXml.fieldByName("NFS").isNull())
            {
                TSQLDataSetEmp cdsNFS = TSQLDataSetEmp.create(vs);
                cdsNFS.commandText("SELECT FILIAL.EMAIL, FILIAL.HPAGE"
                        + " FROM FILIAL"
                        + " INNER JOIN NFSAIDA ON (NFSAIDA.CFILIAL = FILIAL.CFILIAL)"
                        + " WHERE NFSAIDA.NFS=" + dsXml.fieldByName("NFS").asString());
                cdsNFS.open();
                dataSet.fieldByName("EMAIL").asString(cdsNFS.fieldByName("EMAIL").asString());
                dataSet.fieldByName("PAGE").asString(cdsNFS.fieldByName("HPAGE").asString());
                dataSet.fieldByName("EXIBEEMAIL").asString("display:block");
            } else
            {
                dataSet.fieldByName("EMAIL").asString("");
                dataSet.fieldByName("PAGE").asString("");
                dataSet.fieldByName("EXIBEEMAIL").asString("display:none");
            }
            if (pf.retornaRegraNegocio(vs, vs.getValor("filial"), 1245).equals("S") && !dsXml.fieldByName("NFS").isNull())
            {
                TSQLDataSetEmp cdsNFS = TSQLDataSetEmp.create(vs);
                cdsNFS.commandText("SELECT FILIAL.FAX"
                        + " FROM FILIAL"
                        + " INNER JOIN NFSAIDA ON (NFSAIDA.CFILIAL = FILIAL.CFILIAL)"
                        + " WHERE NFSAIDA.NFS=" + dsXml.fieldByName("NFS").asString());
                cdsNFS.open();
                dataSet.fieldByName("FAX").asString(cdsNFS.fieldByName("FAX").asString());
                dataSet.fieldByName("EXIBEFAX").asString("display:block");
            } else
            {
                dataSet.fieldByName("FAX").asString("");
                dataSet.fieldByName("EXIBEFAX").asString("display:none");
            }

            if (vs.getParameter("TPVIA") != null && !vs.getParameter("TPVIA").isEmpty())
            {
                dataSet.fieldByName("TPVIA").asString(vs.getParameter("TPVIA"));
            } else
            {
                dataSet.fieldByName("TPVIA").asString("Consumidor");
            }
            dataSet.fieldByName("CHAVEBARRA").asString(dsXml.fieldByName("CHAVE").asString());
            dataSet.fieldByName("CHAVE").asString(Funcoes.formatMaskText(vs, "9999 9999 9999 9999 9999 9999 9999 9999 9999 9999 9999", dsXml.fieldByName("CHAVE").asString()));

            String imprimirLogo = pf.retornaRegraNegocio(vs, vs.getValor("filial"), 81);
            dataSet.fieldByName("IMPRIMELOGO").asString(imprimirLogo);
            //se retornar vazio para LOGODANFE, deve imprimir as informações do emitente
            //se retornar com alguma informação, é pq as informações se encontram na LOGO e não precisam ser impressas
            if (LogoJPG.RETORNA_LOGO_JPG(vs.getValor("empresa"), vs.getValor("filial"), "", "LOGODANFE", ".JPG").isEmpty())
            {
                dataSet.fieldByName("IMPRIMIRINFOEMITENTE").asString("sim");
                if (modelo != null && modelo.equals("868"))
                {
                    dataSet.fieldByName("TAMANHOLOGO").asString("height=\"50px\"");
                } else
                {
                    dataSet.fieldByName("TAMANHOLOGO").asString("width=\"65px\"");
                }
            } else
            {
                dataSet.fieldByName("IMPRIMIRINFOEMITENTE").asString("nao");
                if (modelo != null && modelo.equals("868"))
                {
                    dataSet.fieldByName("TAMANHOLOGO").asString("height=\"90px\"");
                } else
                {
                    dataSet.fieldByName("TAMANHOLOGO").asString("height=\"80px\"");
                }
            }

            if (vs.getParameter("MODELOECONOMICONFCE") != null && vs.getParameter("MODELOECONOMICONFCE").equals("S"))
            {
                dataSet.fieldByName("IMPITENS").asString("display: none;");
            }

            int numVia = "".equals(dsXml.fieldByName("DANFEIMP").asString()) ? 1 : Integer.parseInt(dsXml.fieldByName("DANFEIMP").asString()) + 1;
            dataSet.fieldByName("NUMVIA").asString(numVia + "ª VIA");

            if (vs.getParameter("NFETERCEIROS") != null && vs.getParameter("NFETERCEIROS").equals("TRUE"))
            {
                dataSet.fieldByName("IMPRIMEMARCADAGUA").asString("N");
                dataSet.fieldByName("IMPRIMELOGO").asString("N");
                dataSet.fieldByName("IMPRIMIRINFOEMITENTE").asString("sim");
                dataSet.fieldByName("TAMANHOLOGO").asString("width=\"5px\"");
            }
            if (("S").equals(dataSet.fieldByName("IMPRIMELOGO").asString()))
            {
                if (dsXml.fieldByName("NFE").asString() != null)
                {
                    TSQLDataSetEmp nfOrigem = TSQLDataSetEmp.create(vs);
                    nfOrigem.commandText("SELECT NFE "
                            + " FROM NFENTRADA"
                            + " INNER JOIN MODELONF ON MODELONF.CMODELONF = NFENTRADA.CMODELONF"
                            + " WHERE MODELONF.EMITENTE = 'T'"
                            + " AND NFENTRADA.NFE = " + dsXml.fieldByName("NFE").asInteger());
                    nfOrigem.open();
                    if (!nfOrigem.isEmpty())
                    {
                        dataSet.fieldByName("IMPRIMELOGO").asString("N");
                    }
                }
            }
            boolean impProtocoloLadoQR = pf.retornaRegraNegocio(vs, vs.getValor("filial"), 1517).equals("S");
            if (impProtocoloLadoQR)
            {
                dataSet.fieldByName("NFCESTYLEQR").asString("padding-left: 30px;");
                dataSet.fieldByName("NFCESTYLEPROTDIR").asString("text-align:right; padding-right: 80px;");
                dataSet.fieldByName("NFCESTYLEPROTCIMA").asString("display: none;");
            } else
            {
                dataSet.fieldByName("NFCESTYLEPROTDIR").asString("display: none;");
                dataSet.fieldByName("NFCESTYLEPROTCIMA").asString("");
            }
            //variaveis utilizadas para gerar o qrcode e para controle
            String tpEmi = "", protAutoriza = "", datahoraEmi;
            //Identificação da NF-e

            String mod = "";
            NodeList ide = document.getElementsByTagName("ide").item(0).getChildNodes();
            for (int i = 0; i < ide.getLength(); i++)
            {
                if (ide.item(i).getNodeName().equals("serie"))
                {
                    dataSet.fieldByName("SERIE").asString(ide.item(i).getTextContent());
                }
                if ("tpNF".equals(ide.item(i).getNodeName()))
                {
                    dataSet.fieldByName("TPNF").asString(ide.item(i).getTextContent());
                }
                if ("mod".equals(ide.item(i).getNodeName()))
                {
                    mod = ide.item(i).getTextContent();
                }

                //date e hora de emissão
                if ("dhEmi".equals(ide.item(i).getNodeName()))
                {
                    datahoraEmi = ide.item(i).getTextContent();
                    String[] td = datahoraEmi.substring(0, 10).split("-");
                    dataSet.fieldByName("DEMI").asString(td[2].concat("/").concat(td[1]).concat("/").concat(td[0]));
                    dataSet.fieldByName("HEMI").asString(datahoraEmi.substring(11, 19));
                }
                if ("dhSaiEnt".equals(ide.item(i).getNodeName()))
                {
                    String datahoraSai = ide.item(i).getTextContent();
                    String[] tds = datahoraSai.substring(0, 10).split("-");
                    dataSet.fieldByName("DSAIDA").asString(tds[2].concat("/").concat(tds[1]).concat("/").concat(tds[0]));
                    dataSet.fieldByName("HSAIDA").asString(datahoraSai.substring(11, 19));
                    continue;
                }
                if ("nNF".equals(ide.item(i).getNodeName()))
                {
                    String nf = Funcoes.car(ide.item(i).getTextContent(), 9, "D", "0");
                    nf = Funcoes.formatMaskText(vs, "999.999.999;0", nf);
                    dataSet.fieldByName("NNF").asString(nf);
                }
                if ("tpEmis".equals(ide.item(i).getNodeName()))
                {
                    tpEmi = ide.item(i).getTextContent();
                }
                if ("natOp".equals(ide.item(i).getNodeName()))
                {
                    dataSet.fieldByName("NATOP").asString(ide.item(i).getTextContent());
                }

                if ("xJust".equals(ide.item(i).getNodeName()) && ide.item(i).getTextContent() != null && !"".equals(ide.item(i).getTextContent()))
                {
                    dataSet.fieldByName("IMPCONTINGENCIA").asString("S");

                } else
                {
                    dataSet.fieldByName("IMPCONTINGENCIA").asString("N");
                }

            }

            //Identificação do emitente da NF-e
            NodeList emit = document.getElementsByTagName("emit").item(0).getChildNodes();
            if (emit != null)
            {
                for (int i = 0; i < emit.getLength(); i++)
                {
                    if (emit.item(i).getNodeName().equals("CPF"))
                    {
                        dataSet.fieldByName("EMITCNPJ").asString(Funcoes.formatMaskText(vs, "999.999.999-99;0", emit.item(i).getTextContent()));
                    }
                    if (emit.item(i).getNodeName().equals("CNPJ"))
                    {
                        dataSet.fieldByName("EMITCNPJ").asString(Funcoes.formatMaskText(vs, "99.999.999/9999-99;0", emit.item(i).getTextContent()));
                    }
                    if (emit.item(i).getNodeName().equals("xNome"))
                    {
                        dataSet.fieldByName("EMITNOME").asString(emit.item(i).getTextContent());
                    }
                    if (emit.item(i).getNodeName().equals("xFant"))
                    {
                    }
                    if (emit.item(i).getNodeName().equals("IE"))
                    {
                        dataSet.fieldByName("EMITIE").asString(emit.item(i).getTextContent());
                    }
                    if (emit.item(i).getNodeName().equals("IEST"))
                    {
                        dataSet.fieldByName("EMITIEST").asString(emit.item(i).getTextContent());
                    }
                    if (emit.item(i).getNodeName().equals("IM"))
                    {
                        dataSet.fieldByName("EMITIM").asString(emit.item(i).getTextContent());
                    }
                }
            }
            //Endereço do emitente
            String ufEmitente = "";
            NodeList enderEmit = document.getElementsByTagName("enderEmit").item(0).getChildNodes();
            if (enderEmit != null)
            {
                for (int i = 0; i < enderEmit.getLength(); i++)
                {
                    if (enderEmit.item(i).getNodeName().equals("xLgr"))
                    {
                        dataSet.fieldByName("EMITENDERECO").asString(enderEmit.item(i).getTextContent());
                        dataSet.fieldByName("EMITENDERECONFCE").asString(enderEmit.item(i).getTextContent());
                    }
                    if (enderEmit.item(i).getNodeName().equals("nro"))
                    {
                        dataSet.fieldByName("EMITENDERECO").asString(dataSet.fieldByName("EMITENDERECO").asString().concat(" - ")
                                .concat(enderEmit.item(i).getTextContent()));
                        dataSet.fieldByName("EMITENDERECONFCE").asString(dataSet.fieldByName("EMITENDERECONFCE").asString().concat(" - ")
                                .concat(enderEmit.item(i).getTextContent()));
                    }
                    if (enderEmit.item(i).getNodeName().equals("xCpl"))
                    {
                        if (!enderEmit.item(i).getTextContent().equals(""))
                        {
                            dataSet.fieldByName("EMITENDERECO").asString(dataSet.fieldByName("EMITENDERECO").asString().concat(" ")
                                    .concat(enderEmit.item(i).getTextContent()));
                        }
                    }
                    if (enderEmit.item(i).getNodeName().equals("xBairro"))
                    {
                        dataSet.fieldByName("EMITENDERECO").asString(dataSet.fieldByName("EMITENDERECO").asString().concat(" ")
                                .concat(enderEmit.item(i).getTextContent()));
                        dataSet.fieldByName("EMITENDERECONFCE").asString(dataSet.fieldByName("EMITENDERECONFCE").asString().concat(" ")
                                .concat(enderEmit.item(i).getTextContent()));
                    }
                    if (enderEmit.item(i).getNodeName().equals("cMun"))
                    {
                    }
                    if (enderEmit.item(i).getNodeName().equals("xMun"))
                    {
                        dataSet.fieldByName("EMITENDERECO").asString(dataSet.fieldByName("EMITENDERECO").asString().concat(", ")
                                .concat(enderEmit.item(i).getTextContent()));
                        dataSet.fieldByName("EMITENDERECONFCE").asString(dataSet.fieldByName("EMITENDERECONFCE").asString().concat("\n")
                                .concat(enderEmit.item(i).getTextContent()));
                    }
                    if (enderEmit.item(i).getNodeName().equals("UF"))
                    {
                        dataSet.fieldByName("EMITENDERECO").asString(dataSet.fieldByName("EMITENDERECO").asString().concat(" - ")
                                .concat(enderEmit.item(i).getTextContent()));
                        dataSet.fieldByName("EMITENDERECONFCE").asString(dataSet.fieldByName("EMITENDERECONFCE").asString().concat(" - ")
                                .concat(enderEmit.item(i).getTextContent()));
                    }
                    if (enderEmit.item(i).getNodeName().equals("CEP"))
                    {
                        dataSet.fieldByName("EMITCEP").asString(enderEmit.item(i).getTextContent());
                    }
                    if (enderEmit.item(i).getNodeName().equals("cPais"))
                    {
                    }
                    if (enderEmit.item(i).getNodeName().equals("xPais"))
                    {
                    }
                    if ("xMun".equals(enderEmit.item(i).getNodeName()))
                    {
                        dataSet.fieldByName("EMITCIDADE").asString(enderEmit.item(i).getTextContent());
                    }
                    if (enderEmit.item(i).getNodeName().equals("fone"))
                    {
                        dataSet.fieldByName("EMITFONE").asString(enderEmit.item(i).getTextContent());
                    }

                    if (enderEmit.item(i).getNodeName().equals("CNAE"))
                    {
                    }
                    if (enderEmit.item(i).getNodeName().equals("CRT"))
                    {
                    }
                    if (enderEmit.item(i).getNodeName().equals("UF"))
                    {
                        ufEmitente = enderEmit.item(i).getTextContent();
                    }
                    if ("fone".equals(enderEmit.item(i).getNodeName()))
                    {
                        dataSet.fieldByName("EMITFONE").asString(enderEmit.item(i).getTextContent());
                    }
                }
            }
            switch (ufEmitente)
            {
                case "AL":
                    if (vs.getParameter("relatorioesp") != null && vs.getParameter("relatorioesp").equals("877"))
                    {
                        //no relatório de bobina precisa quebrar a linha pois não tem espaço para o link inteiro
                        dataSet.fieldByName("URLCONSULTACHAVE").asString("http://nfce.sefaz.al.gov.br/ consultaNFCe.htm");
                    } else
                    {
                        dataSet.fieldByName("URLCONSULTACHAVE").asString("http://nfce.sefaz.al.gov.br/consultaNFCe.htm");
                    }
                    break;
                default:
                    dataSet.fieldByName("URLCONSULTACHAVE").asString("www.nfe.fazenda.gov.br/portal");
            }
            //Identificação do Destinatário da NF-e
            Node tagdest = document.getElementsByTagName("dest").item(0);
            StringBuilder auxDestCli = new StringBuilder("CONSUMIDOR");
            StringBuilder auxDestNFCe = new StringBuilder("<ce>CONSUMIDOR</ce>");
            if (tagdest != null)
            {
                NodeList dest = tagdest.getChildNodes();
                auxDestCli.append("\r\n");
                auxDestNFCe.append("\n");
                for (int i = 0; i < dest.getLength(); i++)
                {
                    if (dest.item(i).getNodeName().equals("CNPJ"))
                    {
                        auxDestCli.append("CNPJ: ".concat(Funcoes.formatMaskText(vs, "99.999.999/9999-99;0", dest.item(i).getTextContent())));
                        auxDestNFCe.append("<ce>CNPJ: ".concat(Funcoes.formatMaskText(vs, "99.999.999/9999-99;0", dest.item(i).getTextContent())));
                        dataSet.fieldByName("DESTCNPJ").asString(Funcoes.formatMaskText(vs, "99.999.999/9999-99;0", dest.item(i).getTextContent()));
                    }
                    if (dest.item(i).getNodeName().equals("CPF"))
                    {
                        auxDestCli.append("CPF: ".concat(Funcoes.formatMaskText(vs, "999.999.999-99;0", dest.item(i).getTextContent())));
                        auxDestNFCe.append("<ce>CPF: ".concat(Funcoes.formatMaskText(vs, "999.999.999-99;0", dest.item(i).getTextContent())));
                        dataSet.fieldByName("DESTCNPJ").asString(Funcoes.formatMaskText(vs, "999.999.999-99;0", dest.item(i).getTextContent()));
                    }
                    if (dest.item(i).getNodeName().equals("idEstrangeiro"))
                    {
                        auxDestCli.append("ID Estrangeiro: ".concat(dest.item(i).getTextContent()));
                        auxDestNFCe.append("<ce>ID Estrangeiro: ".concat(dest.item(i).getTextContent()));
                        dataSet.fieldByName("DESTCNPJ").asString(dest.item(i).getTextContent());
                    }
                    if (dest.item(i).getNodeName().equals("xNome"))
                    {
                        auxDestCli.append(" ").append(dest.item(i).getTextContent()).append("</ce>\r\n");
                        dataSet.fieldByName("DESTXNOME").asString(dest.item(i).getTextContent());
                    }
                    if ("IE".equals(dest.item(i).getNodeName()))
                    {
                        dataSet.fieldByName("DESTIE").asString(dest.item(i).getTextContent());
                    }
                }
            }
            //Endereço do Destinatário da NF-e
//        NodeList enderDest = (document.getElementsByTagName("enderDest") == null ? null : document.getElementsByTagName("enderDest").item(0).getChildNodes());
            StringBuilder auxDestEnde = new StringBuilder();
            if (document.getElementsByTagName("enderDest") != null && document.getElementsByTagName("enderDest").item(0) != null)
            {
                NodeList enderDest = document.getElementsByTagName("enderDest").item(0).getChildNodes();

                auxDestNFCe.append("\n");
                for (int i = 0; i < enderDest.getLength(); i++)
                {
                    if (enderDest.item(i).getNodeName().equals("xLgr"))
                    {
                        auxDestEnde.append(enderDest.item(i).getTextContent());
                        auxDestNFCe.append("<ce>").append(enderDest.item(i).getTextContent());
                        dataSet.fieldByName("DESTENDERECO").asString(enderDest.item(i).getTextContent());
                    }
                    if (enderDest.item(i).getNodeName().equals("nro"))
                    {
                        auxDestEnde.append(", ").append(enderDest.item(i).getTextContent());
                        auxDestNFCe.append(", ").append(enderDest.item(i).getTextContent());
                        dataSet.fieldByName("DESTENDERECO").asString(dataSet.fieldByName("DESTENDERECO").asString() + ", " + enderDest.item(i).getTextContent());
                    }
                    if ("xCpl".equals(enderDest.item(i).getNodeName()))
                    {
                        dataSet.fieldByName("DESTENDERECO").asString(dataSet.fieldByName("DESTENDERECO").asString() + " " + enderDest.item(i).getTextContent());
                        continue;
                    }
                    if (enderDest.item(i).getNodeName().equals("xBairro"))
                    {
                        auxDestEnde.append(", ").append(enderDest.item(i).getTextContent());
                        auxDestNFCe.append(", ").append(enderDest.item(i).getTextContent());
                        dataSet.fieldByName("DESTBAIRRO").asString(enderDest.item(i).getTextContent());
                    }
                    if (enderDest.item(i).getNodeName().equals("xMun"))
                    {
                        auxDestEnde.append(", ").append(enderDest.item(i).getTextContent());
                        auxDestNFCe.append(", ").append(enderDest.item(i).getTextContent()).append("</ce>");
                        dataSet.fieldByName("DESTCIDADE").asString(enderDest.item(i).getTextContent());
                    }
                    if (enderDest.item(i).getNodeName().equals("fone"))
                    {
                        auxDestCli.append(" Fone: ").append(enderDest.item(i).getTextContent());
                        auxDestNFCe.append(" Fone: ").append(enderDest.item(i).getTextContent()).append("</ce>");
                    }
                    if (enderDest.item(i).getNodeName().equals("UF"))
                    {
                        dataSet.fieldByName("DESTUF").asString(enderDest.item(i).getTextContent());
                    }
                    if (enderDest.item(i).getNodeName().equals("CEP"))
                    {
                        dataSet.fieldByName("DESTCEP").asString(Funcoes.formatMaskText(vs, "99999-999", enderDest.item(i).getTextContent()));
                    }
                    if (enderDest.item(i).getNodeName().equals("fone"))
                    {
                        dataSet.fieldByName("DESTFONE").asString(enderDest.item(i).getTextContent());
                    }
                }
            }

            if (mod.equals("65") && document.getElementsByTagName("entrega") != null && document.getElementsByTagName("entrega").item(0) != null)
            {
                auxDestEnde.setLength(0);
                auxDestEnde.append("\n");
                NodeList enderEntrega = document.getElementsByTagName("entrega").item(0).getChildNodes();
                for (int i = 0; i < enderEntrega.getLength(); i++)
                {
                    if (enderEntrega.item(i).getNodeName().equals("xLgr"))
                    {
                        auxDestEnde.append(enderEntrega.item(i).getTextContent());
                        dataSet.fieldByName("DESTENDERECO").asString(enderEntrega.item(i).getTextContent());
                    }
                    if (enderEntrega.item(i).getNodeName().equals("nro"))
                    {
                        auxDestEnde.append(", ").append(enderEntrega.item(i).getTextContent());
                        dataSet.fieldByName("DESTENDERECO").asString(dataSet.fieldByName("DESTENDERECO").asString() + ", " + enderEntrega.item(i).getTextContent());
                    }
                    if ("xCpl".equals(enderEntrega.item(i).getNodeName()))
                    {
                        auxDestEnde.append(", ").append(enderEntrega.item(i).getTextContent());
                        dataSet.fieldByName("DESTENDERECO").asString(dataSet.fieldByName("DESTENDERECO").asString() + " " + enderEntrega.item(i).getTextContent());
                        continue;
                    }
                    if (enderEntrega.item(i).getNodeName().equals("xBairro"))
                    {
                        auxDestEnde.append(", ").append(enderEntrega.item(i).getTextContent());
                        dataSet.fieldByName("DESTBAIRRO").asString(enderEntrega.item(i).getTextContent());
                    }
                    if (enderEntrega.item(i).getNodeName().equals("xMun"))
                    {
                        auxDestEnde.append("\n").append(enderEntrega.item(i).getTextContent());
                        dataSet.fieldByName("DESTCIDADE").asString(enderEntrega.item(i).getTextContent());
                    }
                    if (enderEntrega.item(i).getNodeName().equals("UF"))
                    {
                        auxDestEnde.append(" - ").append(enderEntrega.item(i).getTextContent());
                        dataSet.fieldByName("DESTUF").asString(enderEntrega.item(i).getTextContent());
                    }
                }
            }

            aux = document.getElementsByTagName("total").item(0);
            if (aux != null)
            {
                NodeList total = aux.getChildNodes();
                for (int j = 0; j < total.getLength(); j++)
                {
                    if ("ICMSTot".equals(total.item(j).getNodeName()))
                    {
                        NodeList toticms = total.item(j).getChildNodes();
                        for (int k = 0; k < toticms.getLength(); k++)
                        {
                            if ("vBC".equals(toticms.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("VBCICMSTOT").asString(toticms.item(k).getTextContent());
                                continue;
                            }
                            if ("vICMS".equals(toticms.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("VICMSICMSTOT").asString(toticms.item(k).getTextContent());
                                continue;
                            }
                            if ("vBCST".equals(toticms.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("VBCSTICMSTOT").asString(toticms.item(k).getTextContent());
                                continue;
                            }
                            if ("vST".equals(toticms.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("VSTICMSTOT").asString(toticms.item(k).getTextContent());
                                continue;
                            }
                            if ("vProd".equals(toticms.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("VPRODICMSTOT").asString(toticms.item(k).getTextContent());
                                continue;
                            }
                            if ("vFrete".equals(toticms.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("VFRETEICMSTOT").asString(toticms.item(k).getTextContent());
                                continue;
                            }
                            if ("vSeg".equals(toticms.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("VSEGCMSTOT").asString(toticms.item(k).getTextContent());
                                continue;
                            }
                            if ("vDesc".equals(toticms.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("DESCCMSTOT").asString(toticms.item(k).getTextContent());
                                continue;
                            }
                            if ("vIPI".equals(toticms.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("VIPI").asString(toticms.item(k).getTextContent());
                                continue;
                            }
                            if ("vOutro".equals(toticms.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("VOUTROICMSTOT").asString(toticms.item(k).getTextContent());
                                continue;
                            }
                        }
                    }
                    if ("ISSQNtot".equals(total.item(j).getNodeName()))
                    {
                        NodeList totissqn = total.item(j).getChildNodes();
                        for (int k = 0; k < totissqn.getLength(); k++)
                        {
                            if ("vBC".equals(totissqn.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("VBCISSQN").asString(totissqn.item(k).getTextContent());
                                continue;
                            }
                            if ("vISS".equals(totissqn.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("VTOTISSQN").asString(totissqn.item(k).getTextContent());
                                continue;
                            }
                            if ("vServ".equals(totissqn.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("VLISSQN").asString(totissqn.item(k).getTextContent());
                            }
                        }
                    }
                }
            }
            aux = document.getElementsByTagName("transp").item(0);
            if (aux != null)
            {
                NodeList transp = aux.getChildNodes();
                for (int j = 0; j < transp.getLength(); j++)
                {
                    if ("modFrete".equals(transp.item(j).getNodeName()))
                    {
                        dataSet.fieldByName("MODFRETE").asString(transp.item(j).getTextContent());
                        switch (dataSet.fieldByName("MODFRETE").asString())
                        {
                            case "0": //CIF
                                dataSet.fieldByName("MODFRETE").asString(dataSet.fieldByName("MODFRETE").asString() + " - EMITENTE");
                                break;
                            case "1": //FOB
                                dataSet.fieldByName("MODFRETE").asString(dataSet.fieldByName("MODFRETE").asString() + " - DESTINATÁRIO/REMETENTE");
                                break;
                            case "2": //TERCEIROS
                                dataSet.fieldByName("MODFRETE").asString(dataSet.fieldByName("MODFRETE").asString() + " - TERCEIROS");
                                break;
                            case "9": //SEM FRETE
                                dataSet.fieldByName("MODFRETE").asString(dataSet.fieldByName("MODFRETE").asString() + " - SEM FRETE");
                                break;
                            default: //DEFAULT - FOB
                                dataSet.fieldByName("MODFRETE").asString(dataSet.fieldByName("MODFRETE").asString() + " - DESTINATÁRIO/REMETENTE");
                                break;
                        }
                        continue;
                    }
                    if ("transporta".equals(transp.item(j).getNodeName()))
                    {
                        NodeList transporta = transp.item(j).getChildNodes();
                        for (int k = 0; k < transporta.getLength(); k++)
                        {
                            if ("CPF".equals(transporta.item(k).getNodeName()) || "CNPJ".equals(transporta.item(k).getNodeName()))
                            {
                                if ("CPF".equals(transporta.item(k).getNodeName()))
                                {
                                    dataSet.fieldByName("VEICCGC").asString(Funcoes.formatMaskText(vs, "999.999.999-99;0", transporta.item(k).getTextContent()));
                                } else
                                {
                                    dataSet.fieldByName("VEICCGC").asString(Funcoes.formatMaskText(vs, "99.999.999/9999-99;0", transporta.item(k).getTextContent()));
                                }
                                continue;
                            }
                            if ("xNome".equals(transporta.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("TRANSPNOME").asString(transporta.item(k).getTextContent());
                                continue;
                            }
                            if ("xEnder".equals(transporta.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("TRANSPENDER").asString(transporta.item(k).getTextContent());
                                continue;
                            }
                            if ("xMun".equals(transporta.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("TRANSPCIDADE").asString(transporta.item(k).getTextContent());
                                continue;
                            }
                            if ("UF".equals(transporta.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("TRANSPUF").asString(transporta.item(k).getTextContent());
                                continue;
                            }
                            if ("IE".equals(transporta.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("TRANSPIE").asString(transporta.item(k).getTextContent());
                            }
                        }
                    }

                    if ("vol".equals(transp.item(j).getNodeName()))
                    {
                        NodeList vol = transp.item(j).getChildNodes();
                        for (int k = 0; k < vol.getLength(); k++)
                        {
                            if ("qVol".equals(vol.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("TRANSPQVOL").asString(vol.item(k).getTextContent());
                                continue;
                            }
                            if ("nVol".equals(vol.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("TRANSPNUMERACAO").asString(vol.item(k).getTextContent());
                                continue;
                            }
                            if ("marca".equals(vol.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("TRANSPMARCA").asString(vol.item(k).getTextContent());
                                continue;
                            }
                            if ("esp".equals(vol.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("TRANSPESP").asString(vol.item(k).getTextContent());
                                continue;
                            }
                            if ("nVol".equals(vol.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("TRANSPNUMERACAO").asString(vol.item(k).getTextContent());
                                continue;
                            }
                            if ("pesoL".equals(vol.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("TRANSPPESOL").asString(vol.item(k).getTextContent());
                                continue;
                            }
                            if ("pesoB".equals(vol.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("TRANSPPESOB").asString(vol.item(k).getTextContent());
                            }
                        }
                    }
                    if ("veicTransp".equals(transp.item(j).getNodeName()))
                    {
                        NodeList VeicTransp = transp.item(j).getChildNodes();
                        for (int k = 0; k < VeicTransp.getLength(); k++)
                        {
                            if ("RNTC".equals(VeicTransp.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("VEICANTT").asString(VeicTransp.item(k).getTextContent());
                                continue;
                            }
                            if ("placa".equals(VeicTransp.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("VEICPLACA").asString(VeicTransp.item(k).getTextContent());
                                continue;
                            }
                            if ("UF".equals(VeicTransp.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("VEICPLACAUF").asString(VeicTransp.item(k).getTextContent());
                            }
                        }
                    }
                }
            }
            String nfat = "";
            aux = document.getElementsByTagName("cobr").item(0);
            if (aux != null)
            {
                NodeList cobr = aux.getChildNodes();
                int cont = 0;
                for (int j = 0; j < cobr.getLength(); j++)
                {
                    if ("fat".equals(cobr.item(j).getNodeName()))
                    {
                        NodeList fat = cobr.item(j).getChildNodes();
                        for (int k = 0; k < fat.getLength(); k++)
                        {
                            if ("nFat".equals(fat.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("NFATUCOBR").asString("");
                                nfat = fat.item(k).getTextContent();
                            }
                        }
                    }
                    if ("dup".equals(cobr.item(j).getNodeName()))
                    {
                        NodeList dup = cobr.item(j).getChildNodes();
                        for (int k = 0; k < dup.getLength(); k++)
                        {
                            if ("nDup".equals(dup.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("NFATUCOBR").asString(dataSet.fieldByName("NFATUCOBR").asString() + nfat + "/" + dup.item(k).getTextContent());
                                continue;
                            }
                            if ("dVenc".equals(dup.item(k).getNodeName()))
                            {
                                dataSet.fieldByName("NFATUCOBR").asString(dataSet.fieldByName("NFATUCOBR").asString() + " " + dup.item(k).getTextContent().substring(8, 10) + "/"
                                        + dup.item(k).getTextContent().substring(5, 7) + "/" + dup.item(k).getTextContent().substring(0, 4));
                                continue;
                            }
                            if ("vDup".equals(dup.item(k).getNodeName()))
                            {
                                String valor = dup.item(k).getTextContent();
                                double dbValor = Funcoes.strToDouble(valor);
                                dataSet.fieldByName("NFATUCOBR").asString(dataSet.fieldByName("NFATUCOBR").asString() + " R$ " + Funcoes.formatFloat(",##0.00", dbValor) + " &nbsp; &nbsp; ");
                                cont++;
                                if (cont == 4)
                                {
                                    cont = 0;
                                    dataSet.fieldByName("NFATUCOBR").asString(dataSet.fieldByName("NFATUCOBR").asString() + "\r\n");
                                }
                            }
                        }
                    }
                }
            }
            aux = document.getElementsByTagName("infAdic").item(0);
            if (aux != null && "infAdic".equals(aux.getNodeName()))
            {
                NodeList InfAdic = aux.getChildNodes();
                for (int j = 0; j < InfAdic.getLength(); j++)
                {
                    if ("infCpl".equals(InfAdic.item(j).getNodeName()))
                    {
                        if (dsXml.fieldByName("NFESTATUS").asString().equals("C") || dsXml.fieldByName("NFESTATUS").asString().equals("D"))
                        {
                            dataSet.fieldByName("INFOFISCO").asString(InfAdic.item(j).getTextContent() + "CHAVE DE ACESSO:" + dataSet.fieldByName("CHAVE").asString());
                        } else
                        {
                            dataSet.fieldByName("INFOFISCO").asString(InfAdic.item(j).getTextContent());
                        }
                    }
                    if ("infAdFisco".equals(InfAdic.item(j).getNodeName()))
                    {
                        dataSet.fieldByName("AREAMENSAGEMFISCAL").asString(InfAdic.item(j).getTextContent().replace("$$", "\n").trim());
                        if (InfAdic.item(j).getTextContent().contains("|"))
                        {
                            dataSet.fieldByName("INFOFISCO").asString(dataSet.fieldByName("INFOFISCO").asString() + "\n" + InfAdic.item(j).getTextContent().replace("|", "\n").trim());
                        } else
                        {
                            dataSet.fieldByName("INFOFISCO").asString(dataSet.fieldByName("INFOFISCO").asString() + "\n" + InfAdic.item(j).getTextContent().replace("&&", "\n").trim());
                        }
                    }
                    if ("obsCont".equals(InfAdic.item(j).getNodeName()))
                    {
                        String obsCont = addEspacoStr(InfAdic.item(j).getTextContent(), " ", 48);
                        dataSet.fieldByName("OBSCONT").asString(dataSet.fieldByName("OBSCONT").asString().concat(obsCont.concat("\n\r")));
                    }
                }
            }

            dataSet.fieldByName("NOMECSTCSOSN").asString("CST");
            aux = document.getElementsByTagName("det").item(0);
            if (aux != null)
            {
                NodeList det = aux.getChildNodes();
                for (int i = 0; i < det.getLength(); i++)
                {
                    if (det.item(i).getNodeName().equals("imposto"))
                    {
                        NodeList imp = det.item(i).getChildNodes();
                        for (int ae = 0; ae < imp.getLength(); ae++)
                        {
                            if (imp.item(ae).getNodeName().startsWith("ICMS"))
                            {
                                NodeList imp1 = imp.item(ae).getChildNodes();
                                for (int j = 0; j < imp1.getLength(); j++)
                                {
                                    if (imp1.item(j).getNodeName().startsWith("ICMS"))
                                    {
                                        NodeList imp2 = imp1.item(j).getChildNodes();
                                        for (int k = 0; k < imp2.getLength(); k++)
                                        {
                                            if (imp2.item(k).getNodeName().contains("CSOSN"))
                                            {
                                                dataSet.fieldByName("NOMECSTCSOSN").asString("CSOSN");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (auxDestCli.toString().equals("CONSUMIDOR"))
            {
                auxDestEnde.append(" NÃO IDENTIFICADO");
                auxDestNFCe.append(" NÃO IDENTIFICADO");
            }

            dataSet.fieldByName("DESTINFO").asString(auxDestCli.toString() + auxDestEnde.toString());
            dataSet.fieldByName("DESTINFONFCE").asString(auxDestNFCe.toString());

            aux = document.getElementsByTagName("total").item(0);
            String vnf = "", vicms = "", vprod = "", vdesc = "";
            double vacres = 0.0;
            if (aux != null)
            {
                NodeList total = aux.getChildNodes();
                for (int i = 0; i < total.getLength(); i++)
                {

                    if (total.item(i).getNodeName().equals("ICMSTot"))
                    {
                        NodeList vTrib = total.item(i).getChildNodes();
                        for (int j = 0; j < vTrib.getLength(); j++)
                        {
                            if (vTrib.item(j).getNodeName().equals("vTotTrib"))
                            {
                                dataSet.fieldByName("VTOTTRIB").asDouble(Funcoes.strToDouble(vTrib.item(j).getTextContent()));
                            }
                            if (vTrib.item(j).getNodeName().equals("vProd"))
                            {
                                dataSet.fieldByName("VLRTOTALITENS").asDouble(Funcoes.strToDouble(vTrib.item(j).getTextContent()));
                            }
                            if ("vNF".equals(vTrib.item(j).getNodeName()))
                            {
                                vnf = vTrib.item(j).getTextContent();
                            }
                            if ("vICMS".equals(vTrib.item(j).getNodeName()))
                            {
                                vicms = vTrib.item(j).getTextContent();
                            }
                            if ("vProd".equals(vTrib.item(j).getNodeName()))
                            {
                                vprod = vTrib.item(j).getTextContent();
                            }
                            if ("vDesc".equals(vTrib.item(j).getNodeName()))
                            {
                                vdesc = vTrib.item(j).getTextContent();
                            }
                            if ("vFrete".equals(vTrib.item(j).getNodeName()) && !vTrib.item(j).getTextContent().isEmpty())
                            {
                                vacres += Double.parseDouble(vTrib.item(j).getTextContent());
                            }
                            if ("vSeg".equals(vTrib.item(j).getNodeName()) && !vTrib.item(j).getTextContent().isEmpty())
                            {
                                vacres += Double.parseDouble(vTrib.item(j).getTextContent());
                            }
                            if ("vOutro".equals(vTrib.item(j).getNodeName()) && !vTrib.item(j).getTextContent().isEmpty())
                            {
                                vacres += Double.parseDouble(vTrib.item(j).getTextContent());
                            }
                        }
                    }
                }
            }

            aux = document.getElementsByTagName("entrega").item(0);
            if (aux != null)
            {
                NodeList entr = aux.getChildNodes();
                for (int j = 0; j < entr.getLength(); j++)
                {
                    if ("CPF".equals(entr.item(j).getNodeName()) || "CNPJ".equals(entr.item(j).getNodeName()))
                    {
                        dataSet.fieldByName("ENTRCNPJ").asString(entr.item(j).getTextContent());
                        if ("CPF".equals(entr.item(j).getNodeName()))
                        {
                            dataSet.fieldByName("ENTRCNPJ").asString(Funcoes.formatMaskText(vs, "999.999.999-99;0", entr.item(j).getTextContent()));
                        } else
                        {
                            dataSet.fieldByName("ENTRCNPJ").asString(Funcoes.formatMaskText(vs, "99.999.999/9999-99;0", entr.item(j).getTextContent()));
                        }
                        continue;
                    }
                    if ("xLgr".equals(entr.item(j).getNodeName()))
                    {
                        dataSet.fieldByName("ENTREND").asString(entr.item(j).getTextContent());
                        continue;
                    }
                    if ("nro".equals(entr.item(j).getNodeName()))
                    {
                        dataSet.fieldByName("ENTREND").asString(dataSet.fieldByName("ENTREND").asString() + ", " + entr.item(j).getTextContent());
                        continue;
                    }
                    if ("xCpl".equals(entr.item(j).getNodeName()))
                    {
                        dataSet.fieldByName("ENTREND").asString(dataSet.fieldByName("ENTREND").asString() + " " + entr.item(j).getTextContent());
                        continue;
                    }
                    if ("xBairro".equals(entr.item(j).getNodeName()))
                    {
                        dataSet.fieldByName("ENTRBAIRRO").asString(entr.item(j).getTextContent());
                        continue;
                    }
                    if ("xMun".equals(entr.item(j).getNodeName()))
                    {
                        dataSet.fieldByName("ENTRCIDADE").asString(entr.item(j).getTextContent());
                        continue;
                    }
                    if ("UF".equals(entr.item(j).getNodeName()))
                    {
                        dataSet.fieldByName("ENTRUF").asString(entr.item(j).getTextContent());
                    }
                }
            }
            if ("".equals(dataSet.fieldByName("ENTRCNPJ").asString()))
            {
                dataSet.fieldByName("ENTRCNPJ").asString(dataSet.fieldByName("DESTCNPJ").asString());
            }
            if ("".equals(dataSet.fieldByName("ENTREND").asString()))
            {
                dataSet.fieldByName("ENTREND").asString(dataSet.fieldByName("DESTENDERECO").asString());
            }
            if ("".equals(dataSet.fieldByName("ENTRBAIRRO").asString()))
            {
                dataSet.fieldByName("ENTRBAIRRO").asString(dataSet.fieldByName("DESTBAIRRO").asString());
            }
            if ("".equals(dataSet.fieldByName("ENTRCIDADE").asString()))
            {
                dataSet.fieldByName("ENTRCIDADE").asString(dataSet.fieldByName("DESTCIDADE").asString());
            }
            if ("".equals(dataSet.fieldByName("ENTRUF").asString()))
            {
                dataSet.fieldByName("ENTRUF").asString(dataSet.fieldByName("DESTUF").asString());
            }
            dataSet.fieldByName("DISPLVIA").asString(vs.getParameter("DISPLVIA"));
            //Informação do Protocolo de Autorização
            aux = document.getElementsByTagName("infProt").item(0);
            if (aux != null)
            {
                NodeList infProt = aux.getChildNodes();
                for (int i = 0; i < infProt.getLength(); i++)
                {
                    if ("nProt".equals(infProt.item(i).getNodeName()))
                    {
                        protAutoriza = infProt.item(i).getTextContent();
                    }
                }
            }
            String dtRec = "", hRec = "";
            aux = document.getElementsByTagName("dhRecbto").item(0);
            if (aux != null)
            {
                String dthrec = aux.getTextContent();
                if (dthrec.length() >= 19)
                {
                    String[] td = dthrec.substring(0, 10).split("-");
                    dtRec = td[2].concat("/").concat(td[1]).concat("/").concat(td[0]);
                    hRec = dthrec.substring(11, 19);
                }
            }

            dataSet.fieldByName("NUMPROTOCOLO").asString(protAutoriza + (!dtRec.isEmpty() ? " - " + dtRec + " " + hRec : ""));

            NodeList tagNfRef = document.getElementsByTagName("NFref");
            String nfReferenciados = "";
            for (int it = 0; it < tagNfRef.getLength(); it++)
            {
                aux = document.getElementsByTagName("NFref").item(it);
                if (aux != null)
                {
                    NodeList infNf = aux.getChildNodes();

                    for (int i = 0; i < infNf.getLength(); i++)
                    {
                        if ("refNFe".equals(infNf.item(i).getNodeName()))
                        {
                            String chaveNFe = infNf.item(i).getTextContent();
                            TSQLDataSetEmp sdsNota = TSQLDataSetEmp.create(vs);
                            if (seq_nfe != null && !seq_nfe.isEmpty())
                            {
                                sdsNota.commandText("SELECT NFSAIDA.NF FROM CHAVENFE "
                                        + "  INNER JOIN NFSAIDA ON (NFSAIDA.NFS = CHAVENFE.NFS)"
                                        + "  WHERE CHAVENFE.CHAVE = '" + chaveNFe + "'");
                            } else
                            {
                                sdsNota.commandText("SELECT NFENTRADA.NF FROM CHAVENFET"
                                        + " INNER JOIN NFENTRADA ON (NFENTRADA.NFE = CHAVENFET.NFE)"
                                        + " WHERE CHAVENFET.CHAVE = '" + chaveNFe + "'");
                            }
                            sdsNota.open();
                            nfReferenciados = nfReferenciados.concat("$$Ref. NF-e ").concat(chaveNFe);
                            if (!sdsNota.isEmpty())
                            {
                                nfReferenciados = nfReferenciados.concat(" NF ").concat(sdsNota.fieldByName("NF").asString());
                            }
                        } else if ("refNF".equals(infNf.item(i).getNodeName()))
                        {
                            String sequenciaInfo = "{NUMNF}{AAMM}";
                            NodeList infRefNf = infNf.item(i).getChildNodes();
                            for (int j = 0; j < infRefNf.getLength(); j++)
                            {
                                if ("nNF".equals(infRefNf.item(j).getNodeName()))
                                {
                                    sequenciaInfo = sequenciaInfo.replace("{NUMNF}", "$$Ref. NF " + infRefNf.item(j).getTextContent());
                                }
                                if ("AAMM".equals(infRefNf.item(j).getNodeName()))
                                {
                                    sequenciaInfo = sequenciaInfo.replace("{AAMM}", " de " + identificaAno(vs, infRefNf.item(j).getTextContent()) + "" + infRefNf.item(j).getTextContent());
                                    //COLOCADO O IDENTIFICAANO AO MENOS PARA NAO FICAR FIXO, E CONSEGUIR AO MENOS RETROCEDER NOTAS PARA ANO 19
                                }
                            }
                            nfReferenciados += sequenciaInfo.replace("{NUMNF}", "").replace("{AAMM}", "");
//                        nfReferenciados += sequenciaInfo.concat("\r\n");
                        } else if ("refNFP".equals(infNf.item(i).getNodeName()))
                        {
                            NodeList infRefNf = infNf.item(i).getChildNodes();
                            String sequenciaInfo = "{NUMNF}{SERIE}{AAMM}";
                            for (int j = 0; j < infRefNf.getLength(); j++)
                            {
                                if ("nNF".equals(infRefNf.item(j).getNodeName()))
                                {
                                    sequenciaInfo = sequenciaInfo.replace("{NUMNF}", "$$Ref. NF " + infRefNf.item(j).getTextContent());
                                }
                                if ("serie".equals(infRefNf.item(j).getNodeName()))
                                {
                                    sequenciaInfo = sequenciaInfo.replace("{SERIE}", " Serie " + infRefNf.item(j).getTextContent());
                                }
                                if ("AAMM".equals(infRefNf.item(j).getNodeName()))
                                {
                                    sequenciaInfo = sequenciaInfo.replace("{AAMM}", " de Produtor de " + identificaAno(vs, infRefNf.item(j).getTextContent()) + "" + infRefNf.item(j).getTextContent());
                                }
                            }
                            nfReferenciados += sequenciaInfo.replace("{NUMNF}", "").replace("{SERIE}", "").replace("{AAMM}", "");
                        }
                    }
                }
            }
            if (!nfReferenciados.isEmpty())
            {
                dataSet.fieldByName("INFOFISCO").asString(dataSet.fieldByName("INFOFISCO").asString() + nfReferenciados);
            }
            //necessário manter o limitador de caracteres para funcionar também o limitador de linhas.

            String infoFiscoA = addEspacoStr(dataSet.fieldByName("INFOFISCO").asString().replace("$$$$", "$$").replace("$$", "\n"), "\n", 100, 10).trim().replace("$$", "\n").replace("\r\n", "\n");
            String[] linhasInfoFisco = infoFiscoA.split("\n");
            String[] infos, infoPlaca, infoKM = null;
            String strInfoFisco = "";

            for (int pos = 0; pos < linhasInfoFisco.length; pos++)
            {
                if (!strInfoFisco.contains(linhasInfoFisco[pos]))
                {
                    strInfoFisco += "\n" + linhasInfoFisco[pos];
                }
            }
            dataSet.fieldByName("INFOFISCO").asString(strInfoFisco.replaceFirst("\n", ""));
            for (String s : linhasInfoFisco)
            {
                if (s.contains("PLACA:"))
                {
                    infos = s.split(" ");
                    infoPlaca = infos[0].split("\\:");

                    if (infos.length > 1)
                    {
                        infoKM = infos[1].split("\\:");
                    }

                    if (infoPlaca != null && infoPlaca.length > 1)
                    {
                        dataSet.fieldByName("PLACAINFO").asString("PLACA: " + infoPlaca[1]);
                    } else
                    {
                        dataSet.fieldByName("PLACAINFO").asString(" ");
                    }

                    if (infoKM != null && infoKM.length > 1)
                    {
                        dataSet.fieldByName("KMINFO").asString("KM: " + infoKM[1]);
                    } else
                    {
                        dataSet.fieldByName("KMINFO").asString(" ");
                    }
                }
            }
            if (modelo.equals("email") || modelo.equals("862") || modelo.equals("877") || modelo.equals("1900") || modelo.equals("2025"))
            {
                String URLqr = gerarQRCode(vs, dsXml.fieldByName("NFEXML").asString(), dsXml.fieldByName("CHAVE").asString());
                dataSet.fieldByName("QRCODE").asString(URLqr);
            }
            dataSet.fieldByName("IMPINFOPAGDOIS").asString("nao");
            if (!dataSet.fieldByName("INFOFISCO").asString().isEmpty())
            {
                StringBuilder novaInfo = new StringBuilder();
                int maxLinhas = 10;

                String[] linhas = dataSet.fieldByName("INFOFISCO").asString().split("\n");
                int qtdeLinhas = linhas.length;

                if (qtdeLinhas > maxLinhas)
                {

                    for (int i = 0; i < maxLinhas; i++)
                    {
                        novaInfo.append(linhas[i]).append("\n");
                    }
                    dataSet.fieldByName("INFOFISCO").asString(novaInfo.toString());

                    novaInfo.setLength(0);
                    novaInfo.trimToSize();

                    for (int i = maxLinhas; i < qtdeLinhas; i++)
                    {
                        novaInfo.append(linhas[i]).append("\n");
                    }
                    dataSet.fieldByName("INFOFISCOPAGDOIS").asString(addEspacoStr(novaInfo.toString(), "\n", 150, 9999));
                    dataSet.fieldByName("IMPINFOPAGDOIS").asString("sim");
                }
            }

            dataSet.fieldByName("VNFICMSTOT").asString(vnf);
            dataSet.fieldByName("VITENSTOT").asString(vprod);
            dataSet.fieldByName("VDESCTOT").asString(vdesc);
            dataSet.fieldByName("VACRETOT").asDouble(vacres);

            if (mod.equals("65"))
            {
                dataSet.fieldByName("TROCO").asDouble(getTroco(vs, dsXml.fieldByName("NFS").asString()));
            }

            if ("A".equals(dsXml.fieldByName("NFESTATUS").asString()) || "E".equals(dsXml.fieldByName("NFESTATUS").asString())
                    || tpEmi.equals("2") || tpEmi.equals("5") || tpEmi.equals("6") || tpEmi.equals("7") || tpEmi.equals("9"))
            {
                dataSet.fieldByName("STATUS").asString("sim");
            } else
            {
                dataSet.fieldByName("STATUS").asString("nao");
            }

            if (tpEmi.equals("1"))
            {
                String quebra = impProtocoloLadoQR ? "\n" : " ";
                dataSet.fieldByName("PROTOCOLOAUTORIZA").asString("Protocolo de Autorização:".concat(quebra)
                        .concat(protAutoriza).concat(quebra).concat(impProtocoloLadoQR ? "Data de Autorização\n" : "").concat(dtRec).concat(" ").concat(hRec));
                dataSet.fieldByName("PROTOCOLOAUTORIZANFCE").asString("<ce>Protocolo de Autorizacao: </ce>\n<ce>".concat(protAutoriza).concat(" ").concat(dtRec).concat(" ").concat(hRec).concat("</ce>\n"));
            }
            dataSet.fieldByName("IMPRIMECOMPROVANTEVENDAPRAZO").asString("N");

            boolean imprimeComprovanteVendaPrazo = pf.retornaRegraNegocio(vs, vs.getValor("filial"), 1424).equals("S") || pf.retornaRegraNegocio(vs, vs.getValor("filial"), 1424).equals("COMITEM");

            if (imprimeComprovanteVendaPrazo
                    && (vs.getParameter("IMPRIMECOMPROVANTEVENDAPRAZO") == null || !vs.getParameter("IMPRIMECOMPROVANTEVENDAPRAZO").equals("FALSE")))
            {
                TSQLDataSet dupl = retornaValoresDuplicata(vs);
                if (!dupl.isEmpty())
                {
                    dupl.first();
                    dataSet.fieldByName("IMPRIMECOMPROVANTEVENDAPRAZO").asString("S");
                    dataSet.fieldByName("VALORPRIMEIRAPARCELA").asDouble(dupl.fieldByName("VALOR").asDouble());
                    if (pf.retornaRegraNegocio(vs, vs.getValor("filial"), 1424).equals("COMITEM"))
                    {
                        dataSet.fieldByName("ITENSNOCOMPROVANTEVENDAPRAZO").asString("S");
                    } else
                    {
                        dataSet.fieldByName("ITENSNOCOMPROVANTEVENDAPRAZO").asString("N");
                    }
                    if (!dsXml.fieldByName("NFS").isNull())
                    {
                        TSQLDataSetEmp cdsNFS = TSQLDataSetEmp.create(vs);
                        cdsNFS.commandText("SELECT NFSAIDA.CCLIFOR"
                                + " FROM NFSAIDA"
                                + " WHERE NFSAIDA.NFS=" + dsXml.fieldByName("NFS").asString());
                        cdsNFS.open();
                        dataSet.fieldByName("CCLIFOR").asString(cdsNFS.fieldByName("CCLIFOR").asString());
                    }
                }
            }

            dataSet.post();
            dsXml.next();
        }
        return dataSet;
    }

    public TSQLDataSet retornaProdutosNFCE3(VariavelSessao vs) throws ExcecaoTecnicon
    {
        return (vs.getParameter("VERXMLENTRADA") != null && "S".equals(vs.getParameter("VERXMLENTRADA")) && !"".equals(vs.getParameter("SXMLENTRADA")) ? retProdDANFE_XML(vs) : retProdDANFE(vs));
    }

    public TSQLDataSet retProdDANFE(VariavelSessao vs) throws ExcecaoTecnicon
    {
        ParametrosForm pf = (ParametrosForm) TecniconLookup.lookup("TecniconParametrosForm", "ParametrosFormImpl");
        int casasDecimais = (pf.retornaRegraNegocio(vs, vs.getValor("filial"), 1054).equals("S") ? 3 : (pf.retornaRegraNegocio(vs, vs.getValor("filial"), 1054).equals("6") ? 6 : 2));
        boolean impEspTecQuebraLinha = "S".equalsIgnoreCase(pf.retornaRegraNegocio(vs, vs.getValor("filial"), 2191));//Imprimir especificação técnica com quebra de linha?
        TSQLDataSet tes = TSQLDataSetEmp.create(vs);
        tes.fieldDefs().add("CPRODUTO", "V", 60, false);
        tes.fieldDefs().add("DESCRICAO", "V", 120, false);
        tes.fieldDefs().add("NCM", "V", 60, false);
        tes.fieldDefs().add("CST", "V", 60, false);
        tes.fieldDefs().add("CFOP", "V", 60, false);
        tes.fieldDefs().add("QTDE", "N", 15, false, 3);
        tes.fieldDefs().add("UND", "V", 10, false);
        tes.fieldDefs().add("UNITARIO", "N", 15, false, casasDecimais);
        tes.fieldDefs().add("TOTAL", "N", 17, false, 2);
        tes.fieldDefs().add("BICMS", "N", 17, false, 2);
        tes.fieldDefs().add("ICMS", "N", 17, false, 2);
        tes.fieldDefs().add("IPI", "N", 17, false, 2);
        tes.fieldDefs().add("PICMS", "N", 17, false, 2);
        tes.fieldDefs().add("PIPI", "N", 17, false, 2);
        tes.fieldDefs().add("INFADICIONAL", "V", 500, false);
        tes.fieldDefs().add("ITI", "I", 1, false);
        tes.fieldDefs().add("NFS", "I", 1, false);
        tes.fieldDefs().add("NFE", "I", 1, false);
        tes.fieldDefs().add("PRODUTOCOMB", "V", 20, false);
        tes.createDataSet();
        String seq_nfs = vs.getParameter("NFS");
        String seq_nfe = vs.getParameter("NFE");
        if (!validaParameter(vs.getParameter("SNFECONSULTAITEM")) && seq_nfs == null && seq_nfe == null)
        {
            throw new ExcecaoTecnicon(vs, "Código da nota não informado!");
        }
        TClientDataSet dsXml = retornaCdsXML(vs, seq_nfs, seq_nfe);
        dsXml.first();
        while (!dsXml.eof())
        {
            Document document;
            try
            {
                document = Utils.strToDoc(vs, dsXml.fieldByName("NFEXML").asString());
            } catch (ParserConfigurationException | SAXException | IOException ex)
            {
                throw new ExcecaoTecnicon(vs, "Falha na leitura do XML, verifique se o xml esta valido."
                        + "\nEfetue o download do xml no SEFAZ para realizar a manutenção\nerro:" + ex.getMessage());
            }
            Node aux = document.getElementsByTagName("infNFe").item(0);
            if (aux != null && aux.getAttributes() != null && aux.getAttributes().getNamedItem("versao") != null)
            {
                if (aux.getAttributes().getNamedItem("versao").getTextContent().equals("4.00"))
                {
                    return (TSQLDataSet) TClassLoader.execMethod("TecniconNFe4/RelatorioDANFE", "retornaProdutosNFCE3", vs);
                } else if (!aux.getAttributes().getNamedItem("versao").getTextContent().equals("3.10"))
                {
                    return (TSQLDataSet) TClassLoader.execMethod("TecniconNFe2/RetornaProdutosDanfe", "obterSQLDataSet", vs);
                }
            }
            NodeList det = document.getElementsByTagName("det");
            for (int g = 0; g < det.getLength(); g++)
            {
                tes.insert();
                tes.fieldByName("NFS").asInteger(dsXml.fieldByName("NFS").asInteger());
                tes.fieldByName("NFE").asInteger(dsXml.fieldByName("NFE").asInteger());

                NodeList item = det.item(g).getChildNodes();
                for (int i = 0; i < item.getLength(); i++)
                {

                    if (item.item(i).getNodeName().equals("prod"))
                    {
                        NodeList prod = item.item(i).getChildNodes();

                        tes.fieldByName("ITI").asInteger(1);
                        for (int j = 0; j < prod.getLength(); j++)
                        {

                            if (prod.item(j).getNodeName().equals("cProd"))
                            {
                                tes.fieldByName("CPRODUTO").asString(prod.item(j).getTextContent());
                            }
                            if (prod.item(j).getNodeName().equals("xProd"))
                            {
                                String prodab = addEspacoStr(prod.item(j).getTextContent(), " ", 48).trim();
                                tes.fieldByName("DESCRICAO").asString(prodab);
                            }
                            if (prod.item(j).getNodeName().equals("qCom"))
                            {
                                tes.fieldByName("QTDE").asDouble(Funcoes.strToDouble((prod.item(j).getTextContent() != null && !prod.item(j).getTextContent().equals("") ? prod.item(j).getTextContent() : "0")));
                            }
                            if ("NCM".equals(prod.item(j).getNodeName()))
                            {
                                tes.fieldByName("NCM").asString(prod.item(j).getTextContent());
                                continue;
                            }
                            if (prod.item(j).getNodeName().equals("uCom"))
                            {
                                tes.fieldByName("UND").asString(prod.item(j).getTextContent());
                            }
                            if (prod.item(j).getNodeName().equals("vUnCom"))
                            {
                                tes.fieldByName("UNITARIO").asDouble(Funcoes.strToDouble(prod.item(j).getTextContent() != null && !prod.item(j).getTextContent().equals("") ? prod.item(j).getTextContent() : "0"));
                            }
                            if (prod.item(j).getNodeName().equals("vProd"))
                            {
                                tes.fieldByName("TOTAL").asDouble(Funcoes.strToDouble(prod.item(j).getTextContent() != null && !prod.item(j).getTextContent().equals("") ? prod.item(j).getTextContent() : "0"));
                            }
                            if ("CFOP".equals(prod.item(j).getNodeName()))
                            {
                                tes.fieldByName("CFOP").asString(prod.item(j).getTextContent());
                            }
                            if ("comb".equalsIgnoreCase(prod.item(j).getNodeName()) || (prod.item(j).getNodeName().equals("xProd") && prod.item(j).getTextContent().contains(" ONU ")))
                            {
                                tes.fieldByName("PRODUTOCOMB").asString(";font-weight:bold;");
                            }
                        }
                    }
                    if ("imposto".equals(item.item(i).getNodeName()))
                    {
                        NodeList imposto = item.item(i).getChildNodes();
                        for (int m = 0; m < imposto.getLength(); m++)
                        {
                            if ("ICMS".equals(imposto.item(m).getNodeName()))
                            {
                                NodeList icmsi = imposto.item(m).getChildNodes();
                                for (int j = 0; j < icmsi.getLength(); j++)
                                {
                                    if (icmsi.item(j).getNodeName().contains("ICMS"))
                                    {
                                        NodeList icms = icmsi.item(j).getChildNodes();
                                        String orig = "";
                                        for (int k = 0; k < icms.getLength(); k++)
                                        {
                                            if ("orig".equals(icms.item(k).getNodeName()))
                                            {
                                                orig = icms.item(k).getTextContent();
                                            }
                                        }
                                        for (int k = 0; k < icms.getLength(); k++)
                                        {
                                            if ("CST".equals(icms.item(k).getNodeName()) || "CSOSN".equals(icms.item(k).getNodeName()))
                                            {
                                                orig = orig.concat(icms.item(k).getTextContent());
                                                if ("CSOSN".equals(icms.item(k).getNodeName()))
                                                {
                                                    tes.fieldByName("CST").asString(Funcoes.car(orig, 4, "D", "0"));
                                                } else
                                                {
                                                    tes.fieldByName("CST").asString(Funcoes.car(orig, 3, "D", "0"));
                                                }
                                                continue;
                                            }
                                            if ("vBC".equals(icms.item(k).getNodeName()))
                                            {
                                                tes.fieldByName("BICMS").asString(icms.item(k).getTextContent());
                                                continue;
                                            }
                                            if ("vICMS".equals(icms.item(k).getNodeName()))
                                            {
                                                tes.fieldByName("ICMS").asString(icms.item(k).getTextContent());
                                            }
                                            if ("pICMS".equals(icms.item(k).getNodeName()))
                                            {
                                                tes.fieldByName("PICMS").asString(icms.item(k).getTextContent());
                                            }
                                        }
                                    }
                                }
                            }
                            if ("IPI".equals(imposto.item(m).getNodeName()))
                            {
                                NodeList ipi = imposto.item(m).getChildNodes();
                                for (int k = 0; k < ipi.getLength(); k++)
                                {
                                    if ("IPITrib".equalsIgnoreCase(ipi.item(k).getNodeName()))
                                    {
                                        NodeList IPITrib = ipi.item(k).getChildNodes();
                                        for (int l = 0; l < IPITrib.getLength(); l++)
                                        {
                                            if ("vIPI".equalsIgnoreCase(IPITrib.item(l).getNodeName()))
                                            {
                                                tes.fieldByName("IPI").asString(IPITrib.item(l).getTextContent());
                                                continue;
                                            }
                                            if ("pIPI".equalsIgnoreCase(IPITrib.item(l).getNodeName()))
                                            {
                                                tes.fieldByName("PIPI").asString(IPITrib.item(l).getTextContent());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if ("infAdProd".equals(item.item(i).getNodeName()))
                    {
                        String infAdReplaced = item.item(i).getTextContent().replaceAll("(<!\\[.*]]>)", "");
                        String infAdic;
                        if (infAdReplaced.contains("Bin:"))
                        {
                            infAdic = infAdReplaced.trim().replace("\\$\\$", "\n");
                        } else
                        {
                            infAdic = addEspacoStr(infAdReplaced.replaceAll("\\$\\$\\$\\$", "\\$\\$").replaceAll("\\$\\$", "\n"), " ", 48);
                            if (infAdic.endsWith("\n"))
                            {
                                infAdic = infAdic.substring(0, infAdic.length() - 1);
                            }
                            if (impEspTecQuebraLinha)
                            {
                                if (!infAdic.startsWith("\n"))
                                {
                                    infAdic = "\n" + infAdic;
                                }
                            }
                        }

                        tes.fieldByName("INFADICIONAL").asString(infAdic);
                    }

                }
                tes.post();
            }
            dsXml.next();
        }
        return tes;
    }

    public TSQLDataSet retProdDANFE_XML(VariavelSessao vs) throws ExcecaoTecnicon
    {
        ParametrosForm pf = (ParametrosForm) TecniconLookup.lookup("TecniconParametrosForm", "ParametrosFormImpl");
        int casasDecimais = (pf.retornaRegraNegocio(vs, vs.getValor("filial"), 1054).equals("S") ? 3 : (pf.retornaRegraNegocio(vs, vs.getValor("filial"), 1054).equals("6") ? 6 : 2));
        TSQLDataSet tes = TSQLDataSetEmp.create(vs);
        tes.fieldDefs().add("CPRODUTO", "V", 60, false);
        tes.fieldDefs().add("DESCRICAO", "V", 120, false);
        tes.fieldDefs().add("NCM", "V", 60, false);
        tes.fieldDefs().add("CST", "V", 60, false);
        tes.fieldDefs().add("CFOP", "V", 60, false);
        tes.fieldDefs().add("QTDE", "N", 15, false, 3);
        tes.fieldDefs().add("UND", "V", 10, false);
        tes.fieldDefs().add("UNITARIO", "N", 15, false, casasDecimais);
        tes.fieldDefs().add("TOTAL", "N", 17, false, 2);
        tes.fieldDefs().add("BICMS", "N", 17, false, 2);
        tes.fieldDefs().add("ICMS", "N", 17, false, 2);
        tes.fieldDefs().add("IPI", "N", 17, false, 2);
        tes.fieldDefs().add("PICMS", "N", 17, false, 2);
        tes.fieldDefs().add("PIPI", "N", 17, false, 2);
        tes.fieldDefs().add("INFADICIONAL", "V", 500, false);
        tes.fieldDefs().add("ITI", "I", 1, false);
        tes.fieldDefs().add("NFS", "I", 1, false);
        tes.fieldDefs().add("NFE", "I", 1, false);
        tes.fieldDefs().add("PRODUTOCOMB", "V", 20, false);
        tes.createDataSet();
        TClientDataSet xmlEntrada = TClientDataSet.create(vs, "XMLENTRADA");
        xmlEntrada.createDataSet();
        xmlEntrada.condicao("WHERE XMLENTRADA.SXMLENTRADA = " + vs.getParameter("SXMLENTRADA"));
        xmlEntrada.open();
        xmlEntrada.first();
        while (!xmlEntrada.eof())
        {
            Document document;
            try
            {
                document = Utils.strToDoc(vs, xmlEntrada.fieldByName("XML").asString());
            } catch (ParserConfigurationException | SAXException | IOException ex)
            {
                throw new ExcecaoTecnicon(vs, "Falha na leitura do XML, verifique se o xml esta valido."
                        + "\nEfetue o download do xml no SEFAZ para realizar a manutenção\nerro:" + ex.getMessage());
            }
            Node aux = document.getElementsByTagName("infNFe").item(0);
            if (aux != null && aux.getAttributes() != null && aux.getAttributes().getNamedItem("versao") != null)
            {
                if (aux.getAttributes().getNamedItem("versao").getTextContent().equals("4.00"))
                {
                    return (TSQLDataSet) TClassLoader.execMethod("TecniconNFe4/RelatorioDANFE", "retornaProdutosNFCE3", vs);
                } else if (!aux.getAttributes().getNamedItem("versao").getTextContent().equals("3.10") && !aux.getAttributes().getNamedItem("versao").getTextContent().equals("4.00"))
                {
                    return (TSQLDataSet) TClassLoader.execMethod("TecniconNFe2/RetornaProdutosDanfe", "obterSQLDataSet", vs);
                }
            }
            NodeList det = document.getElementsByTagName("det");
            for (int g = 0; g < det.getLength(); g++)
            {
                tes.insert();

                NodeList item = det.item(g).getChildNodes();
                for (int i = 0; i < item.getLength(); i++)
                {

                    if (item.item(i).getNodeName().equals("prod"))
                    {
                        NodeList prod = item.item(i).getChildNodes();

                        tes.fieldByName("ITI").asInteger(1);
                        for (int j = 0; j < prod.getLength(); j++)
                        {

                            if (prod.item(j).getNodeName().equals("cProd"))
                            {
                                tes.fieldByName("CPRODUTO").asString(prod.item(j).getTextContent());
                            }
                            if (prod.item(j).getNodeName().equals("xProd"))
                            {
                                String prodab = addEspacoStr(prod.item(j).getTextContent(), " ", 48).trim();
                                tes.fieldByName("DESCRICAO").asString(prodab);
                            }
                            if (prod.item(j).getNodeName().equals("qCom"))
                            {
                                tes.fieldByName("QTDE").asDouble(Funcoes.strToDouble((prod.item(j).getTextContent() != null && !prod.item(j).getTextContent().equals("") ? prod.item(j).getTextContent() : "0")));
                            }
                            if ("NCM".equals(prod.item(j).getNodeName()))
                            {
                                tes.fieldByName("NCM").asString(prod.item(j).getTextContent());
                                continue;
                            }
                            if (prod.item(j).getNodeName().equals("uCom"))
                            {
                                tes.fieldByName("UND").asString(prod.item(j).getTextContent());
                            }
                            if (prod.item(j).getNodeName().equals("vUnCom"))
                            {
                                tes.fieldByName("UNITARIO").asDouble(Funcoes.strToDouble(prod.item(j).getTextContent() != null && !prod.item(j).getTextContent().equals("") ? prod.item(j).getTextContent() : "0"));
                            }
                            if (prod.item(j).getNodeName().equals("vProd"))
                            {
                                tes.fieldByName("TOTAL").asDouble(Funcoes.strToDouble(prod.item(j).getTextContent() != null && !prod.item(j).getTextContent().equals("") ? prod.item(j).getTextContent() : "0"));
                            }
                            if ("CFOP".equals(prod.item(j).getNodeName()))
                            {
                                tes.fieldByName("CFOP").asString(prod.item(j).getTextContent());
                            }
                            if ("comb".equalsIgnoreCase(prod.item(j).getNodeName()) || (prod.item(j).getNodeName().equals("xProd") && prod.item(j).getTextContent().contains(" ONU ")))
                            {
                                tes.fieldByName("PRODUTOCOMB").asString(";font-weight:bold;");
                            }
                        }
                    }
                    if ("imposto".equals(item.item(i).getNodeName()))
                    {
                        NodeList imposto = item.item(i).getChildNodes();
                        for (int m = 0; m < imposto.getLength(); m++)
                        {
                            if ("ICMS".equals(imposto.item(m).getNodeName()))
                            {
                                NodeList icmsi = imposto.item(m).getChildNodes();
                                for (int j = 0; j < icmsi.getLength(); j++)
                                {
                                    if (icmsi.item(j).getNodeName().contains("ICMS"))
                                    {
                                        NodeList icms = icmsi.item(j).getChildNodes();
                                        String orig = "";
                                        for (int k = 0; k < icms.getLength(); k++)
                                        {
                                            if ("orig".equals(icms.item(k).getNodeName()))
                                            {
                                                orig = icms.item(k).getTextContent();
                                            }
                                        }
                                        for (int k = 0; k < icms.getLength(); k++)
                                        {
                                            if ("CST".equals(icms.item(k).getNodeName()) || "CSOSN".equals(icms.item(k).getNodeName()))
                                            {
                                                orig = orig.concat(icms.item(k).getTextContent());
                                                if ("CSOSN".equals(icms.item(k).getNodeName()))
                                                {
                                                    tes.fieldByName("CST").asString(Funcoes.car(orig, 4, "D", "0"));
                                                } else
                                                {
                                                    tes.fieldByName("CST").asString(Funcoes.car(orig, 3, "D", "0"));
                                                }
                                                continue;
                                            }
                                            if ("vBC".equals(icms.item(k).getNodeName()))
                                            {
                                                tes.fieldByName("BICMS").asString(icms.item(k).getTextContent());
                                                continue;
                                            }
                                            if ("vICMS".equals(icms.item(k).getNodeName()))
                                            {
                                                tes.fieldByName("ICMS").asString(icms.item(k).getTextContent());
                                            }
                                            if ("pICMS".equals(icms.item(k).getNodeName()))
                                            {
                                                tes.fieldByName("PICMS").asString(icms.item(k).getTextContent());
                                            }
                                        }
                                    }
                                }
                            }
                            if ("IPI".equals(imposto.item(m).getNodeName()))
                            {
                                NodeList ipi = imposto.item(m).getChildNodes();
                                for (int k = 0; k < ipi.getLength(); k++)
                                {
                                    if ("IPITrib".equalsIgnoreCase(ipi.item(k).getNodeName()))
                                    {
                                        NodeList IPITrib = ipi.item(k).getChildNodes();
                                        for (int l = 0; l < IPITrib.getLength(); l++)
                                        {
                                            if ("vIPI".equalsIgnoreCase(IPITrib.item(l).getNodeName()))
                                            {
                                                tes.fieldByName("IPI").asString(IPITrib.item(l).getTextContent());
                                                continue;
                                            }
                                            if ("pIPI".equalsIgnoreCase(IPITrib.item(l).getNodeName()))
                                            {
                                                tes.fieldByName("PIPI").asString(IPITrib.item(l).getTextContent());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if ("infAdProd".equals(item.item(i).getNodeName()))
                    {
                        String infAdReplaced = item.item(i).getTextContent().replaceAll("(<!\\[.*]]>)", "");
                        String infAdic;
                        if (infAdReplaced.contains("Bin:"))
                        {
                            infAdic = infAdReplaced.trim().replace("$$", "\n");
                        } else
                        {
                            infAdic = addEspacoStr(infAdReplaced.replaceAll("\\$\\$", "\n"), " ", 48).trim();
                        }

                        tes.fieldByName("INFADICIONAL").asString(infAdic);
                    }

                }
                tes.post();
            }
            xmlEntrada.next();
        }
        return tes;
    }

    public TSQLDataSet retornaFormaPagamento(VariavelSessao vs) throws ExcecaoTecnicon
    {
        String seq_nfs = vs.getParameter("NFS");
        String seq_nfe = vs.getParameter("NFE");
        if (!validaParameter(vs.getParameter("SNFECONSULTAITEM")) && seq_nfs == null && seq_nfe == null)
        {
            throw new ExcecaoTecnicon(vs, "Código da nota não informado!");
        }
        TSQLDataSet tes = TSQLDataSetEmp.create(vs);
        tes.fieldDefs().add("FORMAPAGAMENTO", "FTSTRING", 50, true);
        tes.fieldDefs().add("VLRFORMAPAGAMENTO", "FTDOUBLE", 15, true, 2);
        tes.open();
        TClientDataSet CDSc = retornaCdsXML(vs, seq_nfs, seq_nfe);
        Document document;
        try
        {
            document = Utils.strToDoc(vs, CDSc.fieldByName("NFEXML").asString());
        } catch (ParserConfigurationException | SAXException | IOException ex)
        {
            throw new ExcecaoTecnicon(vs, ex);
        }
        Node aux = document.getElementsByTagName("infNFe").item(0);
        if (aux != null)
        {
            String version = aux.getAttributes().getNamedItem("versao").getTextContent();
            if (version.equals("4.00"))
            {
                return (TSQLDataSet) TClassLoader.execMethod("TecniconNFe4/RelatorioDANFE", "retornaFormaPagamento", vs);
            }
        }
        NodeList te = document.getElementsByTagName("pag");
        if (te.getLength() > 0)
        {
            for (int i = 0; i < te.getLength(); i++)
            {
                Node npag = te.item(i);
                tes.insert();
                NodeList pags = npag.getChildNodes();
                for (int j = 0; j < pags.getLength(); j++)
                {
                    if (pags.item(j).getNodeName().equals("tPag"))
                    {
                        String forma = "";
                        switch (pags.item(j).getTextContent())
                        {
                            case "01":
                                forma = "Dinheiro";
                                break;
                            case "02":
                                forma = "Cheque";
                                break;
                            case "03":
                                forma = "Cartao de Credito";
                                break;
                            case "04":
                                forma = "Cartao de Debito";
                                break;
                            case "05":
                                forma = "Credito Loja";
                                break;
                            case "10":
                                forma = "Vale Alimentacao";
                                break;
                            case "11":
                                forma = "Vale Refeicao";
                                break;
                            case "12":
                                forma = "Vale Presente";
                                break;
                            case "13":
                                forma = "Vale Combustivel";
                                break;
                            case "99":
                                forma = "Outros";
                                break;
                        }
                        tes.fieldByName("FORMAPAGAMENTO").asString(forma);
                    }
                    if (pags.item(j).getNodeName().equals("vPag"))
                    {
                        tes.fieldByName("VLRFORMAPAGAMENTO").asString(pags.item(j).getTextContent());
                    }
                }
                tes.post();
            }
        }
        te = document.getElementsByTagName("infAdic");
        if (te.getLength() > 0)
        {
            tes.indexFieldNames("FORMAPAGAMENTO");
            for (int i = 0; i < te.getLength(); i++)
            {
                Node npag = te.item(i);
                NodeList pags = npag.getChildNodes();
                for (int j = 0; j < pags.getLength(); j++)
                {
                    if (pags.item(j).getNodeName().equals("obsCont"))
                    {
                        String forma = "";
                        switch (pags.item(j).getAttributes().getNamedItem("xCampo").getTextContent())
                        {
                            case "01":
                                forma = "Dinheiro";
                                break;
                            case "02":
                                forma = "Cheque";
                                break;
                            case "03":
                                forma = "Cartao de Credito";
                                break;
                            case "04":
                                forma = "Cartao de Debito";
                                break;
                            case "05":
                                forma = "Credito Loja";
                                break;
                            case "10":
                                forma = "Vale Alimentacao";
                                break;
                            case "11":
                                forma = "Vale Refeicao";
                                break;
                            case "12":
                                forma = "Vale Presente";
                                break;
                            case "13":
                                forma = "Vale Combustivel";
                                break;
                            case "99":
                                forma = "Outros";
                                break;
                        }

                        if (!forma.isEmpty() && pags.item(j).getFirstChild().getNodeName().equals("xTexto"))
                        {

                            tes.edit();
                            tes.fieldByName("FORMAPAGAMENTO").asString(forma);
                            tes.fieldByName("VLRFORMAPAGAMENTO").asString(pags.item(j).getFirstChild().getTextContent());
                            tes.post();
                        }
                    }
                }
            }
        }

        return tes;
    }

    public TSQLDataSet retornaValoresDuplicata(VariavelSessao vs) throws ExcecaoTecnicon
    {
        TSQLDataSetEmp sds = TSQLDataSetEmp.create(vs);
        sds.fieldDefs().add("DUPLICATA", "V", 30, false);
        sds.fieldDefs().add("VENCIMENTO", "D", 10, false);
        sds.fieldDefs().add("VALOR", "N", 15, false, 2);
        sds.fieldDefs().add("IMPRIME", "V", 1, false);
        if (vs.getParameter("NFS") != null && !vs.getParameter("NFS").trim().isEmpty())
        {
            sds.commandText("SELECT RECEBER.DUPLICATA #TCONC# ' ' #TCONC# RECEBER.PARCELA AS DUPLICATA,"
                    + " RECEBER.VCTO AS VENCIMENTO, RECEBER.VALOR, 'S' AS IMPRIME\n"
                    + " FROM RECEBER"
                    + " WHERE RECEBER.NFS= " + vs.getParameter("NFS")
                    + " UNION ALL \n"
                    + " SELECT '' AS DUPLICATA, FATURAR.VCTO AS VENCIMENTO, FATURAR.VALOR, 'F' AS IMPRIME \n"
                    + " FROM FATURAR \n"
                    + " WHERE FATURAR.NFS=" + vs.getParameter("NFS"));
        }
        sds.open();
        return sds;
    }

    public TSQLDataSet retornaMediaQuilometragem(VariavelSessao vs) throws ExcecaoTecnicon
    {
        TSQLDataSetEmp retorno = TSQLDataSetEmp.create(vs);
        retorno.fieldDefs().add("MEDIAKM", "ftString", 20, false);
        retorno.createDataSet();

        TSQLDataSetEmp sqlEmp = TSQLDataSetEmp.create(vs);
        sqlEmp.createDataSet();
        if (!Funcoes.varIsNull(vs.getParameter("NFS")))
        {
            sqlEmp.commandText(
                    "   SELECT NFSAIDA.CCLIFOR, CLIFOREND.MEDIAKM "
                    + " FROM NFSAIDA "
                    + " INNER JOIN CLIFOREND ON (CLIFOREND.CCLIFOR=NFSAIDA.CCLIFOR AND CLIFOREND.FILIALCF=NFSAIDA.FILIALCF)"
                    + " WHERE NFSAIDA.NFS = " + vs.getParameter("NFS"));
            sqlEmp.open();

            if (!sqlEmp.isEmpty() && "S".equals(sqlEmp.fieldByName("MEDIAKM").asString()))
            {
                String cliente = sqlEmp.fieldByName("CCLIFOR").asString();

                sqlEmp.close();
                sqlEmp.commandText("WITH NOTA AS (\n"
                        + " SELECT FIRST 2 \n"
                        + "     NFSAIDACLI.SNFSAIDACLI, \n"
                        + "     NFSAIDACLI.KM, \n"
                        + "     NFSAIDA.NFS, \n"
                        + "     NFSAIDA.CCLIFOR, \n"
                        + "     SUM(COALESCE(NFSITEM.QTDE,0)) AS LITROS \n"
                        + " FROM NFSAIDACLI\n"
                        + " INNER JOIN NFSAIDA ON (NFSAIDA.NFS=NFSAIDACLI.NFS)\n"
                        + " INNER JOIN NFSITEM ON (NFSITEM.NFS=NFSAIDA.NFS AND NFSITEM.BICO IS NOT NULL)\n"
                        + " WHERE NFSAIDA.CCLIFOR = " + cliente + "\n"
                        + " GROUP BY NFSAIDACLI.SNFSAIDACLI, \n"
                        + "     NFSAIDACLI.KM, \n"
                        + "     NFSAIDA.NFS, \n"
                        + "     NFSAIDA.CCLIFOR\n"
                        + " ORDER BY NFSAIDACLI.SNFSAIDACLI DESC\n"
                        + ")\n"
                        + "\n"
                        + "SELECT \n"
                        + "     NOTA.SNFSAIDACLI, \n"
                        + "     NOTA.KM, \n"
                        + "     NOTA.NFS, \n"
                        + "     NOTA.CCLIFOR, \n"
                        + "     NOTA.LITROS\n"
                        + "FROM NOTA");
                sqlEmp.open();

                double kmAtual = 0.0;
                double kmAnterior = 0.0;
                double litros = 0.0;
                double mediaKm;
                if (sqlEmp.recordCount() > 0)
                {
                    sqlEmp.first();
                    while (!sqlEmp.eof())
                    {
                        if (sqlEmp.recNo() == 1)
                        {
                            kmAtual = sqlEmp.fieldByName("KM").asDouble();
                        } else if (sqlEmp.recNo() == 2)
                        {
                            kmAnterior = sqlEmp.fieldByName("KM").asDouble();
                            litros = sqlEmp.fieldByName("LITROS").asDouble();
                        }
                        sqlEmp.next();
                    }

                    if (litros != 0.0)
                    {
                        mediaKm = (kmAtual - kmAnterior) / litros;
                    } else
                    {
                        mediaKm = 0;
                    }

                    retorno.insert();
                    retorno.fieldByName("MEDIAKM").asString("MÉDIA KM: " + mediaKm);
                    retorno.post();
                } else
                {
                    semMedia(retorno);
                }

            } else
            {
                semMedia(retorno);
            }

        } else
        {
            semMedia(retorno);
        }
        return retorno;
    }

    public void semMedia(TSQLDataSetEmp retorno) throws ExcecaoTecnicon
    {
        retorno.insert();
        retorno.fieldByName("MEDIAKM").asString("");
        retorno.post();
    }

    public TClientDataSet retornaCdsXML(VariavelSessao vs, String nfs, String nfe) throws ExcecaoTecnicon
    {
        TClientDataSet CDSc = TClientDataSet.create(vs);
        CDSc.createDataSet();
        TSQLDataSetTec dataSetTec = TSQLDataSetTec.create(vs);
        dataSetTec.close();
        dataSetTec.commandText("SELECT EMPRESA.BANCODADOS FROM EMPRESA WHERE EMPRESA.CEMPRESA=" + vs.getValor("empresa"));
        dataSetTec.open();

        if (validaParameter(vs.getParameter("VARIASNF")))
        {
            if (nfs == null || nfs.isEmpty())
            {
                nfs = "-1";
            }
            if (nfe == null || nfe.isEmpty())
            {
                nfe = "-1";
            }
            if (dataSetTec.fieldByName("BANCODADOS").asString().equals("FIREBIRD"))
            {
                CDSc.commandText(
                        "SELECT CHAVENFE.SCHAVENFE AS CNF, CHAVENFE.NPROTOCOLO, CHAVENFE.DTSTATUS, CHAVENFE.HRSTATUS, CHAVENFE.NFESTATUS, "
                        + " CHAVENFE.DANFEIMP, CHAVENFE.CHAVE, CAST(CHAVENFEXML.NFEXML AS BLOB SUB_TYPE 1 SEGMENT SIZE 80)AS NFEXML, NFENTRADA.NF,"
                        + " NFENTRADA.NFE, NULL AS NFS"
                        + " FROM CHAVENFE "
                        + " INNER JOIN NFENTRADA ON (NFENTRADA.NFE = CHAVENFE.NFE) "
                        + " LEFT JOIN CHAVENFEXML ON (CHAVENFEXML.SCHAVENFE=CHAVENFE.SCHAVENFE)"
                        + " WHERE CHAVENFE.NFE IN (" + nfe + ")"
                        + " UNION ALL"
                        + " SELECT CHAVENFE.SCHAVENFE AS CNF, CHAVENFE.NPROTOCOLO, CHAVENFE.DTSTATUS, CHAVENFE.HRSTATUS, CHAVENFE.NFESTATUS, "
                        + " CHAVENFE.DANFEIMP,CHAVENFE.CHAVE, CAST(CHAVENFEXML.NFEXML AS BLOB SUB_TYPE 1 SEGMENT SIZE 80)AS NFEXML, NFSAIDA.NF,"
                        + " NULL AS NFE, NFSAIDA.NFS"
                        + " FROM CHAVENFE"
                        + " INNER JOIN NFSAIDA ON (NFSAIDA.NFS = CHAVENFE.NFS) "
                        + " LEFT JOIN CHAVENFEXML ON (CHAVENFEXML.SCHAVENFE=CHAVENFE.SCHAVENFE)");
            } else
            {
                CDSc.commandText(
                        "SELECT CHAVENFE.SCHAVENFE AS CNF, CHAVENFE.NPROTOCOLO, CHAVENFE.DTSTATUS, CHAVENFE.HRSTATUS, CHAVENFE.NFESTATUS, "
                        + " CHAVENFE.DANFEIMP, CHAVENFE.CHAVE, CHAVENFEXML.NFEXML, NFENTRADA.NF,"
                        + " NFENTRADA.NFE, NULL AS NFS"
                        + " FROM CHAVENFE "
                        + " INNER JOIN NFENTRADA ON (NFENTRADA.NFE = CHAVENFE.NFE) "
                        + " LEFT JOIN CHAVENFEXML ON (CHAVENFEXML.SCHAVENFE=CHAVENFE.SCHAVENFE)"
                        + " WHERE CHAVENFE.NFE IN (" + nfe + ")"
                        + " UNION ALL"
                        + " SELECT CHAVENFE.SCHAVENFE AS CNF, CHAVENFE.NPROTOCOLO, CHAVENFE.DTSTATUS, CHAVENFE.HRSTATUS, CHAVENFE.NFESTATUS, "
                        + " CHAVENFE.DANFEIMP, CHAVENFE.CHAVE, CHAVENFEXML.NFEXML, NFSAIDA.NF,"
                        + " NULL AS NFE, NFSAIDA.NFS"
                        + " FROM CHAVENFE "
                        + " INNER JOIN NFSAIDA ON (NFSAIDA.NFS = CHAVENFE.NFS) "
                        + " LEFT JOIN CHAVENFEXML ON (CHAVENFEXML.SCHAVENFE=CHAVENFE.SCHAVENFE)");
            }
            CDSc.condicao(" WHERE CHAVENFE.NFS IN (" + nfs + ")");
            CDSc.open();
            return CDSc;
        }

        if (validaParameter(vs.getParameter("SNFECONSULTAITEM")))
        {
            CDSc.commandText("SELECT NFECONSULTAITEM.SNFECONSULTAITEM AS CNF, CAST('0' AS VARCHAR(1)) AS NPROTOCOLO, NFECONSULTAITEM.DATAEMISSAO AS DTSTATUS, CAST('A' AS VARCHAR(1)) AS NFESTATUS, "
                    + " NULL AS HRSTATUS, CAST('0' AS VARCHAR(1)) AS DANFEIMP, NFECONSULTAITEM.CHNFE AS CHAVE, NFECONSULTAITEM.XMLNFE AS NFEXML, NFECONSULTAITEM.NF,"
                    + " NULL AS NFE, NULL AS NFS"
                    + " FROM NFECONSULTAITEM ");
            CDSc.condicao(" WHERE NFECONSULTAITEM.SNFECONSULTAITEM=" + vs.getParameter("SNFECONSULTAITEM"));
            CDSc.open();
            vs.addParametros("NFETERCEIROS", "TRUE");

            if (CDSc.fieldByName("NFEXML").asString().equals(""))
            {
                throw new ExcecaoTecnicon(vs, "NF selecionada ainda não foi baixada!");
            }

            return CDSc;
        }

        if (!validaParameter(nfe) && !validaParameter(nfs))
        {
            throw new ExcecaoTecnicon(vs, "Nenhum sequencial encontrado para realizar a impressão do DANFE.");
        }

        if (dataSetTec.fieldByName("BANCODADOS").asString().equals("FIREBIRD"))
        {
            if (nfe != null && !"".equals(nfe))
            {
                CDSc.commandText(
                        "SELECT CHAVENFE.SCHAVENFE AS CNF, CHAVENFE.NPROTOCOLO, CHAVENFE.DTSTATUS, CHAVENFE.HRSTATUS, CHAVENFE.NFESTATUS, "
                        + " CHAVENFE.DANFEIMP, CHAVENFE.CHAVE, CAST(CHAVENFEXML.NFEXML AS BLOB SUB_TYPE 1 SEGMENT SIZE 80)AS NFEXML, NFENTRADA.NF,"
                        + " NFENTRADA.NFE, NULL AS NFS"
                        + " FROM CHAVENFE "
                        + " INNER JOIN NFENTRADA ON (NFENTRADA.NFE = CHAVENFE.NFE) "
                        + " LEFT JOIN CHAVENFEXML ON (CHAVENFEXML.SCHAVENFE=CHAVENFE.SCHAVENFE)"
                        + " ");
                CDSc.condicao(" WHERE CHAVENFE.NFE=" + nfe);
            } else
            {
                CDSc.commandText(
                        "SELECT CHAVENFE.SCHAVENFE AS CNF, CHAVENFE.NPROTOCOLO, CHAVENFE.DTSTATUS, CHAVENFE.HRSTATUS, CHAVENFE.NFESTATUS, "
                        + " CHAVENFE.DANFEIMP,CHAVENFE.CHAVE, CAST(CHAVENFEXML.NFEXML AS BLOB SUB_TYPE 1 SEGMENT SIZE 80)AS NFEXML, NFSAIDA.NF,"
                        + " NULL AS NFE, NFSAIDA.NFS"
                        + " FROM CHAVENFE"
                        + " INNER JOIN NFSAIDA ON (NFSAIDA.NFS = CHAVENFE.NFS) "
                        + " LEFT JOIN CHAVENFEXML ON (CHAVENFEXML.SCHAVENFE=CHAVENFE.SCHAVENFE)"
                        + " ");
                CDSc.condicao(" WHERE CHAVENFE.NFS=" + nfs);
            }
        } else if (nfe != null && !"".equals(nfe))
        {
            CDSc.commandText(
                    "SELECT CHAVENFE.SCHAVENFE AS CNF, CHAVENFE.NPROTOCOLO, CHAVENFE.DTSTATUS, CHAVENFE.HRSTATUS, CHAVENFE.NFESTATUS, "
                    + " CHAVENFE.DANFEIMP, CHAVENFE.CHAVE, CHAVENFEXML.NFEXML, NFENTRADA.NF,"
                    + " NFENTRADA.NFE, NULL AS NFS"
                    + " FROM CHAVENFE "
                    + " INNER JOIN NFENTRADA ON (NFENTRADA.NFE = CHAVENFE.NFE) "
                    + " LEFT JOIN CHAVENFEXML ON (CHAVENFEXML.SCHAVENFE=CHAVENFE.SCHAVENFE)"
                    + " ");
            CDSc.condicao(" WHERE CHAVENFE.NFE=" + nfe);
        } else
        {
            CDSc.commandText(
                    "SELECT CHAVENFE.SCHAVENFE AS CNF, CHAVENFE.NPROTOCOLO, CHAVENFE.DTSTATUS, CHAVENFE.NFESTATUS, "
                    + " CHAVENFE.HRSTATUS, CHAVENFE.DANFEIMP, CHAVENFE.CHAVE, CHAVENFEXML.NFEXML, NFSAIDA.NF,"
                    + " NULL AS NFE, NFSAIDA.NFS"
                    + " FROM CHAVENFE "
                    + " INNER JOIN NFSAIDA ON (NFSAIDA.NFS = CHAVENFE.NFS) "
                    + " LEFT JOIN CHAVENFEXML ON (CHAVENFEXML.SCHAVENFE=CHAVENFE.SCHAVENFE)"
                    + " ");
            CDSc.condicao(" WHERE CHAVENFE.NFS=" + nfs);
        }
        CDSc.open();

        if (CDSc.isEmpty() && !"".equals(nfe))
        {
            CDSc.close();
            if (dataSetTec.fieldByName("BANCODADOS").asString().equals("FIREBIRD"))
            {
                CDSc.commandText(
                        "SELECT CHAVENFET.SCHAVENFET AS CNF,  'A' AS NFESTATUS,"
                        + " '' AS DANFEIMP, CHAVENFET.CHAVE, CAST(COALESCE(CHAVENFET.XMLNFE,NFECONSULTAITEM.XMLNFE) AS BLOB SUB_TYPE 1 SEGMENT SIZE 80) AS NFEXML, NFENTRADA.NF,"
                        + " NFENTRADA.NFE, NULL AS NFS"
                        + " FROM CHAVENFET"
                        + " INNER JOIN NFENTRADA ON (NFENTRADA.NFE = CHAVENFET.NFE)"
                        + " LEFT JOIN NFECONSULTAITEM ON (NFECONSULTAITEM.CHNFE=CHAVENFET.CHAVE) ");
                CDSc.condicao(" WHERE CHAVENFET.NFE=" + nfe);
            } else
            {

                CDSc.commandText(
                        "SELECT CHAVENFET.SCHAVENFET AS CNF, 'A' AS NFESTATUS,"
                        + " '' AS DANFEIMP, CHAVENFET.CHAVE, COALESCE(CHAVENFET.XMLNFE,NFECONSULTAITEM.XMLNFE) AS NFEXML, NFENTRADA.NF,"
                        + " NFENTRADA.NFE, NULL AS NFS"
                        + " FROM CHAVENFET"
                        + " INNER JOIN NFENTRADA ON (NFENTRADA.NFE = CHAVENFET.NFE)"
                        + " LEFT JOIN NFECONSULTAITEM ON (NFECONSULTAITEM.CHNFE=CHAVENFET.CHAVE) ");
                CDSc.condicao(" WHERE CHAVENFET.NFE=" + nfe);

            }
            vs.addParametros("NFETERCEIROS", "TRUE");
            CDSc.open();
        } else
        {
            vs.addParametros("NFETERCEIROS", "FALSE");
        }
        return CDSc;
    }

    private static String sha1(String input) throws NoSuchAlgorithmException
    {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < result.length; i++)
        {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

    public void executaImpressao(VariavelSessao vs) throws ExcecaoTecnicon, IOException
    {
        String modelo = validaNFCE3(vs);
        vs.addParametros("esp", "S");
        if (!modelo.equals("0"))
        {
            if (modelo.equals("email"))
            {
                vs.addParametros("relatorioesp", "862");
                vs.addParametros("r", "862");
            } else
            {
                vs.addParametros("relatorioesp", modelo);
                vs.addParametros("r", modelo);
            }

            String msgErro = "da NF" + (vs.getParameter("NFS") != null && !vs.getParameter("NFS").isEmpty() ? "S "
                    + vs.getParameter("NFS") : "E " + vs.getParameter("NFE")) + ": ";
            //imprimir numero de vias
            ParametrosForm pf = (ParametrosForm) TecniconLookup.lookup("TecniconParametrosForm", "ParametrosFormImpl");
            vs.addParametros("DISPLVIA", pf.retornaRegraNegocio(vs, vs.getValor("filial"), 82));
            if ((vs.getParameter("GERAIMPRESSAO") == null || (vs.getParameter("GERAIMPRESSAO").toUpperCase().equals("TRUE"))) && !modelo.equals("email"))
            {
                try
                {
                    //comprovantes de venda a prazo deverá estar junto a cada relatório de NFCe, assim será impresso junto na chamada desse método
                    TClassLoader.execMethod("criaFormPai/FuncoesUteis", "imprimeRelatorio", vs);
                    incNumDanfe(vs);
                } catch (Exception e)
                {
                    TClassLoader.execMethod("DmUtil/DmUtil", "enviarMensagemMyAlert", vs, "Ajustar Impressora",
                            "Falha ao imprimir DANFE " + msgErro + e.getMessage(), vs.getValor("cusuario"), "IMPRESSORA", "{\"SIMPRESSORA\":\"" + vs.getParameter("slImpressoras") + "\"}");
                }
            }

            boolean enviaremail = true;
            if (vs.getParameter("ENVIAREMAILCLI") != null && vs.getParameter("ENVIAREMAILCLI").equals("N"))
            {
                enviaremail = false;
            }

            if (enviaremail && vs.getParameter("JAENVIAEMAIL") != null && vs.getParameter("JAENVIAEMAIL").toUpperCase().equals("TRUE"))
            {
                try
                {
                    enviaEmailNFCE3(vs);
                } catch (Exception e)
                {
                    if (Funcoes.validaVSCampo(vs, "NOALERT"))
                    {
                        throw new ExcecaoTecnicon(vs, "Falha ao enviar DANFE por e-mail " + e.getMessage());
                    } else
                    {
                        TClassLoader.execMethod("DmUtil/DmUtil", "enviarMensagemMyAlert", vs, "Configurar e-mail",
                                "Falha ao enviar DANFE por e-mail " + msgErro + e.getMessage(), vs.getValor("cusuario"), "USUARIOEMAIL", "{\"CUSUARIO\":\"" + vs.getValor("cusuario") + "\"}");
                    }
                }
            }
            vs.addParametros("GERAIMPRESSAO", null);
        }
    }

    public void incNumDanfe(VariavelSessao vs) throws ExcecaoTecnicon
    {
        TSQLDataSetEmp chavenfe = TSQLDataSetEmp.create(vs);
        chavenfe.execSQL(" UPDATE CHAVENFE SET CHAVENFE.DANFEIMP = COALESCE(DANFEIMP,0)+1 WHERE CHAVENFE."
                + ((vs.getParameter("NFS") != null) && !vs.getParameter("NFS").equals("") ? "NFS=" + vs.getParameter("NFS") : "NFE=" + vs.getParameter("NFE")));
    }

    public void retPdfDanfe(VariavelSessao vs) throws ExcecaoTecnicon
    {
        String modelo = validaNFCE3(vs);
        vs.addParametros("esp", "S");
        if (!modelo.equals("0"))
        {
            if (modelo.equals("email"))
            {
                vs.addParametros("relatorioesp", "862");
                vs.addParametros("r", "862");
            } else
            {
                vs.addParametros("relatorioesp", modelo);
                vs.addParametros("r", modelo);
            }
            TClassLoader.execMethod("TecniconRelatorioEsp/RelatorioEsp", "gerarRelatorio", vs);
        }
    }

    public void enviaImp(VariavelSessao vs) throws ExcecaoTecnicon, IOException
    {
        TClientDataSet USUARIO = TClientDataSet.create(vs, "USUARIO");
        USUARIO.createDataSet();
        USUARIO.condicao("WHERE USUARIO.CUSUARIO = " + vs.getValor("cusuario"));
        USUARIO.open();

        TClientDataSet IMP = TClientDataSet.create(vs, "IMPRESSORA");
        IMP.createDataSet();
        IMP.condicao("WHERE IMPRESSORA.IMPRESSORA = '" + USUARIO.fieldByName("IMPRESSORA").asString() + "'");
        IMP.open();
        String NIMP = IMP.fieldByName("IMPRESSORA").asString();

        Map<String, String[]> parameterMap = new HashMap<>();
        for (String key : vs.getParameterMap().keySet())
        {
            parameterMap.put(key, new String[]
            {
                vs.getParameter(key)
            });
        }
        parameterMap.put("slImpressoras", new String[]
        {
            NIMP
        });

        vs.setParameterMap(parameterMap);
        TClassLoader.execMethod("criaFormPai/FuncoesUteis", "imprimeRelatorio", vs);

        //      TClassLoader.execMethod("TecniconRelatorioEsp/RelatorioEsp", "gerarRelatorio", vs);
        //byte[] rel = Funcoes.convertStringToByte(vs.getRetornoOK());
        //todos os modelos impressos devem enviar email
        if (!Funcoes.strToBool(vs.getParameter("JAIMPRIME")))
        {
            vs.setRetornoOK("true");
        } else
        {
            vs.setRetornoOK("false");
        }
        enviaEmailNFCE3(vs);
    }

    public String validaNFCE3(VariavelSessao vs) throws ExcecaoTecnicon
    {
        String num_nfs = vs.getParameter("NFS");
        String num_nfe = vs.getParameter("NFE");

        if (!validaParameter(vs.getParameter("SNFECONSULTAITEM")) && num_nfs == null && num_nfe == null)
        {
            throw new ExcecaoTecnicon(vs, "Sequencial de Nota não informado!");
        }

        Document document = null;
        int i = 2;
        while (i > 0)
        {
            i--;
            TClientDataSet CDSc = retornaCdsXML(vs, num_nfs, num_nfe);
            if (CDSc.isEmpty())
            {
                throw new ExcecaoTecnicon(vs, "Nota/XML não encontrado!");
            }
            if (CDSc.fieldByName("NFEXML").asString().equals(""))
            {
                throw new ExcecaoTecnicon(vs, "Seq. NF " + (num_nfs != null ? "saída \"" + num_nfs : "entrada \"" + num_nfe) + "\" não foi gerado arquivo XML!");
            }

            try
            {
                document = Utils.strToDocCorreto(CDSc.fieldByName("NFEXML").asString(), "NFS=" + num_nfs + " NFE=" + num_nfe + " PAR=" + vs.getParameter("SNFECONSULTAITEM") + " EMP=" + vs.getValor("empresa"));
                break;
            } catch (Exception ex)
            {
                try
                {
                    document = Utils.strToDoc(vs, CDSc.fieldByName("NFEXML").asString());
                } catch (Exception e)
                {
                    throw new ExcecaoTecnicon(vs, "Falha na leitura do XML, verifique se o xml esta valido."
                            + "\nEfetue o download do xml no SEFAZ para realizar a manutenção\nerro:" + e.getMessage(), e);
                }

            }
        }

        if (document == null)
        {
            throw new ExcecaoTecnicon(vs, "Erro ao fazer parse do XML!");
        }

        if (document.getElementsByTagName("infNFe").item(0) == null)
        {
            throw new ExcecaoTecnicon(vs, "XML Informado não é de Nota Fiscal");
        }
        String versao = document.getElementsByTagName("infNFe").item(0).getAttributes().getNamedItem("versao").getTextContent();

        if (document.getElementsByTagName("infNFe").item(0).getAttributes().getNamedItem("versao") != null)
        {
            if (!versao.equals("3.10") && !versao.equals("4.00"))
            {
                return "772";
            }
        }
        Node aux = document.getElementsByTagName("tpImp").item(0);
        if (aux == null)
        {
            throw new ExcecaoTecnicon(vs, "Modelo de impressão não definido no XML!");
        }
        TClientDataSet cdsParNFCE = TClientDataSet.create(vs);
        cdsParNFCE.createDataSet();
        cdsParNFCE.commandText("SELECT PARAMETRONFE.RELIMPNFISC FROM PARAMETRONFE");
        cdsParNFCE.condicao(" WHERE PARAMETRONFE.CFILIAL = " + vs.getValor("filial"));
        cdsParNFCE.open();

        boolean impressaoNFCeImpNaoFiscal = cdsParNFCE.fieldByName("RELIMPNFISC").asString() != null && "S".equals(cdsParNFCE.fieldByName("RELIMPNFISC").asString());

        String modelo = aux.getTextContent();
        switch (modelo)
        {
            case "0":
            case "1":
                modelo = "772";
                break;
            case "2":
                modelo = "868";
                break;
            case "3":
                modelo = "772";//não foi desenvolvida a denfe simplificado;
                break;
            case "4":
                if (impressaoNFCeImpNaoFiscal)
                {
                    modelo = "877";
                    ParametrosForm pf = (ParametrosForm) TecniconLookup.lookup("TecniconParametrosForm", "ParametrosFormImpl");
                    //Tamanho bobina impressão de NFC-e
                    if ("55".equals(pf.retornaRegraNegocio(vs, vs.getValor("filial"), 2063)))
                    {
                        modelo = "2025";
                    }
                    if ("S".equals(pf.retornaRegraNegocio(vs, vs.getValor("filial"), 1871))) //Impressao NFC-e Modelo Reduzido
                    {
                        //TODO: modelo reduzido para bobina de 55mm
                        modelo = "1900";
                    }
                } else
                {
                    modelo = "862";
                }
                break;
            case "5":
                modelo = "email";
                break;
        }
        return modelo;
    }

    public void enviaEmailNFCE3(VariavelSessao vs) throws ExcecaoTecnicon, IOException
    {
        TSQLDataSetEmp ccoFornecedor = TSQLDataSetEmp.create(vs);
        TClientDataSet cdsCom2 = TClientDataSet.create(vs);
        cdsCom2.createDataSet();

        TClientDataSet contatoCli = TClientDataSet.create(vs, "CONTATOCLI");
        contatoCli.createDataSet();

        ParametrosForm pf = (ParametrosForm) TecniconLookup.lookup("TecniconParametrosForm", "ParametrosFormImpl");
        boolean enviaautomatico = ((vs.getParameter("EMAILAUTOMATICO")) != null && (vs.getParameter("EMAILAUTOMATICO").toUpperCase().equals("TRUE")));

        cdsCom2.close();
        ccoFornecedor.close();
        if (vs.getParameter("NFE") != null && !"".equals(vs.getParameter("NFE")))
        {
            ccoFornecedor.commandText(
                    " SELECT CONTATOCLI.EMAIL "
                    + " FROM NFEITEM "
                    + " INNER JOIN PRODUTO ON (PRODUTO.CPRODUTO = NFEITEM.CPRODUTO) "
                    + " INNER JOIN SUBGRUPOCLIFORENDNFE SGCNFE ON (SGCNFE.CSUBGRUPO = PRODUTO.CSUBGRUPO) "
                    + " INNER JOIN CLIFOREND FORN ON (FORN.CCLIFOR = SGCNFE.CCLIFOR AND FORN.FILIALCF = SGCNFE.FILIALCF) "
                    + " INNER JOIN CONTATOCLI ON (CONTATOCLI.CCLIFOR = FORN.CCLIFOR AND CONTATOCLI.FILIALCF = FORN.FILIALCF AND CONTATOCLI.VINCOMPRA IN('V','N','F')) "
                    + " WHERE NFEITEM.NFE = " + vs.getParameter("NFE")
                    + " GROUP BY CONTATOCLI.EMAIL ");

            cdsCom2.commandText(
                    " SELECT NFENTRADA.CCLIFOR, NFENTRADA.FILIALCF, NFENTRADA.CTRANSP, CLIFOREND.NFEENVIADANFEXML,"
                    + " NFENTRADA.FILIALTRANSP, NFENTRADA.CMODELONF, NFENTRADA.DATA, NFENTRADA.NF, CHAVENFE.CHAVE, CLIFOREND.EMAIL, NFENTRADA.CFILIAL, COALESCE(CLIFOREND.SUFRAMA,'') AS SUFRAMA"
                    + " FROM NFENTRADA"
                    + "     INNER JOIN CLIFOREND ON (CLIFOREND.CCLIFOR = NFENTRADA.CCLIFOR AND CLIFOREND.FILIALCF = NFENTRADA.FILIALCF)"
                    + "     LEFT JOIN CHAVENFE ON (CHAVENFE.NFE=NFENTRADA.NFE)");
            cdsCom2.condicao(" WHERE NFENTRADA.NFE=" + vs.getParameter("NFE"));
        } else if (vs.getValorParametro("NFS") != null && !vs.getValorParametro("NFS").equals(""))
        {
            ccoFornecedor.commandText(
                    " SELECT CONTATOCLI.EMAIL "
                    + " FROM NFSITEM "
                    + " INNER JOIN PRODUTO ON (PRODUTO.CPRODUTO = NFSITEM.CPRODUTO) "
                    + " INNER JOIN SUBGRUPOCLIFORENDNFE SGCNFE ON (SGCNFE.CSUBGRUPO = PRODUTO.CSUBGRUPO) "
                    + " INNER JOIN CLIFOREND FORN ON (FORN.CCLIFOR = SGCNFE.CCLIFOR AND FORN.FILIALCF = SGCNFE.FILIALCF) "
                    + " INNER JOIN CONTATOCLI ON (CONTATOCLI.CCLIFOR = FORN.CCLIFOR AND CONTATOCLI.FILIALCF = FORN.FILIALCF AND CONTATOCLI.VINCOMPRA IN('V','N','F')) "
                    + " WHERE NFSITEM.NFS = " + vs.getValorParametro("NFS")
                    + " GROUP BY CONTATOCLI.EMAIL ");

            cdsCom2.commandText(" SELECT NFSAIDA.CCLIFOR, NFSAIDA.FILIALCF, NFSAIDA.CTRANSP, CLIFOREND.NFEENVIADANFEXML,"
                    + " NFSAIDA.FILIALTRANSP, NFSAIDA.CMODELONF, NFSAIDA.DATA, NFSAIDA.NF, CHAVENFE.CHAVE, CLIFOREND.EMAIL, NFSAIDA.CFILIAL, COALESCE(CLIFOREND.SUFRAMA,'') AS SUFRAMA"
                    + " FROM NFSAIDA"
                    + "     INNER JOIN CLIFOREND ON (CLIFOREND.CCLIFOR = NFSAIDA.CCLIFOR AND CLIFOREND.FILIALCF = NFSAIDA.FILIALCF)"
                    + "     LEFT JOIN CHAVENFE ON (CHAVENFE.NFS=NFSAIDA.NFS)");
            cdsCom2.condicao(" WHERE NFSAIDA.NFS=" + vs.getValorParametro("NFS"));
        } else if (vs.getParameter("NFS") != null && !vs.getParameter("NFS").equals(""))
        {
            ccoFornecedor.commandText(
                    " SELECT CONTATOCLI.EMAIL "
                    + " FROM NFSITEM "
                    + " INNER JOIN PRODUTO ON (PRODUTO.CPRODUTO = NFSITEM.CPRODUTO) "
                    + " INNER JOIN SUBGRUPOCLIFORENDNFE SGCNFE ON (SGCNFE.CSUBGRUPO = PRODUTO.CSUBGRUPO) "
                    + " INNER JOIN CLIFOREND FORN ON (FORN.CCLIFOR = SGCNFE.CCLIFOR AND FORN.FILIALCF = SGCNFE.FILIALCF) "
                    + " INNER JOIN CONTATOCLI ON (CONTATOCLI.CCLIFOR = FORN.CCLIFOR AND CONTATOCLI.FILIALCF = FORN.FILIALCF AND CONTATOCLI.VINCOMPRA IN('V','N','F')) "
                    + " WHERE NFSITEM.NFS = " + vs.getParameter("NFS")
                    + " GROUP BY CONTATOCLI.EMAIL ");

            cdsCom2.commandText(" SELECT NFSAIDA.CCLIFOR, NFSAIDA.FILIALCF, NFSAIDA.CTRANSP, CLIFOREND.NFEENVIADANFEXML,"
                    + " NFSAIDA.FILIALTRANSP, NFSAIDA.CMODELONF, NFSAIDA.DATA, NFSAIDA.NF, CHAVENFE.CHAVE, CLIFOREND.EMAIL, NFSAIDA.CFILIAL, COALESCE(CLIFOREND.SUFRAMA,'') AS SUFRAMA"
                    + " FROM NFSAIDA"
                    + "     INNER JOIN CLIFOREND ON (CLIFOREND.CCLIFOR = NFSAIDA.CCLIFOR AND CLIFOREND.FILIALCF = NFSAIDA.FILIALCF)"
                    + "     LEFT JOIN CHAVENFE ON (CHAVENFE.NFS=NFSAIDA.NFS)");
            cdsCom2.condicao(" WHERE NFSAIDA.NFS=" + vs.getParameter("NFS"));
        }
        cdsCom2.open();
        ccoFornecedor.open();

        String CCO = RelatorioDANFE.join(ccoFornecedor, ";", "EMAIL");
        String CCLIFOR = cdsCom2.fieldByName("CCLIFOR").asString();
        String FILIALCF = cdsCom2.fieldByName("FILIALCF").asString();
        String numeroFinal = cdsCom2.fieldByName("NF").asString();
        String filial = cdsCom2.fieldByName("CFILIAL").asString();
        String emailEnviaDanfeXml = cdsCom2.fieldByName("NFEENVIADANFEXML").asString();
        String SUFRAMA = cdsCom2.fieldByName("SUFRAMA").asString();

        String codTransp = cdsCom2.fieldByName("CTRANSP").asString();
        String filialTransp = cdsCom2.fieldByName("FILIALTRANSP").asString();
        String XCCEMAIL = "";
        if (!codTransp.isEmpty() && !filialTransp.isEmpty())
        {
            TSQLDataSetEmp cdsContato = TSQLDataSetEmp.create(vs);
            cdsContato.commandText("SELECT CONTATOCLI.EMAIL FROM CONTATOCLI ");
            cdsContato.condicao(" WHERE CONTATOCLI.CCLIFOR = " + codTransp + " AND CONTATOCLI.FILIALCF = " + filialTransp + " AND CONTATOCLI.VINCOMPRA LIKE '%N%'");
            cdsContato.open();
            cdsContato.first();
            while (!cdsContato.eof())
            {
                if (Funcoes.pos(cdsContato.fieldByName("EMAIL").asString(), XCCEMAIL) == 0)
                {
                    if (Funcoes.length(XCCEMAIL) != 0)
                    {
                        XCCEMAIL = XCCEMAIL + ";";
                    }
                    XCCEMAIL = XCCEMAIL + cdsContato.fieldByName("EMAIL").asString();
                }
                cdsContato.next();
            }
            if (pf.retornaRegraNegocio(vs, vs.getValor("filial"), 1802).equals("S") && (!Funcoes.varIsNull(vs.getParameter("NFS")) || !Funcoes.varIsNull(vs.getValorParametro("NFS"))))
            {
                String nfs;
                if (!Funcoes.varIsNull(vs.getParameter("NFS")))
                {
                    nfs = vs.getParameter("NFS");
                } else
                {
                    nfs = vs.getValorParametro("NFS");
                }
                if (!Funcoes.varIsNull(nfs))
                {
                    cdsContato.close();
                    cdsContato.commandText("SELECT CONTATOCLI.EMAIL "
                            + " FROM NFSDESPACHO "
                            + " INNER JOIN CONTATOCLI ON (CONTATOCLI.CCLIFOR = NFSDESPACHO.CTRANSP AND CONTATOCLI.FILIALCF = NFSDESPACHO.FILIALTRANSP  AND CONTATOCLI.VINCOMPRA LIKE '%N%' ) ");
                    cdsContato.condicao(" WHERE NFSDESPACHO.NFS = " + vs.getParameter("NFS"));
                    cdsContato.open();
                    while (!cdsContato.eof())
                    {
                        if (Funcoes.pos(cdsContato.fieldByName("EMAIL").asString(), XCCEMAIL) == 0)
                        {
                            if (Funcoes.length(XCCEMAIL) != 0)
                            {
                                XCCEMAIL = XCCEMAIL + ";";
                            }
                            XCCEMAIL = XCCEMAIL + cdsContato.fieldByName("EMAIL").asString();
                        }
                        cdsContato.next();
                    }
                }
            }
        }

        boolean considerarEmailEndFilial = pf.retornaRegraNegocio(vs, vs.getValor("filial"), 2068).equals("S");
        String XENDEMAIL = "";

        TClientDataSet emailFilialCliente = TClientDataSet.create(vs, "CLIFOREND");
        emailFilialCliente.createDataSet();

        if (!considerarEmailEndFilial)
        {
//        String XENDEMAIL = ""; // JOSE SPOHR - 156783; deve pegar o email apenas do contato do cliente.
            contatoCli.condicao(
                    " WHERE CONTATOCLI.CCLIFOR=" + CCLIFOR
                    + " AND CONTATOCLI.FILIALCF=" + FILIALCF
                    + " AND CONTATOCLI.VINCOMPRA LIKE '%N%'");
            contatoCli.open();
            contatoCli.first();

            while (!contatoCli.eof())
            {
                if (Funcoes.pos(contatoCli.fieldByName("EMAIL").asString(), XENDEMAIL) == 0)
                {
                    if (Funcoes.length(XENDEMAIL) != 0)
                    {
                        XENDEMAIL = XENDEMAIL + ";";
                    }
                    XENDEMAIL = XENDEMAIL + contatoCli.fieldByName("EMAIL").asString();
                }
                contatoCli.next();
            }

        } else
        {
            emailFilialCliente.condicao(
                    " WHERE CLIFOREND.CCLIFOR=" + CCLIFOR
                    + " AND CLIFOREND.FILIALCF=" + FILIALCF);
            emailFilialCliente.open();
            emailFilialCliente.first();

            while (!emailFilialCliente.eof())
            {
                if (Funcoes.pos(emailFilialCliente.fieldByName("EMAIL").asString(), XENDEMAIL) == 0)
                {
                    if (Funcoes.length(XENDEMAIL) != 0)
                    {
                        XENDEMAIL = XENDEMAIL + ";";
                    }
                    XENDEMAIL = XENDEMAIL + emailFilialCliente.fieldByName("EMAIL").asString();
                }
                emailFilialCliente.next();
            }
        }

        // DanfeConsulta.java - linha 577 - 08/03/2016
        TSQLDataSetEmp XCD = TSQLDataSetEmp.create(vs);
        XCD.close();
        if (vs.getParameter("NFS") != null && !vs.getParameter("NFS").equals("") && pf.retornaRegraNegocio(vs, vs.getValor("filial"), 80).equals("S"))
        {
            XCD.commandText("SELECT DISTINCT PEDIDO.EMAIL"
                    + " FROM PEDIDO"
                    + " INNER JOIN PEDIDOITEM ON (PEDIDOITEM.PEDIDO = PEDIDO.PEDIDO)"
                    + " INNER JOIN NFSITEM ON (NFSITEM.PEDIDOITEM=PEDIDOITEM.PEDIDOITEM)"
                    + " WHERE PEDIDO.EMAIL IS NOT NULL AND NFSITEM.NFS =" + vs.getParameter("NFS"));
            XCD.open();
            XCD.first();
            while (!XCD.eof())
            {
                // SIPMANN - 11/07/12 - SOL 62629
                if (!XENDEMAIL.contains(XCD.fieldByName("EMAIL").asString()))
                {
                    if (Funcoes.length(XENDEMAIL) != 0)
                    {
                        XENDEMAIL += ";";
                    }
                    XENDEMAIL += XCD.fieldByName("EMAIL").asString();
                }
                XCD.next();
            }
        }

        String cc = "";
        TSQLDataSetEmp cdsParNFE = TSQLDataSetEmp.create(vs);
        cdsParNFE.commandText("SELECT PARAMETRONFE.ENVIANFEVENDEDOR, PARAMETRONFE.ENVIARNFEVENDEDORTELE, ADDTEXTEMAIL, TEXTOPADEMAIL FROM PARAMETRONFE");
        cdsParNFE.condicao(" WHERE PARAMETRONFE.CFILIAL = " + filial);
        cdsParNFE.open();
        if (cdsParNFE.fieldByName("ENVIANFEVENDEDOR").asString().equals("S"))
        {
            TSQLDataSetEmp cdsVendedor = TSQLDataSetEmp.create(vs);
            if (vs.getParameter("NFE") != null && !"".equals(vs.getParameter("NFE")))
            {
                cdsVendedor.commandText("select VENDEDOR.EMAIL"
                        + " from VENDEDOR"
                        + " inner join NFEVENDEDOR on (NFEVENDEDOR.CVENDEDOR = VENDEDOR.CVENDEDOR)"
                        + " where NFEVENDEDOR.NFE =" + vs.getParameter("NFE"));
            } else if (vs.getValorParametro("NFS") != null && !vs.getValorParametro("NFS").equals(""))
            {
                cdsVendedor.commandText("select VENDEDOR.EMAIL"
                        + " from VENDEDOR"
                        + " inner join NFSVENDEDOR on (NFSVENDEDOR.CVENDEDOR = VENDEDOR.CVENDEDOR)"
                        + " where NFSVENDEDOR.NFS =" + vs.getValorParametro("NFS"));
            } else if (vs.getParameter("NFS") != null && !vs.getParameter("NFS").equals(""))
            {
                cdsVendedor.commandText("select VENDEDOR.EMAIL"
                        + " from VENDEDOR"
                        + " inner join NFSVENDEDOR on (NFSVENDEDOR.CVENDEDOR = VENDEDOR.CVENDEDOR)"
                        + " where NFSVENDEDOR.NFS =" + vs.getParameter("NFS"));
            }
            cdsVendedor.open();
            while (!cdsVendedor.eof())
            {
                if (Funcoes.pos(cdsVendedor.fieldByName("EMAIL").asString(), cc) == 0)
                {
                    if (Funcoes.length(cc) != 0)
                    {
                        cc = cc + ";";
                    }
                    cc = cc + cdsVendedor.fieldByName("EMAIL").asString();
                }
                cdsVendedor.next();
            }
        }
        if (cdsParNFE.fieldByName("ENVIARNFEVENDEDORTELE").asString().equals("S"))
        {
            TSQLDataSetEmp cdsVendTele = TSQLDataSetEmp.create(vs);
            if (vs.getParameter("NFE") != null && !"".equals(vs.getParameter("NFE")))
            {
                cdsVendTele.fieldByName(" SELECT NFENTRADA.CVENDTELE, VENDEDOR.EMAIL "
                        + " FROM NFENTRADA "
                        + " INNER JOIN VENDEDOR ON (VENDEDOR.CVENDEDOR = NFENTRADA.CVENDTELE) "
                        + " WHERE NFENTRADA.NFE = " + vs.getParameter("NFE"));
            } else if (vs.getValorParametro("NFS") != null && !vs.getValorParametro("NFS").equals(""))
            {
                cdsVendTele.commandText(" SELECT NFSAIDA.CVENDTELE, VENDEDOR.EMAIL "
                        + " FROM NFSAIDA "
                        + " INNER JOIN VENDEDOR ON (VENDEDOR.CVENDEDOR = NFSAIDA.CVENDTELE) "
                        + " WHERE NFSAIDA.NFS = " + vs.getValorParametro("NFS"));
            } else if (vs.getParameter("NFS") != null && !vs.getParameter("NFS").equals(""))
            {
                cdsVendTele.commandText(" SELECT NFSAIDA.CVENDTELE, VENDEDOR.EMAIL "
                        + " FROM NFSAIDA "
                        + " INNER JOIN VENDEDOR ON (VENDEDOR.CVENDEDOR = NFSAIDA.CVENDTELE) "
                        + " WHERE NFSAIDA.NFS = " + vs.getParameter("NFS"));
            }
            cdsVendTele.open();
            while (!cdsVendTele.eof())
            {
                if (Funcoes.pos(cdsVendTele.fieldByName("EMAIL").asString(), cc) == 0)
                {
                    if (Funcoes.length(cc) != 0)
                    {
                        cc = cc + ";";
                    }
                    cc = cc + cdsVendTele.fieldByName("EMAIL").asString();
                }
                cdsVendTele.next();
            }
        }
        vs.addParametros("IMPRIMECOMPROVANTEVENDAPRAZO", "FALSE");
        TClientDataSet dataSet = retornaCdsXML(vs, vs.getParameter("NFS"), vs.getParameter("NFE"));

        String nomeMod = (vs.getParameter("relatorioesp") != null && vs.getParameter("relatorioesp").equals("862") ? "NFCe" : "NFe");
        Map<String, byte[]> anexos = new HashMap<>();
        //Enviar primeiro o XML
        if (emailEnviaDanfeXml.isEmpty() || emailEnviaDanfeXml.equals("A") || emailEnviaDanfeXml.equals("D"))
        {
            vs.addParametros("JAIMPRIME", "FALSE");
            TClassLoader.execMethod("TecniconRelatorioEsp/RelatorioEsp", "gerarRelatorio", vs);
            byte[] rel = Funcoes.convertStringToByte(vs.getRetornoOK());
            anexos.put("DANFE_".concat(nomeMod).concat("_").concat(dataSet.fieldByName("NF").asString()).concat(".pdf"), rel);
        }

        if (emailEnviaDanfeXml.isEmpty() || emailEnviaDanfeXml.equals("A") || emailEnviaDanfeXml.equals("X"))
        {
            byte[] xml = (dataSet.fieldByName("NFEXML").asString()).getBytes();
            anexos.put(nomeMod.concat(dataSet.fieldByName("CHAVE").asString()).concat("-nfe.xml"), xml);
        }

        /*Para pegar a carta de correcao eletronica*/
        ControllerCCe cce = ControllerCCe.getInstance();
        byte[] xmlCCe;
        if (cce.anexarCCe(vs))
        {
            TClientDataSet dataSetCCe = cce.getCCe(vs);
            if (!dataSetCCe.fieldByName("CCEXML").asString().trim().isEmpty())
            {
                xmlCCe = (dataSetCCe.fieldByName("CCEXML").asString()).getBytes();
                anexos.put("CC-e".concat(dataSet.fieldByName("CHAVE").asString()).concat(".xml"), xmlCCe);

                TClientDataSet USUARIO = TClientDataSet.create(vs, "USUARIO");
                USUARIO.createDataSet();
                USUARIO.condicao("WHERE USUARIO.CUSUARIO = " + vs.getValor("cusuario"));
                USUARIO.open();

                TSQLDataSetTec IMP = TSQLDataSetTec.create(vs);
                IMP.commandText("SELECT FIRST 1 IMPRESSORA.IMPRESSORA, IMPRESSORA.SIMPRESSORA FROM IMPRESSORA");
                IMP.condicao("WHERE IMPRESSORA.IMPRESSORA = '" + USUARIO.fieldByName("IMPRESSORA").asString() + "'");
                IMP.open();
                String NIMP = IMP.fieldByName("IMPRESSORA").asString();
                if (NIMP.trim().isEmpty())
                {
                    IMP.close();
                    IMP.condicao("WHERE IMPRESSORA.SIMPRESSORA > -1");
                    IMP.open();
                    NIMP = IMP.fieldByName("IMPRESSORA").asString();
                }

                vs.addParametros("valores", numeroFinal + ";0" + ";" + filial + ";" + filial);
                vs.addParametros("GERAIMPRESSAO", "FALSE");
                vs.addParametros("slImpressoras", NIMP);
                vs.addParametros("JAIMPRIME", "FALSE");
                vs.addParametros("tipoImpressao", "PDF");
                vs.addParametros("r", "2479");
                Object objeto = TClassLoader.execMethod("TecniconRelatorioJava/GerarRelatorio", "obterTelaHtml", vs);
                String retCCe = (String) objeto;
                byte[] rel = Funcoes.convertStringToByte(retCCe);
                if (xmlCCe != null)
                {
                    anexos.put("CC-e".concat(dataSet.fieldByName("CHAVE").asString()).concat("-nfe.pdf"), rel);
                }
            }
        }
        String TEXTOPADEMAIL = "", msgSuframa = "";
        if ((cdsParNFE.fieldByName("ADDTEXTEMAIL").asString().equals("S")) && !(Funcoes.trim(cdsParNFE.fieldByName("TEXTOPADEMAIL").asString()).isEmpty()))
        {
            TEXTOPADEMAIL = Funcoes.trim(cdsParNFE.fieldByName("TEXTOPADEMAIL").asString()).replace("\r", "").replace("\n", "<br>") + "<br>";
        }

        if (pf.retornaRegraNegocio(vs, vs.getValor("filial"), 1942).equals("S") && !SUFRAMA.equals(""))
        {
            msgSuframa = "<br>Você irá receber a Solicitação do PIN pelo Sistema do Suframa dentro de momentos. Gentileza, autorizar a solicitação.";
        }

        String assinatura = "";
        if (vs.getValor("cusuario") != null && !vs.getValor("cusuario").trim().equals(""))
        {
            TSQLDataSetTec confEmailUser = TSQLDataSetTec.create(vs);
            confEmailUser.commandText("SELECT FIRST 1 USUARIOEMAIL.ASSINATURA"
                    + " FROM USUARIOEMAIL"
                    + " WHERE USUARIOEMAIL.CUSUARIO = " + vs.getValor("cusuario"));
            confEmailUser.open();

            if (!confEmailUser.fieldByName("ASSINATURA").asString().equals("") && vs.getParameter("EMAILAUTOMATICO").toUpperCase().equals("TRUE"))
            {
                assinatura = "<br><br>" + confEmailUser.fieldByName("ASSINATURA").asString().replaceAll("\n", "<br>").replace(System.getProperty("line.separator"), "<br>");
            }
        }

        String ass = "Emissao de " + (vs.getParameter("relatorioesp") != null && vs.getParameter("relatorioesp").equals("862") ? "NFCe" : "NFe") + " - ";
        String msg = TEXTOPADEMAIL + "Você está recebendo este e-mail da empresa " + vs.getValor("nomeFilial")
                + " por ter adquirido algum produto ou serviço.<br>"
                + "Na data de " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()) + " foi realizada a emissão da nota fiscal eletrônica<br>"
                + Funcoes.car(numeroFinal, 9, "D", "0") + ", com chave de acesso<br>"
                + dataSet.fieldByName("CHAVE").asString() + ".<br>"
                + "Em anexo segue o arquivo da nota fiscal eletrônica, do qual recomenda-se o<br>"
                + "armazenamento. O arquivo também pode ser utilizado para facilitar o<br>"
                + "lançamento desta nota fiscal em sua base de dados.<br>"
                + "\\\"Consulta de autenticidade no portal nacional da NF-e<br>"
                + "<a href=\\\"http://www.nfe.fazenda.gov.br/portal\\\">http://www.nfe.fazenda.gov.br/portal</a>  ou no site da Sefaz Autorizada\\\"<br>"
                + msgSuframa
                + "<br>"
                + "Gerado automaticamente por TECNICON Sistemas Gerenciais - www.tecnicon.com.br"
                + assinatura;

        if (dataSet.fieldByName("NFESTATUS").asString().equals("C"))
        {
            String xmlCanc = retXMLCanc(vs, vs.getParameter("NFS"), vs.getParameter("NFE"));
            if (!xmlCanc.equals(""))
            {
                anexos.put(nomeMod.concat("Canc").concat(dataSet.fieldByName("CHAVE").asString())
                        .concat(".xml"), (xmlCanc).getBytes());

                ass = "Cancelamento de " + (vs.getParameter("relatorioesp") != null && vs.getParameter("relatorioesp").equals("862") ? "NFCe" : "NFe") + " - ";
                msg = TEXTOPADEMAIL + "Você está recebendo este e-mail da empresa " + vs.getValor("nomeFilial")
                        + " por ter adquirido algum produto ou serviço.<br>"
                        + "Na data de " + new SimpleDateFormat("dd/MM/yyyy").format(new Date())
                        + " foi realizado o cancelamento da nota fiscal eletrônica<br>"
                        + Funcoes.car(cdsCom2.fieldByName("NF").asString(), 9, "D", "0") + ", com chave de acesso<br>"
                        + dataSet.fieldByName("CHAVE").asString() + ".<br>"
                        + "Em anexo segue o arquivo do Cancelamento da NF-e, do qual recomenda-se o<br>"
                        + "armazenamento.<br>"
                        + "\\\"Consulta de autenticidade no portal nacional da NF-e<br>"
                        + "http://www.nfe.fazenda.gov.br/portal  ou no site da Sefaz Autorizada\\\"<br>"
                        + "<br>"
                        + "Gerado automaticamente por TECNICON Sistemas Gerenciais - www.tecnicon.com.br"
                        + assinatura;
            }
        }
        // Anexar boletos junto ao e-mail da Danfe ?
        TSQLDataSetEmp receber = TSQLDataSetEmp.create(vs);
        try
        {
            if (pf.retornaRegraNegocio(vs, vs.getValor("filial"), 1228).equals("S") && vs.getParameter("NFS") != null && !vs.getParameter("NFS").equals(""))
            {
                receber.commandText("SELECT "
                        + " RECEBER.SRECEBER, RECEBER.NOSSONUMERO,"
                        + " RECEBER.CCARTEIRA, "
                        + " BLOQNOSSONUMERO.MODELOBOLETO, "
                        + " BLOQNOSSONUMERO.TPCONVENIO, "
                        + " TSUBSTR(BLOQNOSSONUMERO.CODBANCO,1,3) CODBANCO,"
                        + " CARTEIRAFILIAL.CNEMISSAOBLOQUETO,"
                        + " CASE WHEN (SELECT FIRST 1 CCARTEIRA FROM BOLETOAPOSNF AS B WHERE B.CCARTEIRA = CARTEIRA.CCARTEIRA) IS NOT NULL THEN 'S' ELSE 'N' END AS IMPRIMEBOLETO"
                        + " FROM RECEBER "
                        + " INNER JOIN CARTEIRA ON (CARTEIRA.CCARTEIRA = RECEBER.CCARTEIRA) "
                        + " INNER JOIN CARTEIRAFILIAL ON (CARTEIRAFILIAL.CCARTEIRA = CARTEIRA.CCARTEIRA AND CARTEIRAFILIAL.CFILIAL = " + vs.getValor("filial") + ") "
                        + " INNER JOIN BLOQUETO ON (BLOQUETO.CBLOQUETO = CARTEIRA.CBLOQUETO) "
                        + " INNER JOIN BLOQNOSSONUMERO ON (BLOQNOSSONUMERO.CBLOQUETO = BLOQUETO.CBLOQUETO AND BLOQNOSSONUMERO.CFILIAL = " + vs.getValor("filial") + ")"
                        + " WHERE RECEBER.NFS = " + vs.getParameter("NFS")
                        + " AND (COALESCE(RECEBER.VALOR, 0) - "
                        + "        (SELECT "
                        + "            COALESCE(SUM(BXRECEBER.VALOR), 0) "
                        + "        FROM BXRECEBER WHERE BXRECEBER.SRECEBER = RECEBER.SRECEBER) "
                        + "    ) <> 0 "
                );
                receber.open();
                receber.first();
                TClientDataSet cdsReceberAux = TClientDataSet.create(vs, "RECEBER");
                cdsReceberAux.createDataSet();
                String[] retorno = new String[1];
                retorno[0] = "";
                byte[] relDupl;
                while (!receber.eof())
                {
                    if (!receber.fieldByName("CNEMISSAOBLOQUETO").asString().equals("1")) // Banco Emite
                    {
                        if (receber.fieldByName("IMPRIMEBOLETO").asString().equals("S"))
                        {
                            vs.addParametros("JAIMPRIME", "TRUE");
                        } else
                        {
                            vs.addParametros("JAIMPRIME", "FALSE");
                        }
                        cdsReceberAux.close();
                        cdsReceberAux.condicao(" WHERE RECEBER.SRECEBER = " + receber.fieldByName("SRECEBER").asInteger());
                        cdsReceberAux.open();
                        // Gera o boleto
                        TClassLoader.execMethod("Bloquetos/Bloquetos", "IMPRIMEBLOQUETO", vs, receber.fieldByName("CCARTEIRA").asInteger(), "S",
                                true, "N", cdsReceberAux, retorno, 0.0);
                    }
                    receber.next();
                }
                TSQLDataSetEmp nossNum = TSQLDataSetEmp.create(vs);
                receber.first();
                while (!receber.eof())
                {

                    if (!receber.fieldByName("CNEMISSAOBLOQUETO").asString().equals("1")) // Banco Emite
                    {
                        vs.addParametros("JAIMPRIME", "FALSE");
                        nossNum.close();
                        nossNum.commandText("SELECT RECEBER.NOSSONUMERO FROM RECEBER WHERE RECEBER.SRECEBER = " + receber.fieldByName("SRECEBER").asString());
                        nossNum.open();
                        relDupl = Funcoes.convertStringToByte((String) TClassLoader.execMethod("Bloquetos/Bloquetos", "geraRelatorioBoletos", vs,
                                receber.fieldByName("SRECEBER").asString(), receber.fieldByName("MODELOBOLETO").asString(), receber.fieldByName("TPCONVENIO").asString(), receber.fieldByName("CODBANCO").asString()));
                        anexos.put(nossNum.fieldByName("NOSSONUMERO").asString() + ".pdf", relDupl);
                    }
                    receber.next();
                }

            }
        } catch (ExcecaoTecnicon e)
        {
            // Caso não esteja configurada a carteira pra gerar boleto, somente avisa que não foi possivel gerar o boleto.
            TClassLoader.execMethod("DmUtil/DmUtil", "enviarMensagemMyAlert", vs, "Boletos",
                    "Falha ao anexar os boletos ao email " + e.getMessage(), vs.getValor("cusuario"), "RECEBER", "{\"SRECEBER\":\"" + receber.fieldByName("SRECEBER").asInteger() + "\"}");

        }

        //<editor-fold defaultstate="collapsed" desc=" Integração anexos de Laudo Técnico ">
        String dir = pf.retornaRegraNegocio(vs, vs.getValor("filial"), 1303);
        if (!"".equals(dir) && dir != null)
        {

            File raiz = new File(dir);
            if (raiz.isDirectory())
            {

                boolean isSaida = vs.getParameter("NFS") != null && !vs.getParameter("NFS").trim().isEmpty();

                String item = isSaida ? "NFSITEM" : "NFEITEM",
                        itemLote = isSaida ? "NFSITEMLOTE" : "NFEITEMLOTE",
                        field = isSaida ? "NFS" : "NFE";

                TSQLDataSet buscaLote = TSQLDataSetEmp.create(vs);

                buscaLote.commandText(
                        "   SELECT "
                        + "       PRODUTOLOTE.LOTE, " + item + "." + item
                        + " FROM  " + item
                        + " INNER JOIN " + itemLote + " ON (" + item + "." + item + " = " + itemLote + "." + item + " ) "
                        + " INNER JOIN PRODUTOLOTE ON (" + itemLote + ".SPRODUTOLOTE = PRODUTOLOTE.SPRODUTOLOTE) "
                        + " WHERE " + item + "." + field + " = " + vs.getParameter(isSaida ? "NFS" : "NFE")
                );
                buscaLote.createDataSet();
                buscaLote.open();

                File laudoTecnico;
                String myArchive;
                byte[] bytesArquivo;

                buscaLote.first();
                while (!buscaLote.eof())
                {
                    myArchive = buscaLote.fieldByName("LOTE").asString();
                    if (!"".equals(myArchive))
                    {
                        laudoTecnico = new File(raiz, myArchive.concat(".pdf"));
                        if (laudoTecnico.exists() && laudoTecnico.isFile())
                        {
                            bytesArquivo = FileUtils.toByteArray(vs, laudoTecnico);
                            anexos.put(myArchive.concat(".pdf"), bytesArquivo);
                        }
                    }
                    buscaLote.next();
                }
            }

        }

        //</editor-fold>
        //if (!XCCEMAIL.isEmpty())
        //{
        TSQLDataSetTec dsUserMail = TSQLDataSetTec.create(vs);
        dsUserMail.commandText("SELECT USUARIOEMAIL.EMAIL FROM USUARIOEMAIL "
                + " WHERE USUARIOEMAIL.CUSUARIO = {USER} ");
        dsUserMail.open();
        if (!dsUserMail.fieldByName("EMAIL").asString().isEmpty())
        {
            if (!XCCEMAIL.contains(dsUserMail.fieldByName("EMAIL").asString()))
            {
                XCCEMAIL += (!XCCEMAIL.equals("") && !XCCEMAIL.trim().endsWith(";") ? ";" : "") + dsUserMail.fieldByName("EMAIL").asString();
            }
        }
        //}

        /* Envia e-mail Contra Nota para o contato FISCAL */
        if (!Funcoes.varIsNull(vs.getParameter("NFS")) || !Funcoes.varIsNull(vs.getValorParametro("NFS")))
        {
            String nfs;
            if (!Funcoes.varIsNull(vs.getParameter("NFS")))
            {
                nfs = vs.getParameter("NFS");
            } else
            {
                nfs = vs.getValorParametro("NFS");
            }
            String r2164 = pf.retornaRegraNegocio(vs, vs.getValor("filial"), 2164).trim();
            String cst = "";
            if (!r2164.isEmpty())
            {
                cst = " AND EXISTS (SELECT NFSITEM.NFSITEM FROM NFSITEM WHERE NFSITEM.NFS = NFSAIDA.NFS AND TSUBSTR(NFSITEM.ST,2,3) = " + r2164 + ") ";
            }
            TSQLDataSetEmp nf = TSQLDataSetEmp.create(vs);
            nf.commandText("SELECT CONTATOCLI.CONTATO, NFSAIDA.DATA, NFSAIDA.NF, CLIFOREND.NOMEFILIAL, FILIAL.EMAIL AS EMAILF, CONTATOCLI.EMAIL  "
                    + " FROM NFSAIDA "
                    + " INNER JOIN CIOF ON (CIOF.CIOF = NFSAIDA.CIOF) "
                    + " INNER JOIN CLIFOREND ON (CLIFOREND.CCLIFOR = NFSAIDA.CCLIFOR AND CLIFOREND.FILIALCF = NFSAIDA.FILIALCF) "
                    + " INNER JOIN CONTATOCLI ON (CONTATOCLI.CCLIFOR = CLIFOREND.CCLIFOR AND CONTATOCLI.FILIALCF = CLIFOREND.FILIALCF AND CONTATOCLI.VINCOMPRA LIKE '%I%' ) "
                    + " INNER JOIN FILIAL ON (FILIAL.CFILIAL = NFSAIDA.CFILIAL) "
                    + " WHERE NFSAIDA.NFS =  " + nfs + " AND COALESCE(CIOF.EXIGENF, 'N') = 'S'  " + cst);
            nf.open();
            while (!nf.eof() && !nf.fieldByName("EMAIL").asString().isEmpty())
            {
                String corpoTexto = "Prezado " + nf.fieldByName("CONTATO").asString()
                        + "<br>"
                        + " Comunicamos que no dia " + nf.fieldByName("DATA").asString() + " foi emitida a nota fiscal nº " + nf.fieldByName("NF").asString()
                        + " da " + nf.fieldByName("NOMEFILIAL").asString() + " e que será necessário o envio da contra nota prevista no"
                        + " LIVRO III, ART. 1º, § 3º, DO DECRETO 37.699/97 RICMS/RS. "
                        + "<br>"
                        + " Favor enviar a nota fiscal para o e-mail " + nf.fieldByName("EMAILF").asString();

                try
                {
                    new TEnviarEmail().enviarEmail(nf.fieldByName("EMAIL").asString(), "", "", "Emissão NF nº " + nf.fieldByName("NF").asString() + " - necessita contra nota ",
                            corpoTexto, vs);
                } catch (ExcecaoTecnicon e)
                {
                    TClassLoader.execMethod("DmUtil/DmUtil", "enviarMensagemMyAlert", vs, "E-Mail",
                            " Não foi possível enviar e-mail de contra nota para o contato: " + nf.fieldByName("CONTATO").asString() + " \n "
                            + " Verifique a configuração de e-mail ou o cadastro do contato ",
                            vs.getValor("cusuario"), "NFSAIDA", "{\"NFS\":\"" + nfs + "\"}");
                }
                nf.next();
            }

        }

        cc = RelatorioDANFE.trataCC(cc, CCO);
        if (enviaautomatico)
        {
            if (XENDEMAIL.trim().equals(""))
            {
                if (vs.getParameter("NFS") != null && !vs.getParameter("NFS").equals(""))
                {
                    TClassLoader.execMethod("DmUtil/DmUtil", "enviarMensagemMyAlert", vs, "E-Mail",
                            "O cliente não possui e-mail cadastrado. Não será possível efetuar o envio de e-mail da NF-e/NFC-e automaticamente.",
                            vs.getValor("cusuario"), "NFSAIDA", "{\"NFS\":\"" + vs.getParameter("NFS") + "\"}");
                }
                throw new ExcecaoTecnicon(vs, "O cliente não possui e-mail cadastrado. Não será possível efetuar o envio de e-mail da NF-e/NFC-e automaticamente!");
            }
            try
            {
                new TEnviarEmail().enviarEmail(XENDEMAIL, XCCEMAIL, cc, ass.concat(dataSet.fieldByName("CHAVE").asString()), msg, vs, anexos);
            } catch (ExcecaoTecnicon e)
            {
                throw new ExcecaoTecnicon(vs, "Falha ao enviar e-mail: " + e.getMessage());
            }

        } else
        {
            TClassLoader.execMethod("TecniconMailEJB/EmailWebUtil", "showMailForm", vs, XENDEMAIL, XCCEMAIL, cc, "Emissao de NF-e - ".concat(dataSet.fieldByName("CHAVE").asString()), msg, anexos);
        }
    }

    public static String trataCC(String cc, String cco)
    {

        if (cc.length() > 1 && !cc.endsWith(";"))
        {
            cc = cc.concat(";").concat(cco);
        } else if (cc.length() == 1 && cc.equals(";"))
        {
            cc = cco;
        } else if (cc.trim().isEmpty())
        {
            cc = cco;
        }
        return cc;
    }

    public String retXMLCanc(VariavelSessao vs, String nfs, String nfe) throws ExcecaoTecnicon
    {
        TClientDataSet CDSc = TClientDataSet.create(vs);
        CDSc.createDataSet();
        TSQLDataSetTec dataSetTec = TSQLDataSetTec.create(vs);
        dataSetTec.close();
        dataSetTec.commandText("SELECT EMPRESA.BANCODADOS FROM EMPRESA WHERE EMPRESA.CEMPRESA=" + vs.getValor("empresa"));
        dataSetTec.open();
        if (dataSetTec.fieldByName("BANCODADOS").asString().equals("FIREBIRD"))
        {
            if (nfe != null && !"".equals(nfe))
            {
                CDSc.commandText(
                        "SELECT CHAVENFE.SCHAVENFE AS CNF, CAST(CHAVENFEXML.NFEXMLCANC AS BLOB SUB_TYPE 1 SEGMENT SIZE 80)AS NFEXMLCANC"
                        +//ARQUIVONFE"+
                        " FROM CHAVENFE"
                        + " LEFT JOIN CHAVENFEXML ON (CHAVENFEXML.SCHAVENFE=CHAVENFE.SCHAVENFE)");
                CDSc.condicao(" WHERE CHAVENFE.NFE=" + nfe);
            } else
            {
                CDSc.commandText(
                        "SELECT CHAVENFE.SCHAVENFE AS CNF, CAST(CHAVENFEXML.NFEXMLCANC AS BLOB SUB_TYPE 1 SEGMENT SIZE 80)AS NFEXMLCANC"
                        +// ARQUIVONFE
                        " FROM CHAVENFE"
                        + " LEFT JOIN CHAVENFEXML ON (CHAVENFEXML.SCHAVENFE=CHAVENFE.SCHAVENFE)");
                CDSc.condicao(" WHERE CHAVENFE.NFS=" + nfs);
            }
        } else if (nfe != null && !"".equals(nfe))
        {
            CDSc.commandText(
                    "SELECT CHAVENFE.SCHAVENFE AS CNF, CHAVENFEXML.NFEXMLCANC"
                    +//ARQUIVONFE"+
                    " FROM CHAVENFE"
                    + " LEFT JOIN CHAVENFEXML ON (CHAVENFEXML.SCHAVENFE=CHAVENFE.SCHAVENFE)");
            CDSc.condicao(" WHERE CHAVENFE.NFE=" + nfe);
        } else
        {
            CDSc.commandText(
                    "SELECT CHAVENFE.SCHAVENFE AS CNF, CHAVENFEXML.NFEXMLCANC"
                    +// ARQUIVONFE
                    " FROM CHAVENFE"
                    + " LEFT JOIN CHAVENFEXML ON (CHAVENFEXML.SCHAVENFE=CHAVENFE.SCHAVENFE)");
            CDSc.condicao(" WHERE CHAVENFE.NFS=" + nfs);
        }
        CDSc.open();
        return CDSc.fieldByName("NFEXMLCANC").asString() != null ? CDSc.fieldByName("NFEXMLCANC").asString() : "";
    }

    public void enviaEmailCCe(VariavelSessao vs) throws ExcecaoTecnicon, IOException
    {
        TClientDataSet cdsCom2 = TClientDataSet.create(vs);
        TClientDataSet contatoCli = TClientDataSet.create(vs, "CONTATOCLI");
        cdsCom2.createDataSet();
        contatoCli.createDataSet();

        ParametrosForm pf = (ParametrosForm) TecniconLookup.lookup("TecniconParametrosForm", "ParametrosFormImpl");

        boolean enviaautomatico = ((vs.getParameter("EMAILAUTOMATICO")) != null && (vs.getParameter("EMAILAUTOMATICO").toUpperCase().equals("TRUE")));

        cdsCom2.close();
        if (vs.getParameter("NFE") != null && !"".equals(vs.getParameter("NFE")))
        {
            cdsCom2.commandText(
                    " SELECT NFENTRADA.CCLIFOR, NFENTRADA.FILIALCF, NFENTRADA.CTRANSP,"
                    + " NFENTRADA.FILIALTRANSP, NFENTRADA.CMODELONF, NFENTRADA.DATA, NFENTRADA.NF, CHAVENFE.CHAVE, NFENTRADA.CFILIAL"
                    + " FROM NFENTRADA"
                    + "     LEFT JOIN CHAVENFE ON (CHAVENFE.NFE=NFENTRADA.NFE)");
            cdsCom2.condicao(" WHERE NFENTRADA.NFE=" + vs.getParameter("NFE"));
        } else if (vs.getValorParametro("NFS") != null && !vs.getValorParametro("NFS").equals(""))
        {
            cdsCom2.commandText(" SELECT NFSAIDA.CCLIFOR, NFSAIDA.FILIALCF, NFSAIDA.CTRANSP,"
                    + " NFSAIDA.FILIALTRANSP, NFSAIDA.CMODELONF, NFSAIDA.DATA, NFSAIDA.NF, CHAVENFE.CHAVE, NFSAIDA.CFILIAL"
                    + " FROM NFSAIDA"
                    + "     LEFT JOIN CHAVENFE ON (CHAVENFE.NFS=NFSAIDA.NFS)");
            cdsCom2.condicao(" WHERE NFSAIDA.NFS=" + vs.getValorParametro("NFS"));
        } else if (vs.getParameter("NFS") != null && !vs.getParameter("NFS").equals(""))
        {
            cdsCom2.commandText(" SELECT NFSAIDA.CCLIFOR, NFSAIDA.FILIALCF, NFSAIDA.CTRANSP,"
                    + " NFSAIDA.FILIALTRANSP, NFSAIDA.CMODELONF, NFSAIDA.DATA, NFSAIDA.NF, CHAVENFE.CHAVE, NFSAIDA.CFILIAL"
                    + " FROM NFSAIDA"
                    + "     LEFT JOIN CHAVENFE ON (CHAVENFE.NFS=NFSAIDA.NFS)");
            cdsCom2.condicao(" WHERE NFSAIDA.NFS=" + vs.getParameter("NFS"));
        } else if (vs.getParameter("NFS") != null && !vs.getParameter("NFS").equals(""))
        {
            cdsCom2.commandText(" SELECT NFSAIDA.CCLIFOR, NFSAIDA.FILIALCF, NFSAIDA.CTRANSP,"
                    + " NFSAIDA.FILIALTRANSP, NFSAIDA.CMODELONF, NFSAIDA.DATA, NFSAIDA.NF, CHAVENFE.CHAVE, NFSAIDA.CFILIAL"
                    + " FROM NFSAIDA"
                    + "     LEFT JOIN CHAVENFE ON (CHAVENFE.NFS=NFSAIDA.NFS)");
            cdsCom2.condicao(" WHERE NFSAIDA.NFS=" + vs.getParameter("NFS"));
        }
        cdsCom2.open();

        if (vs.getParameter("NFE") != null && !"".equals(vs.getParameter("NFE")))
        {
            vs.addParametros("valores", "0;" + cdsCom2.fieldByName("NF").asString() + ";" + cdsCom2.fieldByName("CFILIAL").asString() + ";" + cdsCom2.fieldByName("CFILIAL").asString());
        } else
        {
            vs.addParametros("valores", cdsCom2.fieldByName("NF").asString() + ";0" + ";" + cdsCom2.fieldByName("CFILIAL").asString() + ";" + cdsCom2.fieldByName("CFILIAL").asString());
        }
        String CCLIFOR = cdsCom2.fieldByName("CCLIFOR").asString();
        String FILIALCF = cdsCom2.fieldByName("FILIALCF").asString();
        contatoCli.condicao(
                " WHERE CONTATOCLI.CCLIFOR=" + CCLIFOR
                + " AND CONTATOCLI.FILIALCF=" + FILIALCF
                + " AND CONTATOCLI.VINCOMPRA LIKE '%N%'");
        contatoCli.open();
        contatoCli.first();
        String CONTATOCLI = contatoCli.fieldByName("CCONTATO").asString();

        String codTransp = cdsCom2.fieldByName("CTRANSP").asString();
        String filialTransp = cdsCom2.fieldByName("FILIALTRANSP").asString();
        String XCCEMAIL = "";
        if (!codTransp.isEmpty() && !filialTransp.isEmpty())
        {
            TSQLDataSetEmp cdsContato = TSQLDataSetEmp.create(vs);
            cdsContato.commandText("SELECT CONTATOCLI.EMAIL FROM CONTATOCLI ");
            cdsContato.condicao(" WHERE CONTATOCLI.CCLIFOR = " + codTransp + " AND CONTATOCLI.FILIALCF = " + filialTransp + " AND CONTATOCLI.VINCOMPRA LIKE '%N%'");
            cdsContato.open();
            cdsContato.first();
            while (!cdsContato.eof())
            {
                if (Funcoes.pos(cdsContato.fieldByName("EMAIL").asString(), XCCEMAIL) == 0)
                {
                    if (Funcoes.length(XCCEMAIL) != 0)
                    {
                        XCCEMAIL = XCCEMAIL + ";";
                    }
                    XCCEMAIL = XCCEMAIL + cdsContato.fieldByName("EMAIL").asString();
                }
                cdsContato.next();
            }
            if (pf.retornaRegraNegocio(vs, vs.getValor("filial"), 1802).equals("S") && (!Funcoes.varIsNull(vs.getParameter("NFS")) || !Funcoes.varIsNull(vs.getValorParametro("NFS"))))
            {
                String nfs;
                if (!Funcoes.varIsNull(vs.getParameter("NFS")))
                {
                    nfs = vs.getParameter("NFS");
                } else
                {
                    nfs = vs.getValorParametro("NFS");
                }
                if (!Funcoes.varIsNull(nfs))
                {
                    cdsContato.close();
                    cdsContato.commandText("SELECT CONTATOCLI.EMAIL "
                            + " FROM NFSDESPACHO "
                            + " INNER JOIN CONTATOCLI ON (CONTATOCLI.CCLIFOR = NFSDESPACHO.CTRANSP AND CONTATOCLI.FILIALCF = NFSDESPACHO.FILIALTRANSP  AND CONTATOCLI.VINCOMPRA LIKE '%N%' ) ");
                    cdsContato.condicao(" WHERE NFSDESPACHO.NFS = " + vs.getParameter("NFS"));
                    cdsContato.open();
                    while (!cdsContato.eof())
                    {
                        if (Funcoes.pos(cdsContato.fieldByName("EMAIL").asString(), XCCEMAIL) == 0)
                        {
                            if (Funcoes.length(XCCEMAIL) != 0)
                            {
                                XCCEMAIL = XCCEMAIL + ";";
                            }
                            XCCEMAIL = XCCEMAIL + cdsContato.fieldByName("EMAIL").asString();
                        }
                        cdsContato.next();
                    }
                }
            }

            String XENDEMAIL = "";
            while (!contatoCli.eof())
            {
                if (Funcoes.pos(contatoCli.fieldByName("EMAIL").asString(), XENDEMAIL) == 0)
                {
                    if (Funcoes.length(XENDEMAIL) != 0)
                    {
                        XENDEMAIL = XENDEMAIL + ";";
                    }
                    XENDEMAIL = XENDEMAIL + contatoCli.fieldByName("EMAIL").asString();
                }
                contatoCli.next();
            }

            TClientDataSet USUARIO = TClientDataSet.create(vs, "USUARIO");
            USUARIO.createDataSet();
            USUARIO.condicao("WHERE USUARIO.CUSUARIO = " + vs.getValor("cusuario"));
            USUARIO.open();

            TSQLDataSetTec IMP = TSQLDataSetTec.create(vs);
            IMP.commandText("SELECT FIRST 1 IMPRESSORA.IMPRESSORA FROM IMPRESSORA");
            IMP.condicao("WHERE IMPRESSORA.IMPRESSORA = '" + USUARIO.fieldByName("IMPRESSORA").asString() + "'");
            IMP.open();
            String NIMP = IMP.fieldByName("IMPRESSORA").asString();
            if (NIMP.trim().isEmpty())
            {
                IMP.close();
                IMP.condicao("WHERE 1=1");
                IMP.open();
                NIMP = IMP.fieldByName("IMPRESSORA").asString();
            }

            vs.addParametros("slImpressoras", NIMP);
            vs.addParametros("JAIMPRIME", "FALSE");
            vs.addParametros("tipoImpressao", "PDF");
            vs.addParametros("r", "2479");
            Object objeto = TClassLoader.execMethod("TecniconRelatorioJava/GerarRelatorio", "obterTelaHtml", vs);
            String retorno = (String) objeto;
            byte[] rel = Funcoes.convertStringToByte(retorno); //(retorno).getBytes();
            String Chave = "";
            if (vs.getParameter("CHAVE") != null)
            {
                Chave = vs.getParameter("CHAVE");
            }
            String nomeMod = "CCe" + Chave;
            Map<String, byte[]> anexos = new HashMap<>();

            if (vs.getParameter("CCEXML") != null)
            {
                byte[] xml = (vs.getParameter("CCEXML")).getBytes();
                anexos.put(nomeMod.concat("-nfe.xml"), xml);
            }
            anexos.put(nomeMod.concat("-nfe.pdf"), rel);

            String ass = "Emissão de CC-e da NFe - " + Chave;
            String msg = "Você está recebendo este e-mail da empresa " + vs.getValor("nomeFilial")
                    + " por ter adquirido algum produto ou serviço.<br>"
                    + "Na data de " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()) + " foi realizada a emissão CC-e da nota fiscal eletrônica<br>"
                    + Funcoes.car(cdsCom2.fieldByName("NF").asString(), 9, "D", "0") + ", com chave de acesso " + Chave + "<br>"
                    + "Em anexo segue o arquivo da CC-e nota fiscal eletrônica, do qual recomenda-se o armazenamento.<br>"
                    + "Consulta de autenticidade no portal nacional da NF-e<br>"
                    + "http://www.nfe.fazenda.gov.br/portal ou no site da Sefaz Autorizada<br>"
                    + "<br>"
                    + "Gerado automaticamente por TECNICON Sistemas Gerenciais - www.tecnicon.com.br";
            cdsCom2.close();

            if (!XCCEMAIL.isEmpty())
            {
                TSQLDataSetTec dsUserMail = TSQLDataSetTec.create(vs);
                dsUserMail.commandText("SELECT USUARIOEMAIL.EMAIL FROM USUARIOEMAIL "
                        + " WHERE USUARIOEMAIL.CUSUARIO = {USER}");
                dsUserMail.open();
                if (!dsUserMail.fieldByName("EMAIL").asString().isEmpty())
                {
                    if (!XCCEMAIL.contains(dsUserMail.fieldByName("EMAIL").asString()))
                    {
                        XCCEMAIL += (XCCEMAIL.trim().endsWith(";") ? "" : ";") + dsUserMail.fieldByName("EMAIL").asString();
                    }
                }
            }

            String cc = "";
            TSQLDataSetEmp cdsParNFE = TSQLDataSetEmp.create(vs);
            cdsParNFE.commandText("SELECT PARAMETRONFE.ENVIANFEVENDEDOR, PARAMETRONFE.ENVIARNFEVENDEDORTELE, ADDTEXTEMAIL, TEXTOPADEMAIL FROM PARAMETRONFE");
            cdsParNFE.condicao(" WHERE PARAMETRONFE.CFILIAL = " + vs.getValor("filial"));
            cdsParNFE.open();
            if (cdsParNFE.fieldByName("ENVIANFEVENDEDOR").asString().equals("S"))
            {
                TSQLDataSetEmp cdsVendedor = TSQLDataSetEmp.create(vs);
                if (vs.getParameter("NFE") != null && !"".equals(vs.getParameter("NFE")))
                {
                    cdsVendedor.commandText("select VENDEDOR.EMAIL"
                            + " from VENDEDOR"
                            + " inner join NFEVENDEDOR on (NFEVENDEDOR.CVENDEDOR = VENDEDOR.CVENDEDOR)"
                            + " where NFEVENDEDOR.NFE =" + vs.getParameter("NFE"));
                } else if (vs.getValorParametro("NFS") != null && !vs.getValorParametro("NFS").equals(""))
                {
                    cdsVendedor.commandText("select VENDEDOR.EMAIL"
                            + " from VENDEDOR"
                            + " inner join NFSVENDEDOR on (NFSVENDEDOR.CVENDEDOR = VENDEDOR.CVENDEDOR)"
                            + " where NFSVENDEDOR.NFS =" + vs.getValorParametro("NFS"));
                } else if (vs.getParameter("NFS") != null && !vs.getParameter("NFS").equals(""))
                {
                    cdsVendedor.commandText("select VENDEDOR.EMAIL"
                            + " from VENDEDOR"
                            + " inner join NFSVENDEDOR on (NFSVENDEDOR.CVENDEDOR = VENDEDOR.CVENDEDOR)"
                            + " where NFSVENDEDOR.NFS =" + vs.getParameter("NFS"));
                }
                cdsVendedor.open();
                while (!cdsVendedor.eof())
                {
                    if (Funcoes.pos(cdsVendedor.fieldByName("EMAIL").asString(), cc) == 0)
                    {
                        if (Funcoes.length(cc) != 0)
                        {
                            cc = cc + ";";
                        }
                        cc = cc + cdsVendedor.fieldByName("EMAIL").asString();
                    }
                    cdsVendedor.next();
                }
            }
            if (cdsParNFE.fieldByName("ENVIARNFEVENDEDORTELE").asString().equals("S"))
            {
                TSQLDataSetEmp cdsVendTele = TSQLDataSetEmp.create(vs);
                if (vs.getParameter("NFE") != null && !"".equals(vs.getParameter("NFE")))
                {
                    cdsVendTele.fieldByName(" SELECT NFENTRADA.CVENDTELE, VENDEDOR.EMAIL "
                            + " FROM NFENTRADA "
                            + " INNER JOIN VENDEDOR ON (VENDEDOR.CVENDEDOR = NFENTRADA.CVENDTELE) "
                            + " WHERE NFENTRADA.NFE = " + vs.getParameter("NFE"));
                } else if (vs.getValorParametro("NFS") != null && !vs.getValorParametro("NFS").equals(""))
                {
                    cdsVendTele.commandText(" SELECT NFSAIDA.CVENDTELE, VENDEDOR.EMAIL "
                            + " FROM NFSAIDA "
                            + " INNER JOIN VENDEDOR ON (VENDEDOR.CVENDEDOR = NFSAIDA.CVENDTELE) "
                            + " WHERE NFSAIDA.NFS = " + vs.getValorParametro("NFS"));
                } else if (vs.getParameter("NFS") != null && !vs.getParameter("NFS").equals(""))
                {
                    cdsVendTele.commandText(" SELECT NFSAIDA.CVENDTELE, VENDEDOR.EMAIL "
                            + " FROM NFSAIDA "
                            + " INNER JOIN VENDEDOR ON (VENDEDOR.CVENDEDOR = NFSAIDA.CVENDTELE) "
                            + " WHERE NFSAIDA.NFS = " + vs.getParameter("NFS"));
                }
                cdsVendTele.open();
                while (!cdsVendTele.eof())
                {
                    if (Funcoes.pos(cdsVendTele.fieldByName("EMAIL").asString(), cc) == 0)
                    {
                        if (Funcoes.length(cc) != 0)
                        {
                            cc = cc + ";";
                        }
                        cc = cc + cdsVendTele.fieldByName("EMAIL").asString();
                    }
                    cdsVendTele.next();
                }
            }
            RelatorioDANFE.trataCC(cc, "");

            if (vs.getParameter("JAENVIAEMAIL") != null && "true".equalsIgnoreCase(vs.getParameter("JAENVIAEMAIL")))
            {
                if (enviaautomatico)
                {
                    try
                    {
                        new TEnviarEmail().enviarEmail(XENDEMAIL, XCCEMAIL, cc, ass, msg, vs, anexos);

                    } catch (ExcecaoTecnicon e)
                    {
                        throw new ExcecaoTecnicon(vs, "Falha ao enviar e-mail: " + e.getMessage());
                    }
                } else
                {
                    EmailWebUtil.showMailForm(vs, XENDEMAIL, "", "", ass, msg, anexos);
                }

            }
        }
    }

//    private static byte[] stringTOBinary(String obj) throws IOException, ExcecaoTecnicon
//    {
////        byte[] bytes = Base64.decode(obj);
//        byte[] bytes = FileUtils.base64ToByte(vs, obj);
//
//        return bytes;
//
//    }
    public static String join(TSQLDataSetEmp cds, String caracter, String campoCDS) throws ExcecaoTecnicon
    {
        String ok = "";
        cds.first();

        if (caracter.trim().isEmpty())
        {
            caracter = ";";
        }

        while (!cds.eof())
        {
            ok += caracter.concat(cds.fieldByName(campoCDS).asString());
            cds.next();
        }
        cds.first();
        return ok.replaceFirst(caracter, "");
    }

    private String addEspacoStr(String origem, String carAdic, int maxCar) throws ExcecaoTecnicon
    {
        return (addEspacoStr(origem, carAdic, maxCar, 99999));
    }

    private String addEspacoStr(String origem, String carAdic, int maxCar, int linhasMaximas) throws ExcecaoTecnicon
    {
        if ((carAdic == null || "".equals(carAdic)) || origem.length() <= maxCar)
        {
            return origem;
        }

        int ini = 0, fim = maxCar, tamanho = origem.length(), lenCarAdic = carAdic.length();
        StringBuilder tudo = new StringBuilder();
        int linha = 1;
        while (ini < tamanho)
        {
            if (fim > tamanho)
            {
                fim = tamanho;
            }
            String ax = origem.substring(ini, fim);
            if (linha <= linhasMaximas)
            {
                if (ax.contains(carAdic))
                {
                    fim = ini + ax.lastIndexOf(carAdic) + lenCarAdic;
                    if (fim > tamanho)
                    {
                        fim = tamanho;
                    }
                    ax = origem.substring(ini, fim);
                } else if (ax.contains(" "))
                {
                    if (ini + maxCar < fim)
                    {
                        fim = ini + ax.lastIndexOf(" ") + 1;
                        if (fim > tamanho)
                        {
                            fim = tamanho;
                        }
                        ax = origem.substring(ini, fim).concat("{QUEBRA}");
                    }
                } else
                {
                    ax = ax.concat("{QUEBRA}");
                }
                linha++;
            }
            tudo.append(ax);
            ini = fim;
            fim = ini + maxCar;
        }
        return previneQuebraEmNumero(tudo.toString());
    }

    public String previneQuebraEmNumero(String str)
    {
        Pattern p = Pattern.compile("(?<nums>[0-9]*?)(?<quebra>\\{QUEBRA\\})[0-9]");
        Matcher m = p.matcher(str);

        while (m.find())
        {
            str = str.replace(m.group("nums") + m.group("quebra"), m.group("quebra") + m.group("nums"));
        }

        return str.replace("{QUEBRA}", "\r\n");
    }

    public void ImprimeNFCe(VariavelSessao vs) throws ExcecaoTecnicon, ParseException
    {
        String relatorio = MontaNFCe(vs);

        TClientDataSet USUARIO = TClientDataSet.create(vs, "USUARIO");
        USUARIO.createDataSet();
        USUARIO.condicao("WHERE USUARIO.CUSUARIO = " + vs.getValor("cusuario"));
        USUARIO.open();

        TClientDataSet impressora = TClientDataSet.create(vs, "IMPRESSORA");
        impressora.createDataSet();
        impressora.condicao("WHERE IMPRESSORA.IMPRESSORA = '" + vs.getParameter("slImpressoras") + "'");
        impressora.open();
        if (impressora.isEmpty())
        {
            impressora.close();
            impressora.condicao("WHERE IMPRESSORA.IMPRESSORA = '" + USUARIO.fieldByName("IMPRESSORA").asString() + "'");
            impressora.open();
        }
        String NIMP = impressora.fieldByName("IMPRESSORA").asString();

        if (!impressora.fieldByName("SEQUIPAMENTOREMOTO").asString().trim().equals(""))
        {
            vs.addParametros("tipo", "TEXTO");
            vs.addParametros("tipoimpressora", impressora.fieldByName("TIPO").asString());
            vs.addParametros("colunas", "48");
            vs.addParametros("L80", impressora.fieldByName("L80").asString());
            vs.addParametros("L96", impressora.fieldByName("L96").asString());
            vs.addParametros("L136", impressora.fieldByName("L136").asString());
            vs.addParametros("L186", impressora.fieldByName("L186").asString());
            vs.addParametros("inicia", impressora.fieldByName("INICIA").asString());
            vs.addParametros("porta", impressora.fieldByName("PORTA").asString());
            vs.addParametros("objeto", relatorio);
            vs.addParametros("impressora", impressora.fieldByName("IMPRESSORA").asString());
            vs.addParametros("SEQUIPAMENTOREMOTO", impressora.fieldByName("SEQUIPAMENTOREMOTO").asString());

            Object impressaoRemota = TecniconLookup.lookup("PrintService", "MigraPCSocket");

            try
            {
                impressaoRemota.getClass().getMethod("enviar", VariavelSessao.class
                ).invoke(impressaoRemota, vs);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex)
            {
                throw new ExcecaoTecnicon(vs, ex);
            }
        }
    }

    /*
     desenvolvido para impressoras Daruma DR700L
     */
    public String MontaNFCe(VariavelSessao vs) throws ExcecaoTecnicon, ParseException
    {
        TSQLDataSet principalDANFE = retornaRelatorioNFCE3(vs);
        principalDANFE.first();
        boolean modeloEconomico = false;
        if ((vs.getParameter("MODELONFCE") != null && vs.getParameter("MODELONFCE").equals("E"))
                || (vs.getParameter("MODELOECONOMICONFCE") != null && vs.getParameter("MODELOECONOMICONFCE").equals("S")))
        {
            modeloEconomico = true;
        }
        TSQLDataSet produtosDANFE = retornaProdutosNFCE3(vs);
        StringBuilder relatorio = new StringBuilder();
        relatorio.append("<ce>").append(principalDANFE.fieldByName("EMITNOME").asString()).append("</ce>\n");
        relatorio.append("CNPJ - ").append(principalDANFE.fieldByName("EMITCNPJ").asString()).append("\n");
        relatorio.append("IE - ").append(principalDANFE.fieldByName("EMITIE").asString())
                .append("            ").append("IM - ").append(principalDANFE.fieldByName("EMITIM").asString()).append("\n");
        relatorio.append("Endereco - ").append(principalDANFE.fieldByName("EMITENDERECONFCE").asString()).append("\n");
        relatorio.append("--------------------------------------------\n");
        //relatorio.append("<ce>DANFE NFC-e - Documento Auxiliar da Nota Fiscal Eletronica para Consumidor Final</ce>\n");
        relatorio.append("    DANFE NFC-e - Documento Auxiliar da      \n"
                + "        Nota Fiscal Eletronica para         \n"
                + "	        Consumidor Final               \n");
        if (!modeloEconomico)
        {
            relatorio.append("Codigo    Descricao                  \n");
            relatorio.append(" Qtde   Un    Valor Unit     Valor Total\n");
        }
        double qtdeTotal = 0.0;
        produtosDANFE.first();
        while (!produtosDANFE.eof())
        {
            if (!modeloEconomico)
            {
                int lenDesc = produtosDANFE.fieldByName("DESCRICAO").asString().length();
                relatorio.append(fillEspacoDireita(produtosDANFE.fieldByName("CPRODUTO").asString(), 10))
                        .append(produtosDANFE.fieldByName("DESCRICAO").asString().substring(0, lenDesc < 38 ? lenDesc : 38)).append("\n");//solicitado pelo cliente para cortar se passar do limite de colunas
                relatorio.append(fillEspacoDireita(Funcoes.formatFloat("#,##0.000", produtosDANFE.fieldByName("QTDE").asDouble()), 8))
                        .append(fillEspacoDireita(produtosDANFE.fieldByName("UND").asString(), 5))
                        .append(fillEspacoDireita(Funcoes.formatFloat("#,##0.00", produtosDANFE.fieldByName("UNITARIO").asDouble()), 15))
                        .append(fillEspacoDireita(Funcoes.formatFloat("#,##0.00", produtosDANFE.fieldByName("TOTAL").asDouble()), 15)).append("\n");
            }
            qtdeTotal++;
            produtosDANFE.next();
        }
        if (!modeloEconomico)
        {
            relatorio.append("--------------------------------------------\n");
        }
        relatorio.append(fillEspacoDireita("QTD. TOTAL DE ITENS", 30)).append(fillEspacoEsquerda(Funcoes.formatFloat("#,##0.000", qtdeTotal), 18)).append("\n");
        relatorio.append(fillEspacoDireita("TOTAL ITENS R$", 30)).append(fillEspacoEsquerda(Funcoes.formatFloat("#,##0.00", principalDANFE.fieldByName("VITENSTOT").asDouble()), 18)).append("\n");
        relatorio.append(fillEspacoDireita("DESCONTOS R$", 30)).append(fillEspacoEsquerda(Funcoes.formatFloat("#,##0.00", principalDANFE.fieldByName("VDESCTOT").asDouble()), 18)).append("\n");
        relatorio.append(fillEspacoDireita("VALOR TOTAL R$", 30)).append(fillEspacoEsquerda(Funcoes.formatFloat("#,##0.00", principalDANFE.fieldByName("VNFICMSTOT").asDouble()), 18)).append("\n");
        relatorio.append(fillEspacoDireita("FORMA DE PAGAMENTO", 30)).append(fillEspacoEsquerda("VALOR PAGO", 18)).append("\n");
        TSQLDataSet formaPag = retornaFormaPagamento(vs);

        formaPag.first();
        while (!formaPag.eof())
        {
            relatorio.append(fillEspacoDireita(formaPag.fieldByName("FORMAPAGAMENTO").asString(), 30))
                    .append(fillEspacoEsquerda(formaPag.fieldByName("VLRFORMAPAGAMENTO").asString(), 18)).append("\n");
            formaPag.next();
        }
        relatorio.append("--------------------------------------------\n");
        relatorio.append("<ce>").append(principalDANFE.fieldByName("AREAMENSAGEMFISCAL").asString()).append("</ce>\n");
        relatorio.append("--------------------------------------------\n");
        relatorio.append("<ce>Numero ").append(principalDANFE.fieldByName("NNF").asString())
                .append(" Serie ").append(principalDANFE.fieldByName("SERIE").asString()).append("\n")
                .append("Emissao ").append(principalDANFE.fieldByName("DEMI").asString())
                .append("</ce> <ce>").append(principalDANFE.fieldByName("HEMI").asString())
                .append(" - Via Consumidor</ce>\n");
        relatorio.append("<ce>Consulte pela Chave de Acesso em</ce>\n");
        relatorio.append("<ce>www.nfe.fazenda.gov.br/portal</ce>\n");
        relatorio.append("<ce>CHAVE DE ACESSO</ce>\n<ce>").append(principalDANFE.fieldByName("CHAVE").asString()).append("</ce>\n");
        relatorio.append("--------------------------------------------\n");
        relatorio.append(principalDANFE.fieldByName("DESTINFONFCE").asString()).append("\n");
        relatorio.append("--------------------------------------------\n");
        relatorio.append("<ce>Consulta via leitor de QR Code</ce>\n");
        relatorio.append("<qrcode><lmodulo>5</lmodulo>").append(principalDANFE.fieldByName("QRCODE").asString())
                .append("<correcao>M</correcao></qrcode>\n");
        relatorio.append(principalDANFE.fieldByName("PROTOCOLOAUTORIZANFCE").asString()).append("\n");
        relatorio.append("<ce>").append(principalDANFE.fieldByName("INFOFISCO").asString()).append("</ce><sl>4</sl>");

        ParametrosForm pf = (ParametrosForm) TecniconLookup.lookup("TecniconParametrosForm", "ParametrosFormImpl");
        if (formaPag.locate("FORMAPAGAMENTO", "Credito Loja") && pf.retornaRegraNegocio(vs, vs.getValor("filial"), 1424).equals("S")) //segunda via não fiscal da NFCe
        {
            relatorio.append("<gui></gui>");
            relatorio.append("\n<ce>").append(principalDANFE.fieldByName("EMITNOME").asString()).append("</ce>\n");
            relatorio.append("CNPJ - ").append(principalDANFE.fieldByName("EMITCNPJ").asString()).append("\n");
            relatorio.append("IE - ").append(principalDANFE.fieldByName("EMITIE").asString())
                    .append("            ").append("IM - ").append(principalDANFE.fieldByName("EMITIM").asString()).append("\n");
            relatorio.append("Endereco - ").append(principalDANFE.fieldByName("EMITENDERECONFCE").asString()).append("\n");
            relatorio.append("--------------------------------------------\n");
            relatorio.append(principalDANFE.fieldByName("DSAIDA").asString()).append(" ")
                    .append(principalDANFE.fieldByName("HSAIDA").asString()).append("\n\n");//data e hora
            relatorio.append("<ce>NAO E DOCUMENTO FISCAL</ce>\n");
            relatorio.append("<ce><b>COMPROVANTE CREDITO OU DEBITO</b></ce>\n");
            relatorio.append("<ce>Credito Loja</ce>\n");
            relatorio.append("Valor da Compra R$ ").append(fillEspacoEsquerda(principalDANFE.fieldByName("VNFICMSTOT").asString(), 27)).append("\n");

            relatorio.append("Valor do Pagamento R$").append(fillEspacoEsquerda(formaPag.fieldByName("VLRFORMAPAGAMENTO").asString(), 25)).append("\n");
            relatorio.append(principalDANFE.fieldByName("DESTXNOME").asString()).append("\n");
            relatorio.append("<ce><b>VENDA A PRAZO</b></ce>\n");
            relatorio.append("NFC-e Numero: ").append(principalDANFE.fieldByName("NNF").asString()).append("\n");

            relatorio.append("Codigo    Descricao                  \n");
            relatorio.append(" Qtde   Un    Valor Unit     Valor Total\n");

            produtosDANFE.first();
            while (!produtosDANFE.eof())
            {
                int lenDesc = produtosDANFE.fieldByName("DESCRICAO").asString().length();
                relatorio.append(fillEspacoDireita(produtosDANFE.fieldByName("CPRODUTO").asString(), 10))
                        .append(produtosDANFE.fieldByName("DESCRICAO").asString().substring(0, lenDesc < 38 ? lenDesc : 38)).append("\n");//solicitado pelo cliente para cortar se passar do limite de colunas
                relatorio.append(fillEspacoDireita(produtosDANFE.fieldByName("QTDE").asString(), 8))
                        .append(fillEspacoDireita(produtosDANFE.fieldByName("UND").asString(), 5))
                        .append(fillEspacoDireita(produtosDANFE.fieldByName("UNITARIO").asString(), 15))
                        .append(fillEspacoDireita(produtosDANFE.fieldByName("TOTAL").asString(), 15)).append("\n");
                produtosDANFE.next();
            }
            relatorio.append("--------------------------------------------\n");
            relatorio.append(fillEspacoDireita("QTD. TOTAL DE ITENS", 30)).append(fillEspacoEsquerda("" + qtdeTotal, 18)).append("\n");
            relatorio.append(fillEspacoDireita("VALOR TOTAL R$", 30)).append(fillEspacoEsquerda(principalDANFE.fieldByName("VNFICMSTOT").asString(), 18)).append("\n");
            relatorio.append(fillEspacoDireita("FORMA DE PAGAMENTO", 30)).append(fillEspacoEsquerda("VALOR PAGO", 18)).append("\n");

            formaPag.first();
            while (!formaPag.eof())
            {
                relatorio.append(fillEspacoDireita(formaPag.fieldByName("FORMAPAGAMENTO").asString(), 30))
                        .append(fillEspacoEsquerda(formaPag.fieldByName("VLRFORMAPAGAMENTO").asString(), 18)).append("\n");
                formaPag.next();
            }

            relatorio.append("\n\n");
            relatorio.append("<ce>ASSINATURA</ce>\n\n\n");
            relatorio.append("--------------------------------------------\n\n");

        }

        return relatorio.toString();
    }

    public String fillEspacoDireita(String origem, int totalCaracteres)
    {
        StringBuilder result = new StringBuilder(origem);
        for (int i = origem.length(); i < totalCaracteres; i++)
        {
            result.append(" ");
        }
        return result.toString();
    }

    public String fillEspacoEsquerda(String origem, int totalCaracteres)
    {
        StringBuilder result = new StringBuilder(origem);
        for (int i = 0; i < totalCaracteres - origem.length(); i++)
        {
            result.insert(i, " ");
        }
        return result.toString();
    }

    public String gerarQRCode(VariavelSessao vs, String xml, String chave) throws ExcecaoTecnicon
    {
        /*String chave*/

        Document document;
        try
        {
            document = Utils.strToDoc(vs, xml);
        } catch (ParserConfigurationException | SAXException | IOException ex)
        {
            throw new ExcecaoTecnicon(vs, ex);
        }
        String datahoraEmi = "";
        String tpAmb = "";
        String tpEmi = "";
        NodeList ide = document.getElementsByTagName("ide").item(0).getChildNodes();
        for (int i = 0; i < ide.getLength(); i++)
        {   //date e hora de emissão
            if ("dhEmi".equals(ide.item(i).getNodeName()))
            {
                datahoraEmi = ide.item(i).getTextContent();
            }
            if ("tpAmb".equals(ide.item(i).getNodeName()))
            {
                tpAmb = ide.item(i).getTextContent();
            }
            if ("tpEmis".equals(ide.item(i).getNodeName()))
            {
                tpEmi = ide.item(i).getTextContent();
            }

        }

        String cDest = "";
        Node tagdest = document.getElementsByTagName("dest").item(0);
        if (tagdest != null)
        {
            NodeList dest = tagdest.getChildNodes();
            for (int i = 0; i < dest.getLength(); i++)
            {
                if (dest.item(i).getNodeName().equals("CNPJ"))
                {
                    cDest = dest.item(i).getTextContent();
                }
                if (dest.item(i).getNodeName().equals("CPF"))
                {
                    cDest = dest.item(i).getTextContent();
                }
                if (dest.item(i).getNodeName().equals("idEstrangeiro"))
                {
                    cDest = dest.item(i).getTextContent();
                }
            }
        }

        Node aux = document.getElementsByTagName("total").item(0);
        String vnf = "", vicms = "";
        if (aux != null)
        {
            NodeList total = aux.getChildNodes();
            for (int i = 0; i < total.getLength(); i++)
            {

                if (total.item(i).getNodeName().equals("ICMSTot"))
                {
                    NodeList vTrib = total.item(i).getChildNodes();
                    for (int j = 0; j < vTrib.getLength(); j++)
                    {
                        if ("vNF".equals(vTrib.item(j).getNodeName()))
                        {
                            vnf = vTrib.item(j).getTextContent();
                        }
                        if ("vICMS".equals(vTrib.item(j).getNodeName()))
                        {
                            vicms = vTrib.item(j).getTextContent();
                        }
                    }
                }
            }
        }
        String qrcode = ("chNFe=").concat(chave);

        aux = document.getElementsByTagName("DigestValue").item(0);

        TClientDataSet cdsParNFCE = TClientDataSet.create(vs);
        cdsParNFCE.createDataSet();
        cdsParNFCE.commandText("SELECT PARAMETRONFE.SEQCSC, PARAMETRONFE.CSC,"
                + " PARAMETRONFE.URLNFCEPRODUCAO, PARAMETRONFE.URLNFCEHOMOLOGACAO FROM PARAMETRONFE");
        cdsParNFCE.condicao(" WHERE PARAMETRONFE.CFILIAL = " + vs.getValor("filial"));
        cdsParNFCE.open();

        //Gerando o qrcode
        qrcode = qrcode.concat("&nVersao=100");
        qrcode = qrcode.concat("&tpAmb=").concat(tpAmb);
        if (!cDest.equals(""))
        {
            qrcode = qrcode.concat("&cDest=").concat(cDest);
        }
        //datahora de emissão, no qrcode deve constar em hexadecimal
        String hexDatahoraEmi = "";
        for (char c : datahoraEmi.toCharArray())
        {
            hexDatahoraEmi = hexDatahoraEmi.concat(Integer.toHexString(c));
        }
        qrcode = qrcode.concat("&dhEmi=").concat(hexDatahoraEmi);
        //valor total da nf
        qrcode = qrcode.concat("&vNF=").concat(vnf);
        //valor do icms
        qrcode = qrcode.concat("&vICMS=").concat(vicms);
        //Digest Value da NFC-e
        String hexDigval = "", digval = "";
        {
            if (aux != null)
            {
                digval = aux.getTextContent();
            }
        }
        for (char c : digval.toCharArray())
        {
            hexDigval = hexDigval.concat(Integer.toHexString(c));
        }
        qrcode = qrcode.concat("&digVal=").concat(hexDigval);
        //cIdToken - Identificador do CSC – Código de Segurança do Contribuinte no Banco de Dados da SEFAZ

        if (cdsParNFCE.fieldByName("SEQCSC").asString().equals(""))
        {
            throw new ExcecaoTecnicon(vs, "Não foi informado Sequencial do CSC nos Parâmetros da NF-e");
        }
        if (cdsParNFCE.fieldByName("SEQCSC").asString().length() != 6)
        {
            throw new ExcecaoTecnicon(vs, "Sequencial do CSC deve conter 6 caracteres, verifique os Parâmetros da NF-e");
        }
        qrcode = qrcode.concat("&cIdToken=").concat(cdsParNFCE.fieldByName("SEQCSC").asString());
        String URLqr;
        if (tpAmb.equals("2"))
        {
            URLqr = cdsParNFCE.fieldByName("URLNFCEHOMOLOGACAO").asString();
        } else
        {
            URLqr = cdsParNFCE.fieldByName("URLNFCEPRODUCAO").asString();
        }
        if (URLqr.equals(""))
        {
            throw new ExcecaoTecnicon(vs, "Não foi definido site de consulta da NFC-e para gerar o QR Code");
        }

        URLqr = URLqr.concat(qrcode);
        //CSC - Código de Segurança do Contribuinte (antigo Token)
        if (cdsParNFCE.fieldByName("CSC").asString().equals(""))
        {
            throw new ExcecaoTecnicon(vs, "Código de Segurança do Contribuinte não informado, verifique os Parametros da NF-e");
        }
        qrcode = qrcode.concat(cdsParNFCE.fieldByName("CSC").asString());
        String hashqr = "", hexhashqr = "";
        try
        {
            hashqr = sha1(qrcode);
        } catch (NoSuchAlgorithmException ex)
        {
            System.out.println(ex);
        }
        /*for (char c : hashqr.toCharArray())
         {
         hexhashqr = hexhashqr.concat(Integer.toHexString(c));
         }*/
//        URLqr = URLqr.concat("&cHashQRCode=").concat(hexhashqr);
        URLqr = URLqr.concat("&cHashQRCode=").concat(hashqr);
        return URLqr;
    }

    @Deprecated
    public String identificaAno(String aaaaMM) throws ExcecaoTecnicon
    {
        return identificaAno(new TVariavelSessao(), aaaaMM);
    }

    private String identificaAno(VariavelSessao vs, String aaaaMM) throws ExcecaoTecnicon
    {
        if (Funcoes.isInteger(Funcoes.copy(vs, aaaaMM, 1, 2)))
        {
            int ano = Integer.parseInt(Funcoes.copy(vs, aaaaMM, 1, 2));
            if (ano < 70 && ano > 0)
            {
                return "20";
            } else if (ano > 70 && ano > 0)
            {
                return "19";
            } else
            {
                return "";
            }
        }
        return "";
    }

    private boolean validaParameter(String parameter)
    {
        return !(parameter == null || parameter.trim().equals(""));
    }

    private double getTroco(VariavelSessao vs, String nfs) throws ExcecaoTecnicon
    {
        if (nfs != null && !nfs.isEmpty())
        {
            TSQLDataSetEmp dsDados = TSQLDataSetEmp.create(vs);
            dsDados.commandText(" SELECT P_NFSRECEBER.TROCO FROM P_NFSRECEBER(" + nfs + ") ");
            dsDados.open();
            return dsDados.fieldByName("TROCO").asDouble() > 0.0 ? dsDados.fieldByName("TROCO").asDouble() : 0.0;
        } else
        {
            return 0.0;
        }
    }

    public void geraVariasDanfe(VariavelSessao vs) throws ExcecaoTecnicon, DocumentException, IOException
    {
        if ((vs.getParameter("ALLNFE") == null && vs.getParameter("ALLNFS") == null) || (vs.getParameter("ALLNFE").isEmpty() && vs.getParameter("ALLNFS").isEmpty()))
        {
            throw new ExcecaoTecnicon(vs, "Nenhuma nota informada!");
        }
        com.itextpdf.text.Document doc = new com.itextpdf.text.Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfReader reader = null;
        PdfStamper stamper = null;

        doc.open();
        String[] allNFE = vs.getParameter("ALLNFE").split(",");
        String[] allNFS = vs.getParameter("ALLNFS").split(",");

        vs.addParametros("JAIMPRIME", "FALSE");
        int pagina = 0;
        for (String nfs : allNFS)
        {
            vs.removeParametro("NFS");
            vs.addParametros("NFS", nfs);

            try
            {
                retPdfDanfe(vs);
                if (reader == null || stamper == null)
                {
                    reader = new PdfReader(Funcoes.convertStringToByte(vs.getRetornoOK()));
                    stamper = new PdfStamper(reader, baos);
                    pagina = reader.getNumberOfPages();
                    continue;
                }
                PdfReader reader2 = new PdfReader(Funcoes.convertStringToByte(vs.getRetornoOK()));
                for (int i = 1; i <= reader2.getNumberOfPages(); i++)
                {
                    stamper.insertPage(++pagina, reader2.getPageSize(i));
                    stamper.replacePage(reader2, i, pagina);
                }

            } catch (ExcecaoTecnicon ex)
            {
            }
        }
        vs.removeParametro("NFS");

        for (String nfe : allNFE)
        {
            vs.removeParametro("NFE");
            vs.addParametros("NFE", nfe);

            try
            {
                retPdfDanfe(vs);
                if (reader == null || stamper == null)
                {
                    reader = new PdfReader(Funcoes.convertStringToByte(vs.getRetornoOK()));
                    stamper = new PdfStamper(reader, baos);
                    pagina = reader.getNumberOfPages();
                    continue;
                }
                PdfReader reader2 = new PdfReader(Funcoes.convertStringToByte(vs.getRetornoOK()));
                for (int i = 1; i <= reader2.getNumberOfPages(); i++)
                {
                    stamper.insertPage(++pagina, reader2.getPageSize(i));
                    stamper.replacePage(reader2, i, pagina);
                }
            } catch (ExcecaoTecnicon ex)
            {
            }
        }
        if (stamper == null)
        {
            throw new ExcecaoTecnicon(vs, "Nenhum relatório gerado!");
        }
        stamper.close();
        if (reader != null)
        {
            reader.close();
        }
        doc.close();

        if (vs.getParameter("JAIMPRIME") != null && vs.getParameter("JAIMPRIME").toUpperCase().equals("TRUE"))
        {
            if (vs.getParameter("slImpressoras") != null)
            {
                TSQLDataSetTec imp = TSQLDataSetTec.create(vs);
                imp.createDataSet();
                imp.commandText("SELECT IMPRESSORA.SIMPRESSORA FROM IMPRESSORA WHERE IMPRESSORA.IMPRESSORA='" + vs.getParameter("slImpressoras") + "'");
                imp.open();
                vs.addParametros("imp_simpressora", imp.fieldByName("SIMPRESSORA").asString());
            }
            if (vs.getParameter("imp_simpressora") != null && !vs.getParameter("imp_simpressora").trim().isEmpty())
            {
                TSQLDataSetTec cdsImpressora = TSQLDataSetTec.create(vs);
                cdsImpressora.commandText("SELECT IMPRESSORA.SEQUIPAMENTOREMOTO FROM IMPRESSORA WHERE IMPRESSORA.SIMPRESSORA = " + vs.getParameter("imp_simpressora"));
                cdsImpressora.open();

                if (cdsImpressora.isEmpty())
                {
                    throw new ExcecaoTecnicon(vs, "Impressora informada não esta cadastrada no sistema!\nCódigo: " + vs.getParameter("imp_simpressora"));
                } else if (!cdsImpressora.fieldByName("SEQUIPAMENTOREMOTO").isNull())
                {
                    vs.addParametros("SEQUIPAMENTOREMOTO", cdsImpressora.fieldByName("SEQUIPAMENTOREMOTO").asString());
                    TClassLoader.execMethod("PrintService/MigraPCSocket", "verificaEquipamento", vs);
                }

                String idLote = vs.getParameter("idLote");
                boolean gerouId = false;
                try
                {
                    if (idLote == null || idLote.trim().equals(""))
                    {
                        idLote = (String) TClassLoader.execMethod("PrintService/FacadeImpressao", "iniciarLote", vs);
                        gerouId = true;
                    }

                    vs.addParametros("idLote", idLote);
                    vs.addParametros("idLoteImp", idLote);

                    TClassLoader.execMethod("PrintService/FacadeImpressao", "imprimir", vs, vs.getParameter("imp_simpressora"), "" + idLote, baos.toByteArray());
                } catch (Exception e)
                {
                    throw new ExcecaoTecnicon(vs, e, true);
                } finally
                {
                    if (gerouId)
                    {
                        TClassLoader.execMethod("PrintService/FacadeImpressao", "finalizarLote", vs);
                        vs.addParametros("idLote", "");
                        vs.addParametros("idLoteImp", "");
                    }
                }
            }
        }

        vs.setRetornoOK(Funcoes.conversorByteString(baos.toByteArray()));
    }
}