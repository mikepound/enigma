package com.mikepound.enigma;


import com.mikepound.analysis.EnigmaKey;

public class Enigma {
    // 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25
    // A B C D E F G H I J K  L  M  N  O  P  Q  R  S  T  U  V  W  X  Y  Z
    public RotorState leftRotorState;
    public RotorState middleRotorState;
    public RotorState rightRotorState;

    private final Reflector reflector;
    private final Plugboard plugboard;

    public Enigma(RotorType[] rotorTypes, Reflector reflector, int[] initialRotorPositions, int[] ringOffsets, Plugboard plugboard) {
        this.leftRotorState = new RotorState(rotorTypes[0], ringOffsets[0], initialRotorPositions[0]);
        this.middleRotorState = new RotorState(rotorTypes[1], ringOffsets[1], initialRotorPositions[1]);
        this.rightRotorState = new RotorState(rotorTypes[2], ringOffsets[2], initialRotorPositions[2]);
        this.reflector = reflector;
        this.plugboard = plugboard;
    }

    public Enigma(EnigmaKey key) {
        this(key.rotorTypes, Reflector.B, key.rotorPositions, key.ringOffsets, key.plugboard);
    }

    public void rotate() {
        // If middle rotor notch - double-stepping
        if (middleRotorState.isAtNotch()) {
            middleRotorState.turnover();
            leftRotorState.turnover();
        }
        // If left-rotor notch
        else if (rightRotorState.isAtNotch()) {
            middleRotorState.turnover();
        }

        // Increment right-most rotor
        rightRotorState.turnover();
    }

    public int encrypt(int c) {
        rotate();

        // Plugboard in
        c = this.plugboard.forward(c);

        // Right to left
        int c1 = this.rightRotorState.forward(c);
        int c2 = this.middleRotorState.forward(c1);
        int c3 = this.leftRotorState.forward(c2);

        // Reflector
        int c4 = this.reflector.forward(c3);

        // Left to right
        int c5 = this.leftRotorState.backward(c4);
        int c6 = this.middleRotorState.backward(c5);
        int c7 = this.rightRotorState.backward(c6);

        // Plugboard out
        c7 = this.plugboard.forward(c7);

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
