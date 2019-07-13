package peixaria;

import br.com.tecnicon.server.context.TecniconLookup;
import br.com.tecnicon.server.dataset.TSQLDataSetEmp;
import br.com.tecnicon.server.execoes.ExcecaoMsg;
import br.com.tecnicon.server.execoes.ExcecaoTecnicon;
import br.com.tecnicon.server.sessao.VariavelSessao;
import br.com.tecnicon.server.util.funcoes.Funcoes;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Base64;
import java.util.Date;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@Stateless
@LocalBean
public class produtos {

    public JSONArray listarOfertas(VariavelSessao vs) throws ExcecaoTecnicon, Exception {
        TSQLDataSetEmp oferta = TSQLDataSetEmp.create(vs);
        JSONObject produtos = new JSONObject();
        JSONArray produtosArray = new JSONArray();
        int i = 0;
        Object produto2 = TecniconLookup.lookup("Produto2/Produto2");
        String baseImg = "";
        JSONObject retornoImg = new JSONObject();

        oferta.close();
        oferta.commandText("SELECT TAB.CPRODUTO, TAB.DESCRICAO, (TAB.PRECOMERCADO - TAB.PRECOMERCADO * TAB.DESCONTO / 100) AS PRECOMERCADO, "
                + " TAB.DESCONTO, TAB.TEMSALDO "
                + " FROM (SELECT PRODUTO.CPRODUTO, PRODUTO.MERCADORIA AS DESCRICAO, PROMOCAOPXITENS.PERCENTUALDESCONTO DESCONTO, PRODUTO.PRECOMERCADO,"
                + " (SELECT CASE WHEN SUM(SALDOPRODUTOLOTE.SALDO) <= 0 THEN 0 ELSE 1 END AS TEMSALDO FROM SALDOPRODUTOLOTE "
                + " INNER JOIN PRODUTOLOTE ON (PRODUTOLOTE.SPRODUTOLOTE = SALDOPRODUTOLOTE.SPRODUTOLOTE) "
                + " WHERE SALDOPRODUTOLOTE.CPRODUTO = PRODUTO.CPRODUTO AND PRODUTOLOTE.ATIVO = 'S' "
                + " AND PRODUTOLOTE.VCTO >= '" + Funcoes.formatarData(new Date(), "dd.MM.yyyy") + "'"
                + " GROUP BY SALDOPRODUTOLOTE.CPRODUTO) TEMSALDO "
                + " FROM PROMOCAOPX "
                + " INNER JOIN PROMOCAOPXITENS ON (PROMOCAOPXITENS.SPROMOCAOPX = PROMOCAOPX.SPROMOCAOPX) "
                + " INNER JOIN PRODUTO ON (PRODUTO.CPRODUTO = PROMOCAOPXITENS.CPRODUTO) "
                + " WHERE PRODUTO.CSUBGRUPO = " + vs.getParameter("CSUBGRUPO")
                + " AND PRODUTO.ATIVO = 'S'"
                + " AND '" + Funcoes.formatarDB(Funcoes.dateToStr(new Date()), "D") + "' BETWEEN PROMOCAOPX.DATAI AND PROMOCAOPX.DATAF "
                + " AND PROMOCAOPX.ATIVO = 'S' "
                + " ) TAB "
                + " ORDER BY TAB.TEMSALDO DESC, TAB.DESCRICAO"
        );
        oferta.open();

        try {
            oferta.first();
            while (!oferta.eof()) {

                vs.addParametros("CPRODUTO", oferta.fieldByName("CPRODUTO").asString());
                vs.addParametros("filial", "1");
                produto2 = TecniconLookup.lookup("Produto2/Produto2");
                baseImg = (String) produto2.getClass().getMethod("buscarDadosImagem", VariavelSessao.class).invoke(produto2, vs);
                retornoImg = new JSONObject(baseImg);

                produtos = new JSONObject();
                produtos.put("CPRODUTO", oferta.fieldByName("CPRODUTO").asInteger());
                produtos.put("PRODUTO", oferta.fieldByName("DESCRICAO").asString());
                produtos.put("PRECO", oferta.fieldByName("PRECOMERCADO").asDouble());
                produtos.put("TEMSALDO", oferta.fieldByName("TEMSALDO").asDouble());
                produtos.put("DESCONTO", oferta.fieldByName("DESCONTO").asDouble());
                if (!retornoImg.isNull("src") && !retornoImg.getString("src").equals("")) {
                    produtos.put("IMAGEM", retornoImg.getString("src"));
                } else {
                    produtos.put("IMAGEM", "");
                }

                produtosArray.put(i, produtos);
                vs.removeParametro("CPRODUTO");
                i++;
                oferta.next();
            }
        } catch (Exception ex) {
            throw new ExcecaoTecnicon(vs, ex.getMessage());
        }
        return produtosArray;
    }

