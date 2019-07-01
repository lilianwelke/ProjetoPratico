package peixaria;

import br.com.tecnicon.control.TTranslate;
import br.com.tecnicon.notificador.TConfirm;
import br.com.tecnicon.server.context.TClassLoader;
import br.com.tecnicon.server.context.TecniconLookup;
import br.com.tecnicon.server.dataset.TClientDataSet;
import br.com.tecnicon.server.dataset.TSQLDataSetEmp;
import br.com.tecnicon.server.dataset.TSQLDataSetTec;
import br.com.tecnicon.server.execoes.ExcecaoMsg;
import br.com.tecnicon.server.execoes.ExcecaoTecnicon;
import br.com.tecnicon.server.sessao.VariavelSessao;
import static br.com.tecnicon.server.util.funcoes.Funcoes.trim;
import br.com.tecnicon.utils.file.FileUtils;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.net.util.Base64;

@Stateless
@LocalBean
public class uploadImagem {

    public String Enviar(VariavelSessao vs) throws ExcecaoTecnicon, IOException, NoSuchMethodException, FileUploadException {
        if (vs.getParameter("CODEMAIL") != null && !"".equals(vs.getParameter("CODEMAIL")) && vs.getParameter("BASE64FILE") == null) {
            File destino = new File(FileUtils.getDirTmpGF(vs), vs.getParameter("CODEMAIL"));
            if (!destino.exists()) {
                destino.mkdir();
            }

            HttpServletRequest request = (HttpServletRequest) vs.getObject("request");
            DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
            fileItemFactory.setSizeThreshold(10 * 1024 * 1024); //10 MB

            ServletFileUpload uploadHandler = new ServletFileUpload(fileItemFactory);
            List items;
            try {
                items = uploadHandler.parseRequest(request);
                Iterator itr = items.iterator();

                while (itr.hasNext()) {
                    File arq = new File(destino, vs.getParameter("TITULO"));
                    OutputStream arquivo = new FileOutputStream(arq);
                    try {
                        FileItem item = (FileItem) itr.next();
                        InputStream tmp = item.getInputStream();
                        int read = 0;
                        byte[] bytes = new byte[1024];

                        while ((read = tmp.read(bytes)) != -1) {
                            arquivo.write(bytes, 0, read);
                        }

                    } finally {
                        arquivo.close();
                    }
                }

                String retorno = "", nm;
                boolean caminhoCompleto = (vs.getParameter("CAMINHOCOMPLT") != null && vs.getParameter("CAMINHOCOMPLT").equals("S"));
                for (File f : destino.listFiles()) {
                    nm = f.getName();
                    if (caminhoCompleto) {
                        nm = f.getAbsolutePath();
                    }
                    retorno += (retorno.isEmpty() ? "" : ";") + nm;
                }
                return retorno;
            } catch (FileUploadException ex) {
                return ex.getMessage();
            }
        } else if (vs.getParameter("UPLOADMANUAL") != null && vs.getParameter("UPLOADMANUAL").equals("S")) {

            File destino = new File((String) TClassLoader.execMethod("TecniconUtilsEJB/FileUtil", "retornaDirArquivo", vs));
            if (!destino.exists()) {
                destino.mkdir();
            }

            HttpServletRequest request = (HttpServletRequest) vs.getObject("request");
            DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
            fileItemFactory.setSizeThreshold(10 * 1024 * 1024); //10 MB

            ServletFileUpload uploadHandler = new ServletFileUpload(fileItemFactory);
            List items;
            try {
                items = uploadHandler.parseRequest(request);
                Iterator itr = items.iterator();

                if (vs.getParameter("VINCDOC") != null) {
                    if (items.size() > 0) {
                        registraVincDoc(vs);
                    } else {
                        throw new ExcecaoTecnicon(vs, "Você deve adicionar arquivos para enviar");
                    }
                }

                while (itr.hasNext()) {
                    File arq = new File(destino, vs.getParameter("TITULO"));
                    OutputStream arquivo = new FileOutputStream(arq);
                    FileItem item = (FileItem) itr.next();
                    InputStream tmp = null;

                    try {
                        tmp = item.getInputStream();
                        if ("true".equals(vs.getParameter("reduzImagemPX"))) {
                            TSQLDataSetEmp parametros = TSQLDataSetEmp.create(vs);
                            parametros.commandText("SELECT PRODIMAGEMLOCAL FROM PARAMETROS WHERE PARAMETROS.CFILIAL=1");
                            parametros.open();

                            String file = trim(parametros.fieldByName("PRODIMAGEMLOCAL").asString()) + vs.getParameter("TITULO");

                            File caminho = new File(file);

                            if (caminho.exists() && caminho.isFile()) {
                                try {
                                    byte[] buffer = new byte[tmp.available()];
                                    tmp.read(buffer);                                   
                                    BufferedImage img = ImageIO.read(new ByteArrayInputStream(buffer));//ImageIO.read(caminho);
                                    int ow = img.getWidth();
                                    int oh = img.getHeight();

                                    if (ow > 0 && oh > 0) {
                                        int fh = 400;
                                        int fw = (int) (ow / (oh / 400d));
                                        BufferedImage novaImagem = new BufferedImage(fw, fh, img.getType());
                                        Graphics2D g2d = novaImagem.createGraphics();
                                        g2d.drawImage(img, 0, 0, fw, fh, null);
                                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                                        g2d.dispose();
                                        RenderedImage rendImage = novaImagem;
                                        ImageIO.write(rendImage, "JPG", arquivo);// caminho);
                                    }
                                } catch (Exception ex) {
                                    throw new ExcecaoTecnicon(vs, ex.getMessage());
                                }
                            }
                        } else if (!"true".equals(vs.getParameter("reduzImagem"))) {
                            int read = 0;
                            byte[] bytes = new byte[1024];

                            while ((read = tmp.read(bytes)) != -1) {
                                arquivo.write(bytes, 0, read);
                            }

                        } else {

                            byte[] buffer = new byte[tmp.available()];
                            tmp.read(buffer);

                            BufferedImage lido = ImageIO.read(new ByteArrayInputStream(buffer));
                            BufferedImage imgSmall = FileUtils.resizeImage(lido, 128, 128);

                            ImageIO.write(imgSmall, "JPG", arquivo);
                            imgSmall.flush();

                        }

                    } catch (Exception ex) {
                        throw new ExcecaoTecnicon(vs, ex.getMessage());
                    } finally {
                        arquivo.close();

                        if (tmp != null) {
                            tmp.close();
                        }
                    }
                }

                return destino.getAbsolutePath();
            } catch (FileUploadException ex) {
                return ex.getMessage();
            }
        } else if (vs.getParameter("BASE64FILE") != null) {
            File destino = new File(FileUtils.getDirTmpGF(vs), vs.getParameter("CODEMAIL"));
            if (!destino.exists()) {
                destino.mkdir();
            }

            File arq = new File(destino, vs.getParameter("TITULO"));

            byte[] data = Base64.decodeBase64(vs.getParameter("arquivo").split(",", 2)[1]);
            try (OutputStream stream = new FileOutputStream(arq)) {
                stream.write(data);
            }

            String retorno = "", nm;
            boolean caminhoCompleto = (vs.getParameter("CAMINHOCOMPLT") != null && vs.getParameter("CAMINHOCOMPLT").equals("S"));
            for (File f : destino.listFiles()) {
                nm = f.getName();
                if (caminhoCompleto) {
                    nm = f.getAbsolutePath();
                }
                retorno += (retorno.isEmpty() ? "" : ";") + nm;
            }
            return retorno;
        } else {
            String codgrupo = "";
            String codUsuario = vs.getValor("cusuario") != null && !vs.getValor("cusuario").isEmpty() ? vs.getValor("cusuario") : vs.getParameter("cusuario");
            if (codUsuario == null || codUsuario.isEmpty()) {
                throw new ExcecaoTecnicon(vs, "Código do usuario não informado!");
            }
            TSQLDataSetTec sqlUser = TSQLDataSetTec.create(vs);
            sqlUser.commandText("SELECT USUARIOGRUPO.CGRUSUARIO FROM USUARIOGRUPO WHERE USUARIOGRUPO.CUSUARIO = " + codUsuario);
            sqlUser.open();
            sqlUser.first();
            while (!sqlUser.eof()) {
                codgrupo = codgrupo + "," + sqlUser.fieldByName("CGRUSUARIO").asString();
                sqlUser.next();
            }
            codgrupo = codgrupo.replaceFirst(",", "");
            TSQLDataSetEmp sqlValida = TSQLDataSetEmp.create(vs);

            sqlValida.close();
            sqlValida.commandText(" SELECT TPDOCUSUARIO.EMPRESA"
                    + " FROM TPDOCUSUARIO"
                    + " WHERE TPDOCUSUARIO.CTPDOC = " + vs.getParameter("TIPODOC")
                    + " AND (TPDOCUSUARIO.CUSUARIO = " + codUsuario
                    + " OR TPDOCUSUARIO.GRUSUARIO IN (" + codgrupo + "))");
            sqlValida.open();
            TSQLDataSetEmp sqlEmpDoc = TSQLDataSetEmp.create(vs);
            sqlEmpDoc.commandText("SELECT COALESCE(TPDOCUSUARIO.EMPRESA, 'N') AS EMPRESA"
                    + " FROM TPDOCUSUARIO"
                    + " WHERE TPDOCUSUARIO.CTPDOC = " + vs.getParameter("TIPODOC"));
            sqlEmpDoc.open();
            if (sqlValida.isEmpty() && ("N".equals(sqlEmpDoc.fieldByName("EMPRESA").asString().toUpperCase().trim()) || sqlEmpDoc.isEmpty())) {
                throw new ExcecaoTecnicon(vs, TTranslate.translate(vs, "Você não tem permissão de acesso a este folder!"));
            }
            sqlValida.close();

            String val = "";
            if (vs.getParameter("TIPODOC") == null || vs.getParameter("TIPODOC").equals("")) {
                throw new ExcecaoTecnicon(vs, TTranslate.translate(vs, "Tipo do documento não informado"));
            }

            if ((vs.getParameter("TITULO") == null || vs.getParameter("TITULO").equals("")) && (vs.getParameter("CDOC") == null || vs.getParameter("CDOC").equals(""))) {
                throw new ExcecaoTecnicon(vs, TTranslate.translate(vs, "Titulo do documento não informado"));
            }

            String titulo = replaceCharSpecial(replaceSpecial(vs.getParameter("TITULO")));

            TClientDataSet tipodoc = TClientDataSet.create(vs, "TIPODOC");
            tipodoc.createDataSet();
            tipodoc.condicao("WHERE TIPODOC.CTPDOC = " + vs.getParameter("TIPODOC"));
            tipodoc.open();

            TClientDataSet formatodoc = TClientDataSet.create(vs, "FORMATODOC");
            formatodoc.createDataSet();
            formatodoc.condicao("WHERE FORMATODOC.EXTENSAO = '" + vs.getParameter("mimeArquivo").toLowerCase() + "'");
            formatodoc.open();

            if (formatodoc.recordCount() <= 0) {
                return "erro:" + TTranslate.translate(vs, "Extensão ") + vs.getParameter("mimeArquivo") + TTranslate.translate(vs, " não cadastrada.");
            }

            if (tipodoc.fieldByName("CAMINHO").asString().isEmpty()) {
                return TTranslate.translate(vs, "Nenhum caminho para salvar o arquivo informado.\nConfirme o cadastro do tipo de documento.");
            }

            TClientDataSet raiz = TClientDataSet.create(vs, "GEDRAIZ");
            raiz.createDataSet();
            raiz.condicao("WHERE 1=1");
            raiz.open();

            TClientDataSet usu = TClientDataSet.create(vs, "USUARIO");
            usu.createDataSet();

            if (!tipodoc.fieldByName("CUSUARIORESP").asString().isEmpty()) {
                usu.condicao("WHERE USUARIO.CUSUARIO = " + codUsuario);
                usu.open();
            }

            File validaDest = new File(raiz.fieldByName("CAMINHO").asString(), tipodoc.fieldByName("CAMINHO").asString());
            if (!validaDest.exists()) {
                return "erro:" + TTranslate.translate(vs, "Pasta de destino: ") + validaDest.getAbsolutePath() + "\n" + TTranslate.translate(vs, "Não localizada.");
            }

            TClientDataSet doc = TClientDataSet.create(vs, "DOCUMENTO");
            doc.createDataSet();
            doc.condicao("WHERE DOCUMENTO.TITULO='" + vs.getParameter("TITULO") + "' AND DOCUMENTO.CTPDOC=" + vs.getParameter("TIPODOC"));
            doc.open();

            boolean newRev = false;
            if (doc.recordCount() > 0) {
                if (TConfirm.confirm(vs, "Já existe um documento com esse título e tipo\\nDeseja incluir uma nova revisão?", "Confirmação")) {
                    newRev = true;
                } else {
                    return "erro: " + TTranslate.translate(vs, "Processo cancelado");
                }
            }

            if (!newRev) {
                if (vs.getParameter("CDOC") == null || vs.getParameter("CDOC").equals("")) {
                    doc.insert();
                    doc.fieldByName("TITULO").asString(vs.getParameter("TITULO"));
                    doc.fieldByName("CTPDOC").asString(vs.getParameter("TIPODOC"));
                    doc.fieldByName("CONTCOPIA").asString("N");
                    doc.fieldByName("CFORMATODOC").asString(formatodoc.fieldByName("CFORMATODOC").asString());
                    doc.fieldByName("DTCRIACAO").asDate(new Date());
                } else {
                    doc.close();
                    doc.condicao("WHERE DOCUMENTO.CDOC = " + vs.getParameter("CDOC"));
                    doc.open();

                    titulo = replaceSpecial(doc.fieldByName("TITULO").asString());
                    doc.edit();
                }

                if (vs.getParameter("CLASSDOC") != null && !vs.getParameter("CLASSDOC").equals("") && !vs.getParameter("CLASSDOC").equals("null")) {
                    doc.fieldByName("CCLASSIFICACAODOC").asString(vs.getParameter("CLASSDOC"));
                }

                if (usu.recordCount() > 0) {
                    doc.fieldByName("RESPONSAVEL").asString(usu.fieldByName("NOME").asString());
                }

                TecniconLookup formato = new TecniconLookup();
                Object GED = formato.lookup("GED/documento");
                vs.addParametros("CTPDOC", vs.getParameter("TIPODOC"));
                try {
                    val = (String) GED.getClass().getMethod("numDoc", VariavelSessao.class).invoke(GED, vs);
                    if (val == null) {
                        val = vs.getRetornoOK();
                    }
                } catch (Exception ex) {
                    throw new ExcecaoTecnicon(vs, ex.getMessage());
                }

                if (val != null && !val.equals("")) {
                    doc.fieldByName("NUMERO").asString(val);
                }

                doc.post();

                if (val == null || val.equals("")) {
                    val = doc.fieldByName("CDOC").asString();
                }
            } else {
                if (val != null && !val.equals("")) {
                    val = doc.fieldByName("NUMERO").asString();
                }

                if (val == null || val.equals("")) {
                    val = doc.fieldByName("CDOC").asString();
                }
            }
            String REV = "";

            TClientDataSet revisao = TClientDataSet.create(vs, "DOCUMENTOREV");
            revisao.createDataSet();
            revisao.condicao("WHERE DOCUMENTOREV.CDOC = " + doc.fieldByName("CDOC").asString());
            revisao.ordenar("ORDER BY DOCUMENTOREV.REVISAO DESC");
            revisao.open();

            int cRev = 1;
            if (revisao.recordCount() > 0) {
                cRev = revisao.fieldByName("REVISAO").asInteger() + 1;
            }

            REV = cRev + "_";

            revisao.insert();
            revisao.fieldByName("CDOC").asString(doc.fieldByName("CDOC").asString());
            revisao.fieldByName("REVISAO").asInteger(cRev);
            revisao.fieldByName("DATA").asDate(new Date());
            revisao.post();

            doc.edit();
            doc.fieldByName("REVISAO").asInteger(cRev);
            doc.post();

            //<editor-fold defaultstate="collapsed" desc="Upload do arquivo">
            HttpServletRequest request = (HttpServletRequest) vs.getObject("request");
            DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
            fileItemFactory.setSizeThreshold(50 * 1024 * 1024); //50 MB

            ServletFileUpload uploadHandler = new ServletFileUpload(fileItemFactory);
            List items;
            File arq = null;
            try {
                items = uploadHandler.parseRequest(request);
                Iterator itr = items.iterator();

                while (itr.hasNext()) {
                    FileItem item = (FileItem) itr.next();
                    InputStream tmp = item.getInputStream();

//                OutputStream arquivo = new FileOutputStream(new File(caminho + vs.getParameter("TITULO") + "." + vs.getParameter("mimeArquivo").toLowerCase()));//ERRADO
                    arq = new File(validaDest, val + "_" + REV + titulo + "." + vs.getParameter("mimeArquivo").toLowerCase());
                    OutputStream arquivo;
                    try {
                        arquivo = new FileOutputStream(arq);
                    } catch (Exception ex) {
                        // Trata possível erro de permissão
                        uploadLDap(vs, item, tmp, validaDest, val + "_" + REV + titulo + "." + vs.getParameter("mimeArquivo").toLowerCase());
                        continue;
                    }
                    int read = 0;
                    byte[] bytes = new byte[1024];

                    while ((read = tmp.read(bytes)) != -1) {
                        arquivo.write(bytes, 0, read);
                    }

                    if (arquivo != null) {
                        try {
                            // outputStream.flush();
                            arquivo.close();
                        } catch (IOException e) {
                            throw new ExcecaoTecnicon(vs, e);
                        }

                    }

                }
            } catch (FileUploadException ex) {
                return "erro:" + ex.getMessage();
            }
            //</editor-fold>

            TClientDataSet DOCREVARQ = TClientDataSet.create(vs, "DOCREVARQ");
            DOCREVARQ.createDataSet();
            DOCREVARQ.insert();
            DOCREVARQ.fieldByName("SDOCUMENTOREV").asString(revisao.fieldByName("SDOCUMENTOREV").asString());
            DOCREVARQ.fieldByName("DESCRICAO").asString(titulo);
            DOCREVARQ.fieldByName("CFORMATODOC").asString(formatodoc.fieldByName("CFORMATODOC").asString());
            DOCREVARQ.fieldByName("ARQUIVO").asString(arq.getAbsolutePath());
            DOCREVARQ.fieldByName("PRINCIPAL").asString("S");
            DOCREVARQ.post();

            if (vs.getParameter("TABELA") != null && !vs.getParameter("TABELA").equals("") && vs.getParameter("ID") != null && !vs.getParameter("ID").equals("")) {
                TClientDataSet vinc = TClientDataSet.create(vs, "DOCVINCULO");
                vinc.createDataSet();
                vinc.insert();
                vinc.fieldByName("CDOC").asString(doc.fieldByName("CDOC").asString());
                vinc.fieldByName("TABELA").asString(vs.getParameter("TABELA").equals("undefined") || vs.getParameter("TABELA").equals("") ? "DOCUMENTO" : vs.getParameter("TABELA"));
                vinc.fieldByName("ID").asString(vs.getParameter("ID").equals("undefined") || vs.getParameter("ID").equals("") ? doc.fieldByName("CDOC").asString() : vs.getParameter("ID"));
                vinc.fieldByName("LISTA").asString("S");
                vinc.post();
            }
            return TTranslate.translate(vs, "Arquivo enviado com sucesso");
        }
    }

