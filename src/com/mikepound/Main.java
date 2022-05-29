package com.mikepound;

import com.mikepound.analysis.EnigmaAnalysis;
import com.mikepound.analysis.ScoredEnigmaKey;
import com.mikepound.analysis.fitness.*;
import com.mikepound.enigma.Enigma;
import com.mikepound.enigma.Plugboard;

public class Main {

    public static void main(String[] args) {

        FitnessFunction ioc = new IoCFitness();
        FitnessFunction bigrams = new BigramFitness();
        FitnessFunction quadgrams = new QuadramFitness();

        final long startTime = System.currentTimeMillis();

        // For those interested, these were the original settings
        // II V III / 7 4 19 / 12 2 20 / AF TV KO BL RW
        char[] ciphertext = "OZLUDYAKMGMXVFVARPMJIKVWPMBVWMOIDHYPLAYUWGBZFAFAFUQFZQISLEZMYPVBRDDLAGIHIFUJDFADORQOOMIZPYXDCBPWDSSNUSYZTJEWZPWFBWBMIEQXRFASZLOPPZRJKJSPPSTXKPUWYSKNMZZLHJDXJMMMDFODIHUBVCXMNICNYQBNQODFQLOGPZYXRJMTLMRKQAUQJPADHDZPFIKTQBFXAYMVSZPKXIQLOQCVRPKOBZSXIUBAAJBRSNAFDMLLBVSYXISFXQZKQJRIQHOSHVYJXIFUZRMXWJVWHCCYHCXYGRKMKBPWRDBXXRGABQBZRJDVHFPJZUSEBHWAEOGEUQFZEEBDCWNDHIAQDMHKPRVYHQGRDYQIOEOLUBGBSNXWPZCHLDZQBWBEWOCQDBAFGUVHNGCIKXEIZGIZHPJFCTMNNNAUXEVWTWACHOLOLSLTMDRZJZEVKKSSGUUTHVXXODSKTFGRUEIIXVWQYUIPIDBFPGLBYXZTCOQBCAHJYNSGDYLREYBRAKXGKQKWJEKWGAPTHGOMXJDSQKYHMFGOLXBSKVLGNZOAXGVTGXUIVFTGKPJU".toCharArray();

        // Begin by finding the best combination of rotors and start positions (returns top n)
        ScoredEnigmaKey[] rotorConfigurations = EnigmaAnalysis.findRotorConfiguration(ciphertext,
                EnigmaAnalysis.AvailableRotors.FIVE,
                new Plugboard(""),
                10,
                ioc);

        System.out.println("\nTop 10 rotor configurations:");
        for (ScoredEnigmaKey key : rotorConfigurations) {
            System.out.println(String.format("%s %s %s / %d %d %d / %f",
                    key.rotorTypes[0].name, key.rotorTypes[1].name, key.rotorTypes[2].name,
                    key.rotorPositions[0], key.rotorPositions[1], key.rotorPositions[2],
                    key.getScore()));
        }
        System.out.println(String.format("Current decryption: %s\n",
                new String(new Enigma(rotorConfigurations[0]).encrypt(ciphertext))));

        // Next find the best ring settings for the best configuration (index 0)
        ScoredEnigmaKey rotorAndRingConfiguration = EnigmaAnalysis.findRingSettings(rotorConfigurations[0], ciphertext, bigrams);

        System.out.println(String.format("Best ring settings: %d %d %d",
                rotorAndRingConfiguration.ringOffsets[0], rotorAndRingConfiguration.ringOffsets[1], rotorAndRingConfiguration.ringOffsets[2]));
        System.out.println(String.format("Current decryption: %s\n",
                new String(new Enigma(rotorAndRingConfiguration).encrypt(ciphertext))));

        // Finally, perform hill climbing to find plugs one at a time
        ScoredEnigmaKey optimalKeyWithPlugs = EnigmaAnalysis.findPlugs(rotorAndRingConfiguration, 5, ciphertext, quadgrams);
        System.out.println(String.format("Best plugboard: %s", optimalKeyWithPlugs.plugboard.connections));
        System.out.println(String.format("Final decryption: %s\n",
                new String(new Enigma(optimalKeyWithPlugs).encrypt(ciphertext))));

        final long totalTime = System.currentTimeMillis() - startTime;
        System.out.print("Total execution time: ");
        if (totalTime >= 2_000) {
            System.out.println((totalTime / 1_000) + " seconds and " + totalTime % 1_000 + " ms");
        } else {
            System.out.println(totalTime + " ms");
        }
    }
}