    public JSONArray listarProdutos(VariavelSessao vs) throws ExcecaoTecnicon, Exception {
        TSQLDataSetEmp produto = TSQLDataSetEmp.create(vs);
        JSONObject produtos = new JSONObject();
        JSONArray produtosArray = new JSONArray();
        int i = 0;
        Object produto2 = TecniconLookup.lookup("Produto2/Produto2");
        String baseImg = "";
        JSONObject retornoImg = new JSONObject();

        produto.close();
        produto.commandText("SELECT TAB.CPRODUTO, TAB.DESCRICAO, "
                + " CASE WHEN TAB.DESCONTO IS NOT NULL THEN (TAB.PRECOMERCADO - TAB.PRECOMERCADO * TAB.DESCONTO / 100)"
                + " ELSE TAB.PRECOMERCADO END AS PRECOMERCADO, TAB.DESCONTO, TAB.TEMSALDO "
                + " FROM (SELECT PRODUTO.CPRODUTO, PRODUTO.MERCADORIA AS DESCRICAO, "
                + " (SELECT PROMOCAOPXITENS.PERCENTUALDESCONTO "
                + " FROM PROMOCAOPX "
                + " INNER JOIN PROMOCAOPXITENS ON (PROMOCAOPXITENS.SPROMOCAOPX = PROMOCAOPX.SPROMOCAOPX) "
                + " WHERE '" + Funcoes.formatarDB(Funcoes.dateToStr(new Date()), "D") + "' BETWEEN PROMOCAOPX.DATAI AND PROMOCAOPX.DATAF "
                + " AND PROMOCAOPX.ATIVO = 'S'"
                + " AND PROMOCAOPXITENS.CPRODUTO = PRODUTO.CPRODUTO) DESCONTO, PRODUTO.PRECOMERCADO,"
                + " (SELECT CASE WHEN SUM(SALDOPRODUTOLOTE.SALDO) <= 0 THEN 0 ELSE 1 END AS TEMSALDO FROM SALDOPRODUTOLOTE "
                + " INNER JOIN PRODUTOLOTE ON (PRODUTOLOTE.SPRODUTOLOTE = SALDOPRODUTOLOTE.SPRODUTOLOTE) "
                + " WHERE SALDOPRODUTOLOTE.CPRODUTO = PRODUTO.CPRODUTO AND PRODUTOLOTE.ATIVO = 'S' "
                + " AND PRODUTOLOTE.VCTO >= '" + Funcoes.formatarData(new Date(), "dd.MM.yyyy") + "'"
                + " GROUP BY SALDOPRODUTOLOTE.CPRODUTO) TEMSALDO "
                + " FROM PRODUTO "
                + " WHERE PRODUTO.CSUBGRUPO = " + vs.getParameter("CSUBGRUPO")
                + " AND PRODUTO.ATIVO = 'S') TAB"
                + " ORDER BY TAB.TEMSALDO DESC, TAB.DESCRICAO");
        produto.open();

        try {
            produto.first();
            while (!produto.eof()) {

                vs.addParametros("CPRODUTO", produto.fieldByName("CPRODUTO").asString());
                vs.addParametros("filial", "1");
                produto2 = TecniconLookup.lookup("Produto2/Produto2");
                baseImg = (String) produto2.getClass().getMethod("buscarDadosImagem", VariavelSessao.class).invoke(produto2, vs);
                retornoImg = new JSONObject(baseImg);

                produtos = new JSONObject();
                produtos.put("CPRODUTO", produto.fieldByName("CPRODUTO").asInteger());
                produtos.put("PRODUTO", produto.fieldByName("DESCRICAO").asString());
                produtos.put("PRECO", produto.fieldByName("PRECOMERCADO").asDouble());
                produtos.put("TEMSALDO", produto.fieldByName("TEMSALDO").asDouble());
                produtos.put("DESCONTO", produto.fieldByName("DESCONTO").asDouble());
                if (!retornoImg.isNull("src") && !retornoImg.getString("src").equals("")) {
                    produtos.put("IMAGEM", retornoImg.getString("src"));
                } else {
                    produtos.put("IMAGEM", "");
                }

                produtosArray.put(i, produtos);
                vs.removeParametro("CPRODUTO");
                i++;
                produto.next();
            }
        } catch (Exception ex) {
            throw new ExcecaoTecnicon(vs, ex.getMessage());
        }
        return produtosArray;
    }

