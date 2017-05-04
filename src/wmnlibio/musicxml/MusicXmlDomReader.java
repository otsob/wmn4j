/*
 * DOM parser for MusicXML.
 */
package wmnlibio.musicxml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import wmnlibnotation.Barline;
import wmnlibnotation.Chord;
import wmnlibnotation.Clef;
import wmnlibnotation.Clefs;
import wmnlibnotation.Duration;
import wmnlibnotation.KeySignature;
import wmnlibnotation.KeySignatures;
import wmnlibnotation.Measure;
import wmnlibnotation.MeasureBuilder;
import wmnlibnotation.MeasureAttributes;
import wmnlibnotation.Note;
import wmnlibnotation.Part;
import wmnlibnotation.PartBuilder;
import wmnlibnotation.SingleStaffPart;
import wmnlibnotation.Pitch;
import wmnlibnotation.Rest;
import wmnlibnotation.Score;
import wmnlibnotation.ScoreBuilder;
import wmnlibnotation.Staff;
import wmnlibnotation.TimeSignature;

/**
 *
 * @author Otso Björklund
 */
public class MusicXmlDomReader implements MusicXmlReader {
    
    private DocumentBuilder docBuilder;
    
    // Todo: make parses configurable
    // -Validation
    // -scorewise/partwise
    public MusicXmlDomReader() {
        
        try {
            configure();
        } catch (ParserConfigurationException ex) {
            // Todo: Where and how to log parsing errors?
            Logger.getLogger(MusicXmlDomReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void configure() throws ParserConfigurationException {
        
        // Todo: make parser configurable
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        dbf.setNamespaceAware(true);
        dbf.setFeature("http://xml.org/sax/features/namespaces", false);
        dbf.setFeature("http://xml.org/sax/features/validation", false);    
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        this.docBuilder = dbf.newDocumentBuilder();
    }
    
    public Score readScore(String filePath) throws IOException {
    
        Score score = null;
        File musicXmlFile = new File(filePath);
        
        try {
            Document musicXmlDoc = this.docBuilder.parse(musicXmlFile);
            score = createScore(musicXmlDoc);
        } catch (SAXException ex) {
            Logger.getLogger(MusicXmlDomReader.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        return score;
    }
    
    private Score createScore(Document doc) {
        ScoreBuilder scoreBuilder = new ScoreBuilder();
        readScoreAttributes(scoreBuilder, doc);
        createParts(scoreBuilder, doc);
        return scoreBuilder.build();
    }
    
    private void readScoreAttributes(ScoreBuilder scoreBuilder, Document doc) {
        Node movementTitle = doc.getElementsByTagName(MusicXmlTags.SCORE_MOVEMENT_TITLE).item(0);
        if(movementTitle != null)
            scoreBuilder.setAttribute(Score.Attribute.NAME, movementTitle.getTextContent());
        
        Node identification = doc.getElementsByTagName(MusicXmlTags.SCORE_IDENTIFICATION).item(0);
        Node creatorNode = findChild(identification, MusicXmlTags.SCORE_IDENTIFICATION_CREATOR);
        
        if(creatorNode != null)
            scoreBuilder.setAttribute(Score.Attribute.COMPOSER, creatorNode.getTextContent());
    }
    
    private void createParts(ScoreBuilder scoreBuilder, Document doc) {

        Map<String, String> partNames = new HashMap();
        
        // Read part info from <part-list>
        Node partsList = doc.getElementsByTagName(MusicXmlTags.PART_LIST).item(0);
        if(partsList != null) {
            NodeList scoreParts = partsList.getChildNodes();
            for(int i = 0; i < scoreParts.getLength(); ++i) {
                Node child = scoreParts.item(i);
                if(child.getNodeName().equals(MusicXmlTags.PLIST_SCORE_PART)) {
                    String partId = child.getAttributes().getNamedItem(MusicXmlTags.PART_ID).getTextContent();
                        
                    String partName = partId;
                    Node partNameNode = findChild(child, MusicXmlTags.PART_NAME);
                    if(partNameNode != null)
                        partName = partNameNode.getTextContent();

                    partNames.put(partId, partName);
                }
            }
        }
        
        NodeList partNodes = doc.getElementsByTagName(MusicXmlTags.PART);
        for(int i = 0; i < partNodes.getLength(); ++i) {
            Node partNode = partNodes.item(i);
            String partId = partNode.getAttributes().getNamedItem(MusicXmlTags.PART_ID).getTextContent();
            
            PartBuilder partBuilder = new PartBuilder(partNames.get(partId));
            
            createMeasures(partBuilder, partNode);
            scoreBuilder.addPart(partBuilder.build());
        }
    }
    
    private void createMeasures(PartBuilder partBuilder, Node partNode) {
        MeasureAttributes measureAttr = null;
        int divisions = 0;
        
        // Read measure node by node, create measure and add to list
        NodeList measureNodes = partNode.getChildNodes();
        for(int i = 0; i < measureNodes.getLength(); ++i) {
            Node measureNode = measureNodes.item(i);
            if(measureNode.getNodeName().equals(MusicXmlTags.MEASURE)) {
                List<Node> attributesNodes = findChildren(measureNode, MusicXmlTags.MEASURE_ATTRIBUTES);
                List<Node> barlineNodes = findChildren(measureNode, MusicXmlTags.BARLINE);
                
                divisions = getDivisions(attributesNodes, divisions);
                measureAttr = getMeasureAttr(attributesNodes, barlineNodes, measureAttr);

                // TODO: Check to which staff measure should be added.
                partBuilder.addMeasure(createMeasure(measureNode, measureAttr, divisions));
            }
        }
    }
    
    private int getDivisions(List<Node> attributesNodes, int previousDivisions) {
        
        if(!attributesNodes.isEmpty()) {
            Node attributesNode = attributesNodes.get(0);
            Node divisionsNode = findChild(attributesNode, MusicXmlTags.MEAS_ATTR_DIVS);
            if(divisionsNode != null)
                return Integer.parseInt(divisionsNode.getTextContent());
        }
        
        return previousDivisions;
    }
    
    private MeasureAttributes getMeasureAttr(List<Node> attributesNodes, List<Node> barlineNodes, MeasureAttributes previous) {
        if(attributesNodes.isEmpty() && barlineNodes.isEmpty())
            return previous;
        
        Node attributesNode = attributesNodes.get(0);
        
        Barline rightBarline = getRightBarline(barlineNodes);
        Barline leftBarline = getLeftBarline(barlineNodes);
        
        if(attributesNodes.isEmpty() && !barlineNodes.isEmpty()) {
            // TODO: Correct the handling of barlines
            return MeasureAttributes.getMeasureAttr(previous.getTimeSignature(), 
                                                    previous.getKeySignature(), 
                                                    rightBarline,
                                                    leftBarline,
                                                    previous.getClef());
        }
        
        TimeSignature timeSig;
        Node timeSigNode = findChild(attributesNode, MusicXmlTags.MEAS_ATTR_TIME);
        if(timeSigNode != null) {
            int beats = Integer.parseInt(findChild(timeSigNode, MusicXmlTags.MEAS_ATTR_BEATS).getTextContent());
            int beatType = Integer.parseInt(findChild(timeSigNode, MusicXmlTags.MEAS_ATTR_BEAT_TYPE).getTextContent());
            timeSig = TimeSignature.getTimeSignature(beats, beatType);
        }
        else
            timeSig = previous.getTimeSignature();
        
        KeySignature keySig;
        Node keySigNode = findChild(attributesNode, MusicXmlTags.MEAS_ATTR_KEY);
        if(keySigNode != null) {
            int fifths = Integer.parseInt(findChild(keySigNode, MusicXmlTags.MEAS_ATTR_KEY_FIFTHS).getTextContent());
            keySig = getKeySignature(fifths);
        }
        else
            keySig = previous.getKeySignature();
        
        Clef clef;
        Node clefNode = findChild(attributesNode, MusicXmlTags.CLEF);
        if(clefNode != null) {
            Node clefSignNode = findChild(clefNode, MusicXmlTags.CLEG_SIGN);
            clef = getClef(clefSignNode.getTextContent());
        }
        else
            clef = previous.getClef();
        
        // TODO: Handle clef changes.
        
        return MeasureAttributes.getMeasureAttr(timeSig, keySig, rightBarline, leftBarline, clef);
    }
    
    private KeySignature getKeySignature(int alterations) {
        switch(alterations) {    
            case 0: return KeySignatures.CMaj_Amin;
            case 1: return KeySignatures.GMaj_Emin;
            case 2: return KeySignatures.DMaj_Bmin;
            case 3: return KeySignatures.AMaj_FSharpMin;
            case 4: return KeySignatures.EMaj_CSharpMin;
            case 5: return KeySignatures.BMaj_GSharpMin;
            case 6: return KeySignatures.FSharpMaj_DSharpMin;
    
            case -1: return KeySignatures.FMaj_Dmin;
            case -2: return KeySignatures.BFlatMaj_Gmin;
            case -3: return KeySignatures.EFlatMaj_Cmin;
            case -4: return KeySignatures.AFlatMaj_Fmin;
            case -5: return KeySignatures.DFlatMaj_BFlatMin;
            case -6: return KeySignatures.GFlatMaj_EFlatMin;
        }
        
        return KeySignatures.CMaj_Amin;
    }
    
    private Clef getClef(String clefName) {
        switch(clefName) {
            case MusicXmlTags.CLEF_G: return Clefs.G;
            case MusicXmlTags.CLEF_F: return Clefs.F;
        }
    
        return Clefs.G;
    }
    
    private Barline getRightBarline(List<Node> barlineNodes) {
        Barline barline = getBarline(barlineNodes, MusicXmlTags.BARLINE_LOCATION_RIGHT);
        if(barline == null)
            return Barline.SINGLE;
        
        return barline;
    }
    
    private Barline getLeftBarline(List<Node> barlineNodes) {
        Barline barline = getBarline(barlineNodes, MusicXmlTags.BARLINE_LOCATION_LEFT);
        if(barline == null)
            return Barline.NONE;
    
        return barline;
    }
    
    private Barline getBarline(List<Node> barlineNodes, String location) {
        for(Node barlineNode : barlineNodes) {
            Node locationNode = barlineNode.getAttributes().getNamedItem(MusicXmlTags.BARLINE_LOCATION);
            if(locationNode.getTextContent().equals(location)) {
                return readBarlineNode(barlineNode);
            }
        }
        
        return null;
    }
    
    private Barline readBarlineNode(Node barlineNode) {
        if(barlineNode != null) {
            Node barlineStyleNode = findChild(barlineNode, MusicXmlTags.BARLINE_STYLE);
            String barlineString = barlineStyleNode.getTextContent();
            Node repeatNode = findChild(barlineNode, MusicXmlTags.BARLINE_REPEAT);

            switch(barlineString) {
                case MusicXmlTags.BARLINE_STYLE_DASHED: return Barline.DASHED;
                case MusicXmlTags.BARLINE_STYLE_HEAVY: return Barline.THICK;
                case MusicXmlTags.BARLINE_STYLE_HEAVY_LIGHT: return Barline.REPEAT_LEFT;
                case MusicXmlTags.BARLINE_STYLE_INVISIBLE: return Barline.INVISIBLE;
                case MusicXmlTags.BARLINE_STYLE_LIGHT_HEAVY: {
                    if(repeatNode == null)
                        return Barline.FINAL;
                    else
                        return Barline.REPEAT_RIGHT;
                }
                case MusicXmlTags.BARLINE_STYLE_LIGHT_LIGHT: return Barline.DOUBLE;
                default: return Barline.SINGLE;
            }
        }
        
        return Barline.NONE;
    }
    
    private Measure createMeasure(Node measureNode, MeasureAttributes measureInfo, int divisions) {
        
        int measureNumber = Integer.parseInt(measureNode.getAttributes().getNamedItem(MusicXmlTags.MEASURE_NUM).getTextContent());
        MeasureBuilder builder = new MeasureBuilder(measureNumber, measureInfo);
        List<Pair<Note, Integer>> chordBuffer = new ArrayList();
        
        NodeList measureChildren = measureNode.getChildNodes();
        for(int i = 0; i < measureChildren.getLength(); ++i) {
            Node node = measureChildren.item(i);
            if(node.getNodeName().equals(MusicXmlTags.NOTE) && !isGraceNote(node)) {
                int layer = getLayer(node);
                Duration duration = getDuration(node, divisions, measureInfo.getTimeSignature().getBeatDuration().getDenominator());
                
                // Todo: add handling articulations etc.
                
                if(isRest(node)) {
                    handleChordBuffer(builder, chordBuffer);
                    builder.addToLayer(layer, Rest.getRest(duration));
                }
                else {
                    Pitch pitch = getPitch(node);
                    
                    if(hasChordTag(node)) {
                        chordBuffer.add(new Pair(Note.getNote(pitch, duration), layer));
                    } 
                    else {
                        handleChordBuffer(builder, chordBuffer);
                        chordBuffer.add(new Pair(Note.getNote(pitch, duration), layer));
                    }
                }
            }
        }
        handleChordBuffer(builder, chordBuffer);
        
        return builder.build();
    }
    
    private boolean isGraceNote(Node noteNode) {
        return findChild(noteNode, MusicXmlTags.NOTE_GRACE_NOTE) != null;
    }
    
    private void handleChordBuffer(MeasureBuilder builder, List<Pair<Note, Integer>> chordBuffer) {
        if(!chordBuffer.isEmpty()) {
            if(chordBuffer.size() > 1) {
                List<Note> notes = new ArrayList();
                int layer = chordBuffer.get(0).getValue();
                for(Pair<Note, Integer> pair : chordBuffer)
                    notes.add(pair.getKey());
                
                builder.addToLayer(layer, Chord.getChord(notes));
            }
            else if(chordBuffer.size() == 1) {
                int layer = chordBuffer.get(0).getValue();
                Note note = chordBuffer.get(0).getKey();
                builder.addToLayer(layer, note);
            }

            chordBuffer.clear();
        }
    }
    
    private boolean hasChordTag(Node noteNode) {
        return findChild(noteNode, MusicXmlTags.NOTE_CHORD) != null;
    }
    
    private boolean isRest(Node noteNode) {
        return findChild(noteNode, MusicXmlTags.NOTE_REST) != null;
    }
    
    private int getLayer(Node noteNode) {
        int layer = 0;
        
        Node voiceNode = findChild(noteNode, MusicXmlTags.NOTE_VOICE);
        if(voiceNode != null) {
            layer = Integer.parseInt(voiceNode.getTextContent()) - 1;
        }
        
        return layer;
    }
    
    private Pitch getPitch(Node noteNode) {
        Pitch pitch = null;
        
        Node pitchNode = findChild(noteNode, MusicXmlTags.NOTE_PITCH);
        if(pitchNode != null) {
            Pitch.Base pitchBase = null;
            int alter = 0;
            int octave = 0;
            
            Node stepNode = findChild(pitchNode, MusicXmlTags.PITCH_STEP);
            if(stepNode != null)
                pitchBase = getPitchBase(stepNode);
            
            Node octaveNode = findChild(pitchNode, MusicXmlTags.PITCH_OCT);
            if(octaveNode != null)
                octave = Integer.parseInt(octaveNode.getTextContent());
            
            Node alterNode = findChild(pitchNode, MusicXmlTags.PITCH_ALTER);
            if(alterNode != null)
                alter = Integer.parseInt(alterNode.getTextContent());
            
            pitch = Pitch.getPitch(pitchBase, alter, octave);
        }
        
        return pitch;
    }
    
    private Pitch.Base getPitchBase(Node stepNode) {
        String pitchString = stepNode.getTextContent();
        
        if(pitchString != null) {
            switch(pitchString) {
                case "C": return Pitch.Base.C;
                case "D": return Pitch.Base.D;
                case "E": return Pitch.Base.E;
                case "F": return Pitch.Base.F;
                case "G": return Pitch.Base.G;
                case "A": return Pitch.Base.A;
                case "B": return Pitch.Base.B;
            }
        }
        
        return null;
    }
    
    private Duration getDuration(Node noteNode, int divisions, int beatType) {
        Node durationNode = findChild(noteNode, MusicXmlTags.NOTE_DURATION);
        if(durationNode != null) {
            int nominator = Integer.parseInt(durationNode.getTextContent());
            return Duration.getDuration(nominator, divisions * beatType);
        }
        
        return null;
    }
    
    private Node findChild(Node parent, String childName) {
        NodeList children = parent.getChildNodes();
        for(int i = 0; i < children.getLength(); ++i) {
            Node child = children.item(i);
            if(child != null && child.getNodeName()!= null) {
                if(children.item(i).getNodeName().equals(childName))
                    return children.item(i);
            }
        }
        
        return null;
    }

    private List<Node> findChildren(Node parent, String childName) {
        List<Node> foundChildren = new ArrayList();
        
        NodeList children = parent.getChildNodes();
        for(int i = 0; i < children.getLength(); ++i) {
            Node child = children.item(i);
            if(child != null && child.getNodeName()!= null) {
                if(children.item(i).getNodeName().equals(childName))
                    foundChildren.add(child);
            }
        }
        
        return foundChildren;
    }
}
