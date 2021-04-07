package com.mikepound.analysis;

import java.util.Arrays;

public class EnigmaKey {
    public String[] rotors;
    public int[] indicators;
    public int[] rings;
    public String plugboard;

    public EnigmaKey(String[] rotors, int[] indicators, int[] rings, String plugboardConnections) {
        this.rotors = rotors == null ? new String[] {"I", "II", "III"} : rotors;
        this.indicators = indicators == null ? new int[] {0,0,0} : indicators;
        this.rings = rings == null ? new int[] {0,0,0} : rings;
        this.plugboard = plugboardConnections == null ? "" : plugboardConnections;
    }

    public EnigmaKey(EnigmaKey key) {
        this.rotors = key.rotors == null ? new String[] {"I", "II", "III"} : new String[] {key.rotors[0], key.rotors[1], key.rotors[2]};
        this.indicators = key.indicators == null ? new int[] {0,0,0} : Arrays.copyOf(key.indicators, 3);
        this.rings = key.rings == null ? new int[] {0,0,0} : Arrays.copyOf(key.rings,3);
        this.plugboard = key.plugboard == null ? "" : key.plugboard;
    }
}
