package com.mikepound.enigma;

import java.util.*;

public class Plugboard {

    public final String connections;

    private final int[] wiring = new int[26];
    private final Set<Integer> unpluggedCharacters = new HashSet<>(26);

    public Plugboard(String connections) {
        this.connections = connections;
        initialize(connections);
    }

    public int forward(int c) {
        return this.wiring[c];
    }

    public Set<Integer> getUnpluggedCharacters() {
        return this.unpluggedCharacters;
    }

    private void initialize(String connections) {
        for (int i = 0; i < 26; i++) {
            this.wiring[i] = i;
            this.unpluggedCharacters.add(i);
        }

        if (connections == null || connections.equals("")) {
            return;
        }

        String[] pairings = connections.toUpperCase().split("[^A-Z]");
        List<Character> pluggedCharacters = new ArrayList<>();

        // Validate and create mapping
        for (String pair : pairings) {
            if (pair.length() != 2) {
                throw new IllegalArgumentException("Incorrect plugboard configuration: exactly 2 different keys can be paired together. Connections: " + connections);
            }

            char c1 = pair.charAt(0);
            char c2 = pair.charAt(1);

            if (pluggedCharacters.contains(c1) || pluggedCharacters.contains(c2)) {
                throw new IllegalArgumentException("Incorrect plugboard configuration: one key can't be paired more than once.\n" +
                        "Characters to be paired: " + c1 + " and " + c2 + ".\n" +
                        "Characters already paired: " + pluggedCharacters);
            }

            pluggedCharacters.add(c1);
            pluggedCharacters.add(c2);

            // 0-shift char (the ASCII value of char 'A' is 65)
            int shiftedChar1 = c1 - 65;
            int shiftedChar2 = c2 - 65;

            wiring[shiftedChar1] = shiftedChar2;
            wiring[shiftedChar2] = shiftedChar1;

            unpluggedCharacters.remove(shiftedChar1);
            unpluggedCharacters.remove(shiftedChar2);
        }
    }
}
