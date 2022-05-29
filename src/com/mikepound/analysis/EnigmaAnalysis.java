package com.mikepound.analysis;

import com.mikepound.analysis.fitness.FitnessFunction;
import com.mikepound.enigma.Enigma;
import com.mikepound.enigma.Plugboard;
import com.mikepound.enigma.Reflector;
import com.mikepound.enigma.RotorType;

import java.util.*;

public class EnigmaAnalysis {
    public enum AvailableRotors {
        THREE(new RotorType[]{RotorType.I, RotorType.II, RotorType.III}),
        FIVE(new RotorType[]{RotorType.I, RotorType.II, RotorType.III, RotorType.IV, RotorType.V}),
        EIGHT(new RotorType[]{RotorType.I, RotorType.II, RotorType.III, RotorType.IV, RotorType.V, RotorType.VI, RotorType.VII, RotorType.VIII});

        public final RotorType[] rotorTypes;

        AvailableRotors(RotorType[] rotorTypes) {
            this.rotorTypes = rotorTypes;
        }
    }

    public static ScoredEnigmaKey[] findRotorConfiguration(char[] ciphertext, AvailableRotors rotors, Plugboard plugboard, int requiredKeys, FitnessFunction f) {

        RotorType[] optimalRotors;
        int[] optimalPositions;

        List<ScoredEnigmaKey> keySet = new ArrayList<>();

        for (RotorType rotor1 : rotors.rotorTypes) {
            for (RotorType rotor2 : rotors.rotorTypes) {
                if (rotor1.equals(rotor2)) continue;
                for (RotorType rotor3 : rotors.rotorTypes) {
                    if (rotor1.equals(rotor3) || rotor2.equals(rotor3)) continue;
                    System.out.println(rotor1 + " " + rotor2 + " " + rotor3);

                    float maxFitness = -1e30f;
                    EnigmaKey bestKey = null;
                    for (int i = 0; i < 26; i++) {
                        for (int j = 0; j < 26; j++) {
                            for (int k = 0; k < 26; k++) {
                                Enigma e = new Enigma(new RotorType[]{rotor1, rotor2, rotor3}, Reflector.B, new int[]{i, j, k}, new int[]{0, 0, 0}, plugboard);
                                char[] decryption = e.encrypt(ciphertext);
                                float fitness = f.score(decryption);
                                if (fitness > maxFitness) {
                                    maxFitness = fitness;
                                    optimalRotors = new RotorType[]{e.leftRotorState.rotorType, e.middleRotorState.rotorType, e.rightRotorState.rotorType};
                                    optimalPositions = new int[]{i, j, k};
                                    bestKey = new EnigmaKey(optimalRotors, optimalPositions, null, plugboard);
                                }
                            }
                        }
                    }

                    keySet.add(new ScoredEnigmaKey(bestKey, maxFitness));
                }
            }
        }

        return keySet.stream()
                .sorted(Collections.reverseOrder()) // Sort keys by best performing (highest fitness score)
                .limit(requiredKeys)
                .toArray(ScoredEnigmaKey[]::new);
    }

    public static ScoredEnigmaKey findRingSettings(EnigmaKey key, char[] ciphertext, FitnessFunction f) {
        EnigmaKey newKey = new EnigmaKey(key);

        int rightRotorIndex = 2, middleRotorIndex = 1;

        // Optimise right rotor
        int optimalIndex = EnigmaAnalysis.findRingSetting(newKey, ciphertext, rightRotorIndex, f);
        newKey.ringOffsets[rightRotorIndex] = optimalIndex;
        newKey.rotorPositions[rightRotorIndex] = (newKey.rotorPositions[rightRotorIndex] + optimalIndex) % 26;

        // Optimise middle rotor
        optimalIndex = EnigmaAnalysis.findRingSetting(newKey, ciphertext, middleRotorIndex, f);
        newKey.ringOffsets[middleRotorIndex] = optimalIndex;
        newKey.rotorPositions[middleRotorIndex] = (newKey.rotorPositions[middleRotorIndex] + optimalIndex) % 26;

        // Calculate fitness and return scored key
        Enigma e = new Enigma(newKey);
        char[] decryption = e.encrypt(ciphertext);
        return new ScoredEnigmaKey(newKey, f.score(decryption));
    }

    public static int findRingSetting(EnigmaKey key, char[] ciphertext, int rotor, FitnessFunction f) {
        RotorType[] rotors = key.rotorTypes;
        int[] originalIndicators = key.rotorPositions;
        int[] originalRingOffsets = key.ringOffsets != null ? key.ringOffsets : new int[]{0, 0, 0};
        Plugboard plugboard = key.plugboard;
        int optimalRingSetting = 0;

        float maxFitness = -1e30f;
        for (int i = 0; i < 26; i++) {
            int[] currentStartingPositions = Arrays.copyOf(originalIndicators, 3);
            int[] currentRingOffsets = Arrays.copyOf(originalRingOffsets, 3);

            currentStartingPositions[rotor] = Math.floorMod(currentStartingPositions[rotor] + i, 26);
            currentRingOffsets[rotor] = i;

            Enigma e = new Enigma(rotors,
                    Reflector.B,
                    currentStartingPositions,
                    currentRingOffsets,
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
        Set<Integer> unpluggedCharacters = key.plugboard.getUnpluggedCharacters();

        EnigmaKey currentKey = new EnigmaKey(key);
        Plugboard originalPlugs = currentKey.plugboard;
        String optimalPlugSetting = "";
        float maxFitness = -1e30f;
        for (int i : unpluggedCharacters) {
            for (int j : unpluggedCharacters) {
                if (i >= j) continue;

                String plug = "" + (char) (i + 65) + (char) (j + 65);
                currentKey.plugboard = originalPlugs.connections.isEmpty() ? new Plugboard(plug) : new Plugboard(originalPlugs.connections + " " + plug);

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
            currentKey.plugboard = new Plugboard(plugs);
            String nextPlug = findPlug(currentKey, ciphertext, f);
            plugs = plugs.isEmpty() ? nextPlug : plugs + " " + nextPlug;
        }

        currentKey.plugboard = new Plugboard(plugs);
        // Calculate fitness and return scored key
        Enigma e = new Enigma(currentKey);
        char[] decryption = e.encrypt(ciphertext);
        return new ScoredEnigmaKey(currentKey, f.score(decryption));
    }
}