    public static String[] REPLACES
            = {
                "a", "e", "i", "o", "u", "c", "A", "E", "I", "O", "U", "C"
            };

    public static Pattern[] PATTERNS = null;

    public static void compilePatterns() {
        PATTERNS = new Pattern[REPLACES.length];
        PATTERNS[0] = Pattern.compile("[âãáàä]");
        PATTERNS[1] = Pattern.compile("[éèêë]");
        PATTERNS[2] = Pattern.compile("[íìîï]");
        PATTERNS[3] = Pattern.compile("[óòôõö]");
        PATTERNS[4] = Pattern.compile("[úùûü]");
        PATTERNS[5] = Pattern.compile("[ç]");
        PATTERNS[6] = Pattern.compile("[ÂÃÁÀÄ]");
        PATTERNS[7] = Pattern.compile("[ÉÈÊË]");
        PATTERNS[8] = Pattern.compile("[ÍÌÎÏ]");
        PATTERNS[9] = Pattern.compile("[ÓÒÔÕÖ]");
        PATTERNS[10] = Pattern.compile("[ÚÙÛÜ]");
        PATTERNS[11] = Pattern.compile("[Ç]");
    }

    public static String replaceSpecial(String text) {
        if (PATTERNS == null) {
            compilePatterns();
        }

        String result = text;
        for (int i = 0; i < PATTERNS.length; i++) {
            Matcher matcher = PATTERNS[i].matcher(result);
            result = matcher.replaceAll(REPLACES[i]);
        }
        return result.replace(" ", "");
    }

