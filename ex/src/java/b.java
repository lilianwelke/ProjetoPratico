

import br.com.tecnicon.control.TTranslate;
import br.com.tecnicon.server.Propriedades;
import br.com.tecnicon.server.context.TClassLoader;
import br.com.tecnicon.server.dataset.TClientDataSet;
import br.com.tecnicon.server.dataset.TSQLDataSetEmp;
import br.com.tecnicon.server.execoes.ExcecaoTecnicon;
import br.com.tecnicon.server.sessao.VariavelSessao;
import br.com.tecnicon.utils.file.FileUtils;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author jackson.mireski
 */
@Stateless
@LocalBean
public class LMSMetodosNew
{

    private final String EMPRESA = Propriedades.isServidorDesenvolvimento() ? "13628" : (Propriedades.isServidorHomologacao() ? "2929" : "17");

    public void removeArquivo(VariavelSessao vs) throws ExcecaoTecnicon
    {
        vs.addParametros("cempresa", EMPRESA);
        if (vs.getValor("empresa") == null || vs.getValor("empresa").isEmpty())
        {
            vs.addParametros("empresa", EMPRESA);
        }
        String clmscurso = vs.getParameter("CLMSCURSO");

        TClientDataSet cdsLMSCURSO = TClientDataSet.create(vs, "LMSCURSO");
        cdsLMSCURSO.createDataSet();
        cdsLMSCURSO.condicao(" WHERE LMSCURSO.CLMSCURSO = " + clmscurso);
        cdsLMSCURSO.open();

        if (!cdsLMSCURSO.fieldByName("IMAGEM").asString().trim().isEmpty())
        {
            File f = new File(cdsLMSCURSO.fieldByName("IMAGEM").asString().trim());
            if (f.exists() && f.isFile())
            {
                f.delete();
            }
            cdsLMSCURSO.edit();
            cdsLMSCURSO.fieldByName("IMAGEM").asString("");
            cdsLMSCURSO.post();
        }
    }

    public void beforeDelete(VariavelSessao vs, TClientDataSet lms) throws ExcecaoTecnicon
    {
        vs.addParametros("cempresa", EMPRESA);
        if (vs.getValor("empresa") == null || vs.getValor("empresa").isEmpty())
        {
            vs.addParametros("empresa", EMPRESA);
        }
        String caminho = lms.fieldByName("CAMINHO").asString();

        if (caminho != null && !caminho.equals(""))
        {
            File f = new File(caminho);
            if (f.exists() && f.isFile())
            {
                f.delete();
            }
        }
    }