    public JSONArray listarCategorias(VariavelSessao vs) throws ExcecaoTecnicon {
        TSQLDataSetEmp categoria = TSQLDataSetEmp.create(vs);
        JSONObject categorias = new JSONObject();
        JSONArray categoriasArray = new JSONArray();
        int i = 0;

        categoria.close();
        categoria.commandText(" SELECT GRUPO.CGRUPO, GRUPO.GRUPO "
                + " FROM GRUPO "
                + " WHERE GRUPO.CODIGOREF = " + vs.getParameter("CODIGOREF"));
        categoria.open();

        categoria.first();
        while (!categoria.eof()) {
            categorias = new JSONObject();
            categorias.put("CGRUPO", categoria.fieldByName("CGRUPO").asInteger());
            categorias.put("GRUPO", categoria.fieldByName("GRUPO").asString());
            categoriasArray.put(i, categorias);

            i++;
            categoria.next();
        }
        return categoriasArray;
    }

    public JSONArray listarProdCategoria(VariavelSessao vs) throws ExcecaoTecnicon, Exception {
        TSQLDataSetEmp produto = TSQLDataSetEmp.create(vs);
        JSONObject produtos = new JSONObject();
        JSONArray produtosArray = new JSONArray();
        int i = 0;
        Object produto2 = TecniconLookup.lookup("Produto2/Produto2");
        String baseImg = "";
        JSONObject retornoImg = new JSONObject();

        produto.close();
        produto.commandText(" SELECT PRODUTO.CPRODUTO, PRODUTO.MERCADORIA AS DESCRICAO, PRODUTO.PRECOMERCADO "
                + " FROM PRODUTO "
                + " WHERE PRODUTO.CSUBGRUPO = " + vs.getParameter("CSUBGRUPO")
                + " AND PRODUTO.CGRUPO = " + vs.getParameter("CGRUPO")
                + " AND PRODUTO.ATIVO = 'S'"
                + " ORDER BY PRODUTO.MERCADORIA");
        produto.open();

        produto.first();
        while (!produto.eof()) {

            vs.addParametros("CPRODUTO", produto.fieldByName("CPRODUTO").asString());
            vs.addParametros("filial", "1");
            produto2 = TecniconLookup.lookup("Produto2/Produto2");
            baseImg = (String) produto2.getClass().getMethod("buscarDadosImagem", VariavelSessao.class).invoke(produto2, vs);
            retornoImg = new JSONObject(baseImg);

            produtos = new JSONObject();
            produtos.put("CPRODUTO", produto.fieldByName("CPRODUTO").asInteger());
            produtos.put("PRODUTO", produto.fieldByName("DESCRICAO").asString());
            produtos.put("PRECO", produto.fieldByName("PRECOMERCADO").asDouble());

            if (!retornoImg.isNull("src") && !retornoImg.getString("src").equals("")) {
                produtos.put("IMAGEM", retornoImg.getString("src"));
            } else {
                produtos.put("IMAGEM", "");
            }

            produtosArray.put(i, produtos);
            vs.removeParametro("CPRODUTO");
            i++;
            produto.next();
        }
        return produtosArray;
    }