    private String replaceCharSpecial(String str) {
        return str.replaceAll("[/\\\\<>:*|?\"]", "_");
    }

    private void uploadLDap(VariavelSessao vs, FileItem item, InputStream tmp, File validaDest, String caminho) throws ExcecaoTecnicon {
        TSQLDataSetTec con = TSQLDataSetTec.create(vs);
        con.commandText("SELECT PARAMETROTEC.IPSERVAD, PARAMETROTEC.USUARIO, PARAMETROTEC.PASS, PARAMETROTEC.NOMEDOMINIO FROM PARAMETROTEC");
        con.open();

        if (!"".equals(con.fieldByName("IPSERVAD").asString())) {
            try {
                jcifs.Config.setProperty("jcifs.netbios.wins", con.fieldByName("IPSERVAD").asString());
                NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(con.fieldByName("NOMEDOMINIO").asString(), con.fieldByName("USUARIO").asString(), con.fieldByName("PASS").asString());

                String cam = (validaDest + "/" + caminho).replace("\\", "/");
                if (cam.startsWith("//")) {
                    cam = cam.substring(2);
                }

                SmbFile f = new SmbFile("smb://" + cam, auth);
                if (!f.exists()) {
                    f.createNewFile();
                }

                try (SmbFileOutputStream sfos = new SmbFileOutputStream(f)) {
                    int read = 0;
                    byte[] bytes = new byte[1024];

                    while ((read = tmp.read(bytes)) != -1) {
                        sfos.write(bytes, 0, read);
                    }
                }

            } catch (ExcecaoTecnicon | IOException ex) {
                throw new ExcecaoTecnicon(vs, ex);
            }
        }
    }

