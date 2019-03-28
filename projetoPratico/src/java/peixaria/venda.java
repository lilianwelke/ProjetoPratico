package peixaria;

import br.com.tecnicon.server.dataset.TClientDataSet;
import br.com.tecnicon.server.execoes.ExcecaoTecnicon;
import br.com.tecnicon.server.sessao.VariavelSessao;
import br.com.tecnicon.server.util.funcoes.Funcoes;
import java.util.Date;

public class venda {

    public String inserirPedido(VariavelSessao vs) throws ExcecaoTecnicon {
        TClientDataSet pedido = TClientDataSet.create(vs, "PEDIDO");
        pedido.createDataSet();

        TClientDataSet itemPedido = TClientDataSet.create(vs, "PEDIDOITEM");
        pedido.createDataSet();

        pedido.insert();
        pedido.fieldByName("CCLIFOR").asInteger(Funcoes.strToInt(vs.getParameter("CCLIFOR")));
        pedido.fieldByName("FILIALCF").asInteger(1);
        pedido.fieldByName("CIOF").asInteger(510000);
        pedido.fieldByName("CCUSTO").asInteger(19);
        pedido.fieldByName("CLOCAL").asInteger(1);
        pedido.fieldByName("CCARTEIRA").asInteger(1);
        pedido.fieldByName("CPRAZO").asInteger(1);
        pedido.fieldByName("CTRANSP").asInteger(17);
        pedido.fieldByName("PREVDT").asDate(new Date());
        pedido.fieldByName("CVENDEDOR").asInteger(17);
        pedido.post();

        return "OK";
    }

}
