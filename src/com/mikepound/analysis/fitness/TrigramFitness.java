package com.mikepound.analysis.fitness;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Stream;

public class TrigramFitness extends FitnessFunction {
    private float[] trigrams;

    private static int triIndex(int a, int b, int c) {
        return (a << 10) | (b << 5) | c;
    }

    public TrigramFitness() {
        // Trigrams
        this.trigrams = new float[26426];
        Arrays.fill(this.trigrams, (float)Math.log10(epsilon));
        try (final InputStream is = TrigramFitness.class.getResourceAsStream("/data/trigrams");
             final Reader r = new InputStreamReader(is, StandardCharsets.UTF_8);
             final BufferedReader br = new BufferedReader(r);
             final Stream<String> lines = br.lines()) {
            lines.map(line -> line.split(","))
                    .forEach(s -> {
                        String key = s[0];
                        int i = triIndex(key.charAt(0) - 65, key.charAt(1) - 65, key.charAt(2) - 65);
                        this.trigrams[i] = Float.parseFloat(s[1]);
                    });
        } catch (IOException e) {
            this.trigrams = null;
        }
    }

    @Override
    public float score(char[] text) {
        float fitness = 0;
        int current = 0;
        int next1 = text[0] - 65;
        int next2 = text[1] - 65;
        for (int i = 2; i < text.length; i++) {
            current = next1;
            next1 = next2;
            next2 = text[i] - 65;
            fitness += this.trigrams[triIndex(current, next1, next2)];
        }
        return fitness;
    }
}