    public JSONArray pesquisarProduto(VariavelSessao vs) throws ExcecaoTecnicon, Exception {
        TSQLDataSetEmp produto = TSQLDataSetEmp.create(vs);
        JSONObject produtos = new JSONObject();
        JSONArray produtosArray = new JSONArray();
        int i = 0;
        Object produto2 = TecniconLookup.lookup("Produto2/Produto2");
        String baseImg = "";
        JSONObject retornoImg = new JSONObject();

        produto.close();
        produto.commandText(" SELECT PRODUTO.CPRODUTO, PRODUTO.MERCADORIA AS DESCRICAO, "
                + " COALESCE((SELECT (PRODUTO.PRECOMERCADO - PRODUTO.PRECOMERCADO * PROMOCAOPXITENS.PERCENTUALDESCONTO / 100) PRECOMERCADO"
                + " FROM PROMOCAOPX "
                + " INNER JOIN PROMOCAOPXITENS ON (PROMOCAOPXITENS.SPROMOCAOPX = PROMOCAOPX.SPROMOCAOPX) "
                + " WHERE '" + Funcoes.formatarDB(Funcoes.dateToStr(new Date()), "D") + "' BETWEEN PROMOCAOPX.DATAI AND PROMOCAOPX.DATAF "
                + " AND PROMOCAOPX.ATIVO = 'S'"
                + " AND PROMOCAOPXITENS.CPRODUTO = PRODUTO.CPRODUTO), PRODUTO.PRECOMERCADO) PRECOMERCADO "
                + " FROM PRODUTO "
                + " WHERE PRODUTO.CSUBGRUPO = " + vs.getParameter("CSUBGRUPO")
                + " AND PRODUTO.DESCRICAO CONTAINING '" + vs.getParameter("DESCRICAO").toLowerCase() + "'"
                + " AND PRODUTO.ATIVO = 'S'"
                + " ORDER BY PRODUTO.MERCADORIA");
        produto.open();

        produto.first();
        while (!produto.eof()) {

            vs.addParametros("CPRODUTO", produto.fieldByName("CPRODUTO").asString());
            vs.addParametros("filial", "1");
            produto2 = TecniconLookup.lookup("Produto2/Produto2");
            baseImg = (String) produto2.getClass().getMethod("buscarDadosImagem", VariavelSessao.class).invoke(produto2, vs);
            retornoImg = new JSONObject(baseImg);

            produtos = new JSONObject();
            produtos.put("CPRODUTO", produto.fieldByName("CPRODUTO").asInteger());
            produtos.put("PRODUTO", produto.fieldByName("DESCRICAO").asString());
            produtos.put("PRECO", produto.fieldByName("PRECOMERCADO").asDouble());

            if (!retornoImg.isNull("src") && !retornoImg.getString("src").equals("")) {
                produtos.put("IMAGEM", retornoImg.getString("src"));
            } else {
                produtos.put("IMAGEM", "");
            }

            produtosArray.put(i, produtos);
            vs.removeParametro("CPRODUTO");
            i++;
            produto.next();
        }
        return produtosArray;
    }

