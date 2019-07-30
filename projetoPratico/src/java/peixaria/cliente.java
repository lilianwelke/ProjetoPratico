package peixaria;

import br.com.tecnicon.enviaemail.TEnviarEmail;
import br.com.tecnicon.server.context.TecniconLookup;
import br.com.tecnicon.server.dataset.TClientDataSet;
import br.com.tecnicon.server.dataset.TSQLDataSetEmp;
import br.com.tecnicon.server.execoes.ExcecaoMsg;
import br.com.tecnicon.server.execoes.ExcecaoTecnicon;
import br.com.tecnicon.server.model.EmailConfig;
import br.com.tecnicon.server.sessao.VariavelSessao;
import br.com.tecnicon.server.util.funcoes.Funcoes;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.json.JSONArray;
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

        TClientDataSet contatoCli = TClientDataSet.create(vs, "CONTATOCLI");
        contatoCli.createDataSet();

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

        contatoCli.insert();
        contatoCli.fieldByName("CCLIFOR").asInteger(cliForEnd.fieldByName("CCLIFOR").asInteger());
        contatoCli.fieldByName("FILIALCF").asInteger(1);
        contatoCli.fieldByName("CONTATO").asString(cliForEnd.fieldByName("NOMEFILIAL").asString());
        contatoCli.fieldByName("EMAIL").asString(cliForEnd.fieldByName("EMAIL").asString());
        contatoCli.fieldByName("VINCOMPRA").asString("F");
        contatoCli.post();

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

    public String buscarCpf(VariavelSessao vs) throws ExcecaoTecnicon {
        TSQLDataSetEmp endCliente = TSQLDataSetEmp.create(vs);
        endCliente.commandText("SELECT CLIFOREND.CGC "
                + " FROM CLIFOREND "
                + " WHERE CLIFOREND.EMAIL = '" + vs.getParameter("EMAIL") + "'");
        endCliente.open();

        return endCliente.fieldByName("CGC").asString();
    }

    public String redefinirSenha(VariavelSessao vs) throws ExcecaoTecnicon {
        try {
            TSQLDataSetEmp cliente = TSQLDataSetEmp.create(vs);
            cliente.commandText("SELECT CLIFOREND.CCLIFOR, CLIFOREND.NOMEFILIAL FROM CLIFOREND "
                    + " WHERE CLIFOREND.EMAIL = '" + vs.getParameter("EMAIL") + "'");
            cliente.open();

            if (!cliente.isEmpty()) {
                TClientDataSet loginPx = TClientDataSet.create(vs, "LOGINPX");
                loginPx.createDataSet();
                loginPx.condicao("WHERE LOGINPX.CCLIFOR = " + cliente.fieldByName("CCLIFOR").asString());
                loginPx.open();

                String senha = gerarSenhaAleatoria();

                loginPx.edit();
                loginPx.fieldByName("SENHA").asString(criptografarSenha(senha));
                loginPx.post();

                enviarEmailRedefinirSenha(vs, cliente.fieldByName("NOMEFILIAL").asString(), vs.getParameter("EMAIL"), senha);
            } else {
                throw new ExcecaoMsg(vs, "O e-mail informado não possui cadastro!");
            }

            return "Sua senha foi redefinida com sucesso!\nEnviamos um e-mail com a nova senha para você.\nVerifique a caixa de entrada do seu e-mail.";
        } catch (Exception e) {
            throw new ExcecaoMsg(vs, e.getMessage());
        }
    }

    public String gerarSenhaAleatoria() {
        int qtdeCaracteres = 8;
        String[] caracteres = {"1", "2", "4", "5", "6", "7", "8", "9",
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

        StringBuilder senha = new StringBuilder();

        for (int i = 0; i < qtdeCaracteres; i++) {
            int posicao = (int) (Math.random() * caracteres.length);
            senha.append(caracteres[posicao]);
        }

        return senha.toString();
    }

    public String buscarDadosCli(VariavelSessao vs) throws ExcecaoTecnicon {
        TSQLDataSetEmp clienteSql = TSQLDataSetEmp.create(vs);
        clienteSql.commandText("SELECT CLIFOREND.NOMEFILIAL, CLIFOREND.EMAIL, '' USUARIOPX, CLIFOREND.FONE, CLIFOREND.CELULAR, CLIFOREND.CGC, "
                + " CLIFOREND.COMPLEMENTO, CLIFOREND.BAIRRO, CLIFOREND.NUMERO, CLIFOREND.ENDERECO, CLIFOREND.CEP, CIDADE.CIDADE, CIDADE.UF"
                + " FROM CLIFOREND"
                + " INNER JOIN CIDADE ON (CIDADE.CCIDADE = CLIFOREND.CCIDADE)"
                + " WHERE CLIFOREND.CCLIFOR = " + vs.getParameter("CCLIFOR"));
        clienteSql.open();

        return clienteSql.jsonData();
    }

    public String buscarEndereco(VariavelSessao vs) throws ExcecaoTecnicon {
        TSQLDataSetEmp endCliente = TSQLDataSetEmp.create(vs);
        endCliente.commandText("SELECT CLIENDENT.COMPLEMENTO, CLIENDENT.BAIRRO, CLIENDENT.NUMERO, CLIENDENT.ENDERECO, CLIENDENT.CEP, CIDADE.CIDADE, "
                + " CIDADE.UF, CLIENDENT.SCLIENDENT, CLIENDENT.CLIENDENT "
                + " FROM CLIENDENT "
                + " INNER JOIN CIDADE ON (CIDADE.CCIDADE = CLIENDENT.CCIDADE)"
                + " WHERE CLIENDENT.SCLIENDENT = " + vs.getParameter("SCLIENDENT")
                + " ORDER BY CLIENDENT.SCLIENDENT ");
        endCliente.open();

        return endCliente.jsonData();
    }

    public String buscarEnderecoCli(VariavelSessao vs) throws ExcecaoTecnicon {
        TSQLDataSetEmp endCliente = TSQLDataSetEmp.create(vs);
        endCliente.commandText("SELECT CLIENDENT.SCLIENDENT, CLIENDENT.CLIENDENT "
                + " FROM CLIENDENT "
                + " WHERE CLIENDENT.CCLIFOR = " + vs.getParameter("CCLIFOR")
                + " ORDER BY CLIENDENT.CLIENDENT ");
        endCliente.open();

        return endCliente.jsonData();
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

        TClientDataSet contatoCli = TClientDataSet.create(vs, "CONTATOCLI");
        contatoCli.createDataSet();
        contatoCli.condicao("WHERE CONTATOCLI.CCLIFOR = " + vs.getParameter("CCLIFOR"));
        contatoCli.open();

        contatoCli.edit();
        contatoCli.fieldByName("CONTATO").asString(vs.getParameter("NOME"));
        contatoCli.fieldByName("EMAIL").asString(vs.getParameter("EMAIL"));
        contatoCli.post();

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
                + "         CASE WHEN (SELECT FIRST 1 RECEBER.SRECEBER FROM RECEBER "
                + "                     INNER JOIN BXRECEBER ON (BXRECEBER.SRECEBER = RECEBER.SRECEBER) "
                + "                     WHERE DUPLICATA = 'PX' #TCONC# PEDIDO.PEDIDO) IS NOT NULL THEN 'Confirmado' "
                + "         ELSE 'Pendente' END AS STATUS,"
                + "         SUM(PEDIDOITEM.QTDE) AS QTDE, SUM(PEDIDOITEM.TOTAL) + COALESCE(PEDIDO.FRETE, 0) AS TOTAL "
                + " FROM PEDIDO"
                + " INNER JOIN PEDIDOITEM ON (PEDIDOITEM.PEDIDO = PEDIDO.PEDIDO)"
                + " WHERE PEDIDO.CCLIFOR = " + vs.getParameter("CCLIFOR")
                + " GROUP BY PEDIDO.PEDIDO, PEDIDO.DATA, PEDIDO.PREVDT, PEDIDO.FRETE");
        comprasSql.open();
        return comprasSql.jsonData();
    }

    public JSONArray listarItensCompra(VariavelSessao vs) throws ExcecaoTecnicon, Exception {
        TSQLDataSetEmp item = TSQLDataSetEmp.create(vs);
        JSONObject itens = new JSONObject();
        JSONArray itensArray = new JSONArray();
        int i = 0;
        Object produtoImg = TecniconLookup.lookup("Produto2/Produto2");
        String baseImg = "";
        JSONObject retornoImg = new JSONObject();

        item.close();
        item.commandText(" SELECT PEDIDOITEM.CPRODUTO, GRUPO.GRUPO, PRODUTO.MERCADORIA, PRODUTO.DESCRICAO, PEDIDOITEM.UNITARIOCLI, PEDIDO.FRETE, "
                + " PEDIDOITEM.QTDE, PRODUTO.UNIDADE, PEDIDOITEM.UNITARIOCLI * PEDIDOITEM.QTDE AS TOTALITEM, PEDIDO.FRETE, PEDIDO.PEDIDO, "
                + " (SELECT NFSITEM.NFS FROM NFSITEM WHERE NFSITEM.PEDIDOITEM = PEDIDOITEM.PEDIDOITEM) NFS"
                + " FROM PEDIDO"
                + " INNER JOIN PEDIDOITEM ON (PEDIDOITEM.PEDIDO = PEDIDO.PEDIDO) "
                + " INNER JOIN PRODUTO ON (PRODUTO.CPRODUTO = PEDIDOITEM.CPRODUTO) "
                + " INNER JOIN GRUPO ON (GRUPO.CGRUPO = PRODUTO.CGRUPO) "
                + " WHERE PEDIDO.PEDIDO = " + vs.getParameter("PEDIDO"));
        item.open();
        try {
            item.first();
            while (!item.eof()) {
                vs.addParametros("CPRODUTO", item.fieldByName("CPRODUTO").asString());
                vs.addParametros("filial", "1");
                produtoImg = TecniconLookup.lookup("Produto2/Produto2");
                baseImg = (String) produtoImg.getClass().getMethod("buscarDadosImagem", VariavelSessao.class).invoke(produtoImg, vs);
                retornoImg = new JSONObject(baseImg);

                itens = new JSONObject();
                itens.put("PEDIDO", item.fieldByName("PEDIDO").asInteger());
                itens.put("CPRODUTO", item.fieldByName("CPRODUTO").asInteger());
                itens.put("CATEGORIA", item.fieldByName("GRUPO").asString());
                itens.put("DESCRICAO", item.fieldByName("DESCRICAO").asString());
                itens.put("PRODUTO", item.fieldByName("DESCRICAO").asString());
                itens.put("PRECO", item.fieldByName("UNITARIOCLI").asDouble());
                itens.put("QTDE", item.fieldByName("QTDE").asDouble());
                itens.put("UNIDADE", item.fieldByName("UNIDADE").asString());
                itens.put("TOTALITEM", item.fieldByName("TOTALITEM").asDouble());
                itens.put("FRETE", item.fieldByName("FRETE").asDouble());
                itens.put("NFS", item.fieldByName("NFS").asDouble());

                if (!retornoImg.isNull("src") && !retornoImg.getString("src").equals("")) {
                    itens.put("IMAGEM", retornoImg.getString("src"));
                } else {
                    itens.put("IMAGEM", "");
                }

                itensArray.put(i, itens);
                vs.removeParametro("CPRODUTO");
                i++;
                item.next();
            }
        } catch (Exception e) {
            throw new ExcecaoMsg(e.getMessage());
        }
        return itensArray;
    }

    public String terminarRegistroCliente(VariavelSessao vs) throws ExcecaoTecnicon {
        TClientDataSet contatoCli = TClientDataSet.create(vs, "CONTATOCLI");
        contatoCli.createDataSet();

        TSQLDataSetEmp cliente = TSQLDataSetEmp.create(vs);

        try {
            cliente.commandText("SELECT CLIFOREND.CCLIFOR FROM CLIFOREND WHERE CLIFOREND.EMAIL = '" + vs.getParameter("EMAIL") + "'");
            cliente.open();

            contatoCli.insert();
            contatoCli.fieldByName("CCLIFOR").asInteger(cliente.fieldByName("CCLIFOR").asInteger());
            contatoCli.fieldByName("FILIALCF").asInteger(1);
            contatoCli.fieldByName("CONTATO").asString(vs.getParameter("NOME"));
            contatoCli.fieldByName("EMAIL").asString(vs.getParameter("EMAIL"));
            contatoCli.fieldByName("VINCOMPRA").asString("F");
            contatoCli.post();
        } catch (Exception e) {
            throw new ExcecaoMsg(e.getMessage());
        }
        
        return "OK";
    }

    public String enviarEmailConfirmacao(VariavelSessao vs) throws ExcecaoTecnicon {
        try {
            TClientDataSet usuarioEmail = TClientDataSet.create(vs, "USUARIOEMAIL");
            usuarioEmail.createDataSet();
            usuarioEmail.condicao("WHERE USUARIOEMAIL.CUSUARIO=25");
            usuarioEmail.open();

            EmailConfig config = new EmailConfig(usuarioEmail.fieldByName("USUARIO").asString(), usuarioEmail.fieldByName("SENHA").asString(),
                    usuarioEmail.fieldByName("HOSTSMTP").asString(), usuarioEmail.fieldByName("EMAIL").asString(),
                    usuarioEmail.fieldByName("NOME").asString(), "", "", usuarioEmail.fieldByName("PORTSMTP").asInteger(),
                    usuarioEmail.fieldByName("SSL").asString(), 17, 0);

            String content = "Olá " + vs.getParameter("NOME") + "! "
                    + " <br/>Agradecemos seu cadastro na Píer! "
                    + " <br/>Estamos aqui para atendê-lo com os melhores peixes e o melhor, tudo online!"
                    + " <br/>Você pode acessar o nosso site pelo link: http://portal.tecnicon.com.br:7078/peixaria/"
                    + " <br/><br/><br/><br/>"
                    + " <img src=\"http://portal.tecnicon.com.br:7078/peixaria/img/pierEmail.png\"/>";

            Map<String, byte[]> anexos = new HashMap<>();
            Map<String, String> inlineImages = new HashMap<>();

            TEnviarEmail email = new TEnviarEmail();
            email.enviarEmail(vs.getParameter("DESTINATARIO"),
                    "",
                    "",
                    "Bem-vindo à Píer!",
                    content,
                    config,
                    anexos,
                    false,
                    inlineImages);

            return "Seu cadastro foi concluído com sucesso!\nEnviamos um e-mail de confirmação para você.\nVerifique a caixa de entrada do seu e-mail.";
        } catch (Exception e) {
            throw new ExcecaoMsg(vs, e.getMessage());
        }
    }

    public void enviarEmailRedefinirSenha(VariavelSessao vs, String nome, String destinatario, String senha) throws ExcecaoTecnicon {
        try {
            TClientDataSet usuarioEmail = TClientDataSet.create(vs, "USUARIOEMAIL");
            usuarioEmail.createDataSet();
            usuarioEmail.condicao("WHERE USUARIOEMAIL.CUSUARIO=25");
            usuarioEmail.open();

            EmailConfig config = new EmailConfig(usuarioEmail.fieldByName("USUARIO").asString(), usuarioEmail.fieldByName("SENHA").asString(),
                    usuarioEmail.fieldByName("HOSTSMTP").asString(), usuarioEmail.fieldByName("EMAIL").asString(),
                    usuarioEmail.fieldByName("NOME").asString(), "", "", usuarioEmail.fieldByName("PORTSMTP").asInteger(),
                    usuarioEmail.fieldByName("SSL").asString(), 17, 0);

            String content = "Olá " + nome + "! "
                    + " <br/>Sua senha foi redefinida para: " + senha
                    + " <br/>"
                    + " <br/>Você pode acessar o nosso site pelo link: http://portal.tecnicon.com.br:7078/peixaria/"
                    + " <br/><br/><br/><br/>"
                    + " <img src=\"http://portal.tecnicon.com.br:7078/peixaria/img/pierEmail.png\"/>";

            Map<String, byte[]> anexos = new HashMap<>();
            Map<String, String> inlineImages = new HashMap<>();

            TEnviarEmail email = new TEnviarEmail();
            email.enviarEmail(destinatario,
                    "",
                    "",
                    "Redefinição de senha",
                    content,
                    config,
                    anexos,
                    false,
                    inlineImages);
        } catch (Exception e) {
            throw new ExcecaoMsg(vs, e.getMessage());
        }
    }

    public String enviarMensagemCliente(VariavelSessao vs) throws ExcecaoTecnicon {
        TClientDataSet sac = TClientDataSet.create(vs, "SAC");
        sac.createDataSet();

        try {

            sac.insert();

            sac.fieldByName("HRABERTURA").asTime(new Date());
            sac.fieldByName("DTABERTURA").asDate(new Date());
            sac.fieldByName("CSACFORMACONTATO").asInteger(1);
            sac.fieldByName("CSACTIPO").asInteger(1);
            sac.fieldByName("CSACCATCONTATO").asInteger(2);
            sac.fieldByName("STATUS").asString("P");
            sac.fieldByName("NOME").asString(vs.getParameter("NOME"));
            sac.fieldByName("EMAIL").asString(vs.getParameter("EMAIL"));
            sac.fieldByName("PROBLEMA").asString(vs.getParameter("MENSAGEM"));

            sac.post();

        } catch (Exception e) {
            throw new ExcecaoMsg(vs, e.getMessage());
        }
        return "Sua mensagem foi enviada com sucesso!";
    }

    public String salvarEndereco(VariavelSessao vs) throws ExcecaoTecnicon {
        TClientDataSet endereco = TClientDataSet.create(vs, "CLIENDENT");
        endereco.createDataSet();

        TSQLDataSetEmp cidade = TSQLDataSetEmp.create(vs);
        cidade.commandText("SELECT CIDADE.CCIDADE FROM CIDADE WHERE CIDADE.CIDADE = '" + vs.getParameter("NCIDADE1").toUpperCase() + "'");
        cidade.open();

        endereco.insert();
        endereco.fieldByName("CCLIFOR").asString(vs.getParameter("CCLIFOR"));
        endereco.fieldByName("FILIALCF").asString("1");
        endereco.fieldByName("ATIVO").asString("S");
        endereco.fieldByName("CEP").asString(vs.getParameter("CEP"));
        endereco.fieldByName("ENDERECO").asString(vs.getParameter("ENDERECO"));
        endereco.fieldByName("CCIDADE").asInteger(cidade.fieldByName("CCIDADE").asInteger());
        endereco.fieldByName("NUMERO").asString(vs.getParameter("NUMERO"));
        endereco.fieldByName("BAIRRO").asString(vs.getParameter("BAIRRO"));
        endereco.fieldByName("COMPLEMENTO").asString(vs.getParameter("COMPLEMENTO"));
        endereco.fieldByName("CLIENDENT").asString(vs.getParameter("APELIDO"));
        endereco.post();

        return "Novo endereço cadastrado com sucesso!";
    }

    public String salvarEditEnd(VariavelSessao vs) throws ExcecaoTecnicon {
        TClientDataSet endereco = TClientDataSet.create(vs, "CLIENDENT");
        endereco.createDataSet();
        endereco.condicao("WHERE CLIENDENT.SCLIENDENT = " + vs.getParameter("SCLIENDENT"));
        endereco.open();

        TSQLDataSetEmp cidade = TSQLDataSetEmp.create(vs);
        cidade.commandText("SELECT CIDADE.CCIDADE FROM CIDADE WHERE CIDADE.CIDADE = '" + vs.getParameter("NCIDADE1").toUpperCase() + "'");
        cidade.open();

        endereco.edit();
        endereco.fieldByName("CEP").asString(vs.getParameter("CEP"));
        endereco.fieldByName("ENDERECO").asString(vs.getParameter("ENDERECO"));
        endereco.fieldByName("CCIDADE").asInteger(cidade.fieldByName("CCIDADE").asInteger());
        endereco.fieldByName("NUMERO").asString(vs.getParameter("NUMERO"));
        endereco.fieldByName("BAIRRO").asString(vs.getParameter("BAIRRO"));
        endereco.fieldByName("COMPLEMENTO").asString(vs.getParameter("COMPLEMENTO"));
        endereco.fieldByName("CLIENDENT").asString(vs.getParameter("APELIDO"));
        endereco.post();

        return "Dados atualizados com sucesso!";
    }

