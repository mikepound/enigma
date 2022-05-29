package com.mikepound.enigma;

public enum RotorType {

    I("I", "EKMFLGDQVZNTOWYHXUSPAIBRCJ", new int[]{16}),
    II("II", "AJDKSIRUXBLHWTMCQGZNPYFVOE", new int[]{4}),
    III("III", "BDFHJLCPRTXVZNYEIWGAKMUSQO", new int[]{21}),
    IV("IV", "ESOVPZJAYQUIRHXLNFTGKDCMWB", new int[]{9}),
    V("V", "VZBRGITYUPSDNHLXAWMJQOFECK", new int[]{25}),
    VI("VI", "JPGVOUMFYQBENHZRDKASXLICTW", new int[]{12, 25}),
    VII("VII", "NZJHGRCXMYSWBOUFAIVLPEKQDT", new int[]{12, 25}),
    VIII("VIII", "FKQHTLXOCBJSPDZRAMEWNIUYGV", new int[]{12, 25}),
    IDENTITY("Identity", "ABCDEFGHIJKLMNOPQRSTUVWXYZ", new int[]{0});

    public final String name;
    private final int[] notchPositions;
    private final int[] forwardWiring;
    private final int[] backwardWiring;

    RotorType(String name, String encoding, int[] notchPositions) {
        this.name = name;
        this.notchPositions = notchPositions;
        this.forwardWiring = decodeWiring(encoding);
        this.backwardWiring = inverseWiring(this.forwardWiring);
    }

    public int encipher(int c, int ringOffset, int position, boolean forward) {
        int[] mapping;

        if (forward) {
            mapping = this.forwardWiring;
        } else {
            mapping = this.backwardWiring;
        }

        int shift = position - ringOffset;
        return (mapping[(c + shift + 26) % 26] - shift + 26) % 26;
    }

    public boolean isAtNotch(int rotorPosition) {
        for (int notchPosition : this.notchPositions) {
            if (notchPosition == rotorPosition) return true;
        }

        return false;
    }

    private int[] decodeWiring(String encoding) {
        char[] charWiring = encoding.toCharArray();
        int[] wiring = new int[charWiring.length];
        for (int i = 0; i < charWiring.length; i++) {
            wiring[i] = charWiring[i] - 65;
        }
        return wiring;
    }

    private int[] inverseWiring(int[] wiring) {
        int[] inverse = new int[wiring.length];
        for (int i = 0; i < wiring.length; i++) {
            int forward = wiring[i];
            inverse[forward] = i;
        }
        return inverse;
    }
}
