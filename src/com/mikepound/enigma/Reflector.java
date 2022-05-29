package com.mikepound.enigma;

public enum Reflector {

    B("YRUHQSLDPXNGOKMIEBFZCWVJAT"),
    C("FVPJIAOYEDRZXWGCTKUQSBNMHL"),
    DEFAULT("ZYXWVUTSRQPONMLKJIHGFEDCBA");

    private final int[] forwardWiring;

    Reflector(String encoding) {
        this.forwardWiring = decodeWiring(encoding);
    }

    public int forward(int c) {
        return this.forwardWiring[c];
    }

    private int[] decodeWiring(String encoding) {
        char[] charWiring = encoding.toCharArray();
        int[] wiring = new int[charWiring.length];
        for (int i = 0; i < charWiring.length; i++) {
            wiring[i] = charWiring[i] - 65;
        }
        return wiring;
    }

}
