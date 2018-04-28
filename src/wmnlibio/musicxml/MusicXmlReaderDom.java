/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
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
import wmnlibnotation.builders.ChordBuilder;
import wmnlibnotation.noteobjects.Barline;
import wmnlibnotation.noteobjects.Chord;
import wmnlibnotation.noteobjects.Clef;
import wmnlibnotation.noteobjects.Duration;
import wmnlibnotation.noteobjects.KeySignature;
import wmnlibnotation.noteobjects.KeySignatures;
import wmnlibnotation.builders.MeasureBuilder;
import wmnlibnotation.builders.NoteBuilder;
import wmnlibnotation.noteobjects.Note;
import wmnlibnotation.builders.PartBuilder;
import wmnlibnotation.builders.RestBuilder;
import wmnlibnotation.noteobjects.Pitch;
import wmnlibnotation.noteobjects.Rest;
import wmnlibnotation.noteobjects.Score;
import wmnlibnotation.builders.ScoreBuilder;
import wmnlibnotation.noteobjects.SingleStaffPart;
import wmnlibnotation.noteobjects.TimeSignature;

/**
 * A parser for MusicXML files.
 * @author Otso Björklund
 */
public class MusicXmlReaderDom implements MusicXmlReader {
    
    private static final int MIN_STAFF_NUMBER = SingleStaffPart.STAFF_NUMBER;
    
    private DocumentBuilder docBuilder;
    
