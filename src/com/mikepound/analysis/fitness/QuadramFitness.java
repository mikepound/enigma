package com.mikepound.analysis.fitness;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Stream;

public class QuadramFitness extends FitnessFunction {
    private float[] quadgrams;

    private static int quadIndex(int a, int b, int c, int d) {
        return (a << 15) | (b << 10) | (c << 5) | d;
    }

    public QuadramFitness() {
        // Quadgrams
        this.quadgrams = new float[845626];
        Arrays.fill(this.quadgrams, (float)Math.log10(epsilon));
        try (final InputStream is = QuadramFitness.class.getResourceAsStream("/data/quadgrams");
             final Reader r = new InputStreamReader(is, StandardCharsets.UTF_8);
             final BufferedReader br = new BufferedReader(r);
             final Stream<String> lines = br.lines()) {
            lines.map(line -> line.split(","))
                    .forEach(s -> {
                        String key = s[0];
                        int i = quadIndex(key.charAt(0) - 65,key.charAt(1) - 65,key.charAt(2) - 65,key.charAt(3) - 65);
                        this.quadgrams[i] = Float.parseFloat(s[1]);
                    });
        } catch (IOException e) {
            this.quadgrams = null;
        }
    }

    @Override
    public float score(char[] text) {
        float fitness = 0;
        int current = 0;
        int next1 = text[0] - 65;
        int next2 = text[1] - 65;
        int next3 = text[2] - 65;
        for (int i = 3; i < text.length; i++) {
            current = next1;
            next1 = next2;
            next2 = next3;
            next3 = text[i] - 65;
            fitness += this.quadgrams[quadIndex(current, next1, next2, next3)];
        }
        return fitness;
    }
}
