package peixaria;

import br.com.tecnicon.server.dataset.TSQLDataSetEmp;
import br.com.tecnicon.server.execoes.ExcecaoTecnicon;
import br.com.tecnicon.server.sessao.VariavelSessao;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import org.json.JSONArray;
import org.json.JSONObject;

@Stateless
@LocalBean
public class produtos {

    public JSONArray listarProdutos(VariavelSessao vs) throws ExcecaoTecnicon {

        TSQLDataSetEmp produto = TSQLDataSetEmp.create(vs);
        produto.createDataSet();

        JSONObject produtos = new JSONObject();
        JSONArray produtosArray = new JSONArray();
        int i = 0;

        produto.commandText("SELECT "
                + " (CASE WHEN PARAMETROS.PRODIMAGEMLOCALLINUX <> '' THEN\n"
                + "		(CASE\n"
                + "			WHEN PARAMETROS.PRODIMAGEMARQ = 'R' THEN\n"
                + "					PARAMETROS.PRODIMAGEMLOCALLINUX #TCONC# '/' #TCONC# PRODUTO.REF_FORNECE #TCONC# '.' #TCONC#\n"
                + "			(CASE\n"
                + "				WHEN PARAMETROS.EXTENSAODES = PARAMETROS.EXTENSAODES THEN\n"
                + "					PARAMETROS.EXTENSAODES\n"
                + "				ELSE\n"
                + "					'JPG'\n"
                + "			END )\n"
                + "				WHEN PARAMETROS.PRODIMAGEMARQ = 'D' THEN\n"
                + "					PARAMETROS.PRODIMAGEMLOCALLINUX #TCONC# '/' #TCONC# PRODUTO.CDESENHO #TCONC# '.' #TCONC#\n"
                + "			(CASE\n"
                + "				WHEN PARAMETROS.EXTENSAODES = PARAMETROS.EXTENSAODES THEN\n"
                + "					PARAMETROS.EXTENSAODES\n"
                + "				ELSE\n"
                + "					'JPG'\n"
                + "			END )\n"
                + "				WHEN PARAMETROS.PRODIMAGEMARQ = '1' THEN\n"
                + "					PARAMETROS.PRODIMAGEMLOCALLINUX #TCONC# '/' #TCONC# PRODUTO.REF_FORNECE #TCONC# '_' #TCONC# DESENHO.REVISAO #TCONC# '.' #TCONC#\n"
                + "			(CASE\n"
                + "				WHEN PARAMETROS.EXTENSAODES = PARAMETROS.EXTENSAODES THEN\n"
                + "					PARAMETROS.EXTENSAODES\n"
                + "				ELSE\n"
                + "					'JPG'\n"
                + "			END )\n"
                + "				WHEN PARAMETROS.PRODIMAGEMARQ = '2' THEN\n"
                + "					PARAMETROS.PRODIMAGEMLOCALLINUX #TCONC# '/' #TCONC# PRODUTO.REF_FORNECE #TCONC# '_' #TCONC#\n"
                + "	 					CAST((SELECT first 1 P_PRODREV.REVISAO FROM P_PRODREV(PRODUTO.CPRODUTO)) AS VARCHAR(10)) #TCONC# '.' #TCONC#\n"
                + "			(CASE\n"
                + "		 		WHEN PARAMETROS.EXTENSAODES = PARAMETROS.EXTENSAODES THEN\n"
                + "	 				PARAMETROS.EXTENSAODES ELSE 'JPG' END )\n"
                + "	 			ELSE PARAMETROS.PRODIMAGEMLOCALLINUX #TCONC# '/' #TCONC#\n"
                + "	 				CAST(PRODUTO.CPRODUTO AS VARCHAR(10)) #TCONC# '.' #TCONC#\n"
                + "			(CASE\n"
                + "		 		WHEN PARAMETROS.EXTENSAODES = PARAMETROS.EXTENSAODES THEN\n"
                + "	 				PARAMETROS.EXTENSAODES\n"
                + "	 		ELSE\n"
                + "	 			'JPG'\n"
                + "		 	END )\n"
                + "		 END )\n"
                + "	 ELSE\n"
                + "		(CASE\n"
                + "			WHEN PARAMETROS.PRODIMAGEMARQ = 'R' THEN\n"
                + "					PARAMETROS.PRODIMAGEMLOCAL #TCONC# '/' #TCONC# PRODUTO.REF_FORNECE #TCONC# '.' #TCONC#\n"
                + "			(CASE\n"
                + "				WHEN PARAMETROS.EXTENSAODES = PARAMETROS.EXTENSAODES THEN\n"
                + "					PARAMETROS.EXTENSAODES\n"
                + "				ELSE\n"
                + "					'JPG'\n"
                + "			END )\n"
                + "				WHEN PARAMETROS.PRODIMAGEMARQ = 'D' THEN\n"
                + "					PARAMETROS.PRODIMAGEMLOCAL #TCONC# '/' #TCONC# PRODUTO.CDESENHO #TCONC# '.' #TCONC#\n"
                + "			(CASE\n"
                + "				WHEN PARAMETROS.EXTENSAODES = PARAMETROS.EXTENSAODES THEN\n"
                + "					PARAMETROS.EXTENSAODES\n"
                + "				ELSE\n"
                + "					'JPG'\n"
                + "			END )\n"
                + "				WHEN PARAMETROS.PRODIMAGEMARQ = '1' THEN\n"
                + "					PARAMETROS.PRODIMAGEMLOCAL #TCONC# '/' #TCONC# PRODUTO.REF_FORNECE #TCONC# '_' #TCONC# DESENHO.REVISAO #TCONC# '.' #TCONC#\n"
                + "			(CASE\n"
                + "				WHEN PARAMETROS.EXTENSAODES = PARAMETROS.EXTENSAODES THEN\n"
                + "					PARAMETROS.EXTENSAODES\n"
                + "				ELSE\n"
                + "					'JPG'\n"
                + "			END )\n"
                + "				WHEN PARAMETROS.PRODIMAGEMARQ = '2' THEN\n"
                + "					PARAMETROS.PRODIMAGEMLOCAL #TCONC# '/' #TCONC# PRODUTO.REF_FORNECE #TCONC# '_' #TCONC#\n"
                + "	 					CAST((SELECT first 1 P_PRODREV.REVISAO FROM P_PRODREV(PRODUTO.CPRODUTO)) AS VARCHAR(10)) #TCONC# '.' #TCONC#\n"
                + "			(CASE\n"
                + "		 		WHEN PARAMETROS.EXTENSAODES = PARAMETROS.EXTENSAODES THEN\n"
                + "	 				PARAMETROS.EXTENSAODES ELSE 'JPG' END )\n"
                + "	 			ELSE PARAMETROS.PRODIMAGEMLOCAL #TCONC# '/' #TCONC#\n"
                + "	 				CAST(PRODUTO.CPRODUTO AS VARCHAR(10)) #TCONC# '.' #TCONC#\n"
                + "			(CASE\n"
                + "		 		WHEN PARAMETROS.EXTENSAODES = PARAMETROS.EXTENSAODES THEN\n"
                + "	 				PARAMETROS.EXTENSAODES\n"
                + "	 		ELSE\n"
                + "	 			'JPG'\n"
                + "		 	END )\n"
                + "		 END )\n"
                + "	 END) AS IMAGEM,"
                + " PRODUTO.CPRODUTO, PRODUTO.DESCRICAO, PRODUTO.PRECOMERCADO "
                + " FROM PRODUTO "
                + " LEFT JOIN DESENHO ON(DESENHO.CPRODUTO = PRODUTO.CPRODUTO)"
                + " LEFT JOIN PARAMETROS ON(PARAMETROS.CFILIAL=1)"
                + " WHERE PRODUTO.CSUBGRUPO = " + vs.getParameter("CSUBGRUPO"));
        produto.open();

        while (!produto.eof()) {
            produtos = new JSONObject();
            produtos.put("CODIGO", produto.fieldByName("CPRODUTO").asInteger());
            produtos.put("PRODUTO", produto.fieldByName("DESCRICAO").asString());
            produtos.put("PRECO", produto.fieldByName("PRECOMERCADO").asDouble());
            produtos.put("IMAGEM", produto.fieldByName("IMAGEM").asString());
            produtosArray.put(i, produtos);
            i++;
            produto.next();
        }

        return produtosArray;
    }

}
