package com.mikepound.analysis.fitness;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Stream;

public class BigramFitness extends FitnessFunction {
    private float[] bigrams;

    private static int biIndex(int a, int b) {
        return (a << 5) | b;
    }


    public BigramFitness() {
        // Bigrams
        this.bigrams = new float[826];
        Arrays.fill(this.bigrams, (float)Math.log10(epsilon));
        try (final InputStream is = BigramFitness.class.getResourceAsStream("/data/bigrams");
             final Reader r = new InputStreamReader(is, StandardCharsets.UTF_8);
             final BufferedReader br = new BufferedReader(r);
             final Stream<String> lines = br.lines()) {
            lines.map(line -> line.split(","))
                    .forEach(s -> {
                        String key = s[0];
                        int i = biIndex(key.charAt(0) - 65, key.charAt(1) - 65);
                        this.bigrams[i] = Float.parseFloat(s[1]);
                    });
        } catch (IOException e) {
            this.bigrams = null;
        }
    }

    @Override
    public float score(char[] text) {
        float fitness = 0;
        int current = 0;
        int next = text[0] - 65;
        for (int i = 1; i < text.length; i++) {
            current = next;
            next = text[i] - 65;
            fitness += this.bigrams[biIndex(current, next)];
        }
        return fitness;
    }
}
