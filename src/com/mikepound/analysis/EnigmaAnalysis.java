package com.mikepound.analysis;

import com.mikepound.analysis.fitness.FitnessFunction;
import com.mikepound.enigma.Enigma;
import com.mikepound.enigma.Plugboard;

import java.util.*;

public class EnigmaAnalysis {
    public enum AvailableRotors {
        THREE,
        FIVE,
        EIGHT
    }

    public static ScoredEnigmaKey[] findRotorConfiguration(char[] ciphertext, AvailableRotors rotors, String plugboard, int requiredKeys, FitnessFunction f) {
        String[] availableRotorList;

        switch (rotors) {
            case THREE:
                availableRotorList = new String[] {"I", "II", "III"};
                break;
            case FIVE:
                availableRotorList = new String[] {"I", "II", "III", "IV", "V"};
                break;
            case EIGHT:
            default:
                availableRotorList = new String[] {"I", "II", "III", "IV", "V", "VI", "VII", "VIII"};
                break;
        }

        String[] optimalRotors;
        int[] optimalPositions;

        List<ScoredEnigmaKey> keySet = new ArrayList<>();

        for (String rotor1 : availableRotorList) {
            for (String rotor2 : availableRotorList) {
                if (rotor1.equals(rotor2)) continue;
                for (String rotor3 : availableRotorList) {
                    if (rotor1.equals(rotor3) || rotor2.equals(rotor3)) continue;
                    System.out.println(rotor1 + " " + rotor2 + " " + rotor3 );

                    float maxFitness = -1e30f;
                    EnigmaKey bestKey = null;
                    for (int i = 0; i < 26; i++) {
                        for (int j = 0; j < 26; j++) {
                            for (int k = 0; k < 26; k++) {
                                Enigma e = new Enigma(new String[]{rotor1, rotor2, rotor3}, "B", new int[]{i, j, k}, new int[]{0, 0, 0}, plugboard);
                                char[] decryption = e.encrypt(ciphertext);
                                float fitness = f.score(decryption);
                                if (fitness > maxFitness) {
                                    maxFitness = fitness;
                                    optimalRotors = new String[] { e.leftRotor.getName(), e.middleRotor.getName(), e.rightRotor.getName()};
                                    optimalPositions = new int[] { i, j, k};
                                    bestKey = new EnigmaKey(optimalRotors, optimalPositions, null, plugboard);
                                }
                            }
                        }
                    }

                    keySet.add(new ScoredEnigmaKey(bestKey, maxFitness));
                }
            }
        }

        // Sort keys by best performing (highest fitness score)
        keySet.sort(Collections.reverseOrder());
        return keySet.stream()
                .sorted(Collections.reverseOrder())
                .limit(requiredKeys)
                .toArray(ScoredEnigmaKey[]::new);
    }

    public static ScoredEnigmaKey findRingSettings(EnigmaKey key, char[] ciphertext, FitnessFunction f) {
        EnigmaKey newKey = new EnigmaKey(key);

        int rightRotorIndex = 2, middleRotorIndex = 1;

        // Optimise right rotor
        int optimalIndex = EnigmaAnalysis.findRingSetting(newKey, ciphertext, rightRotorIndex, f);
        newKey.rings[rightRotorIndex] = optimalIndex;
        newKey.indicators[rightRotorIndex] = (newKey.indicators[rightRotorIndex] + optimalIndex) % 26;

        // Optimise middle rotor
        optimalIndex = EnigmaAnalysis.findRingSetting(newKey, ciphertext, middleRotorIndex, f);
        newKey.rings[middleRotorIndex] = optimalIndex;
        newKey.indicators[middleRotorIndex] = (newKey.indicators[middleRotorIndex] + optimalIndex) % 26;

        // Calculate fitness and return scored key
        Enigma e = new Enigma(newKey);
        char[] decryption = e.encrypt(ciphertext);
        return new ScoredEnigmaKey(newKey, f.score(decryption));
    }

    public static int findRingSetting(EnigmaKey key, char[] ciphertext, int rotor, FitnessFunction f) {
        String[] rotors = key.rotors;
        int[] originalIndicators = key.indicators;
        int[] originalRingSettings = key.rings != null ? key.rings : new int[] {0,0,0} ;
        String plugboard = key.plugboard;
        int optimalRingSetting = 0;


        float maxFitness = -1e30f;
        for (int i = 0; i < 26; i++) {
            int[] currentStartingPositions = Arrays.copyOf(originalIndicators,3);
            int[] currentRingSettings = Arrays.copyOf(originalRingSettings, 3);

            currentStartingPositions[rotor] = Math.floorMod(currentStartingPositions[rotor] + i, 26);
            currentRingSettings[rotor] = i;

            Enigma e = new Enigma(rotors,
                    "B",
                    currentStartingPositions,
                    currentRingSettings,
                    plugboard);
            char[] decryption = e.encrypt(ciphertext);
            float fitness = f.score(decryption);
            if (fitness > maxFitness) {
                maxFitness = fitness;
                optimalRingSetting = i;
            }
        }
        return optimalRingSetting;
    }

    public static String findPlug(EnigmaKey key, char[] ciphertext, FitnessFunction f) {
        Set<Integer> unpluggedCharacters = Plugboard.getUnpluggedCharacters(key.plugboard);
        Set<Integer> charCount = new HashSet<Integer>();

        EnigmaKey currentKey = new EnigmaKey(key);
        String originalPlugs = currentKey.plugboard;
        String optimalPlugSetting = "";
        float maxFitness = -1e30f;
        for (int i: unpluggedCharacters) {
            for (int j: unpluggedCharacters) {
                if (i >= j) continue;

                String plug = "" + (char)(i + 65) + (char)(j + 65);
                currentKey.plugboard = originalPlugs.isEmpty() ? plug : originalPlugs + " " + plug;

                Enigma e = new Enigma(currentKey);
                char[] decryption = e.encrypt(ciphertext);
                float fitness = f.score(decryption);
                if (fitness > maxFitness) {
                    maxFitness = fitness;
                    optimalPlugSetting = plug;
                }
            }
        }

        return optimalPlugSetting;
    }

    public static ScoredEnigmaKey findPlugs(EnigmaKey key, int maxPlugs, char[] ciphertext, FitnessFunction f) {
        EnigmaKey currentKey = new EnigmaKey(key);
        String plugs = "";
        //String findPlug(EnigmaKey key, char[] ciphertext, FitnessFunction f) {
        for (int i = 0; i < maxPlugs; i++) {
            currentKey.plugboard = plugs;
            String nextPlug = findPlug(currentKey, ciphertext, f);
            plugs = plugs.isEmpty() ? nextPlug : plugs + " " + nextPlug;
        }

        currentKey.plugboard = plugs;
        // Calculate fitness and return scored key
        Enigma e = new Enigma(currentKey);
        char[] decryption = e.encrypt(ciphertext);
        return new ScoredEnigmaKey(currentKey, f.score(decryption));
    }
}
