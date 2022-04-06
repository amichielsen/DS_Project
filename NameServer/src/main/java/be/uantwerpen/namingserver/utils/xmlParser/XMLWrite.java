package be.uantwerpen.namingserver.utils.xmlParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.net.Inet4Address;
import java.util.Map;
import java.util.TreeMap;

/**
 * This function will write a XML file based on a Map.
 * -- Louis de Looze
 */
public class XMLWrite {
    public static void serverList(TreeMap<Integer, Inet4Address> list) {
        try {
            // create new `Document`
            DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            Document dom = builder.newDocument();

            // first create root element
            Element root = dom.createElement("servers");
            dom.appendChild(root);

            for(Map.Entry<Integer, Inet4Address> entry : list.entrySet()){
                Element element = dom.createElement("server");
                root.appendChild(element);
                // now create child elements (name, email, phone)
                Element hash = dom.createElement("hash");
                hash.setTextContent(String.valueOf(entry.getKey()));

                Element ip = dom.createElement("ip");
                ip.setTextContent(entry.getValue().getHostAddress());
                // add child nodes to root node
                element.appendChild(hash);
                element.appendChild(ip);
            }

            // write DOM to XML file
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.transform(new DOMSource(dom), new StreamResult(new File("file.xml")));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