    private void registraVincDoc(VariavelSessao vs) throws ExcecaoTecnicon {

        TSQLDataSetEmp sql = TSQLDataSetEmp.create(vs);

        sql.commandText("SELECT VINCDOC.ORDEM FROM VINCDOC"
                + " WHERE VINCDOC.TIPO = '" + vs.getParameter("TIPO") + "'"
                + " AND VINCDOC.CODIGO = " + vs.getParameter("CODIGO")
                + " ORDER BY VINCDOC.ORDEM DESC");
        sql.open();

        int ordem = 1;
        if (!sql.isEmpty()) {
            ordem = sql.fieldByName("ORDEM").asInteger() + 1;
        }

        TClientDataSet VINCDOC = TClientDataSet.create(vs, "VINCDOC");
        VINCDOC.createDataSet();

        VINCDOC.insert();
        VINCDOC.fieldByName("TIPO").asString(vs.getParameter("TIPO"));
        VINCDOC.fieldByName("TITULO").asString(vs.getParameter("TITULO"));

        if (vs.getParameter("CAMINHOARQUIVO") != null && !vs.getParameter("CAMINHOARQUIVO").equals("")) {
            VINCDOC.fieldByName("CAMINHO").asString(vs.getParameter("CAMINHOARQUIVO"));
        } else {
            VINCDOC.fieldByName("CAMINHO").asString(vs.getParameter("CAMINHOUPLOAD"));
        }

        VINCDOC.fieldByName("ORDEM").asInteger(ordem);
        VINCDOC.fieldByName("CODIGO").asString(vs.getParameter("CODIGO"));

        if (vs.getParameter("CODIGO1") != null && !vs.getParameter("CODIGO1").equals("")) {
            VINCDOC.fieldByName("CODIGO1").asString(vs.getParameter("CODIGO1"));
        }

        VINCDOC.post();
    }
}
