
package wmnkitio.musicxml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import wmnkitnotation.Chord;
import wmnkitnotation.Duration;
import wmnkitnotation.Measure;
import wmnkitnotation.NotationElement;
import wmnkitnotation.Note;
import wmnkitnotation.Pitch;
import wmnkitnotation.Rest;
import wmnkitnotation.Score;
import wmnkitnotation.Staff;

/**
 *
 * @author Otso Björklund
 */
public class MusicXMLReaderMock {
    
    private final DocumentBuilder docBuilder;
    private int divisions = 0;
    private int beatType = 4;
    
    public MusicXMLReaderMock() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        dbf.setNamespaceAware(true);
        dbf.setFeature("http://xml.org/sax/features/namespaces", false);
        dbf.setFeature("http://xml.org/sax/features/validation", false);    
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        this.docBuilder = dbf.newDocumentBuilder();
    }
    
    public Score createScoreFromFile(String inputFile) {
        
        Score score = null;
        
        try {
            File input = new File(inputFile);
            Document doc = this.docBuilder.parse(input);
            List<Staff> staves = createStavesFromDocument(doc);
            score = new Score(staves);
        }
        catch( Exception e ) {
            System.out.println( "Parsing of " + inputFile + " failed with exception " + e );
        }
        
        return score;
    }
    
    private List<Staff> createStavesFromDocument(Document musicXmlDoc) {
        ArrayList<Staff> staves = new ArrayList();
    
        NodeList parts = musicXmlDoc.getElementsByTagName(MusicXmlTags.part);
        
        for(int i = 0; i < parts.getLength(); ++i) {
            Node part = parts.item(i);
            Staff staff = staffFromPartNode(part);
            
            if(staff != null)
                staves.add(staff);
        }
        
        return staves;
    }
    
    private Staff staffFromPartNode(Node part) {
        String staffName = part.getAttributes().getNamedItem(MusicXmlTags.partId).getNodeValue();
        
        System.out.println("Found part with id: " + staffName);
        ArrayList<Measure> measures = new ArrayList();
        
        NodeList partChildren = part.getChildNodes();
        
        for(int i = 0; i < partChildren.getLength(); ++i) {
            Node m = partChildren.item(i);
            
            if(m.getNodeName().equals(MusicXmlTags.measure)) {
                Measure measure = createMeasureFromElement(m);
            
                if(measure != null)
                    measures.add(measure);
            }
        }
        
        return new Staff(staffName, measures);
    }
    
    private Measure createMeasureFromElement(Node measureElement) {
        List<List<NotationElement>> layers = new ArrayList();
        layers.add(new ArrayList());
        
        NamedNodeMap measureAttributes = measureElement.getAttributes();
        String measureNumString = measureAttributes.getNamedItem(MusicXmlTags.measureNum).getTextContent();
        int measureNumber = Integer.parseInt(measureNumString);
        
        NodeList measureChildren = measureElement.getChildNodes();
        List<NotationElement> tmpList = new ArrayList();
        int layer = 0;
            
        
        for(int i = 0; i < measureChildren.getLength(); ++i) {
            Node n = measureChildren.item(i);
            
            // Read measure attributes
            if(n.getNodeName().equals(MusicXmlTags.measureAttr)) {
                
                Node divsEl = null;
                
                for(int j = 0; j < n.getChildNodes().getLength(); ++j) {
                    
                    Node attrChild = n.getChildNodes().item(j);
                    if(attrChild.getNodeName().equals(MusicXmlTags.attrDivs)) {
                        divsEl = attrChild;
                        break;
                    }
                }
                
                if(divsEl != null) {
                    int divs = Integer.parseInt(divsEl.getTextContent());
                
                    if(divs != 0)
                        this.divisions = divs;
                }
            }
            
            // Read note 
            if(n.getNodeName().equals(MusicXmlTags.note)) {
            
                NotationElement e = createNotationElemementFromXmlElement(n);
                
                // Get voice from n and set layer
                Node voiceNode = getChildWithName(n, MusicXmlTags.voice);
                if(voiceNode != null) {
                    String voiceStr = voiceNode.getTextContent();
                    layer = Integer.parseInt(voiceStr) - 1;
                }
                
                if(e != null) {
                    if(e.isRest()) {
                        if(!tmpList.isEmpty()) {
                            NotationElement el = noteElFromList(tmpList);
                            addToLayers(layers, layer, el);
                            tmpList.clear();
                        }
                        
                        addToLayers(layers, layer, e);
                    }
                    else {
                        if(getChildWithName(n, MusicXmlTags.chord) == null) {
                            if(!tmpList.isEmpty()) {
                                NotationElement el = noteElFromList(tmpList);
                                addToLayers(layers, layer, el);
                                tmpList.clear();
                            } 
                            
                            tmpList.add(e);
                        } 
                        else {
                            tmpList.add(e);
                        } 
                    }
                }
            }
            
            if(i == measureChildren.getLength() - 1) {
                if(!tmpList.isEmpty()) {
                    NotationElement el = noteElFromList(tmpList);
                    addToLayers(layers, layer, el);
                    tmpList.clear();
                } 
            }
        }
        
        return Measure.getMeasure(measureNumber, layers);
    }
    
    private void addToLayers(List<List<NotationElement>> layers, int layer, NotationElement el) {
        if(layer < layers.size())
            layers.get(layer).add(el);
        else {
            while(layers.size() <= layer) {
                layers.add(new ArrayList());
            }
            layers.get(layer).add(el);
        }
    }
    
    private NotationElement noteElFromList(List<NotationElement> notes) {
        NotationElement ret;
        
        if(notes.size() == 1)
            ret = notes.get(0);
        else {
            List<Note> noteList = new ArrayList();
            
            for(NotationElement el : notes) {
                if(el instanceof Note) {
                    noteList.add((Note) el);
                }
            }
            
            ret = Chord.getChord(noteList);
        }
        
        return ret;
    }
    
    private NotationElement createNotationElemementFromXmlElement(Node noteElement) {
        NotationElement created = null;
        
        Node pitch = getChildWithName(noteElement, MusicXmlTags.pitch);
        Node rest = getChildWithName(noteElement, MusicXmlTags.rest);
        Node duration = getChildWithName(noteElement, MusicXmlTags.duration);
        Node voice = getChildWithName(noteElement, MusicXmlTags.voice);
        
        if(pitch != null) {
            Pitch.Base base = pitchNameBaseFromString(getChildWithName(pitch, MusicXmlTags.pitchStep).getTextContent());
            int alter = 0;
            Node octaveNode = getChildWithName(pitch, MusicXmlTags.pitchOct);
            String octaveString = octaveNode.getTextContent();
            int octave = Integer.parseInt(octaveString);
            Pitch p = Pitch.getPitch(base, alter, octave);
            
            Duration d = calculateDuration(Integer.parseInt(duration.getTextContent()));
            created = Note.getNote(p, d);
        } 
        else if(rest != null) {
            Duration d = calculateDuration(Integer.parseInt(duration.getTextContent()));
            created = Rest.getRest(d);
        }
        
        return created;
    }
    
    private Pitch.Base pitchNameBaseFromString(String pitchName) {
        
        if(pitchName != null) {
            switch(pitchName) {
                case "C": return Pitch.Base.C;
                case "D": return Pitch.Base.D;
                case "E": return Pitch.Base.E;
                case "F": return Pitch.Base.F;
                case "G": return Pitch.Base.G;
                case "A": return Pitch.Base.A;
                case "B": return Pitch.Base.B;
            }
        }
        else
        {
            System.out.println("NULL pitchName");
        }
        
        return Pitch.Base.C;
    }   
    
    private Node getChildWithName(Node parent, String name) {
        Node n = null;
        NodeList children = parent.getChildNodes();
        
        for(int i = 0; i < children.getLength(); ++i) {
//            System.out.println("Looking for node " + name + ". Looking at:" + children.item(i).getNodeName());
            if(children.item(i).getNodeName().equals(name)) {
//                System.out.println("Found " + name);
                return children.item(i);
            }
        }
    
        return n;
    }
    
    private Duration calculateDuration(int durationNumber) {
        return Duration.getDuration(durationNumber, this.divisions * this.beatType);
    }
}
