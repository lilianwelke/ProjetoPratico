package peixaria;

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
        pedido.fieldByName("CIOF").asInteger(510000);
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
        pedido.post();

        for (int i = 0; i < itens.length(); i++) {
            pedidoItem.insert();
            pedidoItem.fieldByName("PEDIDO").asInteger(pedido.fieldByName("PEDIDO").asInteger());
            pedidoItem.fieldByName("CIOF").asInteger(510000);
            pedidoItem.fieldByName("CCUSTO").asInteger(19);
            pedidoItem.fieldByName("CLOCAL").asInteger(1);
            pedidoItem.fieldByName("PREVDT").asDate(new Date());
            pedidoItem.fieldByName("CTBPRECO").asInteger(1);
            pedidoItem.fieldByName("CPRODUTO").asInteger(itens.getJSONObject(i).getInt("CPRODUTO"));
            pedidoItem.fieldByName("UNITARIOCLI").asDouble(itens.getJSONObject(i).getDouble("PRECO"));
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
        duplicatasReceber.fieldByName("DUPLICATA").asString("P" + pedido.fieldByName("PEDIDO").asInteger());
        duplicatasReceber.fieldByName("VCTO").asDate(Funcoes.addHoras(new Date(), 360));
        duplicatasReceber.fieldByName("VCTOP").asDate(Funcoes.addHoras(new Date(), 360));
        duplicatasReceber.fieldByName("VALOR").asDouble(valor);
        duplicatasReceber.post();

        return "OK";
    }

    public String inserirNfs(VariavelSessao vs) throws ExcecaoTecnicon {
        TClientDataSet pedido = TClientDataSet.create(vs, "PEDIDO");
        pedido.createDataSet();
        return "OK";
    }

    public JSONObject verificaCidadeFrete(VariavelSessao vs) throws ExcecaoTecnicon {
        JSONObject retorno = new JSONObject();
        
        TSQLDataSetEmp cidadeFrete = TSQLDataSetEmp.create(vs);
        cidadeFrete.commandText("SELECT CIDADESFRETEPX.CCIDADE "
                + " FROM CIDADESFRETEPX "
                + " INNER JOIN CIDADE ON (CIDADE.CCIDADE = CIDADESFRETEPX.CCIDADE) "
                + " WHERE CIDADE.CIDADE = '" + vs.getParameter("CIDADE").toUpperCase() + "'");
        cidadeFrete.open();
        
        if (cidadeFrete.isEmpty()) {
            return retorno.put("N", "Nenhuma forma de envio disponível para esta cidade.");
        } else {
            return retorno.put("S", cidadeFrete.fieldByName("CCIDADE").asString());
        }
    }
}