    public String moveArquivos(VariavelSessao vs) throws ExcecaoTecnicon
    {
        vs.addParametros("cempresa", EMPRESA);
        if (vs.getValor("empresa") == null || vs.getValor("empresa").isEmpty())
        {
            vs.addParametros("empresa", EMPRESA);
        }
        if (vs.getParameter("transferidos") != null && vs.getParameter("transferidos").equals("S"))
        {
            String movidos = "";

            TSQLDataSetEmp cdsLMSPARAMETRO = TSQLDataSetEmp.create(vs);
            cdsLMSPARAMETRO.createDataSet();
            cdsLMSPARAMETRO.commandText("SELECT LMSPARAMETRO.DIRETORIODOCS FROM LMSPARAMETRO");
            cdsLMSPARAMETRO.open();

            File diretorio = new File(cdsLMSPARAMETRO.fieldByName("DIRETORIODOCS").asString());
            if (diretorio.exists())
            {
                TSQLDataSetEmp cdsLMSCONTEUDO = TSQLDataSetEmp.create(vs);
                cdsLMSCONTEUDO.createDataSet();
                cdsLMSCONTEUDO.commandText("SELECT LMSCONTEUDO.CAMINHO FROM LMSCONTEUDO");
                cdsLMSCONTEUDO.open();

                InputStream in;
                OutputStream out;

                File[] arquivos = diretorio.listFiles();

                String arq = "";
                for (int cont = 0; cont < arquivos.length; cont++)
                {
                    if (!cdsLMSCONTEUDO.locate("CAMINHO", arquivos[cont].getAbsolutePath().trim()))
                    {
                        if (arquivos[cont].isFile())
                        {
                            movidos += arquivos[cont].getAbsolutePath().trim() + "\n";
                        }
                    }
                }
            }
            return movidos;

        } else
        {
            TSQLDataSetEmp cdsLMSPARAMETRO = TSQLDataSetEmp.create(vs);
            cdsLMSPARAMETRO.createDataSet();
            cdsLMSPARAMETRO.commandText("SELECT LMSPARAMETRO.DIRETORIODOCS FROM LMSPARAMETRO");
            cdsLMSPARAMETRO.open();

            File diretorio = new File(cdsLMSPARAMETRO.fieldByName("DIRETORIODOCS").asString());
            if (diretorio.exists())
            {
                TSQLDataSetEmp cdsLMSCONTEUDO = TSQLDataSetEmp.create(vs);
                cdsLMSCONTEUDO.createDataSet();
                cdsLMSCONTEUDO.commandText("SELECT LMSCONTEUDO.CAMINHO FROM LMSCONTEUDO");
                cdsLMSCONTEUDO.open();

                InputStream in;
                OutputStream out;

                File[] arquivos = diretorio.listFiles();

                String arq = "";
                String movidos = "";
                for (int cont = 0; cont < arquivos.length; cont++)
                {
                    if (!cdsLMSCONTEUDO.locate("CAMINHO", arquivos[cont].getAbsolutePath().trim()))
                    {
                        if (arquivos[cont].isFile())
                        {
                            arq = arquivos[cont].getAbsolutePath().trim();
                            try
                            {
                                File toFile = new File(arq);
                                File fromFile = new File(arq.replace("/lms/uploads/", "/lms/uploadsbkp/"));
                                if (!fromFile.exists())
                                {
                                    if (!fromFile.getParentFile().exists())
                                    {
                                        fromFile.getParentFile().mkdir();
                                    }
                                    fromFile.createNewFile();
                                }
                                in = new FileInputStream(toFile);
                                out = new FileOutputStream(fromFile);

                                byte[] buffer = new byte[1024];
                                int length;
                                while ((length = in.read(buffer)) > 0)
                                {
                                    out.write(buffer, 0, length);
                                }

                                in.close();
                                out.close();

                                toFile.delete();

                                movidos += arq + "\n";

                            } catch (IOException e)
                            {
                                throw new ExcecaoTecnicon(vs, e);
                            }
                        }
                    }
                }
                return movidos;//arquivos[0].getAbsolutePath();
            }
            return "";
        }
    }

