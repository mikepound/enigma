package com.mikepound.analysis.fitness;

public class KnownPlaintextFitness extends FitnessFunction {
    char[] plaintext;

    public KnownPlaintextFitness(char[] plaintext) {
        this.plaintext = plaintext;
    }

    public KnownPlaintextFitness(String[] words, int[] offsets) {
        int length = 0;
        for (int i = 0; i < words.length; i++) {
            int offset = offsets[i] + words[i].length();
            length = Math.max(offset, length);
        }

        this.plaintext = new char[length];

        for (int i = 0; i < words.length; i++) {
            System.arraycopy(words[i].toCharArray(), 0, this.plaintext, offsets[i], words[i].length());
        }

    }

    @Override
    public float score(char[] text) {
        int length = Math.min(this.plaintext.length, text.length);
        int total = 0;
        for (int i = 0; i < length; i++) {
            if (this.plaintext[i] > 0) {
                total += this.plaintext[i] == text[i] ? 1 : 0;
            }
        }
        return total;
    }
}
