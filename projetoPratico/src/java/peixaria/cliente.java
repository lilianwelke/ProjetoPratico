package peixaria;

import br.com.tecnicon.server.dataset.TClientDataSet;
import br.com.tecnicon.server.dataset.TSQLDataSetEmp;
import br.com.tecnicon.server.execoes.ExcecaoMsg;
import br.com.tecnicon.server.execoes.ExcecaoTecnicon;
import br.com.tecnicon.server.sessao.VariavelSessao;
import br.com.tecnicon.server.util.funcoes.Funcoes;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
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

        String usuario[] = vs.getParameter("EMAIL").split("@");

        cliFor.insert();
        cliFor.fieldByName("NOME").asString(vs.getParameter("NOME"));
        cliFor.fieldByName("TIPO").asString("C");
        cliFor.fieldByName("CFILIAL").asInteger(1);
        cliFor.post();

        loginPx.insert();
        loginPx.fieldByName("CCLIFOR").asInteger(cliFor.fieldByName("CCLIFOR").asInteger());
        loginPx.fieldByName("USUARIOPX").asString(usuario[0]);
        loginPx.fieldByName("SENHA").asString(criptografarSenha(vs.getParameter("SENHAPX")));
        loginPx.post();

        cliente.put("CCLIFOR", cliFor.fieldByName("CCLIFOR").asInteger());
        cliente.put("NOME", cliFor.fieldByName("NOME").asString());
        cliente.put("EMAIL", vs.getParameter("EMAIL"));

        return cliente;
    }

    public static String criptografarSenha(String senha) {
        MessageDigest algorithm = null;
        byte messageDigest[] = null;
        StringBuilder hexString = new StringBuilder();

        try {
            algorithm = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            ex.getMessage();
        }

        try {
            messageDigest = algorithm.digest(senha.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            ex.getMessage();
        }

        for (byte b : messageDigest) {
            hexString.append(String.format("%02X", 0xFF & b));
        }

        return hexString.toString();
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

    public int fazerLogin(VariavelSessao vs) throws ExcecaoTecnicon {
        TSQLDataSetEmp cliente = TSQLDataSetEmp.create(vs);
        cliente.commandText("SELECT CLIFOREND.EMAIL, LOGINPX.SENHA, LOGINPX.CCLIFOR FROM LOGINPX "
                + " INNER JOIN CLIFOREND ON (CLIFOREND.CCLIFOR = LOGINPX.CCLIFOR)"
                + " WHERE LOGINPX.SENHA = '" + criptografarSenha(vs.getParameter("SENHA")) + "'"
                + " AND CLIFOREND.EMAIL = '" + vs.getParameter("EMAIL") + "'");
        cliente.open();

        if (cliente.isEmpty()) {
            cliente.close();
            cliente.commandText("SELECT CLIFOREND.EMAIL, LOGINPX.SENHA FROM LOGINPX "
                    + " INNER JOIN CLIFOREND ON (CLIFOREND.CCLIFOR = LOGINPX.CCLIFOR)"
                    + " AND CLIFOREND.EMAIL = '" + vs.getParameter("EMAIL") + "'");
            cliente.open();
            if (!cliente.isEmpty()) {
                throw new ExcecaoMsg(vs, "Senha não corresponde ao usuário informado. \nTente novamente!");
            } else {
                throw new ExcecaoMsg(vs, "O usuário informado não possui cadastro. \nTente novamente ou crie uma conta!");
            }
        }

        return cliente.fieldByName("CCLIFOR").asInteger();
    }

    public String buscarDadosCli(VariavelSessao vs) throws ExcecaoTecnicon {
        TSQLDataSetEmp clienteSql = TSQLDataSetEmp.create(vs);
        clienteSql.commandText("SELECT CLIFOREND.NOMEFILIAL, CLIFOREND.EMAIL, LOGINPX.USUARIOPX, CLIFOREND.FONE, CLIFOREND.CELULAR, CLIFOREND.CGC, "
                + " CLIFOREND.COMPLEMENTO, CLIFOREND.BAIRRO, CLIFOREND.NUMERO, CLIFOREND.ENDERECO, CLIFOREND.CEP, CIDADE.CIDADE, CIDADE.UF"
                + " FROM LOGINPX"
                + " INNER JOIN CLIFOREND ON (CLIFOREND.CCLIFOR = LOGINPX.CCLIFOR)"
                + " INNER JOIN CIDADE ON (CIDADE.CCIDADE = CLIFOREND.CCIDADE)"
                + " WHERE LOGINPX.CCLIFOR = " + vs.getParameter("CCLIFOR"));
        clienteSql.open();
        return clienteSql.jsonData();
    }

    public void validarSenhaAntiga(VariavelSessao vs) throws ExcecaoTecnicon {
        TSQLDataSetEmp cliente = TSQLDataSetEmp.create(vs);
        cliente.commandText("SELECT LOGINPX.CCLIFOR, LOGINPX.SENHA FROM LOGINPX "
                + " WHERE LOGINPX.CCLIFOR = " + vs.getParameter("CCLIFOR")
                + " AND LOGINPX.SENHA = '" + criptografarSenha(vs.getParameter("SENHAPX")) + "'");
        cliente.open();

        if (cliente.isEmpty()) {
            throw new ExcecaoTecnicon(vs, "A senha informada não correponde a senha do usuário logado.\n Tente novamente!");
        }
    }

    public String salvarEditCli(VariavelSessao vs) throws ExcecaoTecnicon {
        if (!vs.getParameter("SENHAPX").isEmpty()) {
            validarSenhaAntiga(vs);
        }

        TClientDataSet cliFor = TClientDataSet.create(vs, "CLIFOR");
        cliFor.createDataSet();
        cliFor.condicao("WHERE CLIFOR.CCLIFOR = " + vs.getParameter("CCLIFOR"));
        cliFor.open();

        cliFor.edit();
        cliFor.fieldByName("NOME").asString(vs.getParameter("NOME"));
        cliFor.fieldByName("DTALT").asDate(new Date());
        cliFor.post();
        cliFor.close();

        TClientDataSet loginPx = TClientDataSet.create(vs, "LOGINPX");
        loginPx.createDataSet();
        loginPx.condicao("WHERE LOGINPX.CCLIFOR = " + vs.getParameter("CCLIFOR"));
        loginPx.open();

        loginPx.edit();
        loginPx.fieldByName("USUARIOPX").asString(vs.getParameter("USUARIOPX"));
        if (!vs.getParameter("SENHANOVA").isEmpty()) {
            loginPx.fieldByName("SENHA").asString(criptografarSenha(vs.getParameter("SENHANOVA")));
        }
        loginPx.post();
        loginPx.close();

        TClientDataSet cliForEnd = TClientDataSet.create(vs, "CLIFOREND");
        cliForEnd.createDataSet();
        cliForEnd.condicao("WHERE CLIFOREND.CCLIFOR = " + vs.getParameter("CCLIFOR"));
        cliForEnd.open();

        TSQLDataSetEmp cidade = TSQLDataSetEmp.create(vs);
        cidade.commandText("SELECT CIDADE.CCIDADE FROM CIDADE WHERE CIDADE.CIDADE = '" + vs.getParameter("NCIDADE1").toUpperCase() + "'");
        cidade.open();

        cliForEnd.edit();
        cliForEnd.fieldByName("CGC").asString(vs.getParameter("CPF"));
        cliForEnd.fieldByName("NOMEFILIAL").asString(vs.getParameter("NOME"));
        cliForEnd.fieldByName("CEP").asString(vs.getParameter("CEP"));
        cliForEnd.fieldByName("ENDERECO").asString(vs.getParameter("ENDERECO"));
        cliForEnd.fieldByName("CCIDADE").asInteger(cidade.fieldByName("CCIDADE").asInteger());
        cliForEnd.fieldByName("CCIDADE1").asInteger(cidade.fieldByName("CCIDADE").asInteger());
        cliForEnd.fieldByName("CELULAR").asString(vs.getParameter("CELULAR"));
        cliForEnd.fieldByName("FONE").asString(vs.getParameter("FONE"));
        cliForEnd.fieldByName("NUMERO").asString(vs.getParameter("NUMERO"));
        cliForEnd.fieldByName("BAIRRO").asString(vs.getParameter("BAIRRO"));
        cliForEnd.fieldByName("COMPLEMENTO").asString(vs.getParameter("COMPLEMENTO"));
        cliForEnd.fieldByName("EMAIL").asString(vs.getParameter("EMAIL"));
        cliForEnd.post();

        return "Dados atualizados com sucesso!";
    }

    public String listarMinhasCompras(VariavelSessao vs) throws ExcecaoTecnicon {
        TSQLDataSetEmp comprasSql = TSQLDataSetEmp.create(vs);
        comprasSql.commandText("SELECT PEDIDO.PEDIDO, "
                + "         CASE WHEN TDAY(PEDIDO.DATA) < 10 THEN '0' #TCONC# TDAY(PEDIDO.DATA) ELSE TDAY(PEDIDO.DATA) END #TCONC# '/' #TCONC# "
                + "         CASE WHEN TMONTH(PEDIDO.DATA) < 10 THEN '0' #TCONC# TMONTH(PEDIDO.DATA) ELSE TMONTH(PEDIDO.DATA) END #TCONC# '/' #TCONC# "
                + "         TYEAR(PEDIDO.DATA) AS DATA, "
                + "         CASE WHEN TDAY(PEDIDO.PREVDT) < 10 THEN '0' #TCONC# TDAY(PEDIDO.PREVDT) ELSE TDAY(PEDIDO.PREVDT) END #TCONC# '/' #TCONC# "
                + "         CASE WHEN TMONTH(PEDIDO.PREVDT) < 10 THEN '0' #TCONC# TMONTH(PEDIDO.PREVDT) ELSE TMONTH(PEDIDO.PREVDT) END #TCONC# '/' #TCONC# "
                + "         TYEAR(PEDIDO.PREVDT) AS PREVDT, "
                + "         SUM(PEDIDOITEM.QTDE) AS QTDE, SUM(PEDIDOITEM.TOTAL) AS TOTAL "
                + " FROM PEDIDO"
                + " INNER JOIN PEDIDOITEM ON (PEDIDOITEM.PEDIDO = PEDIDO.PEDIDO)"
                + " WHERE PEDIDO.CCLIFOR = " + vs.getParameter("CCLIFOR")
                + " GROUP BY PEDIDO.PEDIDO, PEDIDO.DATA, PEDIDO.PREVDT");
        comprasSql.open();
        return comprasSql.jsonData();
    }
}
