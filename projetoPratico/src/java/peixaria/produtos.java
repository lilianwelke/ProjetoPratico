package peixaria;

import br.com.tecnicon.server.context.TecniconLookup;
import br.com.tecnicon.server.dataset.TSQLDataSetEmp;
import br.com.tecnicon.server.execoes.ExcecaoMsg;
import br.com.tecnicon.server.execoes.ExcecaoTecnicon;
import br.com.tecnicon.server.sessao.VariavelSessao;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Base64;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@Stateless
@LocalBean
public class produtos {

    public JSONArray listarProdutos(VariavelSessao vs) throws ExcecaoTecnicon, Exception {
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
                + " WHERE PRODUTO.CSUBGRUPO = " + vs.getParameter("CSUBGRUPO"));
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
                + " AND PRODUTO.CGRUPO = " + vs.getParameter("CGRUPO"));
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
        produto.commandText(" SELECT PRODUTO.CPRODUTO, PRODUTO.MERCADORIA AS DESCRICAO, PRODUTO.PRECOMERCADO "
                + " FROM PRODUTO "
                + " WHERE PRODUTO.CSUBGRUPO = " + vs.getParameter("CSUBGRUPO")
                + " AND PRODUTO.DESCRICAO CONTAINING '" + vs.getParameter("DESCRICAO") + "'");
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
        produto.commandText(" SELECT PRODUTO.CPRODUTO, GRUPO.GRUPO, PRODUTO.MERCADORIA, PRODUTO.DESCRICAO, PRODUTO.PRECOMERCADO, PRODUTO.UN "
                + " FROM PRODUTO "
                + " INNER JOIN GRUPO ON (GRUPO.CGRUPO = PRODUTO.CGRUPO)"
                + " WHERE PRODUTO.CPRODUTO = " + vs.getParameter("CPRODUTO"));
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
        produtos.put("UN", produto.fieldByName("UN").asString());

        if (!retornoImg.isNull("src") && !retornoImg.getString("src").equals("")) {
            produtos.put("IMAGEM", retornoImg.getString("src"));
        } else {
            produtos.put("IMAGEM", "");
        }

        produtosArray.put(i, produtos);

        return produtosArray;
    }

}
