/*
 * DOM parser for MusicXML.
 */
package wmnkitio.musicxml;

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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import wmnkitnotation.Chord;
import wmnkitnotation.Clef;
import wmnkitnotation.Duration;
import wmnkitnotation.KeySignature;
import wmnkitnotation.Measure;
import wmnkitnotation.MeasureBuilder;
import wmnkitnotation.MeasureInfo;
import wmnkitnotation.Note;
import wmnkitnotation.Pitch;
import wmnkitnotation.Rest;
import wmnkitnotation.Score;
import wmnkitnotation.Staff;
import wmnkitnotation.TimeSignature;

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
        
        Node movementTitle = doc.getElementsByTagName(MusicXmlTags.SCORE_MOVEMENT_TITLE).item(0);
        String scoreName = "";
        if(movementTitle != null)
            scoreName = movementTitle.getTextContent();
        
        Node identification = doc.getElementsByTagName(MusicXmlTags.SCORE_IDENTIFICATION).item(0);
        Node creatorNode = findChild(identification, MusicXmlTags.SCORE_IDENTIFICATION_CREATOR);
        String composerName = "";
        if(creatorNode != null)
            composerName = creatorNode.getTextContent();
        
        List<Staff> staves = createStaves(doc);
        
        return new Score(scoreName, composerName, staves);
    }
    
    private List<Staff> createStaves(Document doc) {
        
        List<Staff> staves = new ArrayList();
        Map<String, Map<Staff.Info, String>> partsInfo = new HashMap();
        
        // Read staff info from <part-list>
        Node partsList = doc.getElementsByTagName(MusicXmlTags.PART_LIST).item(0);
        if(partsList != null) {
            NodeList scoreParts = partsList.getChildNodes();
            for(int i = 0; i < scoreParts.getLength(); ++i) {
                Node child = scoreParts.item(i);
                if(child.getNodeName().equals(MusicXmlTags.PLIST_SCORE_PART)) {
                    String partId = child.getAttributes().getNamedItem(MusicXmlTags.PART_ID).getTextContent();
                    
                    if(partsInfo.get(partId) == null) {
                        Map<Staff.Info, String> staffInfo = new HashMap();
                        
                        String staffName = "";
                        
                        Node partNameNode = findChild(child, MusicXmlTags.PART_NAME);
                        if(partNameNode != null)
                            staffName = partNameNode.getTextContent();
                        
                        staffInfo.put(Staff.Info.NAME, staffName);
                        partsInfo.put(partId, staffInfo);
                    }
                }
            }
        }
        
        // Read part nodes and create measures list
        NodeList partNodes = doc.getElementsByTagName(MusicXmlTags.PART);
        for(int i = 0; i < partNodes.getLength(); ++i) {
            Node partNode = partNodes.item(i);
            String partId = partNode.getAttributes().getNamedItem(MusicXmlTags.PART_ID).getTextContent();
            List<Measure> measures = createMeasures(partNode);

            // Create staff from staff info and measure list
            String staffName = "";
            Map<Staff.Info, String> partInfo = partsInfo.get(partId);
            
            if(partInfo != null)
                staffName = partInfo.get(Staff.Info.NAME);
            
            staves.add(new Staff(staffName, measures));
        }
        
        return staves;
    }
    
    private List<Measure> createMeasures(Node partNode) {
        List<Measure> measures = new ArrayList();
        MeasureInfo measureInfo = null;
        int divisions = 0;
        
        // Read measure node by node, create measure and add to list
        NodeList measureNodes = partNode.getChildNodes();
        for(int i = 0; i < measureNodes.getLength(); ++i) {
            Node measureNode = measureNodes.item(i);
            if(measureNode.getNodeName().equals(MusicXmlTags.MEASURE)) {
                Node attributesNode = findChild(measureNode, MusicXmlTags.MEASURE_ATTRIBUTES);
                Node barlineNode = findChild(measureNode, MusicXmlTags.BARLINE);

                if(attributesNode != null) {
                    Node divisionsNode = findChild(attributesNode, MusicXmlTags.MEAS_ATTR_DIVS);
                    if(divisionsNode != null)
                        divisions = Integer.parseInt(divisionsNode.getTextContent());

                    measureInfo = getMeasureInfo(attributesNode, barlineNode, measureInfo);
                } 
                else if(barlineNode != null) {
                    measureInfo = MeasureInfo.getMeasureInfo(measureInfo.getTimeSignature(), 
                                                             measureInfo.getKeySignature(), 
                                                             getBarline(barlineNode), 
                                                             measureInfo.getClef());
                }

                measures.add(createMeasure(measureNode, measureInfo, divisions));
            }
        }
        
        return measures;
    }
    
    private MeasureInfo getMeasureInfo(Node attributesNode, Node barlineNode, MeasureInfo previous) {
        Measure.Barline barline = getBarline(barlineNode);
        
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
        
        return MeasureInfo.getMeasureInfo(timeSig, keySig, barline, clef);
    }
    
    private KeySignature getKeySignature(int alterations) {
        switch(alterations) {    
            case 0: return KeySignature.CMaj_Amin;
            case 1: return KeySignature.GMaj_Emin;
            case 2: return KeySignature.DMaj_Bmin;
            case 3: return KeySignature.AMaj_FSharpMin;
            case 4: return KeySignature.EMaj_CSharpMin;
            case 5: return KeySignature.BMaj_GSharpMin;
            case 6: return KeySignature.FSharpMaj_DSharpMin;
    
            case -1: return KeySignature.FMaj_Dmin;
            case -2: return KeySignature.BFlatMaj_Gmin;
            case -3: return KeySignature.EFlatMaj_Cmin;
            case -4: return KeySignature.AFlatMaj_Fmin;
            case -5: return KeySignature.DFlatMaj_BFlatMin;
            case -6: return KeySignature.GFlatMaj_EFlatMin;
        }
        
        return KeySignature.CMaj_Amin;
    }
    
    private Clef getClef(String clefName) {
        switch(clefName) {
            case MusicXmlTags.CLEF_G: return Clef.G_CLEF;
            case MusicXmlTags.CLEF_F: return Clef.F_CLEF;
        }
    
        return Clef.G_CLEF;
    }
    
    private Measure.Barline getBarline(Node barlineNode) {
        if(barlineNode != null) {
            Node barlineStyleNode = findChild(barlineNode, MusicXmlTags.BARLINE_STYLE);
            String barlineString = barlineStyleNode.getTextContent();

            switch(barlineString) {
                case MusicXmlTags.BARLINE_STYLE_LIGHT_HEAVY: return Measure.Barline.FINAL;
            }
        }
        
        return Measure.Barline.SINGLE;
    }
    
    private Measure createMeasure(Node measureNode, MeasureInfo measureInfo, int divisions) {
        
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
}
