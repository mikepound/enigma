package com.mikepound.analysis.fitness;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public class SingleCharacterFitness extends FitnessFunction {
    private float[] singles;

    public SingleCharacterFitness() {
        // Single characters
        this.singles = new float[26];
        try (final InputStream is = SingleCharacterFitness.class.getResourceAsStream("/data/single");
             final Reader r = new InputStreamReader(is, StandardCharsets.UTF_8);
             final BufferedReader br = new BufferedReader(r);
             final Stream<String> lines = br.lines()) {
            lines.map(line -> line.split(","))
                    .forEach(s -> {
                        int i = s[0].charAt(0) - 65;
                        this.singles[i] = Float.parseFloat(s[1]);
                    });
        } catch (IOException e) {
            this.singles = null;
        }
    }

    @Override
    public float score(char[] text) {
        float fitness = 0;
        for (char c: text) {
            fitness += this.singles[c - 65];
        }
        return fitness;
    }

}
