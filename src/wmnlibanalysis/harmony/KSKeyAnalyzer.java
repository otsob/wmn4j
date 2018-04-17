/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmnlibanalysis.harmony;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import wmnlibnotation.Durational;
import wmnlibnotation.Key;
import wmnlibnotation.PitchClass;

/**
 * Implements Krumhansl-Schmuckler algorithm
 * @author Otso Björklund
 */
public class KSKeyAnalyzer implements KeyAnalyzer {
    
    private final Map<Key, PCProfile> keyProfiles;
    
    public KSKeyAnalyzer() {
        this.keyProfiles = new HashMap();
        this.readKeyProfiles();
    }
    
    public Key analyzeKey(List<Durational> durationals) {
        PCProfile profile = PCProfile.getDurationWeightedProfile();
        for(Durational durational : durationals)
            profile.add(durational);
        
        profile.normalize();
        
        Key bestKey = Key.C_MAJOR;
        double maxCorrelation = -2.0;
        
        for(Key key : this.keyProfiles.keySet()) {
            double correlation = PCProfile.correlation(this.keyProfiles.get(key), profile);
            
            if(correlation > maxCorrelation) {
                maxCorrelation = correlation;
                bestKey = key;
            }
        }
        
        return bestKey;
    }
    
    private void readKeyProfiles() {
        
        Map<String,Key> keyStrings = new HashMap();
        for(Key key : Key.values())
            keyStrings.put(key.toString(), key);
        
        try {
            File keyProfilesFile = new File(KSKeyAnalyzer.class.getResource("../resources/KSKeyProfiles.csv").getPath());
            keyProfilesFile.setReadOnly();
            
            BufferedReader br = new BufferedReader(new FileReader(keyProfilesFile));
            String line = br.readLine();
            
            while(line != null && !line.isEmpty()) {
                if(line.charAt(0) != '#') {
                    String[] lineContents = line.split(",");
                    String cleanedKeyString = lineContents[0].trim();
                    Key key = keyStrings.get(cleanedKeyString);
                    
                    List<Double> values = new ArrayList();
                    for(int i = 1; i < lineContents.length; ++i) {
                        String cleanedString = lineContents[i].trim();
                        values.add(Double.parseDouble(cleanedString));
                    }
                    
                    PCProfile profile = createPCProfile(values);
                    this.keyProfiles.put(key, profile);
                }
                
                line = br.readLine();
            }
            
        } 
        catch (Exception e) {
            System.out.println(e);
        }
    }
    
    private PCProfile createPCProfile(List<Double> values) {
        PCProfile profile = new PCProfile();
        int i = 0;
        
        for(PitchClass pc : PitchClass.values()) {
            profile.setValue(pc, values.get(i++));
        }
    
        return profile;
    }
}
