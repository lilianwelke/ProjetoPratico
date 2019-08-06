package peixaria;

import br.com.tecnicon.server.execoes.ExcecaoTecnicon;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.ejb.Stateless;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author manuel.tusset
 */
@Stateless
public class UtilsRequest
{

    public HashMap<Integer, String> error = new HashMap<>();

    public Document strToDoc(String xml) throws ParserConfigurationException, SAXException, IOException, Exception
    {
        Document xDoc = null;
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            xDoc = builder.parse(new InputSource(new StringReader(xml)));
        } catch (ParserConfigurationException | SAXException | IOException er)
        {
            throw new Exception("ERRO PARSE XML: " + er.getMessage() + "\n"
                    + "XML:" + xml, er);
        }
        return xDoc;
    }

    public static List<Element> getElements(String elementTag, Element fromElement)
    {
        List<Element> elements = new ArrayList<Element>();
        NodeList nodeList = fromElement.getElementsByTagName(elementTag);
        for (int i = 0; i < nodeList.getLength(); i++)
        {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                elements.add((Element) node);
            }
        }
        return elements;
    }

    public static List<String> readErrosXml(InputStream xml)
            throws ParserConfigurationException, SAXException, IOException
    {

        List<String> errors = new ArrayList<String>();

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        InputSource is = new InputSource(xml);
        is.setEncoding("utf-8");
        Document doc = dBuilder.parse(is);

        Element errorsElement = doc.getDocumentElement();
        List<Element> errorElements = getElements("error", errorsElement);

        for (int i = 0; i < errorElements.size(); i++)
        {
            Element errorElement = errorElements.get(i);
            errors.add(getTagValue("code", errorElement) + "-" + getTagValue("message", errorElement));
        }

        return errors;
    }

    public static String getTagValue(String valueTag, Element fromElement)
    {
        NodeList nodeList = fromElement.getElementsByTagName(valueTag);
        if (nodeList != null && nodeList.getLength() > 0)
        {
            NodeList childList = nodeList.item(0).getChildNodes();
            if (childList != null && childList.getLength() > 0)
            {
                Node node = childList.item(0);
                return node.getNodeValue();
            }
        }
        return null;
    }

    public String translate(int erro) throws ExcecaoTecnicon
    {
        populaHash();

        return error.get(erro);
    }

    public void populaHash()
    {
        error.put(5003, "Falha de comunicação com a instituição financeira");
        error.put(10000, "Bandeira do Cartão de Credito Inválida");
        error.put(10001, "Tamanho do número do Cartão de Crédito Inválido");
        error.put(10002, "Formato de Data Inválido");
        error.put(10003, "Campo de Segurança Inválido");
        error.put(10004, "Código de Segurança é obrigatório");
        error.put(10006, "Tamanho do Código de Segurança Inválido");
        error.put(53004, "Quantidade de Itens da Compra Inválido");
        error.put(53005, "Informar tipo da Moeda é Obrigatório");
        error.put(53006, "Valor da Moeda é Inválido");
        error.put(53007, "Referencia do pagamento é inválida");
        error.put(53008, "Tamanho da URL de Notificação é inválida");
        error.put(53009, "Valor da URL de Notificação é inválida");
        error.put(53010, "E-mail do Remetente é obrigatório");
        error.put(53011, "Tamanho do E-mail do Remetente está inválido");
        error.put(53012, "Valor do E-mail do Remetente é inválido");
        error.put(53013, "Nome do Remetente é obrigatório");
        error.put(53014, "Tamanho do Nome do Remetente está inválido");
        error.put(53015, "Valor do Nome do Remetente está inválido");
        error.put(53017, "Valor do CPF do Remetente está inválido");
        error.put(53018, "Código de Área do Remetente é Obrigatório");
        error.put(53019, "Valor do Código de Áre do Remetente está inválido");
        error.put(53020, "Telefone do Remetente é Obrigatório");
        error.put(53021, "Valor do Telefone do Remetente está inválido");
        error.put(53022, "CEP do Endereço de Entrega é Obrigatório");
        error.put(53023, "Valor do CEP do Endereço de Entrega está inválido");
        error.put(53024, "Endereço de Entrega é Obrigatório");
        error.put(53025, "Valor do Endereço de Entrega está inválido");
        error.put(53026, "Número do Endereço de Entrega é Obrigatório");
        error.put(53027, "Valor do Número do Endereço de Entrega está Inválido");
        error.put(53028, "Tamanho do Complemento do Endereço de Entrega está Inválido");
        error.put(53029, "Bairro do Endereço de Entrega é Obrigatório");
        error.put(53030, "Tamanho do Bairro do Endereço de Entrega está inválido");
        error.put(53031, "Cidade do Endereço de Entrega é Obrigatório");
        error.put(53032, "Tamanho da Cidade do Endereço de Entrega está inválido");
        error.put(53033, "UF do Endereço de Entrega é obrigatório");
        error.put(53034, "Tamanho da UF do Endereço de Entrega está inválido");
        error.put(53035, "País do Endereço de Entrega é Obrigatório");
        error.put(53036, "Valor do País do Endereço de Entrega está inválido");
        error.put(53037, "Token do Cartão de Crédito é obrigatório");
        error.put(53038, "Quantidade de Parcelas é obrigatório");
        error.put(53039, "Valor da Quantidade de Parcelas está inválido");
        error.put(53040, "Valor da Parcela é Obrigatório");
        error.put(53041, "Tamanho do Valor da Parcela está inválido");
        error.put(53042, "Nome do Titular do Cartão de Crédito é obrigatório");
        error.put(53043, "Tamanho do Nome do Titular do Cartão de Crédito está inválido");
        error.put(53044, "Valor do Nome do Titular do Cartão de Crédito está inválido");
        error.put(53045, "CPF do Titular do Cartão de Crédito é Obrigatório");
        error.put(53046, "Valor do CPF do Titular do Cartão de Crédito está inválido");
        error.put(53047, "Data de Nascimento do Titular do Cartão de Crédito é Obrigatória");
        error.put(53048, "Valor da Data de Nascimento do Titular do Cartão de Crédito está inválida");
        error.put(53049, "Código de Área do Titular do Cartão de Crédito é Obrigatório");
        error.put(53050, "Valor do Código de Área do Titular do Cartão de Crédito está inválido");
        error.put(53051, "");
        error.put(53052, "");
        error.put(53053, "");
        error.put(53054, "");
        error.put(53055, "");
        error.put(53056, "");
        error.put(53057, "");
        error.put(53058, "");
        error.put(53059, "");
        error.put(53060, "");
        error.put(53061, "");
        error.put(53062, "");
        error.put(53063, "");
        error.put(53064, "");
        error.put(53065, "");
        error.put(53066, "");
        error.put(53067, "");
        error.put(53068, "");
        error.put(53069, "");
        error.put(53070, "");
        error.put(53071, "");
        error.put(53072, "");
        error.put(53073, "");
        error.put(53074, "");
        error.put(53075, "");
        error.put(53076, "");
        error.put(53077, "");
        error.put(53078, "");
        error.put(53079, "");
        error.put(53081, "");
        error.put(53084, "");
        error.put(53085, "");
        error.put(53086, "");
        error.put(53087, "");
        error.put(53091, "");
        error.put(53092, "");
        error.put(53095, "");
        error.put(53096, "");
        error.put(53097, "");
        error.put(53098, "");
        error.put(53099, "");
        error.put(53101, "");
        error.put(53102, "");
        error.put(53104, "");
        error.put(53105, "");
        error.put(53106, "");
        error.put(53109, "");
        error.put(53110, "");
        error.put(53111, "");
        error.put(53115, "");
        error.put(53117, "");
        error.put(53122, "");
        error.put(53140, "");
        error.put(53141, "");
        error.put(53142, "");
    }
}
