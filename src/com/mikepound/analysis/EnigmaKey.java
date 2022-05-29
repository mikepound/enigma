package com.mikepound.analysis;

import com.mikepound.enigma.Plugboard;
import com.mikepound.enigma.RotorType;

import java.util.Arrays;

public class EnigmaKey {
    public final RotorType[] rotorTypes;
    public int[] rotorPositions;
    public int[] ringOffsets;
    public Plugboard plugboard;

    public EnigmaKey(RotorType[] rotorTypes, int[] rotorPositions, int[] ringOffsets, Plugboard plugboard) {
        this.rotorTypes = rotorTypes == null ? EnigmaAnalysis.AvailableRotors.THREE.rotorTypes : rotorTypes;
        this.rotorPositions = rotorPositions == null ? new int[]{0, 0, 0} : rotorPositions;
        this.ringOffsets = ringOffsets == null ? new int[]{0, 0, 0} : ringOffsets;
        this.plugboard = plugboard == null ? new Plugboard("") : plugboard;
    }

    public EnigmaKey(EnigmaKey key) {
        this.rotorTypes = key.rotorTypes == null ? EnigmaAnalysis.AvailableRotors.THREE.rotorTypes : new RotorType[]{key.rotorTypes[0], key.rotorTypes[1], key.rotorTypes[2]};
        this.rotorPositions = key.rotorPositions == null ? new int[]{0, 0, 0} : Arrays.copyOf(key.rotorPositions, 3);
        this.ringOffsets = key.ringOffsets == null ? new int[]{0, 0, 0} : Arrays.copyOf(key.ringOffsets, 3);
        this.plugboard = key.plugboard == null ? new Plugboard("") : key.plugboard;
    }
}
