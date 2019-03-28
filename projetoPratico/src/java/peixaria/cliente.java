package peixaria;

import br.com.tecnicon.server.dataset.TClientDataSet;
import br.com.tecnicon.server.dataset.TSQLDataSetEmp;
import br.com.tecnicon.server.execoes.ExcecaoMsg;
import br.com.tecnicon.server.execoes.ExcecaoTecnicon;
import br.com.tecnicon.server.sessao.VariavelSessao;
import br.com.tecnicon.server.util.funcoes.Funcoes;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import org.json.JSONObject;

@Stateless
@LocalBean
public class cliente {

    public JSONObject inserirCliente(VariavelSessao vs) throws ExcecaoTecnicon {
        TClientDataSet cliFor = TClientDataSet.create(vs, "CLIFOR");
        cliFor.createDataSet();

        TClientDataSet loginPx = TClientDataSet.create(vs, "LOGINPX");
        loginPx.createDataSet();

        JSONObject cliente = new JSONObject();

        cliFor.insert();
        cliFor.fieldByName("NOME").asString(vs.getParameter("NOME"));
        cliFor.fieldByName("TIPO").asString("C");
        cliFor.fieldByName("CFILIAL").asInteger(1);
        cliFor.post();

        loginPx.insert();
        loginPx.fieldByName("CCLIFOR").asInteger(cliFor.fieldByName("CCLIFOR").asInteger());
        loginPx.fieldByName("USUARIOPX").asString(cliFor.fieldByName("NOME").asString());
        loginPx.fieldByName("SENHAPX").asString(vs.getParameter("SENHAPX"));
        loginPx.post();

        cliente.put("CCLIFOR", cliFor.fieldByName("CCLIFOR").asInteger());
        cliente.put("NOME", cliFor.fieldByName("NOME").asString());
        cliente.put("EMAIL", vs.getParameter("EMAIL"));

        return cliente;
    }

    public String inserirClienteEnd(VariavelSessao vs) throws ExcecaoTecnicon {
        TClientDataSet cliForEnd = TClientDataSet.create(vs, "CLIFOREND");
        cliForEnd.createDataSet();

        TSQLDataSetEmp cidade = TSQLDataSetEmp.create(vs);
        cidade.commandText("SELECT CIDADE.CCIDADE FROM CIDADE WHERE CIDADE.CIDADE = '" + vs.getParameter("NCIDADE1").toUpperCase() + "'");
        cidade.open();

        cliForEnd.insert();
        cliForEnd.fieldByName("CCLIFOR").asInteger(Funcoes.strToInt(vs.getParameter("CCLIFOR")));
        cliForEnd.fieldByName("CGC").asString(vs.getParameter("CPF"));
        cliForEnd.fieldByName("NOMEFILIAL").asString(vs.getParameter("NOME"));
        cliForEnd.fieldByName("FILIALCF").asInteger(1);
        cliForEnd.fieldByName("CEP").asString(vs.getParameter("CEP"));
        cliForEnd.fieldByName("ENDERECO").asString(vs.getParameter("ENDERECO"));
        cliForEnd.fieldByName("CCIDADE").asInteger(cidade.fieldByName("CCIDADE").asInteger());
//        if (true)
//        {
//            throw new ExcecaoMsg(vs, cidade.commandText().toString() + "---" + vs.getParameter("NCIDADE1") + "---- "+ cidade.fieldByName("CCIDADE").asInteger() + "---" + cidade.fieldByName("CCIDADE").asInteger());
//        }
        cliForEnd.fieldByName("CCIDADE1").asInteger(cidade.fieldByName("CCIDADE").asInteger());
        cliForEnd.fieldByName("CELULAR").asString(vs.getParameter("CELULAR"));
        cliForEnd.fieldByName("FONE").asString(vs.getParameter("FONE"));
        cliForEnd.fieldByName("ATIVO").asString("S");
        cliForEnd.fieldByName("NUMERO").asString(vs.getParameter("NUMERO"));
        cliForEnd.fieldByName("BAIRRO").asString(vs.getParameter("BAIRRO"));
        cliForEnd.fieldByName("COMPLEMENTO").asString(vs.getParameter("COMPLEMENTO"));
        cliForEnd.fieldByName("EMAIL").asString(vs.getParameter("EMAIL"));
        cliForEnd.post();

        return "OK";
    }
}
