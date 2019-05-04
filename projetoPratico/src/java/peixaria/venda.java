package peixaria;

import br.com.tecnicon.server.context.TClassLoader;
import br.com.tecnicon.server.dataset.TClientDataSet;
import br.com.tecnicon.server.dataset.TSQLDataSetEmp;
import br.com.tecnicon.server.execoes.ExcecaoMsg;
import br.com.tecnicon.server.execoes.ExcecaoTecnicon;
import br.com.tecnicon.server.sessao.VariavelSessao;
import br.com.tecnicon.server.util.funcoes.Funcoes;
import java.util.Date;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import org.json.JSONArray;
import org.json.JSONObject;

@Stateless
@LocalBean
public class venda {

    public String inserirPedido(VariavelSessao vs) throws ExcecaoTecnicon {
        TClientDataSet pedido = TClientDataSet.create(vs, "PEDIDO");
        pedido.createDataSet();

        TClientDataSet pedidoItem = TClientDataSet.create(vs, "PEDIDOITEM");
        pedidoItem.createDataSet();

        TClientDataSet duplicatasReceber = TClientDataSet.create(vs, "RECEBER");
        duplicatasReceber.createDataSet();

        TClientDataSet nfsItemLote = TClientDataSet.create(vs, "NFSITEMLOTE");
        nfsItemLote.createDataSet();

        JSONArray itens = new JSONArray(vs.getParameter("ITENS"));

        double valor = 0d, qtde = 0d, qtdeFalta = 0d;

        TSQLDataSetEmp lote = TSQLDataSetEmp.create(vs);

        pedido.insert();
        pedido.fieldByName("CCLIFOR").asInteger(Funcoes.strToInt(vs.getParameter("CCLIFOR")));
        pedido.fieldByName("FILIALCF").asInteger(1);
        pedido.fieldByName("CIOF").asInteger(510100);
        pedido.fieldByName("CCUSTO").asInteger(19);
        pedido.fieldByName("CLOCAL").asInteger(1);
        pedido.fieldByName("CCARTEIRA").asInteger(1);
        pedido.fieldByName("CPRAZO").asInteger(1);
        pedido.fieldByName("CTRANSP").asInteger(17);
        pedido.fieldByName("FILIALTRANSP").asInteger(1);
        pedido.fieldByName("PREVDT").asDate(new Date());
        pedido.fieldByName("DATA").asDate(new Date());
        pedido.fieldByName("CVENDEDOR").asInteger(17);
        pedido.fieldByName("CFILIAL").asInteger(1);
        pedido.fieldByName("FRETE").asDouble(Funcoes.strToDouble(vs.getParameter("FRETE")));

        pedido.post();

        for (int i = 0; i < itens.length(); i++) {
            pedidoItem.insert();
            pedidoItem.fieldByName("PEDIDO").asInteger(pedido.fieldByName("PEDIDO").asInteger());
            pedidoItem.fieldByName("CIOF").asInteger(510100);
            pedidoItem.fieldByName("CCUSTO").asInteger(19);
            pedidoItem.fieldByName("CLOCAL").asInteger(1);
            pedidoItem.fieldByName("PREVDT").asDate(new Date());
            pedidoItem.fieldByName("CTBPRECO").asInteger(1);
            pedidoItem.fieldByName("CPRODUTO").asInteger(itens.getJSONObject(i).getInt("CPRODUTO"));
            pedidoItem.fieldByName("UNITARIOCLI").asDouble(itens.getJSONObject(i).getDouble("PRECO"));
            pedidoItem.fieldByName("UNITARIO").asDouble(itens.getJSONObject(i).getDouble("PRECO"));
            pedidoItem.fieldByName("QTDE").asDouble(itens.getJSONObject(i).getDouble("QTDE"));
            pedidoItem.fieldByName("QTDECLIENTE").asDouble(itens.getJSONObject(i).getDouble("QTDE"));
            pedidoItem.fieldByName("TOTAL").asDouble(itens.getJSONObject(i).getDouble("PRECO") * itens.getJSONObject(i).getDouble("QTDE"));
            pedidoItem.post();
            valor += Funcoes.multiplDouble(pedidoItem.fieldByName("UNITARIOCLI").asDouble(), pedidoItem.fieldByName("QTDE").asDouble(), 3, "");

            lote.close();
            lote.commandText("SELECT SALDOPRODUTOLOTE.SPRODUTOLOTE, SALDOPRODUTOLOTE.CLOCALLOTE, SALDOPRODUTOLOTE.SALDO AS QTDE"
                    + " FROM SALDOPRODUTOLOTE "
                    + " INNER JOIN PRODUTOLOTE ON (PRODUTOLOTE.SPRODUTOLOTE = SALDOPRODUTOLOTE.SPRODUTOLOTE) "
                    + " WHERE SALDOPRODUTOLOTE.CPRODUTO = " + pedidoItem.fieldByName("CPRODUTO").asInteger()
                    + " AND PRODUTOLOTE.ATIVO = 'S' AND PRODUTOLOTE.VCTO >= '" + Funcoes.formatarData(new Date(), "dd.MM.yyyy") + "'"
                    + " ORDER BY PRODUTOLOTE.VCTO, PRODUTOLOTE.SPRODUTOLOTE");
            lote.open();

            qtde = 0d;
            qtdeFalta = pedidoItem.fieldByName("QTDE").asDouble();

            while (!lote.eof() && qtdeFalta > 0d) {
                if (qtdeFalta == pedidoItem.fieldByName("QTDE").asDouble()
                        && (pedidoItem.fieldByName("QTDE").asDouble() < lote.fieldByName("QTDE").asDouble()
                        || pedidoItem.fieldByName("QTDE").asDouble() == lote.fieldByName("QTDE").asDouble())) {
                    qtde = pedidoItem.fieldByName("QTDE").asDouble();
                    qtdeFalta = pedidoItem.fieldByName("QTDE").asDouble() - lote.fieldByName("QTDE").asDouble();
                } else {
                    if (qtdeFalta != pedidoItem.fieldByName("QTDE").asDouble()
                            && (qtdeFalta < lote.fieldByName("QTDE").asDouble() || qtdeFalta == lote.fieldByName("QTDE").asDouble())) {
                        qtde = qtdeFalta;
                        qtdeFalta = 0d;
                    } else {
                        qtde = lote.fieldByName("QTDE").asDouble();
                        qtdeFalta = qtdeFalta - lote.fieldByName("QTDE").asDouble();
                    }
                }

                nfsItemLote.insert();
                nfsItemLote.fieldByName("PEDIDOITEM").asInteger(pedidoItem.fieldByName("PEDIDOITEM").asInteger());
                nfsItemLote.fieldByName("SPRODUTOLOTE").asInteger(lote.fieldByName("SPRODUTOLOTE").asInteger());
                nfsItemLote.fieldByName("CLOCALLOTE").asInteger(lote.fieldByName("CLOCALLOTE").asInteger());
                nfsItemLote.fieldByName("QTDE").asDouble(qtde);
                nfsItemLote.post();

                lote.next();
            }
        }

        duplicatasReceber.insert();
        duplicatasReceber.fieldByName("CFILIAL").asInteger(1);
        duplicatasReceber.fieldByName("DATA").asDate(new Date());
        duplicatasReceber.fieldByName("CCLIFOR").asInteger(Funcoes.strToInt(vs.getParameter("CCLIFOR")));
        duplicatasReceber.fieldByName("FILIALCF").asInteger(1);
        duplicatasReceber.fieldByName("CCARTEIRA").asInteger(1);
        duplicatasReceber.fieldByName("DUPLICATA").asString("PX" + pedido.fieldByName("PEDIDO").asInteger());
        duplicatasReceber.fieldByName("VCTO").asDate(Funcoes.addHoras(new Date(), 360));
        duplicatasReceber.fieldByName("VCTOP").asDate(Funcoes.addHoras(new Date(), 360));
        duplicatasReceber.fieldByName("VALOR").asDouble(valor);
        duplicatasReceber.post();

        vs.addParametros("filial", "1");
        vs.addParametros("cusuario", "25");
        vs.addParametros("empresa", "17");
        vs.addParametros("usuario", "CFJL.LILIAN");
        vs.addParametros("CCARTEIRA", "1");
        vs.addParametros("cbMsgDescVcto", "false");
        vs.addParametros("CUSTOBLOQUETO", duplicatasReceber.fieldByName("VALOR").asString());
        vs.addParametros("SRECEBER", duplicatasReceber.fieldByName("SRECEBER").asString());

        try {
            //enviarEmail(vs, inscricao);
            TClassLoader.execMethod("BloquetoImprime/BloquetoImprime", "enviarSelecionados", vs);

        } catch (ExcecaoTecnicon ex) {
            throw new ExcecaoTecnicon(vs, ex.getMessage());
        }

        return "OK";
    }

