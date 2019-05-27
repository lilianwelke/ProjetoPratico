package peixaria;

import br.com.tecnicon.enviaemail.TEnviarEmail;
import br.com.tecnicon.server.context.TClassLoader;
import br.com.tecnicon.server.context.TecniconLookup;
import br.com.tecnicon.server.dataset.TClientDataSet;
import br.com.tecnicon.server.dataset.TSQLDataSetEmp;
import br.com.tecnicon.server.execoes.ExcecaoMsg;
import br.com.tecnicon.server.execoes.ExcecaoTecnicon;
import br.com.tecnicon.server.interfaces.RelatorioEsp;
import br.com.tecnicon.server.model.EmailConfig;
import br.com.tecnicon.server.sessao.VariavelSessao;
import br.com.tecnicon.server.util.funcoes.Funcoes;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
        duplicatasReceber.fieldByName("PARCELA").asString("1/1");
        duplicatasReceber.fieldByName("DUPLICATA").asString("PX" + pedido.fieldByName("PEDIDO").asInteger());
        duplicatasReceber.fieldByName("NOSSONUMERO").asString(duplicatasReceber.fieldByName("DUPLICATA").asString());
        duplicatasReceber.fieldByName("VCTO").asDate(Funcoes.addHoras(new Date(), 360));
        duplicatasReceber.fieldByName("VCTOP").asDate(Funcoes.addHoras(new Date(), 360));
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

                valorTotalNfs += nfsItem.fieldByName("UNITARIO").asDouble() * nfsItem.fieldByName("QTDE").asDouble();

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

}
