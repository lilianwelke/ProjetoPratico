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
        
        JSONArray itens = new JSONArray(vs.getParameter("ITENS"));

        double valor = 0d;

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
            pedidoItem.post();
            valor += Funcoes.multiplDouble(pedidoItem.fieldByName("UNITARIOCLI").asDouble(), pedidoItem.fieldByName("QTDE").asDouble(), 3, "");
        }

        duplicatasReceber.insert();
        duplicatasReceber.fieldByName("CFILIAL").asInteger(1);
        duplicatasReceber.fieldByName("DATA").asDate(new Date());
        duplicatasReceber.fieldByName("CCLIFOR").asInteger(Funcoes.strToInt(vs.getParameter("CCLIFOR")));
        duplicatasReceber.fieldByName("FILIALCF").asInteger(1);
        duplicatasReceber.fieldByName("CCARTEIRA").asInteger(1);
        duplicatasReceber.fieldByName("DUPLICATA").asString("P" + pedido.fieldByName("PEDIDO").asInteger());
        duplicatasReceber.fieldByName("VCTO").asDate(new Date());
        duplicatasReceber.fieldByName("VCTOP").asDate(new Date());
        duplicatasReceber.fieldByName("VALOR").asDouble(valor);
        duplicatasReceber.post();

        return "OK";
    }

}