    public JSONArray retornarProduto(VariavelSessao vs) throws ExcecaoTecnicon, Exception {
        TSQLDataSetEmp produto = TSQLDataSetEmp.create(vs);
        JSONObject produtos = new JSONObject();
        JSONArray produtosArray = new JSONArray();
        int i = 0;
        Object produto2 = TecniconLookup.lookup("Produto2/Produto2");
        String baseImg = "";
        JSONObject retornoImg = new JSONObject();

        produto.close();
        produto.commandText(" SELECT PRODUTO.CPRODUTO, GRUPO.GRUPO, PRODUTO.MERCADORIA, PRODUTO.DESCRICAO, "
                + " COALESCE((SELECT (PRODUTO.PRECOMERCADO - PRODUTO.PRECOMERCADO * PROMOCAOPXITENS.PERCENTUALDESCONTO / 100) PRECOMERCADO"
                + " FROM PROMOCAOPX "
                + " INNER JOIN PROMOCAOPXITENS ON (PROMOCAOPXITENS.SPROMOCAOPX = PROMOCAOPX.SPROMOCAOPX) "
                + " WHERE '" + Funcoes.formatarDB(Funcoes.dateToStr(new Date()), "D") + "' BETWEEN PROMOCAOPX.DATAI AND PROMOCAOPX.DATAF "
                + " AND PROMOCAOPX.ATIVO = 'S'"
                + " AND PROMOCAOPXITENS.CPRODUTO = PRODUTO.CPRODUTO), PRODUTO.PRECOMERCADO) PRECOMERCADO, "
                + " PRODUTO.UNIDADE,"
                + " (SELECT SUM(SALDOPRODUTOLOTE.SALDO) FROM SALDOPRODUTOLOTE "
                + " INNER JOIN PRODUTOLOTE ON (PRODUTOLOTE.SPRODUTOLOTE = SALDOPRODUTOLOTE.SPRODUTOLOTE) "
                + " WHERE SALDOPRODUTOLOTE.CPRODUTO = PRODUTO.CPRODUTO AND PRODUTOLOTE.ATIVO = 'S' "
                + " AND PRODUTOLOTE.VCTO >= '" + Funcoes.formatarData(new Date(), "dd.MM.yyyy") + "'"
                + " GROUP BY SALDOPRODUTOLOTE.CPRODUTO) SALDO "
                + " FROM PRODUTO "
                + " INNER JOIN GRUPO ON (GRUPO.CGRUPO = PRODUTO.CGRUPO)"
                + " WHERE PRODUTO.CPRODUTO = " + vs.getParameter("CPRODUTO")
                + " GROUP BY PRODUTO.CPRODUTO, GRUPO.GRUPO, PRODUTO.MERCADORIA, PRODUTO.DESCRICAO, PRODUTO.PRECOMERCADO, PRODUTO.UNIDADE");
        produto.open();

        vs.addParametros("filial", "1");
        produto2 = TecniconLookup.lookup("Produto2/Produto2");
        baseImg = (String) produto2.getClass().getMethod("buscarDadosImagem", VariavelSessao.class).invoke(produto2, vs);
        retornoImg = new JSONObject(baseImg);

        produtos = new JSONObject();
        produtos.put("CPRODUTO", produto.fieldByName("CPRODUTO").asInteger());
        produtos.put("CATEGORIA", produto.fieldByName("GRUPO").asString());
        produtos.put("MERCADORIA", produto.fieldByName("MERCADORIA").asString());
        produtos.put("PRODUTO", produto.fieldByName("DESCRICAO").asString());
        produtos.put("PRECO", produto.fieldByName("PRECOMERCADO").asDouble());
        produtos.put("UN", produto.fieldByName("UNIDADE").asString());

        if (!retornoImg.isNull("src") && !retornoImg.getString("src").equals("")) {
            produtos.put("IMAGEM", retornoImg.getString("src"));
        } else {
            produtos.put("IMAGEM", "");
        }

        produtos.put("SALDO", produto.fieldByName("SALDO").asDouble());

        produtosArray.put(i, produtos);

        return produtosArray;
    }

}