//    public String enviarMensagemCliente(VariavelSessao vs) throws ExcecaoTecnicon, AddressException {
//        Properties props = new Properties();
//        try {
//            props.put("mail.smtp.host", "smtp.gmail.com");
//            props.put("mail.smtp.socketFactory.port", "465");
//            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//            props.put("mail.smtp.auth", "true");
//            props.put("mail.smtp.port", "465");
//
//            Session session = Session.getInstance(props,
//                    new javax.mail.Authenticator() {
//                protected PasswordAuthentication getPasswordAuthentication() {
//                    return new PasswordAuthentication("pier95.contato@gmail.com", "pier123!");
//                }
//            });
//
//            session.setDebug(true);
//
//            Message message = new MimeMessage(session);
//            message.setFrom(new InternetAddress("pier95.contato@gmail.com", "Píer 95 Contato"));
//
//            message.setReplyTo(InternetAddress.parse(vs.getParameter("EMAIL")));
//
//            Address[] destinatario = InternetAddress.parse("lw005973@cfjl.com.br");
//
//            message.setRecipients(Message.RecipientType.TO, destinatario);
//            message.setSubject("E-mail recebido pelo site Píer 95 - Peixaria Online");//Assunto
//            message.setText("Este e-mail foi enviado por " + vs.getParameter("NOME") + " na página \"conheça a gente\" do site Píer 95 - Peixaria Online."
//                    + " \nMensagem: \"" + vs.getParameter("MENSAGEM") + "\"");
//            Transport.send(message);
//
//        } catch (Exception e) {
//            throw new ExcecaoMsg(vs, e.getMessage());
//        }
//        return "Sua mensagem foi enviada com sucesso!";
//    }
}
