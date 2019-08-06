package peixaria;

import br.com.tecnicon.enviaemail.TEnviarEmail;
import br.com.tecnicon.server.context.TClassLoader;
import br.com.tecnicon.server.context.TecniconLookup;
import br.com.tecnicon.server.dataset.TClientDataSet;
import br.com.tecnicon.server.dataset.TSQLDataSetEmp;
import br.com.tecnicon.server.execoes.ExcecaoMsg;
import br.com.tecnicon.server.execoes.ExcecaoTecnicon;
import br.com.tecnicon.server.interfaces.ParametrosForm;
import br.com.tecnicon.server.interfaces.RelatorioEsp;
import br.com.tecnicon.server.model.EmailConfig;
import br.com.tecnicon.server.sessao.VariavelSessao;
import br.com.tecnicon.server.util.funcoes.Funcoes;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

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

        double valor = Funcoes.strToDouble(vs.getParameter("FRETE")), qtde = 0d, qtdeFalta = 0d;

        int cCliFor = Funcoes.strToInt(vs.getParameter("CCLIFOR"));

        TSQLDataSetEmp lote = TSQLDataSetEmp.create(vs);

        validaSaldo(vs, itens);

        pedido.insert();
        pedido.fieldByName("CCLIFOR").asInteger(cCliFor);
        pedido.fieldByName("FILIALCF").asInteger(1);
        pedido.fieldByName("CIOF").asInteger(510100);
        pedido.fieldByName("CCUSTO").asInteger(19);
        pedido.fieldByName("CLOCAL").asInteger(1);
        pedido.fieldByName("CCARTEIRA").asInteger(1);
        pedido.fieldByName("CPRAZO").asInteger(1);
        pedido.fieldByName("CTRANSP").asInteger(17);
        pedido.fieldByName("FILIALTRANSP").asInteger(1);
        pedido.fieldByName("PREVDT").asDate(Funcoes.incDay(new Date(), 14));
        pedido.fieldByName("DATA").asDate(new Date());
        pedido.fieldByName("CVENDEDOR").asInteger(17);
        pedido.fieldByName("CFILIAL").asInteger(1);
        pedido.fieldByName("FRETE").asDouble(Funcoes.strToDouble(vs.getParameter("FRETE")));

        lote.close();
        lote.commandText("SELECT CONTATO, EMAIL "
                + " FROM CONTATOCLI "
                + " WHERE CONTATOCLI.CCLIFOR = " + pedido.fieldByName("CCLIFOR").asInteger()
                + " AND CONTATOCLI.FILIALCF = " + pedido.fieldByName("FILIALCF").asInteger()
                + " AND CONTATOCLI.VINCOMPRA = 'F'");
        lote.open();

        pedido.fieldByName("CONTATO").asString(lote.fieldByName("CONTATO").asString());
        pedido.fieldByName("EMAIL").asString(lote.fieldByName("EMAIL").asString());

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
        duplicatasReceber.fieldByName("PARCELA").asString("1/1");
        duplicatasReceber.fieldByName("DUPLICATA").asString("PX" + pedido.fieldByName("PEDIDO").asInteger());
        duplicatasReceber.fieldByName("NOSSONUMERO").asString(duplicatasReceber.fieldByName("DUPLICATA").asString());
        duplicatasReceber.fieldByName("VCTO").asDate(Funcoes.incDay(new Date(), 7));
        duplicatasReceber.fieldByName("VCTOP").asDate(Funcoes.incDay(new Date(), 7));
        duplicatasReceber.fieldByName("VALOR").asDouble(valor);
        duplicatasReceber.post();

        try {

            vs.addParametros("filial", "1");
            vs.addParametros("cusuario", "25");
            vs.addParametros("empresa", "17");
            vs.addParametros("usuario", "CFJL.LILIAN");
            vs.addParametros("CCARTEIRA", "26");
            vs.addParametros("cbMsgDescVcto", "false");
            vs.addParametros("CUSTOBLOQUETO", duplicatasReceber.fieldByName("VALOR").asString());
            vs.addParametros("SRECEBER", duplicatasReceber.fieldByName("SRECEBER").asString());

            TClassLoader.execMethod("BloquetoImprime/BloquetoImprime", "enviarSelecionados", vs);

            vs.setRetornoOK("Compra realizada com sucesso! \nUm e-mail com o boleto da compra foi enviado para o seu e-mail.");

        } catch (ExcecaoTecnicon ex) {
            throw new ExcecaoTecnicon(vs, ex.getMessage());
        }

        return "OK";
    }

    public void validaSaldo(VariavelSessao vs, JSONArray itens) throws ExcecaoTecnicon {
        TSQLDataSetEmp produtoSaldo = TSQLDataSetEmp.create(vs);
        StringBuilder prodSemSaldo = new StringBuilder();

        for (int i = 0; i < itens.length(); i++) {
            produtoSaldo.close();
            produtoSaldo.commandText("SELECT SUM(SALDOPRODUTOLOTE.SALDO) SALDO, PRODUTO.MERCADORIA "
                    + " FROM SALDOPRODUTOLOTE "
                    + " INNER JOIN PRODUTOLOTE ON (PRODUTOLOTE.SPRODUTOLOTE = SALDOPRODUTOLOTE.SPRODUTOLOTE)"
                    + " INNER JOIN PRODUTO ON (PRODUTO.CPRODUTO = SALDOPRODUTOLOTE.CPRODUTO) "
                    + " WHERE SALDOPRODUTOLOTE.CPRODUTO = " + itens.getJSONObject(i).getInt("CPRODUTO")
                    + " AND PRODUTOLOTE.ATIVO = 'S' "
                    + " AND PRODUTOLOTE.VCTO >= '" + Funcoes.formatarData(new Date(), "dd.MM.yyyy") + "'"
                    + " GROUP BY SALDOPRODUTOLOTE.CPRODUTO, PRODUTO.MERCADORIA"
                    + " HAVING SUM(SALDOPRODUTOLOTE.SALDO) < " + itens.getJSONObject(i).getDouble("QTDE"));
            produtoSaldo.open();

            if (!produtoSaldo.isEmpty()) {
                prodSemSaldo.append(produtoSaldo.fieldByName("MERCADORIA").asString()).append(" - Saldo: ")
                        .append(produtoSaldo.fieldByName("SALDO").asDouble()).append("\n");
            }
        }

        if (!prodSemSaldo.toString().isEmpty()) {
            throw new ExcecaoMsg(vs, "Produtos com saldo em estoque menor do que a quantidade requisitada:\n"
                    + prodSemSaldo.toString() + "Para finalizar a compra, informe uma quantidade disponível.");
        }
    }

    public String inserirNfs(VariavelSessao vs, TClientDataSet ds1) throws ExcecaoTecnicon {
        try {
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

                TClientDataSet duplicatasReceber = TClientDataSet.create(vs, "RECEBER");
                duplicatasReceber.createDataSet();
                duplicatasReceber.condicao("WHERE DUPLICATA = '" + ds1.fieldByName("DUPLICATA").asString() + "'");
                duplicatasReceber.open();

                double valorTotalNfs = 0.0;

                nfs.insert();
                nfs.fieldByName("CFILIAL").asInteger(1);
                nfs.fieldByName("CCLIFOR").asString(ds1.fieldByName("CCLIFOR").asString());
                nfs.fieldByName("FILIALCF").asString("1");
                nfs.fieldByName("CIOF").asString(pedido.fieldByName("CIOF").asString());
                nfs.fieldByName("CFOP").asString("5101");
                nfs.fieldByName("CCUSTO").asInteger(pedido.fieldByName("CCUSTO").asInteger());
                nfs.fieldByName("CLOCAL").asInteger(pedido.fieldByName("CLOCAL").asInteger());
                nfs.fieldByName("CMODELONF").asString("31");
                nfs.fieldByName("NF").asString(Funcoes.copy(vs, Funcoes.formatarData(new Date(), "dd.MM.yyyy"), 9, 2)
                        + Funcoes.copy(vs, Funcoes.formatarData(new Date(), "dd.MM.yyyy"), 4, 2)
                        + Funcoes.copy(vs, Funcoes.formatarData(new Date(), "dd.MM.yyyy"), 1, 2));
                nfs.fieldByName("NF1").asString(Funcoes.copy(vs, Funcoes.formatarData(new Date(), "dd.MM.yyyy"), 9, 2)
                        + Funcoes.copy(vs, Funcoes.formatarData(new Date(), "dd.MM.yyyy"), 4, 2)
                        + Funcoes.copy(vs, Funcoes.formatarData(new Date(), "dd.MM.yyyy"), 1, 2));
                nfs.fieldByName("VALOR_TOTAL").asDouble(0d);
                nfs.fieldByName("DATA").asDate(new Date());
                nfs.fieldByName("DATASAIDA").asDate(new Date());
                nfs.fieldByName("FRETE").asDouble(pedido.fieldByName("FRETE").asDouble());
                nfs.fieldByName("QTDE").asDouble(1.0);
                nfs.post();

                pedido.close();
                pedido.commandText("SELECT PEDIDOITEM.CPRODUTO, PEDIDOITEM.QTDE, NFSITEMLOTE.SPRODUTOLOTE, NFSITEMLOTE.CLOCALLOTE, "
                        + " NFSITEMLOTE.QTDE AS QTDELOTE, PEDIDOITEM.PEDIDOITEM, PEDIDOITEM.UNITARIOCLI, NFSITEMLOTE.SNFSITEMLOTE "
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
                    nfsItem.fieldByName("CFOP").asString("5101");
                    nfsItem.fieldByName("UNITARIOCLI").asDouble(pedido.fieldByName("UNITARIOCLI").asDouble());
                    nfsItem.fieldByName("UNITARIO").asDouble(pedido.fieldByName("UNITARIOCLI").asDouble());
                    nfsItem.fieldByName("IPI").asDouble(0d);
                    nfsItem.fieldByName("CPRODUTO").asInteger(pedido.fieldByName("CPRODUTO").asInteger());
                    nfsItem.fieldByName("CLOCAL").asInteger(nfs.fieldByName("CLOCAL").asInteger());
                    nfsItem.fieldByName("QTDE").asDouble(pedido.fieldByName("QTDE").asDouble());
                    nfsItem.fieldByName("TOTAL").asDouble(nfsItem.fieldByName("UNITARIO").asDouble() * nfsItem.fieldByName("QTDE").asDouble());
                    nfsItem.fieldByName("CIPI").asInteger(1313);
                    nfsItem.fieldByName("QTDECLIENTE").asDouble(nfsItem.fieldByName("QTDE").asDouble());
                    nfsItem.fieldByName("PEDIDOITEM").asInteger(pedido.fieldByName("PEDIDOITEM").asInteger());
                    nfsItem.fieldByName("ST").asString("000");
                    nfsItem.post();

                    valorTotalNfs += nfsItem.fieldByName("UNITARIO").asDouble() * nfsItem.fieldByName("QTDE").asDouble();

                    pedidoItem = pedido.fieldByName("PEDIDOITEM").asInteger();

                    while (!pedido.eof() && pedidoItem == pedido.fieldByName("PEDIDOITEM").asInteger()) {
                        if (!pedido.fieldByName("SPRODUTOLOTE").asString().isEmpty()) {
                            nfsItemLote.close();
                            nfsItemLote.condicao(" WHERE NFSITEMLOTE.SNFSITEMLOTE = " + pedido.fieldByName("SNFSITEMLOTE").asInteger());
                            nfsItemLote.open();

                            nfsItemLote.edit();
                            nfsItemLote.fieldByName("NFSITEM").asInteger(nfsItem.fieldByName("NFSITEM").asInteger());
                            nfsItemLote.post();
                        }
                        pedido.next();
                    }
                }

                nfs.edit();
                nfs.fieldByName("VALOR_TOTAL").asDouble(valorTotalNfs);
                nfs.post();

                duplicatasReceber.edit();
                duplicatasReceber.fieldByName("NFS").asInteger(nfs.fieldByName("NFS").asInteger());
                duplicatasReceber.post();

            }
        } catch (Exception e) {
            throw new ExcecaoTecnicon(vs, e.getMessage());
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

    public String consultarEndPadrao(VariavelSessao vs) throws ExcecaoTecnicon {
        TSQLDataSetEmp end = TSQLDataSetEmp.create(vs);
        end.commandText("SELECT CLIFOREND.CEP "
                + " FROM CLIFOREND "
                + " WHERE CLIFOREND.CCLIFOR = " + vs.getParameter("CCLIFOR"));
        end.open();

        return end.fieldByName("CEP").asString();
    }

    public String consultarTodosEnds(VariavelSessao vs) throws ExcecaoTecnicon {
        TSQLDataSetEmp end = TSQLDataSetEmp.create(vs);
        end.commandText("SELECT ENDEREC.CEP, ENDEREC.CIDADE, ENDEREC.UF, ENDEREC.PADRAO"
                + " FROM ( "
                + " SELECT CLIFOREND.CEP, CIDADE.CIDADE, CIDADE.UF, 0 AS PADRAO "
                + " FROM CLIFOREND "
                + " INNER JOIN CIDADE ON (CIDADE.CCIDADE = CLIFOREND.CCIDADE) "
                + " WHERE CLIFOREND.CCLIFOR = " + vs.getParameter("CCLIFOR")
                + " UNION ALL "
                + " SELECT CLIENDENT.CEP, CIDADE.CIDADE, CIDADE.UF, CLIENDENT.SCLIENDENT AS PADRAO "
                + " FROM CLIENDENT "
                + " INNER JOIN CIDADE ON (CIDADE.CCIDADE = CLIENDENT.CCIDADE) "
                + " WHERE CLIENDENT.CCLIFOR = " + vs.getParameter("CCLIFOR")
                + " ) ENDEREC"
                + " ORDER BY ENDEREC.PADRAO "
        );
        end.open();

        return end.jsonData();
    }

    public String goTransaction(VariavelSessao vs) throws ExcecaoTecnicon, SAXException, IOException, Exception {
        try {
            String urlTransaction = "https://ws.sandbox.pagseguro.uol.com.br/v2/transactions/";

            TSQLDataSetEmp end = TSQLDataSetEmp.create(vs);
            if (vs.getParameter("CENDERECO").equals("0")) {
                end.commandText("SELECT NUMERO, ENDERECO, BAIRRO, COMPLEMENTO, CIDADE.CIDADE, CIDADE.UF, CEP1, 'PADRAO' AS REFER "
                        + " FROM CLIFOREND "
                        + " INNER JOIN CIDADE ON (CIDADE.CCIDADE = CLIFOREND.CCIDADE)"
                        + " WHERE CCLIFOR = " + vs.getParameter("CCFLIFOR"));
            } else {
                end.commandText("SELECT NUMERO, ENDERECO, BAIRRO, COMPLEMENTO, CIDADE.CIDADE, CIDADE.UF, CLIENDENT.CEP CEP1, CLIENDENT AS REFER "
                        + " FROM CLIENDENT "
                        + " INNER JOIN CIDADE ON (CIDADE.CCIDADE = CLIENDENT.CCIDADE)"
                        + " WHERE CCLIFOR = " + vs.getParameter("CCFLIFOR")
                        + " AND CLIENDENT.CCLIENDENT = " + vs.getParameter("CENDERECO"));
            }
            end.open();

            TClientDataSet transacoesEfetuadas = TClientDataSet.create(vs, "TRANSACOESEFETUADAS");
            transacoesEfetuadas.createDataSet();
            transacoesEfetuadas.condicao("WHERE TRANSACOESEFETUADAS.TABELAORIGEM = 'ECOMMERCE'"
                    + " AND TRANSACOESEFETUADAS.SEQORIGEM = 1");
            transacoesEfetuadas.open();

            if (!transacoesEfetuadas.isEmpty()) {
                transacoesEfetuadas.edit();
            } else {
                transacoesEfetuadas.insert();
            }

            transacoesEfetuadas.fieldByName("TABELAORIGEM").asString("ECOMMERCE");
            transacoesEfetuadas.fieldByName("SEQORIGEM").asString(vs.getParameter("SEQORIGEM"));
            transacoesEfetuadas.fieldByName("TIPOPAGAMENTO").asString(vs.getParameter("TIPOPAGAMENTO"));
            transacoesEfetuadas.fieldByName("VALORPARCELAS").asDouble(Funcoes.strToDouble(vs.getParameter("installmentValue")));
            transacoesEfetuadas.fieldByName("QTDEPARCELAS").asString(vs.getParameter("installmentQuantity"));
            transacoesEfetuadas.fieldByName("VALORTOTAL").asDouble(Funcoes.strToDouble(vs.getParameter("TOTALTRANSACAO")));
            transacoesEfetuadas.fieldByName("VALORFRETE").asDouble(Funcoes.strToDouble(vs.getParameter("shippingCost")));
            transacoesEfetuadas.fieldByName("TIPOFRETE").asString(vs.getParameter("shippingType"));
            transacoesEfetuadas.fieldByName("REFERENCIA").asString(vs.getParameter("reference"));
            transacoesEfetuadas.fieldByName("JUROSPARCELA").asDouble(0.0);

            JSONArray itens = new JSONArray(vs.getParameter("JSONITENS"));

            transacoesEfetuadas.fieldByName("QTDEITEM").asInteger(itens.length());
            transacoesEfetuadas.post();

            JSONObject joRet = new JSONObject();

            StringBuilder params = new StringBuilder();
            params.append("email=").append(URLEncoder.encode(vs.getParameter("email")));
            params.append("&token=").append(URLEncoder.encode(vs.getParameter("token")));
            params.append("&paymentMode=default");
            params.append("&paymentMethod=").append(((vs.getParameter("TIPOPAGAMENTO").equals("1") ? "creditCard" : "creditCard")));
            params.append("&currency=").append(URLEncoder.encode(vs.getParameter("currency")));

            for (int pos = 0; pos < itens.length(); pos++) {
                JSONObject jO = new JSONObject(itens.get(pos).toString());

                params.append("&itemId").append(pos + 1).append("=").append(URLEncoder.encode(jO.getString("cProduto")));
                params.append("&itemDescription").append(pos + 1).append("=").append(URLEncoder.encode(jO.getString("descricao")));
                params.append("&itemAmount").append(pos + 1).append("=").append(URLEncoder.encode(
                        Funcoes.formatFloat("#0.00", Funcoes.strToDouble(jO.getString("unitario"))).replace(".", "").replace(",", ".")));
                params.append("&itemQuantity").append(pos + 1).append("=").append(Funcoes.strToInt(jO.getString("qtde")));
                //params.append("&itemShippingCost").append(pos + 1).append("=").append();
            }

            params.append("&notificationURL=").append(URLEncoder.encode("http://portal.tecnicon.com.br:7078/peixaria/"));
            params.append("&reference=").append(end.fieldByName("REFER").asString());
            params.append("&senderName=").append(URLEncoder.encode(vs.getParameter("senderName")));
            params.append("&senderCPF=").append(URLEncoder.encode(vs.getParameter("senderCPF")));
            params.append("&senderAreaCode=").append(URLEncoder.encode(vs.getParameter("senderAreaCode")));
            params.append("&senderPhone=").append(URLEncoder.encode(vs.getParameter("senderPhone")));
            params.append("&senderEmail=").append(URLEncoder.encode(vs.getParameter("senderEmail")));
            params.append("&senderHash=").append(URLEncoder.encode(vs.getParameter("senderHash")));

            params.append("&shippingAddressStreet=").append(end.fieldByName("ENDERECO").asString());
            params.append("&shippingAddressNumber=").append(end.fieldByName("NUMERO").asString());
            params.append("&shippingAddressComplement=").append(end.fieldByName("COMPLEMENTO").asString());
            params.append("&shippingAddressDistrict=").append(end.fieldByName("BAIRRO").asString());
            params.append("&shippingAddressPostalCode=").append(end.fieldByName("CP1").asString());
            params.append("&shippingAddressCity=").append(end.fieldByName("CIDADE").asString());
            params.append("&shippingAddressState=").append(end.fieldByName("UF").asString());
            params.append("&shippingAddressCountry=").append(URLEncoder.encode(vs.getParameter("shippingAddressCountry")));

            params.append("&creditCardToken=").append(URLEncoder.encode(vs.getParameter("creditCardToken")));
            params.append("&installmentQuantity=").append(URLEncoder.encode(vs.getParameter("installmentQuantity")));
            params.append("&installmentValue=").append(URLEncoder.encode(
                    Funcoes.formatFloat("#0.00", Funcoes.strToDouble(vs.getParameter("installmentValue"))).replace(".", "").replace(",", ".")));
            params.append("&creditCardHolderName=").append(URLEncoder.encode(vs.getParameter("creditCardHolderName")));
            params.append("&creditCardHolderCPF=").append(URLEncoder.encode(vs.getParameter("creditCardHolderCPF")));
            params.append("&creditCardHolderBirthDate=").append(URLEncoder.encode(vs.getParameter("creditCardHolderBirthDate")));
            params.append("&creditCardHolderAreaCode=").append(URLEncoder.encode(vs.getParameter("creditCardHolderAreaCode")));
            params.append("&creditCardHolderPhone=").append(URLEncoder.encode(vs.getParameter("creditCardHolderPhone")));

            params.append("&billingAddressStreet=").append(URLEncoder.encode(vs.getParameter("billingAddressStreet")));
            params.append("&billingAddressNumber=").append(URLEncoder.encode(vs.getParameter("billingAddressNumber")));
            params.append("&billingAddressComplement=").append(URLEncoder.encode(vs.getParameter("billingAddressComplement")));
            params.append("&billingAddressDistrict=").append(URLEncoder.encode(vs.getParameter("billingAddressDistrict")));
            params.append("&billingAddressPostalCode=").append(URLEncoder.encode(vs.getParameter("billingAddressPostalCode")));
            params.append("&billingAddressCity=").append(URLEncoder.encode(vs.getParameter("billingAddressCity")));
            params.append("&billingAddressState=").append(URLEncoder.encode(vs.getParameter("billingAddressState")));
            params.append("&billingAddressCountry=").append(URLEncoder.encode(vs.getParameter("billingAddressCountry")));

            String xmlRet = sendPostTransacao(urlTransaction, params.toString());

            if (xmlRet.contains("erro:")) {
                return xmlRet;
            }

            Document doc = new UtilsRequest().strToDoc(xmlRet);

            if (doc.getElementsByTagName("errors") != null && doc.getElementsByTagName("error").item(0) != null) {
                String errors = doc.getElementsByTagName("code").item(0).getTextContent() + " - " + doc.getElementsByTagName("message").item(0).getTextContent();
                return "erro:" + errors;
            } else {
                String paymentLink = (doc.getElementsByTagName("paymentLink") != null && doc.getElementsByTagName("paymentLink").item(0) != null ? doc.getElementsByTagName("paymentLink").item(0).getTextContent() : "");
                String cStatus = (doc.getElementsByTagName("status") != null && doc.getElementsByTagName("status").item(0) != null ? doc.getElementsByTagName("status").item(0).getTextContent() : "");
                String xStatus = getStatusCompra(Funcoes.strToInt(cStatus));
                String codeTransaction = (doc.getElementsByTagName("code") != null && doc.getElementsByTagName("code").item(0) != null ? doc.getElementsByTagName("code").item(0).getTextContent() : "");
                String recoveryCode = (doc.getElementsByTagName("recoveryCode") != null && doc.getElementsByTagName("recoveryCode").item(0) != null ? doc.getElementsByTagName("recoveryCode").item(0).getTextContent() : "");
                String cancellationSource = (doc.getElementsByTagName("cancellationSource") != null && doc.getElementsByTagName("cancellationSource").item(0) != null ? doc.getElementsByTagName("cancellationSource").item(0).getTextContent() : "");

                transacoesEfetuadas.edit();
                transacoesEfetuadas.fieldByName("LINKPAGAMENTO").asString(paymentLink);
                transacoesEfetuadas.fieldByName("CSTATUS").asString(cStatus);
                transacoesEfetuadas.fieldByName("XSTATUS").asString(xStatus);
                transacoesEfetuadas.fieldByName("RECOVERYCODE").asString(recoveryCode);
                transacoesEfetuadas.fieldByName("CODE").asString(codeTransaction);
                transacoesEfetuadas.fieldByName("XMLRETORNO").asString(xmlRet);
                transacoesEfetuadas.post();

                joRet.put("STRANSACAO", transacoesEfetuadas.fieldByName("STRANSACAO").asString());
                joRet.put("paymentLink", paymentLink);
                joRet.put("cStatus", cStatus);
                joRet.put("xStatus", xStatus);
                joRet.put("codeTransaction", codeTransaction);
                joRet.put("recoveryCode", recoveryCode);
                joRet.put("cancellationSource", getStatusCancelamento(cancellationSource));
                return joRet.toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new ExcecaoTecnicon(vs, e.getMessage(), e);
        }
    }

    private String sendPostTransacao(String url, String parametros) throws Exception {
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setDoOutput(true);
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.setRequestProperty("charset", "utf-8");
        con.setRequestProperty("Content-Length", String.valueOf(parametros.length()));
        con.setUseCaches(false);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(parametros);
        wr.flush();
        wr.close();
        int responseCode = con.getResponseCode();
        if (responseCode == HttpsURLConnection.HTTP_BAD_REQUEST) {
            List<String> errors = new UtilsRequest().readErrosXml(con.getErrorStream());
            StringBuilder errosOK = new StringBuilder();

            for (String error : errors) {
                errosOK.append(error + "#TEC#");
            }
            if (!errosOK.toString().isEmpty()) {
                return "erro:" + errosOK.toString();
            }
        }
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    public String getStatusCancelamento(String canc) {
        switch (canc.trim()) {
            case "INTERNAL":
                return "Cancelamento efetuado pelo PagSeguro";
            case "EXTERNAL":
                return "Cancelameto efetuado pela Instituição Financeira";
        }
        return "";
    }

    public String getStatusCompra(int cStatus) {
        switch (cStatus) {
            case 1:
                return "Aguardando Pagamento";
            case 2:
                return "Em análise";
            case 3:
                return "Paga";
            case 4:
                return "Disponível";
            case 5:
                return "Em disputa";
            case 6:
                return "Devolvida";
            case 7:
                return "Cancelada";
            case 8:
                return "Debitado";
            case 9:
                return "Retenção temporária";
        }
        return "Pendente";
    }

}
