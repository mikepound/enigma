package com.mikepound;

import com.mikepound.analysis.EnigmaAnalysis;
import com.mikepound.analysis.EnigmaKey;
import com.mikepound.analysis.ScoredEnigmaKey;
import com.mikepound.analysis.fitness.*;
import com.mikepound.enigma.Enigma;

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
                "",
                10,
                ioc);

        System.out.println("\nTop 10 rotor configurations:");
        for (ScoredEnigmaKey key : rotorConfigurations) {
            System.out.println(String.format("%s %s %s / %d %d %d / %f",
                    key.rotors[0], key.rotors[1], key.rotors[2],
                    key.indicators[0], key.indicators[1], key.indicators[2],
                    key.getScore()));
        }
        System.out.println(String.format("Current decryption: %s\n",
                new String(new Enigma(rotorConfigurations[0]).encrypt(ciphertext))));

        // Next find the best ring settings for the best configuration (index 0)
        ScoredEnigmaKey rotorAndRingConfiguration = EnigmaAnalysis.findRingSettings(rotorConfigurations[0], ciphertext, bigrams);

        System.out.println(String.format("Best ring settings: %d %d %d",
                rotorAndRingConfiguration.rings[0], rotorAndRingConfiguration.rings[1], rotorAndRingConfiguration.rings[2]));
        System.out.println(String.format("Current decryption: %s\n",
                new String(new Enigma(rotorAndRingConfiguration).encrypt(ciphertext))));

        // Finally, perform hill climbing to find plugs one at a time
        ScoredEnigmaKey optimalKeyWithPlugs = EnigmaAnalysis.findPlugs(rotorAndRingConfiguration, 5, ciphertext, quadgrams);
        System.out.println(String.format("Best plugboard: %s", optimalKeyWithPlugs.plugboard));
        System.out.println(String.format("Final decryption: %s\n",
                new String(new Enigma(optimalKeyWithPlugs).encrypt(ciphertext))));

        final long endTime = System.currentTimeMillis();

        System.out.println("Total execution time: " + (endTime - startTime));
    }
}
