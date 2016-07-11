
package wmnkitio.musicxml;

import java.io.File;
import java.net.URLConnection;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

/**
 *
 * @author Otso Björklund
 */
public class MusicXMLReader {
    
    private DocumentBuilder docBuilder;
    
    public MusicXMLReader() throws ParserConfigurationException {
        this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    }
    
    public void readXMLMock(String inputFile) {
        try {
            System.setProperty("http.agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        
            Document doc = docBuilder.parse(new File(inputFile));
            
            NodeList nodes = doc.getChildNodes();
            
            for( int i = 0; i < nodes.getLength(); ++i){
                System.out.println( nodes.item(i).getNodeValue());
                System.out.println( nodes.item(i).getChildNodes());
                System.out.println( nodes.item(i).getTextContent());
            }
            
            
        
        }
        catch( Exception e ) {
            System.out.println(e);
        }
    
    }
}