    public String uploadArquivo(VariavelSessao vs) throws ExcecaoTecnicon
    {
        vs.addParametros("cempresa", EMPRESA);
        if (vs.getValor("empresa") == null || vs.getValor("empresa").isEmpty())
        {
            vs.addParametros("empresa", EMPRESA);
        }
        ArrayList<String> formatosSuportadosConteudo = new ArrayList<>(Arrays.asList(
                "image/jpeg",
                "image/png",
                "application/pdf",
                "video/mp4",
                "text/html;charset=utf-8"));

        ArrayList<String> formatosSuportadosCabecalho = new ArrayList<>(Arrays.asList(
                "image/jpeg",
                "image/png"));

        TSQLDataSetEmp dir = TSQLDataSetEmp.create(vs);
        dir.commandText("SELECT LMSPARAMETRO.DIRETORIODOCS FROM LMSPARAMETRO");
        dir.open();

        if (dir.isEmpty() || dir.fieldByName("DIRETORIODOCS").isNull())
        {
            return "ERRO:" + TTranslate.translate(vs, "Diretório para salvar os arquivos não foi informado!");
        }

        File destino = new File(dir.fieldByName("DIRETORIODOCS").asString());
        if (!destino.exists())
        {
            if (!destino.mkdir())
            {
                return "ERRO:" + TTranslate.translate(vs, "Não foi possível criar o diretório com o caminho") + " \"" + dir.fieldByName("DIRETORIODOCS").asString() + "\" "
                        + TTranslate.translate(vs, "parametrizado!");
            }
        }

        TClientDataSet dsUCTCONTEUDO = TClientDataSet.create(vs, "LMSCONTEUDO");
        TClientDataSet dsLMSCURSO = TClientDataSet.create(vs, "LMSCURSO");
        TClientDataSet dsLMSWBS = TClientDataSet.create(vs, "LMSWBS");
        dsUCTCONTEUDO.createDataSet();
        dsLMSCURSO.createDataSet();
        dsLMSWBS.createDataSet();

        HttpServletRequest request = (HttpServletRequest) vs.getObject("request");
        DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
        fileItemFactory.setSizeThreshold(50 * 1024 * 1024); //50 MB

        ServletFileUpload uploadHandler = new ServletFileUpload(fileItemFactory);
        List items;
        File arq;
        String name;
        try
        {
            items = uploadHandler.parseRequest(request);
            Iterator itr = items.iterator();

            while (itr.hasNext())
            {
                FileItem item = (FileItem) itr.next();
                InputStream tmp = item.getInputStream();

                if (vs.getParameter("CABECALHO") != null && vs.getParameter("CABECALHO").equals("true"))
                {
                    if (!formatosSuportadosCabecalho.contains(item.getContentType()))
                    {
                        return "ERRO:" + TTranslate.translate(vs, "Formato de arquivo não suportado");
                    }
                    name = (new Random().nextInt((100000 - 1) + 1) + 1) + "_" + item.getName();
                } else if (vs.getParameter("IMAGEMWBS") != null && vs.getParameter("IMAGEMWBS").equals("true"))
                {
                    if (!formatosSuportadosCabecalho.contains(item.getContentType()))
                    {
                        return "ERRO:" + TTranslate.translate(vs, "Formato de arquivo não suportado");
                    }
                    name = (new Random().nextInt((100000 - 1) + 1) + 1) + "_" + item.getName();
                } else
                {
                    if (!formatosSuportadosConteudo.contains(item.getContentType()))
                    {
                        return "ERRO:" + TTranslate.translate(vs, "Formato de arquivo não suportado");
                    }
                    name = (new Random().nextInt((100000 - 1) + 1) + 1) + "_" + item.getName();
                }

                arq = new File(destino.getAbsolutePath(), name);
                OutputStream arquivo;
                try
                {
                    arquivo = new FileOutputStream(arq);
                } catch (Exception ex)
                {
                    // Trata possível erro de permissão
                    TClassLoader.execMethod("criaFormPai/UploadArquivo", "uploadLDap", vs, item, tmp, destino.getAbsolutePath(), name);
                    continue;
                }
                int read = 0;
                byte[] bytes = new byte[1024];

                while ((read = tmp.read(bytes)) != -1)
                {
                    arquivo.write(bytes, 0, read);
                }
                if (vs.getParameter("CABECALHO") != null && vs.getParameter("CABECALHO").equals("true"))
                {
                    insertArquivoCabecalhoCurso(vs, dsLMSCURSO, arq.getAbsolutePath());
                } else if (vs.getParameter("IMAGEMWBS") != null && vs.getParameter("IMAGEMWBS").equals("true"))
                {
                    insertArquivoLMSWBS(vs, dsLMSWBS, arq.getAbsolutePath());
                } else
                {
                    insertArquivoConteudo(vs, dsUCTCONTEUDO, item.getName(), arq.getAbsolutePath());
                }

                try
                {
                    arquivo.close();
                } catch (IOException e)
                {
                    throw new ExcecaoTecnicon(vs, e);
                }
            }
        } catch (FileUploadException | IOException ex)
        {
            return "ERRO:" + ex.getMessage();
        }

        if (!"".equals(vs.getParameter("SEQ")))
        {
            return vs.getParameter("SEQ");
        }

        return "";
    }

    private void insertArquivoConteudo(VariavelSessao vs, TClientDataSet dsLMSCONTEUDO,
            String item, String path) throws ExcecaoTecnicon
    {

        String smlsConteudo = vs.getParameter("SLMSCONTEUDO") != null && !vs.getParameter("SLMSCONTEUDO").equals("")
                ? vs.getParameter("SLMSCONTEUDO") : "0";

        dsLMSCONTEUDO.condicao(" WHERE LMSCONTEUDO.SLMSCONTEUDO = " + smlsConteudo);
        dsLMSCONTEUDO.open();

        if (!dsLMSCONTEUDO.isEmpty() && !dsLMSCONTEUDO.fieldByName("CAMINHO").asString().isEmpty())
        {
            dsLMSCONTEUDO.edit();
            dsLMSCONTEUDO.fieldByName("LMSCONTEUDO").asString(item);
            dsLMSCONTEUDO.fieldByName("CAMINHO").asString(path);
            dsLMSCONTEUDO.post();
        } else
        {
            dsLMSCONTEUDO.insert();
            dsLMSCONTEUDO.fieldByName("LMSCONTEUDO").asString(item);
            dsLMSCONTEUDO.fieldByName("CAMINHO").asString(path);
            dsLMSCONTEUDO.post();
        }

        vs.addParametros("SEQ", dsLMSCONTEUDO.fieldByName("SLMSCONTEUDO").asString());
    }