    public String inserirNfs(VariavelSessao vs, TClientDataSet ds1) throws ExcecaoTecnicon {
        if (ds1.fieldByName("DUPLICATA").asString().startsWith("PX")) {
            String[] duplicata = ds1.fieldByName("DUPLICATA").asString().split("PX");
            String codPedido = duplicata[1];

            TSQLDataSetEmp pedido = TSQLDataSetEmp.create(vs);
            pedido.commandText("SELECT PEDIDO.CIOF, PEDIDO.CCUSTO, PEDIDO.CLOCAL, PEDIDO.FRETE "
                    + " FROM PEDIDO "
                    + " WHERE PEDIDO.PEDIDO = " + codPedido);
            pedido.open();

            TClientDataSet nfs = TClientDataSet.create(vs, "NFSAIDA");
            nfs.createDataSet();

            double valorTotalNfs = pedido.fieldByName("FRETE").asDouble();

            nfs.insert();
            nfs.fieldByName("CFILIAL").asInteger(1);
            nfs.fieldByName("CCLIFOR").asString(ds1.fieldByName("CCLIFOR").asString());
            nfs.fieldByName("FILIALCF").asString("1");
            nfs.fieldByName("CIOF").asString(pedido.fieldByName("CIOF").asString());
            nfs.fieldByName("CCUSTO").asInteger(pedido.fieldByName("CCUSTO").asInteger());
            nfs.fieldByName("CLOCAL").asInteger(pedido.fieldByName("CLOCAL").asInteger());
            nfs.fieldByName("CMODELONF").asString("1");
            nfs.fieldByName("NF").asString(Funcoes.copy(vs, Funcoes.formatarData(new Date(), "dd.MM.yyyy"), 9, 2)
                    + Funcoes.copy(vs, Funcoes.formatarData(new Date(), "dd.MM.yyyy"), 4, 2)
                    + Funcoes.copy(vs, Funcoes.formatarData(new Date(), "dd.MM.yyyy"), 1, 2));
            nfs.fieldByName("NF1").asString(Funcoes.copy(vs, Funcoes.formatarData(new Date(), "dd.MM.yyyy"), 9, 2)
                    + Funcoes.copy(vs, Funcoes.formatarData(new Date(), "dd.MM.yyyy"), 4, 2)
                    + Funcoes.copy(vs, Funcoes.formatarData(new Date(), "dd.MM.yyyy"), 1, 2));
            nfs.fieldByName("VALOR_TOTAL").asDouble(0d);
            nfs.fieldByName("DATA").asDate(new Date());
            nfs.fieldByName("FRETE").asDouble(pedido.fieldByName("FRETE").asDouble());
            nfs.post();

            pedido.close();
            pedido.commandText("SELECT PEDIDOITEM.CPRODUTO, PEDIDOITEM.QTDE, NFSITEMLOTE.SPRODUTOLOTE, NFSITEMLOTE.CLOCALLOTE, "
                    + " NFSITEMLOTE.QTDE AS QTDELOTE, PEDIDOITEM.PEDIDOITEM, PEDIDOITEM.UNITARIOCLI "
                    + " FROM PEDIDOITEM"
                    + " LEFT JOIN NFSITEMLOTE ON (NFSITEMLOTE.PEDIDOITEM = PEDIDOITEM.PEDIDOITEM)"
                    + " WHERE PEDIDOITEM.PEDIDO = " + codPedido);
            pedido.open();

            TClientDataSet nfsItem = TClientDataSet.create(vs, "NFSITEM");
            nfsItem.createDataSet();

            TClientDataSet nfsItemLote = TClientDataSet.create(vs, "NFSITEMLOTE");
            nfsItemLote.createDataSet();

            int pedidoItem = 0;

            while (!pedido.eof()) {
                nfsItem.insert();
                nfsItem.fieldByName("NFS").asString(nfs.fieldByName("NFS").asString());
                nfsItem.fieldByName("CCUSTO").asInteger(nfs.fieldByName("CCUSTO").asInteger());
                nfsItem.fieldByName("CIOF").asString(nfs.fieldByName("CIOF").asString());
                nfsItem.fieldByName("UNITARIOCLI").asDouble(pedido.fieldByName("UNITARIOCLI").asDouble());
                nfsItem.fieldByName("UNITARIO").asDouble(pedido.fieldByName("UNITARIOCLI").asDouble());
                nfsItem.fieldByName("TOTAL").asDouble(0d);
                nfsItem.fieldByName("IPI").asDouble(0d);
                nfsItem.fieldByName("CPRODUTO").asInteger(pedido.fieldByName("CPRODUTO").asInteger());
                nfsItem.fieldByName("CLOCAL").asInteger(nfs.fieldByName("CLOCAL").asInteger());
                nfsItem.fieldByName("QTDE").asDouble(pedido.fieldByName("QTDE").asDouble());
                nfsItem.fieldByName("CIPI").asInteger(1313);
                nfsItem.fieldByName("QTDECLIENTE").asDouble(nfsItem.fieldByName("QTDE").asDouble());
                nfsItem.fieldByName("PEDIDOITEM").asInteger(pedido.fieldByName("PEDIDOITEM").asInteger());
                nfsItem.post();

                valorTotalNfs += nfsItem.fieldByName("UNITARIO").asDouble();

                pedidoItem = pedido.fieldByName("PEDIDOITEM").asInteger();

                while (!pedido.eof() && pedidoItem == pedido.fieldByName("PEDIDOITEM").asInteger()) {
                    if (!pedido.fieldByName("SPRODUTOLOTE").asString().isEmpty()) {
                        nfsItemLote.insert();
                        nfsItemLote.fieldByName("NFSITEM").asInteger(nfsItem.fieldByName("NFSITEM").asInteger());
                        nfsItemLote.fieldByName("SPRODUTOLOTE").asInteger(pedido.fieldByName("SPRODUTOLOTE").asInteger());
                        nfsItemLote.fieldByName("CLOCALLOTE").asInteger(pedido.fieldByName("CLOCALLOTE").asInteger());
                        nfsItemLote.fieldByName("QTDE").asDouble(pedido.fieldByName("QTDELOTE").asDouble());
                        nfsItemLote.post();
                    }
                    pedido.next();
                }
            }

            nfs.edit();
            nfs.fieldByName("VALOR_TOTAL").asDouble(valorTotalNfs);
            nfs.post();

        }

        return "OK";
    }