    // TODO: make parses configurable
    // -Validation
    // -scorewise/partwise
    public MusicXmlReaderDom() {
        
        try {
            configure();
        } catch (ParserConfigurationException ex) {
            // Todo: Where and how to log parsing errors?
            Logger.getLogger(MusicXmlReaderDom.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void configure() throws ParserConfigurationException {
        
        // TODO: make parser configurable
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        dbf.setNamespaceAware(true);
        dbf.setFeature("http://xml.org/sax/features/namespaces", false);
        dbf.setFeature("http://xml.org/sax/features/validation", false);    
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        this.docBuilder = dbf.newDocumentBuilder();
    }
    
    /**
     * Create a <code>Score</code> from a MusicXML file.
     * @param filePath Path of the MusicXML file.
     * @return Score from the file.
     * @throws IOException 
     */
    public Score readScore(String filePath) throws IOException {
    
        Score score = null;
        File musicXmlFile = new File(filePath);
        
        try {
            Document musicXmlDoc = this.docBuilder.parse(musicXmlFile);
            score = createScore(musicXmlDoc);
        } catch (SAXException ex) {
            Logger.getLogger(MusicXmlReaderDom.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        return score;
    }
    
    /**
     * Create a Score from a MusicXML document.
     */
    private Score createScore(Document doc) {
        ScoreBuilder scoreBuilder = new ScoreBuilder();
        readScoreAttributesToBuilder(scoreBuilder, doc);
        readPartsIntoScoreBuilder(scoreBuilder, doc);
        return scoreBuilder.build();
    }
    
    /**
     * Read the attributes of the score from the Document and 
     * add them to the ScoreBuilder.
     */
    private void readScoreAttributesToBuilder(ScoreBuilder scoreBuilder, Document doc) {
        // TODO: Extend this when the attributes of a score are increased.
        Node movementTitle = doc.getElementsByTagName(MusicXmlTags.SCORE_MOVEMENT_TITLE).item(0);
        if(movementTitle != null)
            scoreBuilder.setAttribute(Score.Attribute.NAME, movementTitle.getTextContent());
        
        Node identification = doc.getElementsByTagName(MusicXmlTags.SCORE_IDENTIFICATION).item(0);
        Node creatorNode = findChild(identification, MusicXmlTags.SCORE_IDENTIFICATION_CREATOR);
        
        if(creatorNode != null)
            scoreBuilder.setAttribute(Score.Attribute.COMPOSER, creatorNode.getTextContent());
    }
    
    /**
     * Go through the parts defined in the MusicXML Document and
     * add the parts to the ScoreBuilder.
     */
    private void readPartsIntoScoreBuilder(ScoreBuilder scoreBuilder, Document doc) {

        Map<String, PartBuilder> partBuilders = new HashMap<>();
        
        // Read part attributes.
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

                    partBuilders.put(partId, new PartBuilder(partName));
                    // TODO: read the other part attributes into the partBuilder.
                }
            }
        }
        
        // Read measures into part builders.
        NodeList partNodes = doc.getElementsByTagName(MusicXmlTags.PART);
        for(int i = 0; i < partNodes.getLength(); ++i) {
            Node partNode = partNodes.item(i);
            String partId = partNode.getAttributes().getNamedItem(MusicXmlTags.PART_ID).getTextContent();
            PartBuilder partBuilder = partBuilders.get(partId);
            
            readMeasuresIntoPartBuilder(partBuilder, partNode);
            scoreBuilder.addPart(partBuilder.build());
        }
    }
    
    /**
     * Go through the measures in the part and add them to the PartBuilder.
     */
    private void readMeasuresIntoPartBuilder(PartBuilder partBuilder, Node partNode) {
        
        int staves = getNumberOfStaves(partNode);
        Map<Integer, Context> contexts = new HashMap<>();
        
        // Create the context containers for the staves.
        for(int staffNumber = MIN_STAFF_NUMBER; staffNumber < staves + MIN_STAFF_NUMBER; ++staffNumber)
            contexts.put(staffNumber, new Context());
            
        // Read measure node by node, create measure and add to list
        NodeList measureNodes = partNode.getChildNodes();
        for(int i = 0; i < measureNodes.getLength(); ++i) {
            Node measureNode = measureNodes.item(i);
            
            // Make sure that the node really is a measure node.
            if(measureNode.getNodeName().equals(MusicXmlTags.MEASURE)) {
                readMeasureIntoPartBuilder(partBuilder, measureNode, contexts, staves);
            }
        }
    }
    
    /**
     * Find the number of staves in the part.
     */
    private int getNumberOfStaves(Node partNode) {
        int staves = 1;
        
        // Find first attributes node and check if it has staves defined.
        NodeList measureNodes = partNode.getChildNodes();
        for(int i = 0; i < measureNodes.getLength(); ++i) {
            Node measureNode = measureNodes.item(i);
            
            if(measureNode.getNodeName().equals(MusicXmlTags.MEASURE)) {
                
                Node attributesNode = findChild(measureNode, MusicXmlTags.MEASURE_ATTRIBUTES);
                if(attributesNode != null) {
                    Node stavesNode = findChild(attributesNode, MusicXmlTags.MEAS_ATTR_STAVES);
                    if(stavesNode != null) {
                        staves = Integer.parseInt(stavesNode.getTextContent());
                    }
                    
                    // If the number of staves is not defined in the first attributes node,
                    // then assume that the part has 1 staff.
                    break;
                }
            }
        }
        
        return staves;
    }
    
    /**
     * Read the measure information from the Node, create a Measure
     * from the node and add it to the PartBuilder.
     */
    private void readMeasureIntoPartBuilder(PartBuilder partBuilder, Node measureNode, Map<Integer, Context> contexts, int staves) {
        
        int measureNumber = Integer.parseInt(measureNode.getAttributes().getNamedItem(MusicXmlTags.MEASURE_NUM).getTextContent());
        
        Map<Integer, MeasureBuilder> measureBuilders = new HashMap<>();
        Map<Integer, ChordBuffer> chordBuffers = new HashMap<>();
        Map<Integer, List<Duration>> offsets = new HashMap<>();
        Map<Integer, Clef> lastClefs = new HashMap<>();
        
        // Initialize the helper data structures.
        for(int staff = SingleStaffPart.STAFF_NUMBER; staff <= staves; ++staff) {
            measureBuilders.put(staff, new MeasureBuilder(measureNumber));
            chordBuffers.put(staff, new ChordBuffer());
            offsets.put(staff, new ArrayList<>());
            lastClefs.put(staff, contexts.get(staff).getClef());
        }
        
        NodeList measureChildren = measureNode.getChildNodes();
        for(int i = 0; i < measureChildren.getLength(); ++i) {
            Node node = measureChildren.item(i);
            
            // Handle measure attributes that occur in the beginning of measure
            if(offsets.get(1).isEmpty() && node.getNodeName().equals(MusicXmlTags.MEASURE_ATTRIBUTES)) {
                updateContexts(node, contexts);
                
                for(Integer staff : contexts.keySet())
                    lastClefs.put(staff, contexts.get(staff).getClef());
            }
            
            // Handle note
            if(node.getNodeName().equals(MusicXmlTags.NOTE) && !isGraceNote(node)) {
                int staffNumber = MIN_STAFF_NUMBER;
                Node staffNode = findChild(node, MusicXmlTags.NOTE_STAFF);
                if(staffNode != null)
                    staffNumber = Integer.parseInt(staffNode.getTextContent());

                Context context = contexts.get(staffNumber);
                MeasureBuilder builder = measureBuilders.get(staffNumber);
                
                int layer = getLayer(node);
                
                Duration duration = getDuration(node, context.getDivisions());
                offsets.get(staffNumber).add(duration);
                
                // TODO: add handling articulations etc.
                
                if(isRest(node)) {
                    chordBuffers.get(staffNumber).contentsToBuilder(builder);
                    builder.addToLayer(layer, new RestBuilder(duration));
                }
                else {
                    Pitch pitch = getPitch(node);
                    NoteBuilder noteBuilder = new NoteBuilder(pitch, duration);
                    
                    // Read articulations etc.
                    
                    if(endsTie(node))
                        noteBuilder.setIsTiedFromPrevious(true);
                    
                    if(hasChordTag(node)) {
                        chordBuffers.get(staffNumber).addNote(noteBuilder, layer);
                    } 
                    else {
                        chordBuffers.get(staffNumber).contentsToBuilder(builder);
                        chordBuffers.get(staffNumber).addNote(noteBuilder, layer);
                    }
                }
            }
            
            // Handle clef changes
            if(node.getNodeName().equals(MusicXmlTags.MEASURE_ATTRIBUTES)) {
                
                Clef clef = null;
                Node clefNode = findChild(node, MusicXmlTags.CLEF);
                int clefStaff = 1;
                
                if(clefNode != null) {
                    clef = MusicXmlReaderDom.this.getClef(clefNode);
                    NamedNodeMap clefAttributes = clefNode.getAttributes();
                    Node clefStaffNode = clefAttributes.getNamedItem(MusicXmlTags.CLEF_STAFF);
                    if(clefStaffNode != null)
                        clefStaff = Integer.parseInt(clefStaffNode.getTextContent());
                }
                if(clef != null && !offsets.get(clefStaff).isEmpty()) {
                    Duration cumulatedDur = Duration.sumOf(offsets.get(clefStaff));
                    measureBuilders.get(clefStaff).addClefChange(cumulatedDur, clef);
                    lastClefs.put(clefStaff, clef);
                }
            }
            
            // Handle barlines
            // Barlines are not staff specific, so a barline node affects all staves in measure.
            if(node.getNodeName().equals(MusicXmlTags.BARLINE)) {
                
                Barline barline = getBarline(node);
                
                Node locationNode = node.getAttributes().getNamedItem(MusicXmlTags.BARLINE_LOCATION);
                if(locationNode != null && locationNode.getTextContent().equals(MusicXmlTags.BARLINE_LOCATION_LEFT)) {
                    for(Integer staff : measureBuilders.keySet())
                        measureBuilders.get(staff).setLeftBarline(barline);
                }
                else { 
                    for(Integer staff : measureBuilders.keySet())
                        measureBuilders.get(staff).setRightBarline(barline);
                }
            }
        }
        
        for(int staffNumber = MIN_STAFF_NUMBER; staffNumber < staves + MIN_STAFF_NUMBER; ++staffNumber) {
            MeasureBuilder builder = measureBuilders.get(staffNumber);
            Context context = contexts.get(staffNumber);
            
            chordBuffers.get(staffNumber).contentsToBuilder(builder);

            builder.setClef(context.getClef());
            builder.setTimeSig(context.getTimeSig());
            builder.setKeySig(context.getKeySig());
            context.setClef(lastClefs.get(staffNumber));
            
            partBuilder.addMeasureToStaff(staffNumber, builder.build());
        }
    }
    
    private void updateContexts(Node attributesNode, Map<Integer, Context> contexts) {
        
        for(Integer staff : contexts.keySet()) {
            Context context = contexts.get(staff);
            
            // Divisions are the same for all staves.
            context.setDivisions(getDivisions(attributesNode, context.getDivisions()));
            
            // TODO:
            // Time and key signatures can be different for different staves but
            // that's not necessarily handled in the attributes nodes.
            context.setTimeSig(getTimeSig(attributesNode, context, staff));
            context.setKeySig(getKeySig(attributesNode, context));
        }
        
        List<Node> clefNodes = findChildren(attributesNode, MusicXmlTags.CLEF);
        
        for(Node clefNode : clefNodes) {
            int staffNumber = MIN_STAFF_NUMBER;
            Node clefStaffNode = clefNode.getAttributes().getNamedItem(MusicXmlTags.CLEF_STAFF);
            if(clefStaffNode != null)
                staffNumber = Integer.parseInt(clefStaffNode.getTextContent());
            
            Context context = contexts.get(staffNumber);
            context.setClef(MusicXmlReaderDom.this.getClef(clefNode));
        }
    }
    
    /**
     * Get the number of divisions from the Node.
     */
    private int getDivisions(Node attributesNode, int previousDivisions) {
        Node divisionsNode = findChild(attributesNode, MusicXmlTags.MEAS_ATTR_DIVS);
        if(divisionsNode != null)
            return Integer.parseInt(divisionsNode.getTextContent());
        
        return previousDivisions;
    }
        
    /**
     * Get the KeySignature defined in the Node.
     */
    private KeySignature getKeySig(Node attributesNode, Context previous) {
        Node keySigNode = findChild(attributesNode, MusicXmlTags.MEAS_ATTR_KEY);
        if(keySigNode != null) {
            int fifths = Integer.parseInt(findChild(keySigNode, MusicXmlTags.MEAS_ATTR_KEY_FIFTHS).getTextContent());
            return keyFromAlterations(fifths);
        }
        else
            return previous.getKeySig();
    }
    
    /**
     * Get TimeSignature from Node if it is for staff with staffNumber.
     */
    private TimeSignature getTimeSig(Node attributesNode, Context previous, int staffNumber) {
        TimeSignature timeSig;
        Node timeSigNode = findChild(attributesNode, MusicXmlTags.MEAS_ATTR_TIME);
        if(timeSigNode != null) {
            int beats = Integer.parseInt(findChild(timeSigNode, MusicXmlTags.MEAS_ATTR_BEATS).getTextContent());
            int beatType = Integer.parseInt(findChild(timeSigNode, MusicXmlTags.MEAS_ATTR_BEAT_TYPE).getTextContent());
           
            Node staffNumberNode = timeSigNode.getAttributes().getNamedItem(MusicXmlTags.MEAS_ATTR_STAFF_NUMBER);
            if(staffNumberNode != null) {
                int staffNumberAttr = Integer.parseInt(staffNumberNode.getTextContent());
                if(staffNumberAttr == staffNumber)
                    timeSig = TimeSignature.getTimeSignature(beats, beatType);
                else
                    timeSig = previous.getTimeSig();
            }
            else
                timeSig = TimeSignature.getTimeSignature(beats, beatType);
        }
        else
            timeSig = previous.getTimeSig();
        
        return timeSig;
    }
    
    /**
     * Get the KeySignature based on number of alterations.
     */
    private KeySignature keyFromAlterations(int alterations) {
        switch(alterations) {    
            case 0: return KeySignatures.CMAJ_AMIN;
            case 1: return KeySignatures.GMAJ_EMIN;
            case 2: return KeySignatures.DMAJ_BMIN;
            case 3: return KeySignatures.AMAJ_FSHARPMIN;
            case 4: return KeySignatures.EMAJ_CSHARPMIN;
            case 5: return KeySignatures.BMAJ_GSHARPMIN;
            case 6: return KeySignatures.FSHARPMAJ_DSHARPMIN;
    
            case -1: return KeySignatures.FMAJ_DMIN;
            case -2: return KeySignatures.BFLATMAJ_GMIN;
            case -3: return KeySignatures.EFLATMAJ_CMIN;
            case -4: return KeySignatures.AFLATMAJ_FMIN;
            case -5: return KeySignatures.DFLATMAJ_BFLATMIN;
            case -6: return KeySignatures.GFLATMAJ_EFLATMIN;
        }
        
        return KeySignatures.CMAJ_AMIN;
    }
    
    /**
     * Get Clef from clefNode. 
     */
    private Clef getClef(Node clefNode) {
        Node clefSignNode = findChild(clefNode, MusicXmlTags.CLEG_SIGN);
        String clefName = clefSignNode.getTextContent();
        
        Node clefLineNode = findChild(clefNode, MusicXmlTags.CLEF_LINE);
        int clefLine = 3;
        if(clefLineNode != null)
            clefLine = Integer.parseInt(clefLineNode.getTextContent());
        
        Clef.Type type = Clef.Type.G;
        switch(clefName) {
            case MusicXmlTags.CLEF_G: type = Clef.Type.G; break;
            case MusicXmlTags.CLEF_F: type = Clef.Type.F; break;
            case MusicXmlTags.CLEF_C: type = Clef.Type.C; break;
            case MusicXmlTags.CLEF_PERC: type = Clef.Type.PERCUSSION; break;
        }
    
        return Clef.getClef(type, clefLine);
    }
    
    private Barline getBarline(Node barlineNode) {
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
    
    private boolean isGraceNote(Node noteNode) {
        return findChild(noteNode, MusicXmlTags.NOTE_GRACE_NOTE) != null;
    }
    
    private boolean hasChordTag(Node noteNode) {
        return findChild(noteNode, MusicXmlTags.NOTE_CHORD) != null;
    }
    
    private boolean startsTie(Node noteNode) {
        Node tieNode = findChild(noteNode, MusicXmlTags.TIE);
        if(tieNode == null)
            return false;
        
        String tieType = tieNode.getAttributes().getNamedItem(MusicXmlTags.TIE_TYPE).getTextContent();
        return tieType.equals(MusicXmlTags.TIE_START);
    }
    
    private boolean endsTie(Node noteNode) {
        Node tieNode = findChild(noteNode, MusicXmlTags.TIE);
        if(tieNode == null)
            return false;
        
        String tieType = tieNode.getAttributes().getNamedItem(MusicXmlTags.TIE_TYPE).getTextContent();
        return tieType.equals(MusicXmlTags.TIE_STOP);
    }
    
    private boolean isRest(Node noteNode) {
        return findChild(noteNode, MusicXmlTags.NOTE_REST) != null;
    }
    
    private int getLayer(Node noteNode) {
        int layer = 1;
        
        Node voiceNode = findChild(noteNode, MusicXmlTags.NOTE_VOICE);
        if(voiceNode != null) {
            layer = Integer.parseInt(voiceNode.getTextContent());
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
        else {
            Node unpitchedNode = findChild(noteNode, MusicXmlTags.NOTE_UNPITCHED);
            if(unpitchedNode != null) {
                Node stepNode = findChild(unpitchedNode, MusicXmlTags.UNPITCHED_STEP);
                Node octaveNode = findChild(unpitchedNode, MusicXmlTags.UNPITCHED_OCTAVE);
                
                if(stepNode != null && octaveNode != null) {
                    Pitch.Base pitchBase = getPitchBase(stepNode);
                    int octave = Integer.parseInt(octaveNode.getTextContent());
                    pitch = Pitch.getPitch(pitchBase, 0, octave);
                }
            }
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
    
    private Duration getDuration(Node noteNode, int divisions) {
        Node durationNode = findChild(noteNode, MusicXmlTags.NOTE_DURATION);
        if(durationNode != null) {
            int nominator = Integer.parseInt(durationNode.getTextContent());
            // In MusicXml divisions is the number of parts into which a quarter note 
            // is divided. Therefore divisions needs to be multiplied by 4.
            return Duration.getDuration(nominator, divisions * 4);
        }
        
        return null;
    }
    
    /**
     * Find the child with childName from the children of Node parent.
     * return null if no child with given name is found.
     */
    private Node findChild(Node parent, String childName) {
        NodeList children = parent.getChildNodes();
        for(int i = 0; i < children.getLength(); ++i) {
            Node child = children.item(i);
            if(child != null && child.getNodeName()!= null) {
                if(child.getNodeName().equals(childName))
                    return child;
            }
        }
        
        return null;
    }

    /**
     * Find all children with given name from Node parent. 
     */
    private List<Node> findChildren(Node parent, String childName) {
        List<Node> foundChildren = new ArrayList<>();
        
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
    
    /**
     * Class for handling the reading of chords.
     */
    private class ChordBuffer {
        private final List<Pair<NoteBuilder, Integer>> chordBuffer = new ArrayList<>();
        
        public ChordBuffer() {}
        
        public void addNote(NoteBuilder noteBuilder, int layer) {
            this.chordBuffer.add(new Pair<>(noteBuilder, layer));
        }
        
        public void contentsToBuilder(MeasureBuilder builder) {
            if(!this.chordBuffer.isEmpty()) {
                if(this.chordBuffer.size() > 1) {
                    List<NoteBuilder> notes = new ArrayList<>();
                    int layer = this.chordBuffer.get(0).getValue();
                    for(Pair<NoteBuilder, Integer> pair : this.chordBuffer)
                        notes.add(pair.getKey());

                    builder.addToLayer(layer, new ChordBuilder(notes));
                }
                else if(this.chordBuffer.size() == 1) {
                    int layer = this.chordBuffer.get(0).getValue();
                    NoteBuilder noteBuilder = this.chordBuffer.get(0).getKey();
                    builder.addToLayer(layer, noteBuilder);
            }

                this.chordBuffer.clear();
            }   
        }
    }
    
    /**
     * Class for keeping track of all the context dependent information that
     * continue from measure to measure.
     */
    private class Context {

        private int divisions;
        private KeySignature keySig;
        private TimeSignature timeSig;
        private Clef clef;
        
        public Context() {}
        
        public int getDivisions() {
            return divisions;
        }

        public void setDivisions(int divisions) {
            this.divisions = divisions;
        }

        public KeySignature getKeySig() {
            return keySig;
        }

        public void setKeySig(KeySignature keySig) {
            this.keySig = keySig;
        }

        public TimeSignature getTimeSig() {
            return timeSig;
        }

        public void setTimeSig(TimeSignature timeSig) {
            this.timeSig = timeSig;
        }

        public Clef getClef() {
            return clef;
        }

        public void setClef(Clef clef) {
            this.clef = clef;
        }
    }
}
