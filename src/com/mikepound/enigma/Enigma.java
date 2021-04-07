package com.mikepound.enigma;


import com.mikepound.analysis.EnigmaKey;

public class Enigma {
    // 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25
    // A B C D E F G H I J K  L  M  N  O  P  Q  R  S  T  U  V  W  X  Y  Z
    public Rotor leftRotor;
    public Rotor middleRotor;
    public Rotor rightRotor;

    public Reflector reflector;

    public Plugboard plugboard;

    public Enigma(String[] rotors, String reflector, int[] rotorPositions, int[] ringSettings, String plugboardConnections) {
        this.leftRotor = Rotor.Create(rotors[0], rotorPositions[0], ringSettings[0]);
        this.middleRotor = Rotor.Create(rotors[1], rotorPositions[1], ringSettings[1]);
        this.rightRotor = Rotor.Create(rotors[2], rotorPositions[2], ringSettings[2]);
        this.reflector = Reflector.Create(reflector);
        this.plugboard = new Plugboard(plugboardConnections);
    }

    public Enigma(EnigmaKey key) {
        this(key.rotors, "B", key.indicators, key.rings, key.plugboard);
    }

    public void rotate() {
        // If middle rotor notch - double-stepping
        if (middleRotor.isAtNotch()) {
            middleRotor.turnover();
            leftRotor.turnover();
        }
        // If left-rotor notch
        else if (rightRotor.isAtNotch()) {
            middleRotor.turnover();
        }

        // Increment right-most rotor
        rightRotor.turnover();
    }

    public int encrypt(int c) {
        rotate();

        // Plugboard in
        c = this.plugboard.forward(c);

        // Right to left
        int c1 = rightRotor.forward(c);
        int c2 = middleRotor.forward(c1);
        int c3 = leftRotor.forward(c2);

        // Reflector
        int c4 = reflector.forward(c3);

        // Left to right
        int c5 = leftRotor.backward(c4);
        int c6 = middleRotor.backward(c5);
        int c7 = rightRotor.backward(c6);

        // Plugboard out
        c7 = plugboard.forward(c7);

        return c7;
    }

    public char encrypt(char c) {
        return (char)(this.encrypt(c - 65) + 65);
    }

    public char[] encrypt(char[] input) {
        char[] output = new char[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = this.encrypt(input[i]);
        }
        return output;
    }
}