    private void insertArquivoLMSWBS(VariavelSessao vs, TClientDataSet dsLMSWBS, String absolutePath) throws ExcecaoTecnicon
    {
        dsLMSWBS.condicao(" WHERE LMSWBS.SLMSWBS = " + vs.getParameter("SLMSWBS"));
        dsLMSWBS.open();
        dsLMSWBS.edit();
        dsLMSWBS.fieldByName("IMAGEM").asString(absolutePath);
        dsLMSWBS.post();
    }

    private void insertArquivoCabecalhoCurso(VariavelSessao vs, TClientDataSet dsLMSCURSO, String absolutePath) throws ExcecaoTecnicon
    {
        dsLMSCURSO.condicao(" WHERE LMSCURSO.CLMSCURSO = " + vs.getParameter("CLMSCURSO"));
        dsLMSCURSO.open();
        dsLMSCURSO.edit();
        dsLMSCURSO.fieldByName("IMAGEM").asString(absolutePath);
        dsLMSCURSO.post();
        converteIMGCurso(vs, vs.getParameter("CLMSCURSO"));

    }

    public void converteIMGCurso(VariavelSessao vs) throws ExcecaoTecnicon
    {
        converteIMGCurso(vs, "");
    }

    public void converteIMGCurso(VariavelSessao vs, String cLmsCurso) throws ExcecaoTecnicon
    {
        File original;
        TClientDataSet cds = TClientDataSet.create(vs, "LMSCURSO");
        cds.createDataSet();
        if ("".equals(cLmsCurso))
        {
            cds.condicao("WHERE LMSCURSO.IMAGEM IS NOT NULL");
        } else
        {
            cds.condicao(new StringBuilder("WHERE LMSCURSO.IMAGEM IS NOT NULL AND LMSCURSO.CLMSCURSO=").append(cLmsCurso).toString());
            cds.open();
            cds.edit();
            cds.fieldByName("IMGCOMP").asString("N");
            cds.post();
        }
        cds.open();
        bkpImagens(vs, cds);
        cds.first();
        while (!cds.eof())
        {
            if (!"S".equals(cds.fieldByName("IMGCOMP").asString()))
            {
                original = new File(cds.fieldByName("IMAGEM").asString());
                if (original.exists() && original.isFile())
                {
                    try
                    {
                        BufferedImage img = ImageIO.read(original);
                        int ow = img.getWidth();
                        int oh = img.getHeight();
                        if (ow > 0 && oh > 0)
                        {
                            int fh = 288;
                            int fw = (int) (ow / (oh / 288d));
                            BufferedImage novaImagem = new BufferedImage(fw, fh, img.getType());
                            Graphics2D g2d = novaImagem.createGraphics();
                            g2d.drawImage(img, 0, 0, fw, fh, null);
                            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                            g2d.dispose();
                            RenderedImage rendImage = novaImagem;
                            ImageIO.write(rendImage, "jpg", original);
                            cds.edit();
                            cds.fieldByName("IMGCOMP").asString("S");
                            cds.post();
                        }
                    } catch (IOException ex)
                    {
                        throw new ExcecaoTecnicon(vs, ex);
                    }

                }
            }
            cds.next();

        }
    }

    public void bkpImagens(VariavelSessao vs, TClientDataSet cds) throws ExcecaoTecnicon
    {
        File diretorio;
        File bkp;
        File arqBkp;
        cds.first();
        while (!cds.eof())
        {
            if (!"S".equals(cds.fieldByName("IMGCOMP").asString()))
            {
                diretorio = new File(cds.fieldByName("IMAGEM").asString());
                bkp = new File(diretorio.getParentFile(), "bkp");
                bkp.mkdirs();
                File original = new File(cds.fieldByName("IMAGEM").asString());
                arqBkp = new File(bkp, original.getName());
                if (original.isFile() && !arqBkp.exists())
                {
                    FileUtils.copy(vs, original, arqBkp);
                }
            }
            cds.next();
        }

    }
}
