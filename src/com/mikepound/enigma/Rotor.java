package com.mikepound.enigma;

public class Rotor {
    protected String name;
    protected int[] forwardWiring;
    protected int[] backwardWiring;

    protected int rotorPosition;
    protected int notchPosition;
    protected int ringSetting;

    public Rotor(String name, String encoding, int rotorPosition, int notchPosition, int ringSetting) {
        this.name = name;
        this.forwardWiring = decodeWiring(encoding);
        this.backwardWiring = inverseWiring(this.forwardWiring);
        this.rotorPosition = rotorPosition;
        this.notchPosition = notchPosition;
        this.ringSetting = ringSetting;
    }

    public static Rotor Create(String name, int rotorPosition, int ringSetting) {
        switch (name) {
            case "I":
                return new Rotor("I","EKMFLGDQVZNTOWYHXUSPAIBRCJ", rotorPosition, 16, ringSetting);
            case "II":
                return new Rotor("II","AJDKSIRUXBLHWTMCQGZNPYFVOE", rotorPosition, 4, ringSetting);
            case "III":
                return new Rotor("III","BDFHJLCPRTXVZNYEIWGAKMUSQO", rotorPosition, 21, ringSetting);
            case "IV":
                return new Rotor("IV","ESOVPZJAYQUIRHXLNFTGKDCMWB", rotorPosition, 9, ringSetting);
            case "V":
                return new Rotor("V","VZBRGITYUPSDNHLXAWMJQOFECK", rotorPosition, 25, ringSetting);
            case "VI":
                return new Rotor("VI","JPGVOUMFYQBENHZRDKASXLICTW", rotorPosition, 0, ringSetting) {
                    @Override
                    public boolean isAtNotch() {
                        return this.rotorPosition == 12 || this.rotorPosition == 25;
                    }
                };
            case "VII":
                return new Rotor("VII","NZJHGRCXMYSWBOUFAIVLPEKQDT", rotorPosition, 0, ringSetting) {
                    @Override
                    public boolean isAtNotch() {
                        return this.rotorPosition == 12 || this.rotorPosition == 25;
                    }
                };
            case "VIII":
                return new Rotor("VIII","FKQHTLXOCBJSPDZRAMEWNIUYGV", rotorPosition, 0, ringSetting) {
                    @Override
                    public boolean isAtNotch() {
                        return this.rotorPosition == 12 || this.rotorPosition == 25;
                    }
                };
            default:
                return new Rotor("Identity","ABCDEFGHIJKLMNOPQRSTUVWXYZ", rotorPosition, 0, ringSetting);
        }
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return rotorPosition;
    }

    protected static int[] decodeWiring(String encoding) {
        char[] charWiring = encoding.toCharArray();
        int[] wiring = new int[charWiring.length];
        for (int i = 0; i < charWiring.length; i++) {
            wiring[i] = charWiring[i] - 65;
        }
        return wiring;
    }

    protected static int[] inverseWiring(int[] wiring) {
        int[] inverse = new int[wiring.length];
        for (int i = 0; i < wiring.length; i++) {
            int forward = wiring[i];
            inverse[forward] = i;
        }
        return inverse;
    }

    protected static int encipher(int k, int pos, int ring, int[] mapping) {
        int shift = pos - ring;
        return (mapping[(k + shift + 26) % 26] - shift + 26) % 26;
    }

    public int forward(int c) {
        return encipher(c, this.rotorPosition, this.ringSetting, this.forwardWiring);
    }

    public int backward(int c) {
        return encipher(c, this.rotorPosition, this.ringSetting, this.backwardWiring);
    }

    public boolean isAtNotch() {
        return this.notchPosition == this.rotorPosition;
    }

    public void turnover() {
        this.rotorPosition = (this.rotorPosition + 1) % 26;
    }


}