    public JSONObject verificaCidadeFrete(VariavelSessao vs) throws ExcecaoTecnicon {
        JSONObject retorno = new JSONObject();

        TSQLDataSetEmp cidadeFrete = TSQLDataSetEmp.create(vs);
        cidadeFrete.commandText("SELECT CIDADESFRETEPX.CCIDADE, CIDADESFRETEPX.VALOR "
                + " FROM CIDADESFRETEPX "
                + " INNER JOIN CIDADE ON (CIDADE.CCIDADE = CIDADESFRETEPX.CCIDADE) "
                + " WHERE CIDADE.CIDADE = '" + vs.getParameter("CIDADE").toUpperCase() + "'");
        cidadeFrete.open();

        if (cidadeFrete.isEmpty()) {
            return retorno.put("N", "Nenhuma forma de envio disponível para esta cidade.");
        } else {
            return retorno.put("S", cidadeFrete.fieldByName("VALOR").asString());
        }
    }

//    public void enviarSelecionados(VariavelSessao vs) throws ExcecaoTecnicon {
//        String[] retorno = new String[1];
//        retorno[0] = "";
//        String CCARTEIRA = vs.getParameter("CCARTEIRA");
//        String SRECEBER = vs.getParameter("SRECEBER");
//        String cbMsgDescVcto = vs.getParameter("cbMsgDescVcto");
//        String CUSTOBLOQUETO = vs.getParameter("CUSTOBLOQUETO");
//        String remetentes = "";
//        TClientDataSet carteira = null;
//        TClientDataSet receber = null;
//        TClientDataSet cliForEnd = null;
//        TSQLDataSetEmp cdsCom1 = null;
//        String XEMAILTEXTO;
//        TClientDataSet filial = null;
//        TSQLDataSetEmp CDSC = null;
//
//        /*DADOS PARA MOSTRAR CASO OCORRA ERRO*/
//        int cliente = 0;
//        int filialCli = 0;
//        String nomeCli = "";
//
//        boolean msgTrow = false;
//        Map<String, byte[]> anexos = new HashMap<>();
//        try {
//            CDSC = TSQLDataSetEmp.create(vs);
//            receber = TClientDataSet.create(vs, "RECEBER");
//            receber.createDataSet();
//            receber.condicao(" WHERE RECEBER.SRECEBER = " + SRECEBER);
//            receber.open();
//
//            filial = TClientDataSet.create(vs, "FILIAL");
//            filial.createDataSet();
//            filial.condicao(" WHERE FILIAL.CFILIAL = " + vs.getValor("filial"));
//            filial.open();
//
//            cliForEnd = TClientDataSet.create(vs, "CLIFOREND");
//            cliForEnd.createDataSet();
//            cliForEnd.close();
//            cliForEnd.condicao(" WHERE CLIFOREND.CCLIFOR=" + receber.fieldByName("CCLIFOR").asInteger()
//                    + " AND CLIFOREND.FILIALCF=" + receber.fieldByName("FILIALCF").asInteger());
//            cliForEnd.open();
//
//            cliente = cliForEnd.fieldByName("CCLIFOR").asInteger();
//            filialCli = cliForEnd.fieldByName("FILIALCF").asInteger();
//            nomeCli = cliForEnd.fieldByName("NOMEFILIAL").asString();
//
//            carteira = TClientDataSet.create(vs, "CARTEIRA");
//            carteira.createDataSet();
//
//            carteira.close();
//            carteira.condicao(" WHERE CARTEIRA.CCARTEIRA=" + CCARTEIRA + " AND CARTEIRA.ATIVO = 'S'");
//            carteira.open();
//            if (carteira.isEmpty() == true) {
//                msgTrow = true;
//                throw new ExcecaoTecnicon(vs, "Cód. Cliente: " + cliForEnd.fieldByName("CCLIFOR").asString() + " Filial: " + cliForEnd.fieldByName("FILIALCF").asString() + " Nome: " + cliForEnd.fieldByName("NOMEFILIAL").asString() + "|TEC#|<br>E-mail não foi enviado, código carteira não localizado ou inativo!");
//            }
//
//            cdsCom1 = TSQLDataSetEmp.create(vs);
//
//            if (("" + TClassLoader.execMethod("Bloquetos/Bloquetos", "IMPRIMEBLOQUETO", vs, Funcoes.strToInt(CCARTEIRA), "N", Boolean.parseBoolean(cbMsgDescVcto), "S", receber, retorno, Funcoes.strToDouble((CUSTOBLOQUETO != null && !CUSTOBLOQUETO.equals("") ? CUSTOBLOQUETO : (CUSTOBLOQUETO == null || CUSTOBLOQUETO.equals("") ? "0,00" : CUSTOBLOQUETO))))).equals("RELATORIO3063")) {
//                msgTrow = true;
//                throw new ExcecaoTecnicon(vs, "MOSTRA|TEC#|Cód. Cliente: " + cliForEnd.fieldByName("CCLIFOR").asString() + " Filial: " + cliForEnd.fieldByName("FILIALCF").asString() + " Nome: " + cliForEnd.fieldByName("NOMEFILIAL").asString() + "|TEC#|<br>E-mail não foi enviado.");
//            }
//
//            receber.close();
//            receber.condicao(" WHERE RECEBER.SRECEBER = " + SRECEBER);
//            receber.open();
//
//            cdsCom1.close();
//            cdsCom1.commandText(
//                    " SELECT CONTATOCLI.EMAIL FROM CONTATOCLI "
//                    + " WHERE CONTATOCLI.CCLIFOR=" + receber.fieldByName("CCLIFOR").asInteger() + " "
//                    + " AND CONTATOCLI.FILIALCF=" + receber.fieldByName("FILIALCF").asInteger() + " AND CONTATOCLI.VINCOMPRA like '%F%' ");
//            cdsCom1.open();
//
//            while (!cdsCom1.eof()) {
//                remetentes += cdsCom1.fieldByName("EMAIL").asString() + ";";
//                cdsCom1.next();
//            }
//
//            CDSC.close();
//            CDSC.commandText("SELECT BLOQUETO.CBLOQUETO,BLOQUETO.MENS,BLOQUETO.LOCALPGTO"
//                    + " FROM BLOQUETO"
//                    + " WHERE BLOQUETO.CBLOQUETO=" + carteira.fieldByName("CBLOQUETO").asString());
//            CDSC.open();
//            int BLOQ_CBLOQUETO = CDSC.fieldByName("CBLOQUETO").asInteger();
//
//            CDSC.close();
//            CDSC.commandText(" SELECT BLOQNOSSONUMERO.MODELOBOLETO, BLOQNOSSONUMERO.TPCONVENIO, TSUBSTR(BLOQNOSSONUMERO.CODBANCO,1,3) CODBANCO "
//                    + " FROM BLOQNOSSONUMERO"
//                    + " WHERE BLOQNOSSONUMERO.CBLOQUETO=" + BLOQ_CBLOQUETO
//                    + " AND BLOQNOSSONUMERO.CFILIAL=" + vs.getValor("filial"));
//            CDSC.open();
//
//            XEMAILTEXTO = ("Prezado(a) " + cliForEnd.fieldByName("NOMEFILIAL").asString()) + "<br />";
//            XEMAILTEXTO += ("Segue em anexo o boleto com data de vencimento " + receber.fieldByName("VCTO").asString()) + "<br />";
//            XEMAILTEXTO += "Dupl: " + (receber.fieldByName("DUPLICATA").asString() + " <br /> Parc: " + receber.fieldByName("PARCELA").asString() + "<br />");
//            XEMAILTEXTO += ("<br />");
//            XEMAILTEXTO += ("<br />");
//            XEMAILTEXTO += ("Este e-mail foi gerado pelo sistema TECNICON Business Suite na empresa " + filial.fieldByName("RAZAO_SOCIAL").asString());
//
//            RelatorioEsp relEsp = (RelatorioEsp) TecniconLookup.lookup("TecniconRelatorioEsp", "RelatorioEsp");
//            VariavelSessao vs2 = vs.clone();
//            vs2.addParametros("SRECEBER", SRECEBER);
//            vs2.addParametros("IMPTODAS", "FALSE");
//            vs2.addParametros("FILTROS", "");
//            vs2.addParametros("IMPNAO", "FALSE");
//            vs2.addParametros("CFILIAL", vs.getValor("filial"));
//
//            switch (CDSC.fieldByName("MODELOBOLETO").asString()) {
//                case "1":
//                    vs2.addParametros("relatorioesp", "132");
//                    break;
//                case "2":
//                    vs2.addParametros("relatorioesp", "1911");
//                    break;
//                case "3":
//                    vs2.addParametros("relatorioesp", "1931");
//                    break;
//                case "5":
//                    vs2.addParametros("relatorioesp", "1921");
//                    break;
//                default:
//                    if ("4".equals(CDSC.fieldByName("MODELOBOLETO").asString())) {
//                        vs2.addParametros("relatorioesp", "1901");
//                    } else if (vs.getValor("nomeFilial").contains("RHRISS")) {
//                        vs2.addParametros("relatorioesp", "1941");
//                    } else if (("07".equals(CDSC.fieldByName("TPCONVENIO").asString())) || ("748".equals(CDSC.fieldByName("CODBANCO").asString()))) {
//                        vs2.addParametros("relatorioesp", "1931");
//                    } else if (("001".equals(CDSC.fieldByName("CODBANCO").asString()))
//                            || ("027".equals(CDSC.fieldByName("CODBANCO").asString()))
//                            || ("041".equals(CDSC.fieldByName("CODBANCO").asString()))
//                            || ("104".equals(CDSC.fieldByName("CODBANCO").asString()))
//                            || ("237".equals(CDSC.fieldByName("CODBANCO").asString()))) {
//                        vs2.addParametros("relatorioesp", "132");
//                    } else if ("5".equals(CDSC.fieldByName("MODELOBOLETO").asString())) {
//                        vs2.addParametros("relatorioesp", "1921");
//                    } else {
//                        vs2.addParametros("relatorioesp", "1911");
//                    }
//                    break;
//            }
//            relEsp.gerarRelatorio(vs2);
//
//            TClientDataSet cdsUserMail = TClientDataSet.create(vs, "USUARIOEMAIL");
//            cdsUserMail.createDataSet();
//            cdsUserMail.condicao("WHERE USUARIOEMAIL.CUSUARIO=" + vs.getValor("cusuario"));
//            cdsUserMail.open();
//
//            if (!"".equals(remetentes)) {
//
//                /* Otimização  - Ilario 19/02/15*/
//                anexos.put(receber.fieldByName("NOSSONUMERO").asString() + ".pdf", Funcoes.convertStringToByte(vs2.getRetornoOK()));
//
//                EmailConfig config = new EmailConfig(cdsUserMail.fieldByName("USUARIO").asString(), cdsUserMail.fieldByName("SENHA").asString(),
//                        cdsUserMail.fieldByName("HOSTSMTP").asString(), cdsUserMail.fieldByName("EMAIL").asString(), cdsUserMail.fieldByName("NOME").asString(),
//                        "", "", cdsUserMail.fieldByName("PORTSMTP").asInteger(),
//                        cdsUserMail.fieldByName("SSL").asString(), Integer.parseInt(vs.getValor("empresa")), 1);
//
//                String assunto = filial.fieldByName("FANTASIA").asString() + " - boleto com vencimento em " + receber.fieldByName("VCTO").asString()
//                        + " " + receber.fieldByName("DUPLICATA").asString() + "/" + receber.fieldByName("PARCELA").asString();
//
//                /* Otimização  - Ilario 19/02/15*/
//                new TEnviarEmail().enviarEmail(remetentes, "", "", assunto, XEMAILTEXTO, config, anexos);
//
//                vs.setRetornoOK("Cód. Cliente: " + cliForEnd.fieldByName("CCLIFOR").asString() + " Filial: " + cliForEnd.fieldByName("FILIALCF").asString() + " Nome: " + cliForEnd.fieldByName("NOMEFILIAL").asString() + "|TEC#|<br>E-mail enviado, para: " + remetentes + "|TEC#|" + retorno[0]);
//            } else {
//                msgTrow = true;
//                throw new ExcecaoTecnicon(vs, "MOSTRA|TEC#|Cód. Cliente: " + cliForEnd.fieldByName("CCLIFOR").asString() + " Filial: " + cliForEnd.fieldByName("FILIALCF").asString() + " Nome: " + cliForEnd.fieldByName("NOMEFILIAL").asString() + "|TEC#|<br>E-mail não foi enviado, não possui destinatário.");
//                /*sol: 149398 - erro de descricao */
//
//            }
//        } catch (ExcecaoTecnicon e) {
//            if (msgTrow) {
//                throw new ExcecaoTecnicon(vs, e);
//            }
//
//            if (cliente > 0) {
//                throw new ExcecaoTecnicon(vs, "Cód. Cliente: " + cliente + " Filial: " + filialCli + " Nome: " + nomeCli + "|TEC#|E-mail não foi enviado, para " + (remetentes.isEmpty() ? "\"Sem remetente\"" : remetentes) + "<br>Erro: " + e.getMessage() + ".");
//            } else {
//                throw new ExcecaoTecnicon(vs, "E-mail não foi enviado, para " + (remetentes.isEmpty() ? "\"Sem remetente\"" : remetentes) + "|TEC#|Erro: " + e.getMessage() + ".");
//            }
//        } finally {
//            if (carteira != null) {
//                carteira.close();
//            }
//
//            if (receber != null) {
//                receber.close();
//            }
//
//            if (filial != null) {
//                filial.close();
//            }
//
//            if (CDSC != null) {
//                CDSC.close();
//            }
//
//            if (cdsCom1 != null) {
//                cdsCom1.close();
//            }
//
//            if (cliForEnd != null) {
//                cliForEnd.close();
//            }
//        }
//    }

}
